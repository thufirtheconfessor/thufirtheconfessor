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
package confessor.thufir.lib.thufir4;

import confessor.thufir.lib.collection.ObjectPool;

public class Thufir4DataPool extends ObjectPool<Thufir4Data> {
	public Thufir4DataPool() {
		super(2, 2);
	}

	@Override
	protected Thufir4Data create() {
		return new Thufir4Data();
	}

	@Override
	protected void init(Thufir4Data object) {
	}

	@Override
	protected void scrub(Thufir4Data object) {
		object.scrub();
	}
}
