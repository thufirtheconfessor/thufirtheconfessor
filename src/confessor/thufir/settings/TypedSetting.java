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

import android.content.SharedPreferences;
import confessor.thufir.CameraHolder;

public abstract class TypedSetting<T> extends AbstractSetting {
	public final Class<T> type;

	public TypedSetting(Class<T> type) {
		this.type=type;
	}
	
	public T cast(Object value) {
		return type.cast(value);
	}

	@Override
	public Object defaultValue(CameraHolder cameraHolder, Settings settings,
			SettingsManager settingsManager) {
		return defaultValueTyped(cameraHolder, settings, settingsManager);
	}
	
	public abstract T defaultValueTyped(CameraHolder cameraHolder,
			Settings settings, SettingsManager settingsManager);

	@Override
	public Object get(Settings settings) {
		return getTyped(settings);
	}
	
	public T getTyped(Settings settings) {
		return cast(settings.settingValues.get(this));
	}

	@Override
	public Object get(SettingsManager settingsManager) {
		return getTyped(settingsManager);
	}
	
	public T getTyped(SettingsManager settingsManager) {
		return getTyped(settingsManager.get());
	}

	@Override
	public Object get(CameraHolder cameraHolder, SettingsMap map,
			Settings settings, SettingsManager settingsManager) {
		return getTyped(cameraHolder, map, settings, settingsManager);
	}
	
	public abstract T getTyped(CameraHolder cameraHolder, SettingsMap map,
			Settings settings, SettingsManager settingsManager);

	@Override
	public void put(SharedPreferences.Editor editor, Object value) {
		putTyped(editor, type.cast(value));
	}
	
	public abstract void putTyped(SharedPreferences.Editor editor, T value);

	@Override
	public String toString(Object value) {
		return toStringTyped(type.cast(value));
	}
	
	public String toStringTyped(T value) {
		return String.valueOf(value);
	}

	@Override
	public boolean valid(CameraHolder cameraHolder, Settings settings,
			SettingsManager settingsManager, Object value) {
		if (!valid(cameraHolder, settings, settingsManager)) {
			return false;
		}
		return validTyped(cameraHolder, settings, settingsManager,
				type.cast(value));
	}
	
	public abstract boolean validTyped(CameraHolder cameraHolder,
			Settings settings, SettingsManager settingsManager, T value);
}
