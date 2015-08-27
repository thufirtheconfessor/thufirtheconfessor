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

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<T> extends AbstractDeque<T> {
	public static final Function<Integer> MAX=new Max();
	public static final Function<Integer> MIN=new Min();
	public static final Function<Integer> SUM=new Sum();
	
	public interface Function<T> {
		T apply(T left, T right);
	}
	
	private class IteratorImpl implements Iterator<T> {
		private int index;

		@Override
		public boolean hasNext() {
			return size()>index;
		}

		@Override
		public T next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			T result=get(index);
			++index;
			return result;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	public static class Max implements Function<Integer> {
		@Override
		public Integer apply(Integer left, Integer right) {
			if (left>=right) {
				return left;
			}
			return right;
		}
	}
	
	public static class Min implements Function<Integer> {
		@Override
		public Integer apply(Integer left, Integer right) {
			if (left<=right) {
				return left;
			}
			return right;
		}
	}
	
	public static class Sum implements Function<Integer> {
		@Override
		public Integer apply(Integer left, Integer right) {
			return left+right;
		}
	}
	
	private Object[] array;
	
	public Deque(int capacity) {
		array=new Object[capacity+1];
	}
	
	public Deque() {
		this(DEFAULT_CAPACITY);
	}
	
	public void addAllLast(T[] values, int offset, int length) {
		addAllLastImpl(values, offset, length);
	}
	
	public void addAllLast(Deque<T> deque) {
		if (deque.isEmpty()) {
			return;
		}
		ensureCapacity(size()+deque.size());
		int first2=deque.first;
		int last2=deque.last;
		if (first2<last2) {
			addAllLastImpl(deque.array, first2, last2-first2);
		}
		else {
			addAllLastImpl(deque.array, first2, deque.arraySize()-first2);
			addAllLastImpl(deque.array, 0, last2);
		}
	}
	
	private void addAllLastImpl(Object[] values, int offset, int length) {
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
	
	public void addFirst(T value) {
		ensureCapacity(size()+1);
		first=decrease(first);
		array[first]=value;
	}
	
	public void addLast(T value) {
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
		Arrays.fill(array, from, to, null);
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
		Object[] newBuf=new Object[newCapacity+1];
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
	
	@SuppressWarnings("unchecked")
	public T foldLeft(Function<T> function, T initialValue) {
		if (first<=last) {
			for (int ii=first; last>ii; ++ii) {
				initialValue=function.apply(initialValue, (T)array[ii]);
			}
		}
		else {
			for (int ii=first; array.length>ii; ++ii) {
				initialValue=function.apply(initialValue, (T)array[ii]);
			}
			for (int ii=0; last>ii; ++ii) {
				initialValue=function.apply(initialValue, (T)array[ii]);
			}
		}
		return initialValue;
	}
	
	@SuppressWarnings("unchecked")
	public T get(int index) {
		return (T)array[index(index)];
	}

	@Override
	public Iterator<T> iterator() {
		return new IteratorImpl();
	}
	
	@SuppressWarnings("unchecked")
	public T removeFirst() throws NoSuchElementException {
		if (first==last) {
			throw new NoSuchElementException();
		}
		T result=(T)array[first];
		array[first]=null;
		first=increase(first);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public T removeLast() throws NoSuchElementException {
		if (first==last) {
			throw new NoSuchElementException();
		}
		last=decrease(last);
		T result=(T)array[last];
		array[last]=null;
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public T set(int index, T value) {
		index=index(index);
		T result=(T)array[index];
		array[index]=value;
		return result;
	}

	@Override
	protected void toString(int index, StringBuilder stringBuilder) {
		stringBuilder.append(array[index]);
	}
}
