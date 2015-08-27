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
package confessor.thufir.diags;

import confessor.thufir.Env;
import confessor.thufir.lib.MutableString;
import confessor.thufir.lib.collection.IntDeque;
import confessor.thufir.lib.color.Brightness;
import confessor.thufir.lib.color.BrightnessClassifier;
import confessor.thufir.lib.image.Contrast;
import confessor.thufir.lib.image.Image;
import confessor.thufir.lib.stream.DiagnosticsStream;
import confessor.thufir.lib.stream.NullImageStream;
import confessor.thufir.settings.SettingsBrightnessClassifierInitializer;

public class Diagnostics {
	public class DiagnosticsStreamImpl extends NullImageStream
			implements DiagnosticsStream {
		private final float[] crosshair=new float[2];
		private final IntDeque droppedImages=new IntDeque(100);
		private final IntDeque frames=new IntDeque(100);
		private long lastFrame=Long.MIN_VALUE;
		private final IntDeque processingTimes=new IntDeque(100);
		
		public DiagnosticsStreamImpl() throws Throwable {
			super(new SettingsBrightnessClassifierInitializer(
						env.settingsManager(), env.threadPool()),
					env.fallbackErrorHandler());
		}

		@Override
		public void droppedImages(int droppedImages) throws Throwable {
			synchronized (lock) {
				while (99<this.droppedImages.size()) {
					this.droppedImages.removeFirst();
				}
				this.droppedImages.addLast(droppedImages);
				droppedImages=100*this.droppedImages.foldLeft(IntDeque.SUM, 0)
								/this.droppedImages.size();
				tempString.clear();
				env.formatter().format(droppedImages/100, tempString);
				env.formatter().decimalSeparator(tempString);
				droppedImages%=100;
				if (10>droppedImages) {
					env.formatter().format(0, tempString);
				}
				env.formatter().format(droppedImages, tempString);
				diagnosticsView.droppedFrames(tempString);
			}
		}

		@Override
		public void grayscaleImage(BrightnessClassifier brightnessClassifier,
				Image image) throws Throwable {
			synchronized (lock) {
				if (null==image) {
					diagnosticsView.avgFrame(empty());
					diagnosticsView.brightness(empty());
					diagnosticsView.brightnessClass(empty());
					diagnosticsView.contrast(empty());
					diagnosticsView.maxFrame(empty());
					diagnosticsView.minFrame(empty());
				}
				else {
					diagnosticsView.threshold(
							number(brightnessClassifier.threshold));

					long now=System.currentTimeMillis();
					if (Long.MIN_VALUE!=lastFrame) {
						while (99<frames.size()) {
							frames.removeFirst();
						}
						frames.addLast((int)(now-lastFrame));
						diagnosticsView.maxFrame(time(frames
								.foldLeft(IntDeque.MAX, Integer.MIN_VALUE)));
						diagnosticsView.minFrame(time(frames
								.foldLeft(IntDeque.MIN, Integer.MAX_VALUE)));
						diagnosticsView.avgFrame(time(
								frames.foldLeft(IntDeque.SUM, 0)
										/frames.size()));
					}
					lastFrame=now;

					env.screen().background().crosshair(crosshair);
					int xx=(int)((image.width-1)*crosshair[0]);
					int yy=(int)((image.height-1)*crosshair[1]);

					byte brightness=image.brightness(xx, yy);
					diagnosticsView.brightness(number(brightness&0xff));
					diagnosticsView.brightnessClass(Brightness.toString(
							brightnessClassifier.brightness(brightness)));

					diagnosticsView.contrast(number(
							Contrast.contrast(image, 8, xx, yy)));
				}
			}
		}

		@Override
		public void processingTime(long processingTime) throws Throwable {
			synchronized (lock) {
				while (99<processingTimes.size()) {
					processingTimes.removeFirst();
				}
				processingTimes.addLast((int)processingTime);
				diagnosticsView.maxProc(time(processingTimes
						.foldLeft(IntDeque.MAX, Integer.MIN_VALUE)));
				diagnosticsView.minProc(time(processingTimes
						.foldLeft(IntDeque.MIN, Integer.MAX_VALUE)));
				diagnosticsView.avgProc(time(
						(processingTimes.foldLeft(IntDeque.SUM, 0)
								/processingTimes.size())));
			}
		}
	}
	
	private final DiagnosticsStream diagnosticsStream;
	private final DiagnosticsView diagnosticsView;
	private final Env env;
	private final Object lock=new Object();
	private final MutableString tempString=new MutableString();

	public Diagnostics(Env env) throws Throwable {
		this.env=env;
		diagnosticsStream=new DiagnosticsStreamImpl();
		diagnosticsView=new DiagnosticsView(env.context());
	}

	public DiagnosticsStream diagnosticsStream() {
		return diagnosticsStream;
	}

	public DiagnosticsView diagnosticsView() {
		return diagnosticsView;
	}
	
	private MutableString empty() {
		tempString.clear();
		tempString.append('-');
		return tempString;
	}
	
	private MutableString number(int value) {
		tempString.clear();
		env.formatter().format(value, tempString);
		return tempString;
	}
	
	private MutableString time(long milliseconds) {
		tempString.clear();
		env.formatter().format(milliseconds, tempString);
		tempString.append("ms");
		return tempString;
	}
}
