该jar包区分网关版本和普通（子服务）版本,涉及到的配置参数如下
swagger.enable 是否启用swagger
swagger.isZuul 是否为微服务网关服务
swagger.basePackage 扫描包路径
swagger.basePath 项目根路径
swagger.apiTitle 接口文档页面标题展示字段
swagger.resourceList 当isZuul为true要设置该参数：目录资源，逗号分割，服务名#服务跟路径