package com.xuecheng.order.dao;

import com.xuecheng.framework.domain.task.XcTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Date;

/**
 * Create by wong on 2021/6/10
 */
public interface XcTaskRepository extends JpaRepository<XcTask, String> {
    // 取出指定时间之前的记录
    Page<XcTask> findByUpdateTimeBefore(Pageable pageable, Date updateTime);

    //更新任务处理时间updateTime
    @Modifying
    @Query("update XcTask t set t.updateTime = :updateTime where t.id = :id ")
    public int updateTaskTime(@Param(value = "id") String id, @Param(value = "updateTime") Date
            updateTime);

}
