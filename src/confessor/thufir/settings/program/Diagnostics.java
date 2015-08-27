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
package confessor.thufir.settings.program;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Diagnostics {
	public static final Diagnostics OFF=new Diagnostics("off", false);
	public static final Diagnostics ON=new Diagnostics("on", true);
	public static final List<Diagnostics> VALUES=Collections.unmodifiableList(
			Arrays.asList(OFF, ON));
	
	public final String name;
	public final boolean on;

	public Diagnostics(String name, boolean on) {
		this.name=name;
		this.on=on;
	}

	@Override
	public String toString() {
		return name;
	}
}
