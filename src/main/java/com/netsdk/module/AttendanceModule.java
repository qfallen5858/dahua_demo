package com.netsdk.module;
import java.io.UnsupportedEncodingException;

import com.netsdk.lib.NetSDKLib;
import com.netsdk.lib.ToolKits;
import com.netsdk.lib.NetSDKLib.LLong;
import com.netsdk.lib.NetSDKLib.NET_CTRL_CAPTURE_FINGER_PRINT;
import com.netsdk.lib.NetSDKLib.NET_IN_ATTENDANCE_ADDUSER;
import com.netsdk.lib.NetSDKLib.NET_OUT_ATTENDANCE_ADDUSER;
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

    /**
	 * 考勤新增加用户
	 * @param userId   用户ID
	 * @param userName 用户名
	 * @param cardNo   卡号
	 */
	public static boolean addUser(String userId, String userName, String cardNo) {
		
		/*
		 * 入参
		 */
		NET_IN_ATTENDANCE_ADDUSER stuIn = new NET_IN_ATTENDANCE_ADDUSER();
		stringToByteArray(userId, stuIn.stuUserInfo.szUserID);
		stringToByteArray(userName, stuIn.stuUserInfo.szUserName);
		stringToByteArray(cardNo, stuIn.stuUserInfo.szCardNo);
		
		/*
		 * 出参
		 */
		NET_OUT_ATTENDANCE_ADDUSER stuOut = new NET_OUT_ATTENDANCE_ADDUSER();
		
		boolean bRet = LoginModule.netsdk.CLIENT_Attendance_AddUser(LoginModule.m_hLoginHandle, stuIn, stuOut, TIME_OUT);
		if (!bRet) {
			System.err.printf("CLIENT_Attendance_AddUser Failed!" + ToolKits.getErrorCodePrint(LoginModule.netsdk.CLIENT_GetLastError()));
		}
		
		return bRet;
	}

    /**
	 * 指纹采集
	 * @param nChannelID   门禁序号
	 * @param szReaderID   读卡器ID
	 */
	public static boolean collectionFinger(int nChannelID, String szReaderID) {
		/*
		 * 入参
		 */
		NET_CTRL_CAPTURE_FINGER_PRINT stuCollection = new NET_CTRL_CAPTURE_FINGER_PRINT();
		stuCollection.nChannelID = nChannelID;
		stringToByteArray(szReaderID, stuCollection.szReaderID);
		
		stuCollection.write();
		boolean bRet = LoginModule.netsdk.CLIENT_ControlDeviceEx(LoginModule.m_hLoginHandle, NetSDKLib.CtrlType.CTRLTYPE_CTRL_CAPTURE_FINGER_PRINT, stuCollection.getPointer(), null, 5000);
		if (!bRet) {
			System.err.printf("CLIENT_ControlDeviceEx CAPTURE_FINGER_PRINT Failed!" + ToolKits.getErrorCodePrint(LoginModule.netsdk.CLIENT_GetLastError()));
		}
		return bRet;
	}

    /**
	 * 字符串转字符数组
	 * @param src   源字符串
	 * @param dst   目标字符数组
	 */
	public static void stringToByteArray(String src, byte[] dst) {
		
		if (src == null || src.isEmpty()) {
			return;
		}
		
		for(int i = 0; i < dst.length; i++) {
			dst[i] = 0;
		}
		
		byte []szSrc;
		
		try {
			szSrc = src.getBytes("GBK");
		} catch (UnsupportedEncodingException e) {
			szSrc = src.getBytes();
		} 
		
		if (szSrc != null) {
			int len = szSrc.length >= dst.length ? dst.length-1:szSrc.length;
			System.arraycopy(szSrc, 0, dst, 0, len);
		}
	}

    /**
     * 用户信息
     * */
	public static class UserData {
		public static int nTotalUser;		// 用户总数
		
		public String userId;				// 用户ID
		public String userName;				// 用户名
		public String cardNo;				// 卡号
		public int[]    nFingerPrintIDs;	// 指纹ID数组
		public byte[][] szFingerPrintInfo;	// 指纹数据数组
	}
	
	 /**
     * 门禁事件信息
     * */
	public static class AccessEventInfo {
    	public String userId;		// 用户ID
		public String cardNo;		// 卡号
		public String eventTime;	// 事件发生时间
    	public int openDoorMethod;	// 开门方式
    }
	
	/**
     * 操作类型
     * */
	public enum OPERATE_TYPE {
		UNKNOWN,						// 未知
		SEARCH_USER,					// 搜索用户（第一页）
		PRE_SEARCH_USER,				// 搜索用户（上一页）
		NEXT_SEARCH_USER,				// 搜索用户（下一页）
		SEARCH_USER_BY_USERID,			// 通过用户ID搜索用户
		ADD_USER,						// 添加用户
		DELETE_USER,					// 删除用户
		MODIFIY_USER,					// 修改用户
		FINGERPRINT_OPEARTE_BY_USERID,	// 通过用户ID操作指纹
		FINGERPRINT_OPEARTE_BY_ID,		// 通过指纹ID操作指纹
		SEARCH_FINGERPRINT_BY_USERID,	// 通过用户ID搜索指纹
		SEARCH_FINGERPRINT_BY_ID,		// 通过指纹ID搜索指纹
		ADD_FINGERPRINT,				// 添加指纹
		DELETE_FINGERPRINT_BY_USERID,	// 通过用户ID删除指纹
		DELETE_FINGERPRINT_BY_ID		// 通过指纹ID删除指纹
	};
	
}
