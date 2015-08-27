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
package confessor.thufir.lib.file;

import confessor.thufir.lib.AbstractCloseable;
import confessor.thufir.lib.ClosedException;
import confessor.thufir.lib.collection.ByteArrayPool;
import confessor.thufir.lib.collection.Deque;
import confessor.thufir.lib.collection.ObjectPool;
import java.util.List;

public class AsyncFileSaver extends AbstractCloseable {
	private static class Data {
		public boolean clock;
		public int crc;
		public byte[] data;
	}
	
	private class DataPool extends ObjectPool<Data> {
		public DataPool(int initialSize, Integer maxSize) {
			super(initialSize, maxSize);
		}

		@Override
		protected Data create() {
			return new Data();
		}
		
		public Data create(boolean clock, int crc, byte[] data, int offset,
				int length) {
			Data result=acquire();
			result.clock=clock;
			result.crc=crc;
			result.data=byteArrayPool.acquire(length);
			System.arraycopy(data, offset, result.data, 0, length);
			return result;
		}

		@Override
		protected void init(Data object) {
		}

		@Override
		protected void scrub(Data object) {
			if (null!=object.data) {
				try {
					byteArrayPool.release(object.data);
				}
				finally {
					object.data=null;
				}
			}
		}
	}
	
	public interface Listener {
		void error(FileSaver fileSaver, Throwable throwable);
		void paused(FileSaver fileSaver);
		void saved(FileSaver fileSaver, FileType fileType, boolean valid);
		void stopped(FileSaver fileSaver);
	}
	
	private class RunnableImpl implements Runnable {
		@Override
		public void run() {
			try {
				try {
					boolean pause2=false;
					boolean stop2=false;
					outer: while (fileSaver.size()>fileSaver.saved()) {
						Data data;
						synchronized (abstractCloseableLock) {
							while (true) {
								checkClosed();
								if (stop) {
									stop2=true;
									break outer;
								}
								if (pause) {
									pause2=true;
									break outer;
								}
								if (!datas.isEmpty()) {
									data=datas.removeFirst();
									break;
								}
								abstractCloseableLock.wait();
							}
							checkClosed();
						}
						fileSaver.save(data.clock, data.crc,
								data.data, 0, data.data.length);
						dataPool.release(data);
					}
					if (stop2) {
						listener.stopped(fileSaver);
						return;
					}
					if (pause2) {
						listener.paused(fileSaver);
						return;
					}
					checkClosed();
					FileType fileType=selectFileType();
					checkClosed();
					boolean valid=check(fileType);
					checkClosed();
					listener.saved(fileSaver, fileType, valid);
				}
				finally {
					synchronized (abstractCloseableLock) {
						finished=true;
						try {
							datas.clear();
						}
						finally {
							fileSaver.close();
						}
					}
				}
			}
			catch (ClosedException ex) {
			}
			catch (Throwable throwable) {
				listener.error(fileSaver, throwable);
			}
		}
	}
	
	private final ByteArrayPool byteArrayPool
			=new ByteArrayPool(Deque.DEFAULT_CAPACITY, Deque.DEFAULT_CAPACITY);
	private final Deque<Data> datas=new Deque<Data>();
	private final DataPool dataPool=new DataPool(
			Deque.DEFAULT_CAPACITY, Deque.DEFAULT_CAPACITY);
	private final FileSaver fileSaver;
	private final List<FileType> fileTypes;
	private boolean finished;
	private final Listener listener;
	private boolean pause;
	private boolean started;
	private boolean stop;

	public AsyncFileSaver(FileSaver fileSaver, List<FileType> fileTypes,
			Listener listener) {
		this.fileSaver=fileSaver;
		this.fileTypes=fileTypes;
		this.listener=listener;
	}

	@Override
	protected void closeImpl() throws Throwable {
		synchronized (abstractCloseableLock) {
			if (!started) {
				datas.clear();
				return;
			}
		}
		start();
	}
	
	private boolean check(FileType fileType) {
		try {
			FileType.IntegrityChecker checker
					=fileType.checkIntegrity(fileSaver.partialFile());
			try {
				while (true) {
					checkClosed();
					Boolean valid=checker.check();
					if (null!=valid) {
						return valid;
					}
				}
			}
			finally {
				checker.close();
			}
		}
		catch (Throwable throwable) {
			return false;
		}
	}
	
	public void data(boolean clock, int crc, byte[] buf, int length,
			int offset) {
		Data data2=dataPool.create(clock, crc, buf, offset, length);
		synchronized (abstractCloseableLock) {
			checkClosed();
			if (finished) {
				return;
			}
			datas.addLast(data2);
		}
		start();
	}
	
	public void pause() {
		synchronized (abstractCloseableLock) {
			pause=true;
		}
		start();
	}
	
	private FileType selectFileType() {
		for (FileType fileType: fileTypes) {
			checkClosed();
			try {
				if (fileType.detectType(fileSaver.resultFile())) {
					return fileType;
				}
			}
			catch (Throwable throwable) {
			}
		}
		return null;
	}
	
	private void start() {
		synchronized (abstractCloseableLock) {
			if (started) {
				abstractCloseableLock.notify();
				return;
			}
			checkClosed();
			started=true;
		}
		new Thread(new RunnableImpl()).start();
	}
	
	public void stop() {
		synchronized (abstractCloseableLock) {
			stop=true;
		}
		start();
	}
}
