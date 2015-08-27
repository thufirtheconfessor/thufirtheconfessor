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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import confessor.thufir.lib.Util;
import confessor.thufir.lib.typein.CrcLines;
import confessor.thufir.lib.typein.Program;
import confessor.thufir.lib.typein.Type;
import confessor.thufir.settings.typein.TextSizeSetting;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SourceView extends View {
	private static final int COLOR_BACKGROUND=0xff000000;
	private static final int COLOR_HEADER_BACKGROUND=0xfff0eee6;
	private static final int COLOR_HEADER_TEXT=0xff363636;
	private static final int COLOR_INVERSE_BACKGROUND=0xffdedacd;
	private static final int COLOR_INVERSE_TEXT=0xff303030;
	private static final int COLOR_SEPARATOR=0xffcccccc;
	private static final int COLOR_TEXT=0xfff0eee6;
	private static final String CRC="Crc";
	private static final String LINE="Line";
	private static final float MARGIN=3.0f;
	private static final String SOURCE="Source";
	
	private class CrcLinesImpl extends CrcLines {
		@Override
		protected void line(String crc, String line) throws Throwable {
			crcs.add(crc);
			lines.add(line);
		}
	}
	
	private final int crcWidth;
	private final List<String> crcs=new ArrayList<String>();
	private final Rect dirtyRect=new Rect();
	private float fontBaseline;
	private float fontHeight;
	private int fontSize;
	private float fontWidth;
	private final int lineWidth;
	private final List<String> lines=new ArrayList<String>();
	private final List<String> measureLayout=new ArrayList<String>();
	private final Paint paint=new Paint();
	private final String program;
	private TextLayout textLayout=TextLayout.LINE_WRAP;
	private final TextLayoutCache textLayoutCache=new TextLayoutCache();
	private final Typeface typeface=Typeface.MONOSPACE;
	private boolean visibleWhitesapces;
	
	public SourceView(Context context, Type type, Program program)
			throws Throwable {
		super(context);
		this.program=program.name();
		
		setTextSizeImpl(TextSizeSetting.DEFAULT);
		
		InputStream inputStream=program.source();
		try {
			new CrcLinesImpl()
					.read(inputStream, "UTF-8", type.lineBytes());
		}
		finally {
			inputStream.close();
		}
		crcWidth=Math.max(8, CRC.length());
		lineWidth=Math.max(LINE.length(),
				Integer.toString(lines.size()+1).length());
	}
	
	private int charsPerLine(float width) {
		return Math.max(1,
				Util.floor((width-2-6*MARGIN)/fontWidth)-crcWidth-lineWidth);
	}
	
	private void drawText(Canvas canvas, String text, float xx, float yy) {
		int length=text.length();
		for (int ii=0; length>ii; ++ii) {
			canvas.drawText(text, ii, ii+1, xx, yy, paint);
			xx+=fontWidth;
		}
	}
	
	public int getTextSize() {
		return fontSize;
	}
	
	private void forceParentLayout() {
		invalidate();
		ViewParent parent=getParent();
		if (null!=parent) {
			if (parent instanceof View) {
				((View)parent).invalidate();
			}
			if (parent instanceof ViewGroup) {
				((ViewGroup)parent).updateViewLayout(this, getLayoutParams());
			}
			parent.requestLayout();
		}
	}
	
	private String line(int line) {
		StringBuilder result=new StringBuilder(lineWidth);
		result.append(line);
		while (lineWidth>result.length()) {
			result.insert(0, '\u0020');
		}
		return result.toString();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int height=getHeight();
		int width=getWidth();
		int dirtyBottom;
		int dirtyTop;
		if (canvas.getClipBounds(dirtyRect)) {
			dirtyBottom=dirtyRect.bottom;
			dirtyTop=dirtyRect.top;
		}
		else {
			dirtyBottom=height;
			dirtyTop=0;
		}
		float crc=MARGIN;
		float line=crcWidth*fontWidth+3*MARGIN+1;
		float source=(crcWidth+lineWidth)*fontWidth+5*MARGIN+2;
		float separator0=crcWidth*fontWidth+2*MARGIN;
		float separator1=(crcWidth+lineWidth)*fontWidth+4*MARGIN+1;
		paint.reset();
		paint.setTypeface(typeface);
		paint.setStrokeWidth(0);
		paint.setStyle(Paint.Style.FILL);
		paint.setAntiAlias(false);
		paint.setLinearText(true);
		paint.setSubpixelText(true);
		paint.setColor(COLOR_INVERSE_BACKGROUND);
		canvas.drawRect(0, dirtyTop, separator1, dirtyBottom, paint);
		paint.setColor(COLOR_BACKGROUND);
		canvas.drawRect(separator1, dirtyTop, width, dirtyBottom, paint);
		
		paint.setTextSize(fontSize);
		if (2*fontHeight>=dirtyTop) {
			paint.setColor(COLOR_HEADER_BACKGROUND);
			canvas.drawRect(0, 0, width, 2*fontHeight, paint);
			paint.setColor(COLOR_SEPARATOR);
			canvas.drawLine(separator0, fontHeight, separator0, 2*fontHeight,
					paint);
			canvas.drawLine(separator1, fontHeight, separator1, 2*fontHeight,
					paint);
			canvas.drawLine(0, fontHeight, width, fontHeight, paint);
			canvas.drawLine(0, 2*fontHeight, separator1, 2*fontHeight, paint);
			paint.setColor(COLOR_BACKGROUND);
			canvas.drawLine(separator1, 2*fontHeight, width, 2*fontHeight,
					paint);
			paint.setColor(COLOR_HEADER_TEXT);
			paint.setAntiAlias(true);
			paint.setFlags(Paint.ANTI_ALIAS_FLAG|Paint.LINEAR_TEXT_FLAG
					|Paint.SUBPIXEL_TEXT_FLAG);
			drawText(canvas, program,
					0.5f*(width-program.length()*fontWidth),
					fontBaseline);
			drawText(canvas, CRC, crc+(crcWidth-CRC.length())*0.5f*fontWidth,
					fontHeight+fontBaseline);
			drawText(canvas, LINE,
					line+(lineWidth-LINE.length())*0.5f*fontWidth,
					fontHeight+fontBaseline);
			drawText(canvas, SOURCE, source+(width-MARGIN-source)*0.5f,
					fontHeight+fontBaseline);
			paint.setAntiAlias(false);
		}
		
		float yy=2*fontHeight+fontBaseline;
		textLayoutCache.layout(textLayout, lines, charsPerLine(width),
				visibleWhitesapces);
		int size=textLayoutCache.layout.size();
		for (int ii=0; size>ii; ++ii) {
			List<String> lineLayout=textLayoutCache.layout.get(ii);
			int layoutSize=lineLayout.size();
			float top=yy-fontBaseline;
			float bottom1=top+fontHeight;
			float bottom2=top+layoutSize*fontHeight;
			if (dirtyBottom<top) {
				break;
			}
			if (dirtyTop<=bottom2) {
				paint.setColor(COLOR_SEPARATOR);
				canvas.drawLine(separator0, top, separator0, bottom1, paint);
				paint.setColor(COLOR_INVERSE_TEXT);
				paint.setAntiAlias(true);
				drawText(canvas, crcs.get(ii), crc, yy);
				drawText(canvas, line(ii+1), line, yy);
			}
			paint.setAntiAlias(true);
			for (int jj=0; layoutSize>jj; ++jj) {
				if (dirtyTop<=bottom2) {
					paint.setColor(COLOR_TEXT);
					drawText(canvas, lineLayout.get(jj), source, yy);
				}
				yy+=fontHeight;
			}
			paint.setAntiAlias(false);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width=SOURCE.length();
		for (int ii=lines.size()-1; 0<=ii; --ii) {
			width=Math.max(width, lines.get(ii).length());
		}
		width=Util.ceiling(2+4*MARGIN+(crcWidth+lineWidth+width)*fontWidth);
		
		int widthMode=View.MeasureSpec.getMode(widthMeasureSpec);
		int widthSize=View.MeasureSpec.getSize(widthMeasureSpec);
		if (View.MeasureSpec.EXACTLY==widthMode) {
			width=widthSize;
		}
		else if (View.MeasureSpec.AT_MOST==widthMode) {
			width=Math.min(width, widthSize);
		}
		width=Math.max(width, getSuggestedMinimumWidth());
		
		int height=2;
		int charsPerLine=charsPerLine(width);
		for (int ii=lines.size()-1; 0<=ii; --ii) {
			measureLayout.clear();
			textLayout.layout(lines.get(ii), measureLayout, charsPerLine,
					visibleWhitesapces);
			height+=Math.max(1, measureLayout.size());
		}
		measureLayout.clear();
		height=Util.ceiling(height*fontHeight);
		
		int heightMode=View.MeasureSpec.getMode(heightMeasureSpec);
		int heightSize=View.MeasureSpec.getSize(heightMeasureSpec);
		if (View.MeasureSpec.EXACTLY==heightMode) {
			height=heightSize;
		}
		else if (View.MeasureSpec.AT_MOST==heightMode) {
			height=Math.min(height, heightSize);
		}
		setMeasuredDimension(width, height);
	}
	
	public void setTextLayout(TextLayout textLayout) {
		this.textLayout=textLayout;
		forceParentLayout();
	}
	
	public void setTextSize(int textSize) {
		setTextSizeImpl(textSize);
	}
	
	private void setTextSizeImpl(int textSize) {
		this.fontSize=textSize;
		char[] chars=new char[128-32+4];
		for (int ii=chars.length-1; 0<=ii; --ii) {
			chars[ii]=(char)(32+ii);
		}
		chars[chars.length-4]=WhitespaceTextLayout.EOL;
		chars[chars.length-3]=WhitespaceTextLayout.VISIBLE_SPACE;
		chars[chars.length-2]=WhitespaceTextLayout.TAB_PAD;
		chars[chars.length-1]=WhitespaceTextLayout.TAB_START;
		float fontBaseline2=1;
		float fontHeight2=1;
		float fontWidth2=1;
		paint.reset();
		paint.setTextSize(fontSize);
		paint.setTypeface(typeface);
		Paint.FontMetrics fontMetrics=paint.getFontMetrics();
		fontBaseline2=Math.max(fontBaseline2,
				fontMetrics.leading-fontMetrics.top);
		fontHeight2=Math.max(fontHeight2,
				fontMetrics.leading+fontMetrics.bottom-fontMetrics.top);
		for (int jj=chars.length-1; 0<=jj; --jj) {
			fontWidth2=Math.max(fontWidth2,
					paint.measureText(chars, jj, 1));
		}
		fontBaseline=fontBaseline2;
		fontHeight=fontHeight2;
		fontWidth=fontWidth2;
		forceParentLayout();
	}
	
	public void setVisibleWhitesapces(boolean visibleWhitesapces) {
		this.visibleWhitesapces=visibleWhitesapces;
		forceParentLayout();
	}
}
