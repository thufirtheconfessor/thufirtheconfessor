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

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import confessor.thufir.AndroidErrorHandler;
import confessor.thufir.Env;
import confessor.thufir.lib.Drawable;
import confessor.thufir.lib.image.Image;
import confessor.thufir.lib.stream.ErrorHandler;
import confessor.thufir.lib.stream.ImageGeometryStream;
import confessor.thufir.lib.tracking.GeometryData;

public class Screen extends FrameLayout implements ErrorHandler {
	private class PostError implements Runnable {
		private final Throwable throwable;

		public PostError(Throwable throwable) {
			this.throwable=throwable;
		}

		@Override
		public void run() {
			if (null!=view) {
				view.error(throwable);
			}
		}
	}
	
	private class PostFactory implements Runnable {
		private final ScreenFactory factory;

		public PostFactory(ScreenFactory factory) {
			this.factory=factory;
		}

		@Override
		public void run() {
			try {
				setFactory(factory);
			}
			catch (Throwable throwable) {
				error(throwable);
			}
		}
	}
	
	private class WrapperStream<D extends GeometryData>
			implements ImageGeometryStream<D> {
		private final ImageGeometryStream<D> stream;
		private final ScreenView view;

		public WrapperStream(ImageGeometryStream<D> stream, ScreenView view) {
			this.stream=stream;
			this.view=view;
		}

		@Override
		public void error(Throwable throwable) {
			stream.error(throwable);
		}

		@Override
		public void next(D geometryData, Image image) throws Throwable {
			if ((null==geometryData)
					|| (null==image)) {
				setDetectedDrawable(null, view, 2, 2);
			}
			else {
				setDetectedDrawable(geometryData.overlayDrawable(), view,
						image.height, image.width);
			}
			stream.next(geometryData, image);
		}
	}
	
	private final ScreenBackground background;
	private Env env;
	private ScreenView view;
	
	public Screen(Context context) {
		this(context, null, 0);
	}

	public Screen(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public Screen(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		background=new ScreenBackground(this);
	}

	public ScreenBackground background() {
		return background;
	}

	@Override
	public void error(Throwable throwable) {
		AndroidErrorHandler.d(throwable);
		post(new PostError(throwable));
	}
	
	public void init(Env env) {
		this.env=env;
		background.init(env.asyncImageStreamHolder());
		setBackgroundDrawable(background);
		setOnTouchListener(background);
	}
	
	public boolean pop() throws Throwable {
		if (null==view) {
			return false;
		}
		ScreenFactory factory=view.parent();
		if (null==factory) {
			return false;
		}
		factory=factory.parent();
		if (null==factory) {
			return false;
		}
		setFactory(factory);
		return true;
	}
	
	public void postFactory(ScreenFactory factory) throws Throwable {
		post(new PostFactory(factory));
	}
	
	public ScreenFactory push() throws Throwable {
		if (null==view) {
			return null;
		}
		return view.push();
	}
	
	public void setDetectedDrawable(Drawable drawable,
			ScreenView screenView, int height, int width) {
		if (this.view==screenView) {
			background.setDetectedDrawable(drawable, height, width);
		}
	}
	
	public void setFactory(ScreenFactory factory)
			throws Throwable {
		setFactory(view, factory.screenView(view));
	}
	
	private void setFactory(ScreenView oldView, ScreenView newView)
			throws Throwable {
		if (null==newView) {
			return;
		}
		boolean success=false;
		try {
			removeAllViews();
			if (newView.screenOn()) {
				env.wakeLockStreamHolderListener().screenOn();
			}
			env.asyncImageStreamHolder().stream(newView.imageStream());
			addView(newView.view());
			view=newView;
			setDetectedDrawable(null, newView, 2, 2);
			success=true;
			if (null!=oldView) {
				oldView.close();
			}
		}
		finally {
			if (!success) {
				try {
					newView.close();
				}
				finally {
					setFactory(null, oldView);
				}
			}
		}
	}
	
	public <D extends GeometryData> ImageGeometryStream<D> wrapStream(
			ImageGeometryStream<D> stream, ScreenView view) {
		return new WrapperStream<D>(stream, view);
	}
}
