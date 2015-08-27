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
package confessor.thufir.settings.program;

import confessor.thufir.lib.color.Brightness;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface BrightnessScheme {
	BrightnessScheme AUTO=new BrightnessScheme() {
				@Override
				public Byte bright() {
					return null;
				}
				
				@Override
				public String toString() {
					return "auto";
				}
			};
	BrightnessScheme BRIGHT_ON_DARK=new BrightnessScheme() {
				@Override
				public Byte bright() {
					return Brightness.BRIGHT;
				}
				
				@Override
				public String toString() {
					return "bright on dark";
				}
			};
	BrightnessScheme DARK_ON_BRIGHT=new BrightnessScheme() {
				@Override
				public Byte bright() {
					return Brightness.DARK;
				}
				
				@Override
				public String toString() {
					return "dark on bright";
				}
			};
	List<BrightnessScheme> VALUES=Collections.unmodifiableList(Arrays.asList(
					AUTO, BRIGHT_ON_DARK, DARK_ON_BRIGHT));
	
	Byte bright();
}
