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
package confessor.thufir.widgets.screen;

import android.os.Environment;
import android.view.View;
import confessor.thufir.Env;
import confessor.thufir.Files;
import confessor.thufir.lib.MutableString;
import confessor.thufir.lib.file.AsyncFileSaver;
import confessor.thufir.lib.file.FileSaver;
import confessor.thufir.lib.file.FileType;
import confessor.thufir.lib.stream.FileProgressStream;
import confessor.thufir.lib.stream.ImageStream;
import confessor.thufir.widgets.ProgressView;
import java.io.File;

public abstract class ReceiveScreenView extends OverlayScreenView {
	private static final String SAVING="Saving";
	private static final String STARTING="Waiting to start";
	private static final int STATE_DATA=0x40000000;
	private static final int STATE_DATA_LENGTH=0x60000000;
	private static final int STATE_NAME=0x80000000;
	private static final int STATE_NAME_LENGTH=0xa0000000;
	private static final int STATE_SAVING=0xc0000000;
	private static final int STATE_STARTING=0xe0000000;
	
	protected class FileProgressStreamImpl implements FileProgressStream {
		@Override
		public void data(boolean clock, int crc, byte[] data, int length,
				int offset) throws Throwable {
			synchronized (lock) {
				asyncfileSaver.data(clock, crc, data, length, offset);
			}
		}

		@Override
		public void dataProgress(int max, int progress,
				float speedInBytesPerSec) throws Throwable {
			synchronized (lock) {
				int state=STATE_DATA|max;
				if (widthState!=state) {
					widthState=state;
					progressView.postProgressWidth(max);
				}
				progressView.postMessage(name);
				progressView.postProgress(progress, max, speedInBytesPerSec);
			}
		}

		@Override
		public void error(Throwable throwable) {
			ReceiveScreenView.this.error(throwable);
		}

		@Override
		public void finished() throws Throwable {
			synchronized (lock) {
				int state=STATE_SAVING;
				if (widthState!=state) {
					widthState=state;
					progressView.postProgressWidth(1);
				}
				progressView.postMessage(SAVING);
				progressView.postProgress(0, 1, 0);
			}
		}

		@Override
		public void length(int length) throws Throwable {
			synchronized (lock) {
				if (!checkStorage()) {
					return;
				}
				boolean success=false;
				try {
					if (null==fileSaver) {
						fileSaver=FileSaver.create(Files.directory(),
								name.toString(), length);
						subCloseables.add(fileSaver);
					}
					if (null==asyncfileSaver) {
						asyncfileSaver=new AsyncFileSaver(fileSaver,
								FileType.FILE_TYPES, new FileSaverListener());
						subCloseables.add(asyncfileSaver);
					}
					success=true;
				}
				finally {
					try {
						if ((!success)
								&& (null!=asyncfileSaver)) {
							asyncfileSaver.close();
						}
					}
					finally {
						if ((!success)
								&& (null!=fileSaver)) {
							fileSaver.close();
						}
					}
				}
			}
		}

		@Override
		public void lengthProgress(int max, int progress) throws Throwable {
			synchronized (lock) {
				int state=STATE_DATA_LENGTH|max;
				if (widthState!=state) {
					widthState=state;
					progressView.postProgressWidth(max);
				}
				progressView.postMessage(name);
				progressView.postProgress(progress, max, 0);
			}
		}

		@Override
		public void name(MutableString name) throws Throwable {
			synchronized (lock) {
				ReceiveScreenView.this.name.clear();
				ReceiveScreenView.this.name.append(name);
			}
		}

		@Override
		public void nameLengthProgress(int max, int progress)
				throws Throwable {
			synchronized (lock) {
				int state=STATE_NAME_LENGTH|max;
				if (widthState!=state) {
					widthState=state;
					progressView.postProgressWidth(max);
				}
				progressView.postMessage(STARTING);
				progressView.postProgress(progress, max, 0);
			}
		}

		@Override
		public void nameProgress(int max, MutableString partialName,
				int progress) throws Throwable {
			synchronized (lock) {
				ReceiveScreenView.this.name.clear();
				ReceiveScreenView.this.name.append(partialName);
				int state=STATE_NAME|max;
				if (widthState!=state) {
					widthState=state;
					progressView.postProgressWidth(max);
				}
				progressView.postMessage(partialName);
				progressView.postProgress(progress, max, 0);
			}
		}
	}
	
	private class FileSaverListener implements AsyncFileSaver.Listener {
		@Override
		public void error(FileSaver fileSaver, Throwable throwable) {
			ReceiveScreenView.this.error(throwable);
		}

		@Override
		public void paused(FileSaver fileSaver) {
			try {
				ReceiveScreenView.this.push(ReceiveScreenView.this.pop());
			}
			catch (Throwable throwable) {
				error(fileSaver, throwable);
			}
		}

		@Override
		public void saved(FileSaver fileSaver, FileType fileType,
				boolean valid) {
			try {
				if (null==fileType) {
					message(false, String.format(
							"File saved as %1$s.",
							fileSaver.resultFile().getName()));
				}
				else if (valid) {
					message(false, String.format(
							"Valid %2$s file saved as %1$s.",
							fileSaver.resultFile().getName(),
							fileType.name()));
				}
				else {
					message(true, String.format(
							"Invalid %2$s file saved as %1$s.",
							fileSaver.resultFile().getName(),
							fileType.name()));
				}
			}
			catch (Throwable throwable) {
				ReceiveScreenView.this.error(throwable);
			}
		}

		@Override
		public void stopped(FileSaver fileSaver) {
			try {
				ReceiveScreenView.this.push(ReceiveScreenView.this.pop());
			}
			catch (Throwable throwable) {
				error(fileSaver, throwable);
			}
			if (fileSaver.partialFile().exists()) {
				fileSaver.partialFile().delete();
			}
		}
	}
	
	private class PauseListener extends PopButtonListener {
		public PauseListener(Env env, EnvScreenView view) {
			super(env, view);
		}

		@Override
		protected void onClickImpl(View view) throws Throwable {
			if (null==asyncfileSaver) {
				super.onClickImpl(view);
			}
			else {
				asyncfileSaver.pause();
			}
		}
	}
	
	private class StopListener extends PopButtonListener {
		public StopListener(Env env, EnvScreenView view) {
			super(env, view);
		}

		@Override
		protected void onClickImpl(View view) throws Throwable {
			if (null==asyncfileSaver) {
				super.onClickImpl(view);
			}
			else {
				asyncfileSaver.stop();
			}
		}
	}
	
	private AsyncFileSaver asyncfileSaver;
	private FileSaver fileSaver;
	private final ImageStream imageStream;
	protected final Object lock=new Object();
	private final MutableString name=new MutableString();
	private final ProgressView progressView;
	private int widthState=STATE_STARTING;
	
	public ReceiveScreenView(Env env, OverlayScreenFactory parent,
			Integer yScroll) throws Throwable {
		super(env, parent, yScroll);
		progressView=new ProgressView(env.context(), env.formatter(),
				env.handler());
		
		progressView.postProgressWidth(1);
		progressView.postMessage(STARTING);
		progressView.postProgress(0, 1, 0f);
		
		setButtons(
				button("Pause", true, new PauseListener(env, this)),
				button("Stop", new StopListener(env, this)));
		setContent(progressView);
		
		imageStream=imageStream(new FileProgressStreamImpl());
	}

	private boolean checkStorage() throws Throwable {
		if (!Environment.MEDIA_MOUNTED.equals(
				Environment.getExternalStorageState())) {
			message(true, "External storage is not mounted.");
			return false;
		}
		File file=Files.directory();
		file.mkdirs();
		if (!file.exists()) {
			message(true,
					String.format("%1$s doesn't exist.", file.getPath()));
			return false;
		}
		if (!file.canWrite()) {
			message(true,
					String.format("%1$s is not writeable.", file.getPath()));
			return false;
		}
		return true;
	}

	@Override
	public ImageStream imageStream() {
		return imageStream;
	}
	
	protected abstract ImageStream imageStream(
			FileProgressStream fileProgressStream) throws Throwable;
	
	private void message(boolean error, String message) throws Throwable {
		env.screen().postFactory(new MessageScreenFactory(
				env, new Message(error, message), parent.parent()));
	}
	
	public void setFileSaver(FileSaver fileSaver) {
		this.fileSaver=fileSaver;
		subCloseables.add(fileSaver);
	}
}
