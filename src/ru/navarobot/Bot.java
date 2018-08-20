package ru.navarobot;

import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Bot extends Tank {

	private boolean rotate;
	private boolean move;

	public Bot(float x, float y, Image image, World world, Body frictionBox, float RATIO) {
		super(x, y, image, world, frictionBox, RATIO);
		move = true;
	}

	public Bullet shootToPlayer(Color color, World world, Body frictionBox, float RATIO) {
		boolean findPlayer[] = new boolean[1];
		world.raycast(new RayCastCallback() {

			@Override
			public float reportFixture(Fixture fixture, Vec2 point, Vec2 normal, float fraction) {
				if (fixture.getBody().getUserData() instanceof TankRedAndBlue
						|| fixture.getBody().getUserData() instanceof TankGreen) {
					findPlayer[0] = true;
				}
				return 0;
			}
		}, getBody().getPosition(), getBulletPosition(10, RATIO));
		if (findPlayer[0]) {
			return shoot(color, world, frictionBox, RATIO);
		} else {
			return null;
		}
	}

	public void moveOneStep() {
		if (isDied())
			return;
		if (Math.random() < 0.1) {
			rotate = !rotate;
		}
		if (Math.random() < 0.05) {
			move = !move;
		}
		if (rotate) {
			rotateLeft();
		} else {
			rotateRight();
		}
		if (move) {
			moveForward();
		} else {
			moveBackward();
		}
	}

}