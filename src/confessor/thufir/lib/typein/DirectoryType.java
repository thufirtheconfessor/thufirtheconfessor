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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class DirectoryType implements Type {
	public static final long DEFAULT_MAX_SIZE=128*1024;
	public static final String TAG="file";
	
	private final File directory;
	private final String displayName;
	private final LineBytes lineBytes;
	private final long maxSize;
	private final String name;

	public DirectoryType(File directory, String displayName,
			LineBytes lineBytes, long maxSize, String name) {
		this.directory=directory;
		this.displayName=displayName;
		this.lineBytes=lineBytes;
		this.maxSize=maxSize;
		this.name=name;
	}

	public DirectoryType(File directory, String displayName, String name) {
		this(directory, displayName, new DefaultLineBytes(), DEFAULT_MAX_SIZE,
				name);
	}

	@Override
	public String displayName() {
		return displayName;
	}

	@Override
	public boolean isSaved(String[] saved) {
		return isSavedType(saved);
	}

	public static boolean isSavedType(String[] saved) {
		return (4==saved.length)
				&& (TAG.equals(saved[0]));
	}

	@Override
	public LineBytes lineBytes() {
		return lineBytes;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public List<Program> programs() throws Throwable {
		Set<File> files=new TreeSet<File>();
		if (directory.isDirectory()
				&& directory.canRead()) {
			File[] files2=directory.listFiles();
			if (null!=files2) {
				for (int ii=files2.length-1; 0<=ii; --ii) {
					File file=files2[ii];
					if (file.isFile()
							&& file.canRead()
							&& (maxSize>=file.length())) {
						files.add(file);
					}
				}
			}
		}
		List<Program> programs=new ArrayList<Program>(files.size());
		for (File file: files) {
			programs.add(new FileProgram(file));
		}
		return programs;
	}

	@Override
	public Program restore(String[] saved) {
		if (!isSaved(saved)) {
			return null;
		}
		return new FileProgram(new File(saved[3]));
	}
	
	public static DirectoryType restoreType(String[] saved) {
		if (!isSavedType(saved)) {
			return null;
		}
		return new DirectoryType(
				new File(saved[3]).getParentFile(),
				saved[1],
				saved[2]);
	}

	@Override
	public String[] save(Program program) {
		return new String[]{TAG, displayName, name,
			((FileProgram)program).file.getPath()};
	}
}
