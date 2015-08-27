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

import java.io.InputStream;

public class ResourceProgram implements Program {
	private static final String PACKAGE='/'+ResourceProgram.class.getPackage()
			.getName().replaceAll("\\.", "/")+'/';
	
	private final String name;
	private final String resource;

	public ResourceProgram(String name, String type) {
		this.name=name;
		this.resource=PACKAGE+type+'/'+name+".source";
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public InputStream source() {
		InputStream inputStream=getClass().getResourceAsStream(resource);
		if (null==inputStream) {
			throw new NullPointerException(String.format(
					"missing resource %1$s", resource));
		}
		return inputStream;
	}
}
