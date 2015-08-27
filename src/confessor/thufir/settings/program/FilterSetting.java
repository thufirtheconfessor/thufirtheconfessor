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

import android.content.Context;
import confessor.thufir.CameraHolder;
import confessor.thufir.image.Max3x3Filter;
import confessor.thufir.image.Max3x5Filter;
import confessor.thufir.image.Max5x5Filter;
import confessor.thufir.image.Min3x3Filter;
import confessor.thufir.image.Min3x5Filter;
import confessor.thufir.image.Min5x5Filter;
import confessor.thufir.lib.image.Filter;
import confessor.thufir.lib.image.NoFilter;
import confessor.thufir.settings.SettingCategory;
import confessor.thufir.settings.Settings;
import confessor.thufir.settings.SettingsManager;
import confessor.thufir.settings.TypedListSetting;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FilterSetting extends TypedListSetting<Filter> {
	public static final String KEY="filter";
	
	public final List<Filter> values;
	
	public FilterSetting(Context context) {
		super(KEY, Filter.class);
		values=Collections.unmodifiableList(
			Arrays.asList(
					new Max3x3Filter(context),
					new Max3x5Filter(context),
					new Max5x5Filter(context),
					new Min3x3Filter(context),
					new Min3x5Filter(context),
					new Min5x5Filter(context),
					NoFilter.INSTANCE));
	}

	@Override
	public SettingCategory category() {
		return SettingCategory.PROGRAM;
	}

	@Override
	protected Filter defaultValueTypedImpl(CameraHolder cameraHolder,
			Settings settings, SettingsManager settingsManager) {
		return NoFilter.INSTANCE;
	}

	@Override
	public boolean reloadCamera() {
		return false;
	}

	@Override
	public String title() {
		return "Filter";
	}

	@Override
	protected List<Filter> values(CameraHolder cameraHolder, Settings settings,
			SettingsManager settingsManager) {
		return values;
	}
}
