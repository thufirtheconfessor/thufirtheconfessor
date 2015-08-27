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

import android.content.Intent;
import android.view.View;
import confessor.thufir.Env;
import confessor.thufir.ManualActivity;
import confessor.thufir.lib.stream.ImageStream;
import confessor.thufir.settings.SettingCategory;

public class MenuScreenView extends OverlayScreenView {
	private class CameraSettingsButtonListener extends FactoryButtonListener {
		public CameraSettingsButtonListener(Env env, EnvScreenView view) {
			super(env, view);
		}

		@Override
		protected ScreenFactory factory() throws Throwable {
			return new SettingsScreenFactory(
					SettingCategory.CAMERA, env, push(), null);
		}
	}
	
	private class CaptureButtonListener extends FactoryButtonListener {
		public CaptureButtonListener(Env env, EnvScreenView view) {
			super(env, view);
		}

		@Override
		protected ScreenFactory factory() throws Throwable {
			return new  CaptureScreenFactory(env, push(), null);
		}
	}
	
	private class DefaultSettingsButtonListener extends FactoryButtonListener {
		public DefaultSettingsButtonListener(Env env, EnvScreenView view) {
			super(env, view);
		}

		@Override
		protected ScreenFactory factory() throws Throwable {
			return new DefaultSettingsScreenFactory(env, push(), null);
		}
	}
	
	private class ManualButtonListener implements View.OnClickListener {
		@Override
		public void onClick(View view) {
			env.context().startActivity(
					new Intent(env.context(), ManualActivity.class));
		}
	}
	
	private class ProgramSettingsButtonListener extends FactoryButtonListener {
		public ProgramSettingsButtonListener(Env env, EnvScreenView view) {
			super(env, view);
		}

		@Override
		protected ScreenFactory factory() throws Throwable {
			return new SettingsScreenFactory(
					SettingCategory.PROGRAM, env, push(), null);
		}
	}
	
	private class TypeInButtonListener extends FactoryButtonListener {
		public TypeInButtonListener(Env env, EnvScreenView view) {
			super(env, view);
		}

		@Override
		protected ScreenFactory factory() throws Throwable {
			return new TypeInScreenFactory(env, push(), null);
		}
	}
	
	public MenuScreenView(Env env, OverlayScreenFactory parent,
			Integer yScroll) throws Throwable {
		super(env, parent, yScroll);
		setButtons(
				backButton(),
				button("Camera settings",
						new CameraSettingsButtonListener(env, this)),
				button("Program settings",
						new ProgramSettingsButtonListener(env, this)),
				button("Default settings",
						new DefaultSettingsButtonListener(env, this)),
				button("Capture",
						new CaptureButtonListener(env, this)),
				button("Manual", new ManualButtonListener()),
				button("Type-in", new TypeInButtonListener(env, this)));
	}

	@Override
	public ImageStream imageStream() {
		return null;
	}
}
