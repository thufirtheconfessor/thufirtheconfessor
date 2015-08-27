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
package confessor.thufir.settings.camera;

import android.hardware.Camera;
import confessor.thufir.CameraHolder;
import confessor.thufir.settings.Settings;
import confessor.thufir.settings.SettingsManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class SizeSetting extends CameraParameterSetting<Size> {
	protected final String title;

	public SizeSetting(String key, String title) {
		super(key, Size.class);
		this.title=title;
	}
	
	protected abstract Camera.Size cameraSize(Camera.Parameters parameters);
	
	protected abstract List<Camera.Size> cameraSizes(
			Camera.Parameters parameters);

	@Override
	protected Size defaultValueTypedImpl(CameraHolder cameraHolder,
			Settings settings, SettingsManager settingsManager) {
		Size size=null;
		for (Size size2: values(cameraHolder, settings, settingsManager)) {
			if ((!suspicious(size2))
					&& ((null==size)
						|| (0>size.compareTo(size2)))) {
				size=size2;
			}
		}
		if (null!=size) {
			return size;
		}
		return new Size(cameraSize(
				cameraHolder.cameraParameters(settings, settingsManager)));
	}

	@Override
	protected boolean suspicious(Size value) {
		return Runtime.getRuntime().maxMemory()<=20*value.height*value.width;
	}

	@Override
	public String title() {
		return title;
	}

	@Override
	protected List<Size> values(CameraHolder cameraHolder, Settings settings,
			SettingsManager settingsManager) {
		List<Camera.Size> cameraSizes=cameraSizes(
				cameraHolder.cameraParameters(settings, settingsManager));
		if (null==cameraSizes) {
			return null;
		}
		List<Size> result=new ArrayList<Size>(cameraSizes.size());
		for (Camera.Size size: cameraSizes) {
			result.add(new Size(size));
		}
		Collections.sort(result);
		return result;
	}
}
