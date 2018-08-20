package ru.navarobot;

import org.jbox2d.particle.ParticleGroup;

public class ParticleGroupWithLifeTime {
	private long time;
	private ParticleGroup particleGroup;

	public ParticleGroupWithLifeTime(ParticleGroup particleGroup) {
		this.particleGroup = particleGroup;
		time = System.currentTimeMillis();
	}
	
	public ParticleGroup getParticleGroup() {
		return particleGroup;
	}

	public long getTime() {
		return time;
	}
}
