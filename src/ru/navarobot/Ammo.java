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

import org.jbox2d.dynamics.World;

import javafx.scene.Group;

public class Ammo extends Entity {
	private long time;
	private Tank tank;

	public void initAmmo(Tank tank) {
		this.tank = tank;
		time = System.currentTimeMillis();
	}

	public boolean checkForLifeTime(ArrayList<Entity> entityList, Group group, World world) {
		if (System.currentTimeMillis() - time > 10000) {
			destroy(entityList, group, world);
			return true;
		}
		return false;
	}

	public Tank getTank() {
		return tank;
	}
}
