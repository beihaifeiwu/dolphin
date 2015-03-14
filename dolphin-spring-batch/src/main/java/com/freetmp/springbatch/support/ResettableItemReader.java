package com.freetmp.springbatch.support;

import org.springframework.batch.item.ItemReader;

/*
 * 可重置的读入器
 * @author Pin Liu
 * @编写日期 2014年11月12日上午11:54:59
 * @param <T>
 */
public interface ResettableItemReader<T> extends ItemReader<T>,RecordLastReader<T> {

	/*
	 * 调用重置方法即可重置读取器至初始化状态
	 * @author Pin Liu
	 * @编写日期: 2014年11月12日上午11:56:21
	 */
	void reset();
	
	/*
	 * 是否已经被重置过 true 是   false 否
	 * @author Pin Liu
	 * @编写日期: 2014年11月12日下午4:34:29
	 */
	boolean isReseted();
	
	/*
	 * 清除重置的痕迹
	 * @author Pin Liu
	 * @编写日期: 2014年11月12日下午5:28:34
	 */
	void clearResetMark();
}
