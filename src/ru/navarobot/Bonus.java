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

	public Bonus(float x, float y, Image image, World world, float RATIO) {
		this.imageView = new ImageView(image);
		imageView.setX(x - image.getWidth() / 2);
		imageView.setY(y - image.getHeight() / 2);
		imageView.setCache(true);
		imageView.setSmooth(true);

		BodyDef bodyDef = new BodyDef();
		bodyDef.setType(BodyType.STATIC);
		bodyDef.setPosition(new Vec2(x * RATIO, y * RATIO));
		bodyDef.setBullet(true);
		body = world.createBody(bodyDef);
		FixtureDef fixtureDef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius((float) image.getWidth() * RATIO / 2);
		fixtureDef.setShape(shape);
		body.createFixture(fixtureDef);
	}

	public Body getBody() {
		return body;
	}

	public ImageView getImageView() {
		return imageView;
	}
}
