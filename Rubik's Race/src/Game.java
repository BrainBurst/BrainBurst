

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Game extends JPanel implements KeyListener, Runnable,MouseListener {
	private static final long serialVersionUID = 1L;
	public static final int WIDTH = 540;
	public static final int HEIGHT = 750;
	public static final Font main = new Font("Arial", Font.BOLD,
			50);
	public static final Font titleFont = new Font("Arial", Font.BOLD,
			60);
	private Thread game;
	private boolean running;
	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT,
			BufferedImage.TYPE_INT_RGB);
	private GameBoard board;
	private Target target;
	/*private long startTime;
	private long elapsed;
	private boolean set;*/
	public Game(JFrame window){
		if(window!=null){
			window.dispose();
			}
			Game game=new Game();
			
			window=new JFrame("Rubik's Race");
			
			window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			window.setResizable(false);
			window.add(game);
			window.pack();
			window.setLocationRelativeTo(null);
			game.start();
			window.setAlwaysOnTop(true);
			
			window.setVisible(true);
	}
	public Game() {
		setFocusable(true);
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		addKeyListener(this);
		addMouseListener(this);
		target=new Target((WIDTH/2-GameBoard.BOARD_WIDTH/2)+Tile.WIDTH,(HEIGHT-GameBoard.BOARD_HEIGHT-100)-200);
		board=new GameBoard((WIDTH/2-GameBoard.BOARD_WIDTH/2),HEIGHT-GameBoard.BOARD_HEIGHT-100,target);
	}

	private void update() {
		board.update();
		target.update();
		Keyboard.update();
	}

	private void render() {
		Graphics2D g = (Graphics2D) image.getGraphics();
		g.setColor(new Color(0x000000));
		g.fillRect(0, 0, WIDTH, HEIGHT);
		board.render(g);
		target.render(g);
		g.dispose();
		Graphics2D g2d = (Graphics2D) getGraphics();
		g2d.drawImage(image, 0, 0, null);
		g2d.dispose();
	}

	public void run() {
	
		@SuppressWarnings("unused")
		int fps = 0, updates = 0;
		long fpsTimer = System.currentTimeMillis();
		double nsPerUpdate = 1000000000.0 / 60;

		// last update time in nano
		double then = System.nanoTime();
		double unprocessed = 0;
		while(running){
			boolean shouldRender = false;
			double now=System.nanoTime();
			unprocessed+=(now-then)/nsPerUpdate;
			then=now;
			
		// update queue
		while (unprocessed >= 1) {
			updates++;
			update();
			unprocessed--;
			shouldRender = true;
		}

		// render
		if (shouldRender) {
			fps++;
			render();
			shouldRender = false;
		} else {
			try {
				Thread.sleep(1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(System.currentTimeMillis()-fpsTimer>1000){
			fps=0;
			updates=0;
			fpsTimer+=1000;
		}
		}
		
	}
	public synchronized void start(){
		if(running)return;
		running=true;
		game=new Thread(this, "game");
		game.start();
	}
	public synchronized void stop(){
		if(!running)return;
		running=false;
		System.exit(0);
	}
	public void keyPressed(KeyEvent arg0) {
		Keyboard.keyPressed(arg0);
	}

	public void keyReleased(KeyEvent arg0) {
		Keyboard.keyReleased(arg0);
	}

	public void keyTyped(KeyEvent arg0) {

	}

	public void mouseClicked(MouseEvent e) {
		if(e.getX()>380&&e.getY()>100&&e.getX()<380+120&&e.getY()<100+40){
		//Start.start();
			target=new Target((WIDTH/2-GameBoard.BOARD_WIDTH/2)+Tile.WIDTH,(HEIGHT-GameBoard.BOARD_HEIGHT-100)-200);
			board=new GameBoard((WIDTH/2-GameBoard.BOARD_WIDTH/2),HEIGHT-GameBoard.BOARD_HEIGHT-100,target);
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}



}
