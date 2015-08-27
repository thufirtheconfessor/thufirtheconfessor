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

public abstract class ObjectPool<T> extends AbstractPool<T> {
	public ObjectPool(int initialSize, Integer maxSize) {
		super(initialSize, maxSize);
	}

	public T acquire() {
		synchronized (abstractCloseableLock) {
			checkClosed();
			if (deque.isEmpty()) {
				return create();
			}
			T object=deque.removeFirst();
			init(object);
			return object;
		}
	}

	@Override
	protected void closeImpl() throws Throwable {
		synchronized (abstractCloseableLock) {
			deque.clear();
		}
	}
	
	protected abstract T create();
	
	protected abstract void init(T object);
}
