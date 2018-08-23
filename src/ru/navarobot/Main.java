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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Random;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Rot;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.particle.ParticleType;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		final float RATIO = 0.01f;

		Group group = new Group();
		Scene scene = new Scene(group, 640, 480);

		// init physics
		World world = new World(new Vec2());

		Body frictionBox;
		{
			BodyDef bodyDef = new BodyDef();
			bodyDef.setType(BodyType.STATIC);
			bodyDef.setPosition(new Vec2());
			frictionBox = world.createBody(bodyDef);
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.setShape(new PolygonShape());
			fixtureDef.setFriction(0.5f);
			frictionBox.createFixture(fixtureDef);
		}

		ImageView background = new ImageView(Images.BACKGROUND.image);
		group.getChildren().add(background);

		ArrayList<Entity> entityList = new ArrayList<>();
		ArrayList<ParticleGroupWithLifeTime> particleGroupList = new ArrayList<>();
		ArrayList<Bot> botList = new ArrayList<>();
		ArrayList<Box> boxList = new ArrayList<>();
		ArrayList<Tank> tankList = new ArrayList<>();
		ArrayList<Ammo> ammoList = new ArrayList<>();
		ArrayList<Missile> missileList = new ArrayList<>();
		ArrayList<Laser> laserList = new ArrayList<>();
		ArrayList<Bomb> bombList = new ArrayList<>();

		TankRedAndBlue tankRed = new TankRedAndBlue(entityList, 200, 200, Images.TANKRED.image, world, group,
				frictionBox, RATIO, new KeyCode[] { KeyCode.W, KeyCode.S, KeyCode.A, KeyCode.D, KeyCode.SPACE });
		TankGreen tankGreen = new TankGreen(entityList, 260, 100, Images.TANKGREEN.image, world, group, frictionBox,
				RATIO);
		TankRedAndBlue tankBlue = new TankRedAndBlue(entityList, 100, 260, Images.TANKBLUE.image, world, group,
				frictionBox, RATIO, new KeyCode[] { KeyCode.UP, KeyCode.DOWN, KeyCode.LEFT, KeyCode.RIGHT, KeyCode.P });
		tankList.add(tankRed);
		tankList.add(tankGreen);
		tankList.add(tankBlue);

		scene.setCursor(new ImageCursor(Images.CURSOR.image, Images.CURSOR.image.getWidth() / 2,
				Images.CURSOR.image.getHeight() / 2));

		ArrayDeque<KeyCode> keyQueue = new ArrayDeque<>();

		// on keyevents need to use internal state variables
		scene.setOnMouseMoved((event) -> {
			tankGreen.processInput(event);
		});

		scene.setOnMouseClicked((event) -> {
			tankGreen.processInput(event);
		});

		scene.setOnKeyPressed((event) -> {
			tankBlue.processInput(event);
			tankRed.processInput(event);
		});

		scene.setOnKeyReleased((event) -> {
			tankBlue.processInput(event);
			tankRed.processInput(event);

			if (event.getCode() == KeyCode.R) {
				keyQueue.add(KeyCode.R);
			} else if (event.getCode() == KeyCode.B) {
				keyQueue.add(KeyCode.B);
			}
		});

		Entity[] borders = new Entity[] { createBorder(entityList, world, group, RATIO),
				createBorder(entityList, world, group, RATIO), createBorder(entityList, world, group, RATIO),
				createBorder(entityList, world, group, RATIO) };

		setBorders(borders, world, (float) scene.getWidth(), (float) scene.getHeight(), RATIO);

		addRandomBoxes(entityList, boxList, group, scene, world, frictionBox, RATIO);

		world.setParticleRadius(3 * RATIO);

		Canvas canvas = new Canvas(640, 480);
		group.getChildren().add(canvas);

		primaryStage.widthProperty().addListener((event, numOld, numNew) -> {
			setBorders(borders, world, (float) scene.getWidth(), (float) scene.getHeight(), RATIO);
			canvas.setWidth(scene.getWidth());
		});

		primaryStage.heightProperty().addListener((event, numOld, numNew) -> {
			setBorders(borders, world, (float) scene.getWidth(), (float) scene.getHeight(), RATIO);
			canvas.setHeight(scene.getHeight());
		});

		Object[] contactData = new Object[3];

		world.setContactListener(new ContactListener() {

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {

			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {

			}

			@Override
			public void endContact(Contact contact) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beginContact(Contact contact) {
				if (contactData[0] != null) {
					return;
				}
				if (contact.m_fixtureA.getBody().getUserData() instanceof Missile) {
					contactData[0] = contact.m_fixtureA.getBody();
					contactData[1] = contact.m_fixtureB.getBody();
					contactData[2] = contact.m_fixtureA.getBody().getLinearVelocity().clone();
				} else if (contact.m_fixtureB.getBody().getUserData() instanceof Missile) {
					contactData[0] = contact.m_fixtureB.getBody();
					contactData[1] = contact.m_fixtureA.getBody();
					contactData[2] = contact.m_fixtureB.getBody().getLinearVelocity().clone();
				} else if (contact.m_fixtureA.getBody().getUserData() instanceof Bullet) {
					contactData[0] = contact.m_fixtureA.getBody();
					contactData[1] = contact.m_fixtureB.getBody();
					contactData[2] = contact.m_fixtureA.getBody().getLinearVelocity().clone();
				} else if (contact.m_fixtureB.getBody().getUserData() instanceof Bullet) {
					contactData[0] = contact.m_fixtureB.getBody();
					contactData[1] = contact.m_fixtureA.getBody();
					contactData[2] = contact.m_fixtureB.getBody().getLinearVelocity().clone();
				} else if (contact.m_fixtureA.getBody().getUserData() instanceof Bomb) {
					contactData[0] = contact.m_fixtureA.getBody();
					contactData[1] = contact.m_fixtureB.getBody();
					contactData[2] = contact.m_fixtureA.getBody().getLinearVelocity().clone();
				} else if (contact.m_fixtureB.getBody().getUserData() instanceof Bomb) {
					contactData[0] = contact.m_fixtureB.getBody();
					contactData[1] = contact.m_fixtureA.getBody();
					contactData[2] = contact.m_fixtureB.getBody().getLinearVelocity().clone();
				} else if (contact.m_fixtureA.getBody().getUserData() instanceof Bonus) {
					contactData[0] = contact.m_fixtureA.getBody();
					contactData[1] = contact.m_fixtureB.getBody();
					contactData[2] = contact.m_fixtureA.getBody().getLinearVelocity().clone();
				} else if (contact.m_fixtureB.getBody().getUserData() instanceof Bonus) {
					contactData[0] = contact.m_fixtureB.getBody();
					contactData[1] = contact.m_fixtureA.getBody();
					contactData[2] = contact.m_fixtureB.getBody().getLinearVelocity().clone();
				}
			}
		});

		new AnimationTimer() {
			long time = System.currentTimeMillis();

			@Override
			public void handle(long now) {
				canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

				KeyCode keyCode = keyQueue.poll();
				if (keyCode == KeyCode.R) {
					tankRed.restart(Images.TANKRED.image);
					tankGreen.restart(Images.TANKGREEN.image);
					tankBlue.restart(Images.TANKBLUE.image);
					for (Box box : boxList) {
						box.destroy(entityList, group, world);
					}
					boxList.clear();
					addRandomBoxes(entityList, boxList, group, scene, world, frictionBox, RATIO);
					for (Bot bot : botList) {
						bot.destroy(entityList, group, world);
						tankList.remove(bot);
					}
					botList.clear();
				} else if (keyCode == KeyCode.B) {
					Bot bot = new Bot(entityList, (float) (Math.random() * scene.getWidth()),
							(float) (Math.random() * scene.getHeight()), Images.TANKBOT.image, world, group,
							frictionBox, RATIO);
					botList.add(bot);
					tankList.add(bot);
				}

				if (Math.random() < 0.005) {
					BonusType[] bonusTypes = new BonusType[] { BonusType.FIREBOOST, BonusType.TANKBOOST,
							BonusType.MISSILEBONUS, BonusType.LASER, BonusType.SUPERLASER, BonusType.BOMBBONUS };
					new Bonus(entityList, (float) (Math.random() * scene.getWidth()),
							(float) (Math.random() * scene.getHeight()), world, group,
							bonusTypes[new Random().nextInt(bonusTypes.length)], RATIO);
				}

				processShoot(tankRed.shoot(entityList, Color.RED, world, group, frictionBox, RATIO), missileList,
						ammoList, laserList, particleGroupList, bombList, RATIO, world, group);
				processShoot(tankGreen.shoot(entityList, Color.GREEN, world, group, frictionBox, RATIO), missileList,
						ammoList, laserList, particleGroupList, bombList, RATIO, world, group);
				processShoot(tankBlue.shoot(entityList, Color.BLUE, world, group, frictionBox, RATIO), missileList,
						ammoList, laserList, particleGroupList, bombList, RATIO, world, group);

				for (Bot bot : botList) {
					if (Math.random() < 0.3) {
						processShoot(bot.shoot(entityList, Color.BLACK, world, group, frictionBox, RATIO), missileList,
								ammoList, laserList, particleGroupList, bombList, RATIO, world, group);
					}
				}

				tankRed.moveOneStep();

				tankGreen.moveOneStep(RATIO);

				tankBlue.moveOneStep();

				for (Bot bot : botList) {
					bot.moveOneStep();
				}

				for (int i = 0; i < ammoList.size();) {
					if (ammoList.get(i).checkForLifeTime(entityList, group, world)) {
						ammoList.remove(i);
					} else {
						i++;
					}
				}

				for (int i = 0; i < bombList.size();) {
					if (bombList.get(i).checkForLifeTime()) {
						int maxBullets = 10;
						for (int j = 0; j < maxBullets; j++) {
							Vec2 localPosition = new Vec2();
							Rot rot = new Rot((float) (2 * Math.PI * j / maxBullets));
							Rot.mulToOut(rot, new Vec2(0.1f * RATIO, 0), localPosition);
							ammoList.add(new Bullet(entityList, null,
									(bombList.get(i).getBody().getPosition().x + localPosition.x) / RATIO,
									(bombList.get(i).getBody().getPosition().y + localPosition.y) / RATIO,
									new Vec2(rot.getCos() * 10, rot.getSin() * 10), Color.PURPLE, world, group,
									frictionBox, 1, 5, RATIO));
						}
						bombList.get(i).destroy(entityList, group, world);
						bombList.remove(i);
					} else {
						i++;
					}
				}

				for (int i = 0; i < laserList.size();) {
					if (laserList.get(i).checkForLifeTime()) {
						laserList.remove(i);
					} else {
						canvas.getGraphicsContext2D().setLineWidth(3);
						if (!laserList.get(i).isSuperLaser()) {
							canvas.getGraphicsContext2D().setStroke(Color.RED);
						} else {
							canvas.getGraphicsContext2D().setStroke(Color.DARKRED);
						}
						canvas.getGraphicsContext2D().strokeLine(laserList.get(i).getStart().x / RATIO,
								laserList.get(i).getStart().y / RATIO, laserList.get(i).getEnd().x / RATIO,
								laserList.get(i).getEnd().y / RATIO);
						i++;
					}
				}

				for (Missile missile : missileList) {
					missile.moveOneStep(tankList);
				}

				world.step((System.currentTimeMillis() - time) / 1000f, 10, 10);
				time = System.currentTimeMillis();

				if (contactData[0] != null) {
					contact(contactData, entityList, ammoList, missileList, world, particleGroupList, bombList, group,
							frictionBox, RATIO);
					contactData[0] = null;
				}

				for (Entity entity : entityList) {
					entity.updatePositionAndAngle(RATIO);
				}

				if (world.getParticlePositionBuffer() != null) {
					for (int i = 0; i < world.getParticlePositionBuffer().length; i++) {
						if ((world.getParticleFlagsBuffer()[i] | ParticleType.b2_zombieParticle) == world
								.getParticleFlagsBuffer()[i]) {
							continue;
						}
						canvas.getGraphicsContext2D().setFill(Color.BLACK);
						canvas.getGraphicsContext2D().fillOval(world.getParticlePositionBuffer()[i].x / RATIO,
								world.getParticlePositionBuffer()[i].y / RATIO, world.getParticleRadius() / RATIO,
								world.getParticleRadius() / RATIO);
					}
				}

				for (int i = 0; i < particleGroupList.size();) {
					if (System.currentTimeMillis() - particleGroupList.get(i).getTime() > 10000) {
						world.destroyParticlesInGroup(particleGroupList.get(i).getParticleGroup());
						particleGroupList.remove(i);
					} else {
						i++;
					}
				}
			}
		}.start();

		primaryStage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("res/images/poop.png")));
		primaryStage.setTitle("Танчики");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public void processShoot(Object object, ArrayList<Missile> missileList, ArrayList<Ammo> ammoList,
			ArrayList<Laser> laserList, ArrayList<ParticleGroupWithLifeTime> particleGroupList,
			ArrayList<Bomb> bombList, float RATIO, World world, Group group) {
		if (object instanceof Missile) {
			missileList.add((Missile) object);
			ammoList.add((Missile) object);
		} else if (object instanceof Bullet) {
			ammoList.add((Bullet) object);
		} else if (object instanceof Laser) {
			laserList.add((Laser) object);
			particleGroupList.add(new ParticleGroupWithLifeTime(((Laser) object).getEnd(), new Vec2(), 10, RATIO, world,
					group, ParticleType.b2_powderParticle));
		} else if (object instanceof ParticleGroupWithLifeTime) {
			particleGroupList.add((ParticleGroupWithLifeTime) object);
		} else if (object instanceof Bomb) {
			bombList.add((Bomb) object);
		}
	}

	public void contact(Object[] data, ArrayList<Entity> entityList, ArrayList<Ammo> ammoList,
			ArrayList<Missile> missileList, World world, ArrayList<ParticleGroupWithLifeTime> particleGroupList,
			ArrayList<Bomb> bombList, Group group, Body frictionBox, float RATIO) {
		Body bodyA = (Body) data[0];
		Body bodyB = (Body) data[1];

		if (bodyA.getUserData() instanceof Missile) {
			Missile missile = (Missile) bodyA.getUserData();
			if (bodyB.getUserData() instanceof Tank) {
				if (((Tank) bodyB.getUserData()).damage(5)) {
					missile.getTank().increaseScore();
				}
				particleGroupList.add(new ParticleGroupWithLifeTime(bodyA.getPosition(), (Vec2) data[2], 10, RATIO,
						world, group, ParticleType.b2_powderParticle));
				missile.destroy(entityList, group, world);
				ammoList.remove(missile);
				missileList.remove(missile);
			}
		} else if (bodyA.getUserData() instanceof Bullet) {
			Bullet bullet = (Bullet) bodyA.getUserData();
			if (bodyB.getUserData() instanceof Tank) {
				if (((Tank) bodyB.getUserData()).damage(1)) {
					if (bullet.getTank() != null) {
						bullet.getTank().increaseScore();
					}
				}
				particleGroupList.add(new ParticleGroupWithLifeTime(bodyA.getPosition(), (Vec2) data[2], 5, RATIO,
						world, group, ParticleType.b2_powderParticle));
				bullet.destroy(entityList, group, world);
				ammoList.remove(bullet);
			} else if (bodyA.getFixtureList().getRestitution() == 0) {
				particleGroupList.add(new ParticleGroupWithLifeTime(bodyA.getPosition(), (Vec2) data[2], 5, RATIO,
						world, group, ParticleType.b2_powderParticle));
				bullet.destroy(entityList, group, world);
				ammoList.remove(bullet);
			}
		} else if (bodyA.getUserData() instanceof Bomb) {
			Bomb bomb = (Bomb) bodyA.getUserData();
			if (bodyB.getUserData() instanceof Tank) {
				if (((Tank) bodyB.getUserData()).damage(10)) {
					bomb.getTank().increaseScore();
				}
				int maxBullets = 10;
				for (int j = 0; j < maxBullets; j++) {
					Vec2 localPosition = new Vec2();
					Rot rot = new Rot((float) (2 * Math.PI * j / maxBullets));
					Rot.mulToOut(rot, new Vec2(0.1f * RATIO, 0), localPosition);
					ammoList.add(
							new Bullet(entityList, null, (bomb.getBody().getPosition().x + localPosition.x) / RATIO,
									(bomb.getBody().getPosition().y + localPosition.y) / RATIO,
									new Vec2(rot.getCos() * 10, rot.getSin() * 10), Color.PURPLE, world, group,
									frictionBox, 1, 5, RATIO));
				}
				bomb.destroy(entityList, group, world);
				bombList.remove(bomb);
			}
		} else if (bodyA.getUserData() instanceof Bonus) {
			Bonus bonus = (Bonus) bodyA.getUserData();
			if (bodyB.getUserData() instanceof Tank) {
				if (bonus.getType() == BonusType.FIREBOOST) {
					((Tank) bodyB.getUserData()).increaseBulletImpulse(1);
				} else if (bonus.getType() == BonusType.TANKBOOST) {
					((Tank) bodyB.getUserData()).increaseSpeed(0.1f);
				} else if (bonus.getType() == BonusType.MISSILEBONUS) {
					((Tank) bodyB.getUserData()).setWeaponType(WeaponType.MISSILE);
				} else if (bonus.getType() == BonusType.LASER) {
					((Tank) bodyB.getUserData()).setWeaponType(WeaponType.LASER);
				} else if (bonus.getType() == BonusType.SUPERLASER) {
					((Tank) bodyB.getUserData()).setWeaponType(WeaponType.SUPERLASER);
				} else if (bonus.getType() == BonusType.BOMBBONUS) {
					((Tank) bodyB.getUserData()).setWeaponType(WeaponType.BOMB);
				}
				bonus.destroy(entityList, group, world);
			}
		}
	}

	public void addRandomBoxes(ArrayList<Entity> entityList, ArrayList<Box> boxList, Group group, Scene scene,
			World world, Body frictionBox, float RATIO) {
		for (int i = 0; i < Math.random() * 10 * (scene.getWidth() * scene.getHeight()) / (640 * 480); i++) {
			boxList.add(new Box(entityList, (float) (Math.random() * scene.getWidth()),
					(float) (Math.random() * scene.getHeight()), Images.BOX.image, world, group, frictionBox, RATIO));
		}
	}

	public void setBorders(Entity[] borders, World world, float width, float height, float RATIO) {
		borders[0].getBody().setTransform(new Vec2(width * RATIO / 2, 0), 0);
		((PolygonShape) (borders[0].getBody().getFixtureList().getShape())).setAsBox(width * RATIO / 2, 10 * RATIO);

		borders[1].getBody().setTransform(new Vec2(0, height * RATIO / 2), 0);
		((PolygonShape) (borders[1].getBody().getFixtureList().getShape())).setAsBox(10 * RATIO, height * RATIO / 2);

		borders[2].getBody().setTransform(new Vec2(width * RATIO / 2, height * RATIO), 0);
		((PolygonShape) (borders[2].getBody().getFixtureList().getShape())).setAsBox(width * RATIO / 2, 10 * RATIO);

		borders[3].getBody().setTransform(new Vec2(width * RATIO, height * RATIO / 2), 0);
		((PolygonShape) (borders[3].getBody().getFixtureList().getShape())).setAsBox(10 * RATIO, height * RATIO / 2);
	}

	public Entity createBorder(ArrayList<Entity> entityList, World world, Group group, float RATIO) {
		Entity entity = new Entity();
		entity.initEntity(entityList, null, BodyType.STATIC, 0, 0, world, new PolygonShape(), 1, 0.5f, false, group, 0,
				RATIO);
		return entity;
	}

}
