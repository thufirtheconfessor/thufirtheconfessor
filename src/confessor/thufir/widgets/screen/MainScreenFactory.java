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
import java.util.List;

public class MainScreenFactory extends OverlayScreenFactory {
	public static final String TAG="main";

	public MainScreenFactory(Env env, Integer yScroll) {
		super(env, null, TAG, yScroll);
	}
	
	public static MainScreenFactory restore(List<String> bundle, Env env)
			throws Throwable {
		return new MainScreenFactory(env, restoreYScroll(bundle, 1));
	}

	@Override
	public ScreenView screenView(ScreenView currentScreenView)
			throws Throwable {
		return new MainScreenView(env, this, yScroll);
	}

	@Override
	public OverlayScreenFactory yScroll(Integer yScroll) {
		return new MainScreenFactory(env, yScroll);
	}
}
