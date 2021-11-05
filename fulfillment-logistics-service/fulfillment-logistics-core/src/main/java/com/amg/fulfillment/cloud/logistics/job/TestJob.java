package com.amg.fulfillment.cloud.logistics.job;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.springframework.stereotype.Component;


@JobHandler(value="test-cms-job")
@Component
public class TestJob extends IJobHandler {

	@Override
	public ReturnT<String> execute(String param) {
		XxlJobLogger.log("测试打印执行日志");
		System.out.println("开始执行任务");
		return SUCCESS;
	}

}
