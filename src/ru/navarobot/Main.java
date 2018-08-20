package ru.navarobot;

import java.util.ArrayDeque;
import java.util.ArrayList;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Rot;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.particle.ParticleGroupDef;
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

		ImageView background = new ImageView(Images.BACKGROUND);
		group.getChildren().add(background);

		TankRedAndBlue tankRed = new TankRedAndBlue(200, 200, Images.TANKRED, world, frictionBox, RATIO,
				new KeyCode[] { KeyCode.W, KeyCode.S, KeyCode.A, KeyCode.D, KeyCode.SPACE });
		TankGreen tankGreen = new TankGreen(260, 100, Images.TANKGREEN, world, frictionBox, RATIO);
		TankRedAndBlue tankBlue = new TankRedAndBlue(100, 260, Images.TANKBLUE, world, frictionBox, RATIO,
				new KeyCode[] { KeyCode.UP, KeyCode.DOWN, KeyCode.LEFT, KeyCode.RIGHT, KeyCode.P });

		ArrayList<Bullet> bullets = new ArrayList<>();
		ArrayList<Box> boxList = new ArrayList<>();
		ArrayList<Bonus> bonusList = new ArrayList<>();
		ArrayList<ParticleGroupWithLifeTime> particleGroupList = new ArrayList<>();
		ArrayList<Bot> botList = new ArrayList<>();

		group.getChildren().addAll(tankRed.getImageView(), tankGreen.getImageView(), tankBlue.getImageView(),
				tankRed.getText(), tankGreen.getText(), tankBlue.getText());

		scene.setCursor(new ImageCursor(Images.CURSOR, Images.CURSOR.getWidth() / 2, Images.CURSOR.getHeight() / 2));

		ArrayDeque<KeyCode> keyQueue = new ArrayDeque<>();

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

		Body[] borders = new Body[] { createBorder(world), createBorder(world), createBorder(world),
				createBorder(world) };

		setBorders(borders, world, (float) scene.getWidth(), (float) scene.getHeight(), RATIO);

		addRandomBoxes(boxList, group, scene, world, frictionBox, RATIO);

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
				Body bodyA, bodyB;
				if (contact.m_fixtureA.getBody().getUserData() instanceof Tank) {
					bodyA = contact.m_fixtureA.getBody();
					bodyB = contact.m_fixtureB.getBody();
				} else if (contact.m_fixtureB.getBody().getUserData() instanceof Tank) {
					bodyA = contact.m_fixtureB.getBody();
					bodyB = contact.m_fixtureA.getBody();
				} else {
					return;
				}

				contactData[0] = bodyA;
				contactData[1] = bodyB;
				contactData[2] = contact.m_manifold.localPoint.clone();
			}
		});

		new AnimationTimer() {
			long time = System.currentTimeMillis();

			@Override
			public void handle(long now) {
				canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

				KeyCode keyCode = keyQueue.poll();
				if (keyCode == KeyCode.R) {
					tankRed.restart(Images.TANKRED);
					tankGreen.restart(Images.TANKGREEN);
					tankBlue.restart(Images.TANKBLUE);
					removeBoxes(boxList, group, world);
					addRandomBoxes(boxList, group, scene, world, frictionBox, RATIO);
					removeBots(botList, group, world);
				} else if (keyCode == KeyCode.B) {
					addBot(botList, scene, group, Images.TANKBOT, world, frictionBox, RATIO);
				}

				if (Math.random() < 0.001) {
					if (Math.random() < 0.5) {
						addBonus(bonusList, group, scene, world, BonusType.FIREBOOST, RATIO);
					} else {
						addBonus(bonusList, group, scene, world, BonusType.TANKBOOST, RATIO);
					}
				}

				if (tankRed.isShooting()) {
					addBullet(tankRed.shootOnce(Color.RED, world, frictionBox, RATIO), bullets, group, world);
				}

				if (tankGreen.isShooting()) {
					addBullet(tankGreen.shootOnce(Color.GREEN, world, frictionBox, RATIO), bullets, group, world);
				}

				if (tankBlue.isShooting()) {
					addBullet(tankBlue.shootOnce(Color.BLUE, world, frictionBox, RATIO), bullets, group, world);
				}

				for (Bot bot : botList) {
					if (Math.random() < 0.1) {
						addBullet(bot.shootToPlayer(Color.BLACK, world, frictionBox, RATIO), bullets, group, world);
					}
				}

				tankRed.moveOneStep();
				tankGreen.moveOneStep(RATIO);
				tankBlue.moveOneStep();

				for (Bot bot : botList) {
					bot.moveOneStep();
				}

				world.step((System.currentTimeMillis() - time) / 1000f, 10, 10);
				time = System.currentTimeMillis();

				if (contactData[0] != null) {
					contact(contactData, world, bullets, particleGroupList, bonusList, group, RATIO);
					contactData[0] = null;
				}

				tankRed.updatePositionAndAngle(RATIO);
				tankGreen.updatePositionAndAngle(RATIO);
				tankBlue.updatePositionAndAngle(RATIO);

				for (Bot bot : botList) {
					bot.updatePositionAndAngle(RATIO);
				}

				for (Bonus bonus : bonusList) {
					bonus.updatePositionAndAngle(RATIO);
				}

				if (world.getParticlePositionBuffer() != null) {
					for (Vec2 particlePosition : world.getParticlePositionBuffer()) {
						// canvas.getGraphicsContext2D()
						// .setFill(new Color(Math.random(), Math.random(), Math.random(), 1));
						canvas.getGraphicsContext2D().fillOval(particlePosition.x / RATIO, particlePosition.y / RATIO,
								world.getParticleRadius() / RATIO, world.getParticleRadius() / RATIO);
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

				for (int i = 0; i < bullets.size();) {
					bullets.get(i).updatePosition(RATIO);
					if (System.currentTimeMillis() - bullets.get(i).getTime() > 10000) {
						destroyBullet(bullets.get(i), bullets, world, group);
					} else {
						i++;
					}
				}

				for (Box box : boxList) {
					box.updatePositionAndAngle(RATIO);
				}
			}
		}.start();

		primaryStage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("res/images/poop.png")));
		primaryStage.setTitle("Танчики");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public void contact(Object[] data, World world, ArrayList<Bullet> bullets,
			ArrayList<ParticleGroupWithLifeTime> particleGroupList, ArrayList<Bonus> bonusList, Group group,
			float RATIO) {
		Body bodyA = (Body) data[0];
		Body bodyB = (Body) data[1];
		Vec2 point = (Vec2) data[2];

		Tank tank = (Tank) bodyA.getUserData();

		if (bodyB.getUserData() instanceof Bullet) {
			Bullet bullet = (Bullet) bodyB.getUserData();
			if (tank.damage(1)) {
				bullet.getTank().increaseScore();
			}
			destroyBullet(bullet, bullets, world, group);
			Vec2 localPoint = new Vec2();
			Rot.mulToOut(new Rot(tank.getBody().getAngle()), point, localPoint);
			addParticleGroup(particleGroupList, localPoint.addLocal(tank.getBody().getPosition()), RATIO, world, group);
		} else if (bodyB.getUserData() instanceof Bonus) {
			Bonus bonus = (Bonus) bodyB.getUserData();
			if (bonus.getType() == BonusType.FIREBOOST) {
				tank.increaseBulletImpulse(0.01f);
			} else if (bonus.getType() == BonusType.TANKBOOST) {
				tank.increaseSpeed(0.1f);
			}
			removeBonus(bonus, bonusList, group, world);
		}
	}

	public void addBot(ArrayList<Bot> botList, Scene scene, Group group, Image image, World world, Body frictionBox,
			float RATIO) {
		Bot bot = new Bot((float) (Math.random() * scene.getWidth()), (float) (Math.random() * scene.getHeight()),
				image, world, frictionBox, RATIO);
		botList.add(bot);
		group.getChildren().addAll(bot.getImageView(), bot.getText());
	}

	public void removeBots(ArrayList<Bot> botList, Group group, World world) {
		for (Bot bot : botList) {
			world.destroyBody(bot.getBody());
			group.getChildren().remove(bot.getImageView());
			group.getChildren().remove(bot.getText());
		}
		botList.clear();
	}

	public void addParticleGroup(ArrayList<ParticleGroupWithLifeTime> particleGroupList, Vec2 position, float RATIO,
			World world, Group group) {
		ParticleGroupDef particleGroupDef = new ParticleGroupDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(5 * RATIO);
		particleGroupDef.shape = shape;
		particleGroupDef.flags = ParticleType.b2_powderParticle;
		particleGroupDef.position.set(position);
		particleGroupList.add(new ParticleGroupWithLifeTime(world.createParticleGroup(particleGroupDef)));
	}

	public void addBonus(ArrayList<Bonus> bonusList, Group group, Scene scene, World world, BonusType type,
			float RATIO) {
		Bonus bonus = new Bonus((float) (Math.random() * scene.getWidth()), (float) (Math.random() * scene.getHeight()),
				world, type, RATIO);
		bonusList.add(bonus);
		group.getChildren().add(bonus.getImageView());
	}

	public void removeBonus(Bonus bonus, ArrayList<Bonus> bonusList, Group group, World world) {
		bonusList.remove(bonus);
		group.getChildren().remove(bonus.getImageView());
		world.destroyBody(bonus.getBody());
	}

	public void addRandomBoxes(ArrayList<Box> boxList, Group group, Scene scene, World world, Body frictionBox,
			float RATIO) {
		for (int i = 0; i < Math.random() * 5; i++) {
			Box box = new Box((float) (Math.random() * scene.getWidth()), (float) (Math.random() * scene.getHeight()),
					Images.BOX, world, frictionBox, RATIO);
			boxList.add(box);
			group.getChildren().add(box.getImageView());
		}
	}

	public void removeBoxes(ArrayList<Box> boxList, Group group, World world) {
		for (Box box : boxList) {
			group.getChildren().remove(box.getImageView());
			world.destroyBody(box.getBody());
		}
		boxList.clear();
	}

	public void addBullet(Bullet bullet, ArrayList<Bullet> bullets, Group group, World world) {
		if (bullet == null) {
			return;
		}
		group.getChildren().add(bullet.getCircle());
		bullets.add(bullet);
	}

	public void destroyBullet(Bullet bullet, ArrayList<Bullet> bullets, World world, Group group) {
		group.getChildren().remove(bullet.getCircle());
		bullets.remove(bullet);
		world.destroyBody(bullet.getBody());
	}

	public void setBorders(Body[] borders, World world, float width, float height, float RATIO) {
		borders[0].setTransform(new Vec2(width * RATIO / 2, 0), 0);
		((PolygonShape) (borders[0].getFixtureList().getShape())).setAsBox(width * RATIO / 2, 10 * RATIO);
		borders[1].setTransform(new Vec2(0, height * RATIO / 2), 0);
		((PolygonShape) (borders[1].getFixtureList().getShape())).setAsBox(10 * RATIO, height * RATIO / 2);
		borders[2].setTransform(new Vec2(width * RATIO / 2, height * RATIO), 0);
		((PolygonShape) (borders[2].getFixtureList().getShape())).setAsBox(width * RATIO / 2, 10 * RATIO);
		borders[3].setTransform(new Vec2(width * RATIO, height * RATIO / 2), 0);
		((PolygonShape) (borders[3].getFixtureList().getShape())).setAsBox(10 * RATIO, height * RATIO / 2);
	}

	public Body createBorder(World world) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.setType(BodyType.STATIC);
		bodyDef.setPosition(new Vec2());
		Body body = world.createBody(bodyDef);
		FixtureDef fixtureDef = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		fixtureDef.setShape(shape);
		fixtureDef.setFriction(0.5f);
		body.createFixture(fixtureDef);
		return body;
	}

}
