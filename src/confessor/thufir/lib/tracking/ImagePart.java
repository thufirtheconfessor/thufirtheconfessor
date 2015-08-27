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

import confessor.thufir.lib.collection.Deque;

public class ImagePart {
	public static final ImagePart UNIT=new ImagePart(0, 0, 0, 1, 1);
	
	public final int fromX;
	public final int fromY;
	public final int shift;
	public final int toX;
	public final int toY;

	public ImagePart(int fromX, int fromY, int shift, int toX, int toY) {
		this.fromX=fromX;
		this.fromY=fromY;
		this.shift=shift;
		this.toX=toX;
		this.toY=toY;
	}
	
	public int fromX(int width) {
		return (fromX*width)>>shift;
	}
	
	public int fromY(int height) {
		return (fromY*height)>>shift;
	}
	
	public static Deque<ImagePart> split(int size) {
		Deque<ImagePart> deque=new Deque<ImagePart>(size);
		deque.addLast(UNIT);
		--size;
		for (; 0<size; --size) {
			deque.removeFirst().split(deque);
		}
		return deque;
	}
	
	public void split(Deque<ImagePart> deque) {
		int fromX2=fromX<<1;
		int fromY2=fromY<<1;
		int shift2=shift+1;
		int toX2=toX<<1;
		int toY2=toY<<1;
		if (toX-fromX>toY-fromY) {
			int middle=fromX+toX;
			deque.addLast(new ImagePart(fromX2, fromY2, shift2, middle, toY2));
			deque.addLast(new ImagePart(middle, fromY2, shift2, toX2, toY2));
		}
		else {
			int middle=fromY+toY;
			deque.addLast(new ImagePart(fromX2, fromY2, shift2, toX2, middle));
			deque.addLast(new ImagePart(fromX2, middle, shift2, toX2, toY2));
		}
	}
	
	@Override
	public String toString() {
		return "ImagePart("+fromX+", "+fromY+"-"+toX+", "+toY+"/1<<"+shift+")";
	}
	
	public int toX(int width) {
		return (toX*width)>>shift;
	}
	
	public int toY(int height) {
		return (toY*height)>>shift;
	}
}
