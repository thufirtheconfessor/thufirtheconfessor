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
import confessor.thufir.Files;
import confessor.thufir.lib.typein.Type;
import java.util.List;

public class TypeInTypeScreenFactory extends OverlayScreenFactory {
	public static final String TAG="type-in-type";
	
	private final Type type;

	public TypeInTypeScreenFactory(Env env, ScreenFactory parent, Type type,
			Integer yScroll) {
		super(env, parent, TAG, yScroll);
		this.type=type;
	}
	
	public static TypeInTypeScreenFactory restore(List<String> bundle, Env env,
			ScreenFactory parent) throws Throwable {
		if (3>bundle.size()) {
			return null;
		}
		String typeName=bundle.get(2);
		Type type=null;
		for (Type type2: Files.types()) {
			if (type2.name().equals(typeName)) {
				type=type2;
				break;
			}
		}
		if (null==type) {
			return null;
		}
		return new TypeInTypeScreenFactory(env, parent, type,
				restoreYScroll(bundle, 1));
	}
	
	@Override
	public boolean save(List<String> bundle) throws Throwable {
		boolean result=super.save(bundle);
		if (result) {
			bundle.add(type.name());
		}
		return result;
	}

	@Override
	public ScreenView screenView(ScreenView currentView) throws Throwable {
		return new TypeInTypeScreenView(env, this, type, yScroll);
	}

	@Override
	public OverlayScreenFactory yScroll(Integer yScroll) {
		return new TypeInTypeScreenFactory(env, parent, type, yScroll);
	}
}
