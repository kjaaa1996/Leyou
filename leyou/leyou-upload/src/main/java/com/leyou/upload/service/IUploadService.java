package com.leyou.upload.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author 26747
 * @description IUploadService
 * @date 2020/5/17 19:56
 */
public interface IUploadService {
    String uploadImage(MultipartFile file) throws IOException;
}
