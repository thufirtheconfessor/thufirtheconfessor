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

uchar __attribute__((kernel)) max3x3(uint32_t x, uint32_t y) {
	uchar result=0;
	for (int dy=-1; 1>=dy; ++dy) {
		for (int dx=-1; 1>=dx; ++dx) {
			result=max(result, rsGetElementAt_uchar(in, x+dx, y+dy));
		}
	}
	return result;
}

uchar __attribute__((kernel)) max3x5(uint32_t x, uint32_t y) {
	uchar result=0;
	for (int dy=-2; 2>=dy; ++dy) {
		for (int dx=-1; 1>=dx; ++dx) {
			result=max(result, rsGetElementAt_uchar(in, x+dx, y+dy));
		}
	}
	return result;
}

uchar __attribute__((kernel)) max5x5(uint32_t x, uint32_t y) {
	uchar result=0;
	for (int dy=-2; 2>=dy; ++dy) {
		for (int dx=-2; 2>=dx; ++dx) {
			result=max(result, rsGetElementAt_uchar(in, x+dx, y+dy));
		}
	}
	return result;
}

uchar __attribute__((kernel)) min3x3(uint32_t x, uint32_t y) {
	uchar result=0xff;
	for (int dy=-1; 1>=dy; ++dy) {
		for (int dx=-1; 1>=dx; ++dx) {
			result=min(result, rsGetElementAt_uchar(in, x+dx, y+dy));
		}
	}
	return result;
}

uchar __attribute__((kernel)) min3x5(uint32_t x, uint32_t y) {
	uchar result=0xff;
	for (int dy=-2; 2>=dy; ++dy) {
		for (int dx=-1; 1>=dx; ++dx) {
			result=min(result, rsGetElementAt_uchar(in, x+dx, y+dy));
		}
	}
	return result;
}

uchar __attribute__((kernel)) min5x5(uint32_t x, uint32_t y) {
	uchar result=0xff;
	for (int dy=-2; 2>=dy; ++dy) {
		for (int dx=-2; 2>=dx; ++dx) {
			result=min(result, rsGetElementAt_uchar(in, x+dx, y+dy));
		}
	}
	return result;
}
