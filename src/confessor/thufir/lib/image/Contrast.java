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

public class Contrast {
	private Contrast() {
	}
	
	public static int contrast(Image image, int radius, int xx, int yy) {
		int left=Math.max(0, xx-radius);
		int right=Math.min(image.width-1, xx+radius);
		int top=Math.max(0, yy-radius);
		int bottom=Math.min(image.height-1, yy+radius);
		int count=(right+1-left)*(bottom+1-top);
		if (0>=count) {
			return -1;
		}
		int min=255;
		int max=0;
		for (int x2=left; right>=x2; ++x2) {
			for (int y2=top; bottom>=y2; ++y2) {
				int value=image.brightness(x2, y2)&0xff;
				if (min>value) {
					min=value;
				}
				if (max<value) {
					max=value;
				}
			}
		}
		int length=max-min+1;
		int sum=0;
		for (int x2=left; right>=x2; ++x2) {
			for (int y2=top; bottom>=y2; ++y2) {
				sum+=normalize(min, length, image.brightness(x2, y2)&0xff);
			}
		}
		int avg=sum/count;
		sum=0;
		for (int x2=left; right>=x2; ++x2) {
			for (int y2=top; bottom>=y2; ++y2) {
				sum+=Math.abs(normalize(min, length,
						image.brightness(x2, y2)&0xff)-avg);
			}
		}
		return sum/count;
	}

	private static int normalize(int min, int length, int value) {
		return ((value-min)*256/length);
	}
}
