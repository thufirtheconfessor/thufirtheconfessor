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
package confessor.thufir.lib.image;

import confessor.thufir.lib.thread.ThreadPool;
import confessor.thufir.lib.tracking.GeometryData;

public abstract class AbstractImageReader<D extends GeometryData>
		implements ImageReader<D> {
	protected D data;
	protected Image image;
	protected ThreadPool threadPool;

	@Override
	public void init(D data, Image image, ThreadPool threadPool)
			throws Throwable {
		this.data=data;
		this.image=image;
		this.threadPool=threadPool;
		initImpl();
	}
	
	protected abstract void initImpl() throws Throwable;

	@Override
	public void scrub() {
		data=null;
		image=null;
		threadPool=null;
	}
}
