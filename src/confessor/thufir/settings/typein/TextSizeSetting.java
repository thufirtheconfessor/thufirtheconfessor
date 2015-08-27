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
import confessor.thufir.widgets.screen.OverlayScreenFactory;
import confessor.thufir.widgets.screen.ScreenView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextSizeSetting extends TypedListSetting<Integer> {
	public static final int DEFAULT=16;
	public static final String KEY="text-size";
	public static final Map<Integer, Integer> REVERSE_VALUES;
	public static final List<Integer> VALUES;
	static {
		List<Integer> values=new ArrayList<Integer>();
		for (int ii=4; 16>ii; ++ii) {
			values.add(ii);
		}
		for (int ii=16; 32>=ii; ii+=2) {
			values.add(ii);
		}
		VALUES=Collections.unmodifiableList(values);
		Map<Integer, Integer> reverseValues=new HashMap<Integer, Integer>(
				VALUES.size());
		for (int ii=VALUES.size()-1; 0<=ii; --ii) {
			reverseValues.put(VALUES.get(ii), ii);
		}
		REVERSE_VALUES=Collections.unmodifiableMap(reverseValues);
	}

	public TextSizeSetting() {
		super(KEY, Integer.class);
	}

	@Override
	public SettingCategory category() {
		return SettingCategory.TYPE_IN;
	}

	@Override
	protected Integer defaultValueTypedImpl(CameraHolder cameraHolder,
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
		return "Text size";
	}

	@Override
	protected List<Integer> values(CameraHolder cameraHolder,
			Settings settings, SettingsManager settingsManager) {
		return VALUES;
	}
}
