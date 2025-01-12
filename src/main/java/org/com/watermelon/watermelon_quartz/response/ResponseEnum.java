package org.com.watermelon.watermelon_quartz.response;



/**
 * @author watermelon
 * @date 2024/07/28
 */
public enum ResponseEnum {

	/**
	 * ok
	 */
	OK("00000", "ok"),
	/**
	 * 失败
	 */
	ERROR("BE001", "出现错误"),

	/**
	 * 用于直接显示提示用户的错误，内容由输入内容决定
	 */
	SHOW_FAIL("A00001", ""),

	/**
	 * 方法参数没有校验，内容由输入内容决定
	 */
	METHOD_ARGUMENT_NOT_VALID("A00002", ""),

	/**
	 * 无法读取获取请求参数
	 */
	HTTP_MESSAGE_NOT_READABLE("A00003", "请求参数格式有误"),

	/**
	 * 未授权
	 */
	UNAUTHORIZED("A00004", "Unauthorized"),

	/**
	 * 服务器出了点小差
	 */
	EXCEPTION("A00005", "服务器出了点小差"),

	/**
	 * 数据异常
	 */
	DATA_ERROR("A00007", "数据异常，请刷新后重新操作"),

	/**
	 * 一些需要登录的接口，而实际上因为前端无法知道token是否已过期，导致token已失效时，
	 * 应该返回一个状态码，告诉前端token已经失效了，及时清理
	 */
	CLEAN_TOKEN("A00008", "clean token"),

	/**
	 * 刷新token已过期
	 */
	REFRESH_TOKEN_EXIST("A00009", "refresh token exist"),

	/**
	 * 数据不完整
	 */
	DATA_INCOMPLETE("A00010", "数据不完整"),

	/**
	 * 存在相同任务
	 */
	HAS_EXIST_SCHEDULE("A00011","已经存在相同任务"),

	/**
	 * 权限不足以访问
	 */
	ACCESS_DENIED_EXCEPTION("4003","authentication not allow to visit")
	;
    private final String code;

	private final String msg;

	public String value() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	ResponseEnum(String code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	@Override
	public String toString() {
		return "ResponseEnum{" + "code='" + code + '\'' + ", msg='" + msg + '\'' + "} " + super.toString();
	}

}
