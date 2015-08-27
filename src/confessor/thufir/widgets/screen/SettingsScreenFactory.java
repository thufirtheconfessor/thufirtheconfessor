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
import confessor.thufir.settings.SettingCategory;
import java.util.List;

public class SettingsScreenFactory extends OverlayScreenFactory {
	public static final String TAG="settings";
	
	private final SettingCategory category;

	public SettingsScreenFactory(SettingCategory category, Env env,
			ScreenFactory parent, Integer yScroll) {
		super(env, parent, TAG, yScroll);
		this.category=category;
	}
	
	public static SettingsScreenFactory restore(List<String> bundle, Env env,
			ScreenFactory parent) throws Throwable {
		if (3>bundle.size()) {
			return null;
		}
		String categoryName=bundle.get(2);
		SettingCategory category=null;
		for (SettingCategory category2: SettingCategory.values()) {
			if (category2.name().equals(categoryName)) {
				category=category2;
				break;
			}
		}
		if (null==category) {
			return null;
		}
		return new SettingsScreenFactory(category, env, parent,
				restoreYScroll(bundle, 1));
	}

	@Override
	public boolean save(List<String> bundle) throws Throwable {
		boolean result=super.save(bundle);
		if (result) {
			bundle.add(category.toString());
		}
		return result;
	}

	@Override
	public ScreenView screenView(ScreenView currentView) throws Throwable {
		return new SettingsScreenView(category, env, this, yScroll);
	}

	@Override
	public OverlayScreenFactory yScroll(Integer yScroll) {
		return new SettingsScreenFactory(category, env, parent, yScroll);
	}
}
