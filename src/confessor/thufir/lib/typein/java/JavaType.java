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
package confessor.thufir.lib.typein.java;

import confessor.thufir.lib.typein.ListType;
import confessor.thufir.lib.typein.ResourceProgram;
import confessor.thufir.lib.typein.TrimNewlineLineBytes;

public class JavaType extends ListType {
	private static final String TYPE="java";
	
	public JavaType() {
		super(new TrimNewlineLineBytes(), "Java", TYPE,
				new ResourceProgram("AnsiStdOut.java", TYPE),
				new ResourceProgram("Awt.java", TYPE),
				new ResourceProgram("Crc.java", TYPE),
				new ResourceProgram("StdOut.java", TYPE));
	}
}
