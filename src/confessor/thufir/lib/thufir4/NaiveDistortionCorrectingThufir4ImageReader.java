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

import confessor.thufir.lib.collection.ObjectPool;
import confessor.thufir.lib.image.AbstractImageReader;
import confessor.thufir.lib.image.LineScan;
import confessor.thufir.lib.thread.AbstractJob;

public abstract class NaiveDistortionCorrectingThufir4ImageReader
		extends AbstractImageReader<Thufir4Data> {
	private static final int LINE_MULTI_THRESHOLD=32;
	private static final int MEASURE_MULTI_THRESHOLD=32;
	
	private class LineJob extends AbstractJob {
		public boolean horizontal;
		public int length;
		public int xx;
		public int yy;
		
		@Override
		protected void closeImpl() throws Throwable {
		}
		
		public void init(int length, int xx, int yy, boolean horizontal) {
			this.horizontal=horizontal;
			this.length=length;
			this.xx=xx;
			this.yy=yy;
		}

		@Override
		protected void runImpl() throws Throwable {
			line(length, xx, yy, horizontal);
		}
	}
	
	private class LineJobPool extends ObjectPool<LineJob> {
		public LineJobPool() {
			super(4, 4);
		}

		@Override
		protected LineJob create() {
			return new LineJob();
		}

		@Override
		protected void init(LineJob object) {
		}

		@Override
		protected void scrub(LineJob object) {
			object.reset();
		}
	}
	
	private class MeasureJob extends AbstractJob {
		public int db;
		public int dl;
		public int doo;
		public int dr;
		public int dt;
		public int ldo;
		public int length;
		private final LineScan lineScan=new LineScan();
		private final V3f[] orthogonal=createVectors(Math.max(height, width));
		private final V3f[] parallel=createVectors(Math.max(height, width));
		public float score;
		private final V3f temp0=new V3f();
		private final V3f temp1=new V3f();
		public int xx;
		public int yy;
		
		@Override
		protected void closeImpl() throws Throwable {
		}

		public void init(int xx, int yy, int doo, int ldo, int length,
				int db, int dl, int dr, int dt) {
			this.db=db;
			this.dl=dl;
			this.doo=doo;
			this.dr=dr;
			this.dt=dt;
			this.ldo=ldo;
			this.length=length;
			this.xx=xx;
			this.yy=yy;
		}
		
		public void mergeJob() {
			merge(length, xx, yy, doo, orthogonal, parallel);
			bottom-=db;
			left+=dl;
			right-=dr;
			top+=dt;
		}

		@Override
		protected void runImpl() throws Throwable {
			int oo=xx+yy*width;
			measureOrthogonal(lineScan, orthogonal, oo, doo, ldo, length);
			measureParallel(lineScan, parallel, temp0, temp1, oo, doo, length);
			score=score(length, xx, yy, doo, orthogonal, parallel);
		}
	}
	
	private class MeasureJobPool extends ObjectPool<MeasureJob> {
		public MeasureJobPool() {
			super(4, 4);
		}

		@Override
		protected MeasureJob create() {
			return new MeasureJob();
		}

		@Override
		protected void init(MeasureJob object) {
		}

		@Override
		protected void scrub(MeasureJob object) {
			object.reset();
		}
	}
	
	public static class MultiLine
			extends NaiveDistortionCorrectingThufir4ImageReader {
		@Override
		protected void narrow() throws Throwable {
			super.lineMultiThreaded();
			super.measureSingleThreaded();
		}
	}
	
	public static class MultiLineMeasure
			extends NaiveDistortionCorrectingThufir4ImageReader {
		@Override
		protected void narrow() throws Throwable {
			super.lineMultiThreaded();
			super.measureMultiThreaded();
		}
	}
	
	public static class MultiMeasure
			extends NaiveDistortionCorrectingThufir4ImageReader {
		@Override
		protected void narrow() throws Throwable {
			super.lineSingleThreaded();
			super.measureMultiThreaded();
		}
	}
	
	public static class Single
			extends NaiveDistortionCorrectingThufir4ImageReader {
		@Override
		protected void narrow() throws Throwable {
			super.lineSingleThreaded();
			super.measureSingleThreaded();
		}
	}
	
	private static class V3f {
		public float xx;
		public float yy;
		public float zz;
	}
	
	private int bottom;
	private int dataHeight=-1;
	private int dataWidth=-1;
	private int height;
	private int left;
	private final LineJobPool lineJobPool=new LineJobPool();
	private final LineScan lineScan=new LineScan();
	private V3f[] measureBO;
	private V3f[] measureBP;
	private V3f[] measureLO;
	private V3f[] measureLP;
	private V3f[] measureRO;
	private V3f[] measureRP;
	private V3f[] measureTO;
	private V3f[] measureTP;
	private final MeasureJobPool measureJobPool=new MeasureJobPool();
	private V3f[] points;
	private int readOffset;
	private int right;
	private final V3f temp0=new V3f();
	private final V3f temp1=new V3f();
	private int top;
	private int width;
	
	private static void addLine(V3f re, V3f v0, V3f v1, int length,
			int distance) {
		float f1=(float)distance/length;
		float f0=1.0f-f1;
		re.xx+=f0*v0.xx+f1*v1.xx;
		re.yy+=f0*v0.yy+f1*v1.yy;
		re.zz+=f0*v0.zz+f1*v1.zz;
	}
	
	private static V3f[] createVectors(int length) {
		V3f[] result=new V3f[length];
		for (int ii=length-1; 0<=ii; --ii) {
			result[ii]=new V3f();
		}
		return result;
	}
	
	@Override
	public byte data(int xx, int yy) {
		V3f vv=points[readOffset+xx+yy*width];
		return image.brightness(vv.xx, vv.yy, vv.zz);
	}
	
	@Override
	protected void initImpl() throws Throwable {
		if ((data.dataHeight!=dataHeight)
				|| (data.dataWidth!=dataWidth)) {
			height=data.dataCellsHeight+2;
			width=data.dataCellsWidth+2;
			points=createVectors(height*width);
			readOffset=width+1;
			measureBO=createVectors(width);
			measureBP=createVectors(width);
			measureLO=createVectors(height);
			measureLP=createVectors(height);
			measureRO=createVectors(height);
			measureRP=createVectors(height);
			measureTO=createVectors(width);
			measureTP=createVectors(width);
			dataHeight=data.dataHeight;
			dataWidth=data.dataWidth;
		}
		V3f b0=points[0];
		V3f b1=points[width-1];
		V3f b2=points[points.length-width];
		V3f b3=points[points.length-1];
		lineBase(b0, -1, -1);
		lineBase(b1, data.dataCellsWidth, -1);
		lineBase(b2, -1, data.dataCellsHeight);
		lineBase(b3, data.dataCellsWidth, data.dataCellsHeight);
		
		for (int xx=width-2, o2=points.length-2; 0<xx; --xx, --o2) {
			line(points[xx], b0, b1, width-1, xx);
			line(points[o2], b2, b3, width-1, xx);
		}
		for (int yy=height-2,
				o1=points.length-2*width, o2=points.length-width-1;
				0<yy; --yy, o1-=width, o2-=width) {
			line(points[o1], b0, b2, height-1, yy);
			line(points[o2], b1, b3, height-1, yy);
		}
		
		bottom=height-2;
		left=1;
		right=width-2;
		top=1;
		while ((left<right)
				&& (top<bottom)) {
			narrow();
		}
		
		if ((left<=right)
				&& (top<=bottom)) {
			if (top<bottom) {
				line(bottom-top+1, left, top, false);
			}
			else {
				line(right-left+1, left, top, true);
			}
		}
	}
	
	private static void line(V3f re, V3f v0, V3f v1,
			int length, int distance) {
		float f1=(float)distance/length;
		float f0=1.0f-f1;
		re.xx=f0*v0.xx+f1*v1.xx;
		re.yy=f0*v0.yy+f1*v1.yy;
		re.zz=f0*v0.zz+f1*v1.zz;
	}
	
	private void line(int length, int xx, int yy, boolean horizontal) {
		int bo=xx+(bottom+1)*width;
		int ld=xx-left+1;
		int lo=left-1+yy*width;
		int lx=right-left+2;
		int ly=bottom-top+2;
		int oo=xx+yy*width;
		int ro=right+1+yy*width;
		int td=yy-top+1;
		int to=xx+(top-1)*width;
		if (horizontal) {
			for (; 0<length;
					--length,
					++bo,
					++ld,
					++oo,
					++to,
					++xx) {
				V3f vv=points[oo];
				line(vv, points[lo], points[ro], lx, ld);
				addLine(vv, points[to], points[bo], ly, td);
				vv.xx*=0.5f;
				vv.yy*=0.5f;
				vv.zz*=0.5f;
			}
		}
		else {
			for (; 0<length;
					--length,
					lo+=width,
					oo+=width,
					ro+=width,
					++td) {
				V3f vv=points[oo];
				line(vv, points[lo], points[ro], lx, ld);
				addLine(vv, points[to], points[bo], ly, td);
				vv.xx*=0.5f;
				vv.yy*=0.5f;
				vv.zz*=0.5f;
			}
		}
	}
	
	private void lineBase(V3f vv, int xx, int yy) {
		float fx1=(xx+0.5f)/data.dataCellsWidth;
		float fx0=1.0f-fx1;
		float fy1=(yy+0.5f)/data.dataCellsHeight;
		float fy0=1.0f-fy1;
		vv.xx=fx0*data.border0x+fx1*data.border1x
				+fy0*data.border0x+fy1*data.border2x
				-data.border0x;
		vv.yy=fx0*data.border0y+fx1*data.border1y
				+fy0*data.border0y+fy1*data.border2y
				-data.border0y;
		vv.zz=fx0*data.border0z+fx1*data.border1z
				+fy0*data.border0z+fy1*data.border2z
				-data.border0z;
	}
	
	private void lineMultiThreaded() throws Throwable {
		int height2=bottom-top+1;
		int width2=right-left+1;
		boolean horizontal=LINE_MULTI_THRESHOLD<=width2;
		boolean vertical=LINE_MULTI_THRESHOLD<=height2;
		
		LineJob hjob0=lineJobPool.acquire();
		hjob0.init(width2, left, top, true);
		LineJob hjob1=lineJobPool.acquire();
		hjob1.init(width2, left, bottom, true);
		LineJob vjob0=lineJobPool.acquire();
		vjob0.init(height2, left, top, false);
		LineJob vjob1=lineJobPool.acquire();
		vjob1.init(height2, right, top, false);
		
		if (horizontal) {
			if (vertical) {
				threadPool.submit(hjob0);
				threadPool.submit(hjob1);
				threadPool.submit(vjob0);
				threadPool.submit(vjob1);
				hjob0.join();
				hjob1.join();
				vjob0.join();
				vjob1.join();
			}
			else {
				threadPool.submit(hjob0);
				threadPool.submit(hjob1);
				hjob0.join();
				hjob1.join();
				vjob0.runImpl();
				vjob1.runImpl();
			}
		}
		else {
			if (vertical) {
				threadPool.submit(vjob0);
				threadPool.submit(vjob1);
				vjob0.join();
				vjob1.join();
				hjob0.runImpl();
				hjob1.runImpl();
			}
			else {
				hjob0.runImpl();
				hjob1.runImpl();
				vjob0.runImpl();
				vjob1.runImpl();
			}
		}
		
		lineJobPool.release(hjob0);
		lineJobPool.release(hjob1);
		lineJobPool.release(vjob0);
		lineJobPool.release(vjob1);
	}
	
	private void lineSingleThreaded() {
		int height2=bottom-top+1;
		int width2=right-left+1;
		line(width2, left, top, true);
		line(width2, left, bottom, true);
		line(height2, left, top, false);
		line(height2, right, top, false);
	}
	
	private void measureMultiThreaded() throws Throwable {
			int height2=bottom-top+1;
			int width2=right-left+1;
			boolean horizontal=MEASURE_MULTI_THRESHOLD<=width2;
			boolean vertical=MEASURE_MULTI_THRESHOLD<=height2;
			
			MeasureJob hjob0=measureJobPool.acquire();
			hjob0.init(left, bottom, 1, width, width2, 1, 0, 0, 0);
			MeasureJob hjob1=measureJobPool.acquire();
			hjob1.init(left, top, 1, -width, width2, 0, 0, 0, 1);
			MeasureJob vjob0=measureJobPool.acquire();
			vjob0.init(left, top, width, -1, height2, 0, 1, 0, 0);
			MeasureJob vjob1=measureJobPool.acquire();
			vjob1.init(right, top, width, 1, height2, 0, 0, 1, 0);
			
			if (horizontal) {
				if (vertical) {
					threadPool.submit(hjob0);
					threadPool.submit(hjob1);
					threadPool.submit(vjob0);
					threadPool.submit(vjob1);
					hjob0.join();
					hjob1.join();
					vjob0.join();
					vjob1.join();
				}
				else {
					threadPool.submit(hjob0);
					threadPool.submit(hjob1);
					hjob0.join();
					hjob1.join();
					vjob0.runImpl();
					vjob1.runImpl();
				}
			}
			else {
				if (vertical) {
					threadPool.submit(vjob0);
					threadPool.submit(vjob1);
					vjob0.join();
					vjob1.join();
					hjob0.runImpl();
					hjob1.runImpl();
				}
				else {
					hjob0.runImpl();
					hjob1.runImpl();
					vjob0.runImpl();
					vjob1.runImpl();
				}
			}
			
			MeasureJob bestJob=hjob0;
			if (hjob1.score>bestJob.score) {
				bestJob=hjob1;
			}
			if (vjob0.score>bestJob.score) {
				bestJob=vjob0;
			}
			if (vjob1.score>bestJob.score) {
				bestJob=vjob1;
			}
			bestJob.mergeJob();

			measureJobPool.release(hjob0);
			measureJobPool.release(hjob1);
			measureJobPool.release(vjob0);
			measureJobPool.release(vjob1);
	}
	
	private void measureOrthogonal(LineScan lineScan, V3f[] result, int oo,
			int doo, int ldo, int length) {
		int li=-1;
		V3f lv=points[oo-doo];
		for (int ll=length, oi=0; 0<ll; --ll, ++oi, oo+=doo) {
			V3f t0=points[oo+ldo];
			V3f t1=points[oo];
			if (lineScan.findBorder(image,
					t0.xx, t0.yy, t0.zz, t1.xx, t1.yy, t1.zz)) {
				V3f vv=result[oi];
				vv.xx=2.0f*lineScan.xx-t0.xx;
				vv.yy=2.0f*lineScan.yy-t0.yy;
				vv.zz=2.0f*lineScan.zz-t0.zz;
				for (int di=1, ii=li+1, le=oi-li; oi>ii; ++di, ++ii) {
					line(result[ii], lv, vv, le, di);
				}
				li=oi;
				lv=vv;
			}
		}
		V3f t0=points[oo];
		for (int di=1, ii=li+1, le=length-li; length>ii; ++di, ++ii) {
			line(result[ii], lv, t0, le, di);
		}
	}
	
	private void measureParallel(LineScan lineScan, V3f[] result, V3f temp0,
			V3f temp1, int oo, int doo, int length) {
		int li=-1;
		V3f pv=points[oo-doo];
		V3f vv=points[oo];
		temp0.xx=1.5f*pv.xx-0.5f*vv.xx;
		temp0.yy=1.5f*pv.yy-0.5f*vv.yy;
		temp0.zz=1.5f*pv.zz-0.5f*vv.zz;
		for (int ll=length, oi=0; 0<=ll; --ll, ++oi, oo+=doo) {
			vv=points[oo];
			if (lineScan.findBorder(image,
					pv.xx, pv.yy, pv.zz, vv.xx, vv.yy, vv.zz)) {
				V3f t0=result[oi];
				t0.xx=lineScan.xx;
				t0.yy=lineScan.yy;
				t0.zz=lineScan.zz;
				for (int di=1, ii=li+1, le=oi-li; oi>ii; ++di, ++ii) {
					line(result[ii], temp0, t0, le, di);
				}
				li=oi;
				temp0=t0;
			}
			pv=vv;
		}
		if (li<length) {
			oo-=doo;
			V3f t0=points[oo];
			V3f t1=points[oo-doo];
			temp1.xx=1.5f*t0.xx-0.5f*t1.xx;
			temp1.yy=1.5f*t0.yy-0.5f*t1.yy;
			temp1.zz=1.5f*t0.zz-0.5f*t1.zz;
			for (int di=1, ii=li+1, le=length-li+1;
					length>=ii;
					++di, ++ii) {
				line(result[ii], temp0, temp1, le, di);
			}
		}
		for (int ii=0; length>ii; ++ii) {
			V3f t0=result[ii];
			V3f t1=result[ii+1];
			t0.xx=0.5f*(t0.xx+t1.xx);
			t0.yy=0.5f*(t0.yy+t1.yy);
			t0.zz=0.5f*(t0.zz+t1.zz);
		}
	}
	
	private void measureSingleThreaded() {
		measureOrthogonal(lineScan, measureBO, left+bottom*width,
				1, width, right-left+1);
		measureParallel(lineScan, measureBP, temp0, temp1, left+bottom*width,
				1, right-left+1);
		float sb=score(right-left+1, left, bottom, 1, measureBO, measureBP);

		measureOrthogonal(lineScan, measureLO, left+top*width,
				width, -1, bottom-top+1);
		measureParallel(lineScan, measureLP, temp0, temp1, left+top*width,
				width, bottom-top+1);
		float sl=score(bottom-top+1, left, top, width, measureLO, measureLP);

		measureOrthogonal(lineScan, measureRO, right+top*width,
				width, 1, bottom-top+1);
		measureParallel(lineScan, measureRP, temp0, temp1, right+top*width,
				width, bottom-top+1);
		float sr=score(bottom-top+1, right, top, width, measureRO, measureRP);

		measureOrthogonal(lineScan, measureTO, left+top*width,
				1, -width, right-left+1);
		measureParallel(lineScan, measureTP, temp0, temp1, left+top*width,
				1, right-left+1);
		float st=score(right-left+1, left, top, 1, measureTO, measureTP);

		if (sb>=sl) {
			if (sr>=st) {
				if (sb>=sr) {
					mergeBottom();
				}
				else {
					mergeRight();
				}
			}
			else {
				if (sb>=st) {
					mergeBottom();
				}
				else {
					mergeTop();
				}
			}
		}
		else {
			if (sr>=st) {
				if (sl>=sr) {
					mergeLeft();
				}
				else {
					mergeRight();
				}
			}
			else {
				if (sl>=st) {
					mergeLeft();
				}
				else {
					mergeTop();
				}
			}
		}
	}
	
	private void merge(int length, int xx, int yy, int doo,
			V3f[] teO, V3f[] teP) {
		for (int ii=length-1, oo=xx+yy*width+ii*doo; 0<=ii; --ii, oo-=doo) {
			V3f v0=points[oo];
			V3f vo=teO[ii];
			V3f vp=teP[ii];
			v0.xx=0.25f*(2.0f*v0.xx+vo.xx+vp.xx);
			v0.yy=0.25f*(2.0f*v0.yy+vo.yy+vp.yy);
			v0.zz=0.25f*(2.0f*v0.zz+vo.zz+vp.zz);
		}
	}
	
	private void mergeBottom() {
		merge(right-left+1, left, bottom, 1, measureBO, measureBP);
		--bottom;
	}
	
	private void mergeLeft() {
		merge(bottom-top+1, left, top, width, measureLO, measureLP);
		++left;
	}
	
	private void mergeRight() {
		merge(bottom-top+1, right, top, width, measureRO, measureRP);
		--right;
	}
	
	private void mergeTop() {
		merge(right-left+1, left, top, 1, measureTO, measureTP);
		++top;
	}
	
	protected abstract void narrow() throws Throwable;
	
	private float score(int length, int xx, int yy, int doo,
			V3f[] teO, V3f[] teP) {
		float score=0.0f;
		for (int ii=length-1, oo=xx+yy*width+ii*doo; 0<=ii; --ii, oo-=doo) {
			V3f v0=points[oo];
			V3f vo=teO[ii];
			V3f vp=teP[ii];
			float dox=v0.xx-vo.xx;
			float doy=v0.yy-vo.yy;
			float doz=v0.zz-vo.zz;
			float dpx=v0.xx-vp.xx;
			float dpy=v0.yy-vp.yy;
			float dpz=v0.zz-vp.zz;
			score+=dox*dox+doy*doy+doz*doz+dpx*dpx+dpy*dpy+dpz*dpz;
		}
		score/=length;
		return score;
	}
}
