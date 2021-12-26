package com.netsdk.module;
import com.netsdk.lib.NetSDKLib;
import com.netsdk.lib.ToolKits;
import com.netsdk.lib.NetSDKLib.LLong;
import com.netsdk.lib.NetSDKLib.fAnalyzerDataCallBack;
/**
 * \if ENGLISH_LANG
 * Attendance Interface
 * contains:smart subscribe、CRUD of user&&fingerprint and collection fingerprint 
 * \else
 * 考勤机接口实现
 * 包含: 智能订阅、考勤用户及指纹的增删改查、指纹采集
 * \endif
 */
public class AttendanceModule {

    public static final int TIME_OUT = 3000;
    public static final int nMaxFingerPrintSize = 2048;
    public static LLong m_hAttachHandle = new LLong(0);
    
    /**
	 * 智能订阅
	 * @param callback   智能订阅回调函数
	 */
    public static boolean realLoadPicture(fAnalyzerDataCallBack callback) {

        int bNeedPicture = 0; // 不需要图片

        m_hAttachHandle =  LoginModule.netsdk.CLIENT_RealLoadPictureEx(LoginModule.m_hLoginHandle, -1,
        		NetSDKLib.EVENT_IVS_ALL, bNeedPicture, callback, null, null);

        if(m_hAttachHandle.longValue() == 0) {
        	System.err.printf("CLIENT_RealLoadPictureEx Failed!" + ToolKits.getErrorCodePrint(LoginModule.netsdk.CLIENT_GetLastError()));
        }
        
        return m_hAttachHandle.longValue() != 0;
    }

    /**
	 * 停止智能订阅
	 */
    public static void stopRealLoadPicture(){
        if (m_hAttachHandle.longValue() == 0) {
            return;
        }
        
        LoginModule.netsdk.CLIENT_StopLoadPic(m_hAttachHandle);
        m_hAttachHandle.setValue(0);
    }
}
