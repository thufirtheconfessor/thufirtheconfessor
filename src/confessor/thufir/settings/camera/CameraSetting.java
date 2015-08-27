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
import confessor.thufir.settings.SettingCategory;
import confessor.thufir.settings.Settings;
import confessor.thufir.settings.SettingsManager;
import confessor.thufir.settings.TypedListSetting;
import java.util.ArrayList;
import java.util.List;

public class CameraSetting extends TypedListSetting<IdName> {
	private static final String KEY="camera-id";
	
	public CameraSetting() {
		super(KEY, IdName.class);
	}

	@Override
	public SettingCategory category() {
		return SettingCategory.CAMERA;
	}

	@Override
	protected IdName defaultValueTypedImpl(CameraHolder cameraHolder,
			Settings settings, SettingsManager settingsManager) {
		return idName(0);
	}
	
	private static IdName idName(int id) {
		Camera.CameraInfo cameraInfo=new Camera.CameraInfo();
		Camera.getCameraInfo(id, cameraInfo);
		String name;
		switch (cameraInfo.facing) {
			case Camera.CameraInfo.CAMERA_FACING_BACK:
				name=id+" - back";
				break;
			case Camera.CameraInfo.CAMERA_FACING_FRONT:
				name=id+" - front";
				break;
			default:
				name=Integer.toString(id);
				break;
		}
		return new IdName(id, name);
	}

	@Override
	public boolean reloadCamera() {
		return true;
	}

	@Override
	public String title() {
		return "Camera";
	}

	@Override
	protected List<IdName> values(CameraHolder cameraHolder, Settings settings,
			SettingsManager settingsManager) {
		int size=Camera.getNumberOfCameras();
		List<IdName> result=new ArrayList<IdName>(size);
		for (int ii=0; size>ii; ++ii) {
			result.add(idName(ii));
		}
		return result;
	}
}
