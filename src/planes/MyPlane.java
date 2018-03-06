package planes;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import config.Config;

public class MyPlane extends Plane{
	
	public MyPlane(int x, int y, JPanel gPanel) {
		super(x, y, gPanel);
		this.xSpeed = Config.MY_SPEED;
		this.ySpeed = Config.MY_SPEED;
		this.icon = new ImageIcon(Config.MYPLANE_IMG).getImage();
		this.height = icon.getHeight(gPanel);
		this.width = icon.getWidth(gPanel);
		vx = 0;
		vy = 0;
		this.life = Config.MY_LIFE;
	}

	@Override
	public void correctPos() {
		if(x < 0)
			x = 0;
		if(x + width > Config.PANEL_WIDTH)
			x = Config.PANEL_WIDTH - width;
		if(y < 0)
			y = 0;
		if(y + height > Config.FRAME_HEIGHT)
			y = Config.FRAME_HEIGHT - height;
	}
	
	public void move(int dirX, int dirY) {
		this.x += dirX * xSpeed;
		this.y += dirY * ySpeed;
		correctPos();
	}
}
