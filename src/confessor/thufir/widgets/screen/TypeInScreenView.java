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

import android.view.View;
import confessor.thufir.Env;
import confessor.thufir.Files;
import confessor.thufir.lib.stream.ImageStream;
import confessor.thufir.lib.typein.Program;
import confessor.thufir.lib.typein.Type;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class TypeInScreenView extends OverlayScreenView {
	private class SaveSources extends FactoryButtonListener {
		public SaveSources(Env env, EnvScreenView view) {
			super(env, view);
		}

		@Override
		protected ScreenFactory factory() throws Throwable {
			File sources=Files.sources();
			sources.mkdirs();
			byte[] buf=new byte[16384];
			for (Type type: Type.TYPES) {
				File typeDir=new File(sources, type.name());
				typeDir.mkdirs();
				for (Program program: type.programs()) {
					OutputStream os=new FileOutputStream(
							new File(typeDir, program.name()));
					try {
						InputStream is=program.source();
						try {
							for (int rr; 0<=(rr=is.read(buf)); ) {
								os.write(buf, 0, rr);
							}
						}
						finally {
							is.close();
						}
					}
					finally {
						os.close();
					}
				}
			}
			return new MessageScreenFactory(env,
					new Message(false,
						"All sources are saved to "+sources.getPath()+'.'),
					parent);
		}
	}
	
	private class TypeButtonListener extends FactoryButtonListener {
		private final Type type;

		public TypeButtonListener(Env env, Type type, EnvScreenView view) {
			super(env, view);
			this.type=type;
		}

		@Override
		protected ScreenFactory factory() throws Throwable {
			return new TypeInTypeScreenFactory(env, push(), type, null);
		}
	}
	
	public TypeInScreenView(Env env, OverlayScreenFactory parent,
			Integer yScroll) throws Throwable {
		super(env, parent, yScroll);
		List<Type> types=Files.types();
		List<View> buttons=new ArrayList<View>(types.size()+2);
		buttons.add(backButton());
		for (Type type: types) {
			buttons.add(button(type.displayName(),
					new TypeButtonListener(env, type, this)));
		}
		buttons.add(button("Save sources", new SaveSources(env, this)));
		setButtons(buttons);
	}

	@Override
	public ImageStream imageStream() {
		return null;
	}
}
