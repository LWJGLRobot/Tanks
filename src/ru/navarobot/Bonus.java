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

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Bonus extends Entity {
	public enum BonusType {
		FIREBOOST, TANKBOOST, MISSILEBONUS, LASER, SUPERLASER, BOMBBONUS, GUM, REFLECTIONLASER, HEALTH;
	}

	private BonusType type;

	public Bonus(ArrayList<Entity> entityList, float x, float y, World world, Group group, BonusType type,
			float RATIO) {

		this.type = type;
		Image image = null;
		if (type == BonusType.FIREBOOST) {
			image = Images.FIREBOOST.image;
		} else if (type == BonusType.TANKBOOST) {
			image = Images.TANKBOOST.image;
		} else if (type == BonusType.MISSILEBONUS) {
			image = Images.MISSILEBONUS.image;
		} else if (type == BonusType.LASER) {
			image = Images.LASER.image;
		} else if (type == BonusType.SUPERLASER) {
			image = Images.SUPERLASER.image;
		} else if (type == BonusType.BOMBBONUS) {
			image = Images.BOMBBONUS.image;
		} else if (type == BonusType.GUM) {
			image = Images.GUM.image;
		} else if (type == BonusType.REFLECTIONLASER) {
			image = Images.REFLECTIONLASER.image;
		} else if (type == BonusType.HEALTH) {
			image = Images.HEALTH.image;
		}

		ImageView imageView = new ImageView(image);
		imageView.setX(x - image.getWidth() / 2);
		imageView.setY(y - image.getHeight() / 2);
		imageView.setCache(true);
		imageView.setSmooth(true);

		CircleShape shape = new CircleShape();
		shape.setRadius((float) image.getWidth() * RATIO / 2);

		initEntity(entityList, imageView, BodyType.DYNAMIC, x, y, world, shape, 0.1f, 0.1f, false, group, 0, RATIO);

		getBody().setUserData(this);
	}

	public BonusType getType() {
		return type;
	}

	public void updatePositionAndAngle(float RATIO) {
		getImageView().setX(getBody().getPosition().x / RATIO - getImageView().getImage().getWidth() / 2);
		getImageView().setY(getBody().getPosition().y / RATIO - getImageView().getImage().getHeight() / 2);
		getImageView().setRotate(Math.toDegrees(getBody().getAngle()));
	}

	public ImageView getImageView() {
		return (ImageView) getNode();
	}
}
