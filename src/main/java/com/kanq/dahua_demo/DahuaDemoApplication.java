package com.kanq.dahua_demo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lombok.extern.slf4j.Slf4j;

import com.netsdk.lib.NetSDKLib;
import com.netsdk.util.DeviceInit;
@SpringBootApplication
@Slf4j
public class DahuaDemoApplication {

	public static void main(String[] args) {
		if(NetSDKLib.NETSDK_INSTANCE != null
		&& NetSDKLib.CONFIG_INSTANCE != null){
			log.info("NetSDK isOK");
			DeviceInit.init();
		}
		
		SpringApplication.run(DahuaDemoApplication.class, args);
	}

}
