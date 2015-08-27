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
import confessor.thufir.lib.image.Image;
import java.util.Arrays;

public abstract class AbstractBlobDetector implements BlobDetector {
	protected static final int[] DX=new int[]{-1, 1, 0, 0};
	protected static final int[] DY=new int[]{0, 0, -1, 1};
	
	private int[] dirty;
	
	@Override
	public void clear(Image image) {
		int dirtyLength=image.width*image.height/32+1;
		if ((null==dirty)
				|| (dirty.length!=dirtyLength)) {
			dirty=new int[dirtyLength];
		}
		else {
			Arrays.fill(dirty, 0);
		}
	}

	@Override
	public Blob detect(BlobPool blobPool, boolean brightOnly, Image image,
			int maxQuietZoneDistance, int xx, int yy) {
		int width=image.width;
		int height=image.height;
		if ((0>xx)
				|| (0>yy)
				|| (width<=xx)
				|| (height<=yy)
				|| getAndSetDirty(width, xx, yy)) {
			return null;
		}
		byte brightness=image.brightness(xx, yy);
		if (brightOnly
				&& (Brightness.DARK==brightness)) {
			return null;
		}
		byte dark;
		if (Brightness.BRIGHT==brightness) {
			dark=Brightness.DARK;
		}
		else {
			dark=Brightness.BRIGHT;
		}
		if (!roughQuietZone(dark, image, maxQuietZoneDistance, xx, yy, width,
				height)) {
			return null;
		}
		Blob blob=detectImpl(blobPool, brightness, dark, image, xx, yy,
				width, height);
		if (null==blob) {
			return null;
		}
		if (!quietZone(blob, dark, image, width, height)) {
			blobPool.release(blob);
			return null;
		}
		return blob;
	}
	
	protected abstract Blob detectImpl(BlobPool blobPool, byte bright,
			byte dark, Image image, int xx, int yy, int width, int height);
	
	protected boolean getAndSetDirty(int offset) {
		int arrayOffset=offset>>5;
		int bitOffset=offset&0x1f;
		int mask=1<<bitOffset;
		int dirty2=dirty[arrayOffset];
		boolean clean=0==(dirty2&mask);
		if (clean) {
			dirty[arrayOffset]=dirty2|mask;
		}
		return !clean;
	}
	
	protected boolean getAndSetDirty(int width, int xx, int yy) {
		return getAndSetDirty(yy*width+xx);
	}
	
	protected boolean getAndSetDirty(Image image, int xx, int yy) {
		return getAndSetDirty(image.offset(xx, yy));
	}
	
	private boolean roughQuietZone(byte dark, Image image, int maxQuietZone,
			int xx, int yy, int width, int height) {
		return roughQuietZone(dark, image, maxQuietZone, xx, yy, 1, 1,
						width, height)
				&& roughQuietZone(dark, image, maxQuietZone, xx, yy, 1, 0,
						width, height)
				&& roughQuietZone(dark, image, maxQuietZone, xx, yy, 1, -1,
						width, height)
				&& roughQuietZone(dark, image, maxQuietZone, xx, yy, -1, 1,
						width, height)
				&& roughQuietZone(dark, image, maxQuietZone, xx, yy, -1, 0,
						width, height)
				&& roughQuietZone(dark, image, maxQuietZone, xx, yy, -1, -1,
						width, height)
				&& roughQuietZone(dark, image, maxQuietZone, xx, yy, 0, 1,
						width, height)
				&& roughQuietZone(dark, image, maxQuietZone, xx, yy, 0, -1,
						width, height);
	}
	
	private boolean roughQuietZone(byte dark, Image image, int maxQuietZone,
			int xx, int yy, int dx, int dy, int width, int height) {
		int distance=1;
		while (distance<=maxQuietZone) {
			int x2=xx+dx;
			if ((0>x2)
					|| (width<=x2)) {
				return false;
			}
			int y2=yy+dy;
			if ((0>y2)
					|| (height<=y2)) {
				return false;
			}
			if (dark==image.brightness(x2, y2)) {
				return true;
			}
			distance<<=1;
			dx<<=1;
			dy<<=1;
		}
		return false;
	}
	
	private boolean quietZone(Blob blob, byte dark, Image image,
			int width, int height) {
		return quietZone(blob, dark, image, 1f, 1f, width, height)
				&& quietZone(blob, dark, image, 1f, 0f, width, height)
				&& quietZone(blob, dark, image, 1f, -1f, width, height)
				&& quietZone(blob, dark, image, -1f, 1f, width, height)
				&& quietZone(blob, dark, image, -1f, 0f, width, height)
				&& quietZone(blob, dark, image, -1f, -1f, width, height)
				&& quietZone(blob, dark, image, 0f, 1f, width, height)
				&& quietZone(blob, dark, image, 0f, -1f, width, height);
	}
	
	private boolean quietZone(Blob blob, byte dark, Image image,
			float dx, float dy, int width, int height) {
		int xx=Math.round(blob.x+2f*dx*blob.radius);
		if ((0>xx)
				|| (width<=xx)) {
			return false;
		}
		int yy=Math.round(blob.y+2f*dy*blob.radius);
		if ((0>yy)
				|| (height<=yy)) {
			return false;
		}
		return dark==image.brightness(xx, yy);
	}
}
