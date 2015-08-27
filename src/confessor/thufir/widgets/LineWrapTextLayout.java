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
package confessor.thufir.widgets;

import java.util.List;

public class LineWrapTextLayout extends WhitespaceTextLayout {
	@Override
	protected void layout(String line, List<String> layout, int charsPerLine) {
		lineWrap(line, layout, charsPerLine);
	}
	
	public static void lineWrap(String line, List<String> layout,
			int charsPerLine) {
		if (charsPerLine>=line.length()) {
			layout.add(line);
			return;
		}
		for (int ii=0; line.length()>ii; ii+=charsPerLine) {
			layout.add(line.substring(ii,
					Math.min(ii+charsPerLine, line.length())));
		}
	}

	@Override
	public String toString() {
		return "Line wrap";
	}
}
