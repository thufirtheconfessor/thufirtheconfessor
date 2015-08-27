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
package confessor.thufir.lib;

/*copied from rfc1952*/
public class Crc32 {
	private static final int[] crcTable=new int[256];

	static {
		for (int nn=0; 256>nn; ++nn) {
			int cc=nn;
			for (int kk=0; 8>kk; ++kk) {
				if (0==(cc&1)) {
					cc=(cc>>1)&0x7fffffff;
				}
				else {
					cc=0xedb88320^((cc>>1)&0x7fffffff);
				}
			}
			crcTable[nn]=cc;
		}
	}

	private Crc32() {
	}

	public static int crc(byte[] buf, int offset, int length) {
		return updateCrc(0, buf, offset, length);
	}

	public static int crc(byte[] buf) {
		return crc(buf, 0, buf.length);
	}

	/*
	 Update a running crc with the bytes buf[0..len-1] and return
	 the updated crc. The crc should be initialized to zero. Pre- and
	 post-conditioning (one's complement) is performed within this
	 function so it shouldn't be done by the caller. Usage example:

	 unsigned long crc = 0L;

	 while (read_buffer(buffer, length) != EOF) {
	 crc = update_crc(crc, buffer, length);
	 }
	 if (crc != original_crc) error();
	 */
	public static int updateCrc(int crc, byte[] buf, int offset, int length) {
		int cc=crc^0xffffffff;
		for (; 0<length; --length, ++offset) {
			cc=crcTable[(cc^buf[offset])&0xff]^((cc>>8)&0x00ffffff);
		}
		return cc^0xffffffff;
	}

	public static int updateCrc(int crc, byte[] buf) {
		return updateCrc(crc, buf, 0, buf.length);
	}
}
