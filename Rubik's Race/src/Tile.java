

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Tile implements Cloneable{
	public static final int WIDTH = 50;
	public static final int HEIGHT = 50;
	public static final int SLIDE_SPEED = 20;
	public static final int ARC_WIDTH = 7;
	public static final int ARC_HEIGHT = 7;

	private RubiksColors value;
	private BufferedImage tileImage;
	private Color background;
	private int x;
	private int y;
	private Point slideTo;
	
	private boolean beginningAnimation = true;
	private double scaleFirst=0.1;
	private BufferedImage beginningImage;
	
	private boolean combineAnimation=false;
	private double scaleCombine = 1.2;
	
	private BufferedImage combineImage;
	private boolean canCombine=true;
	
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	public Tile(RubiksColors value, int x, int y) {
		this.value = value;
		this.x = x;
		this.y = y;
		slideTo=new Point(x,y);
		tileImage = new BufferedImage(WIDTH, HEIGHT,
				BufferedImage.TYPE_INT_ARGB);
		beginningImage=new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_ARGB);
		combineImage = new BufferedImage(WIDTH*2,HEIGHT*2,BufferedImage.TYPE_INT_ARGB);
		drawImage();
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	private void drawImage() {
		Graphics2D g = (Graphics2D) tileImage.getGraphics();
		switch (value) {
		case white:
			background = new Color(0xFFFFFF);
			break;
		case red:
			background = new Color(0xFF0000);
			break;
		case blue:
			background = new Color(0x0033FF);
			break;
		case orange:
			background = new Color(0xFFA319);
			break;
		case green:
			background = new Color(0x00BE00);
			break;
		case yellow:
			background = new Color(0xFFFF00);
			break;
		default:
			background=Color.black;
		}
		g.setColor(new Color(0,0,0,0));
		g.fillRect(0, 0, WIDTH, HEIGHT);
		g.setColor(background);
		g.fillRoundRect(0,0,WIDTH,HEIGHT,ARC_WIDTH,ARC_HEIGHT);
		g.setColor(new Color(0x000000));
		g.setStroke(new BasicStroke(10));
		g.drawRoundRect(0,0,WIDTH,HEIGHT,ARC_WIDTH,ARC_HEIGHT);
		/*g.setColor(text);
		if(value<-64){
			font=Game.main.deriveFont(36f);
		}else{
			font=Game.main;
			g.setFont(font);
		}
		int drawX = WIDTH/2-DrawUtils.getMessageWidth(""+value,font,g)/2;
		int drawY = WIDTH/2+DrawUtils.getMessageWidth(""+value,font,g)/2;
		g.drawString(""+value, drawX, drawY);*/
		g.dispose();
	}
	public void update(){
		if(beginningAnimation){
			AffineTransform transform = new AffineTransform();
			transform.translate(WIDTH/2-scaleFirst*WIDTH/2, HEIGHT/2-scaleFirst*HEIGHT/2);
			transform.scale(scaleFirst,scaleFirst);
			Graphics2D g2d = (Graphics2D)beginningImage.getGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g2d.setColor(new Color(0,0,0,0));
			g2d.fillRect(0, 0, WIDTH, HEIGHT);
			g2d.drawImage(tileImage,transform,null);
			scaleFirst+=0.1;
			g2d.dispose();
			if(scaleFirst>=1)beginningAnimation = false;
		}else if(combineAnimation){
			AffineTransform transform = new AffineTransform();
			transform.translate(WIDTH/2-scaleCombine*WIDTH/2, HEIGHT/2-scaleCombine*HEIGHT/2);
			transform.scale(scaleCombine,scaleCombine);
			Graphics2D g2d = (Graphics2D)combineImage.getGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g2d.setColor(new Color(0,0,0,0));
			g2d.fillRect(0, 0, WIDTH, HEIGHT);
			g2d.drawImage(tileImage,transform,null);
			scaleCombine-=0.05;
			g2d.dispose();
			if(scaleCombine<=1)combineAnimation = false;
		}
	}
	
	public boolean isCombineAnimation() {
		return combineAnimation;
	}

	public void setCombineAnimation(boolean combineAnimation) {
		this.combineAnimation = combineAnimation;
		if(combineAnimation) scaleCombine = 1.3;
	}

	public void render(Graphics2D g){
		if(beginningAnimation){
			g.drawImage(beginningImage,x,y,null);
		}else if(combineAnimation){
			g.drawImage(combineImage,(int)(x+WIDTH/2-scaleCombine*WIDTH/2)
									,(int)(y+HEIGHT/2-scaleCombine*HEIGHT/2),null);
		}else{
			g.drawImage(tileImage,x,y,null);
		}
		
	}
	public RubiksColors getValue(){
		return value;
	}
	public void setValue(RubiksColors value){
		this.value=value;
		drawImage();
	}
	public boolean CanCombine() {
		return canCombine;
	}

	public void setCanCombine(boolean canCombine) {
		this.canCombine = canCombine;
	}

	public Point getSlideTo() {
		return slideTo;
	}

	public void setSlideTo(Point slideTo) {
		this.slideTo = slideTo;
	}
}
