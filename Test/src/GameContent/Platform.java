package GameContent;

import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.particles.Particle;
import org.newdawn.slick.particles.ParticleEmitter;
import org.newdawn.slick.particles.ParticleSystem;

public class Platform implements ParticleEmitter{
	
	protected Rectangle hitBounds;
	protected Image sprite;	
	protected InGameState gameState;
	
	private static Random rnd = new Random();
	
	public Platform(Rectangle hitBounds, Image sprite, InGameState gameState) {
		this.hitBounds = hitBounds;
		this.sprite = sprite;
		this.gameState = gameState;
		this.accelaration = -2 * startSpeed / particleLifeTime;
	}

	public void onHit(Player p){
		Rectangle playerRenderRect = gameState.calcRenderRect(p.getBounds());
		for(int i = 0; i < particlesPerSide; i++){
			Particle particle = getNewBasicParticle();
			particle.setVelocity(maxSpeed - minSpeed * rnd.nextFloat(), startSpeed);
			particle.setPosition(playerRenderRect.getMaxX(), playerRenderRect.getMaxY());
			Particle next = getNewBasicParticle();
			next.setPosition(playerRenderRect.getMinX(), playerRenderRect.getMaxY());
			next.setVelocity(-(maxSpeed - minSpeed * rnd.nextFloat()), startSpeed);
		}
	}
	
	private Particle getNewBasicParticle(){
		Particle particle = gameState.getParticleSystem().getNewParticle(this, particleLifeTime);
		particle.setUsePoint(Particle.USE_QUADS);
		Color c = getRandomColorFromSprite();
		particle.setColor(c.r, c.g, c.b, c.a);
		particle.setSize(gameState.getTextureScaling() * 4);
		particle.setOriented(isOriented());
		particle.setScaleY(maxScale - minScale * rnd.nextFloat());
		return particle;
	}
	
	private Color getRandomColorFromSprite(){
		Color c = sprite.getColor(random(0,sprite.getWidth()), random(0,sprite.getHeight()));
		for(int retries = 0; c.a < 1 && retries < 1000; retries++){
			c = sprite.getColor(random(0,sprite.getWidth()), random(0,sprite.getHeight()));
		}
		return c;
	}
	
	protected int random(int min, int max){
		return min + rnd.nextInt(max - min + 1);
	}
	
	public void update(){
		
	}
	
	public void draw(float offset, InGameState gameState,Graphics g){
		Rectangle drawBounds = gameState.calcRenderRect(hitBounds);
		sprite.draw(drawBounds.getX(), drawBounds.getY(), gameState.getTextureScaling());
	}
	
	public Rectangle getHitBounds(){
		return hitBounds;
	}
	
	public Rectangle getReCalculatedHitBounds(InGameState gameState){
		return getViewBounds(gameState);
	}
	
	public Rectangle getViewBounds(InGameState gameState){
		return gameState.calcRenderRect(hitBounds);
	}

	
	private final int particleLifeTime=500;
	private final int particlesPerSide = 10;
	private final float minSpeed = 0.05f;
	private final float maxSpeed = 1.0f;
	private final float startSpeed = -0.8f;
	private final float accelaration;
	private final float minScale = 1f;
	private final float maxScale = 1.5f;
	private float adjustX, adjustY, lastAdjustX, lastAdjustY;
	
	@Override
	public void update(ParticleSystem system, int delta) {
		adjustX -= lastAdjustX;
		lastAdjustX = adjustX;
		adjustY -= lastAdjustY;
		lastAdjustY = adjustY;
	}

	@Override
	public boolean completed() {
		return this.getHitBounds().getY() - gameState.getCameraHeight() < -100f;
	}

	@Override
	public void wrapUp() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateParticle(Particle particle, int delta) {
		particle.adjustVelocity(0, delta * accelaration);
		particle.move(adjustX, adjustY);
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public void setEnabled(boolean enabled) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean useAdditive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Image getImage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isOriented() {
		return true;
	}

	@Override
	public boolean usePoints(ParticleSystem system) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void resetState() {
		// TODO Auto-generated method stub
	}
	
	public void adjustX(float adjustment){
		adjustX += adjustment;
	}
	public void adjustY(float adjustment){
		adjustY += adjustment;
	}
	
	public boolean movePlayerWithPlatform(){
		return false;
	}
	
	public boolean applyPlayerCollision(){
		return true;
	}
}
