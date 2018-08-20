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

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.FrictionJointDef;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Box {
	private ImageView imageView;
	private Body body;

	public Box(float x, float y, Image image, World world, Body frictionBox, float RATIO) {
		this.imageView = new ImageView(image);
		imageView.setX(x - image.getWidth() / 2);
		imageView.setY(y - image.getHeight() / 2);
		imageView.setCache(true);
		imageView.setSmooth(true);

		BodyDef bodyDef = new BodyDef();
		bodyDef.setType(BodyType.DYNAMIC);
		bodyDef.setPosition(new Vec2(x * RATIO, y * RATIO));
		body = world.createBody(bodyDef);
		FixtureDef fixtureDef = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		shape.setAsBox((float) image.getWidth() * RATIO / 2, (float) image.getHeight() * RATIO / 2);
		fixtureDef.setShape(shape);
		fixtureDef.setDensity(2);
		fixtureDef.setFriction(0.5f);
		body.createFixture(fixtureDef);

		FrictionJointDef frictionJointDef = new FrictionJointDef();
		frictionJointDef.initialize(body, frictionBox, body.getPosition());
		frictionJointDef.maxForce = 1f;
		frictionJointDef.maxTorque = 1f;
		world.createJoint(frictionJointDef);
	}

	public void updatePositionAndAngle(float RATIO) {
		imageView.setX(body.getPosition().x / RATIO - imageView.getImage().getWidth() / 2);
		imageView.setY(body.getPosition().y / RATIO - imageView.getImage().getHeight() / 2);
		imageView.setRotate(Math.toDegrees(body.getAngle()));
	}

	public Body getBody() {
		return body;
	}

	public ImageView getImageView() {
		return imageView;
	}
}
