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

public class PreviewFpsRangeSetting extends CameraParameterSetting<FpsRange> {
	private static final String KEY="preview-fps-range";
	
	public PreviewFpsRangeSetting() {
		super(KEY, FpsRange.class);
	}

	@Override
	protected FpsRange defaultValueTypedImpl(CameraHolder cameraHolder,
			Settings settings, SettingsManager settingsManager) {
		Camera.Parameters parameters
				=cameraHolder.cameraParameters(settings, settingsManager);
		FpsRange range=null;
		for (int[] range2: parameters.getSupportedPreviewFpsRange()) {
			FpsRange range3=new FpsRange(range2);
			if ((null==range)
					|| (0>range.compareTo(range3))) {
				range=range3;
			}
		}
		if (null!=range) {
			return range;
		}
		int[] range2=new int[2];
		parameters.getPreviewFpsRange(range2);
		return new FpsRange(range2);
	}

	@Override
	public void setTyped(Camera.Parameters parameters, FpsRange value) {
		parameters.setPreviewFpsRange(value.min, value.max);
	}

	@Override
	public String title() {
		return "Preview FPS";
	}

	@Override
	protected List<FpsRange> values(CameraHolder cameraHolder,
			Settings settings, SettingsManager settingsManager) {
		List<int[]> ranges=cameraHolder
				.cameraParameters(settings, settingsManager)
				.getSupportedPreviewFpsRange();
		if (null==ranges) {
			return null;
		}
		List<FpsRange> result=new ArrayList<FpsRange>(ranges.size());
		for (int[] range: ranges) {
			result.add(new FpsRange(range));
		}
		return result;
	}
}
