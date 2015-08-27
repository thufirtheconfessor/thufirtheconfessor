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

import android.content.Context;

public class AsyncProgressBar extends ProgressBar {
	private class Updater implements Runnable {
		@Override
		public void run() {
			update();
		}
	}
	
	private int asyncMax=DEFAULT_MAX;
	private int asyncProgress=DEFAULT_PROGRESS;
	protected final Object lock=new Object();
	private final Updater updater=new Updater();
	
	public AsyncProgressBar(Context context, Integer borderColor,
			int backgroundColor, int foregroundColor) {
		super(context, borderColor, backgroundColor, foregroundColor);
	}
	
	public boolean needsUpdate() {
		synchronized (lock) {
			return !progressEquals(asyncProgress, asyncMax);
		}
	}
	
	public void postAsyncProgress(int progress, int max) {
		setAsyncProgress(progress, max);
		postUpdate();
	}
	
	public void postUpdate() {
		if (needsUpdate()) {
			post(updater);
		}
	}

	@Override
	public boolean progressEquals(int progress, int max) {
		synchronized (lock) {
			return super.progressEquals(progress, max);
		}
	}

	public void setAsyncProgress(int progress, int max) {
		synchronized (lock) {
			asyncMax=max;
			asyncProgress=progress;
		}
	}

	@Override
	public void setProgress(int progress, int max) {
		synchronized (lock) {
			setAsyncProgress(progress, max);
			super.setProgress(progress, max);
		}
	}
	
	public void update() {
		synchronized (lock) {
			setProgress(asyncProgress, asyncMax);
		}
	}
}
