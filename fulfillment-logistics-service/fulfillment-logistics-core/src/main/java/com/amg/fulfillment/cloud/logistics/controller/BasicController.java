package com.amg.fulfillment.cloud.logistics.controller;


import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RestController;


@Api(tags={"物流模块接口"})
@RestController
public class BasicController {

//    @Autowired
//    private grpcclientservice grpcClientService;

//    @Autowired
//    private RocketmqUtils rocketmqUtils;
//
//
//    @ApiOperation(value = "测试请求grpc接口")
//    @GetMapping("testGrpc")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "page", value = "页码", dataType = "string", paramType = "query", required = true),
//            @ApiImplicitParam(name = "row", value = "行数", dataType = "string", paramType = "query")
//    })
//    public String getBasicListInfo(String page, String row) {
//        if (StringUtils.isBlank(page) || StringUtils.isBlank(row))
//            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "请求页码和行数不能为空");
//        SendResult sendResult = rocketmqUtils.syncSend("test_topic", "test rocketmq logistics");
//        System.out.println(sendResult);
//        return grpcClientService.getStub().hello(ExampleRequest.newBuilder().setName(page).build()).getDataList().toString();
//    }
//
//
//    @ApiOperation(value = "测试请求grpc接口")
//    @GetMapping("testGrpcamg")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "page", value = "页码", dataType = "string", paramType = "query", required = true),
//            @ApiImplicitParam(name = "row", value = "行数", dataType = "string", paramType = "query")
//    })
//    public String getBasicListInfo1(String page, String row) {
//        if (StringUtils.isBlank(page) || StringUtils.isBlank(row))
//            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "请求页码和行数不能为空");
//        SendResult sendResult = rocketmqUtils.syncSend("test_topic", "test rocketmq logistics");
//        System.out.println(sendResult);
//        return grpcClientService.getAmgRemoteBlockingStub().amgtest(AmgRequest.newBuilder().setName(page).build()).getDataList().toString();
//    }

}
