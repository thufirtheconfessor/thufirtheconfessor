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
import android.util.AttributeSet;
import android.widget.ScrollView;

public class DelayedScrollView extends ScrollView {
	private int scrollX=-1;
	private int scrollY=-1;
	
	public DelayedScrollView(Context context) {
		super(context);
	}

	public DelayedScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DelayedScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void delayedScrollTo(int scrollX, int scrollY) {
		this.scrollX=scrollX;
		this.scrollY=scrollY;
		invalidate();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if ((-1!=scrollX)
				&& (-1!=scrollY)) {
			scrollTo(scrollX, scrollY);
			scrollX=-1;
			scrollY=-1;
		}
	}
}
