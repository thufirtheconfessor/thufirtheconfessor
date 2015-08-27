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
package confessor.thufir.lib.stream;

import confessor.thufir.lib.AbstractCloseable;
import confessor.thufir.lib.color.BrightnessClassifier;
import confessor.thufir.lib.image.Image;
import confessor.thufir.lib.image.ImageFactory;

public class AsyncImageStreamHolder extends AbstractCloseable
		implements ErrorHandler {
	public interface Listener {
		void streamChanged(AsyncImageStreamHolder holder);
	}
	
	private class RunnableImpl implements Runnable {
		@Override
		public void run() {
			while (true) {
				try {
					DiagnosticsStream diagnosticsStream2;
					int droppedImages2;
					ImageFactory imageFactory2;
					ImageStream stream2;
					synchronized (abstractCloseableLock) {
						if (isClosed()) {
							break;
						}
						if (null==imageFactory) {
							abstractCloseableLock.wait();
							continue;
						}
						else if (null==effectiveImageStream) {
							imageFactory2=imageFactory;
							droppedImages=0;
							imageFactory=null;
							imageFactory2.release();
							abstractCloseableLock.wait();
							continue;
						}
						else {
							diagnosticsStream2=diagnosticsStream;
							droppedImages2=droppedImages;
							imageFactory2=imageFactory;
							droppedImages=0;
							imageFactory=null;
							stream2=effectiveImageStream;
						}
					}
					if (null!=imageFactory2) {
						try {
							long start=System.currentTimeMillis();
							if (null!=diagnosticsStream2) {
								diagnosticsStream2.droppedImages(
										droppedImages2);
							}
							imageFactory2.image(image);
							try {
								stream2.filter().filter(image);
								stream2.brightnessClassifierInitializer()
										.init(brightnessClassifier, image);
								if (null!=diagnosticsStream2) {
									diagnosticsStream2.grayscaleImage(
											brightnessClassifier, image);
								}
								stream2.next(brightnessClassifier, image);
								long end=System.currentTimeMillis();
								if (null!=diagnosticsStream2) {
									diagnosticsStream2.processingTime(
											end-start);
								}
							}
							finally {
								try {
									imageFactory2.release(image);
								}
								finally {
									image.scrub();
								}
							}
						}
						finally {
							imageFactory2.release();
						}
					}
				}
				catch (Throwable throwable) {
					error(throwable);
				}
			}
		}
	}
	
	private final BrightnessClassifier brightnessClassifier
			=new BrightnessClassifier();
	private DiagnosticsStream diagnosticsStream;
	private int droppedImages;
	private ImageStream effectiveImageStream;
	private final ErrorHandler errorHandler;
	private ImageFactory imageFactory;
	private final Image image=new Image();
	private ImageStream imageStream;
	private final Listener listener;
	private boolean started;

	public AsyncImageStreamHolder(ErrorHandler errorHandler,
			Listener listener) {
		this.errorHandler=errorHandler;
		this.listener=listener;
	}

	@Override
	protected void closeImpl() throws Throwable {
		synchronized (abstractCloseableLock) {
			effectiveImageStream=null;
			try {
				if (null!=imageFactory) {
					try {
						imageFactory.release();
					}
					finally {
						imageFactory=null;
					}
				}
			}
			finally {
				abstractCloseableLock.notifyAll();
			}
		}
	}

	public void diagnostics(DiagnosticsStream diagnosticsStream)
			throws Throwable {
		synchronized (abstractCloseableLock) {
			checkClosed();
			this.diagnosticsStream=diagnosticsStream;
			setEffectiveStream();
		}
	}

	@Override
	public void error(Throwable throwable) {
		ErrorHandler errorHandler2;
		synchronized (abstractCloseableLock) {
			if (null==imageStream) {
				errorHandler2=errorHandler;
			}
			else {
				errorHandler2=imageStream;
			}
		}
		errorHandler2.error(throwable);
	}
	
	public boolean hasDiagnosticsStream() {
		synchronized (abstractCloseableLock) {
			return null!=diagnosticsStream;
		}
	}
	
	public boolean hasStream() {
		synchronized (abstractCloseableLock) {
			return null!=effectiveImageStream;
		}
	}

	public void next(ImageFactory imageFactory) throws Throwable {
		synchronized (abstractCloseableLock) {
			checkClosed();
			if (null==effectiveImageStream) {
				imageFactory.release();
				return;
			}
			if (null!=this.imageFactory) {
				try {
					this.imageFactory.release();
				}
				finally {
					this.imageFactory=null;
				}
				++droppedImages;
			}
			this.imageFactory=imageFactory;
			if (started) {
				abstractCloseableLock.notifyAll();
				return;
			}
			started=true;
		}
		new Thread(new RunnableImpl()).start();
	}
	
	private void setEffectiveStream() throws Throwable {
		synchronized (abstractCloseableLock) {
			if (null==imageStream) {
				effectiveImageStream=diagnosticsStream;
			}
			else {
				effectiveImageStream=imageStream;
			}
			listener.streamChanged(this);
		}
	}

	public void stream(ImageStream imageStream) throws Throwable {
		synchronized (abstractCloseableLock) {
			checkClosed();
			this.imageStream=imageStream;
			setEffectiveStream();
		}
	}
}
