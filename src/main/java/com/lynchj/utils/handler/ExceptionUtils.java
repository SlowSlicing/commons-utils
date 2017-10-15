package com.lynchj.utils.handler;

import com.lynchj.enums.ErrorStatusEnum;
import com.lynchj.exception.LynchjException;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @Author: 郭青松
 * @Description: 异常处理
 * @Date: Created in 23:26 2017/10/14
 */
public class ExceptionUtils {

    public ExceptionUtils() {
        throw new LynchjException(ErrorStatusEnum.CREATE_UTILS_ENTITY_ERROR);
    }

    public static String detialExceptionInfo(Exception e) {

        try (
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw)
            ) {

            e.printStackTrace(pw);
            sw.flush();
            pw.flush();

            return sw.toString();

        } catch (Exception e1) {
            e1.printStackTrace();
            throw new LynchjException(ErrorStatusEnum.EXCEPTION_DETIAL_INFO_ERROR);
        }

    }

}
