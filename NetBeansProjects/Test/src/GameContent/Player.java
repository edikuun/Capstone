package GameContent;

import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.StateBasedGame;

import ImageLoaders.SpritesheetLoader;

public class Player implements KeyListener {

    //Max Jump Height: 0.5*(maxSpeed)�/gravitation
    private int score = 0;
    private InGameState gameState;
    GameContainer container;
    private boolean isAlive = true;
    private float ySpeed, gravitation = 0.25f, xSpeed, constantXSpeed = 10f,
            maxSpeed = 12.5f, minSpeed = -10f,
            ownScaling = 1f;
    private float x, y, platformXOffset;
    private int jumpCounter, jumpAt = 1;
    private Image spriteStanding, spriteSitting, spriteJumpingRight, spriteJumpingLeft, spriteDead;
    private boolean useOwnScaling = false, onPlatform = false, sitting = false, alive = false;
    private Platform platform;
    private Image currentSprite;
    private Rectangle bounds;

    private boolean canExit = false;

    private static final int SITTINGOFFSET = 20;

    public void init(GameContainer container, StateBasedGame game, InGameState state) {
        container.getInput().addKeyListener(this);
        gameState = state;
        this.container = container;

        SpriteSheet sheet = SpritesheetLoader.getInstance().getSpriteSheet("misc", 64, 64);
        SpriteSheet haramstand = SpritesheetLoader.getInstance().getSpriteSheet("stand", 53, 73);
        SpriteSheet haramjumpright = SpritesheetLoader.getInstance().getSpriteSheet("jump", 55, 73);
        SpriteSheet haramjumpleft = SpritesheetLoader.getInstance().getSpriteSheet("jump2", 73, 73);
        SpriteSheet dead = SpritesheetLoader.getInstance().getSpriteSheet("dead", 75, 75);
        spriteStanding = haramstand.getSprite(0, 0).getSubImage(0, 0, 53, 73);
        spriteJumpingRight = haramjumpright.getSprite(0, 0).getSubImage(0, 0, 55, 73);
        spriteJumpingLeft = haramjumpleft.getSprite(0, 0).getSubImage(0, 0, 73, 73);
        spriteSitting = haramstand.getSprite(0, 0).getSubImage(0, 0, 53, 73);
        spriteDead = dead.getSprite(0, 0).getSubImage(0, 0, 75, 75);

        currentSprite = spriteStanding;
        bounds = new Rectangle(0, 0, spriteStanding.getWidth() * gameState.getTextureScaling(), spriteStanding.getHeight() * gameState.getTextureScaling());
    }

    public void update(GameContainer container, StateBasedGame game, int delta) {
        if (ySpeed <= 5 * maxSpeed / 6f && ySpeed > 0 && alive) {
            setCurrentSprite(spriteJumpingRight);
        }


        applySpriteCollision();

        if (!onPlatform) {
            y += ySpeed;
            applyGravity();
            if (alive) {
                if (x <= 3) {
                    x = 3;
                } else if (x >= container.getWidth() - 120) {
                    x = container.getWidth() - 120;
                }
                x += xSpeed;
               
                applyCollision();
                checkDeath();
            } else {
                if (y < gameState.getCameraHeight()) {
                    canExit = true;
                }
            }
        } else if (onPlatform && alive) {
            checkPlatformChanges();

            if (!sitting && !container.getInput().isKeyDown(Input.KEY_LCONTROL)) {

                if (jumpCounter >= jumpAt) {
                    jumpCounter = 0;
                    ySpeed = maxSpeed;
                    onPlatform = false;
                    setCurrentSprite(spriteStanding);
                    score++;
                }
            }
        }
    }

    private void applySpriteCollision() {
        for (Sprite s : gameState.getSprites()) {
            Rectangle srb = gameState.calcRenderRect(s.getBounds());
            Rectangle rpb = gameState.calcRenderRect(getBounds());
            if (srb.intersects(rpb) || rpb.contains(srb)) {
                s.onPlayerHit(this);
            }
        }
    }

    private void checkPlatformChanges() {
        if (platform != null) {
            if (!platform.applyPlayerCollision()) {
                onPlatform = false;
                platform = null;
            } else if (platform.movePlayerWithPlatform()) {
                float newXOffset = calculateXOffset(platform);
                if (newXOffset != platformXOffset) {
                    x += newXOffset - platformXOffset;
                }
            }
        }
    }

    private float calculateXOffset(Platform p) {
        return p.getHitBounds().getX() - getBounds().getX();
    }

    private void checkDeath() {
        if (y < gameState.getCameraHeight()) {
            alive = false;
            setCurrentSprite(spriteDead);
            ySpeed = 15f;
        }
    }

    private void applyGravity() {
        ySpeed -= gravitation;
        if (ySpeed < minSpeed) {
            ySpeed = minSpeed;
        }
    }

    private void applyCollision() {
        List<Platform> platforms = gameState.getRelevantPlatforms();
        Rectangle myBounds = gameState.calcRenderRect(getBounds());
        myBounds.setY(myBounds.getY() + myBounds.getHeight() * 0.9f);
        myBounds.setHeight(myBounds.getHeight() * 0.1f);

        for (Platform p : platforms) {
            if (p.applyPlayerCollision() && myBounds.intersects(p.getReCalculatedHitBounds(gameState)) && ySpeed < 0) {
                ySpeed = 0;
                onPlatform = true;
                y = p.getHitBounds().getY() + getBounds().getHeight();
                setCurrentSprite(sitting ? spriteSitting : spriteStanding);
                p.onHit(this);
                platform = p;
                platformXOffset = calculateXOffset(platform);
            }
        }
    }

    private void applySideSwitch() {
        if (x >= gameState.getGameScreenBoundings().getWidth() && xSpeed > 0) {
            x -= gameState.getGameScreenBoundings().getWidth();
            x -= this.getBounds().getWidth();
            x += 5;
        } else if (x + getBounds().getWidth() <= 0 && xSpeed < 0) {
            x = gameState.getGameScreenBoundings().getWidth();
            x -= 5;
        }
    }

    public void initNewGame(float x, float y) {
        this.x = x;
        this.y = y;
        alive = true;
        canExit = false;
    }

    public void setCurrentSprite(Image sprite) {

        currentSprite = sprite;

    }

    public Rectangle getBounds() {
        bounds.setX(x);
        bounds.setY(y);
        bounds.setWidth(spriteStanding.getWidth() * gameState.getTextureScaling());
        bounds.setHeight(spriteStanding.getHeight() * gameState.getTextureScaling());
        return bounds;
     
    }

    private float getScaling() {
        return useOwnScaling ? ownScaling : gameState.getTextureScaling();
    }

    private Rectangle drawBounds = new Rectangle(0, 0, 1, 1);

    public void render(GameContainer container, StateBasedGame game, Graphics g) {
        drawBounds = gameState.calcRenderRect(getBounds());
        float rx = drawBounds.getX();
        float ry = drawBounds.getY();
        rx -= (currentSprite.getWidth() * getScaling() - drawBounds.getWidth()) / 2;
        ry -= (currentSprite.getHeight() * getScaling() - drawBounds.getHeight()) / 2;


        currentSprite.draw(rx, ry, gameState.getTextureScaling());

   
    }

    @Override
    public void inputEnded() {
    }

    @Override
    public void inputStarted() {

    }

    @Override
    public boolean isAcceptingInput() {
        return gameState.isAcceptingInput() && isAlive;
    }

    @Override
    public void setInput(Input input) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyPressed(int key, char c) {
      if (Input.KEY_RIGHT == key) {
            xSpeed = constantXSpeed;
        } else if (Input.KEY_LEFT == key) {
            xSpeed = -constantXSpeed;
        } else if (Input.KEY_SPACE == key && onPlatform) {
            setCurrentSprite(spriteStanding);
            jumpCounter++;
            ySpeed = maxSpeed;
        } else if (onPlatform) {
            if (Input.KEY_RIGHT == key) {
                xSpeed = -constantXSpeed;
            }
            if (Input.KEY_LEFT == key) {
                xSpeed = constantXSpeed;
            }
        }
    }

    @Override
    public void keyReleased(int key, char c) {
      if ((Input.KEY_RIGHT == key && xSpeed == constantXSpeed)
                || (Input.KEY_LEFT == key && xSpeed == -constantXSpeed)) {
            xSpeed = 0;
            if (container.getInput().isKeyDown(Input.KEY_LEFT)) {
                xSpeed = -constantXSpeed;
            }
            if (container.getInput().isKeyDown(Input.KEY_RIGHT)) {
                xSpeed = constantXSpeed;
            }
        }
    }

    public boolean canExit() {
        return canExit;
    }

    public boolean isAlive() {
        return alive;
    }

    public float getGravitation() {
        return gravitation;
    }

    public void setGravitation(float gravitation) {
        this.gravitation = gravitation;
    }

    public float getConstantMovement() {
        return constantXSpeed;
    }

    public void setConstantMovement(float constantMovement) {
        this.constantXSpeed = constantMovement;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public float getMinSpeed() {
        return minSpeed;
    }

    public void setMinSpeed(float minSpeed) {
        this.minSpeed = minSpeed;
    }

    public float getOwnScaling() {
        return ownScaling;
    }

    public void setOwnScaling(float ownScaling) {
        this.ownScaling = ownScaling;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public boolean isUseOwnScaling() {
        return useOwnScaling;
    }

    public void setUseOwnScaling(boolean useOwnScaling) {
        this.useOwnScaling = useOwnScaling;
    }

    public boolean isOnPlatform() {
        return onPlatform;
    }

    public boolean isSitting() {
        return sitting;
    }

    public float getYSpeed() {
        return ySpeed;
    }

    public float getXSpeed() {
        return xSpeed;
    }

    public void setYSpeed(float ySpeed) {
        this.ySpeed = ySpeed;
    }

    public void setXSpeed(float xSpeed) {
        this.xSpeed = xSpeed;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

}
