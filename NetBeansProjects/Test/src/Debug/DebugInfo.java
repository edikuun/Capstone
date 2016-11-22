package Debug;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

public class DebugInfo {
	private int gapV, gapH, width, height;
	private Font font;
	private Object[][][] debugRenderings;
	
	public DebugInfo(int width, int height, int gapV, int gapH, StateBasedGame game, GameContainer con){
		this.gapH = gapH;
		this.gapV = gapV;
		this.width = width;
		this.height = height;
		
		font = con.getDefaultFont();
		
		debugRenderings = new Object[width][height][];
	}
	
	public void render(Graphics g){
		int lineHeight = font.getLineHeight() + gapV;
		int currentColumnMaxX=0;
		
		for(int x = 0; x < width; x++){
			int currentRenderX = currentColumnMaxX+gapH;
			for(int y = 0; y < height; y++){
				if(debugRenderings[x][y] != null){
					int currentRenderY = lineHeight * y;
					String strToRender = createString(debugRenderings[x][y]);
					int w = font.getWidth(strToRender);
					if(currentRenderX + w > currentColumnMaxX) currentColumnMaxX = currentRenderX + w;
					font.drawString(currentRenderX, currentRenderY, strToRender);
				}
			}
		}
	}
	
	private String createString(Object[] params){
		String res = "";
		
		for(Object o : params){
			if(o instanceof Object[]){
				Object[] cast = (Object[])o;
				if(cast.length == 2){
					if(cast[0] instanceof Method){
						Method m = (Method) cast[0];
						try {
							res += m.invoke(cast[1]);
						} catch (IllegalAccessException
								| IllegalArgumentException
								| InvocationTargetException e) {
							e.printStackTrace();
						}
					}
				}
				else if(cast.length == 3){
					if(cast[0] instanceof Method && cast[2] instanceof Object[]){
						Method m = (Method) cast[0];
						try {
							res += m.invoke(cast[1], (Object[]) cast[2]);
						} catch (IllegalAccessException
								| IllegalArgumentException
								| InvocationTargetException e) {
							e.printStackTrace();
						}
					}
				}
			}
			else
				res += o.toString();
		}
		
		return res;
	}
	
	public Integer[] getFirstFree(){
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				if(debugRenderings[x][y] == null) return new Integer[]{x,y};
			}
		}
		return new Integer[]{-1,-1};
	}
	
	public void set(int x, int y, Object[] params){
		debugRenderings[x][y] = params;
	}
}
