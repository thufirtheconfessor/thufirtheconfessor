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
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.Script;
import android.support.v8.renderscript.Type;
import confessor.thufir.lib.image.Filter;
import confessor.thufir.lib.image.Image;

public abstract class AbstractMaxMinFilter implements Filter {
	private final Context context;
	private final int height;
	private final String name;
	private final int width;

	public AbstractMaxMinFilter(Context context, int height, String name,
			int width) {
		this.context=context;
		this.height=height;
		this.name=name+'-'+(2*width+1)+'x'+(2*height+1);
		this.width=width;
	}
	
	@Override
	public void filter(Image image) throws Throwable {
		RenderScript renderScript=RenderScript.create(context);
		try {
			Element element=Element.U8(renderScript);
			try {
				Type rype=new Type.Builder(renderScript, element)
						.setX(image.width)
						.setY(image.height)
						.create();
				try {
					Allocation in=Allocation.createTyped(renderScript, rype);
					try {
						Allocation out
								=Allocation.createTyped(renderScript, rype);
						try {
							ScriptC_MaxMinFilter sc
									=new ScriptC_MaxMinFilter(renderScript);
							try {
								sc.set_in(in);
								in.copyFrom(image.brightness);
								filter(out, sc, new Script.LaunchOptions()
										.setX(width, image.width-width)
										.setY(height, image.height-height));
								out.copyTo(image.brightness);
							}
							finally {
								sc.destroy();
							}
						}
						finally {
							out.destroy();
						}
					}
					finally {
						in.destroy();
					}
				}
				finally {
					rype.destroy();
				}
			}
			finally {
				element.destroy();
			}
		}
		finally {
			renderScript.destroy();
		}
	}
	
	protected abstract void filter(Allocation out, ScriptC_MaxMinFilter sc,
			Script.LaunchOptions lo) throws Throwable;

	@Override
	public String toString() {
		return name;
	}
}
