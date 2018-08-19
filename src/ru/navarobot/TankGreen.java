package ru.navarobot;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class TankGreen extends Tank {

	private float destPosition[];
	private boolean isShooting;

	public TankGreen(float x, float y, Image image, World world, Body frictionBox, float RATIO) {
		super(x, y, image, world, frictionBox, RATIO);
		
		destPosition = new float[2];
	}

	public Bullet shootOnce(Color color, World world, Body frictionBox, float RATIO) {
		isShooting = false;
		return shoot(color, world, frictionBox, RATIO);
	}

	public void moveOneStep(float RATIO) {
		if (isDied())
			return;
		if (Math.abs(destPosition[0] - getBody().getPosition().x / RATIO) > getImageView().getImage().getWidth() || Math
				.abs(destPosition[1] - getBody().getPosition().y / RATIO) > getImageView().getImage().getWidth()) {
			getBody().setTransform(getBody().getPosition(),
					(float) Math.atan2(destPosition[1] - getBody().getPosition().y / RATIO,
							destPosition[0] - getBody().getPosition().x / RATIO));
			moveForward();
		}
	}

	public void processInput(MouseEvent event) {
		if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
			if (event.getButton() == MouseButton.PRIMARY) {
				setShooting(true);
			}
		} else if(event.getEventType() == MouseEvent.MOUSE_MOVED) {
			destPosition[0] = (float) event.getSceneX();
			destPosition[1] = (float) event.getSceneY();
		}
	}

	public boolean isShooting() {
		if (isDied()) {
			return false;
		}
		return isShooting;
	}

	public void setShooting(boolean isShooting) {
		this.isShooting = isShooting;
	}

}
