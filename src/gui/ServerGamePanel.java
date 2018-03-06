package gui;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.Random;

import config.Config;
import config.EnemyPlaneType;
import config.GameState;
import mysocket.Server;
import planes.*;

public class ServerGamePanel extends GamePanel {

	private int enemyKilled = 0;
	private boolean enShoot[] = {true, true};
	private PaintThread paintThread;

	private boolean isReady[] = {false, false};
	private Server server;

	private long startTime;
	private long gameTime = 0;

	public ServerGamePanel() {
		super();
		this.addKeyListener(new KeyProcessor());
	}

	public void startGame() {
		if(gState == GameState.G_READY || gState == GameState.G_OVER) {
			gameInit();
			server.sendMsg(Config.SERVER_GAME_START_MSG);
			setGameState(GameState.G_PLAYING);
			startTime = System.currentTimeMillis();
			gameTime = 0;
		}
	}

	public void pauseGame() {
		if(gState == GameState.G_PLAYING) {
			paintThread.suspend();
			server.sendMsg(Config.SERVER_GAME_PAUSE_MSG);
			setGameState(GameState.G_PAUSE);
			gameTime += System.currentTimeMillis() - startTime;
		}
	}

	public void resumeGame() {
		if(gState == GameState.G_PAUSE) {
			paintThread.resume();
			server.sendMsg(Config.SERVER_GAME_RESUME_MSG);
			setGameState(GameState.G_PLAYING);
			startTime = System.currentTimeMillis();
		}
	}

	public void gameInit() {
		myPlanes[0] = (new MyPlane(Config.PLANE0_X, Config.PLANE_Y, this));
		myPlanes[1] = (new MyPlane(Config.PLANE1_X, Config.PLANE_Y, this));
		isWon = false;
		isFailed = false;
		score = 0;
		paintThread = new PaintThread();
		paintThread.start();
	}

	protected void paintComponent(Graphics g) {
		if(gState == GameState.G_PLAYING) {
			this.randGen();
			this.moveComponent();
			syncPos();
			this.drawComponent(g);
			this.checkCollison();
			this.drawBooms(g);
			this.removeDeath();
			this.removeOut();
			this.updateStatus();
			syncStatus();
			this.drawStatus(g);
		}	
	}

	public void randGen() {
		Random rand = new Random();
		int planeSeed = rand.nextInt(Config.ENEMY_GEN);
		int posSeed = rand.nextInt(Config.PANEL_WIDTH - 50) + 25;
		synchronized (this.enemyPlanes) {
			if(planeSeed == 0) {
				enemyPlanes.add(new EnhancedPlane(posSeed, 0, this));
			}
			else if(planeSeed == 1) {

				enemyPlanes.add(new NormalPlane(posSeed, 0, this));
			}
			else if(planeSeed == 2) {
				enemyPlanes.add(new SuicidePlane(posSeed, 0, this));
			}
		}
		Iterator<EnemyPlane> itep = enemyPlanes.iterator();
		while(itep.hasNext()) {
			EnemyPlane ep = itep.next();
			int bulletSeed = rand.nextInt(Config.BULLET_GEN);
			if(bulletSeed == 0 && ep.getType() != EnemyPlaneType.SUICIDE_PLANE) {
				double centerX = ep.getRect().getCenterX();
				double maxY = ep.getRect().getMaxY();
				synchronized (this.enemyBullets) {
					enemyBullets.add(new EnemyBullet((int)centerX - Config.BULLET_WIDTH/2, (int)maxY, this));
				}
			}
		}
	}

	public void moveComponent() {
		Iterator<EnemyPlane> itep = enemyPlanes.iterator();
		Iterator<EnemyBullet> iteb = enemyBullets.iterator();
		Iterator<MyBullet> itmb = myBullets.iterator();
		while(itep.hasNext()) {
			synchronized (this.enemyPlanes) {
				EnemyPlane ep = itep.next();
				if(ep.getType() != EnemyPlaneType.SUICIDE_PLANE)
					ep.move();
				else {
					SuicidePlane sp = (SuicidePlane) ep;
					if(myPlanes[0] == null && myPlanes[1] == null) {
						sp.move();
					}

					else if(myPlanes[0] != null && myPlanes[1]!=null) {
						sp.move((int)myPlanes[0].getRect().getCenterX(), (int)myPlanes[1].getRect().getCenterX());
					}
					else {
						if(myPlanes[0] != null)
							sp.move((int)myPlanes[0].getRect().getCenterX());
						else if(myPlanes[1] != null)
							sp.move((int)myPlanes[1].getRect().getCenterX());
					}
				}
			}
		}
		for(int i = 0; i < 2; ++i) {
			if(myPlanes[i] != null) {
				myPlanes[i].move();
			}
		}
		while(iteb.hasNext()) {
			synchronized (this.enemyBullets) {
				iteb.next().move();
			}
		}
		while(itmb.hasNext()) {
			synchronized (this.myBullets) {
				itmb.next().move();
			}
		}
	}

	public void checkCollison() {
		booms.clear();
		Iterator<EnemyPlane> itep = enemyPlanes.iterator();
		Iterator<EnemyBullet> iteb = enemyBullets.iterator();
		Iterator<MyBullet> itmb = myBullets.iterator();
		enemyPlanes.iterator();
		while(itep.hasNext()) {
			EnemyPlane ep = itep.next();
			for(int i = 0; i < 2; ++i){
				if(myPlanes[i] != null) {
					if(ep.getRect().intersects(myPlanes[i].getRect())) {
						synchronized (enemyPlanes) {
							ep.hit();
						}
						synchronized (myPlanes) {
							myPlanes[i].hit();
						}
						addBoomEffect(myPlanes[i]);
					}
				}
			}
		}
		iteb = enemyBullets.iterator();
		while(iteb.hasNext()) {
			EnemyBullet eb = iteb.next();
			for(int i = 0; i < 2; ++i){
				if(myPlanes[i] != null) {
					if(eb.getRect().intersects(myPlanes[i].getRect())) {
						synchronized (enemyBullets) {
							iteb.remove();
						}
						synchronized (myPlanes) {
							myPlanes[i].hit();
						}
						addBoomEffect(myPlanes[i]);
					}
				}
			}
		}
		itmb = myBullets.iterator();
		while(itmb.hasNext()) {
			MyBullet mb = itmb.next();
			itep = enemyPlanes.iterator();
			while(itep.hasNext()) {
				EnemyPlane ep = itep.next();
				if(mb.getRect().intersects(ep.getRect())) {
					synchronized (myBullets) {
						itmb.remove();
					}
					synchronized (enemyPlanes) {
						ep.hit();
					}
					addBoomEffect(ep);
				}
			}
		}
		syncPos();
		syncBooms();
	}

	public void addBoomEffect(Plane p) {
		booms.add(new Point((int)p.getRect().getCenterX(), (int)p.getRect().getCenterY()));	
	}

	public void removeDeath() {
		Iterator<EnemyPlane> itep = enemyPlanes.iterator();
		while(itep.hasNext()) {
			EnemyPlane ep = itep.next();
			if(ep.isDead()) {
				score += ep.getKilledScore();
				enemyKilled++;
				if(enemyKilled == Config.ENEMY_AMOUNT)
					isWon = true;
				synchronized (enemyPlanes) {
					itep.remove();
				}
			}
		}
		for(int i = 0; i < 2; ++i) {
			if(myPlanes[i] != null){
				if(myPlanes[i].isDead()) {
					myPlanes[i] = null;
				}
			}
		}
	}

	public void removeOut() {
		Iterator<EnemyPlane> itep = enemyPlanes.iterator();
		Iterator<EnemyBullet> iteb = enemyBullets.iterator();
		Iterator<MyBullet> itmb = myBullets.iterator();
		while(itep.hasNext()) {
			if(itep.next().isOut()) {
				synchronized (enemyPlanes) {
					itep.remove();
				}	
			}
		}
		while(iteb.hasNext()) {
			if(iteb.next().isOut()) {
				synchronized (enemyBullets) {
					iteb.remove();
				}
			}
		}
		while(itmb.hasNext()) {
			if(itmb.next().isOut()) {
				synchronized (myBullets) {
					itmb.remove();
				}
			}
		}
	}

	public void updateStatus() {
		for(int i = 0; i < 2; ++i) {
			if(myPlanes[i] != null) {
				life[i] = myPlanes[i].getLife();
			}
			else
				life[i] = 0;
		}
		if(life[0] == 0 && life[1] == 0) {
			isFailed = true;
		}
		if(isWon || isFailed) {
			endGame();
		}
	}

	public void endGame() {
		gameTime += System.currentTimeMillis() - startTime;
		this.setGameState(GameState.G_OVER);
		gf.checkRank(score, gameTime);
		syncStatus();
		if(paintThread != null) {
			if(paintThread != null){
				synchronized (paintThread) {	
					paintThread.stop();
				}
			}
		}
		this.checkGameOver(this.getGraphics());
	}

	class PaintThread extends Thread {
		public void run() {
			while(true) {
				ServerGamePanel.this.repaint();
				try {
					Thread.sleep(Config.SERVER_REPAINT_INTERVAL);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}		
		}
	}

	class KeyProcessor extends KeyAdapter {
		public KeyProcessor() {
		}
		public void keyPressed(KeyEvent ke) {
			int kvx = 0, kvy = 0;
			if(ke.getKeyCode() == KeyEvent.VK_LEFT) {
				kvx = -1;
			}
			if(ke.getKeyCode() == KeyEvent.VK_RIGHT) {
				kvx = 1;
			}
			if(ke.getKeyCode() == KeyEvent.VK_UP) {
				kvy = -1;
			}
			if(ke.getKeyCode() == KeyEvent.VK_DOWN) {
				kvy = 1;
			}
			ServerGamePanel.this.setPlaneDir(kvx, kvy, true);
			if(ke.getKeyCode() == KeyEvent.VK_SPACE) {
				ServerGamePanel.this.shoot(true);
			}
		}
	}

	public void shoot(boolean isServer) {
		int index = isServer? 0:1;
		if(enShoot[index] && myPlanes[index] != null) {
			double centerX = myPlanes[index].getRect().getCenterX();
			double minY = myPlanes[index].getRect().getMinY();
			synchronized (myBullets) {
				myBullets.add(new MyBullet((int)centerX - Config.BULLET_WIDTH/2, (int)minY, ServerGamePanel.this));
			}
			shootClip.setFramePosition(0);
			shootClip.start();
			new ShootThread(isServer).start();
		}
	}

	public void setPlaneDir(int kvx, int kvy, boolean isServer) {
		int index = isServer? 0 : 1;
		if(myPlanes[index] != null) {
			myPlanes[index].move(kvx, kvy);
		}
		this.checkCollison();
		this.removeDeath();
		this.removeOut();
		this.updateStatus();
	}

	class ShootThread extends Thread {
		private boolean isServer;
		public ShootThread(boolean isServer) {
			this.isServer = isServer;
		}
		public void run() {
			int index = isServer? 0 : 1;
			if(myPlanes[index] != null) {
				enShoot[index] = false;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			enShoot[index] = true;
			return;
		}
	}

	public void setReady(boolean isReady, boolean isServer) {
		int index = isServer? 0 : 1;
		this.isReady[index] = isReady;
		if(this.isReady[0] == true && this.isReady[1] == true) {
			if(gState == GameState.G_PAUSE)
				this.resumeGame();
			else if(gState == GameState.G_READY || gState == GameState.G_OVER) {
				this.startGame();
			}
		}
		else {
			if(gState == GameState.G_PLAYING) {
				this.pauseGame();
			}
		}
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public void syncPos() {
		server.sendMsg(Config.SERVER_SYNC_MP_MSG + getMpPos());
		server.sendMsg(Config.SERVER_SYNC_NORMALPLANE_MSG + getEpPos(EnemyPlaneType.NORMAL_PLANE));
		server.sendMsg(Config.SERVER_SYNC_ENHANCEDPLANE_MSG + getEpPos(EnemyPlaneType.ENHANCED_PLANE));
		server.sendMsg(Config.SERVER_SYNC_SUICIDEPLANE_MSG + getEpPos(EnemyPlaneType.SUICIDE_PLANE));
		server.sendMsg(Config.SERVER_SYNC_MB_MSG + getMbPos());
		server.sendMsg(Config.SERVER_SYNC_EB_MSG + getEbPos());
	}

	public void syncStatus() {
		server.sendMsg(Config.SERVER_SYNC_STATUS_MSG + getStatusStr());
	}

	public void syncBooms() {
		server.sendMsg(Config.SERVER_SYNC_BOOM_MSG + getBoomsPos());
	}

	public String getMpPos() {
		StringBuffer msg = new StringBuffer();
		int count = 0;
		for(int i = 0; i < 2; ++i) {
			if(myPlanes[i] != null) {
				count++;
			}
		}
		msg.append('@');
		msg.append(count);
		for(int i = 0; i < 2; ++i) {
			if(myPlanes[i] != null) {
				msg.append('@');
				msg.append(myPlanes[i].getX());
				msg.append('@');
				msg.append(myPlanes[i].getY());
			}
		}
		return msg.toString();
	}

	public String getEpPos(EnemyPlaneType type) {
		StringBuffer msg = new StringBuffer();
		int count = 0;
		Iterator<EnemyPlane> itep = enemyPlanes.iterator();
		while(itep.hasNext()) {
			EnemyPlane ep = itep.next();
			if(ep.getType() == type) {
				count++;
			}
		}
		msg.append('@');
		msg.append(count);
		itep = enemyPlanes.iterator();
		while(itep.hasNext()) {
			EnemyPlane ep = itep.next();
			if(ep.getType() == type) {
				msg.append('@');
				msg.append(ep.getX());
				msg.append('@');
				msg.append(ep.getY());
			}
		}
		return msg.toString();
	}

	public String getMbPos() {
		StringBuffer msg = new StringBuffer();
		msg.append('@');
		msg.append(myBullets.size());
		Iterator<MyBullet> itmb = myBullets.iterator();
		while(itmb.hasNext()) {
			MyBullet mb = itmb.next();
			msg.append('@');
			msg.append(mb.getX());
			msg.append('@');
			msg.append(mb.getY());
		}
		return msg.toString();
	}

	public String getEbPos() {
		StringBuffer msg = new StringBuffer();
		msg.append('@');
		msg.append(enemyBullets.size());
		Iterator<EnemyBullet> iteb = enemyBullets.iterator();
		while(iteb.hasNext()) {
			EnemyBullet eb = iteb.next();
			msg.append('@');
			msg.append(eb.getX());
			msg.append('@');
			msg.append(eb.getY());
		}
		return msg.toString();
	}

	public String getStatusStr() {
		StringBuffer msg = new StringBuffer();
		int isWonInt = isWon? 1 : 0;
		int isFailedInt = isFailed? 1 : 0;
		msg.append('@');
		msg.append(isWonInt);
		msg.append('@');
		msg.append(isFailedInt);
		msg.append('@');
		msg.append(life[0]);
		msg.append('@');
		msg.append(life[1]);
		msg.append('@');
		msg.append(score);
		return msg.toString();
	}

	public String getBoomsPos() {
		StringBuffer msg = new StringBuffer();
		msg.append('@');
		msg.append(booms.size());
		Iterator<Point> itBooms = booms.iterator();
		while(itBooms.hasNext()) {
			Point eb = itBooms.next();
			msg.append('@');
			msg.append((int)eb.getX());
			msg.append('@');
			msg.append((int)eb.getY());
		}
		return msg.toString();
	}
}

