package com.eec.fileuploadservice.service.impl;

import java.awt.Image;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;

import org.apache.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.util.concurrent.ListenableFuture;

import com.eec.data.FilePathData;
import com.eec.data.FileUploadData;
import com.eec.service.FileUploadService;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

@Service
@RefreshScope
public class FileUploadServiceImpl implements FileUploadService {

	private static final Logger logger = LoggerFactory.getLogger(FileUploadServiceImpl.class);

	@Value("${uploadPath.base}")
	private String uploadPath;

	@Value("${uploadPath.original}")
	private String original;

	@Value("${uploadPath.thumbnail}")
	private String thumb;

	@Override
	public String delete(FilePathData fpd) {
		String result = "";
		try {
			result = deleteFile(original, fpd.getPath()).get();
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		} catch (ExecutionException e) {
			logger.error(e.getMessage(), e);
		}
		deleteFile(thumb, fpd.getPath());
		return result;
	}

	@Override
	public byte[] download(FilePathData fpd) {
		String pathString = uploadPath + original + fpd.getPath();
		File file = new File(pathString);
		byte[] bytes = new byte[0];
		if (!file.exists()) {
	        return bytes;
	    }
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			bytes = new byte[is.available()];
			is.read(bytes);
		} catch (FileNotFoundException e) {
			logger.error("file not found:", e);
		} catch (IOException e) {
			logger.error("io exception:", e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				logger.error("io close error:", e);
			}
		}
		
		return bytes;
	}

	@Override
	public String fileUpload(FileUploadData fud) {
		String fileNameString = "temp";

		try {
			fileNameString = new String(fud.getFileName().getBytes(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("filename error: ", e);
		}
		String relativePath = makeRelativePath(fud.getUserId(), fileNameString);
		String destFilePathString = uploadPath + original + relativePath;
		File dest = new File(destFilePathString);
		if (!dest.getParentFile().exists()) {
			dest.getParentFile().mkdirs();
		}
		OutputStream os = null;
		BufferedOutputStream bos = null;
		try {
			os = new FileOutputStream(dest);
			bos = new BufferedOutputStream(os);
			bos.write(fud.getFile());
		} catch (FileNotFoundException e) {
			logger.error("file not found:", e);
			return e.getLocalizedMessage();
		} catch (IOException e) {
			logger.error("io broken:", e);
			return e.getLocalizedMessage();
		} finally {
			try {
				bos.close();
				os.close();
			} catch (IOException e) {
				logger.error("io close error:", e);
			}
		}
		InputStream is = null;
		try {
			is = new FileInputStream(dest);
		} catch (FileNotFoundException e) {
			logger.error("file not exist:", e);
		}
		boolean fileType = isImage(is);
		if (fileType) {
			makeImageThumb(relativePath);
		}
		return relativePath;
	}
	
	@Async("thumbProcessExecutor")
	private void makeImageThumb(String path) {
		logger.info("thumb make begin:" + path );
		String realPath = uploadPath + original + path;
		String thumbPath = uploadPath + thumb + path;
		File file = new File(thumbPath);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		File originalFile = new File(realPath);
		InputStream is;
		int height = 0;
		int width = 0;
		try {
			is = new FileInputStream(originalFile);
			Image imgOriginal = ImageIO.read(is);
			height = imgOriginal.getHeight(null);
			width = imgOriginal.getWidth(null);
		} catch (FileNotFoundException e1) {
			logger.error("file not find error:", e1);
		} catch (IOException e) {
			logger.error("io error: ", e);
		}
		makeThumbBySize(realPath, thumbPath, width, height, 80, 80);
		logger.info("thumb make end!" + path);
	}
	
	@Async("thumbProcessExecutor")
	private ListenableFuture<String> deleteFile(String type, String path) {
		logger.info("delete file start：" + type + "," + path);
		String filePath = uploadPath + type + path;
		File file = new File(filePath);
		if (file.exists()) {
			if (file.delete()) {
				logger.info("delete " + path +" success!");
				return new AsyncResult<String>("success");
			} else {
				return new AsyncResult<String>("fail");
			}
		} else {
			return new AsyncResult<String>("file-not-exist");
		}
	}
	
	private void makeThumbBySize(String originPath, String destPath, int imgWidth, int imgHeight, int destWidth, int destHeight) {
		double width = imgWidth;
		double height = imgHeight;
		double widthCut = destWidth;
		double heigthCut = destHeight;
		double radio = height / width;
		double radioCut = heigthCut / widthCut;
		if (imgWidth > destWidth && imgHeight > destHeight) {
			if (height > width) {
				width = 400;
				height = 400 * radio;
			} else {
				height = 400;
				width = 400 / radio;
			}
		}
		if (radioCut > 1) {
			widthCut = 400;
			heigthCut = 400 * radioCut;
		} else {
			widthCut = 400 * radioCut;
			heigthCut = 400;
		}

		try {
			Thumbnails.of(originPath).size((int)width, (int)height).toFile(destPath);
			Thumbnails.of(destPath).sourceRegion(Positions.CENTER, (int)widthCut, (int)heigthCut).size(destWidth, destHeight).keepAspectRatio(false)
					.toFile(destPath);
		} catch (IOException e) {
			logger.error("make thumb io error:", e);
		}
	}
	
	private String makeRelativePath(String userId, String fileNameString) {
		LocalDateTime nowDate = LocalDateTime.now();
		String timeString = DateTimeFormatter.ISO_DATE_TIME.format(nowDate);
		timeString = timeString.replaceAll("[\\:\\-\\.]", "");
		String[] timeStrings = timeString.split("T");
		String hourMinuteSecond = timeStrings[1];
		String hour = hourMinuteSecond.substring(0, 2);
		String minuteSecond = hourMinuteSecond;
		String relativePath = "/" + timeStrings[0] + "/" + userId + "/" + hour
				+ "/" + minuteSecond + "_" + fileNameString;
		return relativePath;
	}
	
	private boolean isImage(InputStream inputStream) {
		if (inputStream == null) {
			return false;
		}
		Image img;
		try {
			img = ImageIO.read(inputStream);
			return !(img == null || img.getWidth(null) <= 0 || img.getHeight(null) <= 0);
		} catch (Exception e) {
			return false;
		}
	}

}
