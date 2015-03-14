package com.freetmp.springbatch.support;

import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

/*
 * 永远返回空的可重置读取器
 * @author Pin Liu
 * @编写日期 2014年11月12日下午3:00:05
 * @param <T>
 */
public class NullReader<T> extends ResettableItemReaderSupport<T> {

	@Override
	public T doRead() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		return null;
	}

	@Override
	public void doReset() {
		
	}
	
	public static <T>  NullReader<T> of(Class<T> t){
		return new NullReader<T>();
	}

}
