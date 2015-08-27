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
package confessor.thufir.lib.tracking;

import confessor.thufir.lib.color.Brightness;
import confessor.thufir.lib.color.BrightnessClassifier;
import confessor.thufir.lib.color.BrightnessClassifierInitializer;
import confessor.thufir.lib.image.Filter;
import confessor.thufir.lib.image.Image;
import confessor.thufir.lib.stream.ImageGeometryStream;
import confessor.thufir.lib.stream.ImageStream;
import confessor.thufir.lib.thread.Job;
import confessor.thufir.lib.thread.ThreadPool;

public class DetectorStream<D extends GeometryData, G extends Geometry<D, J>
			, J extends Job>
		implements ImageStream {
	private final Byte bright;
	private final BrightnessClassifierInitializer
			brightnessClassifierInitializer;
	private final ParallelDetector<D, G, J> detector;
	private final Filter filter;
	private final G geometry;
	private D geometryData;
	private final ImageGeometryStream<D> stream;
	private final ThreadPool threadPool;

	public DetectorStream(BlobSize blobSize, Byte bright,
			BrightnessClassifierInitializer brightnessClassifierInitializer,
			Filter filter, G geometry, ImageGeometryStream<D> stream,
			ThreadPool threadPool) {
		this.bright=bright;
		this.brightnessClassifierInitializer=brightnessClassifierInitializer;
		this.filter=filter;
		this.geometry=geometry;
		this.stream=stream;
		this.threadPool=threadPool;
		detector=new ParallelDetector<D, G, J>(blobSize, bright, geometry,
				threadPool);
	}

	@Override
	public BrightnessClassifierInitializer brightnessClassifierInitializer()
			throws Throwable {
		return brightnessClassifierInitializer;
	}

	@Override
	public void error(Throwable throwable) {
		stream.error(throwable);
	}

	@Override
	public Filter filter() throws Throwable {
		return filter;
	}

	@Override
	public void next(BrightnessClassifier brightnessClassifier, Image image)
			throws Throwable {
		if (null==image) {
			return;
		}
		boolean reverse=((null!=bright)
					&& (Brightness.DARK==bright.byteValue()))
				|| ((null==bright)
					&& (null!=geometryData)
					&& (!geometryData.brightOnDark()));
		if (reverse) {
			brightnessClassifier.classifyReverse(image.brightness, threadPool);
		}
		else {
			brightnessClassifier.classify(image.brightness, threadPool);
		}
		D geometryData2=detector.detect(geometryData, image);
		if (null!=geometryData) {
			geometry.dataPool().release(geometryData);
			geometryData=null;
		}
		geometryData=geometryData2;
		if (null!=geometryData) {
			if (!geometryData.brightOnDark()) {
				brightnessClassifier.reverse(image.brightness, threadPool);
			}
			if (reverse) {
				geometryData.flipBrightOnDark();
			}
		}
		stream.next(geometryData, image);
	}
}
