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
package confessor.thufir.diags;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import confessor.thufir.widgets.AsyncMutableTextView;
import confessor.thufir.widgets.Widgets;

public class DiagnosticsView extends LinearLayout {
	private final AsyncMutableTextView avgFrameView;
	private final AsyncMutableTextView avgProcView;
	private final AsyncMutableTextView brightnessView;
	private final AsyncMutableTextView brightnessClassView;
	private final AsyncMutableTextView contrastView;
	private final AsyncMutableTextView droppedFramesView;
	private final AsyncMutableTextView maxFrameView;
	private final AsyncMutableTextView maxProcView;
	private final AsyncMutableTextView minFrameView;
	private final AsyncMutableTextView minProcView;
	private final AsyncMutableTextView thresholdView;

	public DiagnosticsView(Context context) {
		super(context);
		
		setBackgroundDrawable(Widgets.labelBackground(false));
		setOrientation(LinearLayout.HORIZONTAL);
		
		LinearLayout verticalLayout0=new LinearLayout(context);
		verticalLayout0.setBackgroundDrawable(
				new ColorDrawable(Color.TRANSPARENT));
		verticalLayout0.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		params.rightMargin=3;
		verticalLayout0.setLayoutParams(params);
		addView(verticalLayout0);
		
		LinearLayout verticalLayout1=new LinearLayout(context);
		verticalLayout1.setBackgroundDrawable(
				new ColorDrawable(Color.TRANSPARENT));
		verticalLayout1.setOrientation(LinearLayout.VERTICAL);
		
		minFrameView=add("min. frame", verticalLayout0, verticalLayout1);
		avgFrameView=add("avg. frame", verticalLayout0, verticalLayout1);
		maxFrameView=add("max. frame", verticalLayout0, verticalLayout1);
		droppedFramesView=add("dropped frames", verticalLayout0, verticalLayout1);
		minProcView=add("min. proc.", verticalLayout0, verticalLayout1);
		avgProcView=add("avg. proc.", verticalLayout0, verticalLayout1);
		maxProcView=add("max. proc.", verticalLayout0, verticalLayout1);
		thresholdView=add("threshold", verticalLayout0, verticalLayout1);
		brightnessView=add("brightness", verticalLayout0, verticalLayout1);
		brightnessClassView=add("brightness", verticalLayout0, verticalLayout1);
		contrastView=add("contrast", verticalLayout0, verticalLayout1);
		params=new LinearLayout.LayoutParams(
				Math.round(minFrameView.getPaint().measureText("99999ms")),
				ViewGroup.LayoutParams.WRAP_CONTENT);
		verticalLayout1.setLayoutParams(params);
		addView(verticalLayout1);
	}
	
	private AsyncMutableTextView add(String title,
			LinearLayout verticalLayout0, LinearLayout verticalLayout1) {
		TextView label=Widgets.textView(getContext(), title, true);
		label.setGravity(Gravity.LEFT);
		LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.leftMargin=3;
		layoutParams.rightMargin=2;
		label.setLayoutParams(layoutParams);
		verticalLayout0.addView(label);
		AsyncMutableTextView textView=Widgets.initTextView(
				new AsyncMutableTextView(getContext()), "-",
				Widgets.labelBackground(true));
		layoutParams=new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.rightMargin=3;
		textView.setLayoutParams(layoutParams);
		textView.setGravity(Gravity.RIGHT);
		verticalLayout1.addView(textView);
		return textView;
	}

	public void avgFrame(CharSequence avgFrame) {
		avgFrameView.postAsyncText(avgFrame);
	}

	public void avgProc(CharSequence avgProc) {
		avgProcView.postAsyncText(avgProc);
	}

	public void brightness(CharSequence brightness) {
		brightnessView.postAsyncText(brightness);
	}

	public void brightnessClass(CharSequence brightnessClass) {
		brightnessClassView.postAsyncText(brightnessClass);
	}

	public void contrast(CharSequence contrast) {
		contrastView.postAsyncText(contrast);
	}

	public void droppedFrames(CharSequence droppedFrames) {
		droppedFramesView.postAsyncText(droppedFrames);
	}

	public void maxFrame(CharSequence maxFrame) {
		maxFrameView.postAsyncText(maxFrame);
	}

	public void maxProc(CharSequence maxProc) {
		maxProcView.postAsyncText(maxProc);
	}

	public void minFrame(CharSequence minFrame) {
		minFrameView.postAsyncText(minFrame);
	}

	public void minProc(CharSequence minProc) {
		minProcView.postAsyncText(minProc);
	}

	public void threshold(CharSequence threshold) {
		thresholdView.postAsyncText(threshold);
	}
}
