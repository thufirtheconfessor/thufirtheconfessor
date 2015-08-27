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
package confessor.thufir.settings.program;

import confessor.thufir.lib.image.ImageReader;
import confessor.thufir.lib.thufir4.NaiveDistortionCorrectingThufir4ImageReader;
import confessor.thufir.lib.thufir4.SimpleThufir4ImageReader;
import confessor.thufir.lib.thufir4.Thufir4Data;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface DistortionCorrection {
	DistortionCorrection NO=new DistortionCorrection() {
		@Override
		public ImageReader<Thufir4Data> imageReader() {
			return new SimpleThufir4ImageReader();
		}
		
		@Override
		public String toString() {
			return "no";
		}
	};
	DistortionCorrection SOME_MULTI_THREADED_LINE=new DistortionCorrection() {
		@Override
		public ImageReader<Thufir4Data> imageReader() {
			return new NaiveDistortionCorrectingThufir4ImageReader
					.MultiLine();
		}
		
		@Override
		public String toString() {
			return "some - ml";
		}
	};
	DistortionCorrection SOME_MULTI_THREADED_LINE_MEASURE=new DistortionCorrection() {
		@Override
		public ImageReader<Thufir4Data> imageReader() {
			return new NaiveDistortionCorrectingThufir4ImageReader
					.MultiLineMeasure();
		}
		
		@Override
		public String toString() {
			return "some - mlm";
		}
	};
	DistortionCorrection SOME_MULTI_THREADED_MEASURE=new DistortionCorrection() {
		@Override
		public ImageReader<Thufir4Data> imageReader() {
			return new NaiveDistortionCorrectingThufir4ImageReader
					.MultiMeasure();
		}
		
		@Override
		public String toString() {
			return "some - mm";
		}
	};
	DistortionCorrection SOME_SINGLE_THREADED=new DistortionCorrection() {
		@Override
		public ImageReader<Thufir4Data> imageReader() {
			return new NaiveDistortionCorrectingThufir4ImageReader
					.Single();
		}
		
		@Override
		public String toString() {
			return "some - s";
		}
	};
	List<DistortionCorrection> VALUES
			=Collections.unmodifiableList(Arrays.asList(
					NO, SOME_MULTI_THREADED_LINE,
					SOME_MULTI_THREADED_LINE_MEASURE,
					SOME_MULTI_THREADED_MEASURE, SOME_SINGLE_THREADED));
	
	ImageReader<Thufir4Data> imageReader();
}
