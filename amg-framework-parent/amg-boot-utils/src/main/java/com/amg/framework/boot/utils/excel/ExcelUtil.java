package com.amg.framework.boot.utils.excel;

import java.io.*;
import java.net.URLEncoder;
import java.util.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.multipart.MultipartFile;

public class ExcelUtil<T> {
	private String[] header;
	private List<T> data;
	private XSSFWorkbook wb;
	private XSSFSheet sheet;
	private OutputStream oStream;

	private JSONArray array;

	boolean hasHeader = false;
	private InputStream inputStream;

	/**
	 * JAVA 对象数据、 高级样式,请自己写<br>
	 * <p>
	 * ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	 * ExcelUtil<XxxVO> excelRead = new ExcelUtil<XxxVO>(header, list, outputStream,
	 * width); excelRead.write(new ExcelUtil.ExcelMapToRow<XxxVO>() {
	 * 
	 * @Override public void toRow(XxxVO xx, HSSFRow row) { //高级样式写这里
	 *           row.createCell(0).setCellValue(xx.getId());
	 *           row.createCell(1).setCellValue(xx.getName()); .... } });
	 *           outHostExcel("文件名", new
	 *           ByteArrayInputStream(outputStream.toByteArray()), response);
	 *           outExcel("文件名", new
	 *           ByteArrayInputStream(outputStream.toByteArray()));
	 *           <p>
	 * @param header  头部标题
	 * @param data    是导入到excel的内容
	 * @param oStream 输出io流
	 * @param width   每一列宽度
	 * 
	 */
	public ExcelUtil(String[] header, List<T> data, OutputStream oStream, int[] width) {
		this.header = header;
		this.data = data;
		this.oStream = oStream;
		wb = new XSSFWorkbook();
		sheet = wb.createSheet();
		if (width != null) {
			for (int i = 0; i < width.length; i++) {
				sheet.setColumnWidth(i, width[i]);
			}
		}
	}

	/**
	 * JsonArray生成Excel数据<br>
	 * Array中的每一个json内的key对应Excel每一列
	 * 
	 * @param jsonArray 数据
	 * @param map       width_每一列宽度、header_excel表头、key_json里面key
	 * @throws IOException
	 */

	public static InputStream fileStream(JSONArray jsonArray, Map<String, String[]> map) throws IOException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		int[] w = StringToInt((String[]) map.get("width"));
		String[] h = (String[]) map.get("header");
		String[] k = (String[]) map.get("key");

		ExcelUtil<JSONObject> excelRead = new ExcelUtil<JSONObject>(h, jsonArray, outStream, w);
		excelRead.jsonWrite(new ExcelUtil.ExcelMapToRow<JSONObject>() {
			@Override
			public void toRow(JSONObject json, XSSFRow row) {
				for (int i = 0; i < h.length; i++) {
					String str = json.getString(k[i]);
					row.createCell(i).setCellValue(StringUtils.isEmpty(str) ? "" : str);
				}
			}
		});
		return new ByteArrayInputStream(outStream.toByteArray());
	}

	/**
	 * 本地项目导出调用
	 * 
	 * @param name
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	public static void outHostExcel(String name, InputStream in, HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.reset();
		response.setContentType("application/octet-stream");
		if ("firefox".equals(getExplorerType(request))) {
			String excelName = new String((name + ".xlsx").getBytes("GB2312"), "ISO-8859-1");
			response.setHeader("Content-Disposition", "attachment; filename=" + excelName);
		} else {
			String excelName = URLEncoder.encode(name + ".xlsx", "UTF-8");
			response.setHeader("Content-Disposition", "attachment;filename=" + excelName);
		}
		ServletOutputStream out = response.getOutputStream();
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			bis = new BufferedInputStream(in);
			bos = new BufferedOutputStream(out);
			byte[] buff = new byte[2048];
			int bytesRead;
			while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
				bos.write(buff, 0, bytesRead);
			}
		} catch (final IOException e) {
			throw e;
		} finally {
			if (bis != null)
				bis.close();
			if (bos != null)
				bos.close();
		}
	}

	/**
	 * 远程+本地 导出都可调用
	 * 
	 * @param name
	 * @param in
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public static ResponseEntity<byte[]> outExcel(String name, InputStream in, HttpServletRequest request) throws IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/octet-stream");
		if ("firefox".equals(getExplorerType(request))) {
			String excelName = new String((name + ".xlsx").getBytes("GB2312"), "ISO-8859-1");
			headers.add("Content-Disposition", "attachment; filename=" + excelName);
		} else {
			String excelName = URLEncoder.encode(name + ".xlsx", "UTF-8");
			headers.add("Content-Disposition", "attachment;filename=" + excelName);
		}
		return new ResponseEntity<byte[]>(inputTobyte(in), headers, HttpStatus.OK);
	}

	private ExcelUtil(String[] header, JSONArray array, OutputStream oStream, int[] width) {
		this.header = header;
		this.array = array;
		this.oStream = oStream;
		wb = new XSSFWorkbook();
		sheet = wb.createSheet();
		if (width != null) {
			for (int i = 0; i < width.length; i++) {
				sheet.setColumnWidth(i, width[i]);
			}
		}
	}

	private void setHeader() {
		XSSFRow row = sheet.createRow(0);
		for (int i = 0; i < header.length; i++)
			row.createCell(i).setCellValue(header[i]);
	}

	public void write(ExcelMapToRow<T> excelMapToRow) throws IOException {
		setHeader();
		for (int i = 0; i < data.size(); i++) {
			XSSFRow row = sheet.createRow(i + 1);
			excelMapToRow.toRow(data.get(i), row);
		}
		this.wb.write(this.oStream);
	}

	private void jsonWrite(ExcelMapToRow<T> excelMapToRow) throws IOException {
		setHeader();
		for (int i = 0; i < array.size(); i++) {
			XSSFRow row = sheet.createRow(i + 1);
			excelMapToRow.toRow(array.getJSONObject(i), row);
		}
		this.wb.write(this.oStream);
	}

	public XSSFWorkbook getWork() {
		return this.wb;
	}

	public static interface ExcelMapToRow<T> {
		void toRow(T t, XSSFRow row);

		void toRow(JSONObject json, XSSFRow row);

	}

	private static int[] StringToInt(String[] str) {
		if (str == null)
			return null;
		int[] w = new int[str.length];
		for (int i = 0; i < str.length; i++) {
			w[i] = Integer.parseInt(str[i]);
		}
		return w;
	}

	private static byte[] inputTobyte(InputStream inStream) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buff = new byte[2048];
		int bytesRead = 0;
		while ((bytesRead = inStream.read(buff, 0, buff.length)) > 0) {
			baos.write(buff, 0, bytesRead);
		}
		return baos.toByteArray();
	}

	/**
	 * 设置表基本信息
	 * 
	 * @param header EXCEL表头
	 * @param key    json中key
	 * @param width  列宽-根据自己需求来写，默认可以不用
	 * @return
	 */
	public static Map<String, String[]> createMap(String[] header, String[] key, String[] width) {
		Map<String, String[]> title = new HashMap<>();
		title.put("header", header);
		title.put("key", key);
		title.put("width", width);
		return title;
	}

	/**
	 * 获取浏览器类型
	 * 
	 * @param request
	 * @return
	 */
	public static String getExplorerType(HttpServletRequest request) {
		String agent = request.getHeader("USER-AGENT");
		if (agent != null && agent.toLowerCase().indexOf("firefox") > 0) {
			return "firefox";
		} else if (agent != null && agent.toLowerCase().indexOf("msie") > 0) {
			return "ie";
		} else if (agent != null && agent.toLowerCase().indexOf("chrome") > 0) {
			return "chrome";
		} else if (agent != null && agent.toLowerCase().indexOf("opera") > 0) {
			return "opera";
		} else if (agent != null && agent.toLowerCase().indexOf("safari") > 0) {
			return "safari";
		}
		return "others";
	}

	@SuppressWarnings("resource")
	public ExcelUtil(InputStream inputStream) {
		try {
			this.sheet = new XSSFWorkbook(inputStream).getSheetAt(0);
			this.inputStream = inputStream;
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static interface ExcelMapRow<T> {
		T mapRow(XSSFRow row);
	}

	public List<T> read(ExcelMapRow<T> mapRow) throws IOException {
		/*List<T> data = new ArrayList<T>();
		for (int index = 1; index < sheet.getPhysicalNumberOfRows(); index++) {
			T t = mapRow.mapRow(sheet.getRow(index));
			if (t != null)
				data.add(t);
			else
				break;
		}
		if (inputStream != null)
			inputStream.close();
		return data;*/
		return this.read(mapRow,1);
	}

	/**
	 *
	 * @param mapRow
	 * @param row 开始操作的行下标
	 * @return
	 * @throws IOException
	 */
	public List<T> read(ExcelMapRow<T> mapRow,int row) throws IOException {
		List<T> data = new ArrayList<T>();
		for (int index = row; index < sheet.getPhysicalNumberOfRows(); index++) {
			T t = mapRow.mapRow(sheet.getRow(index));
			if (t != null)
				data.add(t);
			else
				break;
		}
		if (inputStream != null)
			inputStream.close();
		return data;
	}

	/**######### 以下为Easy Poi 导入导出法 ##########*/
	/**
	 * 导出excel (带是否创建头参数）
	 * @param list<?> 要导出的数据列表 其中的 Bean 要配上相应的 @Excel 注解
	 * @param title 标题
	 * @param sheetName sheet名称
	 * @param pojoClass 列表中的 Bean class
	 * @param fileName 导出的文件名
	 * @param isCreateHeader 是否创建Header
	 * @param response 相应的response
	 */
	public static void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass,
								   String fileName, boolean isCreateHeader, HttpServletResponse response){
		ExportParams exportParams = new ExportParams(title, sheetName);
		exportParams.setCreateHeadRows(isCreateHeader);
		defaultExport(list, pojoClass, fileName, response, exportParams);
	}

	/**
	 * 导出excel
	 * @param list<?>  要导出的数据列表 其中的 Bean 要配上相应的 @Excel 注解
	 * @param title  标题
	 * @param sheetName  sheet名称
	 * @param pojoClass 列表中的 Bean class
	 * @param fileName 导出的文件名
	 * @param response 相应的response
	 */
	public static void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass,String fileName,
								   HttpServletResponse response){
		defaultExport(list, pojoClass, fileName, response, new ExportParams(title, sheetName));
	}

	/**
	 * 导出excel （多sheet导出）
	 * @param list 多个Map key title 对应表格 title key entity 对应表格对应实体 key data collection 数据
	 * @param fileName 导出的文件名
	 * @param response 相应的response
	 */
	public static void exportExcel(List<Map<String, Object>> list, String fileName, HttpServletResponse response){
		defaultExport(list, fileName, response);
	}

	private static void defaultExport(List<?> list, Class<?> pojoClass, String fileName,
									  HttpServletResponse response, ExportParams exportParams) {
		Workbook workbook = ExcelExportUtil.exportExcel(exportParams,pojoClass,list);
		if (workbook != null); downLoadExcel(fileName, response, workbook);
	}

	private static void downLoadExcel(String fileName, HttpServletResponse response, Workbook workbook) {
		try {
			ServletOutputStream out = response.getOutputStream();
			response.setCharacterEncoding("UTF-8");
			response.setHeader("content-Type", "application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
			workbook.write(out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void defaultExport(List<Map<String, Object>> list, String fileName, HttpServletResponse response) {
		Workbook workbook = ExcelExportUtil.exportExcel(list, ExcelType.HSSF);
		if (workbook != null);
		downLoadExcel(fileName, response, workbook);
	}

	public static void getExport(List list, String title, String sheetName, Class<?> pojoClass, FileOutputStream out) {
		Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(title, sheetName), pojoClass, list);
		if (workbook != null);
		try {
			workbook.write(out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static InputStream getExportInputStream(List list, String title, String sheetName, Class<?> pojoClass) {
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		OutputStream output = null;
		Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(title, sheetName), pojoClass, list);
		if (workbook != null) {
			try {
				output = new BufferedOutputStream(arrayOutputStream);
				workbook.write(output);
				output.flush();
				return new ByteArrayInputStream(arrayOutputStream.toByteArray());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					output.close();
					arrayOutputStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
		return null;
	}



	/**
	 * 导入excel
	 * @param filePath
	 * @param titleRows
	 * @param headerRows
	 * @param pojoClass
	 * @param <T>
	 * @return
	 */
	public static <T> List<T> importExcel(String filePath,Integer titleRows,Integer headerRows, Class<T> pojoClass){
		if (org.apache.commons.lang3.StringUtils.isBlank(filePath)){
			return null;
		}
		ImportParams params = new ImportParams();
		params.setTitleRows(titleRows);
		params.setHeadRows(headerRows);
		List<T> list = null;
		try {
			list = ExcelImportUtil.importExcel(new File(filePath), pojoClass, params);
		}catch (NoSuchElementException e){
		} catch (Exception e) {
			e.printStackTrace();
		} return list;
	}

	/**
	 * 导入excel
	 * @param file
	 * @param titleRows
	 * @param headerRows
	 * @param pojoClass
	 * @param <T>
	 * @return
	 */
	public static <T> List<T> importExcel(MultipartFile file, Integer titleRows, Integer headerRows, Class<T> pojoClass){
		if (file == null){ return null;
		}
		ImportParams params = new ImportParams();
		params.setTitleRows(titleRows);
		params.setHeadRows(headerRows);
		List<T> list = null;
		try {
			list = ExcelImportUtil.importExcel(file.getInputStream(), pojoClass, params);
		}catch (NoSuchElementException e){
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

}
