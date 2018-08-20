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
		getBody().setUserData(this);
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
