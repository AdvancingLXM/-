package com.pinyougou.manage.controller;

import com.pinyougou.common.util.FastDFSClient;
import com.pinyougou.vo.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/upload")
@RestController
public class UploadController {

    /**
     * 接收图片并上传到FastDFS
     * @param file 图片文件
     * @return 操作结果
     */
    @PostMapping
    public Result upload(MultipartFile file){
        Result result = Result.fail("上传失败");

        try {
            //1、上传图片到fastdfs
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:fastdfs/tracker.conf");

            //文件后缀名称
            String file_ext_name = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);

            String url = fastDFSClient.uploadFile(file.getBytes(), file_ext_name);

            //2、返回图片地址
            result = Result.ok(url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
