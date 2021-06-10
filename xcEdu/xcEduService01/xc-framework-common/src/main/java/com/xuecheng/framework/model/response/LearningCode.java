package com.xuecheng.framework.model.response;

import lombok.ToString;

/**
 * @Author: mrt.
 * @Description:
 * @Date:Created in 2018/1/24 18:33.
 * @Modified By:
 */

@ToString
public enum LearningCode implements ResultCode{
    LEARNING_GETMEDIA_ERROR(false,23001,"获取学习地址失败！"),
    CHOOSECOURSE_USERISNULL(false,23002,"获取的选课的用户为空！"),
    CHOOSECOURSE_TASKISNULL(false,23003,"获取的选课的任务为空！");
    //操作是否成功
    boolean success;
    //操作代码
    int code;
    //提示信息
    String message;
    private LearningCode(boolean success, int code, String message){
        this.success = success;
        this.code = code;
        this.message = message;
    }

    @Override
    public boolean success() {
        return success;
    }
    @Override
    public int code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }


}
