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
package confessor.thufir;

import android.hardware.Camera;

public class CameraParameters {
	private Camera.Parameters[] parameters;
	
	public Camera.Parameters cameraParameters(int cameraId) {
		if (null==parameters) {
			Camera.Parameters[] parameters2
					=new Camera.Parameters[Camera.getNumberOfCameras()];
			for (int ii=parameters2.length-1; 0<=ii; --ii) {
				Camera camera=Camera.open(ii);
				try {
					parameters2[ii]=camera.getParameters();
				}
				finally {
					camera.release();
				}
			}
			parameters=parameters2;
		}
		return parameters[cameraId];
	}
	
	public Camera openCamera(int cameraId) {
		if (null!=parameters) {
			return Camera.open(cameraId);
		}
		Camera.Parameters[] parameters2
				=new Camera.Parameters[Camera.getNumberOfCameras()];
		for (int ii=parameters2.length-1; 0<=ii; --ii) {
			if (cameraId!=ii) {
				Camera camera=Camera.open(ii);
				try {
					parameters2[ii]=camera.getParameters();
				}
				finally {
					camera.release();
				}
			}
		}
		boolean success=false;
		Camera camera=Camera.open(cameraId);
		try {
			parameters2[cameraId]=camera.getParameters();
			parameters=parameters2;
			success=true;
			return camera;
		}
		finally {
			if (!success) {
				camera.release();
			}
		}
	}
}
