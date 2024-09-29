package gameobject;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import util.Resource;

public class EnemiesManager {
	
	private BufferedImage cactus1;
	private BufferedImage cactus2;
	private Random rand;
	
	private List<Enemy> enemies;
	private MainCharacter mainCharacter;
	private static final int cactus_damage = 1;
	private boolean isDamaged;
	
	public EnemiesManager(MainCharacter mainCharacter) {
		rand = new Random();
		cactus1 = Resource.getResouceImage("data/cactus1.png");
		cactus2 = Resource.getResouceImage("data/cactus2.png");
		enemies = new ArrayList<Enemy>();
		this.mainCharacter = mainCharacter;
		enemies.add(createEnemy());
		isDamaged = false;
	}
	
	public void update() {
		for(Enemy e : enemies) {
			e.update();
		}
		Enemy enemy = enemies.get(0);
		if(enemy.isOutOfScreen()) {
			//mainCharacter.upScore();
			enemies.clear();
			enemies.add(createEnemy());
			isDamaged = false;
			
		}
		
		// if statement to make sure you only take damage once when you get hit by the cactus
		if (isCollision() && !isDamaged) {
			  System.out.println("Collision detected! Current HP: " + mainCharacter.hp);
			  mainCharacter.takeDamage(cactus_damage);
			  System.out.println("Damage taken! New HP: " + mainCharacter.hp);
			  isDamaged = true;
		} else if (!isCollision()) {
			isDamaged = false;
		}
	}
	
	
	
	public void draw(Graphics g) {
		for(Enemy e : enemies) {
			e.draw(g);
		}
	}
	
	private Enemy createEnemy() {
		// if (enemyType = getRandom)
		int type = rand.nextInt(2);
		if(type == 0) {
			return new Cactus(mainCharacter, 800, cactus1.getWidth() - 10, cactus1.getHeight() - 10, cactus1);
		} else {
			return new Cactus(mainCharacter, 800, cactus2.getWidth() - 10, cactus2.getHeight() - 10, cactus2);
		}
	}
	
	public boolean isCollision() {
		for(Enemy e : enemies) {
			if (mainCharacter.getBound().intersects(e.getBound())) {
					return true;			
			}
		}
		return false;
	}

	
	public void reset() {
		enemies.clear();
		enemies.add(createEnemy());
		isDamaged = false;
	}
	
}
