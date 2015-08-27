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

import java.util.NoSuchElementException;

public abstract class AbstractDeque<T> implements Iterable<T> {
	public static final int DEFAULT_CAPACITY=16;
	
	protected int first;
	protected int last;
	
	protected abstract int arraySize();
	
	public int capacity() {
		return arraySize()-1;
	}
	
	public void clear() {
		if (first!=last) {
			if (first<last) {
				clear(first, last);
			}
			else {
				clear(first, arraySize());
				clear(0, last);
			}
		}
		first=0;
		last=0;
	}
	
	protected abstract void clear(int from, int to);
	
	protected int decrease(int value) {
		if (0<value) {
			return value-1;
		}
		else {
			return arraySize()-1;
		}
	}
	
	protected int increase(int value) {
		++value;
		if (arraySize()>value) {
			return value;
		}
		else {
			return 0;
		}
	}
	
	protected int index(int index) throws NoSuchElementException {
		if ((0>index)
				|| (size()<=index)) {
			throw new NoSuchElementException();
		}
		index+=first;
		if (arraySize()<=index) {
			index-=arraySize();
		}
		return index;
	}
	
	public boolean isEmpty() {
		return first==last;
	}
	
	public int size() {
		if (first<=last) {
			return last-first;
		}
		else {
			return arraySize()-first+last;
		}
	}

	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		sb.append('[');
		if (first<=last) {
			boolean first2=true;
			for (int ii=first; last>ii; ++ii) {
				if (first2) {
					first2=false;
				}
				else {
					sb.append(", ");
				}
				toString(ii, sb);
			}
		}
		else {
			boolean first2=true;
			for (int ii=first; arraySize()>ii; ++ii) {
				if (first2) {
					first2=false;
				}
				else {
					sb.append(", ");
				}
				toString(ii, sb);
			}
			for (int ii=0; last>ii; ++ii) {
				sb.append(", ");
				toString(ii, sb);
			}
		}
		sb.append(']');
		return sb.toString();
	}
	
	protected abstract void toString(int index, StringBuilder stringBuilder);
}
