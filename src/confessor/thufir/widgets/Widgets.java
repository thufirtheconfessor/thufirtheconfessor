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
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.Button;
import android.widget.TextView;
import confessor.thufir.lib.Colors;

public class Widgets {
	private Widgets() {
	}
	
	public static AsyncMutableTextView asyncMutableTextView(Context context,
			String text, boolean transparent) {
		AsyncMutableTextView textView=new AsyncMutableTextView(context);
		initTextView(textView, null, labelBackground(transparent));
		if (null!=text) {
			textView.setMutableText(text);
		}
		return textView;
	}
	
	public static Button button(Context context, String text,
			boolean transparent, View.OnClickListener onClickListener,
			boolean focus) {
		Button button=new Button(context);
		initTextView(button, text, new FocusedBackgroundDrawable(
				transparent?Color.TRANSPARENT:Colors.BACKGROUND,
				Colors.FOCUSED_BACKGROUND,
				button));
		button.setSingleLine();
		if (null!=onClickListener) {
			button.setOnClickListener(onClickListener);
		}
		button.setPadding(
				button.getPaddingLeft()+20,
				button.getPaddingTop()+15,
				button.getPaddingRight()+20,
				button.getPaddingBottom()+15);
		if (focus) {
			button.requestFocus();
		}
		return button;
	}
	
	public static <T extends TextView> T buttonTextColor(T view,
			boolean selected, boolean error) {
		view.setTextColor(
				selected
						?(error
								?Colors.SELECTED_ERROR_TEXT
								:Colors.SELECTED_TEXT)
						:(error
								?Colors.ERROR_TEXT
								:Colors.TEXT));
		return view;
	}
	
	public static <T extends TextView> T initTextView(T view, String text,
			Drawable background) {
		if (null!=background) {
			view.setBackgroundDrawable(background);
		}
		view.setEllipsize(TextUtils.TruncateAt.END);
		view.setGravity(Gravity.CENTER);
		view.setPadding(0, 0, 0, 0);
		if (null!=text) {
			view.setText(text);
		}
		view.setTextColor(Colors.TEXT);
		return view;
	}
	
	public static Drawable labelBackground(boolean transparent) {
		return new ColorDrawable(
				transparent?Color.TRANSPARENT:Colors.BACKGROUND);
	}
	
	public static MutableTextView mutableTextView(Context context, String text,
			boolean transparent) {
		MutableTextView textView=new MutableTextView(context);
		initTextView(textView, null, labelBackground(transparent));
		if (null!=text) {
			textView.setMutableText(text);
		}
		return textView;
	}
	
	public static int resolve(int minimum, int value, int spec) {
		value=Math.max(minimum, value);
		switch (MeasureSpec.getMode(spec)) {
			case MeasureSpec.AT_MOST:
				value=Math.min(value, MeasureSpec.getSize(spec));
				break;
			case MeasureSpec.EXACTLY:
				value=MeasureSpec.getSize(spec);
				break;
			case MeasureSpec.UNSPECIFIED:
				break;
			default:
				throw new IllegalArgumentException(Integer.toString(spec));
		}
		return value;
	}
	
	public static TextView textView(Context context, String text,
			boolean transparent) {
		return initTextView(new TextView(context), text,
				labelBackground(transparent));
	}
}
