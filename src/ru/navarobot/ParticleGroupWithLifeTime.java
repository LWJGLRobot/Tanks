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

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.jbox2d.particle.ParticleGroup;
import org.jbox2d.particle.ParticleGroupDef;

import javafx.scene.Group;

public class ParticleGroupWithLifeTime {
	private long time;
	private ParticleGroup particleGroup;

	public ParticleGroupWithLifeTime(Vec2 position, Vec2 linearVelocity, float radius, float RATIO, World world,
			Group group, int flags) {
		ParticleGroupDef particleGroupDef = new ParticleGroupDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(radius * RATIO);
		// particleGroupDef.color = new ParticleColor(Color3f.RED);
		particleGroupDef.shape = shape;
		particleGroupDef.flags = flags;
		particleGroupDef.position.set(position);
		particleGroupDef.linearVelocity.set(linearVelocity);
		particleGroup = world.createParticleGroup(particleGroupDef);
		time = System.currentTimeMillis();
	}

	public ParticleGroup getParticleGroup() {
		return particleGroup;
	}

	public long getTime() {
		return time;
	}
}
