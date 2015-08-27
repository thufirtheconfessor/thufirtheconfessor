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
package confessor.thufir;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import confessor.thufir.lib.AbstractCloseable;
import confessor.thufir.lib.stream.AsyncImageStreamHolder;

public class WakeLockStreamHolderListener extends AbstractCloseable
		implements AsyncImageStreamHolder.Listener {
	private static final String TAG="thufir";
	
	private class UserPresentReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			synchronized (abstractCloseableLock) {
				try {
					unregisterUserPresentReceiver();
				}
				finally {
					if (locked) {
						lock();
					}
				}
			}
		}
	}
	
	private final Context context;
	private boolean locked;
	private UserPresentReceiver userPresentReceiver;
	private PowerManager.WakeLock wakeLock;

	public WakeLockStreamHolderListener(Context context) {
		this.context=context;
	}

	@Override
	protected void closeImpl() {
		synchronized (abstractCloseableLock) {
			locked=false;
			try {
				unregisterUserPresentReceiver();
			}
			finally {
				if (null!=wakeLock) {
					try {
						wakeLock.release();
					}
					finally {
						wakeLock=null;
					}
				}
			}
		}
	}
	
	public static boolean isScreenInteractive(Context context) {
		return isScreenOn(context)
				&& (!isScreenLocked(context));
	}
	
	public static boolean isScreenLocked(Context context) {
		return ((KeyguardManager)context.getSystemService(
					Context.KEYGUARD_SERVICE))
				.inKeyguardRestrictedInputMode();
	}
	
	public static boolean isScreenOn(Context context) {
		return ((PowerManager)context.getSystemService(
						Context.POWER_SERVICE))
					.isScreenOn();
	}
	
	private void lock() {
		synchronized (abstractCloseableLock) {
			if (null==wakeLock) {
				if (!isScreenInteractive(context)) {
					registerUserPresentReceiver();
					return;
				}
				boolean success=false;
				wakeLock=((PowerManager)context.getSystemService(
								Context.POWER_SERVICE))
						.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
								TAG);
				try {
					wakeLock.acquire();
					success=true;
				}
				finally {
					if (!success) {
						closeImpl();
					}
				}
			}
		}
	}
	
	private void registerUserPresentReceiver() {
		unregisterUserPresentReceiver();
		userPresentReceiver=new UserPresentReceiver();
		context.registerReceiver(userPresentReceiver,
				new IntentFilter(Intent.ACTION_USER_PRESENT));
	}

	public void screenOn() {
		checkClosed();
		if (isScreenInteractive(context)) {
			PowerManager.WakeLock lock=
					((PowerManager)context.getSystemService(
						Context.POWER_SERVICE))
					.newWakeLock(
							PowerManager.ON_AFTER_RELEASE
							|PowerManager.SCREEN_DIM_WAKE_LOCK,
						TAG);
			lock.acquire();
			lock.release();
		}
	}

	@Override
	public void streamChanged(AsyncImageStreamHolder holder) {
		checkClosed();
		synchronized (abstractCloseableLock) {
			locked=holder.hasStream();
			if (locked) {
				lock();
			}
			else {
				closeImpl();
			}
		}
	}
	
	private void unregisterUserPresentReceiver() {
		synchronized (abstractCloseableLock) {
			if (null!=userPresentReceiver) {
				try {
					context.unregisterReceiver(userPresentReceiver);
				}
				finally {
					userPresentReceiver=null;
				}
			}
		}
	}
}
