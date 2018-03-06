package planes;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import config.Config;

public class EnemyBullet extends FlyingObject {
	public EnemyBullet(int x, int y, JPanel gPanel) {
		super(x, y, gPanel);
		this.ySpeed = Config.BULLET_SPEED;
		this.xSpeed = 0;
		this.icon = new ImageIcon(Config.ENEMY_BULLET_IMG).getImage();
		this.height = icon.getHeight(gPanel);
		this.width = icon.getWidth(gPanel);
		this.vx = 0;
		this.vy = ySpeed;
	}
}
