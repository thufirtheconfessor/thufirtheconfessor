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

import confessor.thufir.lib.image.Image;

public class Blob {
	public static final Equals EQUALS=new Equals();
	
	public static class Equals
			implements confessor.thufir.lib.collection.Equals<Blob> {
		@Override
		public boolean equals(Blob object0, Blob object1) {
			float dx=object0.x-object1.x;
			float dy=object0.y-object1.y;
			float dr=object0.radius+object1.radius;
			return dr*dr>dx*dx+dy*dy;
		}
	}
	
	public boolean bright;
	public float radius;
	public float x;
	public float y;
	
	public void copy(Blob other) {
		bright=other.bright;
		radius=other.radius;
		x=other.x;
		y=other.y;
	}
	
	private static boolean quiet(byte dark, Image image, float xx, float yy) {
		int ix=Math.round(xx);
		if ((0>ix)
				|| (image.width<=ix)) {
			return false;
		}
		int iy=Math.round(yy);
		if ((0>iy)
				|| (image.height<=iy)) {
			return false;
		}
		return dark==image.brightness(ix, iy);
	}
	
	public boolean quiet(byte dark, Image image) {
		float dd=2*radius;
		return quiet(dark, image, x-dd, y-dd)
				&& quiet(dark, image, x, y-dd)
				&& quiet(dark, image, x+dd, y-dd)
				&& quiet(dark, image, x-dd, y)
				&& quiet(dark, image, x+dd, y)
				&& quiet(dark, image, x-dd, y+dd)
				&& quiet(dark, image, x, y+dd)
				&& quiet(dark, image, x+dd, y+dd);
	}

	@Override
	public String toString() {
		return "Blob("+x+", "+y+": "+radius+", "+bright+")";
	}
}
