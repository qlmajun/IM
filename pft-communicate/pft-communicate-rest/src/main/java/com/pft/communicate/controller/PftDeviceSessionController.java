package com.pft.communicate.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pft.communicate.common.dto.ServiceResultDTO;
import com.pft.communicate.common.dubbo.service.PftDeviceSessionService;
import com.pft.communicate.common.dubbo.service.dto.PftDeviceSessionDTO;

/****
 * 票付通设备session操作controller
 * 
 * @author majun@12301.cc
 *
 */
@RequestMapping("device/session/")
@Controller
public class PftDeviceSessionController {

	@Autowired
	private PftDeviceSessionService sessionService;

	@RequestMapping(value = "list", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ServiceResultDTO getSessions() {
		List<PftDeviceSessionDTO> list = sessionService.getSessions();
		return ServiceResultDTO.success(list);
	}

}
