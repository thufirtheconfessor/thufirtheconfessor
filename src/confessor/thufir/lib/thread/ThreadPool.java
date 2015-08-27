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
package confessor.thufir.lib.thread;

import confessor.thufir.lib.AbstractCloseable;
import confessor.thufir.lib.collection.Deque;
import confessor.thufir.lib.stream.ErrorHandler;
import java.util.List;

public abstract class ThreadPool extends AbstractCloseable {
	private class Worker implements Runnable {
		private final int index;

		public Worker(int index) {
			this.index=index;
		}
		
		@Override
		public void run() {
			try {
				while (true) {
					Job job;
					synchronized (abstractCloseableLock) {
						if (isClosed()
								|| (index>=workers.length)
								|| (this!=workers[index])) {
							return;
						}
						if (jobs.isEmpty()) {
							abstractCloseableLock.wait();
							continue;
						}
						job=jobs.removeFirst();
					}
					try {
						job.run();
					}
					catch (Throwable throwable) {
						errorHandler.error(throwable);
					}
				}
			}
			catch (Throwable throwable) {
				errorHandler.error(throwable);
			}
		}
	}
	
	private final ErrorHandler errorHandler;
	private final Deque<Job> jobs=new Deque<Job>();
	private Worker[] workers=new Worker[0];

	public ThreadPool(ErrorHandler errorHandler) {
		this.errorHandler=errorHandler;
	}

	@Override
	protected void closeImpl() throws Throwable {
		Throwable throwable=null;
		synchronized (abstractCloseableLock) {
			abstractCloseableLock.notifyAll();
			while (!jobs.isEmpty()) {
				try {
					jobs.removeFirst().close();
				}
				catch (Throwable throwable2) {
					if (null!=throwable) {
						throwable=throwable2;
					}
				}
			}
			workers=new Worker[0];
		}
		if (null!=throwable) {
			throw new JobException(throwable);
		}
	}
	
	public void join(List<? extends Job> jobs)
			throws InterruptedException, JobException {
		for (int ii=0, ss=jobs.size(); ss>ii; ++ii) {
			jobs.get(ii).join();
		}
	}
	
	public boolean resize() {
		synchronized (abstractCloseableLock) {
			int size=size();
			if (workers.length<size) {
				Worker[] newWorkers=new Worker[size];
				System.arraycopy(workers, 0, newWorkers, 0, workers.length);
				for (int ii=workers.length; size>ii; ++ii) {
					newWorkers[ii]=new Worker(ii);
					new Thread(newWorkers[ii]).start();
				}
				workers=newWorkers;
				abstractCloseableLock.notifyAll();
				return true;
			}
			if (workers.length>size) {
				Worker[] newWorkers=new Worker[size];
				System.arraycopy(workers, 0, newWorkers, 0, size);
				workers=newWorkers;
				abstractCloseableLock.notifyAll();
				return true;
			}
			return false;
		}
	}
	
	public abstract int size();
	
	public void submit(Job job) {
		synchronized (abstractCloseableLock) {
			jobs.addLast(job);
			if (!resize()) {
				abstractCloseableLock.notify();
			}
		}
	}
	
	public void submit(List<? extends Job> jobs) {
		synchronized (abstractCloseableLock) {
			for (int ii=0, ss=jobs.size(); ss>ii; ++ii) {
				this.jobs.addLast(jobs.get(ii));
			}
			if (!resize()) {
				abstractCloseableLock.notifyAll();
			}
		}
	}
	
	public void submitAndJoin(Job job)
			throws InterruptedException, JobException {
		submit(job);
		job.join();
	}
	
	public void submitAndJoin(List<? extends Job> jobs)
			throws InterruptedException, JobException {
		submit(jobs);
		join(jobs);
	}
}
