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
package confessor.thufir.lib.thufir4;

import confessor.thufir.lib.Canvas;
import confessor.thufir.lib.Colors;
import confessor.thufir.lib.Drawable;
import confessor.thufir.lib.collection.Pool;
import confessor.thufir.lib.tracking.Blob;

public class Thufir4Drawable implements Drawable {
	public final Blob blobX_2Y_2=new Blob();
	public final Blob blobX_2Y1=new Blob();
	public final Blob blobX_4Y1=new Blob();
	public final Blob blobX1Y_2=new Blob();
	public final Blob blobX1Y1=new Blob();
	public final Blob blobX1Y3=new Blob();
	public final Blob blobX3Y1=new Blob();
	public float border0x;
	public float border0y;
	public float border0z;
	public float border1x;
	public float border1y;
	public float border1z;
	public float border2x;
	public float border2y;
	public float border2z;
	public float border3x;
	public float border3y;
	public float border3z;
	public boolean brightOnDark;
	public Pool<Thufir4Drawable> pool;
	
	public void init(Thufir4Data data) {
		data.initOuter();
		border0x=data.border0x;
		border0y=data.border0y;
		border0z=data.border0z;
		border1x=data.border1x;
		border1y=data.border1y;
		border1z=data.border1z;
		border2x=data.border2x;
		border2y=data.border2y;
		border2z=data.border2z;
		border3x=data.border3x;
		border3y=data.border3y;
		border3z=data.border3z;
		blobX_2Y_2.copy(data.blobX_2Y_2);
		blobX_2Y1.copy(data.blobX_2Y1);
		blobX_4Y1.copy(data.blobX_4Y1);
		blobX1Y_2.copy(data.blobX1Y_2);
		blobX1Y1.copy(data.blobX1Y1);
		blobX1Y3.copy(data.blobX1Y3);
		blobX3Y1.copy(data.blobX3Y1);
	}
	
	@Override
	public void draw(Canvas canvas) {
		if (brightOnDark) {
			canvas.setColor(Colors.BRIGHT_DETECTED);
		}
		else {
			canvas.setColor(Colors.DARK_DETECTED);
		}
		drawBlob(canvas, blobX_2Y_2);
		drawBlob(canvas, blobX_2Y1);
		drawBlob(canvas, blobX_4Y1);
		drawBlob(canvas, blobX1Y_2);
		drawBlob(canvas, blobX1Y1);
		drawBlob(canvas, blobX1Y3);
		drawBlob(canvas, blobX3Y1);
		
		if (brightOnDark) {
			canvas.setColor(Colors.DARK_DETECTED);
		}
		else {
			canvas.setColor(Colors.BRIGHT_DETECTED);
		}
		
		for (int ii=4; 0<=ii; --ii) {
			int jj=4-ii;
			canvas.drawLine(
					(ii*border0x+jj*border2x)/(ii*border0z+jj*border2z),
					(ii*border0y+jj*border2y)/(ii*border0z+jj*border2z),
					(ii*border1x+jj*border3x)/(ii*border1z+jj*border3z),
					(ii*border1y+jj*border3y)/(ii*border1z+jj*border3z));
			canvas.drawLine(
					(ii*border0x+jj*border1x)/(ii*border0z+jj*border1z),
					(ii*border0y+jj*border1y)/(ii*border0z+jj*border1z),
					(ii*border2x+jj*border3x)/(ii*border2z+jj*border3z),
					(ii*border2y+jj*border3y)/(ii*border2z+jj*border3z));
		}
	}
	
	private void drawBlob(Canvas canvas, Blob blob) {
		canvas.drawCircle(blob.x, blob.y, blob.radius);
	}

	@Override
	public void release() {
		pool.release(this);
	}
	
	public void scrub() {
		pool=null;
	}
}
