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
package confessor.thufir.widgets;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.view.View;
import confessor.thufir.lib.Colors;

public class FocusedBackgroundDrawable extends Drawable {
	private final int backgroundColor;
	private final int focusedColor;
	private final Paint paint=new Paint();
	private final View view;

	public FocusedBackgroundDrawable(int backgroundColor, int focusedColor,
			View view) {
		this.backgroundColor=backgroundColor;
		this.focusedColor=focusedColor;
		this.view=view;
	}

	public FocusedBackgroundDrawable(View view) {
		this(Colors.BACKGROUND, Colors.FOCUSED_BACKGROUND, view);
	}

	@Override
	public void draw(Canvas canvas) {
		paint.reset();
		if (view.isFocused()) {
			paint.setColor(focusedColor);
		}
		else {
			paint.setColor(backgroundColor);
		}
		canvas.drawPaint(paint);
	}

	@Override
	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}

	@Override
	public void setAlpha(int alpha) {
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
	}
}
