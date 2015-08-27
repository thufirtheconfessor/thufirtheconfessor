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
package confessor.thufir.widgets;

import android.os.Handler;

public abstract class AsyncInt {
	private class Updater implements Runnable {
		@Override
		public void run() {
			update();
		}
	}
	
	private int asyncValue;
	private final Handler handler;
	protected final Object lock=new Object();
	private final Updater updater=new Updater();
	private int value;

	public AsyncInt(Handler handler) {
		this.handler=handler;
	}
	
	public boolean needsUpdate() {
		synchronized (lock) {
			return asyncValue!=value;
		}
	}
	
	public void postAsyncValue(int value) {
		setAsyncValue(value);
		postUpdate();
	}
	
	public void postUpdate() {
		if (needsUpdate()) {
			handler.post(updater);
		}
	}

	public void setAsyncValue(int value) {
		synchronized (lock) {
			asyncValue=value;
		}
	}

	public void setValue(int value) {
		synchronized (lock) {
			setAsyncValue(value);
			this.value=value;
			setValueImpl(value);
		}
	}
	
	protected abstract void setValueImpl(int value);
	
	public void update() {
		synchronized (lock) {
			setValue(asyncValue);
		}
	}
}
