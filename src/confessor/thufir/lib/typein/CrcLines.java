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
package confessor.thufir.lib.typein;

import confessor.thufir.lib.Crc32;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public abstract class CrcLines {
	protected abstract void line(String crc, String line) throws Throwable;
	
	public void read(BufferedReader reader, LineBytes bytes) throws Throwable {
		int crc=0;
		for (String line; null!=(line=reader.readLine()); ) {
			byte[] buf;
			ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
			try {
				bytes.bytes(line, outputStream);
				buf=outputStream.toByteArray();
			}
			finally {
				outputStream.close();
			}
			crc=Crc32.updateCrc(crc, buf);
			line(toString(crc), line);
		}
	}
	
	public void read(Reader reader, LineBytes bytes) throws Throwable {
		BufferedReader bufferedReader=new BufferedReader(reader);
		try {
			read(bufferedReader, bytes);
		}
		finally {
			bufferedReader.close();
		}
	}
	
	public void read(InputStream inputStream, String charset, LineBytes bytes)
			throws Throwable {
		Reader reader=new InputStreamReader(inputStream, charset);
		try {
			read(reader, bytes);
		}
		finally {
			reader.close();
		}
	}
	
	public String toString(int crc) {
		return String.format("%1$08x", crc);
	}
}
