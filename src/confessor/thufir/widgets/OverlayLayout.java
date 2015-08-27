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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import confessor.thufir.widgets.screen.ScreenBackground;
import java.util.ArrayList;
import java.util.List;

public class OverlayLayout extends ViewGroup {
	private final List<View> buttons=new ArrayList<View>();
	private OverlayButtonsPosition buttonsPosition
			=OverlayButtonsPosition.TOP_LEFT;
	private final DelayedScrollView buttonsScroll;
	private final ButtonsLayout buttonsLayout;
	private View content;
	private View diagnostics;
	private final ScrollView diagnosticsScroll;
	
	public OverlayLayout(Context context) {
		this(context, null, 0);
	}

	public OverlayLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public OverlayLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		setBackgroundDrawable(new BorderDrawable(this));
		
		buttonsScroll=new DelayedScrollView(context);
		buttonsScroll.setBackgroundDrawable(
				new ColorDrawable(Color.TRANSPARENT));
		buttonsScroll.setFillViewport(true);
		buttonsScroll.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));
		/*buttonsScroll.setOnTouchListener(new ScrollViewTouchListener(
				buttonsScroll, overlayDrawable));*/
		addView(buttonsScroll);
		buttonsLayout=new ButtonsLayout(getContext());
		buttonsLayout.setLayoutParams(new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
		buttonsScroll.addView(buttonsLayout);
		
		diagnosticsScroll=new ScrollView(context);
		diagnosticsScroll.setBackgroundDrawable(
				new ColorDrawable(Color.TRANSPARENT));
		diagnosticsScroll.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));
		/*diagnosticsScroll.setOnTouchListener(new ScrollViewTouchListener(
				diagnosticsScroll, overlayDrawable));*/
		addView(diagnosticsScroll);
	}
	
	public void clearButtons() {
		if (!buttons.isEmpty()) {
			buttonsLayout.removeAllViews();
			buttons.clear();
			requestLayout();
		}
	}
	
	public void clearContent() {
		if (null!=content) {
			removeView(content);
			content=null;
			requestLayout();
		}
	}
	
	public void clearDiagnostics() {
		if (null!=diagnostics) {
			diagnosticsScroll.removeAllViews();
			diagnostics=null;
			requestLayout();
		}
	}
	
	public Integer getButtonsYScroll() {
		if (buttons.isEmpty()) {
			return null;
		}
		return buttonsScroll.getScrollY();
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		int hh=Math.max(0, bottom-top);
		int ww=Math.max(0, right-left);
		int bh=buttonsScroll.getMeasuredHeight();
		int bw=buttonsScroll.getMeasuredWidth();
		int ch, cw;
		if (null==content) {
			ch=0;
			cw=0;
		}
		else {
			ch=content.getMeasuredHeight();
			cw=content.getMeasuredWidth();
		}
		int dh=diagnosticsScroll.getMeasuredHeight();
		int dw=diagnosticsScroll.getMeasuredWidth();
		
		if ((0<bh)
				&& (0<bw)) {
			buttonsScroll.setVisibility(View.VISIBLE);
			int xx=startCoordinate(bw, ww, buttonsPosition.left());
			int yy=startCoordinate(bh, hh, buttonsPosition.top());
			buttonsScroll.layout(xx, yy, xx+bw, yy+bh);
		}
		else {
			buttonsScroll.setVisibility(View.INVISIBLE);
		}
		
		if (null!=content) {
			int xx=startCoordinate(cw, ww, buttonsPosition.right());
			int yy=startCoordinate(ch, hh, buttonsPosition.top());
			content.layout(xx, yy, xx+cw, yy+ch);
		}
		
		if ((0<dh)
				&& (0<dw)) {
			diagnosticsScroll.setVisibility(View.VISIBLE);
			int xx=startCoordinate(dw, ww, buttonsPosition.right());
			int yy=startCoordinate(dh, hh, buttonsPosition.bottom());
			diagnosticsScroll.layout(xx, yy, xx+dw, yy+dh);
		}
		else {
			diagnosticsScroll.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int unspecified=MeasureSpec.makeMeasureSpec(
				0, MeasureSpec.UNSPECIFIED);
		int bh, bw, ch, cw, dh, dw;
		buttonsScroll.measure(unspecified, unspecified);
		bh=buttonsScroll.getMeasuredHeight();
		bw=buttonsScroll.getMeasuredWidth();
		if (null==content) {
			ch=0;
			cw=0;
		}
		else {
			content.measure(unspecified, unspecified);
			ch=content.getMeasuredHeight();
			cw=content.getMeasuredWidth();
		}
		diagnosticsScroll.measure(unspecified, unspecified);
		dh=diagnosticsScroll.getMeasuredHeight();
		dw=diagnosticsScroll.getMeasuredWidth();
		
		int height=Math.max(bh, ch+1+dh);
		int width=Math.max(cw, dw)+1+bw;
		height=Widgets.resolve(getSuggestedMinimumHeight(), height,
				heightMeasureSpec);
		width=Widgets.resolve(getSuggestedMinimumWidth(), width,
				widthMeasureSpec);
		
		bw=Math.min(bw, width/2);
		buttonsScroll.measure(
				MeasureSpec.makeMeasureSpec(bw, MeasureSpec.EXACTLY),
				unspecified);
		bh=Math.min(buttonsScroll.getMeasuredHeight(), height);
		
		if (null!=content) {
			cw=width-1-bw;
			content.measure(
					MeasureSpec.makeMeasureSpec(cw, MeasureSpec.EXACTLY),
					unspecified);
			ch=Math.min(ch, height/2);
		}
		
		if ((0<ch)
				&& (0<cw)
				&& (0<bh)
				&& (0<bw)) {
			if (ch>bh) {
				bh=ch;
			}
			else if (ch<bh) {
				if (buttonsPosition.top()) {
					ch=buttonsLayout.ceiling(height/2);
				}
				else {
					ch=buttonsLayout.floor(height/2);
				}
			}
		}
		
		dw=Math.min(dw, width-1-bw);
		diagnosticsScroll.measure(
				MeasureSpec.makeMeasureSpec(dw, MeasureSpec.EXACTLY),
				unspecified);
		dh=Math.min(diagnosticsScroll.getMeasuredHeight(), height-1-ch);
		
		buttonsScroll.measure(
				MeasureSpec.makeMeasureSpec(bw, MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec(bh, MeasureSpec.EXACTLY));
		if (null!=content) {
			content.measure(
					MeasureSpec.makeMeasureSpec(cw, MeasureSpec.EXACTLY),
					MeasureSpec.makeMeasureSpec(ch, MeasureSpec.EXACTLY));
		}
		diagnosticsScroll.measure(
				MeasureSpec.makeMeasureSpec(dw, MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec(dh, MeasureSpec.EXACTLY));
		
		setMeasuredDimension(
				resolveSize(width, widthMeasureSpec),
				resolveSize(height, heightMeasureSpec));
	}
	
	public void setButtons(List<View> buttons, Integer yScroll) {
		clearButtons();
		if (!buttons.isEmpty()) {
			int size=buttons.size();
			for (int ii=0; size>ii; ++ii) {
				View view=buttons.get(ii);
				LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				if (0<ii) {
					params.topMargin=1;
				}
				if (view instanceof Button) {
					params.weight=1;
				}
				view.setLayoutParams(params);
				buttonsLayout.addView(view);
				this.buttons.add(view);
			}
			if (null!=yScroll) {
				buttonsScroll.delayedScrollTo(0, yScroll);
			}
			requestLayout();
		}
	}

	public void setButtonsPosition(OverlayButtonsPosition buttonsPosition) {
		if (this.buttonsPosition!=buttonsPosition) {
			this.buttonsPosition=buttonsPosition;
			requestLayout();
		}
	}
	
	public void setContent(View content) {
		clearContent();
		if (null!=content) {
			ViewGroup.LayoutParams params=new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			content.setLayoutParams(params);
			addView(content);
			this.content=content;
			requestLayout();
		}
	}
	
	public void setDiagnostics(View diagnostics) {
		clearDiagnostics();
		if (null!=diagnostics) {
			diagnostics.setLayoutParams(new FrameLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));
			diagnosticsScroll.addView(diagnostics);
			this.diagnostics=diagnostics;
			requestLayout();
		}
	}

	public void setOnTouchListeners(ScreenBackground background) {
		buttonsScroll.setOnTouchListener(
				background.onTouchListener(buttonsScroll));
		diagnosticsScroll.setOnTouchListener(
				background.onTouchListener(diagnosticsScroll));
	}
	
	private static int startCoordinate(int child, int parent, boolean origin) {
		if (origin) {
			return 0;
		}
		else {
			return parent-child;
		}
	}
}
