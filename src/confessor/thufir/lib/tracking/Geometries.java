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
package confessor.thufir.lib.tracking;

import confessor.thufir.lib.Util;
import confessor.thufir.lib.thread.Job;

public class Geometries {
	public static final float COLLINEAR_ERROR=0.02f;
	public static final float DISTANCE_ERROR=0.2f;
	public static final float PERPENDICULAR_ERROR=0.18f;
	public static final float SIMILAR_ERROR=0.8f;
	
	private Geometries() {
	}
	
	public static boolean collinear(float error, float x0, float y0,
			float x1, float y1, float x2, float y2) {
		return 1.0f-error<=normalizedDotProduct(x0, y0, x1, y1, x2, y2);
	}
	
	public static boolean collinear(Blob blob0, Blob blob1, Blob blob2) {
		return collinear(COLLINEAR_ERROR, blob0.x, blob0.y,
				blob1.x, blob1.y, blob2.x, blob2.y);
	}
	
	public static float distance(float x0, float y0,
			float x1, float y1) {
		return Util.sqrt(distanceSquared(x0, y0, x1, y1));
	}
	
	public static float distance(Blob blob0, Blob blob1) {
		return Util.sqrt(distanceSquared(blob0, blob1));
	}
	
	public static float distanceSquared(float x0, float y0,
			float x1, float y1) {
		x0-=x1;
		y0-=y1;
		return x0*x0+y0*y0;
	}
	
	public static float distanceSquared(Blob blob0, Blob blob1) {
		return distanceSquared(blob0.x, blob0.y, blob1.x, blob1.y);
	}
	
	public static boolean distanceSquaredWithError(float actualDistanceSquared,
			float error, float expectedDistanceSquared) {
		float error2=1.0f-error;
		error2*=error2;
		if (actualDistanceSquared<error2*expectedDistanceSquared) {
			return false;
		}
		error2=1.0f+error;
		error2*=error2;
		return actualDistanceSquared<=error2*expectedDistanceSquared;
	}
	
	public static boolean distanceSquaredWithError(float error,
			float expectedDistanceSquared, Blob blob0, Blob blob1) {
		return distanceSquaredWithError(expectedDistanceSquared, error,
				distanceSquared(blob0, blob1));
	}
	
	public static boolean distanceSquaredWithError(
			float expectedDistanceSquared, Blob blob0, Blob blob1) {
		return distanceSquaredWithError(expectedDistanceSquared,
				DISTANCE_ERROR, distanceSquared(blob0, blob1));
	}
	
	public static float interpolate(float i0, float id, float ii,
			float o0, float od) {
		return o0+(ii-i0)*od/id;
	}
	
	public static float interpolateZ(float d1, float z0, float zd, float mdm) {
		/*
		solution of
		solve([mdm*zm=dm, zd/d1=(zm-z0)/dm], [dm, zm]);
		by maxima
		*/
		return d1*z0/(d1-zd*mdm);
	}
	
	public static float interpolateZ(float x0, float y0, float z0,
			float x1, float y1, float z1, float xm, float ym) {
		xm-=x0/z0;
		ym-=y0/z0;
		x1-=x0;
		y1-=y0;
		z1-=z0;
		float mdm=Util.sqrt(xm*xm+ym*ym);
		float d1=Util.sqrt(x1*x1+y1*y1+z1*z1);
		return interpolateZ(d1, z0, z1, mdm);
	}
	
	public static boolean minimumDistanceSquaredWithError(
			float actualDistanceSquared, float error,
			float expectedDistanceSquared) {
		float error2=1.0f-error;
		error2*=error2;
		return actualDistanceSquared>=error2*expectedDistanceSquared;
	}
	
	public static float normalizedDotProduct(float x0, float y0,
			float x1, float y1, float x2, float y2) {
		x1-=x0;
		y1-=y0;
		x2-=x0;
		y2-=y0;
		float d21=x1*x1+y1*y1;
		float d22=x2*x2+y2*y2;
		return (x1*x2+y1*y2)/Util.sqrt(d21*d22);
	}
	
	public static boolean perpendicular(float error, float x0, float y0,
			float x1, float y1, float x2, float y2) {
		return error>Math.abs(normalizedDotProduct(x0, y0, x1, y1, x2, y2));
	}
	
	public static boolean perpendicular(Blob blob0, Blob blob1,
			Blob blob2) {
		return perpendicular(PERPENDICULAR_ERROR, blob0.x, blob0.y,
				blob1.x, blob1.y, blob2.x, blob2.y);
	}
	
	public static boolean perpendicularDistanceRatioWithError(
			float distanceError, float distanceRatioSquared,
			float perpendicularError, float x0, float y0, float x1, float y1,
			float x2, float y2) {
		x1-=x0;
		y1-=y0;
		x2-=x0;
		y2-=y0;
		float d21=x1*x1+y1*y1;
		float d22=x2*x2+y2*y2;
		if (!distanceSquaredWithError(
				d22, distanceError, d21*distanceRatioSquared)) {
			return false;
		}
		return perpendicularError>Math.abs((x1*x2+y1*y2)/Util.sqrt(d21*d22));
	}
	
	public static boolean perpendicularDistanceRatioWithError(
			float distanceError, float distanceRatioSquared, Blob blob0,
			Blob blob1, Blob blob2) {
		return perpendicularDistanceRatioWithError(distanceError,
				distanceRatioSquared, PERPENDICULAR_ERROR, blob0.x, blob0.y,
				blob1.x, blob1.y, blob2.x, blob2.y);
	}
	
	public static boolean perpendicularDistanceRatioWithError(
			float distanceRatioSquared, Blob blob0, Blob blob1,
			Blob blob2) {
		return perpendicularDistanceRatioWithError(DISTANCE_ERROR,
				distanceRatioSquared, blob0, blob1, blob2);
	}
	
	public static boolean perpendicularDistanceWithError(
			float distanceError, float expectedDistanceSquared,
			float perpendicularError, float x0, float y0, float x1, float y1,
			float x2, float y2) {
		x2-=x0;
		y2-=y0;
		float d22=x2*x2+y2*y2;
		if (!distanceSquaredWithError(
				d22, distanceError, expectedDistanceSquared)) {
			return false;
		}
		x1-=x0;
		y1-=y0;
		float d21=x1*x1+y1*y1;
		return perpendicularError>Math.abs(
				(x1*x2+y1*y2)/Util.sqrt(d21*d22));
	}
	
	public static boolean perpendicularDistanceWithError(float distanceError,
			float expectedDistanceSquared, Blob blob0, Blob blob1,
			Blob blob2) {
		return perpendicularDistanceWithError(distanceError,
				expectedDistanceSquared, PERPENDICULAR_ERROR,
				blob0.x, blob0.y, blob1.x, blob1.y, blob2.x, blob2.y);
	}
	
	public static boolean perpendicularDistanceWithError(
			float expectedDistanceSquared, Blob blob0, Blob blob1,
			Blob blob2) {
		return perpendicularDistanceWithError(DISTANCE_ERROR,
				expectedDistanceSquared, blob0, blob1, blob2);
	}
	
	public static boolean projectedDistanceRatioWithError(float collinearError,
			float distanceError, float distanceRatioSquared,
			float x0, float y0, float x1, float y1, float x2, float y2) {
		x1-=x0;
		y1-=y0;
		x2-=x0;
		y2-=y0;
		float d21=x1*x1+y1*y1;
		float d22=x2*x2+y2*y2;
		if (!distanceSquaredWithError(
				d22, distanceError, d21*distanceRatioSquared)) {
			return false;
		}
		return 1.0f-collinearError<=(x1*x2+y1*y2)/Util.sqrt(d21*d22);
	}
	
	public static boolean projectedDistanceRatioWithError(
			float distanceRatioSquared, Blob blob0, Blob blob1,
			Blob blob2) {
		return projectedDistanceRatioWithError(COLLINEAR_ERROR, DISTANCE_ERROR,
				distanceRatioSquared, blob0.x, blob0.y, blob1.x, blob1.y,
				blob2.x, blob2.y);
	}
	
	public static boolean projectedDistanceWithError(float collinearError,
			float distanceError, float expectedDistanceSquared,
			float x0, float y0, float x1, float y1, float x2, float y2) {
		x2-=x0;
		y2-=y0;
		float d22=x2*x2+y2*y2;
		if (!distanceSquaredWithError(
				d22, distanceError, expectedDistanceSquared)) {
			return false;
		}
		x1-=x0;
		y1-=y0;
		float d21=x1*x1+y1*y1;
		return 1.0f-collinearError<=(x1*x2+y1*y2)/Util.sqrt(d21*d22);
	}
	
	public static boolean projectedDistanceWithError(
			float expectedDistanceSquared, Blob blob0, Blob blob1,
			Blob blob2) {
		return projectedDistanceWithError(COLLINEAR_ERROR, DISTANCE_ERROR,
				expectedDistanceSquared, blob0.x, blob0.y,
				blob1.x, blob1.y, blob2.x, blob2.y);
	}
	
	public static boolean projectedMinimumDistanceRatioWithError(
			float collinearError, float distanceError,
			float distanceRatioSquared, float x0, float y0,
			float x1, float y1, float x2, float y2) {
		x1-=x0;
		y1-=y0;
		x2-=x0;
		y2-=y0;
		float d21=x1*x1+y1*y1;
		float d22=x2*x2+y2*y2;
		if (!minimumDistanceSquaredWithError(
				d22, distanceError, d21*distanceRatioSquared)) {
			return false;
		}
		return 1.0f-collinearError<=(x1*x2+y1*y2)/Util.sqrt(d21*d22);
	}
	
	public static boolean projectedMinimumDistanceRatioWithError(
			float distanceRatioSquared, Blob blob0, Blob blob1,
			Blob blob2) {
		return projectedMinimumDistanceRatioWithError(COLLINEAR_ERROR,
				DISTANCE_ERROR, 	distanceRatioSquared, blob0.x, blob0.y,
				blob1.x, blob1.y, blob2.x, blob2.y);
	}
	
	public static <G extends Geometry<D, J>, D extends GeometryData,
			J extends Job> D select(G geometry, D oldData, D newData) {
		if (null==oldData) {
			return newData;
		}
		if (null==newData) {
			return oldData;
		}
		if (geometry.preferNew(oldData, newData)) {
			geometry.dataPool().release(oldData);
			return newData;
		}
		geometry.dataPool().release(newData);
		return oldData;
	}
	
	public static boolean similar(float error, float radius0, float radius1) {
		if ((1f>radius0)
				|| (1f>radius1)) {
			return false;
		}
		if (radius0>radius1) {
			float tt=radius0;
			radius0=radius1;
			radius1=tt;
		}
		return error*radius1<radius0;
	}
	
	public static boolean similar(Blob blob0, Blob blob1) {
		return similar(SIMILAR_ERROR, blob0.radius, blob1.radius);
	}
}
