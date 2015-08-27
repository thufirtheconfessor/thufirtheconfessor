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
package confessor.thufir.image;

import android.content.Context;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Script;

public class Max3x3Filter extends AbstractMaxMinFilter {
	public Max3x3Filter(Context context) {
		super(context, 1, "max", 1);
	}

	@Override
	protected void filter(Allocation out, ScriptC_MaxMinFilter sc,
			Script.LaunchOptions lo) throws Throwable {
		sc.forEach_max3x3(out, lo);
	}
}
