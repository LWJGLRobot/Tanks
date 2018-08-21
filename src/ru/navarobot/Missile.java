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

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.FrictionJointDef;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Missile extends Ammo {

	private float force;

	public Missile(ArrayList<Entity> entityList, Tank tank, float x, float y, Vec2 impulse, Vec2 tankVelocity,
			Image image, World world, Body frictionBox, Group group, float RATIO) {

		ImageView imageView = new ImageView(image);
		imageView.setX(x - image.getWidth() / 2);
		imageView.setY(y - image.getHeight() / 2);
		imageView.setCache(true);
		imageView.setSmooth(true);

		PolygonShape shape = new PolygonShape();
		shape.setAsBox((float) image.getWidth() * RATIO / 2, (float) image.getHeight() * RATIO / 2);

		initEntity(entityList, imageView, BodyType.DYNAMIC, x, y, world, shape, 0.3f, 0.1f, true, group, RATIO);

		getBody().setUserData(this);

		FrictionJointDef frictionJointDef = new FrictionJointDef();
		frictionJointDef.initialize(getBody(), frictionBox, getBody().getPosition());
		frictionJointDef.maxForce = 0.01f;
		frictionJointDef.maxTorque = 0.01f;
		world.createJoint(frictionJointDef);

		getBody().applyLinearImpulse(impulse.addLocal(tankVelocity.mul(getBody().getMass())), getBody().getPosition(),
				true);

		initAmmo(tank);

		force = 0.02f;
	}

	public void moveOneStep(ArrayList<Tank> tankList) {
		float minL = Float.MAX_VALUE;
		Vec2 minLocalVector = null;
		for (int i = 0; i < tankList.size(); i++) {
			Vec2 localVector = tankList.get(i).getBody().getPosition().sub(getBody().getPosition());
			if (localVector.x * localVector.x + localVector.y * localVector.y < minL) {
				minLocalVector = localVector;
				minL = localVector.x * localVector.x + localVector.y * localVector.y;
			}
		}
		getBody().setTransform(getBody().getPosition(), (float) Math.atan2(minLocalVector.y, minLocalVector.x));
		getBody().applyForceToCenter(new Vec2((float) (Math.cos(getBody().getAngle()) * force),
				(float) (Math.sin(getBody().getAngle()) * force)));
	}

	public void updatePositionAndAngle(float RATIO) {
		getImageView().setX(getBody().getPosition().x / RATIO - getImageView().getImage().getWidth() / 2);
		getImageView().setY(getBody().getPosition().y / RATIO - getImageView().getImage().getHeight() / 2);
		getImageView().setRotate(Math.toDegrees(getBody().getAngle()));
	}

	public ImageView getImageView() {
		return (ImageView) getNode();
	}
}
