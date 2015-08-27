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
package confessor.thufir.settings;

import android.content.SharedPreferences;
import android.view.View;
import confessor.thufir.CameraHolder;
import confessor.thufir.Env;
import confessor.thufir.lib.stream.ImageStream;
import confessor.thufir.widgets.Widgets;
import confessor.thufir.widgets.screen.EnvScreenView;
import confessor.thufir.widgets.screen.OverlayScreenFactory;
import confessor.thufir.widgets.screen.OverlayScreenView;
import confessor.thufir.widgets.screen.PopButtonListener;
import confessor.thufir.widgets.screen.ScreenFactory;
import confessor.thufir.widgets.screen.ScreenView;
import java.util.ArrayList;
import java.util.List;

public abstract class TypedListSetting<T> extends TypedSetting<T> {
	private class ValueButtonListener extends PopButtonListener {
		private final T value;

		public ValueButtonListener(Env env, T value, EnvScreenView view) {
			super(env, view);
			this.value=value;
		}

		@Override
		protected ScreenFactory factory() throws Throwable {
			env.settingsManager().set(
					env.settingsManager().get()
					.setting(TypedListSetting.this, value));
			if (reloadCamera()) {
				env.cameraHolder().reloadCamera(env.settingsManager());
			}
			return super.factory();
		}
	}
	
	private class ScreenViewImpl extends OverlayScreenView {
		public ScreenViewImpl(Env env, OverlayScreenFactory parent,
				Integer yScroll) throws Throwable {
			super(env, parent, yScroll);
			Settings settings=env.settingsManager().get();
			T selected=getTyped(settings);
			List<T> values=values(env.cameraHolder(), settings,
					env.settingsManager());
			List<View> buttons=new ArrayList<View>(values.size()+2);
			buttons.add(backButton());
			buttons.add(textView(title()));
			for (T value: values) {
				buttons.add(Widgets.buttonTextColor(
						button(value.toString(),
								new ValueButtonListener(env, value, this)),
						selected.toString().equals(value.toString()),
						suspicious(value)));
			}
			setButtons(buttons);
		}

		@Override
		public ImageStream imageStream() {
			return null;
		}
	}
	
	protected final String key;
	
	public TypedListSetting(String key, Class<T> type) {
		super(type);
		this.key=key;
	}

	@Override
	public T defaultValueTyped(CameraHolder cameraHolder, Settings settings,
			SettingsManager settingsManager) {
		T value=defaultValueTypedImpl(cameraHolder, settings, settingsManager);
		if (validTyped(cameraHolder, settings, settingsManager, value)) {
			return value;
		}
		List<T> values=values(cameraHolder, settings, settingsManager);
		if ((null==value)
				|| values.isEmpty()) {
			return value;
		}
		return values.get(0);
	}
	
	protected abstract T defaultValueTypedImpl(CameraHolder cameraHolder,
			Settings settings, SettingsManager settingsManager);

	@Override
	public T getTyped(CameraHolder cameraHolder, SettingsMap map,
			Settings settings, SettingsManager settingsManager) {
		String value=map.getString(null, key);
		if (null!=value) {
			for (T value2: values(cameraHolder, settings, settingsManager)) {
				if (value2.toString().equals(value)) {
					return value2;
				}
			}
		}
		return defaultValueTyped(cameraHolder, settings, settingsManager);
	}

	@Override
	public void putTyped(SharedPreferences.Editor editor, T value) {
		editor.putString(key, value.toString());
	}

	@Override
	public ScreenView screenView(Env env, OverlayScreenFactory parent,
			Integer yScroll) throws Throwable {
		return new ScreenViewImpl(env, parent, yScroll);
	}
	
	protected boolean suspicious(T value) {
		return false;
	}

	@Override
	public boolean valid(CameraHolder cameraHolder, Settings settings,
			SettingsManager settingsManager) {
		List<T> values=values(cameraHolder, settings, settingsManager);
		return (null!=values)
				&& (!values.isEmpty());
	}
	
	@Override
	public boolean validTyped(CameraHolder cameraHolder, Settings settings,
			SettingsManager settingsManager, T value) {
		for (T value2: values(cameraHolder, settings, settingsManager)) {
			if (value2.toString().equals(value.toString())) {
				return true;
			}
		}
		return false;
	}
	
	protected abstract List<T> values(CameraHolder cameraHolder,
			Settings settings, SettingsManager settingsManager);
}
