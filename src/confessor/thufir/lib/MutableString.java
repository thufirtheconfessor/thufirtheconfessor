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

public class MutableString
		implements Appendable, CharSequence, Comparable<MutableString> {
	public static final int DEFAULT_CAPACITY=16;
	
	public char[] buf;
	public int length;
	public int offset;

	public MutableString(char[] buf, int length, int offset) {
		if (null==buf) {
			throw new NullPointerException("buf");
		}
		if ((0>offset)
				|| (buf.length<offset)) {
			throw new IndexOutOfBoundsException(Integer.toString(offset));
		}
		if ((0>length)
				|| (buf.length<offset+length)){
			throw new IndexOutOfBoundsException(Integer.toString(length));
		}
		this.buf=buf;
		this.length=length;
		this.offset=offset;
	}

	public MutableString(int capacity) {
		this(new char[capacity], 0, 0);
	}

	public MutableString() {
		this(DEFAULT_CAPACITY);
	}
	
	public MutableString(CharSequence csq) {
		length=csq.length();
		offset=0;
		buf=new char[length];
		for (int ii=length-1; 0<=ii; --ii) {
			buf[ii]=csq.charAt(ii);
		}
	}

	@Override
	public MutableString append(CharSequence csq) {
		return append(csq, 0, csq.length());
	}

	@Override
	public MutableString append(CharSequence csq, int start, int end) {
		if (end<start) {
			throw new IndexOutOfBoundsException(Integer.toString(end));
		}
		ensureFreeCapacity(end-start);
		while (start<end) {
			buf[offset+length]=csq.charAt(start);
			++length;
			++start;
		}
		return this;
	}

	@Override
	public MutableString append(char cc) {
		ensureFreeCapacity(1);
		buf[offset+length]=cc;
		++length;
		return this;
	}

	@Override
	public char charAt(int index) {
		if ((0>index)
				|| (length<=index)) {
			throw new IndexOutOfBoundsException(Integer.toString(index));
		}
		return buf[offset+index];
	}
	
	public void charAt(int index, char cc) {
		if ((0>index)
				|| (length<=index)) {
			throw new IndexOutOfBoundsException(Integer.toString(index));
		}
		buf[offset+index]=cc;
	}
	
	public int capacity() {
		return buf.length;
	}
	
	public MutableString clear() {
		length=0;
		offset=0;
		return this;
	}

	@Override
	public int compareTo(MutableString obj) {
		int length2;
		if (length<=obj.length) {
			length2=length;
		}
		else {
			length2=obj.length;
		}
		for (int ii=0; length2>ii; ++ii) {
			char c0=buf[offset+ii];
			char c1=obj.buf[obj.offset+ii];
			if (c0>c1) {
				return 1;
			}
			if (c0<c1) {
				return -1;
			}
		}
		if (length>obj.length) {
			return 1;
		}
		if (length<obj.length) {
			return -1;
		}
		return 0;
	}
	
	private void ensureFreeCapacity(int capacity) {
		capacity+=length;
		if (buf.length-offset>=capacity) {
			return;
		}
		if (buf.length>=capacity) {
			System.arraycopy(buf, offset, buf, 0, length);
			offset=0;
			return;
		}
		if (capacity<2*buf.length) {
			capacity=2*buf.length;
		}
		char[] newBuf=new char[capacity];
		System.arraycopy(buf, offset, newBuf, 0, length);
		buf=newBuf;
		offset=0;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this==obj) {
			return true;
		}
		if ((null==obj)
				|| (!getClass().equals(obj.getClass()))) {
			return false;
		}
		MutableString mutableString=(MutableString)obj;
		if (length!=mutableString.length) {
			return false;
		}
		for (int ii=length-1; 0<=ii; --ii) {
			if (buf[offset+ii]!=mutableString.buf[mutableString.offset+ii]) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		int result=41*length;
		for (int ii=length-1; 0<=ii; --ii) {
			result=37*result+buf[offset+ii];
		}
		return result;
	}

	@Override
	public int length() {
		return length;
	}

	@Override
	public MutableString subSequence(int start, int end) {
		if ((0>start)
				|| (end<start)) {
			throw new IndexOutOfBoundsException(Integer.toString(start));
		}
		if (length<end) {
			throw new IndexOutOfBoundsException(Integer.toString(end));
		}
		return new MutableString(buf, end-start, offset+start);
	}

	@Override
	public String toString() {
		return new String(buf, offset, length);
	}
}
