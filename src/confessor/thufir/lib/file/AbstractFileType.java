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

import confessor.thufir.lib.AbstractCloseable;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public abstract class AbstractFileType<I extends InputStream>
		implements FileType {
	private class StreamIntegrityChecker extends AbstractCloseable
			implements IntegrityChecker {
		private final byte[] buf=new byte[8192];
		private final InputStream bufferedStream;
		private final InputStream fileStream;
		private final I inputStream;

		public StreamIntegrityChecker(File file) throws Throwable {
			boolean success=false;
			fileStream=new FileInputStream(file);
			try {
				bufferedStream=new BufferedInputStream(fileStream);
				try {
					inputStream=checkIntegrityStream(bufferedStream);
					success=true;
				}
				finally {
					if (!success) {
						bufferedStream.close();
					}
				}
			}
			finally {
				if (!success) {
					fileStream.close();
				}
			}
		}

		@Override
		public Boolean check() throws Throwable {
			return checkIntegrity(buf, inputStream);
		}

		@Override
		protected void closeImpl() throws Throwable {
			try {
				try {
					inputStream.close();
				}
				finally {
					bufferedStream.close();
				}
			}
			finally {
				fileStream.close();
			}
		}
	}
	
	@Override
	public IntegrityChecker checkIntegrity(File file) throws Throwable {
		return new StreamIntegrityChecker(file);
	}
	
	protected abstract Boolean checkIntegrity(byte[] buf, I inputStream)
			throws Throwable;

	protected abstract I checkIntegrityStream(InputStream inputStream)
			throws Throwable;

	@Override
	public boolean detectType(File file) throws Throwable {
		InputStream fileStream=new FileInputStream(file);
		try {
			InputStream bufferedStream=new BufferedInputStream(fileStream);
			try {
				return detectType(bufferedStream);
			}
			finally {
				bufferedStream.close();
			}
		}
		finally {
			fileStream.close();
		}
	}
	
	protected abstract boolean detectType(InputStream inputStream)
			throws Throwable;
}
