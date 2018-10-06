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
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Rot;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.FrictionJoint;
import org.jbox2d.dynamics.joints.FrictionJointDef;
import org.jbox2d.particle.ParticleGroupType;
import org.jbox2d.particle.ParticleType;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import ru.navarobot.Laser.LaserType;

public class Tank extends Entity {
	private float force, torque;
	private int health;
	private Text text;
	private boolean died;
	private float ammoVelocity;
	private int score;
	private WeaponType weaponType;
	private FrictionJoint frictionJoint;

	public int DEFAULT_HEALTH;
	public double TEXT_OFFSET;

	public Tank(ArrayList<Entity> entityList, float x, float y, Image image, World world, Group group, Body frictionBox,
			float jointForce, float jointTorque, int defaultHealth, double textOffset, float scale, float RATIO) {

		DEFAULT_HEALTH = defaultHealth;
		TEXT_OFFSET = textOffset;

		text = new Text();
		text.setFill(Color.RED);
		group.getChildren().add(text);

		ImageView imageView = new ImageView();
		imageView.setX(x - image.getWidth() / 2);
		imageView.setY(y - image.getHeight() / 2);
		imageView.setScaleY(scale);
		imageView.setScaleX(scale);
		imageView.setCache(true);
		imageView.setSmooth(true);

		PolygonShape shape = new PolygonShape();
		shape.setAsBox((float) image.getWidth() * scale * RATIO / 2, (float) image.getHeight() * scale * RATIO / 2);

		initEntity(entityList, imageView, BodyType.DYNAMIC, x, y, world, shape, 1, 0.5f, false, group, 0, RATIO);

		getBody().setAngularDamping(0.5f);
		getBody().setLinearDamping(0.3f);

		FrictionJointDef frictionJointDef = new FrictionJointDef();
		frictionJointDef.initialize(getBody(), frictionBox, getBody().getPosition());
		frictionJointDef.maxForce = jointForce;
		frictionJointDef.maxTorque = jointTorque;
		frictionJoint = (FrictionJoint) world.createJoint(frictionJointDef);

		restart(image, x * RATIO, y * RATIO);
	}

	public FrictionJoint getFrictionJoint() {
		return frictionJoint;
	}

	public double[] getSensorData(World world) {
		Vec2[] points = closestPoints(world);
		double[] data = new double[points.length];
		for (int i = 0; i < points.length; i++) {
			data[i] = points[i].sub(getBody().getPosition()).length();
		}
		return data;
	}

	public Vec2[] closestPoints(World world) {
		Vec2[] data = new Vec2[64];
		for (int i = 0; i < data.length; i++) {
			Vec2 end = new Vec2();
			Rot.mulToOut(new Rot((float) (-Math.PI / 2 + Math.PI * i / 64) + getBody().getAngle()), new Vec2(1, 0),
					end);
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
			}, getBody().getPosition(), end.mul(100));
			data[i] = closestPoint[0] == null ? end.mul(10) : closestPoint[0];
		}
		return data;
	}

	public float getForce() {
		return force;
	}

	public void setForce(float force) {
		this.force = force;
	}

	public float getTorque() {
		return torque;
	}

	public void setTorque(float torque) {
		this.torque = torque;
	}

	public void updateText() {
		text.setText("♥:" + health + " K:" + score);
	}

	public void setWeaponType(WeaponType weaponType) {
		this.weaponType = weaponType;
	}

	public void destroy(ArrayList<Entity> entityList, Group group, World world) {
		super.destroy(entityList, group, world);
		group.getChildren().removeAll(text);
	}

	public void increaseScore() {
		Audio.WIN.play();
		score++;
		updateText();
	}

	public void health(int health) {
		this.health += health;
		updateText();
	}

	public int getScore() {
		return score;
	}

	public void increaseSpeed(float force) {
		this.force += force;
	}

	public void increaseBulletImpulse(float impulse) {
		ammoVelocity += impulse;
	}

	public void restart(Image image, float x, float y) {
		getBody().setTransform(new Vec2(x, y), 0);
		weaponType = WeaponType.DEFAULT;
		setDied(false);
		getImageView().setImage(image);
		force = 1.3f;
		torque = 1.02f;
		ammoVelocity = 5f;
		health = DEFAULT_HEALTH;
		updateText();
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

	public boolean damage(int damage, float RATIO) {
		health -= damage;
		updateText();
		if (health < 0 && !died) {
			died = true;
			getImageView().setImage(Images.POOP.image);
			((PolygonShape) getBody().getFixtureList().getShape()).setAsBox(
					(float) (Images.POOP.image.getWidth() * getImageView().getScaleX() * RATIO / 2),
					(float) (Images.POOP.image.getHeight() * getImageView().getScaleY() * RATIO / 2));
			return true;
		}
		return false;
	}

	public Vec2 getDirectionVector(float k, float RATIO) {
		return getDirectionVector(getBody().getAngle(), k, RATIO);
	}

	public Vec2 getDirectionVector(float angle, float k, float RATIO) {
		return new Vec2(
				(float) getBody().getPosition().x
						+ (float) (Math.cos(angle) * getImageView().getImage().getWidth() * RATIO * k),
				(float) getBody().getPosition().y
						+ (float) (Math.sin(angle) * getImageView().getImage().getWidth() * RATIO * k));
	}

	public Object shoot(ArrayList<Entity> entityList, Color color, World world, Group group, Body frictionBox,
			float RATIO) {
		if (died) {
			return null;
		}
		Object object = null;
		if (weaponType == WeaponType.DEFAULT) {
			Audio.POP.play();
			if (Math.random() < 0.3) {
				// soft bullet
				object = shootBullet(entityList, Color.PURPLE, world, group, frictionBox, 1, RATIO);
			} else {
				object = shootBullet(entityList, color, world, group, frictionBox, 0, RATIO);
			}
		} else if (weaponType == WeaponType.MISSILE) {
			Audio.MISSILE.play();
			object = shootMissile(entityList, world, group, frictionBox, RATIO);
		} else if (weaponType == WeaponType.FIRE) {

		} else if (weaponType == WeaponType.LASER) {
			Audio.LASER.play();
			object = shootLaser(world, LaserType.LASER, RATIO);
		} else if (weaponType == WeaponType.SOFTBULLET) {
			Audio.POP.play();
			object = shootBullet(entityList, Color.PURPLE, world, group, frictionBox, 1, RATIO);
		} else if (weaponType == WeaponType.SUPERLASER) {
			Audio.LASER.play();
			object = shootLaser(world, LaserType.SUPERLASER, RATIO);
		} else if (weaponType == WeaponType.BOMB) {
			// Audio.FUSE.play();
			Audio.POP.play();
			object = shootBomb(entityList, world, group, frictionBox, RATIO);
		} else if (weaponType == WeaponType.REFLECTIONLASER) {
			Audio.LASER.play();
			object = shootLaser(world, LaserType.REFLECTIONLASER, RATIO);
		} else if (weaponType == WeaponType.DEFEND) {
			Audio.BUBBLE.play();
			object = defend(color, group, world, RATIO);
		} else if (weaponType == WeaponType.FLASH) {
			Audio.POP.play();
			object = flashLight(entityList, Color.WHITE, world, group, frictionBox, 1, RATIO);
		} else if (weaponType == WeaponType.BIGBULLET) {
			Audio.POP.play();
			object = shootBigBullet(entityList, color, world, group, frictionBox, 0, RATIO);
		}
		weaponType = WeaponType.DEFAULT;
		return object;
	}

	public Flash flashLight(ArrayList<Entity> entityList, Color color, World world, Group group, Body frictionBox,
			float restitution, float RATIO) {
		if (died)
			return null;
		Vec2 flashPosition = getDirectionVector(0.7f, RATIO);
		Flash flash = new Flash(entityList, this, flashPosition.x / RATIO, flashPosition.y / RATIO,
				new Vec2((float) (Math.cos(getBody().getAngle()) * ammoVelocity),
						(float) (Math.sin(getBody().getAngle()) * ammoVelocity)).add(getBody().getLinearVelocity()),
				color, world, group, frictionBox, restitution, 5, RATIO);
		return flash;
	}

	public ParticleGroupWithLifeTime defend(Color color, Group group, World world, float RATIO) {
		return new ParticleGroupWithLifeTime(getBody().getPosition(), new Vec2(), 0, color, 50, RATIO, world, group,
				ParticleType.b2_springParticle, ParticleGroupType.b2_solidParticleGroup, 15000);
	}

	public WeaponType getWeaponType() {
		return weaponType;
	}

	public Bomb shootBomb(ArrayList<Entity> entityList, World world, Group group, Body frictionBox, float RATIO) {
		Vec2 bombPostition = getDirectionVector(1f, RATIO);
		return new Bomb(entityList, this, bombPostition.x / RATIO, bombPostition.y / RATIO,
				new Vec2((float) (Math.cos(getBody().getAngle()) * ammoVelocity),
						(float) (Math.sin(getBody().getAngle()) * ammoVelocity)).add(getBody().getLinearVelocity()),
				world, group, frictionBox, Images.BOMB.image, RATIO);
	}

	public Laser shootLaser(World world, LaserType type, float RATIO) {
		Laser laser = new Laser(type);
		laser.shoot(world, getBody().getPosition(), getDirectionVector(100, RATIO), RATIO);
		return laser;
	}

	public Missile shootMissile(ArrayList<Entity> entityList, World world, Group group, Body frictionBox, float RATIO) {
		Vec2 missilePosition = getDirectionVector(0.9f, RATIO);
		Missile missile = new Missile(entityList, this, missilePosition.x / RATIO, missilePosition.y / RATIO,
				new Vec2((float) (Math.cos(getBody().getAngle()) * ammoVelocity),
						(float) (Math.sin(getBody().getAngle()) * ammoVelocity)).add(getBody().getLinearVelocity()),
				Images.MISSILE.image, world, frictionBox, group, RATIO);
		return missile;
	}

	public Bullet shootBigBullet(ArrayList<Entity> entityList, Color color, World world, Group group, Body frictionBox,
			float restitution, float RATIO) {
		if (died)
			return null;
		Vec2 bulletPosition = getDirectionVector(0.7f, RATIO);
		Bullet bullet = new Bullet(entityList, this, bulletPosition.x / RATIO, bulletPosition.y / RATIO,
				new Vec2((float) (Math.cos(getBody().getAngle()) * ammoVelocity),
						(float) (Math.sin(getBody().getAngle()) * ammoVelocity)).add(getBody().getLinearVelocity()),
				color, world, group, frictionBox, restitution, 10, true, RATIO);
		return bullet;
	}

	public Bullet shootBullet(ArrayList<Entity> entityList, Color color, World world, Group group, Body frictionBox,
			float restitution, float RATIO) {
		if (died)
			return null;
		Vec2 bulletPosition = getDirectionVector(0.7f, RATIO);
		Bullet bullet = new Bullet(entityList, this, bulletPosition.x / RATIO, bulletPosition.y / RATIO,
				new Vec2((float) (Math.cos(getBody().getAngle()) * ammoVelocity),
						(float) (Math.sin(getBody().getAngle()) * ammoVelocity)).add(getBody().getLinearVelocity()),
				color, world, group, frictionBox, restitution, 5, false, RATIO);
		return bullet;
	}

	public void updatePositionAndAngle(float RATIO) {
		text.setX(getBody().getPosition().x / RATIO);
		text.setY(getBody().getPosition().y / RATIO - TEXT_OFFSET);

		getImageView().setX(getBody().getPosition().x / RATIO - getImageView().getImage().getWidth() / 2);
		getImageView().setY(getBody().getPosition().y / RATIO - getImageView().getImage().getHeight() / 2);
		getImageView().setRotate(Math.toDegrees(getBody().getAngle()));
	}

	public void rotateLeft() {
		rotate(-torque);
	}

	public void rotateLeft(float mul) {
		rotate(-torque * mul);
	}

	public void rotateRight() {
		rotate(torque);
	}

	public void rotateRight(float mul) {
		rotate(torque * mul);
	}

	public void moveForward() {
		move(force);
	}

	public void moveForward(float mul) {
		move(force * mul);
	}

	public void moveBackward() {
		move(-force);
	}

	public void moveBackward(float mul) {
		move(-force * mul);
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
