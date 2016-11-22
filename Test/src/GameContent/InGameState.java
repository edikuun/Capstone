package GameContent;

import java.util.LinkedList;
import java.util.List;
import java.io.IOException;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.particles.ParticleSystem;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.util.ResourceLoader;

import Base.MainGame;
import Debug.DebugInfo;
import ObjectGenerator.Generator;
import GamePause.PauseGameState;
import GameOver.GameOverState_Counter;
import ImageLoaders.ImageLoader;
import ImageLoaders.SpritesheetLoader;
import Transitions.FixedAlphaFadingTransition;

public class InGameState extends BasicGameState {

    public static final int ID = 1;

    private Rectangle gameScreenBoundings;
    private float textureScaling = 2f, cameraHeight = 0f;
    private LinkedList<Platform> platforms = new LinkedList<Platform>();
    private LinkedList<Object[]> animations = new LinkedList<Object[]>();
    private SpriteSheet sheet;
    private Player player = new Player();
    private int score;
    private float scrollSpeed, constantScrollSpeed = 7.5f;
    private int applyCounter, applyMax = 15;
    private Font font;
    private float scoreFactor = 0.003f;
    private Generator generator;
    private List<Platform> platformsToRemove = new LinkedList<Platform>();
    private LinkedList<Sprite> sprites = new LinkedList<Sprite>();
    private MainGame game;
    private Image bg;
    private Audio music;

    private LinkedList<Integer[]> placesInDebugToClear = new LinkedList<Integer[]>();

    private ParticleSystem system;

    @Override
    public void init(GameContainer container, StateBasedGame game)
            throws SlickException {
        gameScreenBoundings = new Rectangle(0, 0, container.getWidth(), container.getHeight());
        sheet = SpritesheetLoader.getInstance().getSpriteSheet("normal", 62, 15);
        font = new TrueTypeFont(new java.awt.Font("consola", java.awt.Font.BOLD, 20), true);
        player.init(container, game, this);
        generator = new Generator(this);
        this.game = (MainGame) game;
        Image image = new Image((int) textureScaling, (int) textureScaling);
        Graphics g = image.getGraphics();
        Graphics.setCurrent(g);
        bg = ImageLoader.getInstance().getImage("spritesheet_bg");
        g.drawImage(bg, 0, 0);
        /*try {
            music = AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("Images\\15_-_Classic_-_MKTO.ogg"));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        g.flush();
        system = new ParticleSystem(image);
        system.setRemoveCompletedEmitters(true);
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g)
            throws SlickException {
        if (player.isAlive()) {
            g.drawImage(bg, 0, 0);
        }
        for (int i = platforms.size() - 1; i >= 0; i--) {
            if (gameScreenBoundings.getHeight() - platforms.get(i).getHitBounds().getY() + cameraHeight > gameScreenBoundings.getHeight() + 2) {
                break;
            }
            platforms.get(i).draw(cameraHeight, this, g);
        }
        for (Sprite s : sprites) {
            s.draw(g);
        }
        for (Object[] obj : animations) {
            Animation anim = (Animation) obj[0];
            float x = calcRenderX((float) obj[1]);
            float y = calcRenderY((float) obj[2]);
            anim.draw(x, y, anim.getWidth() * textureScaling, anim.getHeight() * textureScaling);
        }
        player.render(container, game, g);
        if (player.isAlive()) {
            String pointString = "Score: " + player.getScore();
            font.drawString(gameScreenBoundings.getMaxX() - font.getWidth(pointString), 0, pointString);
        }
        system.render();
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta)
            throws SlickException {
        system.update(delta);
        //music.playAsSoundEffect(1.0f, 1.0f, false);
        //SoundStore.get().poll(0);


        for (Platform p : platformsToRemove) {
            platforms.remove(p);
        }
        platformsToRemove.clear();

        List<Object[]> toRemove = new LinkedList<Object[]>();
        for (Object[] obj : animations) {
            Animation anim = (Animation) obj[0];
            anim.update(delta);
            if (anim.isStopped()) {
                toRemove.add(obj);
            }
        }
        for (Object[] obj : toRemove) {
            animations.remove(obj);
        }

        applyCounter += delta;
        if (applyCounter > applyMax) {
            applyCounter -= applyMax;

            for (Sprite s : sprites) {
                s.update();
            }
            for (int i = 0; i < sprites.size(); i++) {
                if (sprites.get(i).canBeRemoved()) {
                    sprites.remove(i);
                    i--;
                }
            }

            generator.update();
            for (Platform p : platforms) {
                p.update();
            }

            if (player.canExit()) {
                game.enterState(GameOverState_Counter.ID, new FixedAlphaFadingTransition(GameOverState_Counter.COLOR_OVERLAY, 1800), null);
                return;
            }
            if (player.isAlive()) {
                Rectangle pBounds = player.getBounds();
                Rectangle renderBounds = this.calcRenderRect(pBounds);
                if (renderBounds.getY() < gameScreenBoundings.getHeight() * 0.2f) {
                    scrollSpeed = constantScrollSpeed;
                } else {
                    scrollSpeed = 0;
                }
                cameraHeight += scrollSpeed;
                for (Platform p : platforms) {
                    p.adjustY(scrollSpeed);
                }
            }
            player.update(container, game, delta);
        }
    }

    public void initNewGame() {
        cameraHeight = 0f;
        platforms.clear();
        player.setScore(0);

        Image base = sheet.getSprite(0, 0).getSubImage(0, 0, 62, 15);
        Image flipped = base.getFlippedCopy(true, false);
        boolean flip = false;
        for (int i = 0;; i++) {
            platforms.addLast(new Platform(new Rectangle(i * 64 * textureScaling, 12 * textureScaling, 64 * textureScaling, 12 * textureScaling),
                    flip ? flipped : base, this));
           system.addEmitter(platforms.getLast()); // TO BE REMOVED
            if ((i + 1) * 64 * textureScaling > gameScreenBoundings.getWidth()) {
                break;
            }
            flip = !flip;
        }
        player.initNewGame(gameScreenBoundings.getCenterX(), gameScreenBoundings.getCenterY());
        generator.update(); //Pre generate a few platforms
    }

    @Override
    public int getID() {
        return ID;
    }

    public Rectangle getGameScreenBoundings() {
        return gameScreenBoundings;
    }

    public float getTextureScaling() {
        return textureScaling;
    }

    public List<Platform> getRelevantPlatforms() {
        LinkedList<Platform> ret = new LinkedList<Platform>();
        for (int i = platforms.size() - 1; i >= 0; i--) {
            if (gameScreenBoundings.getHeight() - platforms.get(i).getHitBounds().getY() + cameraHeight > gameScreenBoundings.getHeight() + 2) {
                break;
            }
            ret.add(platforms.get(i));
        }
        return ret;
    }

    public float getCameraHeight() {
        return cameraHeight;
    }

    public Rectangle calcRenderRect(Rectangle modelRect) {
        return new Rectangle(calcRenderX(modelRect.getX()), calcRenderY(modelRect.getY()),
                modelRect.getWidth(), modelRect.getHeight());
    }

    public float calcRenderY(float modelY) {
        return gameScreenBoundings.getY() + getGameScreenBoundings().getHeight() - modelY + getCameraHeight();
    }

    public float calcRenderX(float modelX) {
        return gameScreenBoundings.getX() + modelX;
    }

    public LinkedList<Platform> getAllPlatforms() {
        return platforms;
    }

    public Player getPlayer() {
        return player;
    }

    public float getScoreFactor() {
        return scoreFactor;
    }

    public void setScoreFactor(float scoreFactor) {
        this.scoreFactor = scoreFactor;
    }

    public void addPlatform(Platform p) {
        platforms.add(p);
        system.addEmitter(p);
    }

    public void removePlatform(Platform p) {
        platformsToRemove.add(p);
    }

    public void playAnimation(Animation animation, float x, float y) {
        animations.add(new Object[]{animation, x, y});
    }

    public int getAmplifiedScore() {
        return score;
    }

    @Override
    public void keyPressed(int key, char c) {
        if (key == PauseGameState.KEY_TO_PAUSEUNPAUSE) {
            game.enterState(PauseGameState.ID, PauseGameState.createLeaveOtherStateTransition(), null);
        }
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException {
        placesInDebugToClear.clear();
        DebugInfo info = this.game.getDebugInfo();

        try {
            Integer[] coordsForPlatCount = info.getFirstFree();
            info.set(coordsForPlatCount[0], coordsForPlatCount[1], new Object[]{"Platform: ", new Object[]{InGameState.class.getMethod("getPlatformCount"), this}});

            Integer[] coordsForPartCount = info.getFirstFree();
            info.set(coordsForPartCount[0], coordsForPartCount[1], new Object[]{"Particle: ", new Object[]{ParticleSystem.class.getMethod("getParticleCount"), system}});

            Integer[] coordsForEmitterCount = info.getFirstFree();
            info.set(coordsForEmitterCount[0], coordsForEmitterCount[1],
                    new Object[]{"Emitter: ", new Object[]{ParticleSystem.class.getMethod("getEmitterCount"), system}});

            placesInDebugToClear.add(coordsForPlatCount);
            placesInDebugToClear.add(coordsForPartCount);
            placesInDebugToClear.add(coordsForEmitterCount);
        } catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void leave(GameContainer container, StateBasedGame game) throws SlickException {
        DebugInfo info = this.game.getDebugInfo();
        for (Integer[] coords : placesInDebugToClear) {
            info.set(coords[0], coords[1], null);
        }
    }

    public int getPlatformCount() {
        return platforms.size();
    }

    public ParticleSystem getParticleSystem() {
        return system;
    }

    public void addSprite(Sprite s) {
        sprites.add(s);
    }

    public LinkedList<Sprite> getSprites() {
        return sprites;
    }
}
