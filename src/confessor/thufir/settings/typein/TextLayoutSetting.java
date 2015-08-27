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

import confessor.thufir.CameraHolder;
import confessor.thufir.Env;
import confessor.thufir.settings.SettingCategory;
import confessor.thufir.settings.Settings;
import confessor.thufir.settings.SettingsManager;
import confessor.thufir.settings.TypedListSetting;
import confessor.thufir.widgets.TextLayout;
import confessor.thufir.widgets.screen.OverlayScreenFactory;
import confessor.thufir.widgets.screen.ScreenView;
import java.util.List;

public class TextLayoutSetting extends TypedListSetting<TextLayout> {
	public static final TextLayout DEFAULT=TextLayout.LINE_WRAP;
	public static final String KEY="text-layout";

	public TextLayoutSetting() {
		super(KEY, TextLayout.class);
	}

	@Override
	public SettingCategory category() {
		return SettingCategory.TYPE_IN;
	}

	@Override
	protected TextLayout defaultValueTypedImpl(CameraHolder cameraHolder,
			Settings settings, SettingsManager settingsManager) {
		return DEFAULT;
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
		return "Text layout";
	}

	@Override
	protected List<TextLayout> values(CameraHolder cameraHolder,
			Settings settings, SettingsManager settingsManager) {
		return TextLayout.VALUES;
	}
}
