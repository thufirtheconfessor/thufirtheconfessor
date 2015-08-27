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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ListType implements Type {
	private final LineBytes bytes;
	private final String displayName;
	private final String name;
	private final List<Program> programs;

	public ListType(LineBytes bytes, String displayName, String name,
			Program... programs) {
		this.bytes=bytes;
		this.displayName=displayName;
		this.name=name;
		this.programs=Collections.unmodifiableList(Arrays.asList(programs));
	}

	@Override
	public String displayName() {
		return displayName;
	}

	@Override
	public boolean isSaved(String[] saved) {
		return (2==saved.length)
				&& name.equals(saved[0]);
	}

	@Override
	public LineBytes lineBytes() {
		return bytes;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public List<Program> programs() throws Throwable {
		return programs;
	}

	@Override
	public Program restore(String[] saved) throws Throwable {
		if (isSaved(saved)) {
			for (Program program: programs()) {
				if (program.name().equals(saved[1])) {
					return program;
				}
			}
		}
		return null;
	}

	@Override
	public String[] save(Program program) {
		return new String[]{name, program.name()};
	}
}
