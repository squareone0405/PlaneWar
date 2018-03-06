package mysocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JOptionPane;

import config.Config;
import gui.GameFrame;
import gui.ServerGamePanel;

public class Server {
	private ServerSocket server;  
	private ClientThread clientThread;
	private ServerThread serverThread;
	private boolean isStarted = false; 
	private boolean isConnected = false;
	private GameFrame gf;
	private ServerGamePanel gp;

	public Server(GameFrame gf) {
		this.gf = gf;
	}
	
	public void setGamePanel(ServerGamePanel gp) {
		this.gp = gp;
	}

	public void buildServer(int port) {
		try {
			server = new ServerSocket(port);
			serverThread = new ServerThread(server);
			serverThread.start();
			isStarted = true;
			JOptionPane.showMessageDialog(gf, "服务器创建成功", "提示",  
					JOptionPane.INFORMATION_MESSAGE); 
		} catch (BindException e) {  
			isStarted = false;  
			JOptionPane.showMessageDialog(gf, "端口号已被占用，请换一个！", "错误",  
					JOptionPane.ERROR_MESSAGE); 
		} catch (Exception e1) {  
			e1.printStackTrace();  
			isStarted = false;  
			JOptionPane.showMessageDialog(gf, "启动服务器异常！", "错误",  
					JOptionPane.ERROR_MESSAGE);
		}
	}

	class ServerThread extends Thread {
		private ServerSocket serverSocket; 
		public ServerThread(ServerSocket serverSocket) {  
			this.serverSocket = serverSocket;  
		}  
		public void run(){
			while(true) {
				try {
					Socket client = serverSocket.accept();
					JOptionPane.showMessageDialog(gf, "客户端连接成功", "提示",  
							JOptionPane.INFORMATION_MESSAGE); 
					clientThread = new ClientThread(client);
					clientThread.start();
					sendMsg(Config.SERVER_CONNECT_MSG); 
				} catch (IOException e) {
					e.printStackTrace();
				} 				
			}
		}
	}

	class ClientThread extends Thread {
		Socket cSocket;
		BufferedReader cReader;  
		PrintWriter cWriter; 
		public ClientThread(Socket cSocket) {
			this.cSocket = cSocket;
			try {
				cReader = new BufferedReader(new InputStreamReader(cSocket  
						.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}  
			try {
				cWriter = new PrintWriter(cSocket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			} 
			isConnected = true;
		}
		public void run() {
			String message = null;
			while(true) {
				try {
					message = cReader.readLine();
					if(message == null) {
						return;
					}
					if(message.equals(Config.CLIENT_CONNECT_MSG)) {
						
					}
					else if(message.equals(Config.CLIENT_CLOSE_MSG)) {
						JOptionPane.showMessageDialog(gf, "客户端端下线", "提示",  
								JOptionPane.INFORMATION_MESSAGE);
						cReader.close();
						return;
					}
					else if(message.equals(Config.CLIENT_START_MSG)) {
						JOptionPane.showMessageDialog(gf, "客户端请求开始", "提示",  
								JOptionPane.INFORMATION_MESSAGE);
						gp.setReady(true, false);
					}
					else if(message.equals(Config.CLIENT_PAUSE_MSG)) {
						JOptionPane.showMessageDialog(gf, "客户端请求暂停", "提示",  
								JOptionPane.INFORMATION_MESSAGE);
						gp.setReady(false, false);
					}
					else if(message.equals(Config.CLIENT_RESUME_MSG)) {
						JOptionPane.showMessageDialog(gf, "客户端请求继续", "提示",  
								JOptionPane.INFORMATION_MESSAGE);
						gp.setReady(true, false);
					}
					else if(message.startsWith(Config.CLIENT_MOVE_MSG)) {
						char dir = message.charAt(5);
						int kvx = 0, kvy = 0;
						switch (dir) {
						case 'u':
							kvy = -1;
							break;
						case 'd':
							kvy = 1;
							break;
						case 'l':
							kvx = -1;
							break;
						case 'r':
							kvx = 1;
							break;
						}
						gp.setPlaneDir(kvx, kvy, false);
					}
					else if(message.equals(Config.CLIENT_SHOOT_MSG)) {
						gp.shoot(false);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}      		
			}
		}
	}

	public boolean isServerConneted() {
		return isConnected;
	}

	public boolean isServerStarted() {
		return isStarted;
	}

	public boolean sendMsg(String message) {  
		if(isConnected){
			clientThread.cWriter.println(message);
			clientThread.cWriter.flush();
			return true;
		}
		else
			return false;
	} 

	public void closeServer() {  
		try {  
			if (serverThread != null)  {
				serverThread.stop();
			}
			if(clientThread != null) {
				clientThread.stop();
				sendMsg(Config.SERVER_CLOSE_MSG);
				if(clientThread.cSocket != null) {
					clientThread.cSocket.close();
				}
			}
			isStarted = false;  
		} catch (IOException e) {  
			e.printStackTrace();  
			isStarted = true;  
		}  
	}
}
