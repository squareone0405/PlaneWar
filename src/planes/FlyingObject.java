package planes;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.JPanel;

import config.Config;


public abstract class FlyingObject {
	protected JPanel gPanel;
	protected Image icon;
	protected int x, y;
	protected int width, height;
	protected int vx, vy;
	protected int xSpeed, ySpeed;
	
	public FlyingObject(int x, int y, JPanel gPanel) {
		this.x = x;
		this.y = y;
		this.gPanel = gPanel;
	}

	public Rectangle getRect() {
		return new Rectangle((int)x, (int)y, width, height);
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(icon, x, y, width, height, gPanel);
	}
	
	public void setPos(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void move() {
		this.x += vx;
		this.y += vy;
		correctPos();
	}
	
	public void correctPos() {
		if(x < 0)
			x = 0;
		if(x + width > Config.PANEL_WIDTH)
			x = Config.PANEL_WIDTH - width;
	}
	
	public boolean isOut() {
		return (y < 0 || y > Config.FRAME_HEIGHT);
	}
	
	public void setDir(int dirX, int dirY) {
		vx = xSpeed * dirX;
		vy = ySpeed * dirY;
	}
	
	public void setDir(int dirX) {
		vx = xSpeed * dirX;
	}
}
