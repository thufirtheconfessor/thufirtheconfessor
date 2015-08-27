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
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Handler;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.widget.Toast;
import confessor.thufir.lib.AbstractCloseable;
import confessor.thufir.lib.Matrix3fs;
import confessor.thufir.lib.collection.ObjectPool;
import confessor.thufir.lib.collection.Pool;
import confessor.thufir.lib.stream.AsyncImageStreamHolder;
import confessor.thufir.lib.stream.ErrorHandler;
import confessor.thufir.settings.Settings;
import confessor.thufir.settings.SettingsManager;
import confessor.thufir.settings.camera.CameraParameterSetting;
import confessor.thufir.settings.camera.CameraSetting;
import confessor.thufir.settings.camera.IdName;
import confessor.thufir.widgets.screen.ScreenBackground;
import java.io.IOException;
import java.util.List;

public class CameraHolder extends AbstractCloseable
		implements PreviewInfo, SurfaceHolder.Callback {
	public static final int PREVIEW_BUFFERS=5;
	
	private class ErrorCallback implements Camera.ErrorCallback {
		@Override
		public void onError(int error, Camera camera) {
			String message;
			switch (error) {
				case Camera.CAMERA_ERROR_SERVER_DIED:
					message="camera error: server died";
					break;
				case Camera.CAMERA_ERROR_UNKNOWN:
					message="camera error: unknown";
					break;
				default:
					message="camera error";
					break;
			}
			error(new RuntimeException(message));
		}
	}
	
	private class PictureCallback implements Camera.PictureCallback, Runnable {
		public byte[] bytes;
		public Camera camera;

		@Override
		public void onPictureTaken(byte[] bytes, Camera camera) {
			if ((this.camera!=camera)
					|| (CameraHolder.this.camera!=camera)) {
				pictureCallbackPool.release(this);
				return;
			}
			this.bytes=bytes;
			handler.post(this);
		}

		@Override
		public void run() {
			if (CameraHolder.this.camera==camera) {
				takingPicture=false;
				try {
					imagePusher.onPictureTaken(bytes, camera);
				}
				catch (Throwable throwable) {
					error(throwable);
				}
				finally {
					startPreview();
				}
			}
			pictureCallbackPool.release(this);
		}
		
		public void scrub() {
			bytes=null;
			camera=null;
		}
	}
	
	private class PictureCallbackPool extends ObjectPool<PictureCallback> {
		public PictureCallbackPool() {
			super(PREVIEW_BUFFERS, null);
		}

		@Override
		protected PictureCallback create() {
			return new PictureCallback();
		}

		@Override
		protected void init(PictureCallback object) {
		}

		@Override
		protected void scrub(PictureCallback object) {
			object.scrub();
		}
	}
	
	private class PreviewBufferPool implements Pool<byte[]> {
		private class RunnableImpl implements Runnable {
			private byte[] object;

			@Override
			public void run() {
				if (camera==poolCamera) {
					camera.addCallbackBuffer(object);
					runnablePool.release(this);
				}
			}
			
			public void scrub() {
				object=null;
			}
		}
		
		private class RunnablePool extends ObjectPool<RunnableImpl> {
			public RunnablePool() {
				super(PREVIEW_BUFFERS, null);
			}

			@Override
			protected RunnableImpl create() {
				return new RunnableImpl();
			}

			@Override
			protected void init(RunnableImpl object) {
			}

			@Override
			protected void scrub(RunnableImpl object) {
				object.scrub();
			}
		}
		
		private final Camera poolCamera;
		private final RunnablePool runnablePool=new RunnablePool();

		public PreviewBufferPool(Camera poolCamera) {
			this.poolCamera=poolCamera;
		}
		
		@Override
		public void clear() {
		}

		@Override
		public void release(byte[] object) {
			RunnableImpl runnableImpl=runnablePool.acquire();
			runnableImpl.object=object;
			handler.post(runnableImpl);
		}

		@Override
		public void release(List<byte[]> list) {
			for (int ii=list.size()-1; 0<=ii; --ii) {
				release(list.remove(ii));
			}
		}
	}

	private class PreviewCallback implements Camera.PreviewCallback {
		@Override
		public void onPreviewFrame(byte[] bytes, Camera camera) {
			if (CameraHolder.this.camera==camera) {
				imagePusher.onPreviewFrame(bytes, camera, previewBufferPool);
			}
		}
	}
	
	private class TakePicture implements Runnable {
		@Override
		public void run() {
			if (CameraHolder.this.camera==camera) {
				PictureCallback callback=pictureCallbackPool.acquire();
				callback.camera=camera;
				camera.setPreviewCallbackWithBuffer(null);
				camera.setPreviewCallback(null);
				camera.takePicture(null, null, null, callback);
				preview=false;
			}
		}
	}
	
	private final Activity activity;
	private Camera camera;
	private int cameraId=-1;
	private final CameraParameters cameraParameters;
	private final ErrorHandler errorHandler;
	private final Handler handler;
	private final ImagePusher imagePusher;
	private Integer orientation;
	private final PictureCallbackPool pictureCallbackPool
			=new PictureCallbackPool();
	private boolean preview;
	private final PreviewCallback previewCallback=new PreviewCallback();
	private final SurfaceHolder previewHolder;
	private PreviewBufferPool previewBufferPool;
	private final ScreenBackground screenBackground;
	private Camera.Size size;
	private final TakePicture takePicture=new TakePicture();
	private boolean takingPicture;
	private final float[] temp90=new float[9];
	private final float[] temp91=new float[9];
	private final float[] temp92=new float[9];
	private final float[] temp93=new float[9];

	public CameraHolder(Activity activity,
			AsyncImageStreamHolder asyncImageStreamHolder,
			CameraParameters cameraParameters, Context context,
			ErrorHandler errorHandler, Handler handler,
			SurfaceHolder previewHolder, ScreenBackground screenBackground,
			SettingsManager settingsManager) {
		this.activity=activity;
		this.cameraParameters=cameraParameters;
		this.errorHandler=errorHandler;
		this.handler=handler;
		this.previewHolder=previewHolder;
		this.screenBackground=screenBackground;
		imagePusher=new ImagePusher(asyncImageStreamHolder, this, context,
				this, settingsManager);
	}
	
	public Camera.Parameters cameraParameters(int cameraId) {
		return cameraParameters.cameraParameters(cameraId);
	}

	public Camera.Parameters cameraParameters(Settings settings,
			SettingsManager settingsManager) {
		return cameraParameters(
				settingsManager.cameraSetting().getTyped(settings).id);
	}

	public Camera.Parameters cameraParameters(
			SettingsManager settingsManager) {
		return cameraParameters(settingsManager.get(), settingsManager);
	}
	
	public void closeCamera() {
		try {
			cameraId=-1;
			orientation=null;
			preview=false;
			takingPicture=false;
			if (null!=camera) {
				try {
					try {
						camera.stopPreview();
					}
					finally {
						camera.release();
					}
				}
				finally {
						camera=null;
				}
			}
		}
		finally {
			previewHolder.removeCallback(this);
		}
	}
	
	@Override
	protected void closeImpl() throws Throwable {
		closeCamera();
	}
	
	private void error(Throwable throwable) {
		errorHandler.error(throwable);
	}

	public void reloadCamera(SettingsManager settingsManager) {
		checkClosed();
		closeCamera();
		CameraSetting cameraSetting=settingsManager.cameraSetting();
		IdName idName=settingsManager.unvalidatedSetting(cameraSetting);
		if (!cameraSetting.validTyped(this, null, settingsManager, idName)) {
			idName=cameraSetting
					.defaultValueTyped(this, null, settingsManager);
		}
		try {
			camera=cameraParameters.openCamera(idName.id);
		}
		catch (Throwable throwable) {
			AndroidErrorHandler.d(throwable);
			Toast.makeText(activity, "Couldn't open camera.",
						Toast.LENGTH_SHORT)
					.show();
			activity.finish();
			return;
		}
		camera.setErrorCallback(new ErrorCallback());
		Settings settings=settingsManager.get();
		cameraId=cameraSetting.getTyped(settings).id;
		if (cameraId!=idName.id) {
			closeCamera();
			try {
				camera=cameraParameters.openCamera(cameraId);
			}
			catch (Throwable throwable) {
				AndroidErrorHandler.d(throwable);
				Toast.makeText(activity, "Couldn't open camera.",
							Toast.LENGTH_SHORT)
						.show();
				activity.finish();
				return;
			}
		}
		Camera.Parameters parameters2=camera.getParameters();
		setParameters(this, parameters2, settings, settingsManager);
		size=parameters2.getPreviewSize();
		camera.setParameters(parameters2);
		setOrientation();
		previewHolder.addCallback(this);
		previewBufferPool=new PreviewBufferPool(camera);
		startPreview();
	}
	
	public void setOrientation() {
		int cameraFacing;
		int cameraOrientation;
		try {
			Camera.CameraInfo info=new Camera.CameraInfo();
			Camera.getCameraInfo(cameraId, info);
			cameraFacing=info.facing;
			cameraOrientation=info.orientation;
		}
		catch (Throwable throwable) {
			AndroidErrorHandler.d(throwable);
			cameraFacing=Camera.CameraInfo.CAMERA_FACING_BACK;
			cameraOrientation=90;
		}
		int rotation=activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		int degrees;
		switch (rotation) {
			case Surface.ROTATION_0:
				degrees=0;
				break;
			case Surface.ROTATION_180:
				degrees=180;
				break;
			case Surface.ROTATION_270:
				degrees=270;
				break;
			case Surface.ROTATION_90:
				degrees=90;
				break;
			default:
				degrees=0;
				break;
		}
		int orientation2;
		Matrix3fs.shift(-0.5f, -0.5f, temp90);
		if (Camera.CameraInfo.CAMERA_FACING_FRONT==cameraFacing) {
			orientation2=(cameraOrientation+degrees)%360;
			orientation2=(360-orientation2)%360;
			switch (orientation2) {
				case 0:
					Matrix3fs.mirrorX(temp93);
					Matrix3fs.multiplyMatrixMatrix(temp93, temp90, temp91);
					break;
				case 180:
					Matrix3fs.rotateClockwise(temp93);
					Matrix3fs.multiplyMatrixMatrix(temp93, temp90, temp91);
					Matrix3fs.multiplyMatrixMatrix(temp93, temp91, temp92);
					Matrix3fs.mirrorX(temp93);
					Matrix3fs.multiplyMatrixMatrix(temp93, temp92, temp91);
					break;
				case 270:
					Matrix3fs.rotateCounterClockwise(temp93);
					Matrix3fs.multiplyMatrixMatrix(temp93, temp90, temp92);
					Matrix3fs.mirrorX(temp93);
					Matrix3fs.multiplyMatrixMatrix(temp93, temp92, temp91);
					break;
				case 90:
					Matrix3fs.rotateClockwise(temp93);
					Matrix3fs.multiplyMatrixMatrix(temp93, temp90, temp92);
					Matrix3fs.mirrorX(temp93);
					Matrix3fs.multiplyMatrixMatrix(temp93, temp92, temp91);
					break;
				default:
					throw new RuntimeException(String.format(
							"unknown orientation %1$s", orientation2));
			}
		}
		else {
			orientation2=(cameraOrientation-degrees+360)%360;
			switch (orientation2) {
				case 0:
					Matrix3fs.identity(temp93);
					Matrix3fs.multiplyMatrixMatrix(temp93, temp90, temp91);
					break;
				case 180:
					Matrix3fs.rotateCounterClockwise(temp93);
					Matrix3fs.multiplyMatrixMatrix(temp93, temp90, temp92);
					Matrix3fs.multiplyMatrixMatrix(temp93, temp92, temp91);
					break;
				case 270:
					Matrix3fs.rotateClockwise(temp93);
					Matrix3fs.multiplyMatrixMatrix(temp93, temp90, temp91);
					break;
				case 90:
					Matrix3fs.rotateCounterClockwise(temp93);
					Matrix3fs.multiplyMatrixMatrix(temp93, temp90, temp91);
					break;
				default:
					throw new RuntimeException(String.format(
							"unexpected orientation %1$s", orientation2));
			}
		}
		Matrix3fs.shift(0.5f, 0.5f, temp93);
		Matrix3fs.multiplyMatrixMatrix(temp93, temp91, temp90);
		orientation=orientation2;
		if (preview) {
			startPreview();
		}
		screenBackground.setTransform(temp90);
	}
	
	public static void setParameters(CameraHolder cameraHolder,
			Camera.Parameters parameters,
			Settings settings, SettingsManager settingsManager) {
		parameters.setJpegQuality(100);
		parameters.setJpegThumbnailQuality(100);
		List<Camera.Size> jpegThumbnailSizes
				=parameters.getSupportedJpegThumbnailSizes();
		if (null!=jpegThumbnailSizes) {
			for (Camera.Size size: jpegThumbnailSizes) {
				if ((0==size.height)
						&& (0==size.width)) {
					parameters.setJpegThumbnailSize(0, 0);
					break;
				}
			}
		}
		try {
			if (parameters.getSupportedPictureFormats()
					.contains(ImageFormat.NV21)) {
				parameters.setPictureFormat(ImageFormat.NV21);
			}
		}
		catch (NullPointerException ex) {
			AndroidErrorHandler.d(ex);
			//Camera.Parameters.getSupportedPictureFormats() throws
			//NullPointerException in the emulator
		}
		try {
			if (parameters.getSupportedPreviewFormats()
					.contains(ImageFormat.NV21)) {
				parameters.setPreviewFormat(ImageFormat.NV21);
			}
		}
		catch (NullPointerException ex) {
			AndroidErrorHandler.d(ex);
			//Camera.Parameters.getSupportedPreviewFormats() throws
			//NullPointerException in the emulator
		}
		for (CameraParameterSetting<?> setting:
				settingsManager.cameraParameterSettingsList()) {
			if (setting.valid(cameraHolder, settings, settingsManager)) {
				setting.set(parameters, setting.getTyped(settings));
			}
		}
	}

	@Override
	public Camera.Size size() {
		return size;
	}
	
	private void startPreview() {
		if (null!=camera) {
			try {
				preview=false;
				camera.stopPreview();
				if (null!=orientation) {
					camera.setDisplayOrientation(orientation);
					orientation=null;
				}
				camera.setPreviewDisplay(previewHolder);
				camera.setPreviewCallbackWithBuffer(null);
				camera.setPreviewCallbackWithBuffer(previewCallback);
				int bufferSize=size.width*size.height
						*ImageFormat.getBitsPerPixel(
								camera.getParameters().getPreviewFormat())
						/8;
				for (int ii=PREVIEW_BUFFERS; 0<ii; --ii) {
					camera.addCallbackBuffer(new byte[bufferSize]);
				}
				camera.startPreview();
				preview=true;
			}
			catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder sh, int format, int width,
			int height) {
		startPreview();
	}

	@Override
	public void surfaceCreated(SurfaceHolder sh) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder sh) {
	}

	public void takePicture() {
		if (takingPicture) {
			return;
		}
		takingPicture=true;
		handler.post(takePicture);
	}
}
