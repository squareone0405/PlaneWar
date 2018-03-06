package planes;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import config.Config;
import config.EnemyPlaneType;

public class EnhancedPlane extends EnemyPlane {
	public EnhancedPlane(int x, int y, JPanel gPanel) {
		super(x, y, gPanel);
		this.type = EnemyPlaneType.ENHANCED_PLANE;
		this.icon = new ImageIcon(Config.EHANCED_PLENE_IMG).getImage();
		this.height = icon.getHeight(gPanel);
		this.width = icon.getWidth(gPanel);
		this.life = Config.ENHANCED_LIFE;
		this.killedScore = Config.ENHANCED_SCORE;
	}
}
