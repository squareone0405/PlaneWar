package config;

public class Config {
	//repaint
	public final static int SERVER_REPAINT_INTERVAL = 50;
	public final static int CLIENT_REPAINT_INTERVAL = 80;
	
	//rank
	public final static int HIGH_SCORE_AMOUNT = 5;
	
	//enemy
	public final static int ENEMY_AMOUNT = 20;
	public final static int ENEMY_GEN = 100;
	public final static int BULLET_GEN = 100;
	
	//size
	public final static int FRAME_HEIGHT = 800;
	public final static int FRAME_WIDTH = 700;
	public final static int PANEL_WIDTH = 520;
	
	public final static int RANK_LABEL_X = 160;
	public final static int RANK_LABEL_Y = 120;
	public final static int RANK_LABEL_WIDTH = 200;
	public final static int RANK_LABEL_HEIGHT = 40;
	public final static int RANK_X = 200;
	public final static int RANK_Y = 250;
	public final static int RANK_WIDTH = 120;
	public final static int RANK_HEIGHT = 40;
	public final static int RANK_GAP = 80;
	
	public final static int BULLET_WIDTH = 48;
	
	public final static int LIFE_Y = 750;
	public final static int LIFE_GAP = 20;
	
	public final static int PLANE_Y = 600;
	public final static int PLANE0_X = 20;
	public final static int PLANE1_X = 400;
	
	public final static int OPTION_X = 30;
	public final static int OPTION_Y = 50;
	public final static int OPTION_GAP = 60;
	public final static int OPTION_WIDTH = 120;
	public final static int OPTION_HEIGHT = 30;
	public final static int INPUT_HEIGHT = 20;
	
	//speed
	public final static int MY_SPEED = 6;
	public final static int ENEMY_SPEED = 2;
	public final static int BULLET_SPEED = 7;
	public final static int ENEMY_X_SPEED = 4;
	
	//life
	public final static int MY_LIFE = 5;
	public final static int SUICIDE_LIFE = 1;
	public final static int NORMAL_LIFE = 1;
	public final static int ENHANCED_LIFE = 2;
	
	//killed score
	public final static int SUICIDE_SCORE = 1;
	public final static int NORMAL_SCORE = 1;
	public final static int ENHANCED_SCORE = 3;
	
	//img
	public final static String LOGO_IMG = "images/logo.png";
	public final static String MYPLANE_IMG = "images/myplane.png";
	public final static String NORMAL_PLENE_IMG = "images/normalplane.png";
	public final static String SUICIDE_IMG = "images/suicideplane.png";
	public final static String EHANCED_PLENE_IMG = "images/enhancedplane.png";
	public final static String MY_BULLET_IMG = "images/mybullet.png";
	public final static String ENEMY_BULLET_IMG = "images/enemybullet.png";
	public final static String lIFE_IMG = "images/life.png";
	public final static String BOOM_IMG = "images/boom.png";
	
	//sound
	public final static String GAME_MUSIC = "sound/game_music.wav";
	public final static String BULLET_MUSIC = "sound/fire_bullet.wav";
	public final static String BOOM_MUSIC = "sound/use_bomb.wav";
	
	//msg
	public final static String SERVER_CONNECT_MSG = "S_CON";
	public final static String SERVER_CLOSE_MSG = "S_CLO";
	public final static String SERVER_START_MSG = "S_STA";
	public final static String SERVER_PAUSE_MSG = "S_PAU";
	public final static String SERVER_RESUME_MSG = "S_RES";
	
	public final static String SERVER_GAME_START_MSG = "S_G_S";
	public final static String SERVER_GAME_PAUSE_MSG = "S_G_P";
	public final static String SERVER_GAME_RESUME_MSG = "S_G_R";
	
	public final static String SERVER_SYNC_MP_MSG = "S_SMP";
	public final static String SERVER_SYNC_NORMALPLANE_MSG = "S_SNP";
	public final static String SERVER_SYNC_ENHANCEDPLANE_MSG = "S_SEP";
	public final static String SERVER_SYNC_SUICIDEPLANE_MSG = "S_SSP";
	public final static String SERVER_SYNC_MB_MSG = "S_SMB";
	public final static String SERVER_SYNC_EB_MSG = "S_SEB";
	public final static String SERVER_SYNC_BOOM_MSG = "S_SBO";
	public final static String SERVER_SYNC_STATUS_MSG = "S_SST";
	
	public final static String CLIENT_CONNECT_MSG = "C_CON";
	public final static String CLIENT_CLOSE_MSG = "C_CLO";
	public final static String CLIENT_START_MSG = "C_STA";
	public final static String CLIENT_PAUSE_MSG = "C_PAU";
	public final static String CLIENT_RESUME_MSG = "C_RES";
	public final static String CLIENT_MOVE_MSG = "C_MOV";
	public final static String CLIENT_SHOOT_MSG = "C_SHO";
}
