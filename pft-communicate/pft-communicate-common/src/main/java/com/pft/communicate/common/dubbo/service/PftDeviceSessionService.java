package com.pft.communicate.common.dubbo.service;

import java.util.List;

import com.pft.communicate.common.dubbo.service.dto.PftDeviceSessionDTO;

/****
 * 票付通设备连接会员操作dubbo服务接口声明
 * 
 * @author majun@12301.cc
 *
 */
public interface PftDeviceSessionService {

	/****
	 * 获取会话信息
	 * 
	 * @return
	 */
	List<PftDeviceSessionDTO> getSessions();

}
