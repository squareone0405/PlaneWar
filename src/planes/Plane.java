package planes;

import javax.swing.JPanel;

public abstract class Plane extends FlyingObject{
	protected int life;
	
	public Plane(int x, int y, JPanel gPanel) {
		super(x, y, gPanel);
	}
	
	public int getLife() {
		return life;
	}
	
	public void hit() {
		life--;
	}
	
	public boolean isDead() {
		return life == 0;
	}
}
