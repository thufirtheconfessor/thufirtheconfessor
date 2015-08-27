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
import android.util.AttributeSet;
import android.widget.TextView;
import confessor.thufir.lib.MutableString;
import confessor.thufir.lib.Util;

public class MutableTextView extends TextView {
	private final MutableString mutableText=new MutableString();

	public MutableTextView(Context context) {
		super(context);
	}

	public MutableTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MutableTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public boolean mutableTextEquals(CharSequence charSequence) {
		return Util.equals(charSequence, mutableText);
	}
	
	public void setMutableText(CharSequence charSequence) {
		if (mutableTextEquals(charSequence)) {
			return;
		}
		mutableText.clear();
		mutableText.append(charSequence);
		setText(mutableText.buf, mutableText.offset, mutableText.length);
	}
}
