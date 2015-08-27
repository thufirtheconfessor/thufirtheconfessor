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
package confessor.thufir.lib.thufir4;

import confessor.thufir.lib.tracking.Geometry;
import confessor.thufir.lib.tracking.GeometryJobPool;

public class Thufir4Geometry implements Geometry<Thufir4Data, Thufir4Job> {
	private final Thufir4DataPool dataPool=new Thufir4DataPool();
	public final Thufir4DrawablePool drawablePool=new Thufir4DrawablePool();
	private final Thufir4JobPool jobPool=new Thufir4JobPool(this);

	@Override
	public Thufir4DataPool dataPool() {
		return dataPool;
	}
	
	@Override
	public int detectedBlobs() {
		return 20;
	}

	@Override
	public GeometryJobPool<Thufir4Data, Thufir4Job> jobPool() {
		return jobPool;
	}

	@Override
	public float maxTrackingBlobToImageSizeRatio() {
		return 1f/13f;
	}

	@Override
	public boolean preferNew(Thufir4Data oldData, Thufir4Data newData) {
		return oldData.approximateAreaToThePowerOf4()
				<newData.approximateAreaToThePowerOf4();
	}

	@Override
	public boolean similar(Thufir4Data data0, Thufir4Data data1) {
		float size0=data0.approximateAreaToThePowerOf4();
		float size1=data1.approximateAreaToThePowerOf4();
		if (size0>size1) {
			float tt=size0;
			size0=size1;
			size1=tt;
		}
		return size0>=0.81f*size1;
	}
}
