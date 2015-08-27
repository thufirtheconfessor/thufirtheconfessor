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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class OverlayButtonsPosition {
	public static final OverlayButtonsPosition BOTTOM_LEFT
			=new OverlayButtonsPosition(true, "Bottom-left", false);
	public static final OverlayButtonsPosition BOTTOM_RIGHT
			=new OverlayButtonsPosition(false, "Bottom-right", false);
	public static final OverlayButtonsPosition TOP_LEFT
			=new OverlayButtonsPosition(true, "Top-left", true);
	public static final OverlayButtonsPosition TOP_RIGHT
			=new OverlayButtonsPosition(false, "Top-right", true);
	public static final List<OverlayButtonsPosition> VALUES
			=Collections.unmodifiableList(Arrays.asList(
					BOTTOM_LEFT, BOTTOM_RIGHT, TOP_LEFT, TOP_RIGHT));
	
	private final boolean bottom;
	private final boolean left;
	private final String name;
	private final boolean right;
	private final boolean top;

	private OverlayButtonsPosition(boolean left, String name, boolean top) {
		this.left=left;
		this.name=name;
		this.top=top;
		bottom=!top;
		right=!left;
	}
	
	public boolean bottom() {
		return bottom;
	}
	
	public boolean left() {
		return left;
	}
	
	public boolean right() {
		return right;
	}
	
	public boolean top() {
		return top;
	}

	@Override
	public String toString() {
		return name;
	}
}
