package com.freetmp.springbatch.support;

import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.util.List;

/*
 * 可分页的读取器
 * @author Pin Liu
 * @编写日期 2014年11月17日上午10:02:49
 * @param <T>
 */
public abstract class PageableItemReaderSupport<T> extends ResettableItemReaderSupport<T> {
	
	/*
	 * 缓存当前页的内容
	 */
	protected List<T> pageContent = null;

	/*
	 * 当前页的当前索引
	 */
	protected volatile int indexOfCurrentPage = 0;
	
	/*
	 * 分页的页面大小
	 */
	protected int pageSize = 100;
	
	/*
	 * 当前页
	 */
	protected int currentPage = 0;
	
	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public List<T> getPageContent() {
		return pageContent;
	}

	public int getIndexOfCurrentPage() {
		return indexOfCurrentPage;
	}

	@Override
	protected void doReset() {
		this.pageContent = null;
		this.indexOfCurrentPage = 0;
		this.currentPage = 0;
	}

	@Override
	protected T doRead() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		
		if(pageContent == null || pageContent.size() <= indexOfCurrentPage){
			int offset = pageSize * currentPage;
			int limit = pageSize;
			pageContent = readPage(offset, limit);
			currentPage ++;
			indexOfCurrentPage = 0;
		}
		
		if(pageContent != null && pageContent.size() > indexOfCurrentPage){
			return pageContent.get(indexOfCurrentPage++);
		}
		
		return null;
	}
	
	/*
	 * 读取一页的内容需要子类去实现
	 * @author Pin Liu
	 * @编写日期: 2014年11月17日上午10:11:24
	 * @return
	 */
	protected abstract List<T> readPage(int page, int pageSize);

}
