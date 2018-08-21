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
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class Tank extends Entity {
	private float force, torque;
	private int health;
	private Text text;
	private boolean died;
	private float bulletImpulse;
	private float missileImpulse;
	private int score;
	private WeaponType weaponType;

	public Tank(ArrayList<Entity> entityList, float x, float y, Image image, World world, Group group, Body frictionBox,
			float RATIO) {

		text = new Text();
		group.getChildren().add(text);

		ImageView imageView = new ImageView();
		imageView.setX(x - image.getWidth() / 2);
		imageView.setY(y - image.getHeight() / 2);
		imageView.setCache(true);
		imageView.setSmooth(true);

		PolygonShape shape = new PolygonShape();
		shape.setAsBox((float) image.getWidth() * RATIO / 2, (float) image.getHeight() * RATIO / 2);

		initEntity(entityList, imageView, BodyType.DYNAMIC, x, y, world, shape, 1, 0.5f, false, group, RATIO);

		FrictionJointDef frictionJointDef = new FrictionJointDef();
		frictionJointDef.initialize(getBody(), frictionBox, getBody().getPosition());
		frictionJointDef.maxForce = 1f;
		frictionJointDef.maxTorque = 1f;
		world.createJoint(frictionJointDef);

		restart(image);
	}

	public void setWeaponType(WeaponType weaponType) {
		this.weaponType = weaponType;
	}

	public void destroy(ArrayList<Entity> entityList, Group group, World world) {
		super.destroy(entityList, group, world);
		group.getChildren().removeAll(text);
	}

	public void increaseScore() {
		score++;
		text.setText(health + " " + score);
	}

	public int getScore() {
		return score;
	}

	public void increaseSpeed(float force) {
		this.force += force;
	}

	public void increaseBulletImpulse(float impulse) {
		bulletImpulse += impulse;
	}

	public void restart(Image image) {
		weaponType = WeaponType.DEFAULT;
		setDied(false);
		getImageView().setImage(image);
		force = 1.3f;
		torque = 1.05f;
		bulletImpulse = 0.01f;
		missileImpulse = 0.03f;
		health = 10;
		text.setText(health + " " + score);
	}

	public boolean isDied() {
		return died;
	}

	public void setDied(boolean died) {
		this.died = died;
	}

	public Text getText() {
		return text;
	}

	public boolean damage(int damage) {
		health -= damage;
		text.setText(health + " " + score);
		if (health < 0 && !died) {
			died = true;
			getImageView().setImage(Images.POOP);
			return true;
		}
		return false;
	}

	public Vec2 getDirectionVector(float k, float RATIO) {
		return new Vec2(
				(float) getBody().getPosition().x
						+ (float) (Math.cos(getBody().getAngle()) * getImageView().getImage().getWidth() * RATIO * k),
				(float) getBody().getPosition().y
						+ (float) (Math.sin(getBody().getAngle()) * getImageView().getImage().getWidth() * RATIO * k));
	}

	public Entity shoot(ArrayList<Entity> entityList, Color color, World world, Group group, Body frictionBox,
			float RATIO) {
		if (died) {
			return null;
		}
		Entity entity = null;
		if (weaponType == WeaponType.DEFAULT) {
			entity = shootBullet(entityList, color, world, group, frictionBox, RATIO);
		} else if (weaponType == WeaponType.MISSILE) {
			entity = shootMissile(entityList, world, group, frictionBox, RATIO);
		}
		weaponType = WeaponType.DEFAULT;
		return entity;
	}

	public Missile shootMissile(ArrayList<Entity> entityList, World world, Group group, Body frictionBox, float RATIO) {
		Vec2 missilePosition = getDirectionVector(0.9f, RATIO);
		Missile missile = new Missile(entityList, this, missilePosition.x / RATIO, missilePosition.y / RATIO,
				new Vec2((float) (Math.cos(getBody().getAngle()) * missileImpulse),
						(float) (Math.sin(getBody().getAngle()) * missileImpulse)),
				getBody().getLinearVelocity(), Images.MISSILE, world, frictionBox, group, RATIO);
		return missile;
	}

	public Bullet shootBullet(ArrayList<Entity> entityList, Color color, World world, Group group, Body frictionBox,
			float RATIO) {
		if (died)
			return null;
		Vec2 bulletPosition = getDirectionVector(0.7f, RATIO);
		Bullet bullet = new Bullet(entityList, this, bulletPosition.x / RATIO, bulletPosition.y / RATIO,
				new Vec2((float) (Math.cos(getBody().getAngle()) * bulletImpulse),
						(float) (Math.sin(getBody().getAngle()) * bulletImpulse)),
				getBody().getLinearVelocity(), color, world, group, frictionBox, RATIO);
		return bullet;
	}

	public void updatePositionAndAngle(float RATIO) {
		text.setX(getBody().getPosition().x / RATIO);
		text.setY(getBody().getPosition().y / RATIO - 30);

		getImageView().setX(getBody().getPosition().x / RATIO - getImageView().getImage().getWidth() / 2);
		getImageView().setY(getBody().getPosition().y / RATIO - getImageView().getImage().getHeight() / 2);
		getImageView().setRotate(Math.toDegrees(getBody().getAngle()));
	}

	public void rotateLeft() {
		rotate(-torque);
	}

	public void rotateRight() {
		rotate(torque);
	}

	public void moveForward() {
		move(force);
	}

	public void moveBackward() {
		move(-force);
	}

	public void move(float force) {
		getBody().applyForceToCenter(new Vec2((float) (Math.cos(getBody().getAngle()) * force),
				(float) (Math.sin(getBody().getAngle()) * force)));
	}

	public void rotate(float torque) {
		getBody().applyTorque(torque);
	}

	public ImageView getImageView() {
		return (ImageView) getNode();
	}
}
