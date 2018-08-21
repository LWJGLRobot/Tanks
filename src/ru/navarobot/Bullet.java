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

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.FrictionJointDef;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Bullet extends Ammo {

	public Bullet(ArrayList<Entity> entityList, Tank tank, float x, float y, Vec2 impulse, Vec2 tankVelocity,
			Color color, World world, Group group, Body frictionBox, float RATIO) {

		Circle circle = new Circle(x, y, 5, color);

		CircleShape shape = new CircleShape();
		shape.setRadius(5 * RATIO);

		initEntity(entityList, circle, BodyType.DYNAMIC, x, y, world, shape, 0.3f, 0.1f, true, group, RATIO);

		getBody().setUserData(this);

		FrictionJointDef frictionJointDef = new FrictionJointDef();
		frictionJointDef.initialize(getBody(), frictionBox, getBody().getPosition());
		frictionJointDef.maxForce = 0.01f;
		frictionJointDef.maxTorque = 0.01f;
		world.createJoint(frictionJointDef);

		getBody().applyLinearImpulse(impulse.addLocal(tankVelocity.mul(getBody().getMass())), getBody().getPosition(),
				true);

		initAmmo(tank);
	}

	public void updatePositionAndAngle(float RATIO) {
		getCircle().setCenterX(getBody().getPosition().x / RATIO);
		getCircle().setCenterY(getBody().getPosition().y / RATIO);
	}

	public Circle getCircle() {
		return (Circle) getNode();
	}
}
