package com.lynchj.enums;

import com.lynchj.enums.base.BaseEnum;

/**
 * 错误状态枚举类
 * 
 * @author 大漠知秋
 */
public enum ErrorStatusEnum implements BaseEnum {

	SYS_ERROR(1001, "系统错误"),
	CREATE_UTILS_ENTITY_ERROR(1002, "不允许创建工具类为实体对象"),
	NULL_VALUE_ERROR(1003, "空值错误"),
	NO_PROPERTIES_NAME_ANNOTATION_ERROR(1004, "传入的类属性中没有加入@PropertiesName注解"),
	EXCEL_LAYOUT_ERROR(1005, "只支持'.xls'和'.xlsx'格式的Excel文档"),
	EXCEPTION_DETIAL_INFO_ERROR(1006, "处理异常详细信息出错"),
	;
	
	private Integer code;
	
	private String msg;

	public Integer getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	ErrorStatusEnum(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	
}
