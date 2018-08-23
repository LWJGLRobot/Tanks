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
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Bot extends Tank {

	private boolean rotate;
	private boolean move;

	public Bot(ArrayList<Entity> entityList, float x, float y, Image image, World world, Group group, Body frictionBox,
			float RATIO) {
		super(entityList, x, y, image, world, group, frictionBox, RATIO);
		getBody().setUserData(this);
		move = true;
	}

	public Object shoot(ArrayList<Entity> entityList, Color color, World world, Group group, Body frictionBox,
			float RATIO) {
		Fixture[] closestFixture = new Fixture[1];
		float[] minL = new float[] { Float.MAX_VALUE };
		world.raycast(new RayCastCallback() {

			@Override
			public float reportFixture(Fixture fixture, Vec2 point, Vec2 normal, float fraction) {
				if (point.sub(getBody().getPosition()).length() < minL[0]) {
					minL[0] = point.sub(getBody().getPosition()).length();
					closestFixture[0] = fixture;
				}
				return -1;
			}
		}, getBody().getPosition(), getDirectionVector(10, RATIO));
		if (closestFixture[0] != null && (closestFixture[0].getBody().getUserData() instanceof TankRedAndBlue
				|| closestFixture[0].getBody().getUserData() instanceof TankGreen)
				&& !((Tank) closestFixture[0].getBody().getUserData()).isDied()) {
			return super.shoot(entityList, color, world, group, frictionBox, RATIO);
		} else {
			return null;
		}
	}

	public void moveOneStep() {
		if (isDied())
			return;
		if (Math.random() < 0.1) {
			rotate = !rotate;
		}
		if (Math.random() < 0.05) {
			move = !move;
		}
		if (rotate) {
			rotateLeft();
		} else {
			rotateRight();
		}
		if (move) {
			moveForward();
		} else {
			moveBackward();
		}
	}

}
