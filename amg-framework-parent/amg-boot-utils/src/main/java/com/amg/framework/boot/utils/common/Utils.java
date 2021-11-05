package com.amg.framework.boot.utils.common;

import com.amg.framework.boot.base.enums.ResponseCodeEnum;
import com.amg.framework.boot.base.enums.ResponseCodeEnumSupport;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.*;


/**
 * 通用工具类
 */
@Component
public class Utils {

	private final static Logger log = LoggerFactory.getLogger(Utils.class);


	/**
	 * 响应输出
	 */
	public static void print(Object object) {
		PrintWriter out = null;
		try {
			ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			HttpServletResponse response = requestAttributes.getResponse();
			response.addHeader("Cache-Control", "no-cache");
			response.setContentType("application/json;charset=utf-8");
			out = response.getWriter();
			out.print(object);
		} catch (Exception e) {
			log.warn("response error");
		} finally {
			if (out != null) {
				out.flush();
				out.close();
			}
		}
	}


	/**
	 * 序列化
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public static byte[] serialize(Serializable obj) throws Exception {
		if (obj == null) {
			return null;
		}
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		ObjectOutputStream oo = new ObjectOutputStream(bo);
		oo.writeObject(obj);
		return bo.toByteArray();
	}


	/**
	 * 反序列化
	 * @param objBytes
	 * @return
	 * @throws Exception
	 */
	public static Object deserialize(byte[] objBytes) throws Exception {
		if (objBytes == null || objBytes.length == 0) {
			return null;
		}
		ByteArrayInputStream bi = new ByteArrayInputStream(objBytes);
		ObjectInputStream oi = new ObjectInputStream(bi);
		return oi.readObject();
	}


	/**
	 * 对象序列化为字符串
	 **/
	public static String serializableString(Object obj) throws Exception {
		if (obj == null) {
			return null;
		}
		String serStr = null;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
		objectOutputStream.writeObject(obj);
		serStr = byteArrayOutputStream.toString("ISO-8859-1");
		serStr = java.net.URLEncoder.encode(serStr, "UTF-8");
		objectOutputStream.close();
		byteArrayOutputStream.close();
		return serStr;
	}


	/**
	 * 字符串反序列化为对象
	 * */
	public static Object deserializeString(String serStr) throws Exception {
		if (StringUtils.isBlank(serStr)) {
			return null;
		}
		ByteArrayInputStream byteArrayInputStream = null;
		ObjectInputStream objectInputStream = null;
		try {
			String redStr = java.net.URLDecoder.decode(serStr, "UTF-8");
			byteArrayInputStream = new ByteArrayInputStream(redStr.getBytes("ISO-8859-1"));
			objectInputStream = new ObjectInputStream(byteArrayInputStream);
			return objectInputStream.readObject();
		} finally {
			if (objectInputStream != null)
				objectInputStream.close();
			if (byteArrayInputStream != null)
				byteArrayInputStream.close();
		}
	}


	/**
	 * 校验是否 Serializable
	 * @param objs
	 * @throws NotSerializableException
	 */
	public static void validateSerializable(Object... objs) throws NotSerializableException {
		for (Object obj : objs) {
			if (obj != null) {
				if (!(obj instanceof Serializable)) {
					throw new NotSerializableException("parameter must be serializable");
				}
			}
		}
	}

}
