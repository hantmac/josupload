package org.example;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        try {
            String size = "2KB";
            createFileWithSize(size);
            String tmpDir = System.getProperty("java.io.tmpdir");
            String filePath = Paths.get(tmpDir, "fileWithSize" + size).toString();
            uploadFileToS3(filePath, "<Your Bucket Name>", "<Your Key Name>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createFileWithSize(String size) throws IOException {
        long sizeInBytes = convertSizeToBytes(size);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        String tmpDir = System.getProperty("java.io.tmpdir");
        String filePath = Paths.get(tmpDir, "fileWithSize" + size).toString();

        try (FileChannel fileChannel = new FileOutputStream(filePath, true).getChannel()) {
            while (sizeInBytes > 0) {
                buffer.clear();
                fileChannel.write(buffer);
                sizeInBytes -= 1024;
            }
        }
        // 输出 filePath 中文件的大小
        System.out.println("File created at: " + filePath);

        // 输出 filePath 中文件的大小
        System.out.println("File size: " + Paths.get(filePath).toFile().length());
    }

    public static long convertSizeToBytes(String size) {
        long value = Long.parseLong(size.substring(0, size.length() - 2));
        String unit = size.substring(size.length() - 2);

        switch (unit) {
            case "GB":
                return value * 1024 * 1024 * 1024;
            case "MB":
                return value * 1024 * 1024;
            case "KB":
                return value * 1024;
            default:
                return value;
        }
    }

    public static void uploadFileToS3(String filePath, String bucketName, String keyName) {
        // 请替换为你的 AWS 访问密钥和秘密密钥
        String accessKey = "<Your AWS Access Key>";
        String secretKey = "<Your AWS Secret Key>";

        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();

        s3Client.putObject(new PutObjectRequest(bucketName, keyName, new File(filePath)));
    }
}