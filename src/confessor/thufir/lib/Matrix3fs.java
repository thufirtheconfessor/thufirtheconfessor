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
package confessor.thufir.lib;

import java.util.Arrays;

public class Matrix3fs {
	private Matrix3fs() {
	}
	
	public static void identity(float[] output) {
		output[0]=1f;
		output[1]=0f;
		output[2]=0f;
		output[3]=0f;
		output[4]=1f;
		output[5]=0f;
		output[6]=0f;
		output[7]=0f;
		output[8]=1f;
	}
	
	public static void mirrorX(float[] output) {
		output[0]=-1f;
		output[1]=0f;
		output[2]=0f;
		output[3]=0f;
		output[4]=1f;
		output[5]=0f;
		output[6]=0f;
		output[7]=0f;
		output[8]=1f;
	}
	
	public static void mirrorY(float[] output) {
		output[0]=1f;
		output[1]=0f;
		output[2]=0f;
		output[3]=0f;
		output[4]=-1f;
		output[5]=0f;
		output[6]=0f;
		output[7]=0f;
		output[8]=1f;
	}
	
	public static void multiplyMatrixMatrix(float[] left, float[] right,
			float[] output) {
		Arrays.fill(output, 0.0f);
		for (int rr=2; 0<=rr; --rr) {
			for (int cc=2; 0<=cc; --cc) {
				for (int ii=2; 0<=ii; --ii) {
					output[3*rr+cc]+=left[rr*3+ii]*right[3*ii+cc];
				}
			}
		}
	}
	
	public static void multiplyMatrixVector(float[] leftMatrix,
			float[] rightVector, float[] output) {
		Arrays.fill(output, 0.0f);
		for (int rr=2, ro=6; 0<=rr; --rr, ro-=3) {
			for (int ii=2; 0<=ii; --ii) {
				output[rr]+=leftMatrix[ro+ii]*rightVector[ii];
			}
		}
	}
	
	public static void rotateClockwise(float[] output) {
		output[0]=0f;
		output[1]=1f;
		output[2]=0f;
		output[3]=-1f;
		output[4]=0f;
		output[5]=0f;
		output[6]=0f;
		output[7]=0f;
		output[8]=1f;
	}
	
	public static void rotateCounterClockwise(float[] output) {
		output[0]=0f;
		output[1]=-1f;
		output[2]=0f;
		output[3]=1f;
		output[4]=0f;
		output[5]=0f;
		output[6]=0f;
		output[7]=0f;
		output[8]=1f;
	}
	
	public static void scale(float sx, float sy, float[] output) {
		output[0]=sx;
		output[1]=0f;
		output[2]=0f;
		output[3]=0f;
		output[4]=sy;
		output[5]=0f;
		output[6]=0f;
		output[7]=0f;
		output[8]=1f;
	}
	
	public static void shift(float dx, float dy, float[] output) {
		output[0]=1f;
		output[1]=0f;
		output[2]=dx;
		output[3]=0f;
		output[4]=1f;
		output[5]=dy;
		output[6]=0f;
		output[7]=0f;
		output[8]=1f;
	}
}
