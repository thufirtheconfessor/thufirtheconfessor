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
package confessor.thufir.lib.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class IntDeque extends AbstractDeque<Integer> {
	public static final Function MAX=new Max();
	public static final Function MIN=new Min();
	public static final Function SUM=new Sum();
	
	public interface Function {
		int apply(int left, int right);
	}
	
	private class IteratorImpl implements Iterator<Integer> {
		private int index;

		@Override
		public boolean hasNext() {
			return size()>index;
		}

		@Override
		public Integer next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			Integer result=get(index);
			++index;
			return result;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	public static class Max implements Function {
		@Override
		public int apply(int left, int right) {
			if (left>=right) {
				return left;
			}
			return right;
		}
	}
	
	public static class Min implements Function {
		@Override
		public int apply(int left, int right) {
			if (left<=right) {
				return left;
			}
			return right;
		}
	}
	
	public static class Sum implements Function {
		@Override
		public int apply(int left, int right) {
			return left+right;
		}
	}
	
	private int[] array;
	
	public IntDeque(int capacity) {
		array=new int[capacity+1];
	}
	
	public IntDeque() {
		this(AbstractDeque.DEFAULT_CAPACITY);
	}
	
	public void addAllLast(int[] values, int offset, int length) {
		if (0>=length) {
			return;
		}
		ensureCapacity(size()+length);
		int length2;
		if (first<=last) {
			length2=arraySize()-last;
		}
		else {
			length2=first-last;
		}
		if (length==length2) {
			System.arraycopy(values, offset, array, last, length);
			last=0;
		}
		else if (length<length2) {
			System.arraycopy(values, offset, array, last, length);
			last+=length;
		}
		else {
			System.arraycopy(values, offset, array, last, length2);
			last=length-length2;
			System.arraycopy(values, offset+length2, array, 0, last);
		}
	}
	
	public void addAllLast(IntDeque deque) {
		if (deque.isEmpty()) {
			return;
		}
		ensureCapacity(size()+deque.size());
		int first2=deque.first;
		int last2=deque.last;
		if (first2<last2) {
			addAllLast(deque.array, first2, last2-first2);
		}
		else {
			addAllLast(deque.array, first2, deque.arraySize()-first2);
			addAllLast(deque.array, 0, last2);
		}
	}
	
	public void addFirst(int value) {
		ensureCapacity(size()+1);
		first=decrease(first);
		array[first]=value;
	}
	
	public void addLast(int value) {
		ensureCapacity(size()+1);
		array[last]=value;
		last=increase(last);
	}

	@Override
	protected int arraySize() {
		return array.length;
	}

	@Override
	protected void clear(int from, int to) {
	}
	
	private void ensureCapacity(int requiredCapacity) {
		int currentCapacity=capacity();
		if (currentCapacity>=requiredCapacity) {
			return;
		}
		int newCapacity=2*currentCapacity;
		if (requiredCapacity>newCapacity) {
			newCapacity=requiredCapacity;
		}
		int[] newBuf=new int[newCapacity+1];
		int newLast;
		if (first<=last) {
			newLast=last-first;
			System.arraycopy(array, first, newBuf, 0, newLast);
		}
		else {
			int length1=array.length-first;
			newLast=length1+last;
			System.arraycopy(array, first, newBuf, 0, length1);
			System.arraycopy(array, 0, newBuf, length1, last);
		}
		array=newBuf;
		first=0;
		last=newLast;
	}
	
	public int foldLeft(Function function, int initialValue) {
		if (first<=last) {
			for (int ii=first; last>ii; ++ii) {
				initialValue=function.apply(initialValue, array[ii]);
			}
		}
		else {
			for (int ii=first; array.length>ii; ++ii) {
				initialValue=function.apply(initialValue, array[ii]);
			}
			for (int ii=0; last>ii; ++ii) {
				initialValue=function.apply(initialValue, array[ii]);
			}
		}
		return initialValue;
	}
	
	public int get(int index) {
		return array[index(index)];
	}

	@Override
	public Iterator<Integer> iterator() {
		return new IteratorImpl();
	}
	
	public int removeFirst() throws NoSuchElementException {
		if (first==last) {
			throw new NoSuchElementException();
		}
		int result=array[first];
		first=increase(first);
		return result;
	}
	
	public int removeLast() throws NoSuchElementException {
		if (first==last) {
			throw new NoSuchElementException();
		}
		last=decrease(last);
		return array[last];
	}
	
	public int set(int index, int value) {
		index=index(index);
		int result=array[index];
		array[index]=value;
		return result;
	}

	@Override
	protected void toString(int index, StringBuilder stringBuilder) {
		stringBuilder.append(array[index]);
	}
}
