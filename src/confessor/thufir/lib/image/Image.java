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

public class Image {
	public byte[] brightness;
	public int height;
	public int width;

	public Image() {
	}

	public Image(int width, int height) {
		this.height=height;
		this.width=width;
		brightness=new byte[height*width];
	}
	
	public int addX(int offset, int xx) {
		return offset+xx;
	}
	
	public int addY(int offset, int yy) {
		return offset+width*yy;
	}
	
	public byte brightness(int offset) {
		return brightness[offset];
	}
	
	public byte brightness(int xx, int yy) {
		return brightness[yy*width+xx];
	}
	
	public byte brightness(float xx, float yy) {
		int x2=Math.round(xx);
		if ((0>x2)
				|| (width<=x2)) {
			return 0;
		}
		int y2=Math.round(yy);
		if ((0>y2)
				|| (height<=y2)) {
			return 0;
		}
		return brightness[y2*width+x2];
	}
	
	public byte brightness(float xx, float yy, float zz) {
		int x2=Math.round(xx/zz);
		if ((0>x2)
				|| (width<=x2)) {
			return 0;
		}
		int y2=Math.round(yy/zz);
		if ((0>y2)
				|| (height<=y2)) {
			return 0;
		}
		return brightness[y2*width+x2];
	}
	
	public int offset(int xx, int yy) {
		return yy*width+xx;
	}
	
	public void set(byte[] brightness, int height, int width) {
		this.brightness=brightness;
		this.height=height;
		this.width=width;
	}
	
	public void scrub() {
		this.brightness=null;
	}
	
	public int xx(int offset) {
		return offset%width;
	}
	
	public int yy(int offset) {
		return offset/width;
	}
}
