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

public abstract class WhitespaceTextLayout implements TextLayout {
	public static final char EOL='\u00b6';
	public static final char SPACE='\u0020';
	public static final char TAB='\u0009';
	public static final char TAB_PAD='\u2013';
	public static final char TAB_START='\u00bb';
	public static final int TAB_SIZE=4;
	public static final char VISIBLE_SPACE='\u00b7';

	@Override
	public void layout(String line, List<String> layout, int charsPerLine,
			boolean visibleWhitespaces) {
		StringBuilder sb=new StringBuilder(line.length()+1);
		for (int ii=0; line.length()>ii; ++ii) {
			char cc=line.charAt(ii);
			if (TAB==cc) {
				char pad;
				char start;
				if (visibleWhitespaces) {
					pad=TAB_PAD;
					start=TAB_START;
				}
				else {
					pad=SPACE;
					start=pad;
				}
				sb.append(start);
				while (0!=sb.length()%TAB_SIZE) {
					sb.append(pad);
				}
			}
			else if (SPACE==cc) {
				sb.append(visibleWhitespaces?VISIBLE_SPACE:cc);
			}
			else {
				sb.append(cc);
			}
		}
		if (visibleWhitespaces) {
			sb.append(EOL);
		}
		layout(sb.toString(), layout, charsPerLine);
	}

	protected abstract void layout(String line, List<String> lines,
			int charsPerLine);
}
