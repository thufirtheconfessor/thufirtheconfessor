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
import android.view.View;
import confessor.thufir.AndroidErrorHandler;
import confessor.thufir.lib.AbstractCloseable;
import confessor.thufir.lib.stream.ImageStream;

public class EmptyScreenView extends AbstractCloseable implements ScreenView {
	private final ScreenFactory parent;
	private final View view;

	public EmptyScreenView(Context context, ScreenFactory parent) {
		this.parent=parent;
		view=new View(context);
	}

	@Override
	protected void closeImpl() throws Throwable {
	}

	@Override
	public void error(Throwable throwable) {
		AndroidErrorHandler.d(throwable);
	}

	@Override
	public ImageStream imageStream() {
		return null;
	}

	@Override
	public ScreenFactory parent() {
		return parent;
	}

	@Override
	public ScreenFactory push() throws Throwable {
		return parent;
	}

	@Override
	public boolean screenOn() {
		return false;
	}

	@Override
	public View view() {
		return view;
	}
}
