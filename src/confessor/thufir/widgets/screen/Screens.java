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

import android.os.Bundle;
import confessor.thufir.Env;
import java.util.ArrayList;
import java.util.List;

public class Screens {
	private static final String KEY="saved-screen-";
	
	private Screens() {
	}
	
	public static List<List<String>> restore(Bundle bundle) throws Throwable {
		if (null==bundle) {
			return null;
		}
		List<List<String>> savedScreen=new ArrayList<List<String>>();
		for (int ii=0; ; ) {
			String sizeString=bundle.getString(KEY+(ii++));
			if (null==sizeString) {
				break;
			}
			int size=Integer.parseInt(sizeString);
			List<String> bundle2=new ArrayList<String>(size);
			for (; 0<size; --size,++ii) {
				bundle2.add(bundle.getString(KEY+ii));
			}
			savedScreen.add(bundle2);
		}
		return savedScreen;
	}
	
	public static ScreenFactory restore(Env env,
			List<List<String>> savedScreen) throws Throwable {
		if (null==savedScreen) {
			return null;
		}
		ScreenFactory result=null;
		for (int ii=0; savedScreen.size()>ii; ++ii) {
			List<String> bundle=savedScreen.get(ii);
			if (bundle.isEmpty()) {
				break;
			}
			String tag=bundle.get(0);
			ScreenFactory next;
			if (CaptureScreenFactory.TAG.equals(tag)) {
				next=CaptureScreenFactory.restore(bundle, env, result);
			}
			else if (DefaultSettingsScreenFactory.TAG.equals(tag)) {
				next=DefaultSettingsScreenFactory.restore(bundle, env, result);
			}
			else if (MainScreenFactory.TAG.equals(tag)) {
				next=MainScreenFactory.restore(bundle, env);
			}
			else if (MenuScreenFactory.TAG.equals(tag)) {
				next=MenuScreenFactory.restore(bundle, env, result);
			}
			else if (MessageScreenFactory.TAG.equals(tag)) {
				next=MessageScreenFactory.restore(bundle, env, result);
			}
			else if (ReceiveScreenFactory.TAG.equals(tag)) {
				next=ReceiveScreenFactory.restore(bundle, env, result);
			}
			else if (SettingScreenFactory.TAG.equals(tag)) {
				next=SettingScreenFactory.restore(bundle, env, result);
			}
			else if (SettingsScreenFactory.TAG.equals(tag)) {
				next=SettingsScreenFactory.restore(bundle, env, result);
			}
			else if (TypeInScreenFactory.TAG.equals(tag)) {
				next=TypeInScreenFactory.restore(bundle, env, result);
			}
			else if (TypeInTypeScreenFactory.TAG.equals(tag)) {
				next=TypeInTypeScreenFactory.restore(bundle, env, result);
			}
			else {
				break;
			}
			if (null==next) {
				break;
			}
			result=next;
		}
		return result;
	}
	
	public static void save(Bundle bundle, List<List<String>> savedScreen) {
		if (null==savedScreen) {
			return;
		}
		for (int ii=0, oo=0; savedScreen.size()>ii; ++ii) {
			List<String> bundle2=savedScreen.get(ii);
			bundle.putString(KEY+(oo++), Integer.toString(bundle2.size()));
			for (int jj=0; bundle2.size()>jj; ++jj,++oo) {
				bundle.putString(KEY+oo, bundle2.get(jj));
			}
		}
	}
	
	public static List<List<String>> save(Env env) throws Throwable {
		List<List<String>> savedScreen=new ArrayList<List<String>>();
		save(savedScreen, env.screen().push());
		return savedScreen;
	}
	
	private static boolean save(List<List<String>> savedScreen,
			ScreenFactory factory) throws Throwable {
		if (null==factory) {
			return true;
		}
		if (!save(savedScreen, factory.parent())) {
			return false;
		}
		if (!(factory instanceof EnvScreenFactory)) {
			return false;
		}
		ArrayList<String> bundle=new ArrayList<String>();
		if (!((EnvScreenFactory)factory).save(bundle)) {
			return false;
		}
		savedScreen.add(bundle);
		return true;
	}
}
