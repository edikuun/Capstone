package Platforms;

import java.util.LinkedList;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.particles.Particle;
import org.newdawn.slick.particles.ParticleSystem;

import GameContent.InGameState;
import GameContent.Platform;
import GameContent.Player;

public class FlexiblePlatform extends Platform{

	private boolean useSuperUpdate=true, useSuperOnHit=true, useSuperParticleSystemUpdate=true, useSuperParticleUpdate=true;
	private LinkedList<PlatformListener> listeners = new LinkedList<PlatformListener>();
	
	public FlexiblePlatform(Rectangle hitBounds, Image sprite,
			InGameState gameState) {
		super(hitBounds, sprite, gameState);
	}

	@Override
	public void update() {
		if(useSuperUpdate)
			super.update();
		for(PlatformListener pl : listeners)
			pl.update(this);
	}
	
	@Override
	public void onHit(Player p) {
		if(useSuperOnHit)
			super.onHit(p);
		for(PlatformListener pl : listeners)
			pl.onHit(p, this);
	}
	
	@Override
	public void update(ParticleSystem system, int delta) {
		if(useSuperParticleSystemUpdate)
			super.update(system, delta);
		for(PlatformListener pl : listeners)
			pl.update(this, system, delta);
	}
	
	@Override
	public void updateParticle(Particle particle, int delta) {
		if(useSuperParticleUpdate)
			super.updateParticle(particle, delta);
		for(PlatformListener pl : listeners)
			pl.updateParticle(this, particle, delta);
	}
	
	public void addListener(PlatformListener pl){
		listeners.add(pl);
	}
	
	public InGameState getInGameState(){
		return gameState;
	}
	
	public Image getSprite(){
		return sprite;
	}

	public boolean useSuperUpdate() {
		return useSuperUpdate;
	}

	public void setUseSuperUpdate(boolean useSuperUpdate) {
		this.useSuperUpdate = useSuperUpdate;
	}

	public boolean useSuperOnHit() {
		return useSuperOnHit;
	}

	public void setUseSuperOnHit(boolean useSuperOnHit) {
		this.useSuperOnHit = useSuperOnHit;
	}

	public boolean useSuperParticleSystemUpdate() {
		return useSuperParticleSystemUpdate;
	}

	public void setUseSuperParticleSystemUpdate(boolean useSuperParticleSystemUpdate) {
		this.useSuperParticleSystemUpdate = useSuperParticleSystemUpdate;
	}

	public boolean useSuperParticleUpdate() {
		return useSuperParticleUpdate;
	}

	public void setUseSuperParticleUpdate(boolean useSuperParticleUpdate) {
		this.useSuperParticleUpdate = useSuperParticleUpdate;
	}
	
	
}
