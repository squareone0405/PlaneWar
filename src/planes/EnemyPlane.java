package planes;

import java.util.Random;

import javax.swing.JPanel;

import config.Config;
import config.EnemyPlaneType;

public abstract class EnemyPlane extends Plane {
	protected EnemyPlaneType type; 
	protected int killedScore;
	
	public EnemyPlane(int x, int y, JPanel gPanel) {
		super(x, y, gPanel);
		this.ySpeed = Config.ENEMY_SPEED;
		this.xSpeed = Config.ENEMY_X_SPEED;
		vx = xSpeed;
		vy = ySpeed;
	}
	
	@Override
	public void move() {
		Random rand = new Random();
		int dir = rand.nextInt(3);
		this.setDir(dir -1);
		this.x += vx;
		this.y += vy;
		correctPos();
	}
	
	public EnemyPlaneType getType() {
		return type;
	}
	
	public int getKilledScore() {
		return killedScore;
	}
}
