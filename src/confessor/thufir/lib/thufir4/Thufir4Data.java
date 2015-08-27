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

import confessor.thufir.lib.Drawable;
import confessor.thufir.lib.color.Brightness;
import confessor.thufir.lib.image.Image;
import confessor.thufir.lib.image.ImageReader;
import confessor.thufir.lib.tracking.Blob;
import confessor.thufir.lib.tracking.DetectedBlob;
import confessor.thufir.lib.tracking.Geometries;
import confessor.thufir.lib.tracking.GeometryData;

public class Thufir4Data implements GeometryData {
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
	public int clockX1;
	public int clockX2;
	public int clockY1;
	public int clockY2;
	public float corner0x;
	public float corner0y;
	public float corner0z;
	public float corner1x;
	public float corner1y;
	public float corner1z;
	public float corner2x;
	public float corner2y;
	public float corner2z;
	public float corner3x;
	public float corner3y;
	public float corner3z;
	public float cornerSize1x;
	public float cornerSize1y;
	public float cornerSize1z;
	public float cornerSize2x;
	public float cornerSize2y;
	public float cornerSize2z;
	public float data1x;
	public float data1y;
	public float data1z;
	public float data2x;
	public float data2y;
	public float data2z;
	public int dataCellsHeight;
	public int dataCellsWidth;
	public int dataHeight;
	public int dataWidth;
	public Thufir4DrawablePool drawablePool;
	public float half12x;
	public float half12y;
	public float half12z;
	public float half1x;
	public float half1y;
	public float half1z;
	public float half2x;
	public float half2y;
	public float half2z;
	public boolean hasInner;
	public boolean hasOuter;
	private byte[] tempData;
	public boolean valid;
	
	public float approximateAreaToThePowerOf4() {
		return Geometries.distanceSquared(blobX1Y_2, blobX_2Y_2)
				*Geometries.distanceSquared(blobX1Y1, blobX_2Y1)
				*Geometries.distanceSquared(blobX_2Y1, blobX_2Y_2)
				*Geometries.distanceSquared(blobX1Y1, blobX1Y_2);
	}

	@Override
	public boolean brightOnDark() {
		return brightOnDark;
	}
	
	public boolean clock(boolean clock, ImageReader<Thufir4Data> imageReader) {
		tempData[0]=0;
		return read(imageReader, 0, 0, 0)
				&& read(imageReader, 0, clockX1, 0)
				&& read(imageReader, 0, clockX2, 0)
				&& read(imageReader, 0, 0, clockY1)
				&& read(imageReader, 0, clockX2, clockY1)
				&& read(imageReader, 0, 0, clockY2)
				&& read(imageReader, 0, clockX1, clockY2)
				&& read(imageReader, 0, clockX2, clockY2)
				&& ((byte)(clock?0xa5:0x5a)==tempData[0]);
	}
	
	private Integer dataSize(float tx, float ty, float tz,
			Image image) {
		int result=1;
		float xx=corner0x+3*tx;
		float yy=corner0y+3*ty;
		float zz=corner0z+3*tz;
		while (true) {
			switch (image.brightness(xx, yy, zz)) {
				case Brightness.BRIGHT:
					return null;
				case Brightness.DARK:
					break;
				default:
					return null;
			}
			xx+=tx;
			yy+=ty;
			zz+=tz;
			switch (image.brightness(xx, yy, zz)) {
				case Brightness.BRIGHT:
					++result;
					break;
				case Brightness.DARK:
					return result;
				default:
					return null;
			}
			xx+=tx;
			yy+=ty;
			zz+=tz;
		}
	}

	@Override
	public void detectedBlobs(boolean brightOnly, DetectedBlob detectedBlob) {
		detectedBlob.blob(brightOnly, blobX_2Y_2.x, blobX_2Y_2.y);
		detectedBlob.blob(brightOnly, blobX_2Y1.x, blobX_2Y1.y);
		detectedBlob.blob(brightOnly, blobX_4Y1.x, blobX_4Y1.y);
		detectedBlob.blob(brightOnly, blobX1Y_2.x, blobX1Y_2.y);
		detectedBlob.blob(brightOnly, blobX1Y1.x, blobX1Y1.y);
		detectedBlob.blob(brightOnly, blobX1Y3.x, blobX1Y3.y);
		detectedBlob.blob(brightOnly, blobX3Y1.x, blobX3Y1.y);
	}

	@Override
	public void flipBrightOnDark() {
		brightOnDark=!brightOnDark;
	}

	public void init(boolean brightOnDark, Thufir4DrawablePool drawablePool,
			Blob blobX_2Y_2, Blob blobX_2Y1, Blob blobX_4Y1, Blob blobX1Y_2,
			Blob blobX1Y1, Blob blobX1Y3, Blob blobX3Y1) {
		this.brightOnDark=brightOnDark;
		this.drawablePool=drawablePool;
		this.blobX_2Y_2.copy(blobX_2Y_2);
		this.blobX_2Y1.copy(blobX_2Y1);
		this.blobX_4Y1.copy(blobX_4Y1);
		this.blobX1Y_2.copy(blobX1Y_2);
		this.blobX1Y1.copy(blobX1Y1);
		this.blobX1Y3.copy(blobX1Y3);
		this.blobX3Y1.copy(blobX3Y1);
	}
	
	public void initInner(Image image) {
		if (hasInner) {
			return;
		}
		initOuter();
		valid=false;
		Integer dataHeight2=dataSize(cornerSize2x, cornerSize2y, cornerSize2z,
				image);
		if (null==dataHeight2) {
			return;
		}
		Integer dataWidth2=dataSize(cornerSize1x, cornerSize1y, cornerSize1z,
				image);
		if (null==dataWidth2) {
			return;
		}
		dataHeight=dataHeight2;
		dataWidth=dataWidth2;
		int tempDataSize=32*dataHeight*dataWidth;
		if ((null==tempData)
				|| (tempData.length!=tempDataSize)) {
			tempData=new byte[tempDataSize];
		}
		dataCellsHeight=16*dataHeight;
		dataCellsWidth=16*dataWidth;
		clockX1=dataCellsWidth/2;
		clockX2=dataCellsWidth-1;
		clockY1=dataCellsHeight/2;
		clockY2=dataCellsHeight-1;
		data1x=(border1x-border0x)/dataCellsWidth;
		data1y=(border1y-border0y)/dataCellsWidth;
		data1z=(border1z-border0z)/dataCellsWidth;
		data2x=(border2x-border0x)/dataCellsHeight;
		data2y=(border2y-border0y)/dataCellsHeight;
		data2z=(border2z-border0z)/dataCellsHeight;
		half1x=0.5f*data1x;
		half1y=0.5f*data1y;
		half1z=0.5f*data1z;
		half2x=0.5f*data2x;
		half2y=0.5f*data2y;
		half2z=0.5f*data2z;
		half12x=half1x+half2x;
		half12y=half1y+half2y;
		half12z=half1z+half2z;
		valid=true;
		hasInner=true;
	}
	
	public void initOuter() {
		if (hasOuter) {
			return;
		}
		float omx0=blobX1Y1.x;
		float omy0=blobX1Y1.y;
		float omx1=blobX_2Y1.x;
		float omy1=blobX_2Y1.y;
		float omx2=blobX1Y_2.x;
		float omy2=blobX1Y_2.y;
		float omx3=blobX_2Y_2.x;
		float omy3=blobX_2Y_2.y;
	
		/*
		solution of
		solve([
		omx0*oz0=ox0,
		omy0*oz0=oy0,
		omx1*oz1=ox1,
		omy1*oz1=oy1,
		omx2*oz2=ox2,
		omy2*oz2=oy2,
		omx3*oz3=ox3,
		omy3*oz3=oy3,
		oz0=1,
		ox1-ox0=ox3-ox2,
		oy1-oy0=oy3-oy2,
		oz1-oz0=oz3-oz2
		], [
		ox0, ox1, ox2, ox3, oy0, oy1, oy2, oy3, oz0, oz1, oz2, oz3
		]);
		by maxima
		*/
		corner0z=1f;
		float dx3x2=omx3-omx2;
		float dy3y2=omy3-omy2;
		float px2y3=omx2*omy3;
		float denom=(px2y3-omx1*dy3y2-omx3*omy2+dx3x2*omy1);
		corner1z=(px2y3-omx0*dy3y2-omx3*omy2+dx3x2*omy0)
				/denom;
		corner2z=(omx3*omy1-omx1*omy3+omx0*(omy3-omy1)-(omx3-omx1)*omy0)
				/denom;
		corner3z=(omx2*omy1-omx1*omy2+omx0*(omy2-omy1)-(omx2-omx1)*omy0)
				/denom;
		corner0x=omx0*corner0z;
		corner0y=omy0*corner0z;
		corner1x=omx1*corner1z;
		corner1y=omy1*corner1z;
		corner2x=omx2*corner2z;
		corner2y=omy2*corner2z;
		corner3x=omx3*corner3z;
		corner3y=omy3*corner3z;
		
		float mmx1=blobX3Y1.x;
		float mmy1=blobX3Y1.y;
		float mmx2=blobX1Y3.x;
		float mmy2=blobX1Y3.y;
		float mz1=Geometries.interpolateZ(corner0x, corner0y, corner0z,
				corner1x, corner1y, corner1z, mmx1, mmy1);
		float mz2=Geometries.interpolateZ(corner0x, corner0y, corner0z,
				corner2x, corner2y, corner2z, mmx2, mmy2);
		float mx1=mmx1*mz1;
		float my1=mmy1*mz1;
		float mx2=mmx2*mz2;
		float my2=mmy2*mz2;
		
		cornerSize1x=0.5f*(mx1-corner0x);
		cornerSize2x=0.5f*(mx2-corner0x);
		cornerSize1y=0.5f*(my1-corner0y);
		cornerSize2y=0.5f*(my2-corner0y);
		cornerSize1z=0.5f*(mz1-corner0z);
		cornerSize2z=0.5f*(mz2-corner0z);

		border0x=corner0x+1.5f*(cornerSize1x+cornerSize2x);
		border0y=corner0y+1.5f*(cornerSize1y+cornerSize2y);
		border0z=corner0z+1.5f*(cornerSize1z+cornerSize2z);
		border1x=corner1x-1.5f*cornerSize1x+1.5f*cornerSize2x;
		border1y=corner1y-1.5f*cornerSize1y+1.5f*cornerSize2y;
		border1z=corner1z-1.5f*cornerSize1z+1.5f*cornerSize2z;
		border2x=corner2x+1.5f*cornerSize1x+0.5f*cornerSize2x;
		border2y=corner2y+1.5f*cornerSize1y+0.5f*cornerSize2y;
		border2z=corner2z+1.5f*cornerSize1z+0.5f*cornerSize2z;
		border3x=corner3x-1.5f*cornerSize1x+0.5f*cornerSize2x;
		border3y=corner3y-1.5f*cornerSize1y+0.5f*cornerSize2y;
		border3z=corner3z-1.5f*cornerSize1z+0.5f*cornerSize2z;
		hasOuter=true;
	}

	@Override
	public Drawable overlayDrawable() {
		Thufir4Drawable drawable=drawablePool.acquire();
		drawable.init(this);
		return drawable;
	}
	
	private boolean read(ImageReader<Thufir4Data> imageReader,
			int tempDataIndex, int xx, int yy) {
		boolean set;
		switch (imageReader.data(xx, yy)) {
			case Brightness.BRIGHT:
				set=true;
				break;
			case Brightness.DARK:
				set=false;
				break;
			default:
				return false;
		}
		tempData[tempDataIndex]=(byte)((tempData[tempDataIndex]>>1)&0x7f);
		if (set) {
			tempData[tempDataIndex]|=0x80;
		}
		return true;
	}
	
	public byte[] read(boolean clock, CrcChecker crcChecker,
			byte[] dataBuffer, ImageReader<Thufir4Data> imageReader) {
		tempData[0]=(byte)(clock?0xa5:0x5a);
		for (int tdc=8, tdi=1, yy=0; dataCellsHeight>yy; ++yy) {
			boolean my=clockY1==yy;
			boolean sy=(0==yy)
					|| (clockY2==yy);
			for (int xx=0; dataCellsWidth>xx; ++xx) {
				boolean mx=clockX1==xx;
				boolean sx=(0==xx)
						|| (clockX2==xx);
				if ((sx && sy)
						|| (mx && sy)
						|| (sx && my)) {
					continue;
				}
				if (!read(imageReader, tdi, xx, yy)) {
					return null;
				}
				--tdc;
				if (0>=tdc) {
					++tdi;
					tdc=8;
					if ((0==(tdi%32))
							&& (!crcChecker.check(tempData, tdi-32))) {
						return null;
					}
				}
			}
		}
		int length=28*dataHeight*dataWidth-1;
		if ((null==dataBuffer)
				|| (dataBuffer.length!=length)) {
			dataBuffer=new byte[length];
		}
		System.arraycopy(tempData, 1, dataBuffer, 0, 27);
		for (int tdi=32, dbi=27, cc=dataHeight*dataWidth-1; 0<cc;
				--cc, tdi+=32, dbi+=28) {
			System.arraycopy(tempData, tdi, dataBuffer, dbi, 28);
		}
		return dataBuffer;
	}
	
	public void scrub() {
		hasInner=false;
		hasOuter=false;
		valid=false;
		drawablePool=null;
	}
}
