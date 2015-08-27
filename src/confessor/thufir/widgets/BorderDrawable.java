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
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import confessor.thufir.lib.Colors;
import confessor.thufir.lib.Rect;
import confessor.thufir.lib.Rects;
import java.util.ArrayList;
import java.util.List;

public class BorderDrawable extends Drawable {
	protected final int backgroundColor;
	protected final int borderColor;
	private Rect clip;
	private Rects lines;
	protected final Paint paint=new Paint();
	protected final ViewGroup parent;
	private final List<Rect> views=new ArrayList<Rect>();

	public BorderDrawable(int backgroundColor, int borderColor,
			ViewGroup parent) {
		this.backgroundColor=backgroundColor;
		this.borderColor=borderColor;
		this.parent=parent;
	}

	public BorderDrawable(ViewGroup parent) {
		this(Color.TRANSPARENT, Colors.BORDER, parent);
	}

	@Override
	public void draw(Canvas canvas) {
		int width=parent.getWidth();
		int height=parent.getHeight();
		if ((null==clip)
				|| (clip.width!=width)
				|| (clip.height!=height)) {
			clip=Rect.create(0, 0, width, height);
		}
		int oo=0;
		for (int ii=parent.getChildCount()-1; 0<=ii; --ii) {
			View view=parent.getChildAt(ii);
			if (View.VISIBLE!=view.getVisibility()) {
				continue;
			}
			int x0=view.getLeft();
			int y0=view.getTop();
			int x1=view.getRight();
			int y1=view.getBottom();
			if ((x0>=x1)
					|| (y0>=y1)) {
				continue;
			}
			if (views.size()<=oo) {
				views.add(Rect.create(x0, y0, x1, y1));
				lines=null;
			}
			else {
				Rect rect=views.get(oo);
				if ((rect.x0!=x0)
						|| (rect.y0!=y0)
						|| (rect.x1!=x1)
						|| (rect.y1!=y1)) {
					views.set(oo, Rect.create(x0, y0, x1, y1));
					lines=null;
				}
			}
			++oo;
		}
		while (oo<views.size()) {
			views.remove(views.size()-1);
			lines=null;
		}
		if (null==lines) {
			Rects rects=new Rects();
			for (int ii=views.size()-1; 0<=ii; --ii) {
				rects.addRemainingBorders(
						views.get(ii), true, true, true, true);
			}
			rects.intersect(clip);
			for (int ii=views.size()-1; 0<=ii; --ii) {
				rects.remove(views.get(ii));
			}
			rects.join();
			lines=rects;
		}
		paint.reset();
		paint.setColor(backgroundColor);
		canvas.drawPaint(paint);
		paint.setColor(borderColor);
		paint.setStyle(Paint.Style.FILL);
		for (int ii=lines.size()-1; 0<=ii; --ii) {
			Rect rect=lines.get(ii);
			canvas.drawRect(rect.x0, rect.y0, rect.x1, rect.y1, paint);
		}
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
