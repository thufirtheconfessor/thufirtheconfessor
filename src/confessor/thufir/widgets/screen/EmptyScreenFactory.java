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

import android.content.Context;

public class EmptyScreenFactory implements ScreenFactory {
	private final Context context;
	private final ScreenFactory parent;

	public EmptyScreenFactory(Context context, ScreenFactory parent) {
		this.context=context;
		this.parent=parent;
	}

	@Override
	public ScreenFactory parent() {
		return parent;
	}

	@Override
	public ScreenView screenView(ScreenView currentView) throws Throwable {
		return new EmptyScreenView(context, this);
	}
}
