package com.leyou.upload.service.impl;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.upload.service.IUploadService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author 26747
 * @description UploadServiceImpl
 * @date 2020/5/17 19:56
 */
@Service("uploadService")
public class UploadServiceImpl implements IUploadService {

    //定义合法的文件类型；这些是html的文件类型不等同于文件后缀名
    private static final List<String> CONTENT_TYPE = Arrays.asList("image/gif", "image/fax", "image/x-icon", "image/jpeg", "image/pnetvue", "image/tiff", "image/png");
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadServiceImpl.class);

    @Resource
    private FastFileStorageClient storageClient;

    @Override
    public String uploadImage(MultipartFile file) {

        //获取文件名
        String originalFilename = file.getOriginalFilename();
        /*//获取最后一个"."后的字符串(文件后缀名、文件类型)
        StringUtils.substringAfterLast(originalFilename, ".");*/
        //获取html文件类型,但不等同于文件后缀名
        String contentType = file.getContentType();

        //1.校验文件类型
        if (!CONTENT_TYPE.contains(contentType)) {
            //java可以使用{}作为占位符
            LOGGER.info("文件类型不合法:{}", originalFilename);
            return null;
        }

        try {
            //2.校验文件内容
            //使用ImageIo获取文件流信息，如果流信息为空则不是图片，
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            //可以加上getHeight()和getWidth()判断图片文件是否有高和宽
            if (bufferedImage == null) {
                LOGGER.info("文件内容不合法:{}", originalFilename);
                return null;
            }

            //3.保存到服务器
            //file.transferTo(new File("E:\\leyou-tools\\images" + originalFilename));  //没有linux服务器
            //使用linux服务器,contentType是html文件类型，不是后缀名
            String type = StringUtils.substringAfterLast(originalFilename, ".");
            StorePath storePath = this.storageClient.uploadFile(file.getInputStream(), file.getSize(), type, null);

            //4.返回url，进行回显
            //可以通过nginx代理一个图片服务器来存放图片资源，url就是图片服务器名+图片文件名
            //return "http://image.leyou.com/" + originalFilename;
            return "http://image.leyou.com/" + storePath.getFullPath();
        } catch (IOException e) {
            LOGGER.info("服务器内部错误:" + originalFilename);
            e.printStackTrace();
        }
        return null;
    }
}
