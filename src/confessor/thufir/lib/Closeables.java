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

public class Closeables extends AbstractCloseable {
	private static class Node {
		public final Closeable closeable;
		public final Node next;

		public Node(Closeable closeable, Node next) {
			this.closeable=closeable;
			this.next=next;
		}
	}
	
	private Node node;
	
	public void add(Closeable closeable) {
		synchronized (abstractCloseableLock) {
			checkClosed();
			if (null!=closeable) {
				node=new Node(closeable, node);
			}
			removeCloseds();
		}
	}

	@Override
	protected void closeImpl() throws Throwable {
		Throwable throwable=null;
		while (null!=node) {
			Node node2=node;
			if (null!=node2.closeable) {
				try {
					node2.closeable.close();
				}
				catch (Throwable throwable2) {
					if (null==throwable) {
						throwable=throwable2;
					}
				}
			}
			if (node2==node) {
				node=node.next;
			}
		}
		if (null!=throwable) {
			throw new RuntimeException(throwable);
		}
	}
	
	public void remove(Closeable closeable) {
		synchronized (abstractCloseableLock) {
			Node node2=node;
			Node node3=null;
			while (null!=node2) {
				if ((null!=node2.closeable)
						&& (closeable!=node2.closeable)
						&& (!node2.closeable.isClosed())) {
					node3=new Node(node2.closeable, node3);
				}
				node2=node2.next;
			}
			while (null!=node3) {
				node2=new Node(node3.closeable, node2);
				node3=node3.next;
			}
			node=node2;
		}
	}
	
	public void removeCloseds() {
		remove(null);
	}
}
