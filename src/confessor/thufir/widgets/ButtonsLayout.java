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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ButtonsLayout extends ViewGroup {
	public ButtonsLayout(Context context) {
		super(context);
		setBackgroundDrawable(new BorderDrawable(this));
	}
	
	public int ceiling(int height) {
		int size=getChildCount();
		if (0>=size) {
			return 0;
		}
		int h0;
		int h1=-1;
		for (int ii=0; size>ii; ++ii) {
			h0=h1;
			h1+=getChildAt(ii).getMeasuredHeight()+1;
			if (h1>height) {
				return h0;
			}
		}
		return h1;
	}
	
	public int floor(int height) {
		int size=getChildCount();
		if (0>=size) {
			return 0;
		}
		int h0;
		int h1=-1;
		for (int ii=size-1; 0<=ii; --ii) {
			h0=h1;
			h1+=getChildAt(ii).getMeasuredHeight()+1;
			if (h1>height) {
				return h0;
			}
		}
		return h1;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		int xx=0;
		for (int size=getChildCount(), ii=0; size>ii; ++ii) {
			View view=getChildAt(ii);
			view.layout(0, xx,
					view.getMeasuredWidth(), xx+view.getMeasuredHeight());
			xx+=view.getMeasuredHeight()+1;
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int size=getChildCount();
		
		int unspecified=MeasureSpec.makeMeasureSpec(
				0, MeasureSpec.UNSPECIFIED);
		int count=0;
		int width=0;
		for (int ii=size-1; 0<=ii; --ii) {
			View view=getChildAt(ii);
			if (View.VISIBLE!=view.getVisibility()) {
				continue;
			}
			++count;
			view.measure(unspecified, unspecified);
			width=Math.max(width, view.getMeasuredWidth());
		}
		width=Widgets.resolve(getSuggestedMinimumWidth(), width,
				widthMeasureSpec);
		
		if (0>=count) {
			setMeasuredDimension(
					resolveSize(getSuggestedMinimumWidth(),
							widthMeasureSpec),
					resolveSize(getSuggestedMinimumHeight(),
							heightMeasureSpec));
			return;
		}
		
		int buttonCount=0;
		int buttonHeight=0;
		int height=size-1;
		for (int ii=getChildCount()-1; 0<=ii; --ii) {
			View view=getChildAt(ii);
			if (View.VISIBLE!=view.getVisibility()) {
				continue;
			}
			view.measure(
					MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
					unspecified);
			if (view instanceof Button) {
				++buttonCount;
				buttonHeight=Math.max(buttonHeight, view.getMeasuredHeight());
			}
			else {
				height+=view.getMeasuredHeight();
			}
		}
		height+=buttonCount*buttonHeight;
		int specHeight=Widgets.resolve(getSuggestedMinimumHeight(), height,
				heightMeasureSpec);
		int extra=0;
		int remaining=0;
		if (specHeight>height) {
			extra=(specHeight-height)/count;
			remaining=(specHeight-height)%count;
			height=specHeight;
		}
		for (int ii=0; size>ii; ++ii) {
			View view=getChildAt(ii);
			if (View.VISIBLE!=view.getVisibility()) {
				continue;
			}
			int childHeight;
			if (view instanceof Button) {
				childHeight=buttonHeight;
			}
			else {
				childHeight=view.getMeasuredHeight();
			}
			childHeight+=extra;
			if (0<remaining) {
				++childHeight;
				--remaining;
			}
			view.measure(
					MeasureSpec.makeMeasureSpec(
							width, MeasureSpec.EXACTLY),
					MeasureSpec.makeMeasureSpec(
							childHeight, MeasureSpec.EXACTLY));
		}
		
		setMeasuredDimension(
				resolveSize(width, widthMeasureSpec),
				resolveSize(height, heightMeasureSpec));
	}
}
