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
package confessor.thufir.settings.camera;

import android.hardware.Camera;

public class Size implements Comparable<Size> {
	public final int height;
	public final int width;

	public Size(int height, int width) {
		this.height=height;
		this.width=width;
	}

	public Size(Camera.Size size) {
		this(size.height, size.width);
	}

	@Override
	public int compareTo(Size size) {
		int pixels0=height*width;
		int pixels1=size.height*size.width;
		if (pixels0>pixels1) {
			return 1;
		}
		if (pixels0<pixels1) {
			return -1;
		}
		if (width>size.width) {
			return 1;
		}
		if (width<size.width) {
			return -1;
		}
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if ((null==obj)
				|| (!getClass().equals(obj.getClass()))) {
			return false;
		}
		Size size=(Size)obj;
		return (height==size.height)
				&& (width==size.width);
	}

	@Override
	public int hashCode() {
		return 41*height+width;
	}
	
	@Override
	public String toString() {
		return width+"x"+height;
	}
}
