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

import java.util.ArrayList;

import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;

public class Laser {

	public enum LaserType {
		LASER, SUPERLASER, REFLECTIONLASER;
	}

	private long time;
	private ArrayList<Vec2> pointList;
	private LaserType type;

	public Laser(LaserType type) {
		time = System.currentTimeMillis();
		pointList = new ArrayList<>();
		this.type = type;
	}

	public LaserType getType() {
		return type;
	}

	public ArrayList<Vec2> getPointList() {
		return pointList;
	}

	public boolean checkForLifeTime() {
		if (System.currentTimeMillis() - time > 200) {
			return true;
		} else {
			return false;
		}
	}

	public void shootSuperLaser(World world, Vec2 start, Vec2 end, float RATIO) {
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
			((Tank) closestFixture[0].getBody().getUserData()).damage(10, RATIO);
		}
		pointList.add(start);
		pointList.add(closestPoint[0]);
	}

	public void shootReflectionLaser(World world, Vec2 start, Vec2 end, float length, float RATIO) {
		while (true) {
			end = shootLaser(world, start, end, RATIO);
			if (end == null) {
				break;
			}
			if (length - pointList.get(pointList.size() - 1).sub(pointList.get(pointList.size() - 2)).length() <= 0) {
				pointList.get(pointList.size() - 1).subLocal(start).normalize();
				pointList.get(pointList.size() - 1).mulLocal(length).addLocal(start);
				break;
			}
			length -= pointList.get(pointList.size() - 1).sub(pointList.get(pointList.size() - 2)).length();
			start = pointList.get(pointList.size() - 1);
		}
	}

	public Vec2 shootLaser(World world, Vec2 start, Vec2 end, float RATIO) {
		Fixture[] closestFixture = new Fixture[1];
		Vec2[] closestPoint = new Vec2[1];
		Vec2[] normalVector = new Vec2[1];
		float[] minL = new float[] { Float.MAX_VALUE };
		world.raycast(new RayCastCallback() {

			@Override
			public float reportFixture(Fixture fixture, Vec2 point, Vec2 normal, float fraction) {
				if (point.sub(start).length() < minL[0]) {
					closestFixture[0] = fixture;
					closestPoint[0] = point.clone();
					normalVector[0] = normal;
					minL[0] = point.sub(start).length();
				}
				return -1;
			}
		}, start, end);
		if (closestFixture[0] == null) {
			return null;
		}
		pointList.add(start);
		pointList.add(closestPoint[0]);
		if (closestFixture[0].getBody().getUserData() instanceof Tank) {
			((Tank) closestFixture[0].getBody().getUserData()).damage(7, RATIO);
			return null;
		}
		// e-2(E*n)n
		return end.sub(start).sub(normalVector[0].mul(2).mul(Vec2.dot(end.sub(start), normalVector[0])));
	}

	public void shoot(World world, Vec2 start, Vec2 end, float RATIO) {
		if (type == LaserType.LASER) {
			shootLaser(world, start, end, RATIO);
		} else if (type == LaserType.SUPERLASER) {
			shootSuperLaser(world, start, end, RATIO);
		} else if (type == LaserType.REFLECTIONLASER) {
			shootReflectionLaser(world, start, end, 20, RATIO);
		}
	}
}
