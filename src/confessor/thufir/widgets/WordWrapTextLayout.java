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

public class WordWrapTextLayout extends WhitespaceTextLayout {
	private static boolean isWhitespace(String string, int index) {
		char cc=string.charAt(index);
		return (EOL==cc)
				|| (SPACE==cc)
				|| (TAB_PAD==cc)
				|| (TAB_START==cc)
				|| (VISIBLE_SPACE==cc);
	}
	
	private static boolean isWord(String string, int index) {
		char cc=string.charAt(index);
		return (('A'<=cc)
					&& ('Z'>=cc))
				|| (('a'<=cc)
					&& ('z'>=cc))
				|| (('0'<=cc)
					&& ('9'>=cc))
				|| ('_'==cc)
				|| ('\''==cc)
				|| ('"'==cc);
	}

	@Override
	protected void layout(String line, List<String> lines, int charsPerLine) {
		if (charsPerLine>=line.length()) {
			lines.add(line);
			return;
		}
		for (int index=0; line.length()>index; ) {
			int index2=index;
			while (line.length()>index2) {
				int l0=whitespaces(line, index2);
				l0+=nonWhitespaces(line, index2+l0);
				int l1=whitespaces(line, index2+l0);
				if (0<nonWhitespaces(line, index2+l0+l1)) {
					l1=0;
				}
				if ((index!=index2)
						&& (l0+l1+index2-index>charsPerLine)) {
					break;
				}
				index2+=l0+l1;
			}
			layoutIdentifier(line.substring(index, index2), lines,
					charsPerLine);
			index=index2;
		}
	}
	
	private void layoutIdentifier(String line, List<String> lines,
			int charsPerLine) {
		if (charsPerLine>=line.length()) {
			lines.add(line);
			return;
		}
		for (int index=0; line.length()>index; ) {
			int index2=index;
			while (line.length()>index2) {
				int ll=word(line, index2);
				if (0>=ll) {
					ll=1;
				}
				if ((index!=index2)
						&& (ll+index2-index>charsPerLine)) {
					break;
				}
				index2+=ll;
			}
			LineWrapTextLayout.lineWrap(line.substring(index, index2), lines,
					charsPerLine);
			index=index2;
		}
	}
	
	private static int nonWhitespaces(String string, int index) {
		int index2=index;
		while ((string.length()>index2)
				&& (!isWhitespace(string, index2))) {
			++index2;
		}
		return index2-index;
	}

	@Override
	public String toString() {
		return "Word wrap";
	}
	
	private static int whitespaces(String string, int index) {
		int index2=index;
		while ((string.length()>index2)
				&& isWhitespace(string, index2)) {
			++index2;
		}
		return index2-index;
	}
	
	private static int word(String string, int index) {
		int index2=index;
		while ((string.length()>index2)
				&& isWord(string, index2)) {
			++index2;
		}
		return index2-index;
	}
}
