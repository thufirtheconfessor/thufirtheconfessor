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
package confessor.thufir.lib.file;

public class Speed {
	private static final float FACTOR=0.1f;
	
	private boolean hasTime;
	private boolean hasLastTime;
	private int lastBytes;
	private long lastTime=Long.MIN_VALUE;
	private float time;
	
	protected long now() {
		return System.currentTimeMillis();
	}
	
	public float speedInBytesPerSec(int bytes) {
		long now=Math.max(lastTime, now());
		if (0<bytes) {
			lastBytes=bytes;
			if (!hasLastTime) {
				hasLastTime=true;
				lastTime=now;
				return 0.0f;
			}
			long diff=now-lastTime;
			lastTime=now;
			if (hasTime) {
				time=(1.0f-FACTOR)*time+FACTOR*diff;
			}
			else {
				hasTime=true;
				time=diff;
			}
			return 1000*lastBytes/time;
		}
		if (hasLastTime
				&& hasTime) {
			long diff=now-lastTime;
			if (diff<4*time) {
				return 1000*lastBytes/time;
			}
			hasLastTime=false;
			hasTime=false;
		}
		return 0.0f;
	}
}
