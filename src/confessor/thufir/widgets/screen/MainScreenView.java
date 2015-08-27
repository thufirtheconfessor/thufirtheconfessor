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

public class MainScreenView extends OverlayScreenView {
	private class MoreButtonListener extends FactoryButtonListener {
		public MoreButtonListener(Env env, EnvScreenView view) {
			super(env, view);
		}
		
		@Override
		protected ScreenFactory factory() throws Throwable {
			return new MenuScreenFactory(env, push(), null);
		}
	}
	
	private class ReceiveButtonListener extends FactoryButtonListener {
		public ReceiveButtonListener(Env env, EnvScreenView view) {
			super(env, view);
		}
		
		@Override
		protected ScreenFactory factory() throws Throwable {
			return new ReceiveScreenFactory(env, push(), null);
		}
	}
	
	public MainScreenView(Env env, OverlayScreenFactory parent,
			Integer yScroll) throws Throwable {
		super(env, parent, yScroll);
		
		setButtons(
				button("Receive", true, new ReceiveButtonListener(env, this)),
				button("More", new MoreButtonListener(env, this)));
	}

	@Override
	public ImageStream imageStream() {
		return null;
	}
}
