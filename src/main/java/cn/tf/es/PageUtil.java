package cn.tf.es;

import java.util.List;

/**
 * 分页对象
 * @author Administrator
 *
 * @param <T>
 */
public class PageUtil<T> {
	//每页显示的记录数
	private int pageSize;
	//当前页码数
	private int pageNum;
	//总记录数
	private int rowCount;
	//总共多少页
	private int pageCount;
	//本次查询开始的行数
	private int rowStart;
	//每页显示的页码数
	private int everyPageCount;
	//每页显示的页码开始数
	private int everyPageStart;
	//每页显示的页码结束数
	private int everyPageEnd;
	//是否有上一页
	private boolean hasPrevious;
	//首页
	private int firstPageNum;
	//上一页
	private int previousPageNum;
	//是否有下一页
	private boolean hasNext;
	//下一页
	private int nextPageNum;
	//尾页
	private int lastPageNum;
	//本次查询的结果
	private List<T> list;

	public PageUtil() {

	}
	/**
	 * 创建分页对象
	 * @param pageNumString
	 * @param pageSizeString
	 * @param rowCount
	 */
	public PageUtil(String pageNumString, String pageSizeString, int rowCount) {
		//每页显示的记录数
		this.pageSize = pageSizeString == null ? 3 : Integer.parseInt(pageSizeString);
		//总记录数
		this.rowCount = rowCount;
		//总共多少页
		this.pageCount = (int) Math.ceil(rowCount * 1.0 / pageSize);
		//当前页码数
		this.pageNum = pageNumString == null ? 1 : Integer.parseInt(pageNumString);
		//如果当前页码数大于总页码数,去总页码数
		if (pageNum > pageCount && pageCount > 0) {
			this.pageNum = pageCount;
		}
		//本次查询开始的行数
		this.rowStart = (pageNum - 1) * pageSize;
		//每页显示的页码数
		this.everyPageCount = 5;
		//每页显示的页码开始数
		this.everyPageStart = pageNum - (everyPageCount / 2) < 1 ? 1 : pageNum - (everyPageCount / 2);
		//每页显示的页码结束数
		this.everyPageEnd = pageNum + (everyPageCount / 2) > pageCount ? pageCount : pageNum + (everyPageCount / 2);
		//是否有上一页
		if (pageNum > 1) {
			this.hasPrevious = true;
			//首页
			this.firstPageNum = 1;
			//上一页
			this.previousPageNum = pageNum - 1;
		}
		//是否有下一页
		if (pageNum < pageCount) {
			this.hasNext = true;
			//下一页
			this.nextPageNum = pageNum + 1;
			//尾页
			this.lastPageNum = pageCount;
		}
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getRowCount() {
		return rowCount;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public int getRowStart() {
		return rowStart;
	}

	public void setRowStart(int rowStart) {
		this.rowStart = rowStart;
	}

	public int getEveryPageCount() {
		return everyPageCount;
	}

	public void setEveryPageCount(int everyPageCount) {
		this.everyPageCount = everyPageCount;
	}

	public int getEveryPageStart() {
		return everyPageStart;
	}

	public void setEveryPageStart(int everyPageStart) {
		this.everyPageStart = everyPageStart;
	}

	public int getEveryPageEnd() {
		return everyPageEnd;
	}

	public void setEveryPageEnd(int everyPageEnd) {
		this.everyPageEnd = everyPageEnd;
	}

	public boolean isHasPrevious() {
		return hasPrevious;
	}

	public void setHasPrevious(boolean hasPrevious) {
		this.hasPrevious = hasPrevious;
	}

	public int getFirstPageNum() {
		return firstPageNum;
	}

	public void setFirstPageNum(int firstPageNum) {
		this.firstPageNum = firstPageNum;
	}

	public int getPreviousPageNum() {
		return previousPageNum;
	}

	public void setPreviousPageNum(int previousPageNum) {
		this.previousPageNum = previousPageNum;
	}

	public boolean isHasNext() {
		return hasNext;
	}

	public void setHasNext(boolean hasNext) {
		this.hasNext = hasNext;
	}

	public int getNextPageNum() {
		return nextPageNum;
	}

	public void setNextPageNum(int nextPageNum) {
		this.nextPageNum = nextPageNum;
	}

	public int getLastPageNum() {
		return lastPageNum;
	}

	public void setLastPageNum(int lastPageNum) {
		this.lastPageNum = lastPageNum;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

}
