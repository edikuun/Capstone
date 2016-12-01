/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameContent;

/**
 *
 * @author Jun
 */
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
import org.newdawn.slick.Music;
import org.newdawn.slick.Sound;
import java.util.*;

public class Lava {

    private Image[] lava;
    public Animation move;
    public Image l, l2;
    private Graphics g = new Graphics();
    private InGameState gameState;
    private GameContainer container;
    private float x;
    private float y;

    public Lava(GameContainer container, InGameState gameState) {
        this.container = container;
        this.gameState = gameState;

        SpriteSheet lava1 = SpritesheetLoader.getInstance().getSpriteSheet("lava1", 501, 668);
        SpriteSheet lava2 = SpritesheetLoader.getInstance().getSpriteSheet("lava2", 501, 668);
        l = lava1.getSprite(0, 0).getSubImage(0, 0, 501, 668);
        l2 = lava2.getSprite(0, 0).getSubImage(0, 0, 501, 668);
        lava = new Image[]{l, l2};
        move = new Animation(lava, new int[]{200, 200}, true);

        y = container.getHeight() - (l.getHeight() - 633);
        x = 0;
    }

    public void reset() {
        SpriteSheet lava1 = SpritesheetLoader.getInstance().getSpriteSheet("lava1", 501, 668);
        SpriteSheet lava2 = SpritesheetLoader.getInstance().getSpriteSheet("lava2", 501, 668);
        l = lava1.getSprite(0, 0).getSubImage(0, 0, 501, 668);
        l2 = lava2.getSprite(0, 0).getSubImage(0, 0, 501, 668);
        lava = new Image[]{l, l2};
        move = new Animation(lava, new int[]{200, 200}, true);

        y = container.getHeight() - (l.getHeight() - 633);
        x = 0;
    }

    public void draw2() {
        g.setColor(Color.yellow);
        g.draw(getBounds());
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, l.getWidth() * gameState.getTextureScaling(), 30);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

}
