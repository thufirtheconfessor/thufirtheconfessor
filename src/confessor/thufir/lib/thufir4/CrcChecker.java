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
package confessor.thufir.lib.thufir4;

import confessor.thufir.lib.Crc32;

public final class CrcChecker {
	private boolean hasCrc;
	private int lastCrc;
	private int savedCrc;
	
	public boolean check(byte[] buf, int offset) {
		int crc0=(buf[offset+28]&0xff)
				|((buf[offset+29]&0xff)<<8)
				|((buf[offset+30]&0xff)<<16)
				|((buf[offset+31]&0xff)<<24);
		if (crc0!=Crc32.updateCrc(lastCrc, buf, offset, 28)) {
			return false;
		}
		lastCrc=crc0;
		return true;
	}
	
	public int crc() {
		return lastCrc;
	}
	
	public void restore(int crc) {
		if (hasCrc) {
			lastCrc=savedCrc;
		}
		else {
			lastCrc=crc;
		}
	}
	
	public void restore(int dataHeight, int dataWidth) {
		restore((dataHeight<<16)|dataWidth);
	}
	
	public void save() {
		hasCrc=true;
		savedCrc=lastCrc;
	}
	
	public void setState(int crc) {
		hasCrc=false;
		restore(crc);
		save();
	}
}
