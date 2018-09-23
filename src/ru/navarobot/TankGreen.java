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

import java.util.ArrayList;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class TankGreen extends Tank {

	private float destPosition[];
	private boolean isShooting;
	private boolean moveBackward;

	public TankGreen(ArrayList<Entity> entityList, float x, float y, Image image, World world, Group group,
			Body frictionBox, float RATIO) {
		super(entityList, x, y, image, world, group, frictionBox, 1, 1, 20, 30, RATIO);

		getBody().setUserData(this);

		destPosition = new float[2];
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

	public void moveOneStep(float RATIO) {
		if (isDied())
			return;
		if (Math.abs(destPosition[0] - getBody().getPosition().x / RATIO) > getImageView().getImage().getWidth() || Math
				.abs(destPosition[1] - getBody().getPosition().y / RATIO) > getImageView().getImage().getWidth()) {
			float newAngle = (float) Math.atan2(destPosition[1] - getBody().getPosition().y / RATIO,
					destPosition[0] - getBody().getPosition().x / RATIO);
			float diff = (float) Math.atan2(Math.sin(newAngle - getBody().getAngle()),
					Math.cos(newAngle - getBody().getAngle()));
			if (diff > 0) {
				rotateRight();
			} else {
				rotateLeft();
			}
			if (!moveBackward) {
				moveForward();
			} else {
				moveBackward();
			}
		}
	}

	public void processInput(MouseEvent event) {
		if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
			if (event.getButton() == MouseButton.PRIMARY) {
				setShooting(true);
			} else if (event.getButton() == MouseButton.SECONDARY) {
				moveBackward = false;
			}
		} else if (event.getEventType() == MouseEvent.MOUSE_MOVED) {
			destPosition[0] = (float) event.getSceneX();
			destPosition[1] = (float) event.getSceneY();
		} else if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
			if (event.getButton() == MouseButton.SECONDARY) {
				moveBackward = true;
			}
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
