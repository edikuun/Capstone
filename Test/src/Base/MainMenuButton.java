package Base;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.MouseListener;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.StateBasedGame;

import ImageLoaders.SpritesheetLoader;


public class MainMenuButton implements MouseListener{
	private MainMenu menu;
	private String text, command;
	private float xOffset, yOffset; //yOffset is at the top border, x Offset is in the middle of the button
	private Rectangle bounds;
	private Font font;
	private Color colorNotHovered = Color.white, colorHovered = Color.red;
	private MainMenuButton alignButton;
	private boolean hovered = false;
	
	
	
	public MainMenuButton(MainMenu menu, String text, String command, float xOffset, float yOffset, GameContainer container){
		this.menu = menu;
		this.text = text;
		this.command = command;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		
		font = new TrueTypeFont(new java.awt.Font("consola", java.awt.Font.BOLD, 20), true);
		this.createBoundings();
		
		container.getInput().addMouseListener(this);	
	}
	
	
	public MainMenuButton(MainMenu menu, String text, String command,
			MainMenuButton alignButton, float xOffset, float yOffset, GameContainer container){
		
		this(menu,text,command,xOffset,yOffset,container);
		this.alignButton = alignButton;
		createBoundings();
	}
	
	

	public MainMenuButton(MainMenu menu, String text, String command,
			MainMenuButton alignButton, float xOffset, float yOffset, Font font, GameContainer container){
		this(menu,text,command,alignButton,xOffset,yOffset,container);
		this.font = font;
		createBoundings();
	}

	//Generate this buttons boundings based on text size; offset and alignment
	private void createBoundings(){
		float midPosX = xOffset;
		float posY = yOffset;
		
		if(alignButton != null){
			midPosX+=alignButton.getBounds().getCenterX();
			posY += alignButton.getBounds().getMaxY();
		}
		
		int textWidth = font.getWidth(text);
		int textHeight = font.getLineHeight();
		
		if(bounds == null) bounds = new Rectangle(0,0, 0, 0);
		bounds.setWidth(textWidth);
		bounds.setHeight(textHeight);
		bounds.setCenterX(midPosX);
		bounds.setY(posY);
	}
	
	public void render(GameContainer container, StateBasedGame game, Graphics g){
//		if(hovered)
//			hoverImage.draw(bounds.getX() - 2, bounds.getY() -2 , bounds.getWidth() + 4, bounds.getHeight() + 4); TO BE REMOVED
		g.setFont(font);
		g.setColor(hovered?colorHovered:colorNotHovered);
		
		g.drawString(text, bounds.getX(), bounds.getY());
	}
	
	@Override
	public void inputEnded() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inputStarted() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isAcceptingInput() {
		// TODO Auto-generated method stub
		return menu.isAcceptingInput();
	}

	@Override
	public void setInput(Input input) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(int button, int x, int y, int clickCount) {
		if(button == Input.MOUSE_LEFT_BUTTON && bounds.contains(x, y)){
			menu.execute(command);
		}		
	}

	@Override
	public void mouseDragged(int oldx, int oldy, int newx, int newy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		
	}

	@Override
	public void mousePressed(int button, int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(int button, int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseWheelMoved(int change) {
		// TODO Auto-generated method stub
		
	}

	
	//Getters and Setters
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		createBoundings();
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public float getxOffset() {
		return xOffset;
	}

	public void setxOffset(float xOffset) {
		this.xOffset = xOffset;
		createBoundings();
	}

	public float getyOffset() {
		return yOffset;
	}

	public void setyOffset(float yOffset) {
		this.yOffset = yOffset;
		createBoundings();
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
		createBoundings();
	}

	public Color getColorNotHovered() {
		return colorNotHovered;
	}

	public void setColorNotHovered(Color colorNotHovered) {
		this.colorNotHovered = colorNotHovered;
	}

	public Color getColorHovered() {
		return colorHovered;
	}

	public void setColorHovered(Color colorHovered) {
		this.colorHovered = colorHovered;
	}

	public MainMenuButton getAlignButton() {
		return alignButton;
	}

	public void setAlignButton(MainMenuButton alignButton) {
		this.alignButton = alignButton;
		createBoundings();
	}

	public Rectangle getBounds() {
		return bounds;
	}	
}
