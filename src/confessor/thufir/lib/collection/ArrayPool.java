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

public abstract class ArrayPool<T> extends AbstractPool<T> {
	public ArrayPool(int initialSize, Integer maxSize) {
		super(initialSize, maxSize);
	}
	
	public T acquire(int size) {
		synchronized (abstractCloseableLock) {
			checkClosed();
			for (int ii=deque.size(); 0<ii; --ii) {
				T array=deque.removeFirst();
				if (equals(array, size)) {
					init(array, size);
					return array;
				}
				deque.addLast(array);
			}
			return create(size);
		}
	}
	
	public void clearExcept(int size) {
		synchronized (abstractCloseableLock) {
			checkClosed();
			for (int ii=deque.size(); 0<ii; --ii) {
				T value=deque.removeFirst();
				if (equals(value, size)) {
					deque.addLast(value);
				}
			}
		}
	}
	
	protected abstract T create(int size);
	
	protected abstract boolean equals(T array, int size);
	
	protected abstract void init(T array, int size);
}
