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
package confessor.thufir.widgets.screen;

import confessor.thufir.Env;
import confessor.thufir.lib.stream.ImageStream;

public class DefaultSettingsScreenView extends OverlayScreenView {
	private class RestoreButtonListener extends PopButtonListener {
		public RestoreButtonListener(Env env, EnvScreenView view) {
			super(env, view);
		}

		@Override
		protected ScreenFactory factory() throws Throwable {
			env.settingsManager().reset();
			env.cameraHolder().reloadCamera(env.settingsManager());
			return super.factory();
		}
	}
	
	public DefaultSettingsScreenView(Env env, OverlayScreenFactory parent,
			Integer yScroll) throws Throwable {
		super(env, parent, yScroll);
		setButtons(
				button("Cancel", true, new PopButtonListener(env, this)),
				button("Restore", new RestoreButtonListener(env, this)));
		setContent(textView("Restore default settings?"));
	}

	@Override
	public ImageStream imageStream() {
		return null;
	}
}
