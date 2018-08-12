package com.happyshopping.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.happyshopping.service.IFileService;
import com.happyshopping.util.FTPUtil;

@Service("iFileService")
public class FileServiceImpl implements IFileService {

	private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
	
	public String upload(MultipartFile file, String path) {
		String fileName = file.getOriginalFilename();
		// get extension-name,ex:jpg/png...
		String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);
		// generate a new file name
		String newFileName = UUID.randomUUID().toString()+"."+fileExtensionName;
		// do logging
		logger.info("start uploading,file_name:{},file_path:{},new_file_name:{}",
				fileName, path, newFileName);
		
		// make directories in Tomcat server
		File fileDir = new File(path);
		if (!fileDir.exists()){
			fileDir.setWritable(true);
			fileDir.mkdirs();
		}
		
		// create an temporary empty file with extension-name in directory made above in Tomcat server
		// for example:path is D:/Tomcat/eclipse-mars-tomcat-8.0.52/wtpwebapps/happyshopping/upload
		// newFileName is 79099ce3-aab1-4b11-9019-e29d243dfdf5.jpg, we will create the new file there
		File tmpFile = new File(path, newFileName);
		
		try{
			// copy content of original file into targetFile
			file.transferTo(tmpFile);
			
			// upload the targetFile to FTP server
			FTPUtil.uploadToFTPServer(Lists.newArrayList(tmpFile));
			
			// delete the temporary file in Tomcat server for saving storage
			tmpFile.delete();
		} catch(IOException e){
			logger.error("File uploading occurs error",e);
			return null;
		}
		return newFileName;
	}

}
