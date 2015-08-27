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

import confessor.thufir.lib.color.BrightnessClassifier;
import confessor.thufir.lib.color.BrightnessClassifierInitializer;
import confessor.thufir.lib.image.Filter;
import confessor.thufir.lib.image.Image;
import confessor.thufir.lib.image.NoFilter;

public class NullImageStream implements ImageStream {
	private final BrightnessClassifierInitializer
			brightnessClassifierInitializer;
	private final ErrorHandler errorHandler;

	public NullImageStream(
			BrightnessClassifierInitializer brightnessClassifierInitializer,
			ErrorHandler errorHandler) {
		this.brightnessClassifierInitializer=brightnessClassifierInitializer;
		this.errorHandler=errorHandler;
	}

	@Override
	public BrightnessClassifierInitializer brightnessClassifierInitializer()
			throws Throwable {
		return brightnessClassifierInitializer;
	}
	
	@Override
	public void error(Throwable throwable) {
		errorHandler.error(throwable);
	}

	@Override
	public Filter filter() throws Throwable {
		return NoFilter.INSTANCE;
	}

	@Override
	public void next(BrightnessClassifier brightnessClassifier, Image image)
			throws Throwable {
	}
}
