package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.UIManager;

import config.*;
import mysocket.Client;
import mysocket.Server;

public class GameFrame extends JFrame {

	private ButtonGroup serverChooser;
	private JRadioButton serverBtn;
	private JRadioButton clientBtn;
	private JButton connectBtn;
	private JButton startBtn;
	private JButton pauseBtn;
	private JButton exitBtn;
	private JButton rankBtn;
	private GamePanel gamePanel;
	private JPanel optionPanel;
	private RankPanel rankPanel;
	private JTextField ipText;
	private JTextField portText;

	private boolean isServer = true;

	private Server server;
	private Client client;

	public GameFrame() {
		super();
		this.setSize(Config.FRAME_WIDTH, Config.FRAME_HEIGHT);
		this.setTitle("PlaneWar");
		ImageIcon icon = new ImageIcon(Config.LOGO_IMG);
		this.setIconImage(icon.getImage());
		this.setLayout(null);

		((JPanel) this.getContentPane()).setOpaque(false);
		this.setBackground(new Color(200, 200, 200));

		Font font = new Font("微软雅黑", Font.BOLD, 15);
		UIManager.put("Button.font", font);		
		UIManager.put("Button.background", new Color(200,200,200));
		UIManager.put("Label.font", font);		
		UIManager.put("Label.background", new Color(230,230,230));

		serverChooser = new ButtonGroup();
		serverBtn = new JRadioButton("创建服务器");
		clientBtn = new JRadioButton("创建客户端");
		serverChooser.add(serverBtn);
		serverChooser.add(clientBtn);
		ipText = new JTextField("127.0.0.1");
		portText = new JTextField("7458");
		connectBtn = new JButton("连接");
		startBtn = new JButton("开始游戏");
		pauseBtn = new JButton("暂停游戏");
		exitBtn = new JButton("退出游戏");
		rankBtn = new JButton("排行榜");

		optionPanel = new JPanel();
		optionPanel.setBounds((int) (Config.PANEL_WIDTH), 0, Config.FRAME_WIDTH - Config.PANEL_WIDTH, Config.FRAME_HEIGHT);
		optionPanel.setLayout(null);
		optionPanel.add(serverBtn);
		optionPanel.add(clientBtn);
		optionPanel.add(ipText);
		optionPanel.add(portText);
		optionPanel.add(connectBtn);
		optionPanel.add(startBtn);
		optionPanel.add(pauseBtn);
		optionPanel.add(exitBtn);
		optionPanel.add(rankBtn);
		serverBtn.setBounds(Config.OPTION_X,Config.OPTION_Y + Config.OPTION_GAP * 1,Config.OPTION_WIDTH, Config.OPTION_HEIGHT);
		serverBtn.setBounds(Config.OPTION_X,Config.OPTION_Y + Config.OPTION_GAP * 1,Config.OPTION_WIDTH, Config.OPTION_HEIGHT);
		clientBtn.setBounds(Config.OPTION_X,Config.OPTION_Y + Config.OPTION_GAP * 2,Config.OPTION_WIDTH, Config.OPTION_HEIGHT);
		ipText.setBounds(Config.OPTION_X,Config.OPTION_Y + Config.OPTION_GAP * 3,Config.OPTION_WIDTH, Config.OPTION_HEIGHT);
		portText.setBounds(Config.OPTION_X,Config.OPTION_Y + Config.OPTION_GAP * 4,Config.OPTION_WIDTH, Config.OPTION_HEIGHT);
		connectBtn.setBounds(Config.OPTION_X,Config.OPTION_Y + Config.OPTION_GAP * 5,Config.OPTION_WIDTH, Config.OPTION_HEIGHT);
		startBtn.setBounds(Config.OPTION_X,Config.OPTION_Y + Config.OPTION_GAP * 6,Config.OPTION_WIDTH, Config.OPTION_HEIGHT);
		pauseBtn.setBounds(Config.OPTION_X,Config.OPTION_Y + Config.OPTION_GAP * 7,Config.OPTION_WIDTH, Config.OPTION_HEIGHT);
		exitBtn.setBounds(Config.OPTION_X,Config.OPTION_Y + Config.OPTION_GAP * 8,Config.OPTION_WIDTH, Config.OPTION_HEIGHT);
		rankBtn.setBounds(Config.OPTION_X,Config.OPTION_Y + Config.OPTION_GAP * 9,Config.OPTION_WIDTH, Config.OPTION_HEIGHT);
		this.add(optionPanel);

		ActionListener serverListener = 
				new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				serverHandle();
			}
		};
		serverBtn.addActionListener(serverListener);

		ActionListener clientListener = 
				new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				clientHandle();
			}
		};
		clientBtn.addActionListener(clientListener);

		ActionListener connectListener = 
				new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				connectHandle();
			}
		};
		connectBtn.addActionListener(connectListener);

		ActionListener startListener = 
				new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				startHandle();
			}
		};
		startBtn.addActionListener(startListener);

		ActionListener pauseListener = 
				new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				pauseHandle();
			}
		};
		pauseBtn.addActionListener(pauseListener);

		ActionListener exitListener = 
				new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				exitHandle();
			}
		};
		exitBtn.addActionListener(exitListener);

		ActionListener rankListener = 
				new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				showRank();
			}
		};
		rankBtn.addActionListener(rankListener);

		this.addWindowListener(new WindowAdapter() {  
			public void windowClosing(WindowEvent e) {  
				if(isServer && server != null) {
					server.closeServer();
				}
				else if(!isServer && client != null) {
					client.closeConnection();
				}
				System.exit(0);
			}  
		});

		this.setVisible(true);
		this.setResizable(false);
		this.setLocationRelativeTo(null); 
	}

	public void serverHandle() {
		if(isConnected()) {
			JOptionPane.showMessageDialog(this, "已连接", "警告",  
					JOptionPane.WARNING_MESSAGE); 
			return;
		}
		isServer = true;
		ipText.setText("127.0.0.1");
		ipText.setEditable(false);
		server = new Server(this);
	}

	public void clientHandle() {
		if(isConnected()) {
			JOptionPane.showMessageDialog(this, "已连接", "警告",  
					JOptionPane.WARNING_MESSAGE); 
			return;
		}
		isServer = false;
		ipText.setEditable(true);
		client = new Client(this);
	}

	public void connectHandle() {
		if(isConnected()) {
			JOptionPane.showMessageDialog(this, "已连接", "警告",  
					JOptionPane.WARNING_MESSAGE); 
			return;
		}
		int port = 0;
		try {
			port = Integer.parseInt(portText.getText());
		} catch(java.lang.NumberFormatException e1) {
			JOptionPane.showMessageDialog(this, "请输入正确的端口号", "警告",  
					JOptionPane.WARNING_MESSAGE); 
		}
		if(isServer && server != null) {
			server.buildServer(port);
			initGamePanel();
			return;
		}
		else if(!isServer && client != null) {
			client.connectServer(ipText.getText(), port);
			initGamePanel();
			return;
		}
		JOptionPane.showMessageDialog(this, "请先选择创建服务器或创建客户端", "警告",  
				JOptionPane.WARNING_MESSAGE); 
	}

	public void startHandle() {
		if(isConnected()) {
			if(isServer) {
				((ServerGamePanel) gamePanel).setReady(true, isServer);
				server.sendMsg(Config.SERVER_START_MSG);
			}
			else {
				client.sendMsg(Config.CLIENT_START_MSG);
			}			
			if(rankPanel != null) {
				rankPanel.setVisible(false);
			}
			gamePanel.setVisible(true);
			gamePanel.requestFocus();
			this.repaint();
		}
		else 
			JOptionPane.showMessageDialog(this, "尚未连接", "警告",  
					JOptionPane.WARNING_MESSAGE); 
	}

	public void pauseHandle() {
		if(gamePanel.getGameState() == GameState.G_PAUSE) {
			if(isServer) {
				((ServerGamePanel) gamePanel).setReady(true, isServer);
				server.sendMsg(Config.SERVER_RESUME_MSG);
			}
			else {
				client.sendMsg(Config.CLIENT_RESUME_MSG);
			}	
			if(rankPanel != null)
				rankPanel.setVisible(false);
			this.repaint();
		}
		else if(gamePanel.getGameState() == GameState.G_PLAYING) {
			if(isServer) {
				((ServerGamePanel) gamePanel).setReady(false, isServer);
				server.sendMsg(Config.SERVER_PAUSE_MSG);
			}
			else {
				client.sendMsg(Config.CLIENT_PAUSE_MSG);
			}		
		}
	}

	public void exitHandle() {
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING) );
	}

	public void showRank() {
		if(gamePanel != null) {
			if(gamePanel.getGameState() == GameState.G_PAUSE 
					|| gamePanel.getGameState() == GameState.G_PLAYING) {
				return;
			}
			gamePanel.setVisible(false);
		}
		ArrayList<Integer> scores = new ArrayList<Integer>();
		ArrayList<Long> times = new ArrayList<Long>();
		getRank(scores, times);
		rankPanel = new RankPanel(scores);
		rankPanel.setBounds(0, 0, Config.PANEL_WIDTH, Config.FRAME_HEIGHT);
		this.add(rankPanel);
		rankPanel.setVisible(true);
		this.repaint();
	}

	public void initGamePanel() {
		if(isServer) {
			gamePanel = new ServerGamePanel();	
			((ServerGamePanel) gamePanel).setServer(server);
			server.setGamePanel((ServerGamePanel) gamePanel);
		}
		else {
			gamePanel = new ClientGamePanel();
			((ClientGamePanel) gamePanel).setClient(client);
			client.setGamePanel((ClientGamePanel) gamePanel);
		}
		gamePanel.setGameFrame(this);
		gamePanel.setBounds(0, 0, Config.PANEL_WIDTH, Config.FRAME_HEIGHT);
		this.add(gamePanel);
		gamePanel.setFocusable(true);
		gamePanel.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {	
			}
			@Override
			public void focusLost(FocusEvent arg0) {
				if(isConnected()) {
					gamePanel.requestFocus();
				}
			}
		});
		gamePanel.requestFocus();
		this.repaint();
	}

	public boolean isConnected() {
		if(isServer && server != null) {
			return server.isServerConneted();
		}
		if(!isServer && client != null) {
			return client.isClientConneted();
		}
		return false;
	}

	public void setIsPause(boolean isPause) {
		if(isPause) {
			pauseBtn.setText("暂停游戏");
		}
		else {
			pauseBtn.setText("继续游戏");
		}
	}

	public void checkRank(int score, long gameTime) {
		if(gamePanel != null) {
			if(gamePanel.getGameState() == GameState.G_PLAYING) {
				return;
			}
		}
		ArrayList<Integer> scores = new ArrayList<Integer>();
		ArrayList<Long> times = new ArrayList<Long>();
		getRank(scores, times);
		if(score > scores.get(Config.HIGH_SCORE_AMOUNT - 1)
				||(score == scores.get(Config.HIGH_SCORE_AMOUNT - 1) && gameTime < times.get(Config.HIGH_SCORE_AMOUNT - 1))) {
			for(int i = 0; i < Config.HIGH_SCORE_AMOUNT; ++i) {
				if(score > scores.get(i)
						||(score == scores.get(i) && gameTime < times.get(i))) {
					scores.add(i, score);
					times.add(i, gameTime);
					scores.remove(Config.HIGH_SCORE_AMOUNT);
					times.remove(Config.HIGH_SCORE_AMOUNT);
					break;
				}
			}
			setRank(scores, times);
			JOptionPane.showMessageDialog(this, "新纪录", "消息",  
					JOptionPane.INFORMATION_MESSAGE); 
		}
	}

	public void getRank(ArrayList<Integer> scores, ArrayList<Long> times) {
		scores.clear();
		times.clear();
		File file = new File("rank.txt");    
		if  (!file .exists()  && !file .isDirectory()) {    
			try {
				FileWriter fw = new FileWriter(file);
				for(int i = 0; i < Config.HIGH_SCORE_AMOUNT; ++i) {
					fw.write("0");
					fw.write("\r\n");
					fw.write("0");
					fw.write("\r\n");
				}
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Scanner sc;
		try {
			sc = new Scanner(file);
			while(sc.hasNextLine()){
				int score = Integer.parseInt(sc.nextLine());
				long time = Long.parseLong(sc.nextLine());
				scores.add(score);
				times.add(time);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void setRank(ArrayList<Integer> scores, ArrayList<Long> times) {
		File file =new File("rank.txt");    
		FileWriter fw;
		try {
			fw = new FileWriter(file);
			for(int i = 0; i < Config.HIGH_SCORE_AMOUNT; ++i) {
				fw.write(scores.get(i).toString());
				fw.write("\r\n");
				fw.write((times.get(i)).toString());
				fw.write("\r\n");
			}
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}


