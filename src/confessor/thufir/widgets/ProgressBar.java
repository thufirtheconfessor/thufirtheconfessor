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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class ProgressBar extends View {
	public static final int DEFAULT_MAX=1;
	public static final int DEFAULT_PROGRESS=0;
	
	private final int backgroundColor;
	private final Integer borderColor;
	private final int foregroundColor;
	private final Paint paint=new Paint();
	private int max=DEFAULT_MAX;
	private int progress=DEFAULT_PROGRESS;

	public ProgressBar(Context context, Integer borderColor,
			int backgroundColor, int foregroundColor) {
		super(context);
		this.backgroundColor=backgroundColor;
		this.borderColor=borderColor;
		this.foregroundColor=foregroundColor;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		paint.reset();
		paint.setAntiAlias(false);
		
		paint.setColor(backgroundColor);
		canvas.drawPaint(paint);
		
		int xx=0;
		int yy=0;
		int ww=getWidth();
		int hh=getHeight();
		if (null!=borderColor) {
			paint.setColor(borderColor);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(0);
			canvas.drawRect(0, 0, ww-1, hh-1, paint);
			++xx;
			++yy;
			ww-=2;
			hh-=2;
		}
		ww=ww*progress/max;
		if ((0<ww)
				&& (0<hh)) {
			paint.setColor(foregroundColor);
			paint.setStyle(Paint.Style.FILL);
			canvas.drawRect(xx, yy, xx+ww, yy+hh, paint);
		}
	}
	
	public boolean progressEquals(int progress, int max) {
		return (this.max==max)
				&& (this.progress==progress);
	}
	
	public void setProgress(int progress, int max) {
		max=Math.max(1, max);
		progress=Math.max(0, Math.min(max, progress));
		if (!progressEquals(progress, max)) {
			this.max=max;
			this.progress=progress;
			invalidate();
		}
	}
}
