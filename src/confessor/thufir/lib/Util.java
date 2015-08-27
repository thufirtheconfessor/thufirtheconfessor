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

public class Util {
	private Util() {
	}
	
	public static int ceiling(float value) {
		int result=Math.round(value);
		if (result<value) {
			++result;
		}
		return result;
	}
	
	public static int compare(int i0, int i1) {
		if (i0<i1) {
			return -1;
		}
		if (i0>i1) {
			return 1;
		}
		return 0;
	}
	
	public static boolean equals(Object object0, Object object1) {
		return ((null==object0)
					&& (null==object1))
				|| ((null!=object0)
					&& object0.equals(object1));
	}
	
	public static boolean equals(CharSequence charSequence0,
			CharSequence charSequence1) {
		if (charSequence0==charSequence1) {
			return true;
		}
		if (null==charSequence0) {
			return null==charSequence1;
		}
		if (null==charSequence1) {
			return false;
		}
		int length=charSequence0.length();
		if (charSequence1.length()!=length) {
			return false;
		}
		for (int ii=length-1; 0<=ii; --ii) {
			if (charSequence0.charAt(ii)!=charSequence1.charAt(ii)) {
				return false;
			}
		}
		return true;
	}
	
	public static int floor(float value) {
		int result=Math.round(value);
		if (result>value) {
			--result;
		}
		return result;
	}
	
	public static int hashCode(Object object) {
		if (null==object) {
			return 0;
		}
		return object.hashCode();
	}
	
	public static float sqr(float xx) {
		return xx*xx;
	}
	
	public static float sqrt(float xx) {
		return (float)Math.sqrt(xx);
	}
}
