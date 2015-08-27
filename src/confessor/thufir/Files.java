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
package confessor.thufir;

import android.os.Environment;
import confessor.thufir.lib.typein.DirectoryType;
import confessor.thufir.lib.typein.Program;
import confessor.thufir.lib.typein.Type;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Files {
	private Files() {
	}
	
	public static File directory() {
		return new File(Environment.getExternalStorageDirectory(), "thufir");
	}
	
	public static File file(String name) {
		return new File(directory(), name);
	}
	
	public static File sources() {
		return new File(directory(), "sources");
	}
	
	public static List<Type> types() {
		List<Type> result=new ArrayList<Type>(Type.TYPES.size()+1);
		result.addAll(Type.TYPES);
		Type type=new DirectoryType(Files.sources(), "External sources",
				"external-sources");
		try {
			List<Program> programs=type.programs();
			if ((null!=programs)
					&& (0<programs.size())) {
				result.add(type);
			}
		}
		catch (Throwable throwable) {
			AndroidErrorHandler.d(throwable);
		}
		return result;
	}
}
