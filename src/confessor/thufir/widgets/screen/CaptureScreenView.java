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
package confessor.thufir.widgets.screen;

import android.graphics.Bitmap;
import confessor.thufir.Env;
import confessor.thufir.Files;
import confessor.thufir.lib.color.BrightnessClassifier;
import confessor.thufir.lib.color.BrightnessClassifierInitializer;
import confessor.thufir.lib.image.Filter;
import confessor.thufir.lib.image.Image;
import confessor.thufir.lib.image.NoFilter;
import confessor.thufir.lib.stream.ImageStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CaptureScreenView extends OverlayScreenView {
	private class CaptureStream implements ImageStream {
		private final BrightnessClassifierInitializer
				brightnessClassifierInitializer
					=new BrightnessClassifierInitializer() {
			@Override
			public void clear() throws Throwable {
			}

			@Override
			public void init(BrightnessClassifier brightnessClassifier,
					Image image) throws Throwable {
				brightnessClassifier.init(
						BrightnessClassifier.DEFAULT_TRESHOLD);
			}
		};
		private final Filter filter=new NoFilter();
		
		@Override
		public BrightnessClassifierInitializer
		brightnessClassifierInitializer() throws Throwable {
			return brightnessClassifierInitializer;
		}

		@Override
		public void error(Throwable throwable) {
			CaptureScreenView.this.error(throwable);
		}

		@Override
		public Filter filter() throws Throwable {
			return filter;
		}

		@Override
		public void next(BrightnessClassifier brightnessClassifier,
				Image image) throws Throwable {
			Bitmap bitmap=Bitmap.createBitmap(image.width, image.height,
					Bitmap.Config.ARGB_8888);
			for (int oo=0, yy=0; image.height>yy; ++yy) {
				for (int xx=0; image.width>xx; ++xx, ++oo) {
					int bb=image.brightness[oo]&0xff;
					bitmap.setPixel(xx, yy, 0xff000000|(bb<<16)|(bb<<8)|bb);
				}
			}
			File dir=Files.directory();
			int ii=0;
			File file;
			for (; true; ++ii) {
				file=new File(dir, String.format("capture-%1$08x.png", ii));
				if (!file.exists()) {
					break;
				}
			}
			OutputStream outputStream=new FileOutputStream(file);
			try {
				if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)) {
					throw new IOException("compress failed");
				}
			}
			finally {
				outputStream.close();
			}
		}
	}
	
	private final ImageStream imageStream;
	
	public CaptureScreenView(Env env, OverlayScreenFactory parent,
			Integer yScroll) throws Throwable {
		super(env, parent, yScroll);
		imageStream=new CaptureStream();
		setButtons(
				button("Stop", new PopButtonListener(env, this)));
		setContent(textView("Capturing..."));
	}

	@Override
	public ImageStream imageStream() {
		return imageStream;
	}
}
