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

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.FrictionJointDef;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Bullet {
	private Circle circle;
	private Body body;
	private long time;
	private Tank tank;

	public Bullet(Tank tank, float x, float y, Vec2 impulse, Vec2 tankVelocity, Color color, World world,
			Body frictionBox, float RATIO) {
		this.tank = tank;
		circle = new Circle(x, y, 5, color);

		BodyDef bodyDef = new BodyDef();
		bodyDef.setType(BodyType.DYNAMIC);
		bodyDef.setPosition(new Vec2(x * RATIO, y * RATIO));
		bodyDef.setFixedRotation(true);
		bodyDef.setBullet(true);
		body = world.createBody(bodyDef);
		FixtureDef fixtureDef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(5 * RATIO);
		fixtureDef.setShape(shape);
		fixtureDef.setDensity(0.3f);
		fixtureDef.setFriction(0.1f);
		body.createFixture(fixtureDef);
		body.setUserData(this);

		FrictionJointDef frictionJointDef = new FrictionJointDef();
		frictionJointDef.initialize(body, frictionBox, body.getPosition());
		frictionJointDef.maxForce = 0.01f;
		frictionJointDef.maxTorque = 0.01f;
		world.createJoint(frictionJointDef);

		body.applyLinearImpulse(impulse.addLocal(tankVelocity.mul(body.getMass())), body.getPosition(), true);

		time = System.currentTimeMillis();
	}

	public Tank getTank() {
		return tank;
	}

	public long getTime() {
		return time;
	}

	public Body getBody() {
		return body;
	}

	public void updatePosition(float RATIO) {
		circle.setCenterX(body.getPosition().x / RATIO);
		circle.setCenterY(body.getPosition().y / RATIO);
	}

	public Circle getCircle() {
		return circle;
	}
}
