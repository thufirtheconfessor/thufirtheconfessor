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
package confessor.thufir.diags;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import confessor.thufir.Env;
import confessor.thufir.lib.AbstractCloseable;

public class DiagnosticsHolder extends AbstractCloseable {
	private Diagnostics diagnostics;
	private final Env env;

	public DiagnosticsHolder(Env env) {
		this.env=env;
	}

	@Override
	protected void closeImpl() throws Throwable {
		if (null!=diagnostics) {
			try {
				env.asyncImageStreamHolder().diagnostics(null);
			}
			finally {
				diagnostics=null;
			}
		}
	}
	
	public View diagnosticsView() throws Throwable {
		boolean on=env.settingsManager().diagnosticsSetting()
				.getTyped(env.settingsManager()).on;
		if (on) {
			if (null==diagnostics) {
				diagnostics=new Diagnostics(env);
				env.asyncImageStreamHolder().diagnostics(
						diagnostics.diagnosticsStream());
			}
			View result=diagnostics.diagnosticsView();
			ViewParent parent=result.getParent();
			if (parent instanceof ViewGroup) {
				((ViewGroup)parent).removeView(result);
			}
			return result;
		}
		else {
			closeImpl();
			return null;
		}
	}
}
