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
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class Tank {
	private ImageView imageView;
	private float force, torque;
	private Body body;
	private int health;
	private Text healthText;
	private boolean died;
	private float bulletImpulse;

	public Tank(float x, float y, Image image, World world, Body frictionBox, float RATIO) {

		this.imageView = new ImageView(image);
		imageView.setX(x - image.getWidth() / 2);
		imageView.setY(y - image.getHeight() / 2);
		imageView.setCache(true);
		imageView.setSmooth(true);

		force = 1.3f;
		torque = 1.05f;

		health = 10;
		healthText = new Text(health + "");

		bulletImpulse = 0.01f;

		BodyDef bodyDef = new BodyDef();
		bodyDef.setType(BodyType.DYNAMIC);
		bodyDef.setPosition(new Vec2(x * RATIO, y * RATIO));
		body = world.createBody(bodyDef);
		FixtureDef fixtureDef = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		shape.setAsBox((float) image.getWidth() * RATIO / 2, (float) image.getHeight() * RATIO / 2);
		fixtureDef.setShape(shape);
		fixtureDef.setDensity(1f);
		fixtureDef.setFriction(0.5f);
		body.createFixture(fixtureDef);

		FrictionJointDef frictionJointDef = new FrictionJointDef();
		frictionJointDef.initialize(body, frictionBox, body.getPosition());
		frictionJointDef.maxForce = 1f;
		frictionJointDef.maxTorque = 1f;
		world.createJoint(frictionJointDef);
	}
	
	public void increaseBulletImpulse(float impulse) {
		bulletImpulse += impulse;
	}

	public void restart(Image image) {
		setDied(false);
		getImageView().setImage(image);
		health = 10;
		healthText.setText(health + "");
	}

	public boolean isDied() {
		return died;
	}

	public void setDied(boolean died) {
		this.died = died;
	}

	public Text getText() {
		return healthText;
	}

	public void damage(int damage) {
		health -= damage;
		healthText.setText(health + "");
		if (health < 0) {
			died = true;
			imageView.setImage(Images.POOP);
		}
	}

	public Bullet shoot(Color color, World world, Body frictionBox, float RATIO) {
		if (died)
			return null;
		Bullet bullet = new Bullet(
				(float) body.getPosition().x / RATIO
						+ (float) (Math.cos(body.getAngle()) * imageView.getImage().getWidth()),
				(float) body.getPosition().y / RATIO
						+ (float) (Math.sin(body.getAngle()) * imageView.getImage().getWidth()),
				new Vec2((float) (Math.cos(body.getAngle()) * bulletImpulse),
						(float) (Math.sin(body.getAngle()) * bulletImpulse)),
				color, world, frictionBox, RATIO);
		return bullet;
	}

	public Body getBody() {
		return body;
	}

	public void updatePositionAndAngle(float RATIO) {
		healthText.setX(body.getPosition().x / RATIO);
		healthText.setY(body.getPosition().y / RATIO - 30);

		imageView.setX(body.getPosition().x / RATIO - imageView.getImage().getWidth() / 2);
		imageView.setY(body.getPosition().y / RATIO - imageView.getImage().getHeight() / 2);
		imageView.setRotate(Math.toDegrees(body.getAngle()));
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
		body.applyForceToCenter(
				new Vec2((float) (Math.cos(body.getAngle()) * force), (float) (Math.sin(body.getAngle()) * force)));
	}

	public void rotate(float torque) {
		body.applyTorque(torque);
	}

	public ImageView getImageView() {
		return imageView;
	}
}
