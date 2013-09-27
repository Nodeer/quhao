package com.withiter.test;

public class PersonalIPTest {

	public static void main(String[] args) {
		String ip = "";
		String userHome = System.getProperty("user.home");
		System.out.println(userHome);
		if(userHome.contains("cross")){
			ip = "192.168.1.20";
		}
		if(userHome.contains("jazze")){
			ip="xxx.xxx.xxx.xxx";
		}
	}

}
