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
import confessor.thufir.AndroidErrorHandler;
import confessor.thufir.CameraHolder;
import confessor.thufir.settings.Settings;
import confessor.thufir.settings.SettingsManager;

public abstract class ExtraMinMaxSetting extends MinMaxSetting {
	private static final String DEF="-def";
	private static final String MAX="-max";
	private static final String MIN="-min";
	
	protected final String prefix;

	public ExtraMinMaxSetting(String key, String prefix) {
		super(key);
		this.prefix=prefix;
	}

	@Override
	protected Integer defaultValueTypedImpl(CameraHolder cameraHolder,
			Settings settings, SettingsManager settingsManager) {
		Camera.Parameters parameters
				=cameraHolder.cameraParameters(settings, settingsManager);
		String def=parameters.get(prefix+DEF);
		if (null!=def) {
			try {
				return Integer.parseInt(def);
			}
			catch (NumberFormatException ex) {
				AndroidErrorHandler.d(ex);
			}
		}
		return Integer.parseInt(parameters.get(prefix));
	}

	@Override
	protected Integer max(Camera.Parameters parameters) {
		String string=parameters.get(prefix+MAX);
		if (null==string) {
			return null;
		}
		try {
			return Integer.parseInt(string);
		}
		catch (NumberFormatException ex) {
			return null;
		}
	}

	@Override
	protected Integer min(Camera.Parameters parameters) {
		String string=parameters.get(prefix+MIN);
		if (null==string) {
			return null;
		}
		try {
			return Integer.parseInt(string);
		}
		catch (NumberFormatException ex) {
			return null;
		}
	}

	@Override
	public void setTyped(Camera.Parameters parameters, Integer value) {
		parameters.set(prefix, value);
	}
}
