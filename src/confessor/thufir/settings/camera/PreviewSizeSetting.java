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
import android.os.Build;
import java.util.List;

public class PreviewSizeSetting extends SizeSetting {
	public PreviewSizeSetting() {
		super("preview-size", "Preview size");
	}

	@Override
	protected Camera.Size cameraSize(Camera.Parameters parameters) {
		return parameters.getPreviewSize();
	}

	@Override
	protected List<Camera.Size> cameraSizes(Camera.Parameters parameters) {
		return parameters.getSupportedPreviewSizes();
	}

	@Override
	public void setTyped(Camera.Parameters parameters, Size value) {
		parameters.setPreviewSize(value.width, value.height);
	}

	@Override
	protected boolean suspicious(Size value) {
		if (super.suspicious(value)) {
			return true;
		}
		String device=Build.DEVICE.toLowerCase();
		if (device.contains("msm7630")
				|| device.contains("passion")
				|| device.contains("qsd8250")) {
			return 720<=value.height;
		}
		return false;
	}
}
