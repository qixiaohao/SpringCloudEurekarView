/**
 * 
 */
package cn.et.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

/**
 * @author Qixiaohao
 * @version 1.0
 * 
 */
@Controller
public class UserView {
	@Autowired
	RestTemplate template;

	@PostMapping(value = "/user")
	public String validUser(@RequestParam Map map) {

		// 模拟请求头
		HttpHeaders requestHeaders = new HttpHeaders(); // 传递的json
		requestHeaders.setContentType(MediaType.APPLICATION_JSON); // 返回可接受json类型
		requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON)); // 将请求头和请求体（表单数据）打包
		HttpEntity<Map> request = new HttpEntity<Map>(map, requestHeaders);
		String returnCode = template.postForObject("http://USERSERVICE/addUser", request, String.class);

		if (returnCode.equals("1")) {
			return "/suc.jsp";
		} else {
			return "/add.jsp";
		}

	}

	@DeleteMapping(value = "/user/{userId}")
	public String deleteUser(@PathVariable String userId) {

		Map variables = new HashMap();
		variables.put("userId", userId);
		template.delete("http://USERSERVICE/deleteUser/{userId}", variables);
		String returnCode = "1";
		if (returnCode.equals("1")) {
			return "/suc.jsp";
		} else {
			return "/delete.jsp";
		}

	}

	@GetMapping("/user")
	public String loginUser(String userName, String password) {
		String returnCode = template.getForObject(
				"http://USERSERVICE/loginUser?userName=" + userName + "&password=" + password, String.class);
		if (returnCode.equals("1")) {
			return "/suc.jsp";
		} else {
			return "/login.jsp";
		}
	}

	@PutMapping(value = "/user/{userId}", produces = "text/html")
	public String updateUser(@PathVariable String userId, @RequestParam Map map) {

		// 模拟请求头
		HttpHeaders requestHeaders = new HttpHeaders();
		// 传递的json
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);
		// 返回可接受json类型
		requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		// 将请求头和请求体（表单数据）打包
		HttpEntity<Map> request = new HttpEntity<Map>(map, requestHeaders);
		Map variables = new HashMap();
		variables.put("userId", userId);
		template.put("http://USERSERVICE/updateUser/{userId}", request, variables);
		return "/suc.jsp";

	}

	@Autowired
	private LoadBalancerClient loadBalancer;

	/**
	 * 启动多个发布者 端口不一致 程序名相同 使用
	 * 
	 * @LoadBalanced必须添加
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/choosePub")
	public String choosePub() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i <= 10; i++) {
			ServiceInstance ss = loadBalancer.choose("USERSERVICE");// 从两个idserver中选择一个 这里涉及到选择算法
			sb.append(ss.getUri().toString() + "<br/>");
		}
		return sb.toString();
	}

}
