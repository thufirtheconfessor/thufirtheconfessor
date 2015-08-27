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
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileSaver extends AbstractCloseable {
	private static final int PARTIAL_EXTRA_DATA=5;
	private static final String PARTIAL_FORMAT="%1$s.%2$016x.partial";
	private static final Pattern PARTIAL_PATTERN
			=Pattern.compile("(.*?)\\.([0-9a-f]{16})\\.partial");
	
	private RandomAccessFile file;
	private boolean lastClock;
	private int lastCrc;
	private final File partialFile;
	private final File resultFile;
	private long saved;
	private final byte[] scratch=new byte[5];
	private final long size;

	public FileSaver(File partialFile, File resultFile, long size) {
		this.partialFile=partialFile;
		this.resultFile=resultFile;
		this.size=size;
	}

	@Override
	protected void closeImpl() throws Throwable {
		if (null!=file) {
			try {
				file.close();
			}
			finally {
				file=null;
			}
		}
	}
	
	public static FileSaver continuePartialDirectory(File directory)
			throws Throwable {
		File[] files=directory.listFiles();
		if (null==files) {
			return null;
		}
		for (File file: directory.listFiles()) {
			FileSaver fileSaver=continuePartialFile(file);
			if (null!=fileSaver) {
				return fileSaver;
			}
		}
		return null;
	}
	
	public static FileSaver continuePartialFile(File partialFile)
			throws Throwable {
		if ((!partialFile.isFile())
				|| (PARTIAL_EXTRA_DATA>partialFile.length())) {
			return null;
		}
		Matcher matcher=PARTIAL_PATTERN.matcher(partialFile.getName());
		if (!matcher.matches()) {
			return null;
		}
		File resultFile=new File(partialFile.getParentFile(),
				matcher.group(1));
		if (resultFile.exists()) {
			return null;
		}
		long size;
		try {
			size=Long.parseLong(matcher.group(2), 16);
		}
		catch (NumberFormatException ex) {
			return null;
		}
		if (0>=size) {
			return null;
		}
		boolean success=false;
		FileSaver fileSaver=new FileSaver(partialFile, resultFile, size);
		try {
			fileSaver.file=new RandomAccessFile(partialFile, "rw");
			fileSaver.saved=fileSaver.file.length()-PARTIAL_EXTRA_DATA;
			fileSaver.file.seek(fileSaver.saved);
			fileSaver.lastClock=fileSaver.file.readBoolean();
			fileSaver.lastCrc=fileSaver.file.read()<<24;
			fileSaver.lastCrc|=fileSaver.file.read()<<16;
			fileSaver.lastCrc|=fileSaver.file.read()<<8;
			fileSaver.lastCrc|=fileSaver.file.read();
			fileSaver.file.close();
			fileSaver.file=null;
			success=true;
			return fileSaver;
		}
		finally {
			if (!success) {
				fileSaver.close();
			}
		}
	}
	
	public static FileSaver create(File directory, String name, long size)
			throws Throwable {
		String name2=name;
		for (int ii=1; ; ++ii,name2=name+'-'+ii) {
			File resultFile=new File(directory, name2);
			if (resultFile.exists()) {
				continue;
			}
			File partialFile=new File(directory,
					String.format(PARTIAL_FORMAT, name2, size));
			if (partialFile.exists()) {
				continue;
			}
			boolean success=false;
			FileSaver fileSaver=new FileSaver(partialFile, resultFile, size);
			try {
				if (0>=size) {
					fileSaver.file=new RandomAccessFile(resultFile, "rw");
					fileSaver.file.setLength(0);
					fileSaver.file.close();
					fileSaver.file=null;
				}
				success=true;
				return fileSaver;
			}
			finally {
				if (!success) {
					fileSaver.close();
				}
			}
		}
	}
	
	public static void delete(File file) throws IOException {
		if (file.exists()
				&& (!file.delete())) {
			throw new IOException(String.format("couldn't delete %1$s", file));
		}
	}

	public boolean lastClock() {
		return lastClock;
	}
	
	public int lastCrc() {
		return lastCrc;
	}
	
	public File partialFile() {
		return partialFile;
	}
	
	public static void rename(File from, File to) throws IOException {
		if (!from.renameTo(to)) {
			throw new IOException(String.format(
					"cannot rename %1$s to %2$s",
					from.getPath(), to.getPath()));
		}
	}
	
	public File resultFile() {
		return resultFile;
	}
	
	public void save(boolean clock, int crc, byte[] buf, int offset,
			int length) throws IOException {
		checkClosed();
		if (size<=saved) {
			return;
		}
		length=(int)Math.min(length, size-saved);
		if (null==file) {
			file=new RandomAccessFile(partialFile, "rw");
		}
		file.seek(saved);
		file.write(buf, offset, length);
		scratch[0]=(byte)(clock?1:0);
		scratch[1]=(byte)((crc>>24)&0xff);
		scratch[2]=(byte)((crc>>16)&0xff);
		scratch[3]=(byte)((crc>>8)&0xff);
		scratch[4]=(byte)(crc&0xff);
		file.write(scratch, 0, 5);
		file.setLength(file.getFilePointer());
		saved+=length;
		lastClock=clock;
		lastCrc=crc;
		if (size<=saved) {
			file.setLength(size);
			file.close();
			rename(partialFile, resultFile);
		}
	}
	
	public long saved() {
		return saved;
	}
	
	public long size() {
		return size;
	}
}
