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
import android.util.AttributeSet;
import confessor.thufir.lib.MutableString;
import confessor.thufir.lib.Util;

public class AsyncMutableTextView extends MutableTextView {
	private class Updater implements Runnable {
		@Override
		public void run() {
			update();
		}
	}
	
	private final MutableString asyncText=new MutableString();
	protected final Object lock=new Object();
	private final Updater updater=new Updater();
	
	public AsyncMutableTextView(Context context) {
		super(context);
	}

	public AsyncMutableTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AsyncMutableTextView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean mutableTextEquals(CharSequence charSequence) {
		synchronized (lock) {
			return super.mutableTextEquals(charSequence);
		}
	}
	
	public boolean needsUpdate() {
		synchronized (lock) {
			return !mutableTextEquals(asyncText);
		}
	}
	
	public void postAsyncText(CharSequence charSequence) {
		setAsyncText(charSequence);
		postUpdate();
	}
	
	public void postUpdate() {
		if (needsUpdate()) {
			post(updater);
		}
	}

	public void setAsyncText(CharSequence charSequence) {
		synchronized (lock) {
			if (!Util.equals(asyncText, charSequence)) {
				asyncText.clear();
				asyncText.append(charSequence);
			}
		}
	}

	@Override
	public void setMutableText(CharSequence charSequence) {
		synchronized (lock) {
			setAsyncText(charSequence);
			super.setMutableText(charSequence);
		}
	}
	
	public void update() {
		synchronized (lock) {
			setMutableText(asyncText);
		}
	}
}
