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
import java.util.zip.GZIPInputStream;

public class GZipFileType extends AbstractFileType<GZIPInputStream> {
	@Override
	protected Boolean checkIntegrity(byte[] buf, GZIPInputStream inputStream)
			throws Throwable {
		int rr=inputStream.read(buf);
		if (0>rr) {
			return Boolean.TRUE;
		}
		return null;
	}

	@Override
	protected GZIPInputStream checkIntegrityStream(InputStream inputStream)
			throws Throwable {
		return new GZIPInputStream(inputStream);
	}

	@Override
	protected boolean detectType(InputStream inputStream) throws Throwable {
		return (0x1f==inputStream.read())
				&& (0x8b==inputStream.read());
	}

	@Override
	public String name() {
		return "gzip";
	}
}
