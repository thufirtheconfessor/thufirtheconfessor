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

import android.content.SharedPreferences;
import confessor.thufir.CameraHolder;
import confessor.thufir.Env;
import confessor.thufir.lib.color.BrightnessClassifier;
import confessor.thufir.lib.stream.ImageStream;
import confessor.thufir.settings.SettingCategory;
import confessor.thufir.settings.Settings;
import confessor.thufir.settings.SettingsManager;
import confessor.thufir.settings.SettingsMap;
import confessor.thufir.settings.TypedSetting;
import confessor.thufir.widgets.IntSpinner;
import confessor.thufir.widgets.screen.OverlayScreenFactory;
import confessor.thufir.widgets.screen.OverlayScreenView;
import confessor.thufir.widgets.screen.ScreenView;

public class ThresholdSetting extends TypedSetting<Integer> {
	private static final String KEY="threshold";
	
	private class ScreenViewImpl extends OverlayScreenView
			implements IntSpinner.Listener {
		public ScreenViewImpl(Env env, OverlayScreenFactory parent,
				Integer yScroll) throws Throwable {
			super(env, parent, yScroll);
			
			IntSpinner spinner=new IntSpinner(env.context(), env.handler(),
					this);
			spinner.setText(Integer.toString(getTyped(env.settingsManager())));
			spinner.setWidth("255");
			
			setButtons(
					backButton(),
					textView(title()),
					spinner);
		}

		@Override
		public void changed(int delta, IntSpinner spinner) {
			Settings settings=env.settingsManager().get();
			int threshold2=getTyped(settings);
			threshold2=changeThreshold(delta, threshold2);
			settings=settings.setting(ThresholdSetting.this, threshold2);
			settings=env.settingsManager().set(settings);
			threshold2=getTyped(settings);
			spinner.setText(Integer.toString(threshold2));
		}

		@Override
		public ImageStream imageStream() {
			return null;
		}
	}

	public ThresholdSetting() {
		super(Integer.class);
	}

	@Override
	public SettingCategory category() {
		return SettingCategory.PROGRAM;
	}
	
	public int changeThreshold(int delta, int value) {
		value+=delta;
		if (0>=value) {
			return 1;
		}
		if (256<=value) {
			return 255;
		}
		return value;
	}

	@Override
	public Integer defaultValueTyped(CameraHolder cameraHolder,
			Settings settings, SettingsManager settingsManager) {
		return BrightnessClassifier.DEFAULT_TRESHOLD;
	}

	@Override
	public Integer getTyped(CameraHolder cameraHolder, SettingsMap map,
			Settings settings, SettingsManager settingsManager) {
		return map.getInt(BrightnessClassifier.DEFAULT_TRESHOLD, KEY);
	}

	@Override
	public void putTyped(SharedPreferences.Editor editor, Integer value) {
		editor.putInt(KEY, value);
	}

	@Override
	public boolean reloadCamera() {
		return false;
	}

	@Override
	public ScreenView screenView(Env env, OverlayScreenFactory parent,
			Integer yScroll) throws Throwable {
		return new ScreenViewImpl(env, parent, yScroll);
	}

	@Override
	public String title() {
		return "Threshold";
	}

	@Override
	public boolean valid(CameraHolder cameraHolder, Settings settings,
			SettingsManager settingsManager) {
		return true;
	}

	@Override
	public boolean validTyped(CameraHolder cameraHolder, Settings settings,
			SettingsManager settingsManager, Integer value) {
		return (0<value)
				&& (256>value);
	}
}
