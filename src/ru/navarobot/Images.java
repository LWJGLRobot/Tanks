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

import javafx.scene.image.Image;

public enum Images {
	POOP("poop.png"), TANKRED("tankRed.png"), TANKGREEN("tankGreen.png"), TANKBLUE("tankBlue.png"),
	TANKBOT("tankBot.png"), BACKGROUND("background.png"), CURSOR("cursor.png"), BOX("box.png"),
	FIREBOOST("fireboost.png"), TANKBOOST("tankboost.png"), MISSILEBONUS("missileBonus.png"), MISSILE("missile.png"),
	LASER("laser.png"), SUPERLASER("superLaser.png"), BOMB("bomb.png"), BOMBBONUS("bombBonus.png"), GUM("gum.png"),
	REFLECTIONLASER("reflectionLaser.png"), HEALTH("health.png"), SUN("sun.png"), WALL("wall.png");

	public final Image image;

	Images(String fileName) {
		this.image = new Image(ClassLoader.getSystemResourceAsStream("res/images/" + fileName));
	}
}
