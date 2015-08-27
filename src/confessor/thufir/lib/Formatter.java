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
package confessor.thufir.lib;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Formatter {
	private final char[] buf=new char[64];
	private final DecimalFormatSymbols dfs;
	private final Object lock=new Object();

	public Formatter(Locale locale) {
		dfs=new DecimalFormatSymbols(locale);
	}

	public Formatter() {
		this(Locale.getDefault());
	}
	
	public MutableString decimalSeparator(MutableString mutableString) {
		return mutableString.append(dfs.getDecimalSeparator());
	}
	
	public MutableString format(int value, MutableString mutableString) {
		return format(value, mutableString, 0);
	}
	
	public MutableString format(int value, MutableString mutableString,
			int pad) {
		synchronized (lock) {
			int length=0;
			if (0==value) {
				buf[length]=dfs.getZeroDigit();
				++length;
			}
			else if (0<value) {
				while (0!=value) {
					int value2=value/10;
					if ((0<length)
							&& (0==(length+1)%4)) {
						buf[length]
								=dfs.getGroupingSeparator();
						++length;
					}
					buf[length]=Character.forDigit(value-value2*10, 10);
					++length;
					value=value2;
				}
			}
			else {
				while (0!=value) {
					int value2=value/10;
					if ((0<length)
							&& (0==(length+1)%4)) {
						buf[length]
								=dfs.getGroupingSeparator();
						++length;
					}
					buf[length]=Character.forDigit(value2*10-value, 10);
					++length;
					value=value2;
				}
				mutableString=mutableString.append(dfs.getMinusSign());
				--pad;
			}
			for (int ii=pad-length; 0<ii; --ii) {
				mutableString=mutableString.append(dfs.getZeroDigit());
			}
			while (0<length) {
				--length;
				mutableString=mutableString.append(buf[length]);
			}
			return mutableString;
		}
	}
	
	public MutableString format(long value, MutableString mutableString) {
		return format(value, mutableString, 0);
	}
	
	public MutableString format(long value, MutableString mutableString,
			int pad) {
		synchronized (lock) {
			int length=0;
			if (0==value) {
				buf[length]=dfs.getZeroDigit();
				++length;
			}
			else if (0<value) {
				while (0!=value) {
					long value2=value/10;
					if ((0<length)
							&& (0==(length+1)%4)) {
						buf[length]
								=dfs.getGroupingSeparator();
						++length;
					}
					buf[length]=Character.forDigit((int)(value-value2*10), 10);
					++length;
					value=value2;
				}
			}
			else {
				while (0!=value) {
					long value2=value/10;
					if ((0<length)
							&& (0==(length+1)%4)) {
						buf[length]
								=dfs.getGroupingSeparator();
						++length;
					}
					buf[length]=Character.forDigit((int)(value2*10-value), 10);
					++length;
					value=value2;
				}
				mutableString=mutableString.append(dfs.getMinusSign());
				--pad;
			}
			for (int ii=pad-length; 0<ii; --ii) {
				mutableString=mutableString.append(dfs.getZeroDigit());
			}
			while (0<length) {
				--length;
				mutableString=mutableString.append(buf[length]);
			}
			return mutableString;
		}
	}
	
	public MutableString formatPercent(int value,
			MutableString mutableString) {
		synchronized (lock) {
			return format(value, mutableString)
					.append(dfs.getPercent());
		}
	}
}
