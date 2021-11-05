package com.amg.fulfillment.cloud.logistics.controller;

import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsRulesDO;
import com.amg.fulfillment.cloud.logistics.model.req.DeleteReq;
import com.amg.fulfillment.cloud.logistics.model.req.LogisticsRuleAddReq;
import com.amg.fulfillment.cloud.logistics.model.req.LogisticsRuleSearchReq;
import com.amg.fulfillment.cloud.logistics.model.vo.LogisticsRuleDetailVO;
import com.amg.fulfillment.cloud.logistics.model.vo.LogisticsRuleVO;
import com.amg.fulfillment.cloud.logistics.service.ILogisticsRulesService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author Tom
 * @date 2021-05-24-17:54
 */
@Slf4j
@RestController
@RequestMapping("/logistics/logisticsRule")
@Api(tags={"物流匹配规则"})
public class LogisticsMatchRulesController {

    @Autowired
    private ILogisticsRulesService logisticsRulesService;

    @GetMapping("/list")
    @ApiOperation("查询物流匹配规则")
    public Page<LogisticsRuleVO> list(LogisticsRuleSearchReq logisticsRuleSearchReq) {
        log.info("查询物流规则请求内容是：{}",logisticsRuleSearchReq);
        return logisticsRulesService.list(logisticsRuleSearchReq);
    }

    @PostMapping("/add")
    @ApiOperation("新增物流匹配规则")
    public Integer add(@RequestBody @Validated(LogisticsRuleAddReq.Add.class) LogisticsRuleAddReq logisticsRuleAddReq) {
        log.info("新增物流规则内容是：{}",logisticsRuleAddReq);
        return logisticsRulesService.save(logisticsRuleAddReq);
    }

    @PostMapping("/update")
    @ApiOperation("修改物流匹配规则")
    public Boolean update(@RequestBody @Validated(LogisticsRuleAddReq.Update.class) LogisticsRuleAddReq logisticsRuleAddReq) {
        log.info("修改物流规则内容是：{}",logisticsRuleAddReq);
        return logisticsRulesService.update(logisticsRuleAddReq);
    }

    @PostMapping("/delete")
    @ApiOperation("删除物流匹配规则")
    @CacheEvict(value = Constant.LOGISTICS_LOGISTIC_MATCH_RULE,allEntries = true)
    public Boolean delete(@RequestBody @Validated DeleteReq deleteReq) {
        log.info("删除物流规则内容是：{}",deleteReq);
        return logisticsRulesService.removeByIds(deleteReq.getIds());
    }

    @GetMapping("/detail")
    @ApiOperation("物流规则匹配详情")
    public LogisticsRuleDetailVO detail(Long id) {
        log.info("物流规则详情请求：{}",id);
        LogisticsRuleDetailVO ruleVO = logisticsRulesService.getLogisticsRuleDetailById(id);
        return ruleVO;
    }

}
