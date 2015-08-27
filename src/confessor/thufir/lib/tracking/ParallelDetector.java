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

import confessor.thufir.lib.collection.AsyncList;
import confessor.thufir.lib.collection.Deque;
import confessor.thufir.lib.collection.ObjectPool;
import confessor.thufir.lib.image.Image;
import confessor.thufir.lib.thread.AbstractJob;
import confessor.thufir.lib.thread.Job;
import confessor.thufir.lib.thread.JobException;
import confessor.thufir.lib.thread.ThreadPool;
import java.util.ArrayList;
import java.util.List;

public class ParallelDetector<D extends GeometryData, G extends Geometry<D, J>,
		J extends Job> {
	private class BlobsJob extends AbstractJob implements DetectedBlob {
		public final BlobDetector blobDetector=blobSize.blobDetector();
		public boolean dirty;
		public int fromX;
		public int fromY;
		public D geometryData;
		public Image image;
		public int maxQuietZoneDistance;
		public int stride;
		public int toX;
		public int toY;

		@Override
		public void blob(boolean brightOnly, float xx, float yy) {
			int x2=Math.round(xx);
			int y2=Math.round(yy);
			if (detect(brightOnly, image, maxQuietZoneDistance, x2, y2)) {
				return;
			}
			for (int dx=-1; 1>=dx; ++dx) {
				for (int dy=-1; 1>=dy; ++dy) {
					int x3=x2+dx*stride;
					int y3=y2+dy*stride;
					if ((fromX<=x3)
							&& (toX>x3)
							&& (fromY<=y3)
							&& (toY>y3)) {
						detect(brightOnly, image, maxQuietZoneDistance,
								x3, y3);
					}
				}
			}
		}

		@Override
		protected void closeImpl() throws Throwable {
			detecteds.forceEol();
		}
		
		protected void detect(boolean brightOnly) throws Throwable {
			if (dirty) {
				blobDetector.clear(image);
				dirty=false;
			}
			if (null==geometryData) {
				detectAll(brightOnly);
			}
			else {
				detectOld(brightOnly);
			}
		}
		
		protected boolean detect(boolean brightOnly, Image image,
				int maxQuietZoneDistance, int xx, int yy) {
			Blob blob=blobDetector.detect(blobPool, brightOnly, image,
					maxQuietZoneDistance, xx, yy);
			if (null==blob) {
				return false;
			}
			if (!detecteds.addIfDoesntContain(blob, Blob.EQUALS)) {
				blobPool.release(blob);
			}
			return true;
		}
		
		private void detectAll(boolean brightOnly) throws Throwable {
			for (int xx=from(fromX, stride); toX>xx; xx+=stride) {
				for (int yy=from(fromY, stride); toY>yy; yy+=stride) {
					detect(brightOnly, image, maxQuietZoneDistance, xx, yy);
				}
			}
		}
		
		private void detectOld(boolean brightOnly) throws Throwable {
			if (null!=geometryData) {
				geometryData.detectedBlobs(brightOnly, this);
			}
		}

		@Override
		protected void runImpl() throws Throwable {
			try {
				detect(null!=bright);
			}
			catch (Throwable throwable) {
				detecteds.forceEol();
				throw throwable;
			}
			finally {
				detecteds.eol();
			}
		}
		
		public void scrub() {
			geometryData=null;
			image=null;
			reset();
		}

		public void set(D geometryData, Image image, ImagePart imagePart,
				int stride) {
			this.geometryData=geometryData;
			this.image=image;
			this.stride=stride;
			dirty=true;
			int height=image.height;
			int width=image.width;
			fromX=imagePart.fromX(width);
			fromY=imagePart.fromY(height);
			toX=imagePart.toX(width);
			toY=imagePart.toY(height);
			maxQuietZoneDistance=Math.round(Math.max(width, height)
						*geometry.maxTrackingBlobToImageSizeRatio())
					+1;
		}
	}
	
	private class BlobsJobPool extends ObjectPool<BlobsJob> {
		public BlobsJobPool() {
			super(Deque.DEFAULT_CAPACITY, null);
		}

		@Override
		protected BlobsJob create() {
			return new BlobsJob();
		}

		@Override
		protected void init(BlobsJob object) {
		}

		@Override
		protected void scrub(BlobsJob object) {
			object.scrub();
		}
	}
	
	private final BlobPool blobPool;
	private final BlobSize blobSize;
	private final BlobsJobPool blobsJobPool=new BlobsJobPool();
	private final ArrayList<BlobsJob> blobsJobs=new ArrayList<BlobsJob>();
	private final Byte bright;
	private final AsyncList<Blob> detecteds=new AsyncList<Blob>();
	private final Geometry<D, J> geometry;
	private final ArrayList<J> geometryJobs=new ArrayList<J>();
	private Deque<ImagePart> imageParts;
	private final ThreadPool threadPool;

	public ParallelDetector(BlobSize blobSize, Byte bright,
			Geometry<D, J> geometry, ThreadPool threadPool) {
		this.blobSize=blobSize;
		this.bright=bright;
		this.geometry=geometry;
		this.threadPool=threadPool;
		blobPool=new BlobPool(8*geometry.detectedBlobs(),
				8*geometry.detectedBlobs());
	}
	
	public D detect(D geometryData, Image image) throws Throwable {
		blobsJobs.clear();
		geometryJobs.clear();
		try {
			int size=threadPool.size();
			if ((null==imageParts)
					|| (imageParts.size()!=size)) {
				imageParts=ImagePart.split(size);
			}
			int stride=Stride.stride(image);
			detecteds.reset(size);
			GeometryJobPool<D, J> geometryJobPool=geometry.jobPool();
			for (int ii=0; size>ii; ++ii) {
				BlobsJob blobsJob=blobsJobPool.acquire();
				blobsJob.set(geometryData, image, imageParts.get(ii), stride);
				blobsJob.geometryData=geometryData;
				blobsJobs.add(blobsJob);
				geometryJobs.add(
						geometryJobPool.acquire(detecteds, image, ii, size));
			}
			blobsJobPool.clear();
			geometryJobPool.clear();
			D result=null;
			if (null!=geometryData) {
				result=runJobs(geometryJobPool, geometryJobs, blobsJobs,
						size);
				if ((null!=result)
						&& (!geometry.similar(geometryData, result))) {
					geometry.dataPool().release(result);
					result=null;
				}
				if (null==result) {
					detecteds.eol(size);
					for (int ii=size-1; 0<=ii; --ii) {
						BlobsJob blobsJob=blobsJobs.get(ii);
						blobsJob.geometryData=null;
						blobsJob.reset();
						geometryJobs.get(ii).reset();
					}
				}
			}
			if (null==result) {
				result=runJobs(geometryJobPool, geometryJobs, blobsJobs, size);
			}
			blobsJobPool.release(blobsJobs);
			geometryJobPool.release(geometryJobs);
			for (int ii=0; ; ++ii) {
				Blob blob=detecteds.get(ii, null);
				if (null==blob) {
					break;
				}
				blobPool.release(blob);
			}
			return result;
		}
		finally {
			blobsJobs.clear();
			geometryJobs.clear();
			detecteds.reset(0);
		}
	}
	
	private static int from(int from, int stride) {
		from-=stride/2;
		if (0>=from) {
			return stride/2;
		}
		int rr=from%stride;
		if (0!=rr) {
			from-=rr;
			from+=stride;
		}
		from+=stride/2;
		return from;
	}
	
	private D runJobs(GeometryJobPool<D, J> geometryJobPool,
			List<J> geometryJobs, List<BlobsJob> blobsJobs, int size)
			throws InterruptedException, JobException {
		threadPool.submit(blobsJobs);
		threadPool.submit(geometryJobs);
		threadPool.join(blobsJobs); //for errors
		threadPool.join(geometryJobs);
		D result=null;
		for (int ii=size-1; 0<=ii; --ii) {
			result=Geometries.select(geometry, result,
					geometryJobPool.result(geometryJobs.get(ii)));
		}
		return result;
	}
}
