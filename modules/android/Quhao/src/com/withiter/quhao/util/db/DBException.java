package com.withiter.quhao.util.db;

/**
 * 数据库操作异常处理类
 */
public class DBException extends Exception {
	/**
	 * ID
	 */
	private static final long serialVersionUID = -3051748715160422241L;

	/**
	 * 构造函数，实现初始化对象
	 * @param message
	 *            异常信息
	 */
	public DBException(String message) {
		super(message);
	}

	/**
	 * 
	 * 构造函数，实现初始化对象
	 * 
	 * @param message
	 *            异常信息
	 * @param cause
	 *            异常原因
	 */
	public DBException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * 
	 * 构造函数，实现初始化对象
	 * 
	 * @param cause
	 *            异常原因
	 */
	public DBException(Throwable cause) {
		super(cause);
	}
}
