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

import confessor.thufir.lib.typein.excel.ExcelType;
import confessor.thufir.lib.typein.html5.HTML5Type;
import confessor.thufir.lib.typein.java.JavaType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface Type {
	List<Type> TYPES=Collections.unmodifiableList(Arrays.asList((Type)
			new ExcelType(), new HTML5Type(), new JavaType()));
	
	String displayName();
	boolean isSaved(String[] saved);
	LineBytes lineBytes();
	String name();
	List<Program> programs() throws Throwable;
	Program restore(String[] saved) throws Throwable;
	String[] save(Program program);
}
