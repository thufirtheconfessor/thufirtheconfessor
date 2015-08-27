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
#pragma version(1)
#pragma rs java_package_name(confessor.thufir.image)

rs_allocation in;

uchar __attribute__((kernel)) root(uint x, uint y) {
	ushort in2=rsGetElementAt_ushort(in, x, y);
	uchar red=(in2>>10)&0x3e;
	uchar green=(in2>>4)&0x7e;
	uchar blue=(in2<<1)&0x3e;
	return red+green+blue;
}
