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
package confessor.thufir;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ScrollView;
import confessor.thufir.lib.typein.Program;
import confessor.thufir.lib.typein.Type;
import confessor.thufir.lib.typein.Types;
import confessor.thufir.settings.SettingsManager;
import confessor.thufir.settings.typein.KeepScreenOnSetting;
import confessor.thufir.settings.typein.TextLayoutSetting;
import confessor.thufir.settings.typein.TextSizeSetting;
import confessor.thufir.settings.typein.VisibleWhitespacesSetting;
import confessor.thufir.widgets.LineWrapTextLayout;
import confessor.thufir.widgets.SourceView;
import confessor.thufir.widgets.TextLayout;

public class TypeInActivity extends Activity {
	public static final String SAVED="saved";
	
	private class OnTouch
			extends ScaleGestureDetector.SimpleOnScaleGestureListener
			implements View.OnTouchListener {
		private final ScaleGestureDetector detector;
		private float factor=1.0f;

		public OnTouch(Context context) {
			detector=new ScaleGestureDetector(context, this);
		}

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			factor*=detector.getScaleFactor();
			int scale=0;
			while (1.25f<=factor) {
				++scale;
				factor*=0.8;
			}
			while (0.8f>=factor) {
				--scale;
				factor*=1.25f;
			}
			onScale(scale);
			return true;
		}
		
		private void onScale(int scale) {
			if (0==scale) {
				return;
			}
			int textSize=sourceView.getTextSize();
			int index=TextSizeSetting.REVERSE_VALUES.get(textSize);
			index+=scale;
			if ((0<=index)
					&& (TextSizeSetting.VALUES.size()>index)) {
				textSize(TextSizeSetting.VALUES.get(index));
			}
		}
		
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			detector.onTouchEvent(motionEvent);
			return false;
		}
	}
	
	private class TextSizeListener implements DialogInterface.OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
			if ((0>which)
					|| (TextSizeSetting.VALUES.size()<=which)) {
				return;
			}
			textSize(TextSizeSetting.VALUES.get(which));
		}
	}
	
	private ScrollView scrollView;
	private SourceView sourceView;
	
	private boolean keepScreenOn() {
		return SettingsManager.sharedPreferences(this)
				.getBoolean(KeepScreenOnSetting.KEY,
						KeepScreenOnSetting.DEFAULT);
	}

	@Override
	public void onBackPressed() {
		setContentView(new View(this));
		super.onBackPressed();
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.typein);
		try {
			Intent intent=getIntent();
			if (null==intent) {
				finish();
				return;
			}
			String[] saved=intent.getStringArrayExtra(SAVED);
			if (null==saved) {
				finish();
				return;
			}
			Type type=Types.restoreType(saved, Files.types());
			if (null==type) {
				finish();
				return;
			}
			Program program=type.restore(saved);
			if (null==program) {
				finish();
				return;
			}
			scrollView=(ScrollView)findViewById(R.id.sourceCodeVScroll);
			sourceView=new SourceView(this, type, program);
			ViewGroup.LayoutParams params=new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			sourceView.setLayoutParams(params);
			sourceView.setTextLayout(textLayout());
			sourceView.setTextSize(textSize());
			sourceView.setVisibleWhitesapces(visibleWhitespaces());
			scrollView.addView(sourceView);
			scrollView.setOnTouchListener(new OnTouch(this));
			registerForContextMenu(scrollView);
			if (keepScreenOn()) {
				getWindow().addFlags(
						WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			}
		}
		catch (Throwable throwable) {
			AndroidErrorHandler.d(throwable);
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.typein, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		scrollView=null;
		sourceView=null;
		super.onDestroy();
	}
	
	private boolean onKeepScreenOnClick() {
		SharedPreferences sharedPreferences=
				SettingsManager.sharedPreferences(this);
		boolean keepScreenOn=!sharedPreferences
				.getBoolean(KeepScreenOnSetting.KEY,
						KeepScreenOnSetting.DEFAULT);
		SharedPreferences.Editor editor=sharedPreferences.edit();
		editor.putBoolean(KeepScreenOnSetting.KEY, keepScreenOn);
		editor.commit();
		if (keepScreenOn) {
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
		else {
			getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.keep_screen_on:
				return onKeepScreenOnClick();
			case R.id.text_layout:
				return onTextLayoutClick();
			case R.id.text_size:
				return onTextSizeClick();
			case R.id.visible_whitespaces:
				return onVisibleWhitespacesClick();
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.keep_screen_on).setTitle(
				keepScreenOn()
						?R.string.dont_keep_screen_on
						:R.string.keep_screen_on);
		menu.findItem(R.id.text_layout).setTitle(
				textLayout() instanceof LineWrapTextLayout
						?R.string.word_wrap
						:R.string.line_wrap);
		menu.findItem(R.id.visible_whitespaces).setTitle(
				visibleWhitespaces()
						?R.string.hide_whitespaces
						:R.string.show_whitespaces);
		return super.onPrepareOptionsMenu(menu);
	}
	
	private boolean onTextLayoutClick() {
		TextLayout textLayout=textLayout() instanceof LineWrapTextLayout
				?TextLayout.WORD_WRAP:TextLayout.LINE_WRAP;
		sourceView.setTextLayout(textLayout);
		SharedPreferences.Editor editor=SettingsManager
				.sharedPreferences(TypeInActivity.this).edit();
		editor.putString(TextLayoutSetting.KEY, textLayout.toString());
		editor.commit();
		return true;
	}
	
	private boolean onTextSizeClick() {
		AlertDialog.Builder builder=new AlertDialog.Builder(this);
		builder.setCancelable(true);
		String[] items=new String[TextSizeSetting.VALUES.size()];
		int index=-1;
		int textSize=textSize();
		for (int ii=items.length-1; 0<=ii; --ii) {
			int value=TextSizeSetting.VALUES.get(ii);
			items[ii]=Integer.toString(value);
			if (textSize==value) {
				index=ii;
			}
		}
		builder.setSingleChoiceItems(items, index, new TextSizeListener());
		builder.setNegativeButton(R.string.cancel, null);
		builder.setTitle(R.string.text_size);
		AlertDialog dialog=builder.create();
		dialog.show();
		return true;
	}
	
	private boolean onVisibleWhitespacesClick() {
		SharedPreferences sharedPreferences=
				SettingsManager.sharedPreferences(this);
		boolean visibleWhitespaces=!sharedPreferences
				.getBoolean(VisibleWhitespacesSetting.KEY,
						VisibleWhitespacesSetting.DEFAULT);
		SharedPreferences.Editor editor=sharedPreferences.edit();
		editor.putBoolean(VisibleWhitespacesSetting.KEY, visibleWhitespaces);
		editor.commit();
		sourceView.setVisibleWhitesapces(visibleWhitespaces);
		return true;
	}
	
	public static void startActivity(Context context, Type type,
			Program program) {
		Intent intent=new Intent(context, TypeInActivity.class);
		intent.putExtra(TypeInActivity.SAVED, type.save(program));
		context.startActivity(intent);
	}
	
	private TextLayout textLayout() {
		String string=SettingsManager.sharedPreferences(this)
				.getString(TextLayoutSetting.KEY, null);
		for (TextLayout textLayout: TextLayout.VALUES) {
			if (textLayout.toString().equals(string)) {
				return textLayout;
			}
		}
		return TextLayoutSetting.DEFAULT;
	}
	
	private int textSize() {
		String string=SettingsManager.sharedPreferences(this)
				.getString(TextSizeSetting.KEY, null);
		if (null!=string) {
			try {
				return Integer.parseInt(string);
			}
			catch (NumberFormatException ex) {
				AndroidErrorHandler.d(ex);
			}
		}
		return TextSizeSetting.DEFAULT;
	}
	
	private void textSize(int textSize) {
		sourceView.setTextSize(textSize);
		SharedPreferences.Editor editor=SettingsManager
				.sharedPreferences(TypeInActivity.this).edit();
		editor.putString(TextSizeSetting.KEY, Integer.toString(textSize));
		editor.commit();
	}
	
	private boolean visibleWhitespaces() {
		return SettingsManager.sharedPreferences(this)
				.getBoolean(VisibleWhitespacesSetting.KEY,
						VisibleWhitespacesSetting.DEFAULT);
	}
}
