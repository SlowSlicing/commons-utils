package com.lynchj.exception;

import com.lynchj.enums.base.BaseEnum;

/**
 * 自定义异常类
 * 
 * @author lynch
 */
@SuppressWarnings("all")
public class LynchjException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private Integer code;
	
	public LynchjException(BaseEnum baseEnum) {
		super(baseEnum.getMsg());
		this.code = baseEnum.getCode();
	}
	
}
