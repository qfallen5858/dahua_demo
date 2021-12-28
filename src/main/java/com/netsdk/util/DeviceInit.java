package com.netsdk.util;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.lang.Runnable;

import com.netsdk.lib.ToolKits;
import com.netsdk.lib.NetSDKLib.DEVICE_NET_INFO_EX;
import com.netsdk.lib.NetSDKLib.fSearchDevicesCB;
import com.netsdk.module.DeviceSearchModule;
import com.netsdk.module.LoginModule;
import com.sun.jna.Pointer;

public class DeviceInit {

    private static DeviceInit _instance = null;

    private DeviceInit() {}

    private ArrayList<String> macList = new ArrayList<String>();

    public static DeviceInit getInstance() {
        if(_instance == null){
            _instance = new DeviceInit();
        }
        return _instance;
    }
    public static void init(){
        LoginModule.init(null, null);
        DeviceInit instance = DeviceInit.getInstance();
        instance.search("192.168.1.108");
    }

    private void search(String destIP){
        executorService.execute(new Runnable(){
            @Override
            public void run() {
                try{
                    List<String> ipList = DeviceSearchModule.getHostAddress();
                    for(String ip : ipList){
                        DeviceSearchModule.unicastDeviceSearch(ip, destIP, 1, callback);
                    }
                }catch(SocketException e){
                    e.printStackTrace();
                }
            }
        });
        
        
        // DeviceSearchModule.unica
    }

    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    private SearchDeviceCallback callback = new SearchDeviceCallback();

    
    //线程池，用于单播搜索
    private class SearchDeviceCallback implements fSearchDevicesCB{

        @Override
        public void invoke(Pointer pDevNetInfo, Pointer pUserData) {
            DEVICE_NET_INFO_EX deviceInfo = new DEVICE_NET_INFO_EX();
            ToolKits.GetPointerData(pDevNetInfo, deviceInfo);
            String mac = new String(deviceInfo.szMac) ;
            // if(!this.mac)
            System.out.println(mac);
        }

    }
    
}
