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

import confessor.thufir.lib.collection.AsyncList;
import confessor.thufir.lib.collection.Deque;
import confessor.thufir.lib.collection.ObjectPool;
import confessor.thufir.lib.image.Image;
import confessor.thufir.lib.tracking.Blob;
import confessor.thufir.lib.tracking.GeometryJobPool;

public class Thufir4JobPool extends ObjectPool<Thufir4Job>
		implements GeometryJobPool<Thufir4Data, Thufir4Job> {
	private final Thufir4Geometry geometry;
	
	public Thufir4JobPool(Thufir4Geometry geometry) {
		super(Deque.DEFAULT_CAPACITY, null);
		this.geometry=geometry;
	}

	@Override
	public Thufir4Job acquire(AsyncList<Blob> detectedBlobs, Image image,
			int thread, int threads) {
		Thufir4Job job=acquire();
		job.detectedBlobs=detectedBlobs;
		job.geometry=geometry;
		job.image=image;
		job.result=null;
		job.thread=thread;
		job.threads=threads;
		return job;
	}

	@Override
	protected Thufir4Job create() {
		return new Thufir4Job();
	}

	@Override
	protected void init(Thufir4Job object) {
	}

	@Override
	public Thufir4Data result(Thufir4Job job) {
		return job.result();
	}

	@Override
	protected void scrub(Thufir4Job object) {
		object.scrub();
	}
}
