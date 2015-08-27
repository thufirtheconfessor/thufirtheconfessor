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

import confessor.thufir.Env;
import confessor.thufir.Files;
import confessor.thufir.lib.file.FileSaver;
import confessor.thufir.lib.file.FileStream;
import confessor.thufir.lib.stream.FileProgressStream;
import confessor.thufir.lib.stream.ImageGeometryStream;
import confessor.thufir.lib.stream.ImageStream;
import confessor.thufir.lib.thufir4.Thufir4Data;
import confessor.thufir.lib.thufir4.Thufir4Geometry;
import confessor.thufir.lib.thufir4.Thufir4Job;
import confessor.thufir.lib.thufir4.Thufir4Stream;
import confessor.thufir.lib.tracking.DetectorStream;
import confessor.thufir.settings.SettingsBrightnessClassifierInitializer;
import confessor.thufir.settings.SettingsManager;
import java.util.List;

public class ReceiveScreenFactory extends OverlayScreenFactory {
	public static final String TAG="receive";

	public ReceiveScreenFactory(Env env, ScreenFactory parent,
			Integer yScroll) {
		super(env, parent, TAG, yScroll);
	}
	
	public static ReceiveScreenFactory restore(List<String> bundle, Env env,
			ScreenFactory parent) throws Throwable {
		return new ReceiveScreenFactory(env, parent,
				restoreYScroll(bundle, 1));
	}

	@Override
	public ScreenView screenView(ScreenView currentView) throws Throwable {
		return new ReceiveScreenView(env, this, yScroll) {
			@Override
			public ImageStream imageStream(
					FileProgressStream fileProgressStream) throws Throwable {
				FileStream fileStream=new FileStream(fileProgressStream);
				boolean success=false;
				FileSaver fileSaver=FileSaver.continuePartialDirectory(
						Files.directory());
				if (null!=fileSaver) {
					try {
						setFileSaver(fileSaver);
						fileStream.setState(fileSaver.resultFile().getName(),
								(int)fileSaver.size(), (int)fileSaver.saved());
						success=true;
					}
					finally {
						if (!success) {
							fileSaver.close();
						}
					}
				}
				SettingsManager settingsManager=env.settingsManager();
				Thufir4Stream thufir4Stream=new Thufir4Stream(
						settingsManager.distortionCorrectionSetting()
								.getTyped(settingsManager)
								.imageReader(),
						fileStream,
						env.threadPool());
				if (null!=fileSaver) {
					thufir4Stream.setState(
							fileSaver.lastClock(), fileSaver.lastCrc());
				}
				ImageGeometryStream<Thufir4Data>
						previewStream=env.screen().wrapStream(thufir4Stream, this);
				DetectorStream<Thufir4Data, Thufir4Geometry, Thufir4Job>
						detectorStream=new DetectorStream
									<Thufir4Data, Thufir4Geometry, Thufir4Job>(
								settingsManager.blobSizeSetting()
										.getTyped(settingsManager),
								settingsManager.brightnessSchemeSetting()
										.getTyped(env.settingsManager())
										.bright(),
								new SettingsBrightnessClassifierInitializer(
										settingsManager, env.threadPool()),
								settingsManager.filterSetting()
										.getTyped(settingsManager),
								new Thufir4Geometry(), previewStream,
											env.threadPool());
				return detectorStream;
			}
		};
	}

	@Override
	public OverlayScreenFactory yScroll(Integer yScroll) {
		return new ReceiveScreenFactory(env, parent, yScroll);
	}
}
