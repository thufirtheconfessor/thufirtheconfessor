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
package confessor.thufir.widgets.screen;

import java.util.List;

public class Message {
	public final boolean error;
	public final String message;

	public Message(boolean error, String message) {
		this.error=error;
		this.message=message;
	}
	
	public static Message restore(List<String> bundle, int index)
			throws Throwable {
		if (bundle.size()<=index+1) {
			return null;
		}
		boolean error=Boolean.parseBoolean(bundle.get(index));
		String message=bundle.get(index+1);
		return new Message(error, message);
	}
	
	public void save(List<String> bundle) {
		bundle.add(Boolean.toString(error));
		bundle.add(message);
	}
}
