package com.aws.s3.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.aws.s3.constants.CommanConstant;

public class EntryPoint {

	// static Logger logger = Logger.getLogger(EntryPoint.class);
	public static void main(String[] args) throws FileNotFoundException, IOException {

		CommonService commonService = new CommonService();

		BasicAWSCredentials credential = new BasicAWSCredentials(CommanConstant.ACCESS_KEY_ID,
				CommanConstant.ACCESS_SEC_KEY);

		AmazonS3 s3client = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credential))
				.withRegion(Regions.EU_WEST_2)
				.build();

		String bucketName = CommanConstant.BUCKET_NAME;

		s3client.createBucket(bucketName);

		String folderName = CommanConstant.FOLDER_NAME;

		CommonService.createFolder(bucketName, folderName, s3client, CommanConstant.SUFFIX);

		String fileName = folderName + CommanConstant.SUFFIX + CommanConstant.FILE_NAME;
		s3client.putObject(new PutObjectRequest(bucketName, fileName, new File(CommanConstant.FILE_PATH))
				.withCannedAcl(CannedAccessControlList.PublicRead));

		System.out.println("Execution Completed");

		commonService.getObj(s3client);

		System.out.println("folder created");

		CommonService.downloadFile(bucketName, fileName, s3client);

		CommonService.deleteFolder(bucketName, folderName, s3client);

		System.out.println("folder deleted");
		// deletes bucket
		s3client.deleteBucket(bucketName);

	}

}
