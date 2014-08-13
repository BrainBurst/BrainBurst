import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.List;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class GameBoard {
	public static int ROWS = 5;
	public static int COLS = 5;
	private final int difficulty=10;
	private Tile[][] board;
	private boolean dead, won;
	private boolean hasStarted;
	private BufferedImage gameBoard;
	private BufferedImage finalBoard;
	private int x;
	private int y;
	private int score;
	private int highScore = 0;
	private Target target;
	private Font scoreFont;
	private int blueCount, redCount, greenCount, yellowCount, whiteCount,
			orangeCount;
	ArrayList<Direction> solution = new ArrayList<Direction>();
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

	public GameBoard(int x, int y, Target target) {
		this.target = target;
		try {
			saveDataPath = GameBoard.class.getProtectionDomain()
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
		// TODO loadHighScore();
		createBoardImage();
		start();
	}

	/*
	 * private void createSaveData() { try { File file = new File(saveDataPath,
	 * fileName);
	 * 
	 * FileWriter output = new FileWriter(file); BufferedWriter writer = new
	 * BufferedWriter(output); writer.write("" + 0); writer.newLine();
	 * writer.write("" + Integer.MAX_VALUE); writer.close(); } catch (Exception
	 * e) { e.printStackTrace(); } }
	 * 
	 * private void loadHighScore() { try { File f = new File(saveDataPath,
	 * fileName);
	 * 
	 * if (!f.isFile()) { createSaveData(); } BufferedReader reader = new
	 * BufferedReader(new InputStreamReader( new FileInputStream(f))); highScore
	 * = Integer.parseInt(reader.readLine()); fastestMS =
	 * Long.parseLong(reader.readLine()); reader.close(); } catch (Exception e)
	 * { e.printStackTrace(); } }
	 * 
	 * private void setHighScore() { FileWriter output = null; try { File f =
	 * new File(saveDataPath, fileName); output = new FileWriter(f);
	 * BufferedWriter writer = new BufferedWriter(output); writer.write("" +
	 * highScore + "\n"); if (elapsedMS >= fastestMS && won) { writer.write("" +
	 * elapsedMS); } else { writer.write("" + fastestMS); } writer.close(); }
	 * catch (Exception e) { e.printStackTrace(); } }
	 */

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
		
		if (!won && !dead) {
			if (hasStarted) {
				elapsedMS = (System.nanoTime() - startTime) / 1000000;
				fromattedTime = formatTime(elapsedMS);
			} else {
				startTime = System.nanoTime();
			}
		}
		checkKeys();
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

	private String formatTime(long millis) {
		String formattedTime;
		String hourFormat = "";
		int hours = (int) (millis / 3600000);
		if (hours >= 1) {
			millis -= hours * 3600000;
			if (hours < 10) {
				hourFormat = "0" + hours;
			} else {
				hourFormat = "" + hours;
			}
		}
		hourFormat += ":";

		String minuteFormat;
		int minutes = (int) (millis / 60000);
		if (minutes >= 1) {
			millis -= minutes * 60000;
			if (minutes > 10) {
				minuteFormat = "0" + minutes;
			} else {
				minuteFormat = "" + minutes;
			}
		} else {
			minuteFormat = "00";
		}
		String secondFormat;
		int seconds = (int) (millis / 1000);
		if (seconds >= 1) {
			millis -= seconds * 1000;
			if (seconds > 10) {
				secondFormat = "0" + seconds;
			} else {
				secondFormat = "" + seconds;
			}
		} else {
			secondFormat = "00";
		}

		String milliFormat;
		if (millis > 99) {
			milliFormat = "" + millis;
		} else if (millis > 9) {
			milliFormat = "0" + millis;
		} else {
			milliFormat = "00" + millis;
		}
		formattedTime = hourFormat + minuteFormat + ":" + secondFormat + ":"
				+ milliFormat;
		return formattedTime;
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
			spawnRandom();
		for (int i = 2; i < 5; i++) {
			for (int j = 2; j < 5; j++) {
				Tile tile;
				try {
					tile = (Tile) target.board[i - 2][j - 2].clone();
					tile.setX(tile.getX() - Tile.WIDTH);
					tile.setY(tile.getY() - Tile.HEIGHT);
					board[i - 1][j - 1] = tile;
				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (!hasStarted)
			hasStarted = true;
		for(int i=0;true;i++){
			Random random=new Random();
			int j=random.nextInt(4);
			if(j==0){
			moveTiles(Direction.UP);
			solution.add(Direction.DOWN);
			}if(j==1){
			moveTiles(Direction.LEFT);
			solution.add(Direction.RIGHT);
			}if(j==2){
			moveTiles(Direction.RIGHT);
			solution.add(Direction.LEFT);
			}if(j==3){
			moveTiles(Direction.DOWN);
			solution.add(Direction.UP);
			}
			if(!haveIWonYet()){
				if(i>this.difficulty){
					break;
				}
			}
		}
		System.out.println(solution.toString());
	}

	private boolean haveIWonYet(){
		for (int i = 2; i < 5; i++) {
			for (int j = 2; j < 5; j++) {
				Tile tile=null;
					try {
						tile = (Tile) target.board[i - 2][j - 2].clone();
					} catch (CloneNotSupportedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					tile.setX(tile.getX() - Tile.WIDTH);
					tile.setY(tile.getY() - Tile.HEIGHT);
					if(board[i-1][j-1]==null){
						return false;
					}
					;
					if(board[i - 1][j - 1].getX() == tile.getX()&&board[i - 1][j - 1].getY() == tile.getY()&&board[i - 1][j - 1].getY() == tile.getY()&&board[i - 1][j - 1].getValue() == tile.getValue()){
						if(board[i - 1][j - 1].getValue() == tile.getValue()){
							
						}else{
						System.out.println(board[i - 1][j - 1].getX()+" != "+tile.getX());
						System.out.println(board[i - 1][j - 1].getY()+" != "+tile.getY());
						System.out.println(board[i - 1][j - 1].getValue()+" != "+tile.getValue());
						return false;
					}	
					}
			}
		}
		return true;
	}
	private void spawnRandom() {
		Random random = new Random();
		boolean notValid = true;
		this.yellowCount = target.yellowCount;
		this.whiteCount = target.whiteCount;
		this.redCount = target.redCount;
		this.blueCount = target.blueCount;
		this.orangeCount = target.orangeCount;
		this.greenCount = target.greenCount;
		int location = random.nextInt(ROWS * COLS);
		int row1 = location / ROWS;
		int col1 = location % COLS;
		while(true){
			location = random.nextInt(ROWS * COLS);
			row1 = location / ROWS;
			col1 = location % COLS;
			if (row1 > 0 && row1 < 4 && col1 > 0 && col1 < 4) {
				continue;
			}else{
				break;
			}
		}
		for (int row = 0; row < 5; row++) {
			for (int col = 0; col < 5; col++) {
				Tile current = board[row][col];
				
				if (row > 0 && row < 4 && col > 0 && col < 4) {
					
					continue;
				}
				else if(row==row1&&col==col1){
					
				}
				else {
					RubiksColors which = null;
					for(long i=0;i<90;i++){
						int value = random.nextInt(6);
						if (value == 0) {
							if (yellowCount < 4) {
								which = RubiksColors.yellow;
								yellowCount++;
								break;
							}
						} else if (value == 1) {
							if (whiteCount < 4) {
								which = RubiksColors.white;
								whiteCount++;
								break;
							}
						} else if (value == 2) {
							if (redCount < 4) {
								which = RubiksColors.red;
								redCount++;
								break;
							}
						} else if (value == 3) {
							if (blueCount < 4) {
								which = RubiksColors.blue;
								blueCount++;
								break;
							}
						} else if (value == 4) {
							if (orangeCount < 4) {
								which = RubiksColors.orange;
								orangeCount++;
								break;
							}
						} else if (value == 5) {
							if (greenCount < 4) {
								which = RubiksColors.green;
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
	}

	public int getTileX(int col) {
		return SPACING + col * Tile.WIDTH + col * SPACING;
	}

	public int getTileY(int row) {
		return SPACING + row * Tile.HEIGHT + row * SPACING;
	}

	private boolean move(int row, int col, int horizontalDirection,
			int verticalDirection, Direction dir) {
		boolean canMove = false;
		Tile current = board[row][col];
		if (current == null)
			return false;
		boolean move = true;
		int newCol = col;
		int newRow = row;
		while (move) {
			newCol += horizontalDirection;
			newRow += verticalDirection;
			if (checkOutOfBounds(dir, newRow, newCol))
				break;
			if (board[newRow][newCol] == null) {
				board[newRow][newCol] = current;
				board[newRow - verticalDirection][newCol - horizontalDirection] = null;
				board[newRow][newCol].setSlideTo(new Point(newRow, newCol));
				canMove = true;
			} else {
				move = false;
			}
		}

		return canMove;
	}

	private boolean checkOutOfBounds(Direction dir, int row, int col) {
		if (dir == Direction.LEFT) {
			return col < 0;
		} else if (dir == Direction.RIGHT) {
			return col > COLS - 1;
		} else if (dir == Direction.UP) {
			return row < 0;
		} else if (dir == Direction.DOWN) {
			return row > ROWS - 1;
		}
		return false;
	}

	private Point getEmpty() {
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				if (board[row][col] == null) {
					return new Point(row, col);
				}
			}
		}
		return null;
	}

	private boolean isValidMove(Point p) {
		int row = p.row;
		int col = p.col;
		if (row > -1 && col > -1 && col < 5 && row < 5) {
			return true;
		}
		return false;
	}

	private boolean isValidMove(int row, int col) {
		if (row > -1 && col > -1 && col < 5 && row < 5) {
			return true;
		}
		return false;
	}

	private void moveTiles(Direction dir) {
		boolean canMove = false;
		int horizontalDirection = 0;
		int verticalDirection = 0;
		Point empty = getEmpty();
		if (dir == Direction.LEFT) {
			horizontalDirection = -1;
			int row = empty.row;
			int col = empty.col + 1;
			if (isValidMove(row, col)) {
				if (!canMove) {
					canMove = move(row, col, horizontalDirection,
							verticalDirection, dir);
				} else {
					move(row, col, horizontalDirection, verticalDirection, dir);
				}
			}
		}
		if (dir == Direction.RIGHT) {
			horizontalDirection = 1;
			int row = empty.row;
			int col = empty.col - 1;
			if (isValidMove(row, col)) {
				if (!canMove) {
					canMove = move(row, col, horizontalDirection,
							verticalDirection, dir);
				} else {
					move(row, col, horizontalDirection, verticalDirection, dir);
				}
			}
		}
		if (dir == Direction.UP) {
			verticalDirection = -1;
			int row = empty.row + 1;
			int col = empty.col;
			if (isValidMove(row, col)) {
				if (!canMove) {
					canMove = move(row, col, horizontalDirection,
							verticalDirection, dir);
				} else {
					move(row, col, horizontalDirection, verticalDirection, dir);
				}
			}
		}
		if (dir == Direction.DOWN) {
			verticalDirection = 1;
			int row = empty.row - 1;
			int col = empty.col;
			if (isValidMove(row, col)) {
				if (!canMove) {
					canMove = move(row, col, horizontalDirection,
							verticalDirection, dir);
				} else {
					move(row, col, horizontalDirection, verticalDirection, dir);
				}
			}

		}
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				Tile current = board[row][col];
				if (current == null)
					continue;
				current.setCanCombine(true);

			}
		}
		if (canMove) {
			checkDead();
		}
	}

	private void checkDead() {
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				if (board[row][col] == null)
					return;
				if (checkSurroundingTiles(row, col, board[row][col])) {
					return;
				}
			}
		}
		dead = true;
		if (score >= highScore) {
			highScore = score;
		}
		// TODO setHighScore();
	}

	private boolean checkSurroundingTiles(int row, int col, Tile current) {
		if (row > 0) {
			Tile check = board[row - 1][col];
			if (check == null)
				return true;
			if (current.getValue() == check.getValue())
				return true;
		}
		if (row < ROWS - 1) {
			Tile check = board[row + 1][col];
			if (check == null)
				return true;
			if (current.getValue() == check.getValue())
				return true;
		}
		if (col > 0) {
			Tile check = board[row][col - 1];
			if (check == null)
				return true;
			if (current.getValue() == check.getValue())
				return true;
		}
		if (col < COLS - 1) {
			Tile check = board[row][col + 1];
			if (check == null)
				return true;
			if (current.getValue() == check.getValue())
				return true;
		}
		return false;
	}

	private void checkKeys() {
		if (Keyboard.typed(KeyEvent.VK_LEFT)) {
			moveTiles(Direction.LEFT);
			if (!hasStarted)
				hasStarted = true;
		}
		if (Keyboard.typed(KeyEvent.VK_RIGHT)) {
			moveTiles(Direction.RIGHT);
			if (!hasStarted)
				hasStarted = true;
		}
		if (Keyboard.typed(KeyEvent.VK_UP)) {
			moveTiles(Direction.UP);
			if (!hasStarted)
				hasStarted = true;
		}
		if (Keyboard.typed(KeyEvent.VK_DOWN)) {
			moveTiles(Direction.DOWN);
			if (!hasStarted)
				hasStarted = true;
		}
	}

}
