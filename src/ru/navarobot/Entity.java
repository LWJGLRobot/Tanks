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

import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import javafx.scene.Group;
import javafx.scene.Node;

public class Entity {
	private Node node;
	private Body body;

	public void initEntity(ArrayList<Entity> entityList, Node node, BodyType type, float x, float y, World world, Shape shape,
			float density, float friction, boolean isBullet, Group group, float restitution, float RATIO) {
		this.node = node;
		if (node != null) {
			group.getChildren().add(node);
		}

		BodyDef bodyDef = new BodyDef();
		bodyDef.setType(type);
		bodyDef.setPosition(new Vec2(x * RATIO, y * RATIO));
		bodyDef.setBullet(isBullet);
		body = world.createBody(bodyDef);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.setRestitution(restitution);
		fixtureDef.setShape(shape);
		fixtureDef.setDensity(density);
		fixtureDef.setFriction(friction);
		body.createFixture(fixtureDef);
		
		entityList.add(this);
	}
	
	public void updatePositionAndAngle(float RATIO) {

	}

	public void destroy(ArrayList<Entity> entityList, Group group, World world) {
		entityList.remove(this);
		if (node != null) {
			group.getChildren().remove(node);
		}
		world.destroyBody(body);
	}

	public Node getNode() {
		return node;
	}

	public Body getBody() {
		return body;
	}
}
