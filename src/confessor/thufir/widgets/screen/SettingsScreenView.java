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

import android.view.View;
import confessor.thufir.Env;
import confessor.thufir.lib.stream.ImageStream;
import confessor.thufir.settings.Setting;
import confessor.thufir.settings.SettingCategory;
import confessor.thufir.settings.Settings;
import java.util.ArrayList;
import java.util.List;

public class SettingsScreenView extends OverlayScreenView {
	private class SettingButtonListener extends FactoryButtonListener {
		private final Setting setting;

		public SettingButtonListener(Env env, Setting setting,
				EnvScreenView view) {
			super(env, view);
			this.setting=setting;
		}

		@Override
		protected ScreenFactory factory() throws Throwable {
			return new SettingScreenFactory(env, push(), setting, null);
		}
	}
	
	public SettingsScreenView(SettingCategory category, Env env,
			OverlayScreenFactory parent, Integer yScroll) throws Throwable {
		super(env, parent, yScroll);
		List<Setting> settings=env.settingsManager().settingsList(category);
		List<View> buttons=new ArrayList<View>(settings.size()+1);
		buttons.add(backButton());
		Settings settings2=env.settingsManager().get();
		for (Setting setting: settings) {
			if (setting.valid(env.cameraHolder(), settings2,
					env.settingsManager())) {
				buttons.add(button(setting.title(),
						new SettingButtonListener(env, setting, this)));
			}
		}
		setButtons(buttons);
	}

	@Override
	public ImageStream imageStream() {
		return null;
	}
}
