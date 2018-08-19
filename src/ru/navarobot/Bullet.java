package ru.navarobot;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.FrictionJointDef;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Bullet {
	private Circle circle;
	private Body body;
	private long time;
	
	public Bullet(float x, float y, Vec2 impulse, Color color, World world, Body frictionBox, float RATIO) {
		circle = new Circle(x, y, 5, color);
			
		BodyDef bodyDef = new BodyDef();
		bodyDef.setType(BodyType.DYNAMIC);
		bodyDef.setPosition(new Vec2(x * RATIO, y * RATIO));
		bodyDef.setFixedRotation(true);
		bodyDef.setBullet(true);
		body = world.createBody(bodyDef);
		FixtureDef fixtureDef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(5 * RATIO);
		fixtureDef.setShape(shape);
		fixtureDef.setDensity(0.3f);
		fixtureDef.setFriction(0.1f);
		body.createFixture(fixtureDef);
		
		FrictionJointDef frictionJointDef = new FrictionJointDef();
		frictionJointDef.initialize(body, frictionBox, body.getPosition());
		frictionJointDef.maxForce = 0.01f;
		frictionJointDef.maxTorque = 0.01f;
		world.createJoint(frictionJointDef);
		
		body.applyLinearImpulse(impulse, body.getPosition(), true);
		
		time = System.currentTimeMillis();
	}
	
	public long getTime() {
		return time;
	}

	public Body getBody() {
		return body;
	}
	
	public void updatePosition(float RATIO) {
		circle.setCenterX(body.getPosition().x / RATIO);
		circle.setCenterY(body.getPosition().y / RATIO);
	}
	
	public Circle getCircle() {
		return circle;
	}
}
