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
import android.os.Handler;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import confessor.thufir.lib.Colors;
import confessor.thufir.lib.Formatter;
import confessor.thufir.lib.MutableString;
import confessor.thufir.lib.Util;

public class ProgressView extends LinearLayout {
	private static final String DIGITS="0123456789";
	
	private class AsyncWidth extends AsyncInt {
		public AsyncWidth(Handler handler) {
			super(handler);
		}

		@Override
		protected void setValueImpl(int value) {
			tempString.clear();
			formatter.format(maxDigits(widestDigit(), value), tempString);
			int width=width(progressProgress, tempString);
			ViewGroup.LayoutParams params=progressProgress.getLayoutParams();
			if (params.width!=width) {
				params.width=width;
				requestLayout();
			}
		}

		private int widestDigit() {
			TextPaint paint=progressProgress.getPaint();
			char result=0;
			float width=-1;
			for (int ii=DIGITS.length()-1; 0<=ii; --ii) {
				float width2=paint.measureText(DIGITS, ii, ii+1);
				if (width2>width) {
					result=DIGITS.charAt(ii);
					width=width2;
				}
			}
			return result-'0';
		}
	}
	
	private final AsyncWidth asyncWidth;
	protected final Formatter formatter;
	protected final Object lock=new Object();
	private final AsyncMutableTextView message;
	private final AsyncProgressBar progressBar;
	private final AsyncMutableTextView progressMax;
	private final AsyncMutableTextView progressProgress;
	private final AsyncMutableTextView remainingTime;
	private final MutableString tempString=new MutableString();

	public ProgressView(Context context, Formatter formatter,
			Handler handler) {
		super(context);
		this.formatter=formatter;
		asyncWidth=new AsyncWidth(handler);
		
		setBackgroundDrawable(new ColorDrawable(Colors.BACKGROUND));
		setGravity(Gravity.CENTER);
		LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		setLayoutParams(params);
		setOrientation(LinearLayout.VERTICAL);
		
		LinearLayout inner=new LinearLayout(context);
		inner.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		params=new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		params.bottomMargin=2;
		params.leftMargin=2;
		params.rightMargin=2;
		params.topMargin=2;
		inner.setLayoutParams(params);
		inner.setOrientation(LinearLayout.VERTICAL);
		addView(inner);
		
		message=Widgets.asyncMutableTextView(context, null, true);
		params=new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity=Gravity.CENTER;
		params.leftMargin=2;
		params.rightMargin=2;
		params.topMargin=2;
		message.setLayoutParams(params);
		message.setSingleLine();
		inner.addView(message);
		
		LinearLayout progressLayout=new LinearLayout(context);
		progressLayout.setBackgroundDrawable(
				new ColorDrawable(Color.TRANSPARENT));
		params=new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity=Gravity.CENTER;
		params.leftMargin=2;
		params.rightMargin=2;
		params.topMargin=2;
		progressLayout.setLayoutParams(params);
		progressLayout.setOrientation(LinearLayout.HORIZONTAL);
		inner.addView(progressLayout);
		
		TextView progressLabel=Widgets.textView(context, "Progress:", true);
		params=new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		progressLabel.setLayoutParams(params);
		progressLayout.addView(progressLabel);
		
		progressProgress=Widgets.asyncMutableTextView(context, "-", true);
		progressProgress.setGravity(Gravity.CENTER_VERTICAL|Gravity.RIGHT);
		params=new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		params.leftMargin=3;
		progressProgress.setLayoutParams(params);
		progressLayout.addView(progressProgress);
		
		TextView separator=Widgets.textView(context, "/", true);
		params=new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		params.leftMargin=2;
		separator.setLayoutParams(params);
		progressLayout.addView(separator);
		
		progressMax=Widgets.asyncMutableTextView(context, "-", true);
		progressMax.setGravity(Gravity.CENTER);
		params=new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		params.leftMargin=2;
		progressMax.setLayoutParams(params);
		progressLayout.addView(progressMax);
		
		LinearLayout remainingTimeLayout=new LinearLayout(context);
		remainingTimeLayout.setBackgroundDrawable(
				new ColorDrawable(Color.TRANSPARENT));
		params=new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity=Gravity.CENTER;
		params.leftMargin=2;
		params.rightMargin=2;
		params.topMargin=2;
		remainingTimeLayout.setLayoutParams(params);
		remainingTimeLayout.setOrientation(LinearLayout.HORIZONTAL);
		inner.addView(remainingTimeLayout);
		
		TextView remainingTimeLabel=Widgets.textView(context,
				"Remaining time:", true);
		params=new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		remainingTimeLabel.setLayoutParams(params);
		remainingTimeLayout.addView(remainingTimeLabel);
		
		remainingTime=Widgets.asyncMutableTextView(context, "-", true);
		remainingTime.setGravity(Gravity.CENTER_VERTICAL|Gravity.RIGHT);
		params=new LinearLayout.LayoutParams(
				width(remainingTime, "100:00:00"),
				ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity=Gravity.CENTER;
		params.leftMargin=3;
		remainingTime.setLayoutParams(params);
		remainingTimeLayout.addView(remainingTime);
		
		progressBar=new AsyncProgressBar(context, null, Colors.BACKGROUND,
				Colors.TEXT);
		params=new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, 2);
		params.bottomMargin=2;
		params.leftMargin=2;
		params.rightMargin=2;
		params.topMargin=2;
		progressBar.setLayoutParams(params);
		inner.addView(progressBar);
	}
	
	private static int maxDigits(int digit, int max) {
		if (0>=max) {
			return digit;
		}
		int result=0;
		while (0<max) {
			result=10*result+digit;
			max/=10;
		}
		return result;
	}
	
	public void postMessage(CharSequence message) {
		this.message.postAsyncText(message);
	}
	
	public void postProgress(int progress, int max, float speed) {
		progressBar.postAsyncProgress(progress, max);
		synchronized (lock) {
			tempString.clear();
			this.progressMax.postAsyncText(
					formatter.format(max, tempString));
			tempString.clear();
			this.progressProgress.postAsyncText(
					formatter.format(progress, tempString));
			if (0.0f>=speed) {
				remainingTime.postAsyncText("-");
			}
			else {
				int seconds=(int)Math.ceil((max-progress)/speed);
				int minutes=seconds/60;
				seconds%=60;
				int hours=minutes/60;
				minutes%=60;
				tempString.clear();
				int minutesPad;
				if (0<hours) {
					formatter.format(hours, tempString);
					tempString.append(':');
					minutesPad=2;
				}
				else {
					minutesPad=1;
				}
				formatter.format(minutes, tempString, minutesPad);
				tempString.append(':');
				formatter.format(seconds, tempString, 2);
				remainingTime.postAsyncText(tempString);
			}
		}
	}
	
	public void postProgressWidth(int max) {
		asyncWidth.postAsyncValue(max);
	}
	
	private static int width(TextView textView, CharSequence charSequence) {
		return Util.ceiling(textView.getPaint()
				.measureText(charSequence, 0, charSequence.length()));
	}
}
