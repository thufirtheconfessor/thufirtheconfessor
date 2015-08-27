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
package confessor.thufir.lib;

import java.util.Collection;
import java.util.Comparator;

public class Rect {
	public static class RectComparator implements Comparator<Rect> {
		@Override
		public int compare(Rect rect0, Rect rect1) {
			int cc=Util.compare(rect0.x0, rect1.x0);
			if (0!=cc) {
				return cc;
			}
			cc=Util.compare(rect0.y0, rect1.y0);
			if (0!=cc) {
				return cc;
			}
			cc=Util.compare(rect0.x1, rect1.x1);
			if (0!=cc) {
				return cc;
			}
			return Util.compare(rect0.y1, rect1.y1);
		}
	}
	
	public final int height;
	public final int width;
	public final int x0;
	public final int x1;
	public final int y0;
	public final int y1;

	public Rect(int x0, int x1, int y0, int y1) {
		this.x0=x0;
		this.x1=x1;
		this.y0=y0;
		this.y1=y1;
		height=y1-y0;
		width=x1-x0;
	}
	
	public void addBorders(Collection<Rect> collection,
			boolean bottom, boolean left, boolean right, boolean top) {
		if (isEmpty()) {
			return;
		}
		int x2=x0;
		int x3=x1;
		if (left) {
			--x2;
			addNonEmpty(collection, create(x2, y0, x2+1, y1));
		}
		if (right) {
			addNonEmpty(collection, create(x3, y0, x3+1, y1));
			++x3;
		}
		if (top) {
			addNonEmpty(collection, create(x2, y0-1, x3, y0));
		}
		if (bottom) {
			addNonEmpty(collection, create(x2, y1, x3, y1+1));
		}
	}
	
	public static void addBorders(Rect rect, Collection<Rect> collection,
			boolean bottom, boolean left, boolean right, boolean top) {
		if (!isEmpty(rect)) {
			rect.addBorders(collection, bottom, left, right, top);
		}
	}
	
	public static void addNonEmpty(Collection<Rect> collection, Rect rect) {
		if (!Rect.isEmpty(rect)) {
			collection.add(rect);
		}
	}
	
	public Rect bottom(int yy) {
		if (isEmpty()) {
			return null;
		}
		if (y0>=yy) {
			return this;
		}
		if (y1<=yy) {
			return null;
		}
		return new Rect(x0, x1, yy, y1);
	}
	
	public boolean canJoinHorizontal(Rect right) {
		return (!isEmpty())
				&& (!isEmpty(right))
				&& (x1==right.x0)
				&& (y0==right.y0)
				&& (y1==right.y1);
	}
	
	public boolean canJoinVertical(Rect bottom) {
		return (!isEmpty())
				&& (!isEmpty(bottom))
				&& (x0==bottom.x0)
				&& (x1==bottom.x1)
				&& (y1==bottom.y0);
	}
	
	public static Rect create(int x0, int y0, int x1, int y1) {
		if ((x0>=x1)
				|| (y0>=y1)) {
			return null;
		}
		return new Rect(x0, x1, y0, y1);
	}

	@Override
	public boolean equals(Object obj) {
		if ((null==obj)
				|| (!getClass().equals(obj.getClass()))) {
			return false;
		}
		Rect rect=(Rect)obj;
		return (x0==rect.x0)
				&& (x1==rect.x1)
				&& (y0==rect.y0)
				&& (y1==rect.y1);
	}
	
	public static boolean equals(Rect rect0, Rect rect1) {
		if (isEmpty(rect0)) {
			return isEmpty(rect1);
		}
		else {
			return rect0.equals(rect1);
		}
	}

	@Override
	public int hashCode() {
		return x0
				+17*x1
				+17*31*y0
				+17*31*47*y1;
	}
	
	public boolean in(int xx, int yy) {
		return (x0<=xx)
				&& (x1>xx)
				&& (y0<=yy)
				&& (y1>yy);
	}

	public Rect intersect(Rect rect) {
		if (isEmpty()
				|| isEmpty(rect)) {
			return null;
		}
		int x2=Math.max(x0, rect.x0);
		int x3=Math.min(x1, rect.x1);
		int y2=Math.max(y0, rect.y0);
		int y3=Math.min(y1, rect.y1);
		if ((x2>=x3)
				|| (y2>=y3)) {
			return null;
		}
		if ((x0==x2)
				&& (x1==x3)
				&& (y0==y2)
				&& (y1==y3)) {
			return this;
		}
		if ((rect.x0==x2)
				&& (rect.x1==x3)
				&& (rect.y0==y2)
				&& (rect.y1==y3)) {
			return rect;
		}
		return new Rect(x2, x3, y2, y3);
	}
	
	public boolean isEmpty() {
		return (0>=height)
				|| (0>=width);
	}
	
	public static boolean isEmpty(Rect rect) {
		return (null==rect)
				|| rect.isEmpty();
	}
	
	public Rect joinHorizontal(Rect right) {
		if (canJoinHorizontal(right)) {
			return create(x0, y0, right.x1, y1);
		}
		else {
			return null;
		}
	}
	
	public Rect joinVertical(Rect bottom) {
		if (canJoinVertical(bottom)) {
			return create(x0, y0, x1, bottom.y1);
		}
		else {
			return null;
		}
	}
	
	public Rect left(int xx) {
		if (isEmpty()) {
			return null;
		}
		if (x0>=xx) {
			return null;
		}
		if (x1<=xx) {
			return this;
		}
		return new Rect(x0, xx, y0, y1);
	}
	
	public void remove(Collection<Rect> collection, Rect rect) {
		if (isEmpty()) {
			return;
		}
		if (isEmpty(rect)
				|| (x1<=rect.x0)
				|| (rect.x1<=x0)
				|| (y1<=rect.y0)
				|| (rect.y1<=y0)) {
			collection.add(this);
			return;
		}
		addNonEmpty(collection, top(rect.y0));
		Rect rect2=bottom(rect.y0);
		if (!isEmpty(rect2)) {
			addNonEmpty(collection, rect2.bottom(rect.y1));
			rect2=rect2.top(rect.y1);
			if (!isEmpty(rect2)) {
				addNonEmpty(collection, rect2.left(rect.x0));
				rect2=rect2.right(rect.x0);
				if (!isEmpty(rect2)) {
					addNonEmpty(collection, rect2.right(rect.x1));
				}
			}
		}
	}
	
	public Rect right(int xx) {
		if (isEmpty()) {
			return null;
		}
		if (x0>=xx) {
			return this;
		}
		if (x1<=xx) {
			return null;
		}
		return new Rect(xx, x1, y0, y1);
	}
	
	public Rect top(int yy) {
		if (isEmpty()) {
			return null;
		}
		if (y0>=yy) {
			return null;
		}
		if (y1<=yy) {
			return this;
		}
		return new Rect(x0, x1, y0, yy);
	}
	
	@Override
	public String toString() {
		return "Rect("+x0+", "+y0+"-"+x1+", "+y1+")";
	}
}
