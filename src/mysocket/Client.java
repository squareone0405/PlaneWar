package mysocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import config.Config;
import config.GameState;
import gui.ClientGamePanel;
import gui.GameFrame;

public class Client {
	private Socket socket;  
	private PrintWriter writer;  
	private BufferedReader reader;  
	private MessageThread messageThread;
	private boolean isConnected = false;
	private GameFrame gf;
	private ClientGamePanel gp;

	public Client(GameFrame gf) {
		this.gf = gf;
	}

	public void setGamePanel(ClientGamePanel gp) {
		this.gp = gp;
	}

	public boolean connectServer(String ip, int port){
		if(isConnected) {
			return true;
		}
		try {
			socket = new Socket(ip, port);
			isConnected = true;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(gf, "连接失败", "错误",  
					JOptionPane.ERROR_MESSAGE); 
			e.printStackTrace();
			return false;
		}
		try {
			writer = new PrintWriter(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}  
		try {
			reader = new BufferedReader(new InputStreamReader(socket  
					.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		} 
		messageThread = new MessageThread(reader);
		messageThread.start();  
		return true;
	}

	class MessageThread extends Thread {
		private BufferedReader mReader;
		public MessageThread(BufferedReader mReader) {
			this.mReader = mReader;
		}
		public void run() {
			String message = null;
			while(true) {
				try {
					message = mReader.readLine();
					if(message == null) {
						return;
					}
					synchronized (gp) {
						if(message.equals(Config.SERVER_CONNECT_MSG)){
							isConnected = true;
							JOptionPane.showMessageDialog(gf, "连接成功", "提示",  
									JOptionPane.INFORMATION_MESSAGE); 
						}
						else if(message.equals(Config.SERVER_CLOSE_MSG)) {
							JOptionPane.showMessageDialog(gf, "服务器端下线", "提示",  
									JOptionPane.INFORMATION_MESSAGE);
							mReader.close();
							return;
						}
						else if(message.equals(Config.SERVER_START_MSG)) {
							JOptionPane.showMessageDialog(gf, "服务器端请求开始", "提示",  
									JOptionPane.INFORMATION_MESSAGE);
						}
						else if(message.equals(Config.SERVER_PAUSE_MSG)) {
							JOptionPane.showMessageDialog(gf, "服务器端请求暂停", "提示",  
									JOptionPane.INFORMATION_MESSAGE);
						}
						else if(message.equals(Config.SERVER_RESUME_MSG)) {
							JOptionPane.showMessageDialog(gf, "服务器端请求继续", "提示",  
									JOptionPane.INFORMATION_MESSAGE);
						}
						else if(message.equals(Config.SERVER_GAME_START_MSG)) {
							gp.startGame();
						}
						else if(message.equals(Config.SERVER_GAME_PAUSE_MSG)) {
							gp.pauseGame();
						}
						else if(message.equals(Config.SERVER_GAME_RESUME_MSG)) {
							gp.resumeGame();
						}
						else if(message.startsWith(Config.SERVER_SYNC_MP_MSG)) {
							String info = message.substring(6);
							String[] posInfo = info.split("\\@");
							int count = Integer.parseInt(posInfo[0]);
							int[] xArr = new int[count];
							int[] yArr = new int[count];
							for(int i = 0; i < count; ++i) {
								xArr[i] = Integer.parseInt(posInfo[2*i+1]);
								yArr[i] = Integer.parseInt(posInfo[2*i+2]);
							}
							gp.setMp(count, xArr, yArr);
						}
						else if(message.startsWith(Config.SERVER_SYNC_NORMALPLANE_MSG)) {
							String info = message.substring(6);
							String[] posInfo = info.split("\\@");
							int count = Integer.parseInt(posInfo[0]);
							int[] xArr = new int[count];
							int[] yArr = new int[count];
							for(int i = 0; i < count; ++i) {
								xArr[i] = Integer.parseInt(posInfo[2*i+1]);
								yArr[i] = Integer.parseInt(posInfo[2*i+2]);
							}
							gp.setNormalPlane(count, xArr, yArr);
						}
						else if(message.startsWith(Config.SERVER_SYNC_ENHANCEDPLANE_MSG)) {
							String info = message.substring(6);
							String[] posInfo = info.split("\\@");
							int count = Integer.parseInt(posInfo[0]);
							int[] xArr = new int[count];
							int[] yArr = new int[count];
							for(int i = 0; i < count; ++i) {
								xArr[i] = Integer.parseInt(posInfo[2*i+1]);
								yArr[i] = Integer.parseInt(posInfo[2*i+2]);
							}
							gp.setEnhancedPlane(count, xArr, yArr);
						}
						else if(message.startsWith(Config.SERVER_SYNC_SUICIDEPLANE_MSG)) {
							String info = message.substring(6);
							String[] posInfo = info.split("\\@");
							int count = Integer.parseInt(posInfo[0]);
							int[] xArr = new int[count];
							int[] yArr = new int[count];
							for(int i = 0; i < count; ++i) {
								xArr[i] = Integer.parseInt(posInfo[2*i+1]);
								yArr[i] = Integer.parseInt(posInfo[2*i+2]);
							}
							gp.setSuicidePlane(count, xArr, yArr);
						}
						else if(message.startsWith(Config.SERVER_SYNC_MB_MSG)) {
							String info = message.substring(6);
							String[] posInfo = info.split("\\@");
							int count = Integer.parseInt(posInfo[0]);
							int[] xArr = new int[count];
							int[] yArr = new int[count];
							for(int i = 0; i < count; ++i) {
								xArr[i] = Integer.parseInt(posInfo[2*i+1]);
								yArr[i] = Integer.parseInt(posInfo[2*i+2]);
							}
							gp.setMb(count, xArr, yArr);
						}
						else if(message.startsWith(Config.SERVER_SYNC_EB_MSG)) {
							String info = message.substring(6);
							String[] posInfo = info.split("\\@");
							int count = Integer.parseInt(posInfo[0]);
							int[] xArr = new int[count];
							int[] yArr = new int[count];
							for(int i = 0; i < count; ++i) {
								xArr[i] = Integer.parseInt(posInfo[2*i+1]);
								yArr[i] = Integer.parseInt(posInfo[2*i+2]);
							}
							gp.setEb(count, xArr, yArr);
						}
						else if(message.startsWith(Config.SERVER_SYNC_BOOM_MSG)) {
							String info = message.substring(6);
							String[] posInfo = info.split("\\@");
							int count = Integer.parseInt(posInfo[0]);
							int[] xArr = new int[count];
							int[] yArr = new int[count];
							for(int i = 0; i < count; ++i) {
								xArr[i] = Integer.parseInt(posInfo[2*i+1]);
								yArr[i] = Integer.parseInt(posInfo[2*i+2]);
							}
							gp.setBooms(count, xArr, yArr);
						}
						else if(message.startsWith(Config.SERVER_SYNC_STATUS_MSG)) {
							String info = message.substring(6);
							String[] statusInfo = info.split("\\@");
							boolean isWon = (statusInfo[0].equals("1"));
							boolean isFailed = (statusInfo[1].equals("1"));
							int[] life = new int[2];
							life[0] = Integer.parseInt(statusInfo[2]);
							life[1] = Integer.parseInt(statusInfo[3]);
							int score = Integer.parseInt(statusInfo[4]);
							if(gp != null)
								gp.setStatus(isWon, isFailed, life, score);
						}
					}
				} catch (IOException e) {
					JOptionPane.showMessageDialog(gf, "连接断开", "提示",  
							JOptionPane.INFORMATION_MESSAGE);
					try {
						mReader.close();
						return;
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}

	public boolean isClientConneted() {
		return isConnected;
	}

	public boolean sendMsg(String message) {  
		if(isConnected){
			writer.println(message);
			writer.flush();
			return true;
		}
		else
			return false;
	}

	public boolean closeConnection() {  
		try {  
			sendMsg(Config.CLIENT_CLOSE_MSG);
			if(messageThread != null)
				messageThread.stop();
			/*if (reader != null) {  
				reader.close();  
			}  
			if (writer != null) {  
				writer.close();  
			}  */
			if (socket != null) {  
				socket.close();  
			}  
			isConnected = false;  
			return true;  
		} catch (IOException e1) {  
			e1.printStackTrace();  
			isConnected = true;  
			return false;  
		}  
	}  
}
