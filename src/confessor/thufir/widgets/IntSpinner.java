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
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import confessor.thufir.lib.Util;

public class IntSpinner extends LinearLayout {
	private class ButtonListener
			implements Runnable, View.OnKeyListener, View.OnTouchListener {
		private final int direction;
		private Long lastChange;
		private Long lastPress;

		public ButtonListener(int direction) {
			this.direction=direction;
		}

		@Override
		public boolean onKey(View view, int keyCode, KeyEvent ke) {
			if (KeyEvent.KEYCODE_DPAD_CENTER!=keyCode) {
				return false;
			}
			if (KeyEvent.ACTION_DOWN==ke.getAction()) {
				press();
			}
			else {
				release();
			}
			return true;
		}

		@Override
		public boolean onTouch(View view, MotionEvent me) {
			if (1!=me.getPointerCount()) {
				return false;
			}
			if (MotionEvent.ACTION_DOWN==me.getAction()) {
				press();
			}
			else if (MotionEvent.ACTION_MOVE!=me.getAction()) {
				release();
			}
			return true;
		}
		
		private void press() {
			lastPress=System.currentTimeMillis();
			lastChange=lastPress;
			run();
		}
		
		private void release() {
			Long lastChange2=lastChange;
			Long lastPress2=lastPress;
			lastChange=null;
			lastPress=null;
			if ((null!=lastPress2)
					&& (lastPress2.longValue()==lastChange2.longValue())) {
				listener.changed(direction, IntSpinner.this);
			}
		}
		
		@Override
		public void run() {
			if (null==lastPress) {
				return;
			}
			long now=System.currentTimeMillis();
			if (lastChange+250>now) {
				handler.postDelayed(this, lastChange+250-now);
				return;
			}
			lastChange=now;
			handler.postDelayed(this, 250);
			long time=now-lastPress;
			if (500>time) {
				return;
			}
			int change;
			if (1000>=time) {
				change=1;
			}
			else if (2000>=time) {
				change=2;
			}
			else if (3000>=time) {
				change=3;
			}
			else if (4000>=time) {
				change=5;
			}
			else {
				change=10;
			}
			change*=direction;
			listener.changed(change, IntSpinner.this);
		}
	}
	
	public interface Listener {
		void changed(int delta, IntSpinner spinner);
	}
	
	private final Handler handler;
	private final Listener listener;
	private final TextView textView;

	public IntSpinner(Context context, Handler handler, Listener listener) {
		super(context);
		this.handler=handler;
		this.listener=listener;
		
		setBackgroundDrawable(new BorderDrawable(this));
		setOrientation(LinearLayout.HORIZONTAL);
		
		ButtonListener minusListener=new ButtonListener(-1);
		Button minus=Widgets.button(context, "-", false, null, false);
		LinearLayout.LayoutParams params=new LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		minus.setLayoutParams(params);
		minus.setOnKeyListener(minusListener);
		minus.setOnTouchListener(minusListener);
		addView(minus);
		
		textView=Widgets.textView(context, "", false);
		params=new LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		params.leftMargin=1;
		params.rightMargin=1;
		textView.setLayoutParams(params);
		textView.setPadding(10, 0, 10, 0);
		addView(textView);
		
		ButtonListener plusListener=new ButtonListener(1);
		Button plus=Widgets.button(context, "+", false, null, false);
		params=new LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		plus.setLayoutParams(params);
		plus.setOnKeyListener(plusListener);
		plus.setOnTouchListener(plusListener);
		addView(plus);
		
		int width=Util.ceiling(Math.max(width(minus), width(plus)));
		minus.getLayoutParams().width=width;
		plus.getLayoutParams().width=width;
	}
	
	public void setText(String text) {
		textView.setText(text);
	}
	
	public void setWidth(String string) {
		LinearLayout.LayoutParams params
				=(LinearLayout.LayoutParams)textView.getLayoutParams();
		params.width=(int)Math.ceil(textView.getPaint().measureText(string))
				+textView.getPaddingLeft()+textView.getPaddingRight();
		requestLayout();
	}
	
	private float width(TextView textView) {
		CharSequence charSequence=textView.getText();
		return 3*textView.getPaint()
					.measureText(charSequence, 0, charSequence.length())
				+textView.getPaddingLeft()+textView.getPaddingRight();
	}
}
