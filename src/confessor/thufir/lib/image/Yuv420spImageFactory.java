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
package confessor.thufir.lib.image;

import confessor.thufir.lib.collection.Pool;

public class Yuv420spImageFactory implements ImageFactory {
	public Pool<byte[]> byteArrayPool;
	public byte[] data;
	public int height;
	public Pool<Yuv420spImageFactory> imageFactoryPool;
	public int width;

	@Override
	public void image(Image image) throws Throwable {
		image.set(data, height, width);
	}

	@Override
	public void release() {
		byteArrayPool.release(data);
		imageFactoryPool.release(this);
	}

	@Override
	public void release(Image image) {
		image.scrub();
	}
	
	public void scrub() {
		byteArrayPool=null;
		data=null;
		imageFactoryPool=null;
	}

	public void set(Pool<byte[]> byteArrayPool, byte[] data, int height,
			Pool<Yuv420spImageFactory> imageFactoryPool, int width) {
		this.byteArrayPool=byteArrayPool;
		this.data=data;
		this.height=height;
		this.imageFactoryPool=imageFactoryPool;
		this.width=width;
	}
	
	public static boolean valid(byte[] bytes, int height, int width) {
		return bytes.length==((3*height*width)>>1);
	}
}
