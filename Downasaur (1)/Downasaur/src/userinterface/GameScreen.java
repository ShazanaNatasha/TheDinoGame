package userinterface;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import gameobject.Clouds;
import gameobject.EnemiesManager;
import gameobject.Land;
import gameobject.MainCharacter;
import util.Resource;

public class GameScreen extends JPanel implements Runnable, KeyListener {

	private static final int START_GAME_STATE = 0;
	private static final int GAME_PLAYING_STATE = 1;
	private static final int GAME_OVER_STATE = 2;
	
	private Land land;
	private int fps = 100;
	private MainCharacter mainCharacter;
	private EnemiesManager enemiesManager;
	private Clouds clouds;
	private Thread thread;

	private boolean isKeyPressed;
	private int gameState = START_GAME_STATE;

	private BufferedImage replayButtonImage;
	private BufferedImage gameOverButtonImage;
	private BufferedImage playGameButtonImage;

	public GameScreen() {
		mainCharacter = new MainCharacter();
		land = new Land(GameWindow.SCREEN_WIDTH, mainCharacter);
		mainCharacter.setSpeedX(4);
		replayButtonImage = Resource.getResouceImage("data/replay_button.png");
		gameOverButtonImage = Resource.getResouceImage("data/gameover_text.png");
		playGameButtonImage = Resource.getResouceImage("data/play_button.png");
		enemiesManager = new EnemiesManager(mainCharacter);
		clouds = new Clouds(GameWindow.SCREEN_WIDTH, mainCharacter);
		
	}

	public void startGame() {
		thread = new Thread(this);
		thread.start();
	}

	public void gameUpdate() {
		if (gameState == GAME_PLAYING_STATE) {
			clouds.update();
			land.update();
			mainCharacter.update();
			enemiesManager.update();
			if (enemiesManager.isCollision()) {
				mainCharacter.playDeadSound(); //hurt sound when getting hit
				if (mainCharacter.hp <= 0) {  //if statement to check if dino has <= 0 hp
					mainCharacter.playDeadSound();
					gameState = GAME_OVER_STATE;
				}
			}
		}
	}

	public void paint(Graphics g) {
		g.setColor(Color.decode("#f7f7f7"));
		g.fillRect(0, 0, getWidth(), getHeight());

		switch (gameState) {
		case START_GAME_STATE:
			land.draw(g);
			clouds.draw(g);
			g.setColor(Color.BLACK);
			g.setFont(new Font("Arial", Font.BOLD, 20));
			g.drawString("Dino Game", 250, 25);
			g.setFont(new Font("Arial", Font.BOLD, 10));
			g.drawString("Press Space to Play", 250, 45);
			g.drawString("Hold Z for 2x speed", 400, 65);
			g.drawString("DOWN for crouch", 400, 55);
			g.drawString("UP/Space for jump", 400, 45);
			g.drawImage(playGameButtonImage,283, 50, null);

			break;
		case GAME_PLAYING_STATE:
		case GAME_OVER_STATE:
			clouds.draw(g);
			land.draw(g);
			enemiesManager.draw(g);
			mainCharacter.draw(g);
			g.setColor(Color.BLACK);
			g.drawString("HP " + mainCharacter.hp, 400, 20);
			g.drawString("HI " + mainCharacter.score, 450, 20);
			g.drawString("HS " + mainCharacter.highscore, 500, 20);
			if (gameState == GAME_OVER_STATE) {
				g.drawImage(gameOverButtonImage, 200, 30, null);
				g.drawImage(replayButtonImage, 283, 50, null);
				
			}
			break;
		}
	}

	@Override
	public void run() {

		//int fps = this.fps;
		
		long lastTime = 0;
		long elapsed;
		
		int msSleep;
		int nanoSleep;

		long endProcessGame;
		long lag = 0;
		while (true) {
			gameUpdate();
			repaint();
			long msPerFrame = 1000 * 1000000 / fps;
			endProcessGame = System.nanoTime();
			elapsed = (lastTime + msPerFrame - System.nanoTime());
			msSleep = (int) (elapsed / 1000000);
			nanoSleep = (int) (elapsed % 1000000);
			if (msSleep <= 0) {
				lastTime = System.nanoTime();
				continue;
			}
			try {
				Thread.sleep(msSleep, nanoSleep);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			lastTime = System.nanoTime();
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
	    switch (gameState) {
	        case START_GAME_STATE:
	            if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_UP) {
	                gameState = GAME_PLAYING_STATE;
	            }
	            break;
	        case GAME_PLAYING_STATE:
	
	            if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_UP) {
	                mainCharacter.jump();
	            }
	
	            else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
	                mainCharacter.down(true);
	            }
	   
	            else if (e.getKeyCode() == KeyEvent.VK_Z) {
	                fps = 200;
	            }
	            break;
	        case GAME_OVER_STATE:
	            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
	                gameState = GAME_PLAYING_STATE;
	                resetGame();
	            }
	            break;
	    }
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (gameState == GAME_PLAYING_STATE) {
			if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				mainCharacter.down(false);
			}
			if (e.getKeyCode() == KeyEvent.VK_Z) {
	            fps = 100;
	        }
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub-

	}

	
	private void resetGame() {
		enemiesManager.reset();
		mainCharacter.dead(false);
		mainCharacter.reset();
	}
	
}