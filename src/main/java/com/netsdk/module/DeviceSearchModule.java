package com.netsdk.module;

import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import com.sun.jna.Pointer;
import com.sun.jna.Memory;
import com.netsdk.lib.ToolKits;
import com.netsdk.lib.NetSDKLib.fSearchDevicesCBEx;
import com.netsdk.lib.NetSDKLib.DEVICE_IP_SEARCH_INFO;
import com.netsdk.lib.NetSDKLib.LLong;
import com.netsdk.lib.NetSDKLib.fSearchDevicesCB;
// import com.netsdk.lib.Utils.LLong;
import com.netsdk.lib.enumeration.EM_SEND_SEARCH_TYPE;
import com.netsdk.lib.structure.NET_IN_STARTSERACH_DEVICE;
import com.netsdk.lib.structure.NET_OUT_STARTSERACH_DEVICE;

public class DeviceSearchModule {

    /**
     * 设备组播和广播搜索
     */
    public static LLong multiBroadcastDeviceSearch(fSearchDevicesCBEx searchDevices, String localIp)
            throws SocketException {
        NET_IN_STARTSERACH_DEVICE inParam = new NET_IN_STARTSERACH_DEVICE();

        inParam.cbSearchDevices = searchDevices;
        System.arraycopy(localIp.getBytes(), 0, inParam.szLocalIp, 0, localIp.getBytes().length);
        inParam.emSendType = EM_SEND_SEARCH_TYPE.EM_SEND_SEARCH_TYPE_MULTICAST_AND_BROADCAST.ordinal();

        Pointer inBuf = new Memory(inParam.size());
        ToolKits.SetStructDataToPointer(inParam, inBuf, 0);

        NET_OUT_STARTSERACH_DEVICE outParam = new NET_OUT_STARTSERACH_DEVICE();

        Pointer outBuf = new Memory(outParam.size());
        ToolKits.SetStructDataToPointer(outParam, outBuf, 0);

        return LoginModule.netsdk.CLIENT_StartSearchDevicesEx(inBuf, outBuf);
    }

    /**
     * 停止设备组播和广播搜索
     */
    public static void stopDeviceSearch(LLong m_DeviceSearchHandle) {
        if (m_DeviceSearchHandle.longValue() == 0) {
            return;
        }

        LoginModule.netsdk.CLIENT_StopSearchDevices(m_DeviceSearchHandle);
        m_DeviceSearchHandle.setValue(0);
    }

    /**
     * 设备IP单播搜索
     * 
     * @param startIP 起始IP
     * @param nIpNum  IP个数，最大 256
     * @throws SocketException
     */
    public static boolean unicastDeviceSearch(String localIp, String startIP, int nIpNum,
            fSearchDevicesCB cbSearchDevices) throws SocketException {
        String[] szIPStr = startIP.split("\\.");

        DEVICE_IP_SEARCH_INFO deviceSearchInfo = new DEVICE_IP_SEARCH_INFO();
        deviceSearchInfo.nIpNum = nIpNum;
        for (int i = 0; i < deviceSearchInfo.nIpNum; i++) {
            System.arraycopy(getIp(szIPStr, i).getBytes(), 0, deviceSearchInfo.szIPArr[i].szIP, 0,
                    getIp(szIPStr, i).getBytes().length);
        }
        if (LoginModule.netsdk.CLIENT_SearchDevicesByIPs(deviceSearchInfo, cbSearchDevices, null, localIp, 6000)) {
            System.out.println("SearchDevicesByIPs Succeed!");
            return true;
        }
        return false;
    }

    public static String getIp(String[] ip, int num) {
        String szIp = "";
        if (Integer.parseInt(ip[3]) >= 255) {
            szIp = ip[0] + "." + ip[1] + "." + String.valueOf(Integer.parseInt(ip[2]) + 1) + "."
                    + String.valueOf(Integer.parseInt(ip[3]) + num - 255);
        } else {
            szIp = ip[0] + "." + ip[1] + "." + ip[2] + "." + String.valueOf(Integer.parseInt(ip[3]) + num);
        }

        return szIp;
    }

    /**
     * 获取多网卡IP列表
     */
    public static List<String> getHostAddress() throws SocketException {
        List<String> ipList = new ArrayList<String>();
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();

            while (inetAddresses.hasMoreElements()) {
                InetAddress inetAddress = inetAddresses.nextElement();

                if (inetAddress.isLoopbackAddress()) {

                } else if (inetAddress.isLinkLocalAddress()) {

                } else if (inetAddress instanceof Inet4Address) {
                    String localIp = inetAddress.getHostAddress();
                    ipList.add(localIp);
                }
            }
        }
        return ipList;

    }
}
