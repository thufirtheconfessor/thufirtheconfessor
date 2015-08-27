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
import java.util.List;

public abstract class MinMaxSetting extends CameraParameterSetting<Integer> {
	public MinMaxSetting(String key) {
		super(key, Integer.class);
	}
	
	protected abstract Integer max(Camera.Parameters parameters);
	
	protected abstract Integer min(Camera.Parameters parameters);

	@Override
	protected List<Integer> values(CameraHolder cameraHolder,
			Settings settings, SettingsManager settingsManager) {
		Camera.Parameters parameters
				=cameraHolder.cameraParameters(settings, settingsManager);
		Integer max=max(parameters);
		if (null==max) {
			return null;
		}
		Integer min=min(parameters);
		if (null==min) {
			return null;
		}
		List<Integer> result=new ArrayList<Integer>(Math.max(0, max-min+1));
		for (int ii=min; max>=ii; ++ii) {
			result.add(ii);
		}
		return result;
	}
}
