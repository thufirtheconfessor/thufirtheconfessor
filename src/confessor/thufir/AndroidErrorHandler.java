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

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import confessor.thufir.lib.ClosedException;
import confessor.thufir.lib.stream.ErrorHandler;

public class AndroidErrorHandler implements ErrorHandler {
	public static final String LOG="thufir";
	
	private final Context context;
	private final Handler handler;

	public AndroidErrorHandler(Context context, Handler handler) {
		this.context=context;
		this.handler=handler;
	}
	
	public static void d(String message) {
		Log.d(LOG, message);
	}
	
	public static void d(String message, Throwable throwable) {
		Log.d(LOG, message, throwable);
	}
	
	public static void d(Throwable throwable) {
		Log.d(LOG, "", throwable);
	}
	
	@Override
	public void error(final Throwable throwable) {
		d(throwable);
		if (!(throwable instanceof ClosedException)) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(context, throwable.toString(),
								Toast.LENGTH_LONG)
							.show();
				}
			});
		}
	}
}
