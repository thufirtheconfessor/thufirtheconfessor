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
import confessor.thufir.lib.tracking.BlobSize;
import confessor.thufir.settings.SettingCategory;
import confessor.thufir.settings.Settings;
import confessor.thufir.settings.SettingsManager;
import confessor.thufir.settings.TypedListSetting;
import java.util.List;

public class BlobSizeSetting extends TypedListSetting<BlobSize> {
	private static final String KEY="blob-size";
	
	public BlobSizeSetting() {
		super(KEY, BlobSize.class);
	}

	@Override
	public SettingCategory category() {
		return SettingCategory.PROGRAM;
	}

	@Override
	protected BlobSize defaultValueTypedImpl(CameraHolder cameraHolder,
			Settings settings, SettingsManager settingsManager) {
		return BlobSize.PERIMETER;
	}

	@Override
	public boolean reloadCamera() {
		return false;
	}

	@Override
	public String title() {
		return "Blob size";
	}

	@Override
	protected List<BlobSize> values(CameraHolder cameraHolder,
			Settings settings, SettingsManager settingsManager) {
		return BlobSize.VALUES;
	}
}
