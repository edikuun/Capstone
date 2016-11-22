package ScoreData;


import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.fills.GradientFill;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import Base.MainMenu;

public class HighscoreState extends BasicGameState{

	public static final int ID=4;
	
	private Highscore highscore;
	private int highlight=-1;
	private Color normalColor=Color.white,highlightColor=Color.red;
	private StateBasedGame game;
	private Font font, fontHeader;
	private String headerText="Highscore";
	private Image bufferImg;
	private Graphics imgGraphics;
	private GradientFill overlay;
	private Rectangle fillingRect=new Rectangle(0, 0, 0, 0);
	private float m, xpms=0.1f;
	private float ypms=0.3f, startFac=-1f, endFac=1f;
	
	private GameContainer con;
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		this.game = game;
		font = new TrueTypeFont(new java.awt.Font("verdana", java.awt.Font.BOLD, 20), true);
		fontHeader = new TrueTypeFont(new java.awt.Font("verdana", java.awt.Font.BOLD, 45), true);
		
		highscore = Highscore.load(Highscore.standardFileName);
		if(highscore==null){
			highscore = new Highscore();
		}
		
		bufferImg = new Image(container.getWidth(), container.getHeight());
		imgGraphics = bufferImg.getGraphics();
		
		overlay = new GradientFill(0,1, Color.red, 1, 0, Color.orange.darker(0.25f));
		overlay.setLocal(false);
		
		calcFillingRect(container);
		con = container;
		
		
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		Graphics.setCurrent(imgGraphics);
		imgGraphics.clearAlphaMap();
		imgGraphics.setDrawMode(Graphics.MODE_NORMAL);
		
		int x=container.getWidth()/2;
		int y=(container.getHeight() - (highscore.getNumberOfEntries() * font.getLineHeight()))/2;
		
		List<HighscoreEntry> entries = highscore.getEntries();
		for(int i = 0; i < entries.size(); i++){
			String s = (i+1)+". " + entries.get(i).toString();
			if(i==highlight){
				s = "> " + s + " <";
			}
			font.drawString(x-font.getWidth(s)/2,y+i*font.getLineHeight(), s, /*i==highlight?highlightColor:*/normalColor);
		}
		
		int hx = (container.getWidth()-fontHeader.getWidth(headerText))/2;
		fontHeader.drawString(hx, y /2/*- fontHeader.getLineHeight() - 15*/, headerText);
		
		imgGraphics.setDrawMode(Graphics.MODE_COLOR_MULTIPLY);
		imgGraphics.fill(fillingRect, overlay);
		
		imgGraphics.flush();
		
		Graphics.setCurrent(g);
		bufferImg.draw();
		
		//DEBUG: Show gradient start and end point
//		g.setColor(Color.green);
//		g.fillRect(overlay.getStart().x -5, overlay.getStart().y -5, 10, 10);
//		g.setColor(Color.blue);
//		g.fillRect(overlay.getEnd().x -5, overlay.getEnd().y -5, 10, 10);
	}
	
	private void calcFillingRect(GameContainer container){
		int x=container.getWidth()/2;
		int y=(container.getHeight() - (highscore.getNumberOfEntries() * font.getLineHeight()))/2;
		
		int minY = y/2; int maxY = y+highscore.getNumberOfEntries()*font.getLineHeight();
		int minX = 0; int maxX = 0;
		minX = (container.getWidth()-fontHeader.getWidth(headerText))/2; maxX = minX + fontHeader.getWidth(headerText);
		List<HighscoreEntry> entries = highscore.getEntries();
		for(int i = 0; i < entries.size(); i++){
			String s = (i+1)+". " + entries.get(i).toString();
			if(i==highlight){
				s = "> " + s + " <";
			}
			int ex = x-font.getWidth(s)/2;
			if(ex < minX){
				minX = ex;
				maxX = minX + font.getWidth(s);
			}
		}
		
		fillingRect.setBounds(minX, minY, maxX - minX, maxY - minY);
		
		overlay.setStart(minX, maxY);
		overlay.setEnd(maxX, minY);
		
		m = fillingRect.getHeight() / fillingRect.getWidth();
		
		overlay.setEnd(maxX, minY);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		Vector2f start = overlay.getStart();
		Vector2f end = overlay.getEnd();
		
//		float dx = delta * xpms;
//		float dy = m * dx;
		
		float dy = delta * ypms;
		
////		
////		start.x += dx;
////		start.y -= dy;
////		
////		if(start.x > fillingRect.getMaxX()){
////			start.x -= fillingRect.getWidth();
////			start.y += fillingRect.getHeight();
////		}
////		
//		end.x += dx;
//		end.y -= dy;
//		
//		if(end.x > fillingRect.getMaxX()){
////			end.x -= fillingRect.getWidth();
////			end.y += fillingRect.getHeight();
//			xpms *= -1;
//		}
//		else if(end.x < fillingRect.getMinX()){
//			xpms *= -1;
//		}
		
		start.y += dy * startFac;
		end.y += dy*endFac;
		if(start.y < fillingRect.getMinY()){
			startFac = 1;
		} else if(start.y > fillingRect.getMaxY()){
			startFac = -1;
		}
		if(end.y < fillingRect.getMinY()){
			endFac = 1;
		} else if(end.y > fillingRect.getMaxY()){
			endFac = -1;
		}
		
		overlay.setStart(start);
		overlay.setEnd(end);
	}

	@Override
	public int getID() {
		return ID;
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		
	}
	
	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		highlight = -1;
	}
	
	@Override
	public void keyPressed(int key, char c) {
		if(key == Input.KEY_ESCAPE){
			game.enterState(MainMenu.ID);
		}
	}
	
	public int add(String name, int score){
		int r = highscore.add(name, score);
		calcFillingRect(con);
		return r;
	}
	
	public void highlight(int i){
		highlight = i;
		calcFillingRect(con);
	}
}
