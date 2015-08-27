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

public abstract class OverlayScreenFactory extends EnvScreenFactory {
	public final Integer yScroll;

	public OverlayScreenFactory(Env env, ScreenFactory parent, String tag,
			Integer yScroll) {
		super(env, parent, tag);
		this.yScroll=yScroll;
	}
	
	public static Integer restoreYScroll(List<String> bundle, int index)
			throws Throwable {
		if (bundle.size()<=index) {
			return null;
		}
		String ss=bundle.get(index);
		if (null==ss) {
			return null;
		}
		int ii=Integer.parseInt(ss);
		if (0>ii) {
			return null;
		}
		return ii;
	}

	@Override
	public boolean save(List<String> bundle) throws Throwable {
		boolean result=super.save(bundle);
		if (result) {
			bundle.add(saveYScroll(yScroll));
		}
		return result;
	}
	
	public static String saveYScroll(Integer yScroll) {
		return Integer.toString(null==yScroll?-1:yScroll);
	}
	
	public abstract OverlayScreenFactory yScroll(Integer yScroll);
}
