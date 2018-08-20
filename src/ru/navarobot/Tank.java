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
	private Text text;
	private boolean died;
	private float bulletImpulse;
	private int score;

	public Tank(float x, float y, Image image, World world, Body frictionBox, float RATIO) {

		text = new Text();

		this.imageView = new ImageView();
		restart(image);
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
		fixtureDef.setDensity(1f);
		fixtureDef.setFriction(0.5f);
		body.createFixture(fixtureDef);
		// default
		body.setUserData(this);

		FrictionJointDef frictionJointDef = new FrictionJointDef();
		frictionJointDef.initialize(body, frictionBox, body.getPosition());
		frictionJointDef.maxForce = 1f;
		frictionJointDef.maxTorque = 1f;
		world.createJoint(frictionJointDef);
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
		setDied(false);
		getImageView().setImage(image);
		force = 1.3f;
		torque = 1.05f;
		bulletImpulse = 0.01f;
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
			imageView.setImage(Images.POOP);
			return true;
		}
		return false;
	}

	public Vec2 getBulletPosition(float k, float RATIO) {
		return new Vec2(
				(float) body.getPosition().x
						+ (float) (Math.cos(body.getAngle()) * imageView.getImage().getWidth() * RATIO * k),
				(float) body.getPosition().y
						+ (float) (Math.sin(body.getAngle()) * imageView.getImage().getWidth() * RATIO * k));
	}

	public Bullet shoot(Color color, World world, Body frictionBox, float RATIO) {
		if (died)
			return null;
		Vec2 bulletPosition = getBulletPosition(0.7f, RATIO);
		Bullet bullet = new Bullet(this, bulletPosition.x / RATIO, bulletPosition.y / RATIO,
				new Vec2((float) (Math.cos(body.getAngle()) * bulletImpulse),
						(float) (Math.sin(body.getAngle()) * bulletImpulse)),
				body.getLinearVelocity(), color, world, frictionBox, RATIO);
		return bullet;
	}

	public Body getBody() {
		return body;
	}

	public void updatePositionAndAngle(float RATIO) {
		text.setX(body.getPosition().x / RATIO);
		text.setY(body.getPosition().y / RATIO - 30);

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
