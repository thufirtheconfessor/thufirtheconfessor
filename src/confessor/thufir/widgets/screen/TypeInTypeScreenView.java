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
import confessor.thufir.TypeInActivity;
import confessor.thufir.lib.stream.ImageStream;
import confessor.thufir.lib.typein.Program;
import confessor.thufir.lib.typein.Type;
import java.util.ArrayList;
import java.util.List;

public class TypeInTypeScreenView extends OverlayScreenView {
	private class ProgramButtonListener implements View.OnClickListener {
		private final Program program;

		public ProgramButtonListener(Program program) {
			this.program=program;
		}

		@Override
		public void onClick(View view) {
			TypeInActivity.startActivity(env.context(), type, program);
		}
	}
	
	private final Type type;
	
	public TypeInTypeScreenView(Env env, OverlayScreenFactory parent,
			Type type, Integer yScroll) throws Throwable {
		super(env, parent, yScroll);
		this.type=type;
		List<Program> programs=type.programs();
		List<View> buttons=new ArrayList<View>(programs.size()+1);
		buttons.add(backButton());
		for (Program program: programs) {
			buttons.add(button(program.name(),
					new ProgramButtonListener(program)));
		}
		setButtons(buttons);
	}

	@Override
	public ImageStream imageStream() {
		return null;
	}
}
