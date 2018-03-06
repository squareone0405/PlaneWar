package gui;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import config.Config;
import config.GameState;
import planes.EnemyBullet;
import planes.EnemyPlane;
import planes.MyBullet;
import planes.MyPlane;

public class GamePanel extends JPanel {
	protected volatile ArrayList<EnemyBullet> enemyBullets;
	protected volatile ArrayList<EnemyPlane> enemyPlanes;
	protected volatile ArrayList<MyBullet> myBullets;
	protected volatile MyPlane[] myPlanes;
	protected volatile ArrayList<Point> booms;
	protected volatile int score = 0;
	protected volatile int life[];

	protected volatile boolean isWon = false;
	protected volatile boolean isFailed = false;
	protected volatile GameState gState;

	protected Clip gameMusicClip;
	protected Clip shootClip;
	protected Clip boomClip;

	protected GameFrame gf;

	public GamePanel() {
		super();
		this.setDoubleBuffered(true);
		this.setOpaque(false);

		enemyBullets = new ArrayList<EnemyBullet>();
		myBullets = new ArrayList<MyBullet>();
		enemyPlanes = new ArrayList<EnemyPlane>();
		myPlanes = new MyPlane[2];
		booms = new ArrayList<Point>();
		life = new int[2];

		gState = GameState.G_READY;
		
		try {
			File gameMusic = new File(Config.GAME_MUSIC);
			AudioInputStream ais_gameMusic = AudioSystem.getAudioInputStream(gameMusic);
			gameMusicClip = AudioSystem.getClip();
			gameMusicClip.open(ais_gameMusic);
			
			File shootMusic = new File(Config.BULLET_MUSIC);
			AudioInputStream ais_shootMusic = AudioSystem.getAudioInputStream(shootMusic);
			shootClip = AudioSystem.getClip();
			shootClip.open(ais_shootMusic);
			
			File boomMusic = new File(Config.BOOM_MUSIC);
			AudioInputStream ais_boomMusic = AudioSystem.getAudioInputStream(boomMusic);
			boomClip = AudioSystem.getClip();
			boomClip.open(ais_boomMusic);
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e1) {
			e1.printStackTrace();
		}
		
	}

	public void setGameFrame(GameFrame gf) {
		this.gf = gf;
	}

	public void setGameState(GameState gState) {
		if(gState == GameState.G_PLAYING) { 
			gameMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
		}
		else {
			gameMusicClip.stop();
		}
		
		if(gState == GameState.G_PAUSE) {
			gf.setIsPause(false);
		}
		else {
			gf.setIsPause(true);
		}
		this.gState = gState;
	}

	public GameState getGameState() {
		return gState;
	} 

	public void drawComponent(Graphics g) { 
		Iterator<EnemyPlane> itep = enemyPlanes.iterator();
		Iterator<EnemyBullet> iteb = enemyBullets.iterator();
		Iterator<MyBullet> itmb = myBullets.iterator();
		try{
			while(itep.hasNext()) {
				synchronized (this.enemyPlanes) {
					itep.next().draw(g);
				}
			}
			for(int i = 0; i < 2; ++i) {
				if(myPlanes[i] != null) {
					myPlanes[i].draw(g);
				}
			}
			while(iteb.hasNext()) {
				synchronized (this.enemyBullets) {
					iteb.next().draw(g);
				}
			}
			while(itmb.hasNext()) {
				synchronized (this.myBullets) {
					itmb.next().draw(g);
				}
			}
		}catch(java.util.ConcurrentModificationException e) {
			System.out.println("ConcurrentModificationException");
			return;
		}
	}

	public void drawBooms(Graphics g) { 
		Image icon = new ImageIcon(Config.BOOM_IMG).getImage();
		Graphics2D g2d = (Graphics2D) g;
		Iterator<Point> itBooms = booms.iterator();
		while(itBooms.hasNext()) {
			Point boomPoint = itBooms.next();
			g2d.drawImage(icon, ((int)boomPoint.getX() - icon.getWidth(this)/2), 
					((int)boomPoint.getX() - icon.getHeight(this)/2), icon.getWidth(this), icon.getHeight(this), this);
			boomClip.setFramePosition(0);
			boomClip.start();
		}
	}

	public void drawStatus(Graphics g) {
		Font font = new Font("微软雅黑", Font.BOLD, 20);
		g.setFont(font);	
		g.drawString(String.valueOf(score), 5, 25);
		for(int i = 0; i < life[0]; ++i) {
			Image icon = new ImageIcon(Config.lIFE_IMG).getImage();
			Graphics2D g2d = (Graphics2D) g;
			g2d.drawImage(icon, Config.LIFE_GAP * i, Config.LIFE_Y, icon.getWidth(this), icon.getHeight(this), this);
		}
		for(int i = 0; i < life[1]; ++i) {
			Image icon = new ImageIcon(Config.lIFE_IMG).getImage();
			Graphics2D g2d = (Graphics2D) g;
			g2d.drawImage(icon, Config.PANEL_WIDTH - Config.LIFE_GAP * (i + 1), Config.LIFE_Y, icon.getWidth(this), icon.getHeight(this), this);
		}
		checkGameOver(g);
	}
	
	public void checkGameOver(Graphics g) {
		if(isFailed) {
			JOptionPane.showMessageDialog(gf, "失败", "消息",  
					JOptionPane.INFORMATION_MESSAGE); 
			setGameState(GameState.G_OVER);
			g.clearRect(0, 0, this.getWidth(), this.getHeight());
			gf.initGamePanel();
		}
		if(isWon) {
			JOptionPane.showMessageDialog(gf, "获得胜利", "消息",  
					JOptionPane.INFORMATION_MESSAGE); 
			setGameState(GameState.G_OVER);
			g.clearRect(0, 0, this.getWidth(), this.getHeight());
			gf.initGamePanel();
		}
	}
}
