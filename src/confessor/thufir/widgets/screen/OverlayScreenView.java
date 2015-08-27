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
package confessor.thufir.widgets.screen;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import confessor.thufir.Env;
import confessor.thufir.widgets.OverlayLayout;
import confessor.thufir.widgets.Widgets;
import java.util.Arrays;
import java.util.List;

public abstract class OverlayScreenView extends EnvScreenView {
	public static final String BACK="Back";
	
	protected final OverlayLayout overlayLayout;
	protected final Integer yScroll;

	public OverlayScreenView(Env env, OverlayScreenFactory parent,
			Integer yScroll) throws Throwable {
		super(env, parent);
		this.yScroll=yScroll;
		overlayLayout=new OverlayLayout(env.context());
		overlayLayout.setButtonsPosition(env.settingsManager()
				.overlayButtonsPositionSetting()
				.getTyped(env.settingsManager()));
		FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		overlayLayout.setLayoutParams(params);
		overlayLayout.setOnTouchListeners(env.screen().background());
		
		overlayLayout.setDiagnostics(
				env.diagnosticsHolder().diagnosticsView());
	}
	
	protected Button backButton() {
		return button(BACK, true, new PopButtonListener(env, this));
	}
	
	protected Button button(String text, boolean focus,
			View.OnClickListener listener) {
		return Widgets.button(env.context(), text, false, listener, focus);
	}
	
	protected Button button(String text, View.OnClickListener listener) {
		return button(text, false, listener);
	}

	@Override
	protected ScreenFactory errorFactory(ScreenFactory parent,
			Throwable throwable) {
		return new MessageScreenFactory(env,
				new Message(true, throwable.toString()), parent);
	}

	public ScreenFactory push() throws Throwable {
		return ((OverlayScreenFactory)parent())
				.yScroll(overlayLayout.getButtonsYScroll());
	}

	protected void setButtons(List<View> buttons) {
		overlayLayout.setButtons(buttons, yScroll);
	}

	protected void setButtons(View... buttons) {
		setButtons(Arrays.asList(buttons));
	}

	protected void setContent(View content) {
		overlayLayout.setContent(content);
	}

	protected void setDiagnostics(View diagnostics) {
		overlayLayout.setDiagnostics(diagnostics);
	}
	
	protected TextView textView(String text) {
		TextView textView=Widgets.textView(env.context(), text, false);
		textView.setPadding(5, 5, 5, 5);
		return textView;
	}

	@Override
	public View view() {
		return overlayLayout;
	}
}
