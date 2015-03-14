package com.freetmp.springbatch.support;

import org.springframework.batch.item.ItemReader;

/*
 * 记录上一个读取值的读取器
 * 	其上一个的有效数据会在read()调用后被重置
 *  reset会清除记录的值
 * @author Pin Liu
 * @编写日期 2014年11月12日下午4:10:26
 * @param <T>
 */
public interface RecordLastReader<T> extends ItemReader<T> {
	
	T last();

}
