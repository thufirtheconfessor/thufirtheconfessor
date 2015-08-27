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

import confessor.thufir.lib.Util;
import confessor.thufir.lib.Vector2is;
import confessor.thufir.lib.collection.IntDeque;
import confessor.thufir.lib.color.Brightness;
import confessor.thufir.lib.image.Image;

public class AreaBlobDetector extends AbstractBlobDetector {
	private final IntDeque fringe=new IntDeque();

	@Override
	public BlobSize blobSize() {
		return BlobSize.AREA;
	}

	@Override
	protected Blob detectImpl(BlobPool blobPool, byte bright, byte dark,
			Image image, int xx, int yy, int width, int height) {
		fringe.clear();
		float xsum=0;
		float ysum=0;
		int count=0;
		byte[] brightness=image.brightness;
		fringe.addLast(Vector2is.offset(xx, yy));
		while (!fringe.isEmpty()) {
			int offset=fringe.removeFirst();
			int x2=Vector2is.xx(offset);
			int y2=Vector2is.yy(offset);
			xsum+=x2;
			ysum+=y2;
			++count;
			for (int dd=3; 0<=dd; --dd) {
				int dx=DX[dd];
				int dy=DY[dd];
				int x3=x2+dx;
				int y3=y2+dy;
				if ((0<=x3)
						&& (width>x3)
						&& (0<=y3)
						&& (height>y3)) {
					int offset3=image.offset(x3, y3);
					if ((bright==brightness[offset3])
							&& (!getAndSetDirty(offset3))) {
						fringe.addLast(Vector2is.offset(x3, y3));
					}
				}
			}
		}
		Blob blob=blobPool.acquire();
		blob.bright=Brightness.BRIGHT==bright;
		blob.radius=Util.sqrt(count)/2f;
		blob.x=xsum/count;
		blob.y=ysum/count;
		return blob;
	}
}
