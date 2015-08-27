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
package confessor.thufir.lib.tracking;

import confessor.thufir.lib.Vector2is;
import confessor.thufir.lib.collection.IntDeque;
import confessor.thufir.lib.color.Brightness;
import confessor.thufir.lib.image.Image;

public class PerimeterBlobDetector extends AbstractBlobDetector {
	private final IntDeque perimeter=new IntDeque();

	@Override
	public BlobSize blobSize() {
		return BlobSize.PERIMETER;
	}

	@Override
	protected Blob detectImpl(BlobPool blobPool, byte bright, byte dark,
			Image image, int xx, int yy, int width, int height) {
		perimeter.clear();
		byte[] brightness=image.brightness;
		if (perimeter(bright, brightness, image.offset(xx, yy), xx, yy, width,
				height)) {
			perimeter.addLast(Vector2is.offset(xx, yy));
		}
		else {
			for (int dd=3; 0<=dd; --dd) {
				int dx=DX[dd];
				int dy=DY[dd];
				int x2=xx;
				int y2=yy;
				while (true) {
					x2+=dx;
					y2+=dy;
					int offset2=image.offset(x2, y2);
					if (getAndSetDirty(offset2)) {
						return null;
					}
					if (perimeter(bright, brightness, offset2, x2, y2, width,
							height)) {
						perimeter.addLast(Vector2is.offset(x2, y2));
						break;
					}
				}
			}
		}
		float xsum=0;
		float ysum=0;
		int count=0;
		while (!perimeter.isEmpty()) {
			int offset=perimeter.removeFirst();
			int x2=Vector2is.xx(offset);
			int y2=Vector2is.yy(offset);
			xsum+=x2;
			ysum+=y2;
			++count;
			for (int dd=3; 0<=dd; --dd) {
				int x3=x2+DX[dd];
				int y3=y2+DY[dd];
				if ((0<=x3)
						&& (0<=y3)
						&& (width>x3)
						&& (height>y3)) {
					int offset3=image.offset(x3, y3);
					if ((!getAndSetDirty(offset3))
							&& perimeter(bright, brightness, offset3, x3, y3,
									width, height)) {
						perimeter.addLast(Vector2is.offset(x3, y3));
					}
				}
			}
		}
		Blob blob=blobPool.acquire();
		blob.bright=Brightness.BRIGHT==bright;
		blob.radius=count/8f;
		blob.x=xsum/count;
		blob.y=ysum/count;
		return blob;
	}
	
	private boolean perimeter(byte bright, byte[] brightness, int offset,
			int xx, int yy, int width, int height) {
		byte brightness2=brightness[offset];
		if (bright!=brightness2) {
			return false;
		}
		if ((0==xx)
				|| (0==yy)
				|| (width-1==xx)
				|| (height-1==yy)) {
			return true;
		}
		int line0=offset-width/*image.addY(offset, -1)*/;
		int line2=offset+width/*image.addY(offset, 1)*/;
		return (bright!=brightness[line0-1]/*image.addX(line0, -1)*/)
			|| (bright!=brightness[line0])
			|| (bright!=brightness[line0+1]/*image.addX(line0, 1)*/)
			|| (bright!=brightness[offset-1]/*image.addX(offset, -1)*/)
			|| (bright!=brightness[offset+1]/*image.addX(offset, 1)*/)
			|| (bright!=brightness[line2-1]/*image.addX(line2, -1)*/)
			|| (bright!=brightness[line2])
			|| (bright!=brightness[line2+1]/*image.addX(line2, 1)*/);
	}
}
