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

import confessor.thufir.Env;
import confessor.thufir.settings.Setting;
import java.util.List;

public class SettingScreenFactory extends OverlayScreenFactory {
	public static final String TAG="setting";
	
	private final Setting setting;

	public SettingScreenFactory(Env env, ScreenFactory parent, Setting setting,
			Integer yScroll) {
		super(env, parent, TAG, yScroll);
		this.setting=setting;
	}
	
	public static SettingScreenFactory restore(List<String> bundle, Env env,
			ScreenFactory parent) throws Throwable {
		if (3>bundle.size()) {
			return null;
		}
		String settingTitle=bundle.get(2);
		Setting setting=null;
		for (Setting setting2: env.settingsManager().settingsList()) {
			if (setting2.title().equals(settingTitle)) {
				setting=setting2;
				break;
			}
		}
		if (null==setting) {
			return null;
		}
		return new SettingScreenFactory(env, parent, setting,
				restoreYScroll(bundle, 1));
	}

	@Override
	public boolean save(List<String> bundle) throws Throwable {
		boolean result=super.save(bundle);
		if (result) {
			bundle.add(setting.title());
		}
		return result;
	}

	public ScreenView screenView(ScreenView currentView) throws Throwable {
		return setting.screenView(env, this, yScroll);
	}

	@Override
	public OverlayScreenFactory yScroll(Integer yScroll) {
		return new SettingScreenFactory(env, parent, setting, yScroll);
	}
}
