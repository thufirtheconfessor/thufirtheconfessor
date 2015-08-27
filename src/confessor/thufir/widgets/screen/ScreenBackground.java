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

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import confessor.thufir.lib.Colors;
import confessor.thufir.lib.Matrix3fs;
import confessor.thufir.lib.stream.AsyncImageStreamHolder;

public class ScreenBackground extends Drawable
		implements View.OnTouchListener {
	private class CanvasImpl implements confessor.thufir.lib.Canvas {
		public Canvas canvas;
		private final float[] temp30=new float[3];
		private final float[] temp31=new float[3];
		public float[] transform;

		@Override
		public void drawCircle(float xx, float yy, float radius) {
			temp30[0]=xx-radius;
			temp30[1]=yy-radius;
			temp30[2]=1f;
			Matrix3fs.multiplyMatrixVector(transform, temp30, temp31);
			rect.left=temp31[0];
			rect.top=temp31[1];
			temp30[0]=xx+radius;
			temp30[1]=yy+radius;
			temp30[2]=1f;
			Matrix3fs.multiplyMatrixVector(transform, temp30, temp31);
			rect.right=temp31[0];
			rect.bottom=temp31[1];
			canvas.drawOval(rect, paint);
		}

		@Override
		public void drawLine(float x0, float y0, float x1, float y1) {
			temp30[0]=x0;
			temp30[1]=y0;
			temp30[2]=1f;
			Matrix3fs.multiplyMatrixVector(transform, temp30, temp31);
			x0=temp31[0];
			y0=temp31[1];
			temp30[0]=x1;
			temp30[1]=y1;
			temp30[2]=1f;
			Matrix3fs.multiplyMatrixVector(transform, temp30, temp31);
			x1=temp31[0];
			y1=temp31[1];
			canvas.drawLine(x0, y0, x1, y1, paint);
		}

		@Override
		public void setColor(int color) {
			paint.setColor(color);
		}
	}
	
	private class TouchListener implements View.OnTouchListener {
		@Override
		public boolean onTouch(View view, MotionEvent me) {
			return ScreenBackground.this.onTouch(view, me);
		}
	}
	
	private class ViewGroupOnTouchListener implements View.OnTouchListener {
		private final ViewGroup viewGroup;

		public ViewGroupOnTouchListener(ViewGroup viewGroup) {
			this.viewGroup=viewGroup;
		}

		@Override
		public boolean onTouch(View view, MotionEvent me) {
			if (1!=viewGroup.getChildCount()) {
				return false;
			}
			View child=viewGroup.getChildAt(0);
			if ((viewGroup.getHeight()<child.getHeight())
					|| (viewGroup.getWidth()<child.getWidth())) {
				return false;
			}
			return ScreenBackground.this.onTouch(view, me);
		}
	}
	
	private AsyncImageStreamHolder asyncImageStreamHolder;
	private final CanvasImpl canvasImpl=new CanvasImpl();
	private final float[] crosshair=new float[]{0.5f, 0.5f};
	private int detectedHeight;
	private confessor.thufir.lib.Drawable detectedDrawable;
	private int detectedWidth;
	private final Object lock=new Object();
	private final Paint paint=new Paint();
	private final View parent;
	private final RectF rect=new RectF();
	private final float[] temp90=new float[9];
	private final float[] temp91=new float[9];
	private final float[] temp92=new float[9];
	private final float[] transform=new float[9];

	public ScreenBackground(View parent) {
		this.parent=parent;
		Matrix3fs.identity(transform);
		parent.setOnTouchListener(new TouchListener());
	}
	
	public void crosshair(float[] crosshair) {
		synchronized (lock) {
			System.arraycopy(this.crosshair, 0, crosshair, 0,
					this.crosshair.length);
		}
	}
	
	private boolean crosshairMoveable() {
		synchronized (lock) {
			if (asyncImageStreamHolder.hasDiagnosticsStream()) {
				return true;
			}
			if ((0.5f!=crosshair[0])
					|| (0.5f!=crosshair[1])) {
				crosshair[0]=0.5f;
				crosshair[1]=0.5f;
				parent.invalidate();
			}
			return false;
		}
	}
	
	@Override
	public void draw(Canvas canvas) {
		confessor.thufir.lib.Drawable dd;
		int dh;
		int dw;
		synchronized (lock) {
			crosshairMoveable();
			dd=detectedDrawable;
			dh=detectedHeight;
			dw=detectedWidth;
		}
		
		int ww=parent.getWidth();
		int hh=parent.getHeight();
		paint.reset();
		paint.setColor(Color.TRANSPARENT);
		canvas.drawPaint(paint);
		paint.setStrokeWidth(0);
		paint.setStyle(Paint.Style.STROKE);
		if (null!=dd) {
			Matrix3fs.scale(1f/(dw-1), 1f/(dh-1), temp90);
			synchronized (lock) {
				Matrix3fs.multiplyMatrixMatrix(transform, temp90, temp91);
			}
			Matrix3fs.scale(ww-1, hh-1, temp90);
			Matrix3fs.multiplyMatrixMatrix(temp90, temp91, temp92);
			canvasImpl.canvas=canvas;
			canvasImpl.transform=temp92;
			dd.draw(canvasImpl);
		}
		paint.setColor(Colors.TEXT);
		float cx;
		float cy;
		synchronized (lock) {
			cx=(int)((ww-1)*crosshair[0]);
			cy=(int)((hh-1)*crosshair[1]);
		}
		paint.setColor(Colors.TEXT);
		canvas.drawLine(cx-5, cy, cx+6, cy, paint);
		canvas.drawLine(cx, cy-5, cx, cy+6, paint);
	}

	@Override
	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}
	
	public int getWidth() {
		return parent.getWidth();
	}

	public void init(AsyncImageStreamHolder asyncImageStreamHolder) {
		this.asyncImageStreamHolder=asyncImageStreamHolder;
	}

	@Override
	public boolean onTouch(View view, MotionEvent me) {
		if (!crosshairMoveable()) {
			return true;
		}
		if ((MotionEvent.ACTION_DOWN!=me.getAction())
				&& (MotionEvent.ACTION_MOVE!=me.getAction())) {
			return true;
		}
		InputDevice device=me.getDevice();
		InputDevice.MotionRange range
				=device.getMotionRange(InputDevice.MOTION_RANGE_X);
		float minX=range.getMin();
		float maxX=range.getMax();
		range=device.getMotionRange(InputDevice.MOTION_RANGE_Y);
		float minY=range.getMin();
		float maxY=range.getMax();
		float xx=me.getRawX();
		if (minX>xx) {
			xx=minX;
		}
		else if (maxX<xx) {
			xx=maxX;
		}
		float yy=me.getRawY();
		if (minY>yy) {
			yy=minY;
		}
		else if (maxY<yy) {
			yy=maxY;
		}
		synchronized (lock) {
			if (!crosshairMoveable()) {
				return true;
			}
			crosshair[0]=(xx-minX)/(maxX-minX);
			crosshair[1]=(yy-minY)/(maxY-minY);
		}
		parent.postInvalidate();
		return true;
	}
	
	public View.OnTouchListener onTouchListener(ViewGroup viewGroup) {
		return new ViewGroupOnTouchListener(viewGroup);
	}
	
	@Override
	public void setAlpha(int alpha) {
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
	}
	
	public void setDetectedDrawable(
			confessor.thufir.lib.Drawable detectedDrawable,
			int height, int width) {
		synchronized (lock) {
			if (null!=this.detectedDrawable) {
				this.detectedDrawable.release();
			}
			this.detectedDrawable=detectedDrawable;
			detectedHeight=height;
			detectedWidth=width;
		}
		parent.postInvalidate();
	}
	
	public void setTransform(float[] transform) {
		synchronized (lock) {
			System.arraycopy(transform, 0, this.transform, 0,
					transform.length);
		}
		parent.postInvalidate();
	}
}
