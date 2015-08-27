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

import confessor.thufir.lib.tracking.Geometries;

public class LineScan {
	public boolean horizontal;
	public float xx;
	public float yy;
	public float zz;
	
	public boolean findBorder(Image image, int x0, int y0,
			int x1, int y1) {
		xx=Float.NaN;
		yy=Float.NaN;
		zz=Float.NaN;
		byte pb=image.brightness(x0, y0);
		if (pb==image.brightness(x1, y1)) {
			return false;
		}
		int dx=x1-x0;
		int dy=y1-y0;
		if (Math.abs(dx)>=Math.abs(dy)) {
			int dd=0<dx?1:-1;
			int ix=x0+dd;
			for (; x1!=ix; ix+=dd) {
				int iy=Math.round(Geometries.interpolate(x0, dx, ix, y0, dy));
				byte bb=image.brightness(ix, iy);
				if (pb!=bb) {
					break;
				}
			}
			horizontal=true;
			xx=ix-0.5f*dd;
			return true;
		}
		else {
			int dd=0<dy?1:-1;
			int iy=y0+dd;
			for (; y1!=iy; iy+=dd) {
				int ix=Math.round(Geometries.interpolate(y0, dy, iy, x0, dx));
				byte bb=image.brightness(ix, iy);
				if (pb!=bb) {
					break;
				}
			}
			horizontal=false;
			yy=iy-0.5f*dd;
			return true;
		}
	}
	
	public boolean findBorder(Image image, float x0, float y0, float z0,
			float x1, float y1, float z1) {
		float fx0=x0/z0;
		float fx1=x1/z1;
		float fy0=y0/z0;
		float fy1=y1/z1;
		int ix0=Math.round(fx0);
		int ix1=Math.round(fx1);
		int iy0=Math.round(fy0);
		int iy1=Math.round(fy1);
		if (!findBorder(image, ix0, iy0, ix1, iy1)) {
			return false;
		}
		if (horizontal) {
			yy=Geometries.interpolate(fx0, fx1-fx0, xx, fy0, fy1-fy0);
		}
		else {
			xx=Geometries.interpolate(fy0, fy1-fy0, yy, fx0, fx1-fx0);
		}
		float xd=x1-x0;
		float yd=y1-y0;
		float zd=z1-z0;
		zz=Geometries.interpolateZ(x0, y0, z0, x1, y1, z1, xx, yy);
		xx*=zz;
		yy*=zz;
		if (horizontal) {
			yy=Geometries.interpolate(x0, xd, xx, y0, yd);
			zz=Geometries.interpolate(x0, xd, xx, z0, zd);
		}
		else {
			xx=Geometries.interpolate(y0, yd, yy, x0, xd);
			zz=Geometries.interpolate(y0, yd, yy, z0, zd);
		}
		return true;
	}
}
