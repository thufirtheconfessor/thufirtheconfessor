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
import confessor.thufir.lib.Closeable;
import confessor.thufir.lib.stream.ErrorHandler;
import confessor.thufir.lib.stream.ImageStream;

public interface ScreenView extends Closeable, ErrorHandler {
	ImageStream imageStream();
	ScreenFactory parent();
	ScreenFactory push() throws Throwable;
	boolean screenOn();
	View view();
}
