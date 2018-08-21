package ru.navarobot;

import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;

public class Laser {
	private long time;
	private Vec2 start, end;
	
	public Laser(Vec2 start, Vec2 end) {
		time = System.currentTimeMillis();
		this.start = start.clone();
		this.end = end;
	}
	
	public boolean checkForLifeTime() {
		if(System.currentTimeMillis() - time > 200) {
			return true;
		} else {
			return false;
		}
	}

	public void shoot(World world) {
		world.raycast(new RayCastCallback() {

			@Override
			public float reportFixture(Fixture fixture, Vec2 point, Vec2 normal, float fraction) {
				if (fixture.getBody().getUserData() instanceof Tank) {
					((Tank) fixture.getBody().getUserData()).damage(8);
					end = point.clone();
					return 0;
				} else {
					return -1;
				}
			}
		}, start, end);
	}

	public Vec2 getStart() {
		return start;
	}

	public Vec2 getEnd() {
		return end;
	}
}
