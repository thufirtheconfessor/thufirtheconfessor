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

import android.content.Context;
import android.os.Handler;
import confessor.thufir.diags.DiagnosticsHolder;
import confessor.thufir.lib.Closeables;
import confessor.thufir.lib.Formatter;
import confessor.thufir.lib.stream.AsyncImageStreamHolder;
import confessor.thufir.lib.thread.ThreadPool;
import confessor.thufir.settings.SettingsManager;
import confessor.thufir.widgets.screen.Screen;

public interface Env {
	class Mutable implements Env {
		private AndroidErrorHandler androidErrorHandler;
		private AsyncImageStreamHolder asyncImageStreamHolder;
		private CameraHolder cameraHolder;
		private CameraParameters cameraParameters;
		private Closeables closeables;
		private Context context;
		private DiagnosticsHolder diagnosticsHolder;
		private FallbackErrorHandler fallbackErrorHandler;
		private Formatter formatter;
		private Handler handler;
		private Screen screen;
		private SettingsManager settingsManager;
		private ThreadPool threadPool;
		private WakeLockStreamHolderListener wakeLockStreamHolderListener;

		@Override
		public AndroidErrorHandler androidErrorHandler() {
			return androidErrorHandler;
		}

		public void androidErrorHandler(
				AndroidErrorHandler androidErrorHandler) {
			this.androidErrorHandler=androidErrorHandler;
		}

		@Override
		public AsyncImageStreamHolder asyncImageStreamHolder() {
			return asyncImageStreamHolder;
		}

		public void asyncImageStreamHolder(
				AsyncImageStreamHolder asyncImageStreamHolder) {
			this.asyncImageStreamHolder=asyncImageStreamHolder;
		}

		@Override
		public CameraHolder cameraHolder() {
			return cameraHolder;
		}

		public void cameraHolder(CameraHolder cameraHolder) {
			this.cameraHolder=cameraHolder;
		}

		@Override
		public CameraParameters cameraParameters() {
			return cameraParameters;
		}

		public void cameraParameters(CameraParameters cameraParameters) {
			this.cameraParameters=cameraParameters;
		}

		@Override
		public Closeables closeables() {
			return closeables;
		}

		public void closeables(Closeables closeables) {
			this.closeables=closeables;
		}

		@Override
		public Context context() {
			return context;
		}

		public void context(Context context) {
			this.context=context;
		}

		@Override
		public DiagnosticsHolder diagnosticsHolder() {
			return diagnosticsHolder;
		}

		public void diagnosticsHolder(DiagnosticsHolder diagnosticsHolder) {
			this.diagnosticsHolder=diagnosticsHolder;
		}

		@Override
		public FallbackErrorHandler fallbackErrorHandler() {
			return fallbackErrorHandler;
		}

		public void fallbackErrorHandler(
				FallbackErrorHandler fallbackErrorHandler) {
			this.fallbackErrorHandler=fallbackErrorHandler;
		}

		@Override
		public Formatter formatter() {
			return formatter;
		}

		public void formatter(Formatter formatter) {
			this.formatter=formatter;
		}

		@Override
		public Handler handler() {
			return handler;
		}

		public void handler(Handler handler) {
			this.handler=handler;
		}

		@Override
		public Screen screen() {
			return screen;
		}

		public void screen(Screen screen) {
			this.screen=screen;
		}

		@Override
		public SettingsManager settingsManager() {
			return settingsManager;
		}

		public void settingsManager(SettingsManager settingsManager) {
			this.settingsManager=settingsManager;
		}

		@Override
		public ThreadPool threadPool() {
			return threadPool;
		}

		public void threadPool(ThreadPool threadPool) {
			this.threadPool=threadPool;
		}

		@Override
		public WakeLockStreamHolderListener wakeLockStreamHolderListener() {
			return wakeLockStreamHolderListener;
		}
		
		public void wakeLockStreamHolderListener(
				WakeLockStreamHolderListener wakeLockStreamHolderListener) {
			this.wakeLockStreamHolderListener=wakeLockStreamHolderListener;
		}
	}
	
	AndroidErrorHandler androidErrorHandler();
	AsyncImageStreamHolder asyncImageStreamHolder();
	CameraHolder cameraHolder();
	CameraParameters cameraParameters();
	Closeables closeables();
	Context context();
	DiagnosticsHolder diagnosticsHolder();
	FallbackErrorHandler fallbackErrorHandler();
	Formatter formatter();
	Handler handler();
	Screen screen();
	SettingsManager settingsManager();
	ThreadPool threadPool();
	WakeLockStreamHolderListener wakeLockStreamHolderListener();
}
