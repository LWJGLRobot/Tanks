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
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.FrictionJointDef;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Flash extends Entity {

	private int numOfLights = 1000;
	private long time;

	public Flash(ArrayList<Entity> entityList, Tank tank, float x, float y, Vec2 velocity, Color color, World world,
			Group group, Body frictionBox, float restitution, float radius, float RATIO) {

		Circle circle = new Circle(x, y, radius, color);

		CircleShape shape = new CircleShape();
		shape.setRadius(radius * RATIO);

		initEntity(entityList, circle, BodyType.DYNAMIC, x, y, world, shape, 0.3f, 0.1f, false, group, restitution,
				RATIO);

		getBody().setUserData(this);

		FrictionJointDef frictionJointDef = new FrictionJointDef();
		frictionJointDef.initialize(getBody(), frictionBox, getBody().getPosition());
		frictionJointDef.maxForce = 0.01f;
		frictionJointDef.maxTorque = 0.01f;
		world.createJoint(frictionJointDef);

		getBody().applyLinearImpulse(velocity.mul(getBody().getMass()), getBody().getPosition(), true);
		
		time = System.currentTimeMillis();
	}
	
	public boolean checkForLifeTime() {
		if (System.currentTimeMillis() - time > 10000) {
			return true;
		}
		return false;
	}

	public Vec2 raycastClosestPoint(Vec2 end, World world) {
		Vec2[] closestPoint = new Vec2[1];
		float[] minL = new float[] { Float.MAX_VALUE };
		world.raycast(new RayCastCallback() {

			@Override
			public float reportFixture(Fixture fixture, Vec2 point, Vec2 normal, float fraction) {
				if (point.sub(getBody().getPosition()).length() < minL[0]) {
					closestPoint[0] = point.clone();
					minL[0] = point.sub(getBody().getPosition()).length();
				}
				return -1;
			}
		}, getBody().getPosition(), end);
		return closestPoint[0] == null ? end : closestPoint[0];
	}

	public int getNumOfLights() {
		return numOfLights;
	}

	public void updatePositionAndAngle(float RATIO) {
		getCircle().setCenterX(getBody().getPosition().x / RATIO);
		getCircle().setCenterY(getBody().getPosition().y / RATIO);
	}

	public Circle getCircle() {
		return (Circle) getNode();
	}
}
