package com.eec.service;

import com.eec.data.FilePathData;
import com.eec.data.FileUploadData;

public interface FileUploadService {

    String fileUpload(FileUploadData fud);

    String delete(FilePathData fpd);
    
    byte[] download(FilePathData fpd);
}
