package com.happyshopping.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FTPUtil {

	private static final Logger logger = LoggerFactory.getLogger(FTPUtil.class);
	
	private static String ftpIP = PropertiesUtil.getProperty("ftp.server.ip", "192.168.174.128");
	private static String ftpUser = PropertiesUtil.getProperty("ftp.user", "ftpuser");
	private static String ftpPwd = PropertiesUtil.getProperty("ftp.pass", "19951210");
	private static int ftpPort = 21;
	
	private String ip;
	private int port;
	private String user;
	private String pwd;
	private FTPClient ftpClient;
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public FTPClient getFtpClient() {
		return ftpClient;
	}
	public void setFtpClient(FTPClient ftpClient) {
		this.ftpClient = ftpClient;
	}
	
	public FTPUtil(String ip, int port, String user, String pwd){
		this.ip 	= ip;
		this.port 	= port;
		this.user	= user;
		this.pwd	= pwd;
	}
	
	// connect ftp server
	private boolean connectFTPServer(String ip, int port, String user, String pwd){
		boolean flag= false;
		this.ftpClient = new FTPClient();
		try{
			this.ftpClient.connect(ip, port);
			flag = this.ftpClient.login(user, pwd);
		} catch(IOException e){
			logger.error("connecting FTP server occurs error",e);
		}
		return flag;
	}
	
	private boolean upload(String remotePath, List<File> fileList) throws IOException{
		boolean flag = true;
		FileInputStream fis = null;
		// connect FTP server
		if (this.connectFTPServer(this.ip, this.port, this.user, this.pwd)){
			try{
				// change directory
				System.out.println(this.ftpClient.changeWorkingDirectory(remotePath));
//				this.ftpClient.changeWorkingDirectory(remotePath);
				this.ftpClient.setBufferSize(1024);
				this.ftpClient.setControlEncoding("UTF-8");
				this.ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
				this.ftpClient.enterLocalPassiveMode();
				for (File fileItem : fileList){
					fis = new FileInputStream(fileItem);
					System.out.println(this.ftpClient.storeFile(fileItem.getName(), fis));
//					this.ftpClient.storeFile(fileItem.getName(), fis);
				}
			} catch(IOException e){
				logger.error("uploading file to FTP server occurs error",e);
				flag = false;
				e.printStackTrace();
			} finally {
				fis.close();
				this.ftpClient.disconnect();
			}
		}
		return flag;
	}
	
	public static boolean uploadToFTPServer(String remotePath, List<File> fileList) throws IOException{
		FTPUtil ftpUtil = new FTPUtil(ftpIP,ftpPort,ftpUser,ftpPwd);
		logger.info("start connecting FTP server");
		boolean res = ftpUtil.upload(remotePath, fileList);
		logger.info("finish uploading, disconnect FTP server");
		return res;
	}
	
	public static boolean uploadToFTPServer(List<File> fileList) throws IOException{
		FTPUtil ftpUtil = new FTPUtil(ftpIP,ftpPort,ftpUser,ftpPwd);
		logger.info("start connecting FTP server");
//		boolean res = ftpUtil.upload("img", fileList);
		boolean res = ftpUtil.upload("/", fileList);
		logger.info("finish uploading, disconnect FTP server");
		return res;
	}
}
