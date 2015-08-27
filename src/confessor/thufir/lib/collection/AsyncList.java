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

public class AsyncList<T> {
	private final Deque<T> deque=new Deque<T>();
	private int eol;
	private final Object lock=new Object();
	
	public void add(T object) {
		synchronized (lock) {
			deque.addLast(object);
			lock.notifyAll();
		}
	}
	
	public boolean addIfDoesntContain(T element, Equals<T> equals) {
		synchronized (lock) {
			for (int ii=deque.size()-1; 0<=ii; --ii) {
				if (equals.equals(element, deque.get(ii))) {
					return false;
				}
			}
			deque.addLast(element);
			lock.notifyAll();
			return true;
		}
	}
	
	public T get(int index, T terminator) {
		synchronized (lock) {
			while (true) {
				if (deque.size()>index) {
					return deque.get(index);
				}
				if (0>=eol) {
					return terminator;
				}
				try {
					lock.wait();
				}
				catch (InterruptedException ex) {
					throw new RuntimeException(ex);
				}
			}
		}
	}
	
	public void eol() {
		synchronized (lock) {
			if (0<eol) {
				--eol;
				if (0>=eol) {
					lock.notifyAll();
				}
			}
		}
	}
	
	public void eol(int eol) {
		synchronized (lock) {
			this.eol=eol;
			lock.notifyAll();
		}
	}
	
	public void forceEol() {
		synchronized (lock) {
			if (0<eol) {
				eol=0;
				lock.notifyAll();
			}
		}
	}
	
	public void reset(int eol) {
		synchronized (lock) {
			deque.clear();
			this.eol=eol;
			lock.notifyAll();
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder(32);
		sb.append("AsyncList(eol=");
		synchronized (lock) {
			sb.append(eol);
			int size=deque.size();
			for (int ii=0; size>ii; ++ii) {
				sb.append(", ");
				sb.append(deque.get(ii));
			}
		}
		sb.append(')');
		return sb.toString();
	}
}
