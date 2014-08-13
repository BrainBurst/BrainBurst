
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Random;

public class Target {
	public static int ROWS = 3;
	public static int COLS = 3;
	public int blueCount,redCount,greenCount,yellowCount,whiteCount,orangeCount;
	private final int startingTiles = 9;
	protected Tile[][] board;
	private boolean dead, won;
	private boolean hasStarted;
	private BufferedImage gameBoard;
	private BufferedImage finalBoard;
	private int x;
	private int y;
	private int score;
	private int highScore = 0;
	private Font scoreFont;
	public RubiksColors[][] screenColors = new RubiksColors[ROWS][COLS]; //DECLARES screenColors A VARIABLE WHERE THE COLORS OF BOARD WILL BE STORED
	// Saving
	private String saveDataPath;
	private String fileName = "SaveData";

	private static int SPACING = 2;
	public static int BOARD_WIDTH = (COLS + 1) * SPACING + COLS * Tile.WIDTH;
	public static int BOARD_HEIGHT = (ROWS + 1) * SPACING + COLS * Tile.WIDTH;

	private long elapsedMS;
	private long fastestMS;
	private long startTime;
	private String fromattedTime = "00:00:000";

	public Target(int x, int y) {
		orangeCount=whiteCount=yellowCount=greenCount=redCount=blueCount=0;
		try {
			saveDataPath = Target.class.getProtectionDomain()
					.getCodeSource().getLocation().toURI().getPath();

		} catch (Exception e) {
			e.printStackTrace();
		}
		scoreFont = Game.main.deriveFont(24f);
		this.x = x;
		this.y = y;
		board = new Tile[ROWS][COLS];
		gameBoard = new BufferedImage(BOARD_WIDTH, BOARD_HEIGHT,
				BufferedImage.TYPE_INT_RGB);
		finalBoard = new BufferedImage(BOARD_WIDTH, BOARD_HEIGHT,
				BufferedImage.TYPE_INT_RGB);
		startTime = System.nanoTime();
		loadHighScore();
		createBoardImage();
		start();
	}
	private void createSaveData() {
		try {
			File file = new File(saveDataPath, fileName);

			FileWriter output = new FileWriter(file);
			BufferedWriter writer = new BufferedWriter(output);
			writer.write("" + 0);
			writer.newLine();
			writer.write("" + Integer.MAX_VALUE);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadHighScore() {
		try {
			File f = new File(saveDataPath, fileName);

			if (!f.isFile()) {
				createSaveData();
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(f)));
			highScore = Integer.parseInt(reader.readLine());
			fastestMS = Long.parseLong(reader.readLine());
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setHighScore() {
		FileWriter output = null;
		try {
			File f = new File(saveDataPath, fileName);
			output = new FileWriter(f);
			BufferedWriter writer = new BufferedWriter(output);
			writer.write("" + highScore + "\n");
			if (elapsedMS >= fastestMS && won) {
				writer.write("" + elapsedMS);
			} else {
				writer.write("" + fastestMS);
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createBoardImage() {
		Graphics2D g = (Graphics2D) gameBoard.getGraphics();
		g.setColor(new Color(0x999999));
		g.fillRoundRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT, Tile.ARC_WIDTH,
				Tile.ARC_HEIGHT);
		g.setColor(new Color(0x999999));
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				int x = SPACING + SPACING * col + Tile.WIDTH * col;
				int y = SPACING + SPACING * row + Tile.HEIGHT * row;
				g.fillRoundRect(x, y, Tile.WIDTH, Tile.HEIGHT, Tile.ARC_WIDTH,
						Tile.ARC_HEIGHT);
			}
		}
	}

	public void render(Graphics2D g) {
		Graphics2D g2d = (Graphics2D) finalBoard.getGraphics();
		g2d.drawImage(gameBoard, 0, 0, null);
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < ROWS; col++) {
				Tile current = board[row][col];
				if (current == null)
					continue;
				current.render(g2d);
			}
		}

		g.drawImage(finalBoard, x, y, null);
		g2d.dispose();

		g.setColor(Color.lightGray);
		g.setColor(new Color(0xFF0000));
		g.setFont(new Font("Arial", Font.BOLD, 16));
		String howTo1 = "HOW TO PLAY: Use your arrow keys to move the tiles. When";
		String howTo2 = "two tiles with the same number touch, they merge into one!";
		g.drawString(howTo1, 40, Game.HEIGHT - 40);
		g.drawString(howTo2, 40, Game.HEIGHT - 10);
		g.drawString("Move the Tiles to Match the Picture!", 50, 130);
		g.fillRect(380, 100, 120, 40);
		g.fillRect(380, 100, 120, 40);
		g.setColor(new Color(0xF9F6F2));
		g.setFont(new Font("Arial", Font.BOLD, 18));
		g.drawString("New Game", 392, 127);
		g.setColor(new Color(0xFF0000));
		g.setFont(new Font("Arial", Font.BOLD, 43));
		g.drawString("Rubik's Race", 50, 80);
		g.setColor(new Color(0xBBADA0));
		g.fillRect(330, 32, 90, 50);
		g.fillRect(424, 32, 90, 50);
		g.setColor(new Color(0xEEE4DA));
		g.setFont(new Font("Arial", Font.BOLD, 13));
		g.drawString("SCORE", 353, 50);
		g.drawString("BEST", 450, 50);
		g.setColor(new Color(0xFFFFFF));
		g.setFont(new Font("Arial", Font.BOLD, 17));
		g.drawString("" + score, 353, 68);
		g.drawString("" + highScore, 450, 68);
		g.setStroke(new BasicStroke(3));
		g.setColor(new Color(0xBF00FD));
		g.drawRect(192, 442, Tile.WIDTH * 3 + 5, 6 + Tile.HEIGHT * 3);
		/*
		 * g.setFont(scoreFont); g.drawString("" + score, 30, 40);
		 * g.setColor(Color.red); g.drawString("Best: "+highScore,
		 * Game.WIDTH-DrawUtils.getMessageWidth("Best: "+highScore, scoreFont,
		 * g)-20, 40);
		 * g.drawString("Fastest: "+formatTime(fastestMS),Game.WIDTH-
		 * DrawUtils.getMessageWidth("Fastest: "+formatTime(fastestMS),
		 * scoreFont, g)-20,90); g.setColor(Color.black);
		 * g.drawString("Time: "+fromattedTime, 30, 90);
		 */
	}

	public void update() {

		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				Tile current = board[row][col];
				if (current == null)
					continue;
				current.update();
				resetPosition(current, row, col);
			}
		}
	}



	private void resetPosition(Tile current, int row, int col) {
		if (current == null)
			return;
		int x = getTileX(col);
		int y = getTileY(row);
		int distX = current.getX() - x;
		int distY = current.getY() - y;
		if (Math.abs(distX) < Tile.SLIDE_SPEED) {
			current.setX(current.getX() - distX);
		}
		if (Math.abs(distY) < Tile.SLIDE_SPEED) {
			current.setY(current.getY() - distY);
		}
		if (distX < 0) {
			current.setX(current.getX() + Tile.SLIDE_SPEED);
		}
		if (distY < 0) {
			current.setY(current.getY() + Tile.SLIDE_SPEED);
		}
		if (distX > 0) {
			current.setX(current.getX() - Tile.SLIDE_SPEED);
		}
		if (distY > 0) {
			current.setY(current.getY() - Tile.SLIDE_SPEED);
		}
	}

	private void start() {
		for (int i = 0; i < startingTiles; i++) {
			spawnRandom();
		}
	}

	private void spawnRandom() {
		Random random = new Random();
		boolean notValid = true;
		while (notValid) {
			int location = random.nextInt(ROWS * COLS);
			int row = location / ROWS;
			int col = location % COLS;
			Tile current = board[row][col];
			if (current == null) {
				RubiksColors which = null;
				while(true){
					int value = random.nextInt(6);
				if (value == 0) {
					if(yellowCount<4){
					which = RubiksColors.yellow;
					screenColors[row][col]=which;
					yellowCount++;
					break;
					}
				} else if (value == 1) {
					if(whiteCount<4){
					which = RubiksColors.white;
					screenColors[row][col]=which;
					whiteCount++;
					break;
					}
				} else if (value == 2) {
					if(redCount<4){
					which = RubiksColors.red;
					screenColors[row][col]=which;
					redCount++;
					break;
					}
				} else if (value == 3) {
					if(blueCount<4){
					which = RubiksColors.blue;
					screenColors[row][col]=which;
					blueCount++;
					break;
					}
				} else if (value == 4) {
					if(orangeCount<4){
					which = RubiksColors.orange;
					screenColors[row][col]=which;
					orangeCount++;
					break;
					}
				} else if (value == 5) {
					if(greenCount<4){
					which = RubiksColors.green;
					screenColors[row][col]=which;
					greenCount++;
					break;
					}
				}
				}
				Tile tile = new Tile(which, getTileX(col), getTileY(row));
				board[row][col] = tile;
				notValid = false;
			}
		}
	}

	public int getTileX(int col) {
		return SPACING + col * Tile.WIDTH + col * SPACING;
	}

	public int getTileY(int row) {
		return SPACING + row * Tile.HEIGHT + row * SPACING;
	}














	


}
