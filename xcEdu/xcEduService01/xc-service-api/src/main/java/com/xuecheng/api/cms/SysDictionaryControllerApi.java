package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.system.SysDictionary;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Create by wong on 2021/5/25
 */
@Api(value="数据字典接口",description = "数据字典接口，提供数据字典接口的管理、查询功能")
public interface SysDictionaryControllerApi {

    @ApiOperation("查询数据字典")
    public SysDictionary getByType(String type);
}
