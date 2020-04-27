package com.aws.s3.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.aws.s3.constants.CommanConstant;

public class CommonService {

	public void getObj(AmazonS3 s3client) {

		String bucketName = CommanConstant.BUCKET_NAME;
		String objectName = CommanConstant.BUCKET_FILE_PATH;

		try {
			S3Object s3object = s3client.getObject(bucketName, objectName);

			S3ObjectInputStream inputStream = s3object.getObjectContent();

			FileUtils.copyInputStreamToFile(inputStream, new File(CommanConstant.LOCAL_DOWNLOAD_PATH));

			System.out.println("file copied to destination");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void createFolder(String bucketName, String folderName, AmazonS3 client, String SUFFIX) {

		ObjectMetadata metaData = new ObjectMetadata();

		metaData.setContentLength(0);

		InputStream emptyStream = new ByteArrayInputStream(new byte[0]);

		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, folderName + SUFFIX, emptyStream,
				metaData);

		client.putObject(putObjectRequest);

	}

	public static void downloadFile(String bucketName, String folderName, AmazonS3 s3client)
			throws FileNotFoundException, IOException {

		try {
			S3Object s3object = s3client.getObject(new GetObjectRequest(bucketName, folderName));
			InputStream reader = new BufferedInputStream(s3object.getObjectContent());
			File file = new File("C:\\Users\\Administrator\\Downloads\\" + CommanConstant.FILE_NAME);
			BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(file));
			int read = -1;
			while ((read = reader.read()) != -1) {
				writer.write(read);

			}
			writer.flush();
			writer.close();
			reader.close();

		} catch (AmazonServiceException e) {
			System.err.println(e.getErrorMessage());
		}

		System.out.println("File are download successfully");

	}

	public static void deleteFolder(String bucketName, String folderName, AmazonS3 client) {
		List fileList = client.listObjects(bucketName, folderName).getObjectSummaries();
		for (Object obj : fileList) {
			S3ObjectSummary file = (S3ObjectSummary) obj;
			client.deleteObject(bucketName, file.getKey());

		}
		client.deleteObject(bucketName, folderName);

	}
}
