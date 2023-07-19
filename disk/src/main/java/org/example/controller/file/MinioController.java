package org.example.controller.file;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


@CrossOrigin
@Api(value = "minio")
@RestController
public class MinioController {
    @Value("${minio.endpoint}")
    private String endpoint;
    @Value("${minio.accessKey}")
    private String accessKey;
    @Value("${minio.secretKey}")
    private String secretKey;


    @ApiOperation("上传文件")
    @CacheEvict(value = {"files"}, allEntries = true)
    @PostMapping("upload/")

    public ResultType uploadFile(@RequestParam String fileName, @RequestParam("file") MultipartFile file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        MinioClient minioClient = new MinioClient.Builder().endpoint(endpoint).credentials(accessKey, secretKey).build();
        String bucketName = fileName.split("/")[0];
        System.out.println("FILENAME:" + fileName);
        System.out.println(bucketName);

//      检查是否存在bucket
        boolean isBucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!isBucketExists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
        InputStream in = null;
        try {
            in = file.getInputStream();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .contentType(file.getContentType())
                    .stream(in, in.available(), -1)
                    .build());
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResultType<>().message("上传成功").code(200);
    }


    @ApiOperation("获取所有的文件")
    @Cacheable(value = {"files"})
    @GetMapping("getFiles/{prefix}")
    public ResultType getFiles(@PathVariable(value = "prefix", required = false) String prefix) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        MinioClient minioClient = new MinioClient.Builder().endpoint(endpoint).credentials(accessKey, secretKey).build();
        System.out.println("prefix:" + prefix);
        List<Bucket> buckets = minioClient.listBuckets();
        List<String> result = new ArrayList<>();
        for (Bucket bucket : buckets) {
            result.addAll(getBucketFiles(minioClient, bucket.name(), prefix));
        }
        return new ResultType<>().data(result).code(200).message("查询成功");
    }


    public List<String> getBucketFiles(MinioClient minioClient, String bucketName, String prefix) {
        List<String> list = new ArrayList<>();
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (found) {
                Iterable<Result<Item>> myObjects;
                if (prefix.equals("null")) {
                    myObjects = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).recursive(true).build());

                } else {
                    myObjects = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).recursive(true).prefix(prefix).build());
                }
                for (Result<Item> result : myObjects) {
                    list.add(result.get().objectName());
                }
                return list;
            }
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        } catch (NoSuchAlgorithmException | IOException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        return list;
    }


    @ApiOperation("下载或预览某个文件")
    @GetMapping("/download/{fileName}")
    @CrossOrigin(origins = "*", allowCredentials = "true", allowedHeaders = "*", methods = {RequestMethod.GET})
    public ResultType getFileUrl(@PathVariable("fileName")String fileName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        MinioClient minioClient = new MinioClient.Builder().endpoint(endpoint).credentials(accessKey, secretKey).build();
        System.out.println(fileName);
        String bucketName = fileName.split("/")[0];
        System.out.println("bucketName:" + bucketName);
        String url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().bucket(bucketName).object(fileName).method(Method.GET).build());
        System.out.println(url);
        return new ResultType<>().data(url).message("获取链接成功").code(200);
    }


    @CacheEvict(value = {"files"}, allEntries = true)
    @ApiOperation("删除某个文件")
    @PostMapping("/deleteObject/{fileName}")
    @CrossOrigin(origins = "*", allowCredentials = "true", allowedHeaders = "*", methods = {RequestMethod.POST})
    public ResultType deleteFile(@PathVariable("fileName")String fileName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        System.out.println(fileName);
        MinioClient minioClient = new MinioClient.Builder().endpoint(endpoint).credentials(accessKey, secretKey).build();
        String bucketName = fileName.split("/")[0];
        System.out.println(bucketName);
        minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(fileName).build());
        return new ResultType<>().message("删除成功").code(200);
    }

}