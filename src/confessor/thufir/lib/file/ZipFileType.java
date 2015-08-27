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

import java.io.InputStream;
import java.util.zip.ZipInputStream;

public class ZipFileType extends AbstractFileType<ZipInputStream> {
	@Override
	protected Boolean checkIntegrity(byte[] buf, ZipInputStream inputStream)
			throws Throwable {
		int rr=inputStream.read(buf);
		if (0<=rr) {
			return null;
		}
		if (null==inputStream.getNextEntry()) {
			return Boolean.TRUE;
		}
		return null;
	}

	@Override
	protected ZipInputStream checkIntegrityStream(InputStream inputStream)
			throws Throwable {
		return new ZipInputStream(inputStream);
	}

	@Override
	protected boolean detectType(InputStream inputStream) throws Throwable {
		if ((0x50!=inputStream.read())
			|| (0x4b!=inputStream.read())) {
			return false;
		}
		switch (inputStream.read()) {
			case 0x03:
				return 0x04==inputStream.read();
			case 0x05:
				return 0x06==inputStream.read();
			case 0x07:
				return 0x08==inputStream.read();
			default:
				return false;
		}
	}

	@Override
	public String name() {
		return "zip";
	}
}
