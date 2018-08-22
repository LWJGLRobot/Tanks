/*******************************************************************************
 * Copyright (C) 2018 Anvar Sultanbekov
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 ******************************************************************************/
package ru.navarobot;

import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;

public class Laser {
	private long time;
	private Vec2 start, end;
	private boolean superLaser;

	public Laser(Vec2 start, Vec2 end, boolean superLaser) {
		time = System.currentTimeMillis();
		this.start = start.clone();
		this.end = end;
		this.superLaser = superLaser;
	}

	public boolean isSuperLaser() {
		return superLaser;
	}

	public boolean checkForLifeTime() {
		if (System.currentTimeMillis() - time > 200) {
			return true;
		} else {
			return false;
		}
	}

	public void shootSuperLaser(World world) {
		Fixture[] closestFixture = new Fixture[1];
		boolean[] tankFound = new boolean[1];
		Vec2[] closestPoint = new Vec2[1];
		float[] minL = new float[] { Float.MAX_VALUE };
		world.raycast(new RayCastCallback() {

			@Override
			public float reportFixture(Fixture fixture, Vec2 point, Vec2 normal, float fraction) {
				if (fixture.getBody().getUserData() instanceof Tank) {
					if (point.sub(start).length() < minL[0] || !tankFound[0]) {
						closestFixture[0] = fixture;
						closestPoint[0] = point.clone();
						minL[0] = point.sub(start).length();
					}
					tankFound[0] = true;
				} else if (point.sub(start).length() < minL[0] && !tankFound[0]) {
					closestFixture[0] = fixture;
					closestPoint[0] = point.clone();
					minL[0] = point.sub(start).length();
				}
				return -1;
			}
		}, start, end);
		if (closestFixture[0].getBody().getUserData() instanceof Tank) {
			((Tank) closestFixture[0].getBody().getUserData()).damage(10);
		}
		end = closestPoint[0];
	}

	public void shootLaser(World world) {
		Fixture[] closestFixture = new Fixture[1];
		Vec2[] closestPoint = new Vec2[1];
		float[] minL = new float[] { Float.MAX_VALUE };
		world.raycast(new RayCastCallback() {

			@Override
			public float reportFixture(Fixture fixture, Vec2 point, Vec2 normal, float fraction) {
				if (point.sub(start).length() < minL[0]) {
					closestFixture[0] = fixture;
					closestPoint[0] = point.clone();
					minL[0] = point.sub(start).length();
				}
				return -1;
			}
		}, start, end);
		if (closestFixture[0].getBody().getUserData() instanceof Tank) {
			((Tank) closestFixture[0].getBody().getUserData()).damage(7);
		}
		end = closestPoint[0];
	}

	public void shoot(World world) {
		if (superLaser) {
			shootSuperLaser(world);
		} else {
			shootLaser(world);
		}
	}

	public Vec2 getStart() {
		return start;
	}

	public Vec2 getEnd() {
		return end;
	}
}
