package planes;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import config.Config;
import config.EnemyPlaneType;

public class SuicidePlane extends EnemyPlane {
	public SuicidePlane(int x, int y, JPanel gPanel) {
		super(x, y, gPanel);
		this.type = EnemyPlaneType.SUICIDE_PLANE;
		this.icon = new ImageIcon(Config.SUICIDE_IMG).getImage();
		this.height = icon.getHeight(gPanel);
		this.width = icon.getWidth(gPanel);
		this.life = Config.SUICIDE_LIFE;
		this.killedScore = Config.SUICIDE_SCORE;
	}
	
	public void move(int x1, int x2) {
		int dif1 = Math.abs(x1 - (int)this.getRect().getCenterX());
		int dif2 = Math.abs(x2 - (int)this.getRect().getCenterX());
		if(dif1 < dif2) {
			move(x1);
		}
		else
			move(x2);
	}
	
	public void move(int otherX) {
		if(otherX > (int)this.getRect().getCenterX()) {
			this.setDir(1);
		}
		else if(otherX < (int)this.getRect().getCenterX()) {
			this.setDir(-1);
		}
		else if(otherX == (int)this.getRect().getCenterX()) {
			this.setDir(0);
		}
		this.x += vx;
		this.y += vy;
		correctPos();
	}
}