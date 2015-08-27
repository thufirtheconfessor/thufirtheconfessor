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

import android.app.Activity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import confessor.thufir.lib.typein.Program;
import confessor.thufir.lib.typein.Type;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ManualActivity extends Activity {
	public static final String MANUAL="manual.html";
	public static final String PACKAGE
			=ManualActivity.class.getPackage().getName().replaceAll("\\.", "/")
					+"/";
	public static final String SOURCE="source.tar.gz";
	
	private class WebViewClientImpl extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (null!=url) {
				if (SOURCE.equals(url)) {
					source();
					return true;
				}
				if (typeIn(url)) {
					return true;
				}
			}
			return super.shouldOverrideUrlLoading(view, url);
		}
		
		private void source() {
			File file=Files.file(SOURCE);
			try {
				InputStream inputStream=getClassLoader().getResourceAsStream(
						PACKAGE+SOURCE);
				if (null!=inputStream) {
					try {
						InputStream bufferedInputStream
								=new BufferedInputStream(inputStream);
						try {
							OutputStream outputStream
									=new FileOutputStream(file);
							try {
								OutputStream bufferedOutputStream
										=new BufferedOutputStream(outputStream);
								try {
									byte[] buf=new byte[4096];
									for (int rr; 0<=(rr
											=bufferedInputStream.read(buf)); ) {
										bufferedOutputStream.write(buf, 0, rr);
									}
								}
								finally {
									bufferedOutputStream.close();
								}
							}
							finally {
								outputStream.close();
							}
						}
						finally {
							bufferedInputStream.close();
						}
					}
					finally {
						inputStream.close();
					}
					Toast.makeText(ManualActivity.this,
								String.format(
										"Sources were saved to %1$s",
										file.getAbsolutePath()),
								Toast.LENGTH_SHORT)
							.show();
				}
			}
			catch (Throwable throwable) {
				AndroidErrorHandler.d(throwable);
				Toast.makeText(ManualActivity.this, throwable.toString(),
							Toast.LENGTH_SHORT)
						.show();
			}
		}
		
		private boolean typeIn(String url) {
			try {
				int index=url.indexOf('/');
				if (0<=index) {
					return typeIn(
							url.substring(0, index),
							url.substring(index+1, url.length()));
				}
			}
			catch (Throwable throwable) {
				AndroidErrorHandler.d(throwable);
				Toast.makeText(ManualActivity.this, throwable.toString(),
							Toast.LENGTH_SHORT)
						.show();
			}
			return false;
		}
		
		private boolean typeIn(String type, String program) throws Throwable {
			for (Type type2: Files.types()) {
				if (type.equals(type2.name())) {
					for (Program program2: type2.programs()) {
						if (program.equals(program2.name())) {
							TypeInActivity.startActivity(
									ManualActivity.this, type2, program2);
							return true;
						}
					}
				}
			}
			return false;
		}
	}
	
	@Override
	public void onBackPressed() {
		setContentView(new View(this));
		super.onBackPressed();
	}
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.manual);
		WebView webView=(WebView)findViewById(R.id.manualView);
		webView.setWebViewClient(new WebViewClientImpl());
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setSupportZoom(true);
		try {
			InputStream inputStream=getClassLoader().getResourceAsStream(
					PACKAGE+MANUAL);
			if (null!=inputStream) {
				try {
					ByteArrayOutputStream outputStream
							=new ByteArrayOutputStream();
					try {
						byte[] buf=new byte[4096];
						int rr;
						while (0<=(rr=inputStream.read(buf))) {
							outputStream.write(buf, 0, rr);
						}
					}
					finally {
						outputStream.close();
					}
					webView.loadData(
							Base64.encodeToString(outputStream.toByteArray(),
									Base64.DEFAULT),
							"text/html; charset=UTF-8", "base64");
				}
				finally {
					inputStream.close();
				}
			}
		}
		catch (IOException ex) {
			AndroidErrorHandler.d(ex);
		}
	}
}
