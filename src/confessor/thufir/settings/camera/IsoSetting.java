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

public class IsoSetting extends CameraParameterSetting<String> {
	public static final String DEFAULT="100";
	public static final String KEY="iso";
	public static final String ISO="iso";
	public static final String ISO_VALUES="iso-values";
	
	public IsoSetting() {
		super(KEY, String.class);
	}

	@Override
	protected String defaultValueTypedImpl(CameraHolder cameraHolder,
			Settings settings, SettingsManager settingsManager) {
		Camera.Parameters parameters
				=cameraHolder.cameraParameters(settings, settingsManager);
		if (values(ISO_VALUES, parameters).contains(DEFAULT)) {
			return DEFAULT;
		}
		return parameters.get(ISO);
	}

	@Override
	public void setTyped(Camera.Parameters parameters, String value) {
		parameters.set(ISO, value);
	}

	@Override
	public String title() {
		return "ISO";
	}

	@Override
	protected List<String> values(CameraHolder cameraHolder, Settings settings,
			SettingsManager settingsManager) {
		return values(ISO_VALUES,
					cameraHolder.cameraParameters(settings, settingsManager));
	}
}
