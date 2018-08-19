package ru.navarobot;

import java.util.ArrayList;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.ContactEdge;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
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

		group.getChildren().addAll(tankRed.getImageView(), tankGreen.getImageView(), tankBlue.getImageView(),
				tankRed.getText(), tankGreen.getText(), tankBlue.getText());

		scene.setCursor(new ImageCursor(Images.CURSOR, Images.CURSOR.getWidth() / 2, Images.CURSOR.getHeight() / 2));

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
				tankRed.restart(Images.TANKRED);
				tankGreen.restart(Images.TANKGREEN);
				tankBlue.restart(Images.TANKBLUE);
				removeBoxes(boxList, group, world);
				addRandomBoxes(boxList, group, scene, world, frictionBox, RATIO);
			}
		});

		Body[] borders = new Body[] { createBorder(world), createBorder(world), createBorder(world),
				createBorder(world) };

		setBorders(borders, world, (float) scene.getWidth(), (float) scene.getHeight(), RATIO);

		primaryStage.widthProperty().addListener((event, numOld, numNew) -> {
			setBorders(borders, world, (float) scene.getWidth(), (float) scene.getHeight(), RATIO);
		});

		primaryStage.heightProperty().addListener((event, numOld, numNew) -> {
			setBorders(borders, world, (float) scene.getWidth(), (float) scene.getHeight(), RATIO);
		});

		addRandomBoxes(boxList, group, scene, world, frictionBox, RATIO);

		new AnimationTimer() {
			long time = System.currentTimeMillis();

			@Override
			public void handle(long now) {
				if (Math.random() < 0.001) {
					addBonus(bonusList, group, scene, world, RATIO);
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

				tankRed.moveOneStep();
				tankGreen.moveOneStep(RATIO);
				tankBlue.moveOneStep();

				world.step((System.currentTimeMillis() - time) / 1000f, 10, 10);
				time = System.currentTimeMillis();

				for (ContactEdge edge = tankRed.getBody().getContactList(); edge != null; edge = edge.next) {
					if (edge.contact.isTouching()) {
						for (int i = 0; i < bullets.size();) {
							if (edge.other == bullets.get(i).getBody()) {
								tankRed.damage(1);
								destroyBullet(bullets.get(i), bullets, world, group);
							} else {
								i++;
							}
						}
						for (int i = 0; i < bonusList.size();) {
							if (edge.other == bonusList.get(i).getBody()) {
								tankRed.increaseBulletImpulse(0.01f);
								removeBonus(bonusList.get(i), bonusList, group, world);
							} else {
								i++;
							}
						}
					}
				}
				for (ContactEdge edge = tankGreen.getBody().getContactList(); edge != null; edge = edge.next) {
					if (edge.contact.isTouching()) {
						for (int i = 0; i < bullets.size();) {
							if (edge.other == bullets.get(i).getBody()) {
								tankGreen.damage(1);
								destroyBullet(bullets.get(i), bullets, world, group);
							} else {
								i++;
							}
						}
						for (int i = 0; i < bonusList.size();) {
							if (edge.other == bonusList.get(i).getBody()) {
								tankGreen.increaseBulletImpulse(0.01f);
								removeBonus(bonusList.get(i), bonusList, group, world);
							} else {
								i++;
							}
						}
					}
				}
				for (ContactEdge edge = tankBlue.getBody().getContactList(); edge != null; edge = edge.next) {
					if (edge.contact.isTouching()) {
						for (int i = 0; i < bullets.size();) {
							if (edge.other == bullets.get(i).getBody()) {
								tankBlue.damage(1);
								destroyBullet(bullets.get(i), bullets, world, group);
							} else {
								i++;
							}
						}
						for (int i = 0; i < bonusList.size();) {
							if (edge.other == bonusList.get(i).getBody()) {
								tankBlue.increaseBulletImpulse(0.01f);
								removeBonus(bonusList.get(i), bonusList, group, world);
							} else {
								i++;
							}
						}
					}
				}

				tankRed.updatePositionAndAngle(RATIO);
				tankGreen.updatePositionAndAngle(RATIO);
				tankBlue.updatePositionAndAngle(RATIO);

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

	public void addBonus(ArrayList<Bonus> bonusList, Group group, Scene scene, World world, float RATIO) {
		Bonus bonus = new Bonus((float) (Math.random() * scene.getWidth()), (float) (Math.random() * scene.getHeight()),
				Images.TANKBOOST, world, RATIO);
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
		((PolygonShape) (borders[0].getFixtureList().getShape())).setAsBox(width * RATIO / 2, 3 * RATIO);
		borders[1].setTransform(new Vec2(0, height * RATIO / 2), 0);
		((PolygonShape) (borders[1].getFixtureList().getShape())).setAsBox(3 * RATIO, height * RATIO / 2);
		borders[2].setTransform(new Vec2(width * RATIO / 2, height * RATIO), 0);
		((PolygonShape) (borders[2].getFixtureList().getShape())).setAsBox(width * RATIO / 2, 3 * RATIO);
		borders[3].setTransform(new Vec2(width * RATIO, height * RATIO / 2), 0);
		((PolygonShape) (borders[3].getFixtureList().getShape())).setAsBox(3 * RATIO, height * RATIO / 2);
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
