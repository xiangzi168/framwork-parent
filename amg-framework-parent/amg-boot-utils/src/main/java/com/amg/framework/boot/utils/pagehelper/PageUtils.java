package com.amg.framework.boot.utils.pagehelper;




/**
 * 分页处理
 * */
public class PageUtils {

	public static void builder(String currPage, String row) {
		builder(currPage, row, true);
	}

	public static void builder(int currPage, int row) {
		builder(currPage, row, true);
	}

	/**
	 * 分页
	 * @param currPage 当前页
	 * @param row 每页显示行数
	 * @param isCount 是否查询总条数
	 */
	public static void builder(String currPage, String row, boolean isCount) {
		int nowPage = Integer.valueOf(currPage) <= 0 ? 1 : Integer.valueOf(currPage);
		int rowNum = Integer.valueOf(row) <= 0 ? 1 : Integer.valueOf(row);
		startPage(nowPage, rowNum, isCount);
	}

	/**
	 * 分页
	 * @param currPage 当前页
	 * @param row 每页显示行数
	 * @param isCount 是否查询总条数
	 */
	public static void builder(int currPage, int row, boolean isCount) {
		int nowPage = currPage <= 0 ? 1 : currPage;
		int rowNum = row <= 0 ? 1 : row;
		startPage(nowPage, rowNum, isCount);
	}


	private static void startPage(int nowPage, int rowNum, boolean isCount) {
		// PageHelper.startPage(nowPage, rowNum, isCount);
	}

}
