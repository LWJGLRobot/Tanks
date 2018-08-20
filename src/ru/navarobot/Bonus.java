package ru.navarobot;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Bonus {
	private ImageView imageView;
	private Body body;
	private BonusType type;

	public Bonus(float x, float y, World world, BonusType type, float RATIO) {
		
		this.type = type;
		Image image = null;
		if(type == BonusType.FIREBOOST) {
			image = Images.FIREBOOST;
		} else if(type == BonusType.TANKBOOST) {
			image = Images.TANKBOOST;
		}

		this.imageView = new ImageView(image);
		imageView.setX(x - image.getWidth() / 2);
		imageView.setY(y - image.getHeight() / 2);
		imageView.setCache(true);
		imageView.setSmooth(true);

		// TODO sensor
		BodyDef bodyDef = new BodyDef();
		bodyDef.setType(BodyType.DYNAMIC);
		bodyDef.setPosition(new Vec2(x * RATIO, y * RATIO));
		body = world.createBody(bodyDef);
		FixtureDef fixtureDef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius((float) image.getWidth() * RATIO / 2);
		fixtureDef.setDensity(0.1f);
		fixtureDef.setFriction(0.1f);
		fixtureDef.setShape(shape);
		body.createFixture(fixtureDef);
		body.setUserData(this);
	}

	public BonusType getType() {
		return type;
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
