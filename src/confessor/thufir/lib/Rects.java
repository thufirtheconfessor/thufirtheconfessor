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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class Rects implements Iterable<Rect> {
	private static class Joint {
		public final Integer bottom;
		public final int center;
		public final Integer left;
		public final int rank;
		public final Integer right;
		public final Integer top;

		public Joint(Integer bottom, int center,
				Integer left, Integer right, Integer top) {
			this.bottom=bottom;
			this.center=center;
			this.left=left;
			this.right=right;
			this.top=top;
			rank=rank(bottom)
					+rank(center)
					+rank(left)
					+rank(right)
					+rank(top);
		}
	
		private static int rank(Integer index) {
			return null==index?0:1;
		}
	}
	
	private ArrayList<Rect> rects=new ArrayList<Rect>();
	
	public void add(Rect rect) {
		Rect.addNonEmpty(rects, rect);
	}
	
	private static Rect addExcess(List<Rect> list, Rect rect0, Rect rect1) {
		if (Rect.isEmpty(rect0)) {
			if (Rect.isEmpty(rect1)) {
				throw new IllegalArgumentException();
			}
			else {
				return rect1;
			}
		}
		else {
			if (!Rect.isEmpty(rect1)) {
				list.add(rect1);
			}
			return rect0;
		}
	}
	
	public void addRemaining(Rect rect) {
		ArrayList<Rect> remaining=new ArrayList<Rect>();
		Rect.addNonEmpty(remaining, rect);
		ArrayList<Rect> temp=new ArrayList<Rect>();
		for (int size=rects.size(), ii=0; size>ii; ++ii) {
			temp.clear();
			Rect rect2=rects.get(ii);
			for (int rsize=remaining.size(), jj=0; rsize>jj; ++jj) {
				remaining.get(jj).remove(temp, rect2);
			}
			remaining.clear();
			for (int tsize=temp.size(), jj=0; tsize>jj; ++jj) {
				Rect.addNonEmpty(remaining, temp.get(jj));
			}
		}
		rects.addAll(remaining);
	}
	
	public void addRemainingBorders(Rect rect, boolean bottom, boolean left,
			boolean right, boolean top) {
		ArrayList<Rect> temp=new ArrayList<Rect>();
		Rect.addBorders(rect, temp, bottom, left, right, top);
		for (int tsize=temp.size(), ii=0; tsize>ii; ++ii) {
			addRemaining(temp.get(ii));
		}
	}
	
	private static Joint bestJoint(List<Rect> rects) {
		Joint result=null;
		for (int center=rects.size()-1; 0<=center; --center) {
			Integer bottom=null;
			Integer left=null;
			Integer right=null;
			Integer top=null;
			Rect centerRect=rects.get(center);
			for (int ii=rects.size()-1; 0<=ii; --ii) {
				if (center==ii) {
					continue;
				}
				Rect rect=rects.get(ii);
				if (centerRect.canJoinVertical(rect)) {
					bottom=better(rects, bottom, ii);
				}
				if (rect.canJoinHorizontal(centerRect)) {
					left=better(rects, left, ii);
				}
				if (centerRect.canJoinHorizontal(rect)) {
					right=better(rects, right, ii);
				}
				if (rect.canJoinVertical(centerRect)) {
					top=better(rects, top, ii);
				}
			}
			if ((null!=bottom)
					|| (null!=left)
					|| (null!=right)
					|| (null!=top)) {
				result=better(result,
						new Joint(bottom, center, left, right, top));
			}
		}
		return result;
	}

	private static Joint better(Joint oldJoint, Joint newJoint) {
		if ((null==oldJoint)
				|| (newJoint.rank>oldJoint.rank)) {
			return newJoint;
		}
		return oldJoint;
	}

	private static Integer better(List<Rect> rects, Integer oldIndex,
			int newIndex) {
		if (null==oldIndex) {
			return newIndex;
		}
		Rect oldRect=rects.get(oldIndex);
		Rect newRect=rects.get(newIndex);
		if (newRect.height*newRect.width>oldRect.height*oldRect.width) {
			return newIndex;
		}
		return oldIndex;
	}
	
	public Rect bound() {
		int x0=Integer.MAX_VALUE;
		int y0=Integer.MAX_VALUE;
		int x1=Integer.MIN_VALUE;
		int y1=Integer.MIN_VALUE;
		for (int ii=rects.size()-1; 0<=ii; --ii) {
			Rect rect=rects.get(ii);
			x0=Math.min(x0, rect.x0);
			y0=Math.min(y0, rect.y0);
			x1=Math.max(x1, rect.x1);
			y1=Math.max(y1, rect.y1);
		}
		return Rect.create(x0, y0, x1, y1);
	}
	
	public Rect get(int index) {
		return rects.get(index);
	}
	
	public boolean in(int xx, int yy) {
		for (int ii=rects.size()-1; 0<=ii; --ii) {
			if (rects.get(ii).in(xx, yy)) {
				return true;
			}
		}
		return false;
	}
	
	public void intersect(Rect rect) {
		int size=rects.size();
		ArrayList<Rect> newRects=new ArrayList<Rect>(size);
		for (int ii=0; size>ii; ++ii) {
			Rect.addNonEmpty(newRects, rects.get(ii).intersect(rect));
		}
		rects=newRects;
	}
	
	public boolean isEmpty() {
		return rects.isEmpty();
	}

	@Override
	public Iterator<Rect> iterator() {
		return rects.iterator();
	}
	
	public void join() {
		Collection<Integer> xs=new HashSet<Integer>();
		Collection<Integer> ys=new HashSet<Integer>();
		for (Rect rect: rects) {
			xs.add(rect.x0);
			xs.add(rect.x1);
			ys.add(rect.y0);
			ys.add(rect.y1);
		}
		ArrayList<Rect> newRects=new ArrayList<Rect>(rects);
		for (int ii=0; newRects.size()>ii; ++ii) {
			Rect rect=newRects.get(ii);
			for (int xx: xs) {
				rect=addExcess(newRects, rect.left(xx), rect.right(xx));
			}
			for (int yy: ys) {
				rect=addExcess(newRects, rect.top(yy), rect.bottom(yy));
			}
			newRects.set(ii,rect);
		}
		while (true) {
			Joint joint=bestJoint(newRects);
			if (null==joint) {
				break;
			}
			switch (joint.rank) {
				case 2:
					join2(newRects, joint);
					break;
				case 3:
					join3(newRects, joint);
					break;
				case 4:
					join4(newRects, joint);
					break;
				case 5:
					join5(newRects, joint);
					break;
				default:
					throw new IllegalArgumentException();
			}
		}
		Collections.sort(newRects, new Rect.RectComparator());
		rects=newRects;
	}
	
	private static void join2(List<Rect> rects, Joint joint) {
		Rect center=rects.get(joint.center);
		if (null!=joint.left) {
			Rect left=rects.get(joint.left);
			swapRemove(rects, joint.center, joint.left);
			Rect.addNonEmpty(rects, left.joinHorizontal(center));
		}
		else if (null!=joint.right) {
			Rect right=rects.get(joint.right);
			swapRemove(rects, joint.center, joint.right);
			Rect.addNonEmpty(rects, center.joinHorizontal(right));
		}
		else if (null!=joint.bottom) {
			Rect bottom=rects.get(joint.bottom);
			swapRemove(rects, joint.bottom, joint.center);
			Rect.addNonEmpty(rects, center.joinVertical(bottom));
		}
		else if (null!=joint.top) {
			Rect top=rects.get(joint.top);
			swapRemove(rects, joint.center, joint.top);
			Rect.addNonEmpty(rects, top.joinVertical(center));
		}
	}
	
	private static void join3(List<Rect> rects, Joint joint) {
		if ((null==joint.bottom)!=(null==joint.top)) {
			join2(rects, joint);
		}
		else {
			join4(rects, joint);
		}
	}
	
	private static void join4(List<Rect> rects, Joint joint) {
		Rect center=rects.get(joint.center);
		if ((null==joint.bottom)
				|| (null==joint.top)) {
			Rect left=rects.get(joint.left);
			Rect right=rects.get(joint.right);
			swapRemove(rects, joint.center, joint.left, joint.right);
			Rect.addNonEmpty(rects,
					left.joinHorizontal(center).joinHorizontal(right));
		}
		else {
			Rect bottom=rects.get(joint.bottom);
			Rect top=rects.get(joint.top);
			swapRemove(rects, joint.bottom, joint.center, joint.top);
			Rect.addNonEmpty(rects,
					top.joinVertical(center).joinVertical(bottom));
		}
	}
	
	private static void join5(List<Rect> rects, Joint joint) {
		Rect center=rects.get(joint.center);
		Rect left=rects.get(joint.left);
		Rect right=rects.get(joint.right);
		swapRemove(rects, joint.center, joint.left, joint.right);
		Rect.addNonEmpty(rects,
				left.joinHorizontal(center).joinHorizontal(right));
	}
	
	public void remove(Rect rect) {
		int size=rects.size();
		ArrayList<Rect> newRects=new ArrayList<Rect>(size);
		ArrayList<Rect> temp=new ArrayList<Rect>();
		for (int ii=0; size>ii; ++ii) {
			temp.clear();
			rects.get(ii).remove(temp, rect);
			for (int tsize=temp.size(), jj=0; tsize>jj; ++jj) {
				Rect.addNonEmpty(newRects, temp.get(jj));
			}
		}
		rects=newRects;
	}

	public int size() {
		return rects.size();
	}
	
	public static <T> void swapRemove(List<T> list, int index) {
		int last=list.size()-1;
		if (last!=index) {
			list.set(index, list.get(last));
		}
		list.remove(last);
	}
	
	public static <T> void swapRemove(List<T> list, int index0, int index1) {
		if (index0>index1) {
			swapRemove(list, index0);
			swapRemove(list, index1);
		}
		else {
			swapRemove(list, index1);
			swapRemove(list, index0);
		}
	}
	
	public static <T> void swapRemove(List<T> list, int index0, int index1,
			int index2) {
		if (index0>index1) {
			if (index0>index2) {
				swapRemove(list, index0);
				swapRemove(list, index1, index2);
				return;
			}
		}
		else {
			if (index1>index2) {
				swapRemove(list, index1);
				swapRemove(list, index0, index2);
				return;
			}
		}
		swapRemove(list, index2);
		swapRemove(list, index0, index1);
	}

	@Override
	public String toString() {
		return rects.toString();
	}
}
