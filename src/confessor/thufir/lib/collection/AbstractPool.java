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

import confessor.thufir.lib.AbstractCloseable;
import java.util.List;

public abstract class AbstractPool<T> extends AbstractCloseable
		implements Pool<T> {
	protected final Deque<T> deque;
	protected final Integer maxSize;

	public AbstractPool(int initialSize, Integer maxSize) {
		this.maxSize=maxSize;
		deque=new Deque<T>(initialSize);
	}
	
	@Override
	public void clear() {
		synchronized (abstractCloseableLock) {
			checkClosed();
			deque.clear();
		}
	}

	@Override
	protected void closeImpl() throws Throwable {
		synchronized (abstractCloseableLock) {
			deque.clear();
		}
	}
	
	@Override
	public void release(T object) {
		synchronized (abstractCloseableLock) {
			if (isClosed()) {
				return;
			}
			scrub(object);
			if (null!=maxSize) {
				while (maxSize<=deque.size()) {
					deque.removeFirst();
				}
			}
			deque.addLast(object);
		}
	}
	
	@Override
	public void release(List<T> list) {
		synchronized (abstractCloseableLock) {
			for (int ii=list.size()-1; 0<=ii; --ii) {
				release(list.remove(ii));
			}
		}
	}
	
	protected abstract void scrub(T object);
}
