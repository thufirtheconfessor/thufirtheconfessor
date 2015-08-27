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

import java.util.ArrayList;
import java.util.List;

public class TextLayoutCache {
	private int charsPerLine;
	public final List<List<String>> layout
			=new ArrayList<List<String>>();
	private TextLayout textLayout;
	private boolean visibleWhitespace;

	public void layout(TextLayout textLayout, List<String> lines,
			int charsPerLine, boolean visibleWhitespace) {
		int size=lines.size();
		if ((this.charsPerLine==charsPerLine)
				&& (this.textLayout==textLayout)
				&& (this.layout.size()==size)
				&& (this.visibleWhitespace==visibleWhitespace)) {
			return;
		}
		this.charsPerLine=charsPerLine;
		this.textLayout=textLayout;
		this.visibleWhitespace=visibleWhitespace;
		for (int ii=0; size>ii; ++ii) {
			List<String> lineLayout;
			if (this.layout.size()>ii) {
				lineLayout=this.layout.get(ii);
				lineLayout.clear();
			}
			else {
				lineLayout=new ArrayList<String>();
			}
			textLayout.layout(lines.get(ii), lineLayout, charsPerLine,
					visibleWhitespace);
			if (this.layout.size()<=ii) {
				this.layout.add(lineLayout);
			}
		}
		while (this.layout.size()>size) {
			this.layout.remove(this.layout.size()-1);
		}
	}
}
