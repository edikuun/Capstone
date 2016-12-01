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
import org.newdawn.slick.Sound;

public class Player implements KeyListener {

    //Max Jump Height: 0.5*(maxSpeed)�/gravitation
    private int score = 0;
    private InGameState gameState;
    GameContainer container;
    public int hp = 3;
    private boolean isAlive = true;
    private float ySpeed, gravitation = 0.25f, xSpeed, constantXSpeed = 10f,
            maxSpeed = 12.5f, minSpeed = -10f,
            ownScaling = 1f;
    private float x, y, platformXOffset;
    private int jumpCounter, jumpAt = 1;
    private Image spriteRHurt, spriteLHurt, spriteRightB, spriteLeftB, spriteStanding, spritedead2, spriteSitting, spriteJumpingRight, spriteJumpingLeft, spriteDead, spriteStandLeft;
    private boolean useOwnScaling = false, onPlatform = false, sitting = false, alive = false;
    private Platform platform;
    private Image currentSprite;
    private Rectangle bounds;
    private Graphics g = new Graphics();
    private Sound jumpSound;

    private boolean canExit = false;

    private static final int SITTINGOFFSET = 20;

    private boolean pressedLeft = false;
    private boolean pressedRight = false;
    private boolean pressedJump = false;
    private boolean faceLeft = false;
    private boolean faceRight = true;
    private boolean isOnAir = false;
    private boolean isFalling = false;
    private Lava lava;
    private boolean check = false;

    public void init(GameContainer container, StateBasedGame game, InGameState state, Lava lava) throws SlickException {
        container.getInput().addKeyListener(this);
        gameState = state;
        this.container = container;
        this.lava = lava;

        SpriteSheet sheet = SpritesheetLoader.getInstance().getSpriteSheet("misc", 64, 64);
        SpriteSheet haramstand = SpritesheetLoader.getInstance().getSpriteSheet("stand", 53, 73);
        SpriteSheet haramjumpright = SpritesheetLoader.getInstance().getSpriteSheet("haramboyJumpRight", 64, 64);
        SpriteSheet haramjumpleft = SpritesheetLoader.getInstance().getSpriteSheet("haramboyJumpLeft", 64, 64);
        SpriteSheet dead = SpritesheetLoader.getInstance().getSpriteSheet("deadboi", 37, 43);
        SpriteSheet dead2 = SpritesheetLoader.getInstance().getSpriteSheet("deadboi2", 37, 43);
        SpriteSheet leftbounds = SpritesheetLoader.getInstance().getSpriteSheet("leftJumpBounds", 26, 50);
        SpriteSheet rightbounds = SpritesheetLoader.getInstance().getSpriteSheet("rightJumpBounds", 26, 50);
        SpriteSheet rightHurt = SpritesheetLoader.getInstance().getSpriteSheet("rightHurt", 28, 45);
        SpriteSheet leftHurt = SpritesheetLoader.getInstance().getSpriteSheet("leftHurt", 28, 45);

        spriteStanding = haramjumpright.getSprite(0, 0).getSubImage(0, 0, 60, 50);
        spriteStandLeft = haramjumpleft.getSprite(0, 0).getSubImage(0, 0, 60, 50);
        spriteJumpingRight = haramjumpright.getSprite(1, 0).getSubImage(0, 0, 64, 50);
        spriteJumpingLeft = haramjumpleft.getSprite(1, 0).getSubImage(0, 0, 64, 50);
        spriteSitting = haramstand.getSprite(0, 0).getSubImage(0, 0, 53, 73);
        spriteLeftB = leftbounds.getSprite(0, 0).getSubImage(0, 0, 26, 50);
        spriteRightB = rightbounds.getSprite(0, 0).getSubImage(0, 0, 26, 50);
        spriteDead = dead.getSprite(0, 0).getSubImage(0, 0, 37, 43);
        spriteRHurt = rightHurt.getSprite(0, 0).getSubImage(0, 0, 28, 45);
        spriteLHurt = leftHurt.getSprite(0, 0).getSubImage(0, 0, 28, 45);
        spritedead2 = dead2.getSprite(0, 0).getSubImage(0, 0, 37, 43);

        //jumpSound = new Sound ("Images\\JUMP.ogg");
        currentSprite = spriteStanding;
        bounds = new Rectangle(0, 0, spriteLeftB.getWidth() * gameState.getTextureScaling(), spriteStanding.getHeight() * gameState.getTextureScaling());
    }

    public void update(GameContainer container, StateBasedGame game, int delta) {
        if (ySpeed <= 5 * maxSpeed / 6f && ySpeed > 0 && alive) {
            if (pressedLeft && isAlive()) {
                setCurrentSprite(spriteJumpingLeft);
                pressedLeft = false;
                faceRight = false;
                faceLeft = true;
            }
            if (pressedRight && isAlive()) {
                setCurrentSprite(spriteJumpingRight);
                pressedRight = false;
                faceLeft = false;
                faceRight = true;
            }
        }

        applySpriteCollision();

        if (!onPlatform) {
            y += ySpeed;
            applyGravity();
            if (pressedLeft && isAlive()) {
                setCurrentSprite(spriteJumpingLeft);
                pressedLeft = false;
                faceRight = false;
                faceLeft = true;
            }
            if (pressedRight && isAlive()) {
                setCurrentSprite(spriteJumpingRight);
                pressedRight = false;
                faceLeft = false;
                faceRight = true;
            }
            if (alive) {
                if (x <= 0) {
                    x = 0;
                } else if (x >= container.getWidth() - getBounds().getWidth()) {
                    x = container.getWidth() - getBounds().getWidth();
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
            checkDeath();
            if (!sitting && !container.getInput().isKeyDown(Input.KEY_LCONTROL)) {

                if (jumpCounter >= jumpAt) {
                    jumpCounter = 0;
                    ySpeed = maxSpeed;
                    onPlatform = false;
                    isFalling = false;
                    if (pressedLeft) {
                        setCurrentSprite(spriteJumpingLeft);
                        pressedLeft = false;
                    }
                    if (pressedRight) {
                        setCurrentSprite(spriteJumpingRight);
                        pressedRight = false;
                    }
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
                isFalling = true;
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

    public void checkDeath() {
        Rectangle pBounds = gameState.calcRenderRect(getBounds());

        if (pBounds.intersects(lava.getBounds()) && check) {
            onPlatform = false;
            check = false;
            hp--;
            if (faceRight) {
                setCurrentSprite(spriteRHurt);
            } else if (faceLeft) {
                setCurrentSprite(spriteLHurt);
            }
            ySpeed = 15f;

        } else if (hp == 0) {
            onPlatform = false;
            alive = false;
            if (faceRight) {
                setCurrentSprite(spriteDead);
            } else if (faceLeft) {
                setCurrentSprite(spritedead2);
            }
            ySpeed = 10f;
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
        myBounds.setX(myBounds.getX());

        for (Platform p : platforms) {
            if (p.applyPlayerCollision() && myBounds.intersects(p.getReCalculatedHitBounds(gameState)) && ySpeed < 0) {
                ySpeed = 0;
                onPlatform = true;
                y = p.getHitBounds().getY() + getBounds().getHeight();
                if (faceRight) {
                    setCurrentSprite(sitting ? spriteSitting : spriteStanding);
                } else if (faceLeft) {
                    setCurrentSprite(sitting ? spriteSitting : spriteStandLeft);
                }
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
        hp = 3;
    }

    public void setCurrentSprite(Image sprite) {

        currentSprite = sprite;

    }

    public Image getSpriteNew() {
        return spriteStanding;
    }

    public Rectangle getBounds() {
        bounds.setX(x);
        bounds.setY(y);
        bounds.setWidth((spriteRightB.getWidth()) * gameState.getTextureScaling());
        bounds.setHeight((currentSprite.getHeight() - 6) * gameState.getTextureScaling());
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
        //g.draw(drawBounds);

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

    public void setCheck(boolean check) {
        this.check = check;
    }

    @Override
    public void setInput(Input input) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyPressed(int key, char c) {
        if (Input.KEY_RIGHT == key && isAlive()) {
            pressedRight = true;
            xSpeed = constantXSpeed;
        } else if (Input.KEY_LEFT == key && isAlive()) {
            pressedLeft = true;
            xSpeed = -constantXSpeed;
        } else if (Input.KEY_SPACE == key && onPlatform && isAlive()) {
            isFalling = false;
            //jumpSound.play();
            if (faceRight) {
                setCurrentSprite(spriteJumpingRight);
            } else if (faceLeft) {
                setCurrentSprite(spriteJumpingLeft);
            }
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

    public void setOnPlatform(boolean onPlatform) {
        this.onPlatform = onPlatform;
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

    public int getHp() {
        return this.hp;
    }

    public void draw() {
        g.setColor(Color.yellow);
        g.draw(getBounds());
    }

    public boolean getJump() {
        return this.isOnAir;
    }

    public int getJumpCounter() {
        return jumpCounter;
    }

    public boolean getFalling() {
        return this.isFalling;

    }

    public boolean isFaceRight() {
        return faceRight;
    }

    public boolean isFaceLeft() {
        return faceLeft;
    }
}
