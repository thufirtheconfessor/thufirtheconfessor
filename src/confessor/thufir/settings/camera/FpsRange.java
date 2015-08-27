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
package confessor.thufir.settings.camera;

public class FpsRange implements Comparable<FpsRange> {
	public final int max;
	public final int min;

	public FpsRange(int max, int min) {
		this.max=max;
		this.min=min;
	}
	
	public FpsRange(int[] range) {
		this(range[1], range[0]);
	}

	@Override
	public int compareTo(FpsRange fpsRange) {
		if (min>fpsRange.min) {
			return 1;
		}
		if (min<fpsRange.min) {
			return -1;
		}
		if (max>fpsRange.max) {
			return 1;
		}
		if (max<fpsRange.max) {
			return -1;
		}
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if ((null==obj)
				|| (!getClass().equals(obj.getClass()))) {
			return false;
		}
		FpsRange range=(FpsRange)obj;
		return (max==range.max)
				&& (min==range.min);
	}

	@Override
	public int hashCode() {
		return 41*max+min;
	}

	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		toString(sb, min);
		sb.append(" - ");
		toString(sb, max);
		return sb.toString();
	}
	
	private void toString(StringBuilder sb, int value) {
		sb.append(value/1000);
		value=value%1000;
		if (0>=value) {
			return;
		}
		sb.append('.');
		sb.append(value/100);
		value=value%100;
		if (0>=value) {
			return;
		}
		sb.append(value/10);
		value=value%10;
		if (0>=value) {
			return;
		}
		sb.append(value);
	}
}
