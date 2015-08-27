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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Settings {
	public final Map<Setting, Object> settingValues;

	public Settings(Map<Setting, Object> settingValues) {
		this.settingValues=settingValues;
	}
	
	public Object setting(Setting setting) {
		return settingValues.get(setting);
	}
	
	public Settings setting(Setting setting, Object value) {
		Map<Setting, Object> settingValues2
				=new HashMap<Setting, Object>(settingValues);
		settingValues2.put(setting, value);
		return new Settings(Collections.unmodifiableMap(settingValues2));
	}
}
