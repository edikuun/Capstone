package Base;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.particles.ConfigurableEmitter;
import org.newdawn.slick.particles.ParticleIO;
import org.newdawn.slick.particles.ParticleSystem;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.Image;

import GameContent.InGameState;
import ScoreData.HighscoreState;
import ImageLoaders.SpritesheetLoader;
import ImageLoaders.ImageLoader;

public class MainMenu extends BasicGameState {

    public final static int ID = 0;
    public final static String CMD_EXIT = "command_exit_full",
            CMD_NEW_GAME = "command_new_game",
            CMD_HIGHSCORE = "command_highscore";

    private StateBasedGame game;
    private GameContainer container;

    private List<MainMenuButton> buttons;

    private SpriteSheet sheet;
    private Image bg, title;
    private int frameWidth = 59, frameHeight = 19;

    private ParticleSystem system;
    //private OneTimeEmitter emitter;

    @Override
    public void init(GameContainer container, StateBasedGame game)
            throws SlickException {
        sheet = SpritesheetLoader.getInstance().getSpriteSheet("move", frameWidth, frameHeight);
        bg = ImageLoader.getInstance().getImage("spritesheet_bg");
        title = ImageLoader.getInstance().getImage("title");

        this.game = game;
        this.container = container;
        this.buttons = new LinkedList<MainMenuButton>();

        system = new ParticleSystem(sheet.getSprite(0, 0));
        system.setUsePoints(true);

       

        buttons.add(new MainMenuButton(this, "START", CMD_NEW_GAME, container.getWidth() / 2, container.getHeight() * 0.1f, container));
        buttons.add(new MainMenuButton(this, "HIGH SCORE", CMD_HIGHSCORE, buttons.get(0), 0, 5, container));
        buttons.add(new MainMenuButton(this, "QUIT", CMD_EXIT, buttons.get(1), 0, 5, container));

    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g)
            throws SlickException {
        system.render();
        g.drawImage(bg, 0, 0);
        g.drawImage(title, 0, 0);
        for (MainMenuButton btn : buttons) {
            btn.render(container, game, g);
        }
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta)
            throws SlickException {
        system.update(delta);
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public boolean isAcceptingInput() {
        return game.getCurrentStateID() == ID;
    }

    public void execute(String command) {

        if (command.equals(CMD_EXIT)) {
            container.exit();
        } else if (command.equals(CMD_NEW_GAME)) {
            ((InGameState) ((MainGame) game).getIngameState()).initNewGame();
            game.enterState(((MainGame) game).getIngameState().getID(), new FadeOutTransition(), new FadeInTransition());
        } else if (command.equals(CMD_HIGHSCORE)) {
            game.enterState(HighscoreState.ID);
        }
    }

    public ParticleSystem getParticleSystem() {
        return system;
    }
}
