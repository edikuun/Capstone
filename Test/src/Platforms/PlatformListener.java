package Platforms;

import org.newdawn.slick.particles.Particle;
import org.newdawn.slick.particles.ParticleSystem;

import GameContent.Platform;
import GameContent.Player;

public interface PlatformListener {
	public void update(FlexiblePlatform p);
	public void update(FlexiblePlatform p, ParticleSystem system, int delta);
	public void onHit(Player p, FlexiblePlatform platform);
	public void updateParticle(FlexiblePlatform p, Particle particle, int delta);
}
