package com.withiter.quhao.task;


/**
 * json包装对象
 * 
 */
public class JsonPack {
	// 请求是否成功 约定 200：成功 其他：异常
	private int re = 200;
	// 异常信息
	private String msg = "";
	// 对象 可以为null
	private String obj = null;
	// 回调函数
	private Runnable callBack;

	// get,set-------------------------------------------------------------------
	public int getRe() {
		return re;
	}

	public void setRe(int re) {
		this.re = re;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getObj() {
		return obj;
	}

	public void setObj(String obj) {
		this.obj = obj;
	}

	public Runnable getCallBack() {
		return callBack;
	}

	public void setCallBack(Runnable callBack) {
		this.callBack = callBack;
	}
}
