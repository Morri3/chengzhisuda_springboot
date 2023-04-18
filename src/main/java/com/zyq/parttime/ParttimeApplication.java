package com.zyq.parttime;

import cn.dev33.satoken.SaManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ParttimeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ParttimeApplication.class, args);
		System.out.println("启动成功：Sa-Token配置如下：" + SaManager.getConfig());
	}

}
