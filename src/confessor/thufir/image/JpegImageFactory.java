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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.Type;
import confessor.thufir.lib.collection.ByteArrayPool;
import confessor.thufir.lib.collection.Pool;
import confessor.thufir.lib.image.Image;
import confessor.thufir.lib.image.ImageFactory;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class JpegImageFactory implements ImageFactory {
	public ByteArrayPool byteArrayPool;
	public Bitmap.Config config;
	public Context context;
	public Pool<JpegImageFactory> imageFactoryPool;
	public byte[] jpeg;
	public boolean renderscript;

	private Bitmap bitmap(Bitmap.Config config) {
		BitmapFactory.Options options=new BitmapFactory.Options();
		options.inPreferQualityOverSpeed=true;
		options.inPreferredConfig=config;
		return BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length, options);
	}

	@Override
	public void image(Image image) throws Throwable {
		if (renderscript) {
			imageRenderscript(image);
		}
		else {
			imageJava(image);
		}
	}
	
	private void imageJava(Image image) {
		int[] ints=null;
		short[] shorts=null;
		int height;
		int width;
		Bitmap bitmap=bitmap(config);
		try {
			config=bitmap.getConfig();
			height=bitmap.getHeight();
			width=bitmap.getWidth();
			switch (config) {
				case ARGB_8888:
					ints=new int[height*width];
					bitmap.copyPixelsToBuffer(IntBuffer.wrap(ints));
					break;
				case RGB_565:
					shorts=new short[height*width];
					bitmap.copyPixelsToBuffer(ShortBuffer.wrap(shorts));
					break;
				default:
					throw new RuntimeException(config+" is not supported");
			}
		}
		finally {
			bitmap.recycle();
		}
		int length=height*width;
		byteArrayPool.clearExcept(length);
		byte[] brightness=byteArrayPool.acquire(length);
		switch (config) {
			case ARGB_8888:
				for (int ii=length-1; 0<=ii; --ii) {
					int pixel=ints[ii];
					int red=(pixel>>16)&0xff;
					int green=(pixel>>7)&0x1fe;
					int blue=pixel&0xff;
					brightness[ii]=(byte)((red+green+blue)>>2);
				}
				break;
			case RGB_565:
				for (int ii=length-1; 0<=ii; --ii) {
					int pixel=shorts[ii];
					int red=(pixel>>10)&0x3e;
					int green=(pixel>>4)&0x7e;
					int blue=(pixel<<1)&0x3e;
					brightness[ii]=(byte)(red+green+blue);
				}
				break;
			default:
				throw new RuntimeException(config+" is not supported");
		}
		image.brightness=brightness;
		image.height=height;
		image.width=width;
	}
	
	private void imageRenderscript(Image image) {
		byte[] brightness;
		int height;
		int width;
		Bitmap bitmap=bitmap(config);
		try {
			config=bitmap.getConfig();
			height=bitmap.getHeight();
			width=bitmap.getWidth();
			byteArrayPool.clearExcept(height*width);
			brightness=byteArrayPool.acquire(height*width);
			RenderScript renderScript=RenderScript.create(context);
			try {
				Allocation in
						=Allocation.createFromBitmap(renderScript, bitmap);
				try {
					Element outElement=Element.U8(renderScript);
					try {
						Type outType=new Type.Builder(renderScript, outElement)
								.setX(width)
								.setY(height)
								.create();
						try {
							Allocation out=Allocation.createTyped(
									renderScript, outType);
							try {
								switch (config) {
									case ARGB_8888:
										ScriptC_BrightnessARGB8888
												brightnessARGB8888
													=new ScriptC_BrightnessARGB8888(
														renderScript);
										try {
											brightnessARGB8888
													.forEach_root(in, out);
										}
										finally {
											brightnessARGB8888.destroy();
										}
										break;
									case RGB_565:
										ScriptC_BrightnessRGB565
												brightnessRGB565
													=new ScriptC_BrightnessRGB565(
															renderScript);
										try {
											brightnessRGB565.set_in(in);
											brightnessRGB565.forEach_root(out);
										}
										finally {
											brightnessRGB565.destroy();
										}
										break;
									default:
										throw new RuntimeException(
												config+" is not supported");
								}
								out.copyTo(brightness);
							}
							finally {
								out.destroy();
							}
						}
						finally {
							outType.destroy();
						}
					}
					finally {
						outElement.destroy();
					}
				}
				finally {
					in.destroy();
				}
			}
			finally {
				renderScript.destroy();
			}
		}
		finally {
			bitmap.recycle();
		}
		image.brightness=brightness;
		image.height=height;
		image.width=width;
	}

	@Override
	public void release() {
		imageFactoryPool.release(this);
	}

	@Override
	public void release(Image image) {
		byteArrayPool.release(image.brightness);
		image.scrub();
	}
	
	public void scrub() {
		byteArrayPool=null;
		config=null;
		context=null;
		imageFactoryPool=null;
		jpeg=null;
	}
}
