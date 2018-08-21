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

public class Images {
	public static final Image POOP = new Image(
			Images.class.getClassLoader().getResourceAsStream("res/images/poop.png"));
	public static final Image TANKRED = new Image(
			Images.class.getClassLoader().getResourceAsStream("res/images/tankRed.png"));
	public static final Image TANKGREEN = new Image(
			Images.class.getClassLoader().getResourceAsStream("res/images/tankGreen.png"));
	public static final Image TANKBLUE = new Image(
			Images.class.getClassLoader().getResourceAsStream("res/images/tankBlue.png"));
	public static final Image TANKBOT = new Image(
			Images.class.getClassLoader().getResourceAsStream("res/images/tankBot.png"));
	public static final Image BACKGROUND = new Image(
			Images.class.getClassLoader().getResourceAsStream("res/images/background.png"));
	public static final Image CURSOR = new Image(
			Images.class.getClassLoader().getResourceAsStream("res/images/cursor.png"));
	public static final Image BOX = new Image(
			Images.class.getClassLoader().getResourceAsStream("res/images/box.png"));
	public static final Image FIREBOOST = new Image(
			Images.class.getClassLoader().getResourceAsStream("res/images/fireboost.png"));
	public static final Image TANKBOOST = new Image(
			Images.class.getClassLoader().getResourceAsStream("res/images/tankboost.png"));
	public static final Image MISSILEBONUS = new Image(
			Images.class.getClassLoader().getResourceAsStream("res/images/missileBonus.png"));
	public static final Image MISSILE = new Image(
			Images.class.getClassLoader().getResourceAsStream("res/images/missile.png"));
}
