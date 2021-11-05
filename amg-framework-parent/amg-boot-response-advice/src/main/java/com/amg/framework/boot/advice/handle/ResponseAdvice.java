package com.amg.framework.boot.advice.handle;

import com.amg.framework.boot.advice.annotation.IgnoreResponseAdvice;
import com.amg.framework.boot.base.enums.ResponseCodeEnum;
import com.amg.framework.boot.base.exception.GlobalException;
import com.amg.framework.boot.base.model.Result;
import com.amg.framework.boot.utils.i18n.I18nUtils;
import com.amg.framework.boot.utils.pagehelper.model.ExternalPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import springfox.documentation.swagger.web.ApiResourceController;
import springfox.documentation.swagger2.web.Swagger2Controller;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static com.amg.framework.boot.advice.serializer.AdviceSerializer.printSerializer;


/**
 * 全局响应代理
 */
@ControllerAdvice
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

	@Autowired
	public HttpServletRequest request;

	@Autowired
	public HttpServletResponse response;


	/**
	 * 类名或者类全路径
	 */
	private static final List IGNORE_CLASS_LIST = new ArrayList<>();

	static {
		// swagger
		IGNORE_CLASS_LIST.add(ApiResourceController.class);
		IGNORE_CLASS_LIST.add(Swagger2Controller.class);
		// actuator
		IGNORE_CLASS_LIST.add("org.springframework.boot.actuate.endpoint.web.servlet.AbstractWebMvcEndpointHandlerMapping$OperationHandler");
	}


	@Override
	public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
		if (methodParameter.getDeclaringClass().isAnnotationPresent(IgnoreResponseAdvice.class)
				|| methodParameter.getMethod().isAnnotationPresent(IgnoreResponseAdvice.class)) {
			return false;
		} else if (IGNORE_CLASS_LIST.contains(methodParameter.getDeclaringClass().getName())
				|| IGNORE_CLASS_LIST.contains(methodParameter.getDeclaringClass())) {
			return false;
		}
//		for (Class cls : methodParameter.getDeclaringClass().getInterfaces()) {
//			if (cls.isAnnotationPresent(FeignClient.class))
//				return false;
//		}
		return true;
	}


	@Override
	public Object beforeBodyWrite(Object obj, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest req, ServerHttpResponse res) {
		try {
			Result result;
			if (obj instanceof Resource)
				return obj;
//			if (obj instanceof Page) {
//				if (((Page) obj).isExternalPage())
//					obj = new ExternalPage((Page) obj);
//			}
			if (obj instanceof Page) {
				obj = new com.amg.framework.boot.utils.pagehelper.model.Page(
						((Page) obj).getRecords(), Long.valueOf(((Page) obj)
						.getCurrent()).intValue(), Long.valueOf(((Page) obj)
						.getSize()).intValue(), ((Page) obj).getTotal());
			}
			if (obj instanceof Result) {
				result = (Result) obj;
			} else {
				result = Result.create(obj);
			}
			result.setMsg(I18nUtils.i18n(result.getMsg())); // 本地国际化
			printSerializer(result);
			return null;
		} catch (Exception e) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, e.getMessage());
		}
	}

}
