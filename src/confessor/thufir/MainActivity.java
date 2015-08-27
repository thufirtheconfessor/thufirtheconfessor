/*
Copyright 2015

This file is part of Thufir the Confessor .

 Thufir the Confessor is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Thufir the Confessor is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
*/
package confessor.thufir;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;
import confessor.thufir.diags.DiagnosticsHolder;
import confessor.thufir.lib.Closeables;
import confessor.thufir.lib.Formatter;
import confessor.thufir.lib.stream.AsyncImageStreamHolder;
import confessor.thufir.lib.stream.ErrorHandler;
import confessor.thufir.settings.SettingsManager;
import confessor.thufir.settings.SettingsThreadPool;
import confessor.thufir.widgets.screen.EmptyScreenCloseable;
import confessor.thufir.widgets.screen.MainScreenFactory;
import confessor.thufir.widgets.screen.Screen;
import confessor.thufir.widgets.screen.ScreenFactory;
import confessor.thufir.widgets.screen.Screens;
import java.util.List;

public class MainActivity extends Activity {
	private CameraParameters cachedCameraParameters;
	private Env env;
	private List<List<String>> savedScreen;
	
	private void close() {
		try {
			if (null!=env) {
				Closeables closeables=env.closeables();
				env=null;
				if (null!=closeables) {
					closeables.close();
				}
			}
		}
		catch (Error er) {
			throw er;
		}
		catch (RuntimeException ex) {
			throw ex;
		}
		catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}
	
	private void error(Throwable throwable) {
		if (null!=env) {
			ErrorHandler errorHandler=env.fallbackErrorHandler();
			if (null!=errorHandler) {
				errorHandler.error(throwable);
				return;
			}
			errorHandler=env.androidErrorHandler();
			if (null!=errorHandler) {
				errorHandler.error(throwable);
				return;
			}
		}
		AndroidErrorHandler.d(throwable);
	}
	
	private void loadMessageBox(Bundle bundle) throws Throwable {
		savedScreen=Screens.restore(bundle);
		restoreSavedState();
	}
	
	@Override
	public void onBackPressed() {
		try {
			if ((null==env)
					|| (null==env.screen())
					|| (!env.screen().pop())){
				super.onBackPressed();
			}
		}
		catch (Throwable throwable) {
			error(throwable);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if ((null!=env)
				&& (null!=env.cameraHolder())) {
			env.cameraHolder().setOrientation();
		}
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if ((!getPackageManager()
					.hasSystemFeature(PackageManager.FEATURE_CAMERA))
				|| (0>=Camera.getNumberOfCameras())) {
			Toast.makeText(this, "There's no camera.", Toast.LENGTH_SHORT)
					.show();
			finish();
			return;
		}
		
		setContentView(R.layout.main);
		try {
			loadMessageBox(savedInstanceState);
		}
		catch (Throwable throwable) {
			error(throwable);
		}
		
		LicenseActivity.showIfNotSeen(this);
    }

	@Override
	protected void onDestroy() {
		try {
			close();
		}
		finally	{
			super.onDestroy();
		}
	}

	@Override
	protected void onPause() {
		try {
			close();
		}
		finally	{
			super.onPause();
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		try {
			loadMessageBox(savedInstanceState);
		}
		catch (Throwable throwable) {
			error(throwable);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		start();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (null!=env) {
			try {
				savedScreen=Screens.save(env);
				if (null!=savedScreen) {
					Screens.save(outState, savedScreen);
				}
			}
			catch (Throwable throwable) {
				env.fallbackErrorHandler().error(throwable);
			}
		}
	}
	
	private void restoreSavedState() throws Throwable {
		if (null!=savedScreen) {
			ScreenFactory factory=Screens.restore(env, savedScreen);
			if (null!=factory) {
				env.screen().setFactory(factory);
				savedScreen=null;
			}
		}
	}
	
	private void start() {
		close();
		
		Env.Mutable env2=new Env.Mutable();
		env=env2;
		env2.context(this);
		if (null==cachedCameraParameters) {
			cachedCameraParameters=new CameraParameters();
		}
		env2.cameraParameters(cachedCameraParameters);
		env2.closeables(new Closeables());
		env2.handler(new Handler());
		env2.formatter(new Formatter());
		env2.androidErrorHandler(new AndroidErrorHandler(this, env.handler()));
		env2.fallbackErrorHandler(
				new FallbackErrorHandler(env.androidErrorHandler()));
		
		SurfaceView preview=(SurfaceView)findViewById(R.id.preview);
		SurfaceHolder previewHolder=preview.getHolder();
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		env2.wakeLockStreamHolderListener(
				new WakeLockStreamHolderListener(this));
		env.closeables().add(env.wakeLockStreamHolderListener());
		env2.asyncImageStreamHolder(
				new AsyncImageStreamHolder(env.fallbackErrorHandler(),
						env.wakeLockStreamHolderListener()));
		env.closeables().add(env.asyncImageStreamHolder());
		
		env2.screen((Screen)findViewById(R.id.screen));
		env.screen().init(env);
		
		env2.settingsManager(new SettingsManager(this));
		env2.threadPool(new SettingsThreadPool(
				env.fallbackErrorHandler(), env.settingsManager()));
		env.closeables().add(env.threadPool());
		
		env2.cameraHolder(new CameraHolder(this, env.asyncImageStreamHolder(),
				env.cameraParameters(), this, env.fallbackErrorHandler(),
				env.handler(), previewHolder, env.screen().background(),
				env.settingsManager()));
		env.closeables().add(env.cameraHolder());
		env.settingsManager().cameraHolder(env.cameraHolder());
		
		env2.diagnosticsHolder(new DiagnosticsHolder(env));
		env.closeables().add(env.diagnosticsHolder());
		
		env.closeables().add(new EmptyScreenCloseable(this, env.screen()));
		
		env.fallbackErrorHandler().screen(env.screen());
		try {
			env.screen().setFactory(new MainScreenFactory(env, null));
			
			env.cameraHolder().reloadCamera(env.settingsManager());
			
			restoreSavedState();
		}
		catch (Throwable throwable) {
			error(throwable);
		}
	}
}
