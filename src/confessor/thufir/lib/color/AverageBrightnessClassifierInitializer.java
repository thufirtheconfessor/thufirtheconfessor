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
package confessor.thufir.lib.color;

import confessor.thufir.lib.image.Image;

public abstract class AverageBrightnessClassifierInitializer
		implements BrightnessClassifierInitializer {
	private int threshold;

	public AverageBrightnessClassifierInitializer() {
		clearImpl();
	}

	@Override
	public void clear() throws Throwable {
		clearImpl();
	}
	
	private void clearImpl() {
		threshold=BrightnessClassifier.DEFAULT_TRESHOLD<<4;
	}

	@Override
	public void init(BrightnessClassifier brightnessClassifier, Image image)
			throws Throwable {
		init(image);
		brightnessClassifier.init(threshold>>4);
	}
	
	protected abstract void init(Image image) throws Throwable;
	
	protected void set(int threshold) {
		this.threshold=((this.threshold+(threshold&0xff))<<4)/17;
	}
}
