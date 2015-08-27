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

import confessor.thufir.lib.collection.ArrayListPool;
import confessor.thufir.lib.collection.AsyncList;
import confessor.thufir.lib.collection.Deque;
import confessor.thufir.lib.color.Brightness;
import confessor.thufir.lib.image.Image;
import confessor.thufir.lib.thread.AbstractJob;
import confessor.thufir.lib.tracking.Blob;
import confessor.thufir.lib.tracking.Geometries;
import java.util.ArrayList;
import java.util.List;

public class Thufir4Job extends AbstractJob {
	private static final int DX_2Y_2=6;
	private static final int DX_2Y1=3;
	private static final int DX_4Y1=4;
	private static final int DX1Y_2=5;
	private static final int DX1Y1=0;
	private static final int DX1Y3=2;
	private static final int DX3Y1=1;
	
	private final ArrayListPool<ArrayList<Blob>> listPool0
			=new ArrayListPool<ArrayList<Blob>>(Deque.DEFAULT_CAPACITY, 1);
	private final ArrayListPool<Blob> listPool1
			=new ArrayListPool<Blob>(Deque.DEFAULT_CAPACITY, null);
	public AsyncList<Blob> detectedBlobs;
	public Thufir4Geometry geometry;
	public Image image;
	public Thufir4Data result;
	public int thread;
	public int threads;
	
	@Override
	protected void closeImpl() throws Throwable {
	}
	
	public Thufir4Data result() {
		Thufir4Data result2=result;
		result=null;
		return result2;
	}

	@Override
	protected void runImpl() throws Throwable {
		ArrayList<ArrayList<Blob>> blobs=listPool0.acquire();
		for (int ii=0; ; ++ii) {
			Blob blob=detectedBlobs.get(ii, null);
			if (null==blob) {
				break;
			}
			if (!blob.quiet(blob.bright?Brightness.DARK:Brightness.BRIGHT,
					image)) {
				continue;
			}
			boolean found=false;
			for (int jj=blobs.size()-1; 0<=jj; --jj) {
				List<Blob> blobs2=blobs.get(jj);
				if (Geometries.similar(blob, blobs2.get(0))) {
					blobs2.add(blob);
					found=true;
					break;
				}
			}
			if (!found) {
				ArrayList<Blob> blobs2=listPool1.acquire();
				blobs2.add(blob);
				blobs.add(blobs2);
			}
		}
		for (int ii=blobs.size()-1; 0<=ii; --ii) {
			ArrayList<Blob> blobs2=blobs.remove(ii);
			search0(blobs2, ii);
			listPool1.release(blobs2);
		}
		listPool0.release(blobs);
	}
	
	public void scrub() {
		detectedBlobs=null;
		image=null;
		if (null!=result) {
			geometry.dataPool().release(result);
			result=null;
		}
		geometry=null;
		reset();
	}
	
	private void search0(ArrayList<Blob> blobs, int index) {
		int size=blobs.size();
		if (7>size) {
			return;
		}
		int ss;
		int mm;
		if (1>=threads) {
			ss=0;
			mm=1;
		}
		else {
			ss=(thread+index)%threads;
			mm=threads;
		}
		for (int i0=ss; size>i0; i0+=mm) {
			Blob blobX1Y1=blobs.get(i0);
			blobs.set(i0, blobs.get(0));
			blobs.set(0, blobX1Y1);
			search1(blobs);
			blobs.set(0, blobs.get(i0));
			blobs.set(i0, blobX1Y1);
		}
	}
	
	private void search1(ArrayList<Blob> blobs) {
		Blob blobX1Y1=blobs.get(DX1Y1);
		for (int i1=blobs.size()-1; 1<=i1; --i1) {
			Blob blobX3Y1=blobs.get(i1);
			float expectedDistance=2*(blobX1Y1.radius+blobX3Y1.radius);
			if (Geometries.distanceSquaredWithError(0.666f,
					expectedDistance*expectedDistance,
					blobX1Y1, blobX3Y1)) {
				blobs.set(i1, blobs.get(1));
				blobs.set(1, blobX3Y1);
				search2(blobs);
				blobs.set(1, blobs.get(i1));
				blobs.set(i1, blobX3Y1);
			}
		}
	}
	
	private void search2(ArrayList<Blob> blobs) {
		Blob blobX1Y1=blobs.get(DX1Y1);
		Blob blobX3Y1=blobs.get(DX3Y1);
		for (int i2=blobs.size()-1; 2<=i2; --i2) {
			Blob blobX1Y3=blobs.get(i2);
			float expectedDistance=2*(blobX1Y1.radius+blobX1Y3.radius);
			if (Geometries.perpendicularDistanceWithError(0.666f,
					expectedDistance*expectedDistance,
					blobX1Y1, blobX3Y1, blobX1Y3)) {
				blobs.set(i2, blobs.get(2));
				blobs.set(2, blobX1Y3);
				search3(blobs);
				blobs.set(2, blobs.get(i2));
				blobs.set(i2, blobX1Y3);
			}
		}
	}
	
	private void search3(ArrayList<Blob> blobs) {
		Blob blobX1Y1=blobs.get(DX1Y1);
		Blob blobX3Y1=blobs.get(DX3Y1);
		for (int i3=blobs.size()-1; 3<=i3; --i3) {
			Blob blobX_2Y1=blobs.get(i3);
			if (Geometries.projectedMinimumDistanceRatioWithError(
					16f, blobX1Y1, blobX3Y1, blobX_2Y1)) {
				blobs.set(i3, blobs.get(3));
				blobs.set(3, blobX_2Y1);
				search4(blobs);
				blobs.set(3, blobs.get(i3));
				blobs.set(i3, blobX_2Y1);
			}
		}
	}
	
	private void search4(ArrayList<Blob> blobs) {
		Blob blobX1Y1=blobs.get(DX1Y1);
		Blob blobX3Y1=blobs.get(DX3Y1);
		Blob blobX_2Y1=blobs.get(DX_2Y1);
		for (int i4=blobs.size()-1; 4<=i4; --i4) {
			Blob blobX_4Y1=blobs.get(i4);
			if (Geometries.projectedDistanceWithError(
					Geometries.distanceSquared(blobX1Y1, blobX3Y1),
					blobX_2Y1, blobX1Y1, blobX_4Y1)) {
				blobs.set(i4, blobs.get(4));
				blobs.set(4, blobX_4Y1);
				search5(blobs);
				blobs.set(4, blobs.get(i4));
				blobs.set(i4, blobX_4Y1);
			}
		}
	}
	
	private void search5(ArrayList<Blob> blobs) {
		Blob blobX1Y1=blobs.get(DX1Y1);
		Blob blobX1Y3=blobs.get(DX1Y3);
		for (int i5=blobs.size()-1; 5<=i5; --i5) {
			Blob blobX1Y_2=blobs.get(i5);
			if (Geometries.projectedMinimumDistanceRatioWithError(
					9f, blobX1Y1, blobX1Y3, blobX1Y_2)) {
				blobs.set(i5, blobs.get(5));
				blobs.set(5, blobX1Y_2);
				search6(blobs);
				blobs.set(5, blobs.get(i5));
				blobs.set(i5, blobX1Y_2);
			}
		}
	}
	
	private void search6(ArrayList<Blob> blobs) {
		Blob blobX_2Y1=blobs.get(DX_2Y1);
		Blob blobX1Y_2=blobs.get(DX1Y_2);
		Blob blobX1Y1=blobs.get(DX1Y1);
		for (int i6=blobs.size()-1; 6<=i6; --i6) {
			Blob blobX_2Y_2=blobs.get(i6);
			if (Geometries.perpendicular(
						blobX1Y_2, blobX1Y1, blobX_2Y_2)
					&& Geometries.perpendicular(
						blobX_2Y1, blobX1Y1, blobX_2Y_2)) {
				blobs.set(i6, blobs.get(6));
				blobs.set(6, blobX_2Y_2);
				select(blobs);
				blobs.set(6, blobs.get(i6));
				blobs.set(i6, blobX_2Y_2);
			}
		}
	}

	private void select(List<Blob> blobs) {
		Blob blobX_2Y_2=blobs.get(DX_2Y_2);
		Blob blobX_2Y1=blobs.get(DX_2Y1);
		Blob blobX_4Y1=blobs.get(DX_4Y1);
		Blob blobX1Y_2=blobs.get(DX1Y_2);
		Blob blobX1Y1=blobs.get(DX1Y1);
		Blob blobX1Y3=blobs.get(DX1Y3);
		Blob blobX3Y1=blobs.get(DX3Y1);
		Thufir4Data newData=geometry.dataPool().acquire();
		newData.init(blobX1Y1.bright, geometry.drawablePool, blobX_2Y_2,
				blobX_2Y1, blobX_4Y1, blobX1Y_2, blobX1Y1, blobX1Y3,
				blobX3Y1);
		result=Geometries.select(geometry, result, newData);
	}
}
