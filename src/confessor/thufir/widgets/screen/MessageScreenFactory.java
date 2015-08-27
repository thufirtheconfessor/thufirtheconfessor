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

public class MessageScreenFactory extends OverlayScreenFactory {
	public static final String TAG="message";
	
	private final Message message;

	public MessageScreenFactory(Env env, Message message,
			ScreenFactory parent) {
		super(env, parent, TAG, null);
		this.message=message;
	}
	
	public static MessageScreenFactory restore(List<String> bundle, Env env,
			ScreenFactory parent) throws Throwable {
		Message message=Message.restore(bundle, 2);
		if (null==message) {
			return null;
		}
		return new MessageScreenFactory(env, message, parent);
	}

	@Override
	public boolean save(List<String> bundle) throws Throwable {
		boolean result=super.save(bundle);
		if (result) {
			message.save(bundle);
		}
		return result;
	}

	@Override
	public ScreenView screenView(ScreenView currentView) throws Throwable {
		return new MessageScreenView(env, message, this);
	}

	@Override
	public OverlayScreenFactory yScroll(Integer yScroll) {
		return this;
	}
}
