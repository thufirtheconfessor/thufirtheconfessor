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

import confessor.thufir.AndroidErrorHandler;
import confessor.thufir.Env;
import confessor.thufir.lib.AbstractCloseable;
import confessor.thufir.lib.ClosedException;
import confessor.thufir.lib.SubCloseables;
import confessor.thufir.lib.thread.JobException;

public abstract class EnvScreenView extends AbstractCloseable
		implements ScreenView {
	protected final Env env;
	protected final ScreenFactory parent;
	protected final SubCloseables subCloseables;

	public EnvScreenView(Env env, ScreenFactory parent) {
		this.env=env;
		this.parent=parent;
		subCloseables=new SubCloseables(env.closeables());
	}

	@Override
	protected void closeImpl() throws Throwable {
		subCloseables.close();
	}

	@Override
	public void error(Throwable throwable) {
		AndroidErrorHandler.d(throwable);
		try {
			if (throwable instanceof JobException) {
				throwable=throwable.getCause();
			}
			if (throwable instanceof ClosedException) {
				push(parent);
			}
			else {
				push(errorFactory(parent, throwable));
			}
		}
		catch (Throwable throwable2) {
			AndroidErrorHandler.d(throwable2);
		}
	}
	
	protected abstract ScreenFactory errorFactory(ScreenFactory parent,
			Throwable throwable);

	@Override
	public ScreenFactory parent() {
		return parent;
	}
	
	public ScreenFactory pop() {
		return parent().parent();
	}
	
	public void push(ScreenFactory factory) throws Throwable {
		env.screen().postFactory(new PushScreenFactory(factory, this));
	}

	@Override
	public boolean screenOn() {
		return true;
	}
}
