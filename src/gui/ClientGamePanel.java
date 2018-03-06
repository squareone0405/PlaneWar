package gui;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;

import javax.swing.JPanel;

import config.Config;
import config.GameState;
import mysocket.Client;
import planes.*;

public class ClientGamePanel extends GamePanel {

	private Client client;
	private PaintThread paintThread;

	public ClientGamePanel() {
		super();
		this.addKeyListener(new KeyProcessor());
	}

	protected void paintComponent(Graphics g) {
		if(gState == GameState.G_PLAYING){
			super.paintComponent(g);
			this.drawComponent(g);
			this.drawBooms(g);
			this.drawStatus(g);
		}
	}

	public void setMp(int count, int[] xArr, int[] yArr) {
		for(int i = 0; i < 2; ++i) {
			myPlanes[i] = null;
		}
		for(int i = 0; i < count; ++i) {
			synchronized (myPlanes) {
				myPlanes[i] = new MyPlane(xArr[i], yArr[i], this);
			}
		}
	}

	public void setNormalPlane(int count, int[] xArr, int[] yArr) {
		synchronized (enemyPlanes) {
			enemyPlanes.clear();
		}
		for(int i = 0; i < count; ++i) {
			synchronized (enemyPlanes) {
				enemyPlanes.add(new NormalPlane(xArr[i], yArr[i], (JPanel)this));
			}
		}
	}

	public void setEnhancedPlane(int count, int[] xArr, int[] yArr) {
		for(int i = 0; i < count; ++i) {
			synchronized (enemyPlanes) {
				enemyPlanes.add(new EnhancedPlane(xArr[i], yArr[i], (JPanel)this));
			}
		}
	}

	public void setSuicidePlane(int count, int[] xArr, int[] yArr) {
		for(int i = 0; i < count; ++i) {
			synchronized (enemyPlanes) {
				enemyPlanes.add(new SuicidePlane(xArr[i], yArr[i], (JPanel)this));
			}
		}
	}

	public void setMb(int count, int[] xArr, int[] yArr) {
		synchronized (myBullets) {
			myBullets.clear();
		}
		for(int i = 0; i < count; ++i) {
			synchronized (myBullets) {
				myBullets.add(new MyBullet(xArr[i], yArr[i], (JPanel)this));
			}
		}
	}

	public void setEb(int count, int[] xArr, int[] yArr) {
		synchronized (enemyBullets) {
			enemyBullets.clear();
		}
		for(int i = 0; i < count; ++i) {
			synchronized (enemyBullets) {
				enemyBullets.add(new EnemyBullet(xArr[i], yArr[i], (JPanel)this));
			}
		}
	}

	public void setBooms(int count, int[] xArr, int[] yArr) {
		synchronized (booms) {
			booms.clear();
		}
		for(int i = 0; i < count; ++i) {
			synchronized (booms) {
				booms.add(new Point(xArr[i], yArr[i]));
			}
		}
	}

	public void setStatus(boolean isWon, boolean isFailed, int[] life, int score) {
		this.isWon = isWon;
		this.isFailed = isFailed;
		this.life = Arrays.copyOf(life, life.length);
		this.score = score;
		if(isWon || isFailed) {
			setGameState(GameState.G_OVER);
			if(paintThread != null){
				synchronized (paintThread) {	
					paintThread.stop();
				}
			}
			this.checkGameOver(getGraphics());
		}
	}

	class KeyProcessor extends KeyAdapter {
		public KeyProcessor() {
		}
		public void keyPressed(KeyEvent ke) {
			if(ke.getKeyCode() == KeyEvent.VK_LEFT) {
				client.sendMsg(Config.CLIENT_MOVE_MSG + "l");
			}
			if(ke.getKeyCode() == KeyEvent.VK_RIGHT) {
				client.sendMsg(Config.CLIENT_MOVE_MSG + "r");
			}
			if(ke.getKeyCode() == KeyEvent.VK_UP) {
				client.sendMsg(Config.CLIENT_MOVE_MSG + "u");
			}
			if(ke.getKeyCode() == KeyEvent.VK_DOWN) {
				client.sendMsg(Config.CLIENT_MOVE_MSG + "d");
			}
			if(ke.getKeyCode() == KeyEvent.VK_SPACE) {
				client.sendMsg(Config.CLIENT_SHOOT_MSG);
			}
		}
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public void startGame() {
		if(gState == GameState.G_READY || gState == GameState.G_OVER) {
			gameInit();
			setGameState(GameState.G_PLAYING);
		}
	}

	public void pauseGame() {
		if(gState == GameState.G_PLAYING) {
			paintThread.suspend();
			setGameState(GameState.G_PAUSE);
		}
	}

	public void resumeGame() {
		if(gState == GameState.G_PAUSE) {
			paintThread.resume();
			setGameState(GameState.G_PLAYING);
		}
	}

	public void gameInit() {
		isWon = false;
		isFailed = false;
		score = 0;
		paintThread = new PaintThread();
		paintThread.start();
	}


	class PaintThread extends Thread {
		public void run() {
			while(true) {
				ClientGamePanel.this.repaint();
				try {
					Thread.sleep(Config.CLIENT_REPAINT_INTERVAL);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}		
		}
	}
}
