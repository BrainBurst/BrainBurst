



import javax.swing.JFrame;


public class Start{
	private static JFrame window;
	public static void main(String[] args){
		new Game(window);
	}
	public static void start(){
		if(window!=null){
		window.dispose();
		}
		Game game=new Game();
		
		window=new JFrame("2048");
		
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.add(game);
		window.pack();
		window.setLocationRelativeTo(null);
		game.start();
		window.setAlwaysOnTop(true);
		window.setVisible(true);
	}

}
