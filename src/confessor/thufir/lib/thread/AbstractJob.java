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

import confessor.thufir.lib.ClosedException;

public abstract class AbstractJob implements Job {
	private boolean error;
	private boolean finished;
	private final Object jobLock=new Object();
	private Throwable throwable;

	@Override
	public void close() throws Throwable {
		try {
			closeImpl();
		}
		catch (Throwable throwable2) {
			synchronized (jobLock) {
				error=true;
				finished=true;
				throwable=throwable2;
			}
		}
		finally {
			synchronized (jobLock) {
				finished=true;
				jobLock.notifyAll();
				if (!error) {
					error=true;
					throwable=new ClosedException();
				}
			}
		}
	}
	
	protected abstract void closeImpl() throws Throwable;

	public boolean finished() {
		synchronized (jobLock) {
			return finished;
		}
	}

	@Override
	public void join() throws InterruptedException, JobException {
		synchronized (jobLock) {
			while (!finished) {
				jobLock.wait();
			}
			if (error) {
				throw new JobException(throwable);
			}
		}
	}
	
	@Override
	public void reset() {
		synchronized (jobLock) {
			error=false;
			finished=false;
			throwable=null;
			jobLock.notifyAll();
		}
	}

	@Override
	public void run() {
		Throwable throwable2=null;
		try {
			runImpl();
		}
		catch (Throwable throwable3) {
			throwable2=throwable3;
		}
		finally {
			synchronized (jobLock) {
				finished=true;
				if (null!=throwable2) {
					error=true;
					throwable=throwable2;
				}
				jobLock.notifyAll();
			}
		}
	}
	
	protected abstract void runImpl() throws Throwable;
}
