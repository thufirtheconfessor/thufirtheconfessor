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
package confessor.thufir.settings;

import java.util.Map;

public class SettingsMap {
	private final Map<String, ?> map;

	public SettingsMap(Map<String, ?> map) {
		this.map=map;
	}
	
	public boolean getBoolean(boolean defaultValue, String key) {
		Object object=map.get(key);
		if (object instanceof Boolean) {
			return (Boolean)object;
		}
		return defaultValue;
	}
	
	public byte getByte(byte defaultValue, String key) {
		int value=getInt(defaultValue&0xff, key);
		if (0>value) {
			value=0;
		}
		else if (255<value) {
			value=255;
		}
		return (byte)value;
	}
	
	public int getInt(int defaultValue, String key) {
		Object object=map.get(key);
		if (object instanceof Integer) {
			return (Integer)object;
		}
		return defaultValue;
	}
	
	public String getString(String defaultValue, String key) {
		Object object=map.get(key);
		if (object instanceof String) {
			return (String)object;
		}
		return defaultValue;
	}
}
