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
package confessor.thufir.lib.thufir4;

import confessor.thufir.lib.image.Image;
import confessor.thufir.lib.image.ImageReader;
import confessor.thufir.lib.stream.ByteStream;
import confessor.thufir.lib.stream.ImageGeometryStream;
import confessor.thufir.lib.thread.ThreadPool;

public class Thufir4Stream implements ImageGeometryStream<Thufir4Data> {
	private boolean clock;
	private final CrcChecker crcChecker=new CrcChecker();
	private byte[] dataBuffer;
	private final ImageReader<Thufir4Data> imageReader;
	private final ByteStream stream;
	private final ThreadPool threadPool;

	public Thufir4Stream(ImageReader<Thufir4Data> imageReader,
			ByteStream stream, ThreadPool threadPool) {
		this.imageReader=imageReader;
		this.stream=stream;
		this.threadPool=threadPool;
	}

	@Override
	public void error(Throwable throwable) {
		stream.error(throwable);
	}

	@Override
	public void next(Thufir4Data geometryData, Image image) throws Throwable {
		int length=0;
		if ((null!=geometryData)
				&& (null!=image)) {
			geometryData.initInner(image);
			if (geometryData.valid) {
				try {
					imageReader.init(geometryData, image, threadPool);
					if (geometryData.clock(clock, imageReader)) {
						crcChecker.restore(geometryData.dataHeight,
								geometryData.dataWidth);
						byte[] newDatabuffer=geometryData.read(
								clock, crcChecker, dataBuffer, imageReader);
						if (null!=newDatabuffer) {
							dataBuffer=newDatabuffer;
							crcChecker.save();
							clock=!clock;
							length=dataBuffer.length;
						}
					}
				}
				finally {
					imageReader.scrub();
				}
			}
		}
		stream.next(clock, crcChecker.crc(), dataBuffer, 0, length);
	}
	
	public void setState(boolean clock, int crc) {
		this.clock=clock;
		crcChecker.setState(crc);
	}
}
