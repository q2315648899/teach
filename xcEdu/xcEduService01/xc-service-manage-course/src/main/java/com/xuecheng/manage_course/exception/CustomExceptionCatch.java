package com.xuecheng.manage_course.exception;

import com.xuecheng.framework.exception.ExceptionCatch;
import com.xuecheng.framework.model.response.CommonCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * 课程管理自定义的异常捕获类(springMVC)，其中定义异常类型所对应的错误代码
 * Create by wong on 2021/6/8
 */
@ControllerAdvice//控制器增强
public class CustomExceptionCatch extends ExceptionCatch {

    static {
        //在这里加入一些课程管理自定义的基础的异常类型判断
        //除了CustomException以外的异常类型及对应的错误代码在这里定义,，如果不定义则统一返回固定的错误信息
        builder.put(AccessDeniedException.class, CommonCode.UNAUTHORISE);
    }
}
