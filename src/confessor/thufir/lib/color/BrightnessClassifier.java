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
import confessor.thufir.lib.thread.AbstractJob;
import confessor.thufir.lib.thread.ThreadPool;
import java.util.ArrayList;
import java.util.Arrays;

public class BrightnessClassifier {
	public static final int DEFAULT_TRESHOLD=64;
	
	private static final byte[] REVERSE;
	
	static {
		REVERSE=new byte[2];
		REVERSE[Brightness.BRIGHT]=Brightness.DARK;
		REVERSE[Brightness.DARK]=Brightness.BRIGHT;
	}
	
	private static class MapJob extends AbstractJob {
		private int from;
		private byte[] map;
		private int to;
		private byte[] values;

		@Override
		protected void closeImpl() throws Throwable {
		}

		public void init(int from, byte[] map, int to, byte[] values) {
			this.from=from;
			this.map=map;
			this.to=to;
			this.values=values;
		}

		@Override
		protected void runImpl() throws Throwable {
			for (int ii=from; to>ii; ++ii) {
				values[ii]=map[values[ii]&0xff];
			}
		}
		
		public void scrub() {
			map=null;
			values=null;
			reset();
		}
	}
	
	private class MapJobPool extends ObjectPool<MapJob> {
		public MapJobPool() {
			super(Deque.DEFAULT_CAPACITY, null);
		}

		@Override
		protected MapJob create() {
			return new MapJob();
		}

		@Override
		protected void init(MapJob object) {
		}

		@Override
		protected void scrub(MapJob object) {
			object.scrub();
		}
	}
	
	private final ArrayList<MapJob> jobs=new ArrayList<MapJob>();
	public final byte[] normal=new byte[256];
	private final MapJobPool mapJobPool=new MapJobPool();
	public final byte[] reverse=new byte[256];
	public int threshold;

	public BrightnessClassifier() {
		Arrays.fill(normal, Brightness.DARK);
		Arrays.fill(reverse, Brightness.BRIGHT);
	}
	
	public byte brightness(byte value) {
		return normal[value&0xff];
	}
	
	public void classify(byte[] brightness, ThreadPool threadPool)
			throws Throwable {
		map(normal, threadPool, brightness);
	}
	
	public void classifyReverse(byte[] brightness, ThreadPool threadPool)
			throws Throwable {
		map(reverse, threadPool, brightness);
	}
	
	public void init(int threshold) {
		this.threshold=threshold;
		for (int ii=threshold-1; 0<=ii; --ii) {
			normal[ii]=Brightness.DARK;
			reverse[ii]=Brightness.BRIGHT;
		}
		for (int ii=threshold; 256>ii; ++ii) {
			normal[ii]=Brightness.BRIGHT;
			reverse[ii]=Brightness.DARK;
		}
	}
	
	private void map(byte[] map, ThreadPool threadPool, byte[] values)
			throws Throwable {
		int size=threadPool.size();
		if (1>=size) {
			for (int ii=values.length-1; 0<=ii; --ii) {
				values[ii]=map[values[ii]&0xff];
			}
		}
		else {
			jobs.clear();
			try {
				int length=values.length;
				int to=length;
				for (int ii=size-1; 0<=ii; --ii) {
					int from=ii*length/size;
					MapJob job=mapJobPool.acquire();
					job.init(from, map, to, values);
					jobs.add(job);
					to=from;
				}
				threadPool.submitAndJoin(jobs);
				mapJobPool.release(jobs);
			}
			finally {
				jobs.clear();
			}
		}
	}
	
	public void reverse(byte[] brightness, ThreadPool threadPool)
			throws Throwable {
		map(REVERSE, threadPool, brightness);
	}
}
