package com.xm.reggie.controller;

import com.xm.reggie.common.R;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 进行文件上传和下载
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.path}")
    private String bassPath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){

        //获取文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //使用UUID生成文件名
        String fileName = UUID.randomUUID().toString() + suffix;

        File dir = new File(bassPath);
        //判断目录是否存在，不存在则创建
        if (!dir.exists()){
            dir.mkdirs();
        }

        //file是临时文件，需要将其保存到指定的目录下
        try {
            file.transferTo(new File(bassPath+ fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //TODO 上传文件
        return R.success(fileName);
    }

    /**
     * 下载文件
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        //TODO 下载文件
        //输入流，读取文件内容
        try {
            FileInputStream fis = new FileInputStream(new File(bassPath + name));
            //输出流，将文件内容写入到客户端浏览器中
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");


            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fis.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

            //关闭流
            outputStream.close();
            fis.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
