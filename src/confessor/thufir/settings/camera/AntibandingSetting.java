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
import java.util.List;

public class AntibandingSetting extends CameraParameterSetting<String> {
	public static final String KEY="antibanding";
	
	public AntibandingSetting() {
		super(KEY, String.class);
	}

	@Override
	protected String defaultValueTypedImpl(CameraHolder cameraHolder,
			Settings settings, SettingsManager settingsManager) {
		Camera.Parameters parameters
				=cameraHolder.cameraParameters(settings, settingsManager);
		if (parameters.getSupportedAntibanding()
				.contains(Camera.Parameters.ANTIBANDING_AUTO)) {
			return Camera.Parameters.ANTIBANDING_AUTO;
		}
		return parameters.getAntibanding();
	}

	@Override
	public void setTyped(Camera.Parameters parameters, String value) {
		parameters.setAntibanding(value);
	}

	@Override
	public String title() {
		return "Antibanding";
	}

	@Override
	protected List<String> values(CameraHolder cameraHolder, Settings settings,
			SettingsManager settingsManager) {
		return cameraHolder
				.cameraParameters(settings, settingsManager)
				.getSupportedAntibanding();
	}
}
