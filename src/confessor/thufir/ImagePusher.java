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
package confessor.thufir;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import confessor.thufir.image.JpegImageFactory;
import confessor.thufir.image.JpegImageFactoryPool;
import confessor.thufir.lib.collection.ByteArrayPool;
import confessor.thufir.lib.collection.Pool;
import confessor.thufir.lib.image.Yuv420spImageFactory;
import confessor.thufir.lib.image.Yuv420spImageFactoryPool;
import confessor.thufir.lib.stream.AsyncImageStreamHolder;
import confessor.thufir.settings.SettingsManager;
import confessor.thufir.settings.program.ImageSource;

public class ImagePusher {
	private final AsyncImageStreamHolder asyncImageStreamHolder;
	private final ByteArrayPool byteArrayPool=new ByteArrayPool(1, 1);
	private final CameraHolder cameraHolder;
	private final Context context;
	private final JpegImageFactoryPool jpegImageFactoryPool
			=new JpegImageFactoryPool(CameraHolder.PREVIEW_BUFFERS, null);
	private final PreviewInfo previewInfo;
	private final SettingsManager settingsManager;
	private final Yuv420spImageFactoryPool yuv420spImageFactoryPool
			=new Yuv420spImageFactoryPool(CameraHolder.PREVIEW_BUFFERS, null);

	public ImagePusher(AsyncImageStreamHolder asyncImageStreamHolder,
			CameraHolder cameraHolder, Context context,
			PreviewInfo previewInfo, SettingsManager settingsManager) {
		this.asyncImageStreamHolder=asyncImageStreamHolder;
		this.cameraHolder=cameraHolder;
		this.context=context;
		this.previewInfo=previewInfo;
		this.settingsManager=settingsManager;
	}

	public void onPictureTaken(byte[] bytes, Camera camera) {
		try {
			if (null==bytes) {
				return;
			}
			if (!asyncImageStreamHolder.hasStream()) {
				return;
			}
			ImageSource imageSource=settingsManager.imageSourceSetting()
					.getTyped(settingsManager);
			if (ImageSource.PICTURE!=imageSource) {
				return;
			}
			try {
				JpegImageFactory imageFactory=jpegImageFactoryPool.acquire();
				imageFactory.byteArrayPool=byteArrayPool;
				imageFactory.config=Bitmap.Config.RGB_565;
				imageFactory.context=context;
				imageFactory.imageFactoryPool=jpegImageFactoryPool;
				imageFactory.jpeg=bytes;
				imageFactory.renderscript=true;
				asyncImageStreamHolder.next(imageFactory);
			}
			catch (Throwable throwable) {
				asyncImageStreamHolder.error(throwable);
			}
		}
		catch (Throwable throwable) {
			AndroidErrorHandler.d(throwable);
		}
	}
	
	public void onPreviewFrame(byte[] bytes, Camera camera,
			Pool<byte[]> pool) {
		try {
			if (null==bytes) {
				return;
			}
			boolean success=false;
			try {
				if (!asyncImageStreamHolder.hasStream()) {
					return;
				}
				ImageSource imageSource=settingsManager.imageSourceSetting()
						.getTyped(settingsManager);
				if (ImageSource.PREVIEW!=imageSource) {
					if (ImageSource.PICTURE==imageSource) {
						cameraHolder.takePicture();
					}
					return;
				}
				try {
					Camera.Size size=previewInfo.size();
					if (!Yuv420spImageFactory.valid(bytes, size.height,
							size.width)) {
						return;
					}
					boolean success2=false;
					Yuv420spImageFactory imageFactory
							=yuv420spImageFactoryPool.acquire();
					imageFactory.byteArrayPool=pool;
					imageFactory.data=bytes;
					imageFactory.height=size.height;
					imageFactory.imageFactoryPool=yuv420spImageFactoryPool;
					imageFactory.width=size.width;
					try {
						success=true;
						asyncImageStreamHolder.next(imageFactory);
						success2=true;
					}
					finally {
						if (!success2) {
							imageFactory.release();
						}
					}
				}
				catch (Throwable throwable) {
					asyncImageStreamHolder.error(throwable);
				}
			}
			finally {
				if (!success) {
					pool.release(bytes);
				}
			}
		}
		catch (Throwable throwable) {
			AndroidErrorHandler.d(throwable);
		}
	}
}
