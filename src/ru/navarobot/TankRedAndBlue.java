/*******************************************************************************
 * Copyright (C) 2018 Anvar Sultanbekov
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 ******************************************************************************/
package ru.navarobot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

public class TankRedAndBlue extends Tank {

	private KeyCode[] keylayout;
	private boolean[] controls;
	private boolean isShooting;

	public TankRedAndBlue(ArrayList<Entity> entityList, float x, float y, Image image, World world, Group group,
			Body frictionBox, float RATIO, KeyCode[] keylayout) {
		super(entityList, x, y, image, world, group, frictionBox, 1, 1, 20, 30, 0.5f, RATIO);

		getBody().setUserData(this);

		this.keylayout = keylayout;

		controls = new boolean[] { false, false, false, false };

		isShooting = false;
	}

	/*
	 * for training bots
	 */
	public void saveDat(World world) {
		double[] data = getSensorData(world);
		try {
			PrintWriter pw = new PrintWriter(
					new FileOutputStream(new File("/home/anvar/eclipse-worspace/Tanks/train.dat"), true));
			for (double num : data) {
				pw.print(num + " ");
			}
			pw.print((isMoveForward() ? 1 : 0) + " " + (isMoveBackward() ? 1 : 0) + " " + (isRotateLeft() ? 1 : 0) + " "
					+ (isRotateRight() ? 1 : 0) + "\n");
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
				setShooting(true);
			}
		}

	}

	public Object shoot(ArrayList<Entity> entityList, Color color, World world, Group group, Body frictionBox,
			float RATIO) {
		if (isShooting) {
			isShooting = false;
			return super.shoot(entityList, color, world, group, frictionBox, RATIO);
		} else {
			return null;
		}
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
