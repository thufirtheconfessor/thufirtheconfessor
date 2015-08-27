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
package confessor.thufir.lib.color;

import confessor.thufir.lib.collection.Deque;
import confessor.thufir.lib.collection.ObjectPool;
import confessor.thufir.lib.image.Image;
import confessor.thufir.lib.thread.AbstractJob;
import confessor.thufir.lib.thread.ThreadPool;
import java.util.ArrayList;
import java.util.Arrays;

public class OtsuBrightnessClassifierInitializer
		extends AverageBrightnessClassifierInitializer {
	private class HistogramJob extends AbstractJob {
		public byte[] brightness;
		public final int[] histogram=new int[256];
		public int from;
		public int to;

		@Override
		protected void closeImpl() throws Throwable {
		}
		
		public void init(byte[] brightness, int from, int to) {
			this.brightness=brightness;
			this.from=from;
			this.to=to;
		}

		@Override
		protected void runImpl() throws Throwable {
			histogram(histogram, brightness, from, to);
		}
		
		public void scrub() {
			brightness=null;
			reset();
		}
	}
	
	private class HistogramJobPool extends ObjectPool<HistogramJob> {
		public HistogramJobPool() {
			super(Deque.DEFAULT_CAPACITY, null);
		}

		@Override
		protected HistogramJob create() {
			return new HistogramJob();
		}

		@Override
		protected void init(HistogramJob object) {
		}

		@Override
		protected void scrub(HistogramJob object) {
			object.scrub();
		}
	}
	
	private final ArrayList<HistogramJob> jobs=new ArrayList<HistogramJob>();
	private final int[] histogram=new int[256];
	private final HistogramJobPool histogramJobPool=new HistogramJobPool();
	private final ThreadPool threadPool;

	public OtsuBrightnessClassifierInitializer(ThreadPool threadPool) {
		this.threadPool=threadPool;
	}
	
	private static void histogram(int[] histogram, byte[] brightness, int from,
			int to) {
		Arrays.fill(histogram, 0);
		int length=to-from;
		if (0>=length) {
			return;
		}
		int remainder=length&63;
		if (0<remainder) {
			int to2=to-remainder;
			histogramBlock(histogram, brightness, to2, to);
			to=to2;
		}
		for (int ff=from; to>ff; ) {
			int tt=ff+64;
			histogramBlock(histogram, brightness, ff, tt);
			ff=tt;
		}
	}
	
	private static void histogramBlock(int[] histogram, byte[] brightness,
			int from, int to) {
		int max=brightness[from]&0xff;
		int min=max;
		for (int ii=from+1; to>ii; ++ii) {
			int value=brightness[ii]&0xff;
			if (value>max) {
				max=value;
			}
			else if (value<min) {
				min=value;
			}
		}
		int size=max-min+1;
		for (int ii=from; to>ii; ++ii) {
			histogram[brightness[ii]&0xff]+=size;
		}
	}
	
	@Override
	protected void init(Image image) throws Throwable {
		byte[] brightness=image.brightness;
		int length=brightness.length;
		Arrays.fill(histogram, 0);
		int size=threadPool.size();
		jobs.clear();
		try {
			int to=length;
			for (int ii=size-1; 0<=ii; --ii) {
				HistogramJob job=histogramJobPool.acquire();
				int from=ii*length/size;
				job.init(brightness, from, to);
				jobs.add(job);
				to=from;
			}
			histogramJobPool.clear();
			threadPool.submitAndJoin(jobs);
			for (int ii=size-1; 0<=ii; --ii) {
				HistogramJob job=jobs.get(ii);
				int[] histogram2=job.histogram;
				for (int jj=255; 0<=jj; --jj) {
					histogram[jj]+=histogram2[jj];
				}
			}
			histogramJobPool.release(jobs);
		}
		finally {
			jobs.clear();
		}
		set(otsuThreshold(histogram));
	}
	
	private static int otsuThreshold(int[] histogram) {
		int total=0;
		float sum=0;
		for (int ii=255; 0<=ii; --ii) {
			sum+=ii*histogram[ii];
			total+=histogram[ii];
		}
		float sumB=0;
		int wB=0;
		int wF;
		float varMax=0;
		int threshold=0;
		for (int tt=0; 256>tt; tt++) {
			wB+=histogram[tt];
			if (0.0f==wB) {
				continue;
			}
			wF=total-wB;
			if (0==wF) {
				break;
			}
			sumB+=tt*histogram[tt];
			float mB=sumB/wB;
			float mF=(sum-sumB)/wF;
			float varBetween=(float)wB*(float)wF*(mB-mF)*(mB-mF);
			if (varBetween>varMax) {
				varMax=varBetween;
				threshold=tt;
			}
		}
		return threshold;
	}
}
