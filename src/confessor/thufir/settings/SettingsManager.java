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
package confessor.thufir.settings;

import android.content.Context;
import android.content.SharedPreferences;
import confessor.thufir.CameraHolder;
import confessor.thufir.settings.camera.AntibandingSetting;
import confessor.thufir.settings.camera.BrightnessSetting;
import confessor.thufir.settings.camera.CameraParameterSetting;
import confessor.thufir.settings.camera.CameraSetting;
import confessor.thufir.settings.camera.ColorEffectSetting;
import confessor.thufir.settings.camera.ContrastSetting;
import confessor.thufir.settings.camera.ExposureCompensationSetting;
import confessor.thufir.settings.camera.FlashModeSetting;
import confessor.thufir.settings.camera.FocusModeSetting;
import confessor.thufir.settings.camera.IsoSetting;
import confessor.thufir.settings.camera.MeterModeSetting;
import confessor.thufir.settings.camera.PictureSizeSetting;
import confessor.thufir.settings.camera.PreviewFpsRangeSetting;
import confessor.thufir.settings.camera.PreviewSizeSetting;
import confessor.thufir.settings.camera.SaturationSetting;
import confessor.thufir.settings.camera.SceneModeSetting;
import confessor.thufir.settings.camera.SharpnessSetting;
import confessor.thufir.settings.camera.WhiteBalanceSetting;
import confessor.thufir.settings.program.BlobSizeSetting;
import confessor.thufir.settings.program.BrightnessSchemeSetting;
import confessor.thufir.settings.program.DiagnosticsSetting;
import confessor.thufir.settings.program.DistortionCorrectionSetting;
import confessor.thufir.settings.program.FilterSetting;
import confessor.thufir.settings.program.ImageSourceSetting;
import confessor.thufir.settings.program.OverlayButtonsPositionSetting;
import confessor.thufir.settings.program.ParallelismSetting;
import confessor.thufir.settings.program.ThresholdModeSetting;
import confessor.thufir.settings.program.ThresholdSetting;
import confessor.thufir.settings.typein.KeepScreenOnSetting;
import confessor.thufir.settings.typein.TextLayoutSetting;
import confessor.thufir.settings.typein.TextSizeSetting;
import confessor.thufir.settings.typein.VisibleWhitespacesSetting;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsManager {
	private final AntibandingSetting antibandingSetting;
	private final BlobSizeSetting blobSizeSetting;
	private final BrightnessSchemeSetting brightnessSchemeSetting;
	private final BrightnessSetting brightnessSetting;
	private CameraHolder cameraHolder;
	private final List<CameraParameterSetting<?>> cameraParameterSettingsList;
	private final CameraSetting cameraSetting;
	private final ColorEffectSetting colorEffectSetting;
	private final ContrastSetting contrastSetting;
	private final DiagnosticsSetting diagnosticsSetting;
	private final DistortionCorrectionSetting distortionCorrectionSetting;
	private final ExposureCompensationSetting exposureCompensationSetting;
	private final FilterSetting filterSetting;
	private final FlashModeSetting flashModeSetting;
	private final FocusModeSetting focusModeSetting;
	private final ImageSourceSetting imageSourceSetting;
	private final IsoSetting isoSetting;
	private final KeepScreenOnSetting keepScreenOnSetting;
	private final Object lock=new Object();
	private final MeterModeSetting meterModeSetting;
	private final OverlayButtonsPositionSetting overlayButtonsPositionSetting;
	private final ParallelismSetting parallelismSetting;
	private final PictureSizeSetting pictureSizeSetting;
	private final PreviewFpsRangeSetting previewFpsRangeSetting;
	private final PreviewSizeSetting previewSizeSetting;
	private final SaturationSetting saturationSetting;
	private final SceneModeSetting sceneModeSetting;
	private Settings settings;
	private final SharpnessSetting sharpnessSetting;
	private final Map<SettingCategory, List<Setting>> settingsListByCategory
			=new EnumMap<SettingCategory, List<Setting>>(
					SettingCategory.class);
	private final List<Setting> settingsList;
	private final SharedPreferences sharedPreferences;
	private final TextLayoutSetting textLayoutSetting;
	private final TextSizeSetting textSizeSetting;
	private final ThresholdModeSetting thresholdModeSetting;
	private final ThresholdSetting thresholdSetting;
	private final VisibleWhitespacesSetting visibleWhitespacesSetting;
	private final WhiteBalanceSetting whiteBalanceSetting;

	public SettingsManager(Context context,
			SharedPreferences sharedPreferences) {
		this.sharedPreferences=sharedPreferences;
		
		antibandingSetting=new AntibandingSetting();
		blobSizeSetting=new BlobSizeSetting();
		brightnessSchemeSetting=new BrightnessSchemeSetting();
		brightnessSetting=new BrightnessSetting();
		cameraSetting=new CameraSetting();
		colorEffectSetting=new ColorEffectSetting();
		contrastSetting=new ContrastSetting();
		diagnosticsSetting=new DiagnosticsSetting();
		distortionCorrectionSetting=new DistortionCorrectionSetting();
		exposureCompensationSetting=new ExposureCompensationSetting();
		filterSetting=new FilterSetting(context);
		flashModeSetting=new FlashModeSetting();
		focusModeSetting=new FocusModeSetting();
		imageSourceSetting=new ImageSourceSetting();
		isoSetting=new IsoSetting();
		keepScreenOnSetting=new KeepScreenOnSetting();
		meterModeSetting=new MeterModeSetting();
		overlayButtonsPositionSetting=new OverlayButtonsPositionSetting();
		parallelismSetting=new ParallelismSetting();
		pictureSizeSetting=new PictureSizeSetting();
		previewFpsRangeSetting=new PreviewFpsRangeSetting();
		previewSizeSetting=new PreviewSizeSetting();
		saturationSetting=new SaturationSetting();
		sceneModeSetting=new SceneModeSetting();
		sharpnessSetting=new SharpnessSetting();
		textLayoutSetting=new TextLayoutSetting();
		textSizeSetting=new TextSizeSetting();
		thresholdModeSetting=new ThresholdModeSetting();
		thresholdSetting=new ThresholdSetting();
		visibleWhitespacesSetting=new VisibleWhitespacesSetting();
		whiteBalanceSetting=new WhiteBalanceSetting();
		
		List<CameraParameterSetting<?>> cameraParameter2
				=new ArrayList<CameraParameterSetting<?>>();
		List<Setting> settings2=new ArrayList<Setting>();
		
		for (SettingCategory category: SettingCategory.values()) {
			settingsListByCategory.put(category, new ArrayList<Setting>());
		}
		
		add(cameraSetting, cameraParameter2, settings2);
		add(antibandingSetting, cameraParameter2, settings2);
		add(brightnessSetting, cameraParameter2, settings2);
		add(colorEffectSetting, cameraParameter2, settings2);
		add(contrastSetting, cameraParameter2, settings2);
		add(exposureCompensationSetting, cameraParameter2, settings2);
		add(flashModeSetting, cameraParameter2, settings2);
		add(focusModeSetting, cameraParameter2, settings2);
		add(isoSetting, cameraParameter2, settings2);
		add(meterModeSetting, cameraParameter2, settings2);
		add(pictureSizeSetting, cameraParameter2, settings2);
		add(previewFpsRangeSetting, cameraParameter2, settings2);
		add(previewSizeSetting, cameraParameter2, settings2);
		add(saturationSetting, cameraParameter2, settings2);
		add(sceneModeSetting, cameraParameter2, settings2);
		add(sharpnessSetting, cameraParameter2, settings2);
		add(whiteBalanceSetting, cameraParameter2, settings2);
		
		add(blobSizeSetting, cameraParameter2, settings2);
		add(brightnessSchemeSetting, cameraParameter2, settings2);
		add(overlayButtonsPositionSetting, cameraParameter2, settings2);
		add(diagnosticsSetting, cameraParameter2, settings2);
		add(distortionCorrectionSetting, cameraParameter2, settings2);
		add(filterSetting, cameraParameter2, settings2);
		add(imageSourceSetting, cameraParameter2, settings2);
		add(parallelismSetting, cameraParameter2, settings2);
		add(thresholdSetting, cameraParameter2, settings2);
		add(thresholdModeSetting, cameraParameter2, settings2);
		
		add(keepScreenOnSetting, cameraParameter2, settings2);
		add(textLayoutSetting, cameraParameter2, settings2);
		add(textSizeSetting, cameraParameter2, settings2);
		add(visibleWhitespacesSetting, cameraParameter2, settings2);
		
		cameraParameterSettingsList
				=Collections.unmodifiableList(cameraParameter2);
		settingsList=Collections.unmodifiableList(settings2);
		for (Map.Entry<SettingCategory, List<Setting>> entry:
				settingsListByCategory.entrySet()) {
			entry.setValue(Collections.unmodifiableList(entry.getValue()));
		}
	}

	public SettingsManager(Context context) {
		this(context, sharedPreferences(context));
	}
	
	private void add(Setting setting,
			List<CameraParameterSetting<?>> cameraParameter, 
			List<Setting> settings) {
		settings.add(setting);
		List<Setting> categoryList
				=settingsListByCategory.get(setting.category());
		categoryList.add(setting);
		if (setting instanceof CameraParameterSetting) {
			cameraParameter.add((CameraParameterSetting<?>)setting);
		}
	}

	public BlobSizeSetting blobSizeSetting() {
		return blobSizeSetting;
	}

	public BrightnessSchemeSetting brightnessSchemeSetting() {
		return brightnessSchemeSetting;
	}
	
	public void cameraHolder(CameraHolder cameraHolder) {
		this.cameraHolder=cameraHolder;
	}

	public List<CameraParameterSetting<?>> cameraParameterSettingsList() {
		return cameraParameterSettingsList;
	}

	public CameraSetting cameraSetting() {
		return cameraSetting;
	}

	public DiagnosticsSetting diagnosticsSetting() {
		return diagnosticsSetting;
	}

	public DistortionCorrectionSetting distortionCorrectionSetting() {
		return distortionCorrectionSetting;
	}
	
	public FilterSetting filterSetting() {
		return filterSetting;
	}
	
	public Settings get() {
		synchronized (lock) {
			if (null==settings) {
				SettingsMap map=new SettingsMap(sharedPreferences.getAll());
				Map<Setting, Object> settingValues
						=new HashMap<Setting, Object>();
				Settings settings2=new Settings(
						Collections.unmodifiableMap(settingValues));
				settingValues.put(cameraSetting,
						cameraSetting.get(cameraHolder, map, settings2, this));
				settings2=new Settings(
						Collections.unmodifiableMap(settingValues));
				for (Setting setting: settingsList) {
					if (setting.valid(cameraHolder, settings2, this)) {
						settingValues.put(setting, setting.get(cameraHolder,
								map, settings2, this));
					}
				}
				settings=validate(new Settings(
						Collections.unmodifiableMap(settingValues)));
			}
			return settings;
		}
	}

	public ImageSourceSetting imageSourceSetting() {
		return imageSourceSetting;
	}

	public OverlayButtonsPositionSetting overlayButtonsPositionSetting() {
		return overlayButtonsPositionSetting;
	}

	public ParallelismSetting parallelismSetting() {
		return parallelismSetting;
	}
	
	public Settings reset() {
		synchronized (lock) {
			this.settings=null;
			SharedPreferences.Editor editor=sharedPreferences.edit();
			editor.clear();
			editor.apply();
			return get();
		}
	}
	
	public static SharedPreferences sharedPreferences(Context context) {
		return context.getSharedPreferences("settings", Context.MODE_PRIVATE);
	}
	
	public Settings set(Settings settings) {
		settings=validate(settings);
		synchronized (lock) {
			this.settings=null;
			SharedPreferences.Editor editor=sharedPreferences.edit();
			editor.clear();
			for (Map.Entry<Setting, Object> entry:
					settings.settingValues.entrySet()) {
				Setting setting=entry.getKey();
				if (setting.valid(cameraHolder, settings, this)) {
					setting.put(editor, entry.getValue());
				}
			}
			editor.apply();
			return get();
		}
	}

	public List<Setting> settingsList() {
		return settingsList;
	}

	public List<Setting> settingsList(SettingCategory category) {
		return settingsListByCategory.get(category);
	}

	public ThresholdModeSetting thresholdModeSetting() {
		return thresholdModeSetting;
	}

	public ThresholdSetting thresholdSetting() {
		return thresholdSetting;
	}
	
	public Object unvalidatedSetting(Setting setting) {
		synchronized (lock) {
			if (null!=settings) {
				return setting.get(settings);
			}
			SettingsMap map=new SettingsMap(sharedPreferences.getAll());
			Map<Setting, Object> settingValues
					=new HashMap<Setting, Object>();
			Settings settings2=new Settings(
					Collections.unmodifiableMap(settingValues));
			Object camera=cameraSetting
					.get(cameraHolder, map, settings2, this);
			if (cameraSetting.equals(setting)) {
				return camera;
			}
			settingValues.put(cameraSetting, camera);
			settings2=new Settings(
					Collections.unmodifiableMap(settingValues));
			return setting.get(cameraHolder, map, settings2, this);
		}
	}
	
	public <T> T unvalidatedSetting(TypedSetting<T> setting) {
		return setting.cast(unvalidatedSetting((Setting)setting));
	}
	
	private Settings validate(Settings settings) {
		Map<Setting, Object> settingValues
				=new HashMap<Setting, Object>(settingsList.size());
		for (Setting setting: settingsList) {
			if (!setting.valid(cameraHolder, settings, this)) {
				continue;
			}
			Object value=settings.settingValues.get(setting);
			if (!setting.valid(cameraHolder, settings, this, value)) {
				value=setting.defaultValue(cameraHolder, settings, this);
			}
			if (setting.valid(cameraHolder, settings, this, value)) {
				settingValues.put(setting, value);
			}
		}
		return new Settings(Collections.unmodifiableMap(settingValues));
	}
}
