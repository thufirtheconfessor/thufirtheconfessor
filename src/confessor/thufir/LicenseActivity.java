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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LicenseActivity extends Activity {
	private static final String KEY="license-seen";
	
	private static class OkButtonDrawable extends Drawable {
		private final int borderColor;
		private final Button button;
		private final Paint paint=new Paint();

		public OkButtonDrawable(int borderColor, Button button) {
			this.borderColor=borderColor;
			this.button=button;
		}
		
		@Override
		public void draw(Canvas canvas) {
			int width=button.getWidth();
			int height=button.getHeight();
			paint.reset();
			paint.setColor(Color.TRANSPARENT);
			canvas.drawPaint(paint);
			paint.setColor(borderColor);
			paint.setStyle(Paint.Style.STROKE);
			canvas.drawRect(0, 0, width-1, height-1, paint);
		}

		@Override
		public int getOpacity() {
			return PixelFormat.TRANSLUCENT;
		}

		@Override
		public void setAlpha(int alpha) {
		}

		@Override
		public void setColorFilter(ColorFilter cf) {
		}
	}

	@Override
	public void onBackPressed() {
		setContentView(new View(this));
		super.onBackPressed();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.license);
		TextView licenseText=(TextView)findViewById(R.id.licenseText);
		Button okButton=(Button)findViewById(R.id.okButton);
		okButton.setBackgroundDrawable(new OkButtonDrawable(
				licenseText.getCurrentTextColor(), okButton));
		okButton.setTextColor(licenseText.getCurrentTextColor());
	}
	
	public void onOkClick(View view) {
		setContentView(new View(this));
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences sharedPreferences=sharedPreferences(this);
		SharedPreferences.Editor editor=sharedPreferences.edit();
		editor.putBoolean(KEY, true);
		editor.apply();
	}
	
	public static SharedPreferences sharedPreferences(Context context) {
		return context.getSharedPreferences("license", Context.MODE_PRIVATE);
	}
	
	public static boolean showIfNotSeen(Context context) {
		SharedPreferences sharedPreferences=sharedPreferences(context);
		if (sharedPreferences.getBoolean(KEY, false)) {
			return false;
		}
		Intent intent=new Intent(context, LicenseActivity.class);
		context.startActivity(intent);
		return true;
	}
}
