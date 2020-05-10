package com.pinyougou.manager.controller;

import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import util.FastDFSClient;

@RestController
public class UploadController {

    @Value("${FILE_SERVER_URL}")
    private String file_server_url;

    @RequestMapping("/upload")
    public Result upload(MultipartFile file){

        String ofilename = file.getOriginalFilename();
        String exName = ofilename.substring(ofilename.lastIndexOf(".") + 1);
        try {
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");
            String fileId  = fastDFSClient.uploadFile(file.getBytes(), exName);
            String url=file_server_url+fileId;
            return new Result(true,url);

        } catch (Exception e) {
            return new Result(false,"上传失败");
        }


    }


}
