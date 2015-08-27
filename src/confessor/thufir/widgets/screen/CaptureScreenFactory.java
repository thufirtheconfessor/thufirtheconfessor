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

public class CaptureScreenFactory extends OverlayScreenFactory {
	public static final String TAG="capture";

	public CaptureScreenFactory(Env env, ScreenFactory parent,
			Integer yScroll) {
		super(env, parent, TAG, yScroll);
	}
	
	public static CaptureScreenFactory restore(List<String> bundle,
			Env env, ScreenFactory parent) throws Throwable {
		return new CaptureScreenFactory(env, parent,
				restoreYScroll(bundle, 1));
	}

	@Override
	public ScreenView screenView(ScreenView currentView) throws Throwable {
		return new CaptureScreenView(env, this, yScroll);
	}

	@Override
	public OverlayScreenFactory yScroll(Integer yScroll) {
		return new CaptureScreenFactory(env, parent, yScroll);
	}
}
