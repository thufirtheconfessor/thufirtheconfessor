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
package confessor.thufir.lib.stream;

import confessor.thufir.lib.MutableString;

public interface FileProgressStream extends ErrorHandler {
	void data(boolean clock, int crc, byte[] data, int length, int offset)
			throws Throwable;
	void dataProgress(int max, int progress, float speedInBytesPerSec)
			throws Throwable;
	void finished() throws Throwable;
	void length(int length) throws Throwable;
	void lengthProgress(int max, int progress) throws Throwable;
	void name(MutableString name) throws Throwable;
	void nameLengthProgress(int max, int progress) throws Throwable;
	void nameProgress(int max, MutableString partialName, int progress)
			throws Throwable;
}
