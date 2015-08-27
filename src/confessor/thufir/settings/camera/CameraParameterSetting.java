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
package confessor.thufir.settings.camera;

import android.hardware.Camera;
import confessor.thufir.settings.SettingCategory;
import confessor.thufir.settings.TypedListSetting;
import java.util.ArrayList;
import java.util.List;

public abstract class CameraParameterSetting<T> extends TypedListSetting<T> {
	public CameraParameterSetting(String key, Class<T> type) {
		super(key, type);
	}

	@Override
	public SettingCategory category() {
		return SettingCategory.CAMERA;
	}

	@Override
	public boolean reloadCamera() {
		return true;
	}
	
	public void set(Camera.Parameters parameters, Object value) {
		setTyped(parameters, cast(value));
	}
	
	public abstract void setTyped(Camera.Parameters parameters, T value);
	
	protected static List<String> values(String key,
			Camera.Parameters parameters) {
		String values=parameters.get(key);
		if (null==values) {
			return null;
		}
		List<String> result=new ArrayList<String>();
		while (true) {
			int index=values.indexOf(',');
			if (0>index) {
				break;
			}
			String next=values.substring(0, index).trim();
			values=values.substring(index+1);
			if (!next.isEmpty()) {
				result.add(next);
			}
		}
		values=values.trim();
		if (!values.isEmpty()) {
			result.add(values);
		}
		return result;
	}
}
