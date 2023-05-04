package com.zyq.parttime;

import cn.dev33.satoken.SaManager;
import com.baidu.aip.ocr.AipOcr;
import com.zyq.parttime.utils.Constant;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.HashMap;

@EnableScheduling
@SpringBootApplication
public class ParttimeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ParttimeApplication.class, args);
		System.out.println("启动成功：Sa-Token配置如下：" + SaManager.getConfig());
	}

}
