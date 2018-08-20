package ru.navarobot;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

public class TankRedAndBlue extends Tank {

	private KeyCode[] keylayout;
	private boolean[] controls;
	private boolean isShooting;

	public TankRedAndBlue(float x, float y, Image image, World world, Body frictionBox, float RATIO,
			KeyCode[] keylayout) {
		super(x, y, image, world, frictionBox, RATIO);

		getBody().setUserData(this);

		this.keylayout = keylayout;

		controls = new boolean[] { false, false, false, false };

		isShooting = false;
	}

	public void processInput(KeyEvent event) {
		if (event.getEventType() == KeyEvent.KEY_PRESSED) {
			if (event.getCode() == keylayout[0]) {
				setMoveForward(true);
			} else if (event.getCode() == keylayout[1]) {
				setMoveBackward(true);
			} else if (event.getCode() == keylayout[2]) {
				setRotateLeft(true);
			} else if (event.getCode() == keylayout[3]) {
				setRotateRight(true);
			} else if (event.getCode() == keylayout[4]) {
				setShooting(true);
			}
		} else if (event.getEventType() == KeyEvent.KEY_RELEASED) {
			if (event.getCode() == keylayout[0]) {
				setMoveForward(false);
			} else if (event.getCode() == keylayout[1]) {
				setMoveBackward(false);
			} else if (event.getCode() == keylayout[2]) {
				setRotateLeft(false);
			} else if (event.getCode() == keylayout[3]) {
				setRotateRight(false);
			} else if (event.getCode() == keylayout[4]) {
				setShooting(false);
			}
		}

	}

	public Bullet shootOnce(Color color, World world, Body frictionBox, float RATIO) {
		isShooting = false;
		return shoot(color, world, frictionBox, RATIO);
	}

	public void moveOneStep() {
		if (isDied())
			return;
		if (isMoveForward()) {
			moveForward();
		}
		if (isMoveBackward()) {
			moveBackward();
		}
		if (isRotateLeft()) {
			rotateLeft();
		}
		if (isRotateRight()) {
			rotateRight();
		}
	}

	public boolean isMoveForward() {
		return controls[0];
	}

	public boolean isMoveBackward() {
		return controls[1];
	}

	public boolean isRotateLeft() {
		return controls[2];
	}

	public boolean isRotateRight() {
		return controls[3];
	}

	public void setMoveForward(boolean control) {
		controls[0] = control;
	}

	public void setMoveBackward(boolean control) {
		controls[1] = control;
	}

	public void setRotateLeft(boolean control) {
		controls[2] = control;
	}

	public void setRotateRight(boolean control) {
		controls[3] = control;
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
