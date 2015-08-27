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

public class Vector2is {
	private Vector2is() {
	}
	
	public static int offset(int xx, int yy) {
		return (xx<<16)|yy;
	}
	
	public static int xx(int offset) {
		return offset>>>16;
	}
	
	public static int yy(int offset) {
		return offset&0xffff;
	}
}
