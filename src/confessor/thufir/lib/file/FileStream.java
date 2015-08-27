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
package confessor.thufir.lib.file;

import confessor.thufir.lib.MutableString;
import confessor.thufir.lib.stream.ByteStream;
import confessor.thufir.lib.stream.FileProgressStream;

public class FileStream implements ByteStream {
	public static enum State {
		DATA, DATA_LENGTH_0, DATA_LENGTH_1, DATA_LENGTH_2, DATA_LENGTH_3,
		FINISHED, NAME, NAME_LENGTH
	}
	
	private int dataLength;
	private int dataReceived;
	private final MutableString name=new MutableString();
	private int nameLength;
	private final Speed speed;
	private State state=State.NAME_LENGTH;
	private final FileProgressStream stream;

	public FileStream(Speed speed, FileProgressStream stream) {
		this.speed=speed;
		this.stream=stream;
	}

	public FileStream(FileProgressStream stream) {
		this(new Speed(), stream);
	}

	@Override
	public void error(Throwable throwable) {
		stream.error(throwable);
	}

	@Override
	public void next(boolean clock, int crc, byte[] bytes, int offset,
			int length) throws Throwable {
		float speedInBytesPerSec=speed.speedInBytesPerSec(length);
		loop: while (0<length) {
			int length2;
			switch (state) {
				case DATA:
					length2=Math.min(length, dataLength-dataReceived);
					if (0>=length2) {
						throw new IllegalStateException(
								"failed to reach finished state");
					}
					stream.data(clock, crc, bytes, length2, offset);
					dataReceived+=length2;
					offset+=length2;
					length-=length2;
					if (dataLength<=dataReceived) {
						state=State.FINISHED;
					}
					break;
				case DATA_LENGTH_0:
					dataLength=bytes[offset]&0xff;
					--length;
					++offset;
					state=State.DATA_LENGTH_1;
					break;
				case DATA_LENGTH_1:
					dataLength|=(bytes[offset]&0xff)<<8;
					--length;
					++offset;
					state=State.DATA_LENGTH_2;
					break;
				case DATA_LENGTH_2:
					dataLength|=(bytes[offset]&0xff)<<16;
					--length;
					++offset;
					state=State.DATA_LENGTH_3;
					break;
				case DATA_LENGTH_3:
					dataLength|=(bytes[offset]&0xff)<<24;
					--length;
					++offset;
					stream.length(dataLength);
					if (0>=dataLength) {
						stream.data(clock, crc, bytes, 0, offset);
						state=State.FINISHED;
					}
					else {
						state=State.DATA;
					}
					break;
				case FINISHED:
					break loop;
				case NAME:
					length2=Math.min(length, nameLength-name.length());
					while (0<length2) {
						char cc=(char)(bytes[offset]&0xff);
						cc=validateName(cc);
						name.append(cc);
						--length;
						--length2;
						++offset;
					}
					if (name.length()>=nameLength) {
						state=State.DATA_LENGTH_0;
						stream.name(name);
					}
					break;
				case NAME_LENGTH:
					nameLength=bytes[offset]&0xff;
					name.clear();
					--length;
					++offset;
					if (0>=nameLength) {
						state=State.DATA_LENGTH_0;
						stream.name(name);
					}
					else {
						state=State.NAME;
					}
					break;
				default:
					throw new IllegalStateException(String.format(
							"unknown state %1$s", state));
			}
		}
		nextState(speedInBytesPerSec);
	}
	
	private void nextState(float speedInBytesPerSec) throws Throwable {
		switch (state) {
			case DATA:
				stream.dataProgress(dataLength, dataReceived,
						speedInBytesPerSec);
				break;
			case DATA_LENGTH_0:
				stream.lengthProgress(4, 0);
				break;
			case DATA_LENGTH_1:
				stream.lengthProgress(4, 1);
				break;
			case DATA_LENGTH_2:
				stream.lengthProgress(4, 2);
				break;
			case DATA_LENGTH_3:
				stream.lengthProgress(4, 3);
				break;
			case FINISHED:
				stream.finished();
				break;
			case NAME:
				stream.nameProgress(nameLength, name, name.length());
				break;
			case NAME_LENGTH:
				stream.nameLengthProgress(1, 0);
				break;
			default:
				throw new RuntimeException(String.format(
						"unknown state %1$s", state));
		}
	}
	
	public void setState(String name, int dataLength, int dataReceived)
			throws Throwable {
		this.dataLength=dataLength;
		this.dataReceived=dataReceived;
		this.name.clear();
		this.name.append(name);
		this.nameLength=name.length();
		stream.name(this.name);
		stream.length(dataLength);
		if ((0>=this.dataLength)
				|| (this.dataReceived>=this.dataLength)) {
			state=State.FINISHED;
		}
		else {
			state=State.DATA;
		}
		nextState(0.0f);
	}
	
	public static char validateName(char cc) {
		if (('\u0020'>cc)
				|| ('\u007f'<cc)
				|| ('\u002f'==cc) //slash
				|| ('\u005c\u005c'==cc)) { //backslash
			return '-';
		}
		return cc;
	}
}
