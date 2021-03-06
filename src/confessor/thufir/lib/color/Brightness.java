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
package confessor.thufir.lib.color;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Brightness {
	public static final byte BRIGHT=1;
	public static final byte DARK=0;
	public static final List<Byte> VALUES=Collections.unmodifiableList(
			Arrays.asList(DARK, BRIGHT));
	
	private Brightness() {
	}
	
	public static String toString(byte brightness) {
		switch (brightness) {
			case DARK:
				return "dark";
			case BRIGHT:
				return "bright";
			default:
				throw new IllegalArgumentException(String.format(
						"unknown brightness %1$s", brightness));
		}
	}
}
