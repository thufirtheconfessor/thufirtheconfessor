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
package confessor.thufir.settings.typein;

import android.content.SharedPreferences;
import confessor.thufir.CameraHolder;
import confessor.thufir.Env;
import confessor.thufir.settings.SettingCategory;
import confessor.thufir.settings.Settings;
import confessor.thufir.settings.SettingsManager;
import confessor.thufir.settings.SettingsMap;
import confessor.thufir.settings.TypedSetting;
import confessor.thufir.widgets.screen.OverlayScreenFactory;
import confessor.thufir.widgets.screen.ScreenView;

public class VisibleWhitespacesSetting extends TypedSetting<Boolean> {
	public static final boolean DEFAULT=false;
	public static final String KEY="visible-whitespaces";
	
	public VisibleWhitespacesSetting() {
		super(Boolean.class);
	}

	@Override
	public SettingCategory category() {
		return SettingCategory.TYPE_IN;
	}

	@Override
	public Boolean defaultValueTyped(CameraHolder cameraHolder,
			Settings settings, SettingsManager settingsManager) {
		return DEFAULT;
	}

	@Override
	public Boolean getTyped(CameraHolder cameraHolder, SettingsMap map,
			Settings settings, SettingsManager settingsManager) {
		return map.getBoolean(
				defaultValueTyped(cameraHolder, settings, settingsManager),
				KEY);
	}

	@Override
	public void putTyped(SharedPreferences.Editor editor, Boolean value) {
		editor.putBoolean(KEY, value);
	}

	@Override
	public boolean reloadCamera() {
		return false;
	}

	@Override
	public ScreenView screenView(Env env, OverlayScreenFactory parent,
			Integer yScroll) throws Throwable {
		throw new UnsupportedOperationException();
	}

	@Override
	public String title() {
		return "Visible whitespaces";
	}

	@Override
	public boolean valid(CameraHolder cameraHolder, Settings settings,
			SettingsManager settingsManager) {
		return true;
	}

	@Override
	public boolean validTyped(CameraHolder cameraHolder, Settings settings,
			SettingsManager settingsManager, Boolean value) {
		return true;
	}
}
