/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameContent;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;
import ImageLoaders.SpritesheetLoader;
import java.util.*;

/**
 *
 * @author Jun
 */
public class FallingObject {

    private Image banana;
    private InGameState gamestate;
    private GameContainer container;
    private Rectangle gameScreenBoundings;
    public float y = 0;
    private float x;
    private Random rand = new Random();
    private Rectangle bounds;
    private Graphics g = new Graphics();

    /* public void init(GameContainer container, StateBasedGame game, InGameState state){
        this.container = container;
        gamestate = state;
        SpriteSheet b = SpritesheetLoader.getInstance().getSpriteSheet("falling", 30, 35);
        banana = b.getSprite(0, 0).getSubImage(0, 0, 30, 35);
        bounds = new Rectangle(0, 0,banana.getWidth() * gamestate.getTextureScaling(), banana.getHeight() * gamestate.getTextureScaling());
        x = rand.nextInt(container.getWidth() + 1);
        y = 0;
    }*/
    public FallingObject(GameContainer container, InGameState gameState) {
        this.container = container;
        this.gamestate = gameState;
        SpriteSheet b = SpritesheetLoader.getInstance().getSpriteSheet("falling", 20, 25);
        banana = b.getSprite(0, 0).getSubImage(0, 0, 20, 25);
        //bounds = new Rectangle(0, 0,banana.getWidth() * gamestate.getTextureScaling(), banana.getHeight() * gamestate.getTextureScaling());
        x = rand.nextInt(container.getWidth() + 1);
        y = 0;
    }

    public void draw2() {
        g.setColor(Color.yellow);
        g.draw(getBounds());
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, banana.getWidth() * gamestate.getTextureScaling(), banana.getHeight() * gamestate.getTextureScaling());

    }

    public void draw(float x, float y) {
        banana.draw(x, y, gamestate.getTextureScaling());
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
