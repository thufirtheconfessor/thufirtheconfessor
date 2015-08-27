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

import confessor.thufir.lib.image.AbstractImageReader;

public class SimpleThufir4ImageReader
		extends AbstractImageReader<Thufir4Data> {
	@Override
	public byte data(int xx, int yy) {
		float x3=data.border0x
				+xx*data.data1x
				+data.half1x
				+yy*data.data2x
				+data.half2x;
		float y3=data.border0y
				+xx*data.data1y
				+data.half1y
				+yy*data.data2y
				+data.half2y;
		float z3=data.border0z
				+xx*data.data1z
				+data.half1z
				+yy*data.data2z
				+data.half2z;
		return image.brightness(x3, y3, z3);
	}

	@Override
	protected void initImpl() {
	}
}
