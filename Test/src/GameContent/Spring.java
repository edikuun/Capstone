package GameContent;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.StateBasedGame;

public class Spring extends Sprite {

    private boolean removeFlag;

    public Spring(Image img, Rectangle bounds, InGameState state,
            StateBasedGame stateGame) {
        super(img, bounds, state, stateGame);
    }

    @Override
    public void update() {
    }

    @Override
    public void onPlayerHit(Player p) {
        if (p.getYSpeed() < 0) {
            p.setYSpeed(p.getMaxSpeed() * 2);
            removeFlag = true;
        }
    }

    @Override
    public boolean canBeRemoved() {
        return super.canBeRemoved() || removeFlag;
    }
}
