package com.zyq.parttime.form.resumemanage;

import lombok.Data;

import java.io.File;
import java.io.Serializable;

@Data
public class UploadInputDto implements Serializable {
    private File file;
    private String telephone;
    private String upload_time;
}
