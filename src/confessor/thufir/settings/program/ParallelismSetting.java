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

import confessor.thufir.CameraHolder;
import confessor.thufir.settings.SettingCategory;
import confessor.thufir.settings.Settings;
import confessor.thufir.settings.SettingsManager;
import confessor.thufir.settings.TypedListSetting;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ParallelismSetting extends TypedListSetting<Integer> {
	private final static List<Integer> VALUES=Collections.unmodifiableList(
			Arrays.asList(1, 2, 4, 8, 16));
	
	public ParallelismSetting() {
		super("parallelism", Integer.class);
	}

	@Override
	public SettingCategory category() {
		return SettingCategory.PROGRAM;
	}

	@Override
	protected Integer defaultValueTypedImpl(CameraHolder cameraHolder,
			Settings settings, SettingsManager settingsManager) {
		/*for (int ii=VALUES.size()-1; 0<=ii; --ii) {
			Integer value=VALUES.get(ii);
			if (!suspicious(value)) {
				return value;
			}
		}*/
		return VALUES.get(0);
	}

	@Override
	public boolean reloadCamera() {
		return false;
	}

	@Override
	protected boolean suspicious(Integer value) {
		return (1<value)
				&& (value>Runtime.getRuntime().availableProcessors());
	}

	@Override
	public String title() {
		return "Parallelism";
	}

	@Override
	protected List<Integer> values(CameraHolder cameraHolder,
			Settings settings, SettingsManager settingsManager) {
		return VALUES;
	}
}
