package planes;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import config.Config;
import config.EnemyPlaneType;

public class NormalPlane extends EnemyPlane {
	public NormalPlane(int x, int y, JPanel gPanel) {
		super(x, y, gPanel);
		this.type = EnemyPlaneType.NORMAL_PLANE;
		this.icon = new ImageIcon(Config.NORMAL_PLENE_IMG).getImage();
		this.height = icon.getHeight(gPanel);
		this.width = icon.getWidth(gPanel);
		this.life = Config.NORMAL_LIFE;
		this.killedScore = Config.NORMAL_SCORE;
	}
}