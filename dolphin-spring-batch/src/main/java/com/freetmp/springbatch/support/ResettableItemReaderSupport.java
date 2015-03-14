package com.freetmp.springbatch.support;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

/*
 * 可重置读取器的支持类
 * @author Pin Liu
 * @编写日期 2014年11月12日下午4:13:35
 * @param <T>
 */
public abstract class ResettableItemReaderSupport<T> implements ResettableItemReader<T>,StepExecutionListener {

	protected T lastItem;
	
	protected T currentItem;
	
	protected boolean reseted;
		
	@Override
	public boolean isReseted() {
		return reseted;
	}

	@Override
	public synchronized void clearResetMark() {
		reseted = false;
	}

	@Override
	public T last() {
		return lastItem;
	}

	@Override
	public T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		
		if(currentItem != null){
			lastItem = currentItem; //使用lastItem记录上次读到的值
		}
		currentItem = doRead(); //使用currentItem记录到当前读取到的值
		
		return currentItem;
	}
	
	@Override
	public synchronized void reset() {
		lastItem = null; //释放lastItem
		currentItem = null; //释放当前读取到值
		reseted = true;
		doReset();
	}
	
	@Override
	public void beforeStep(StepExecution stepExecution) {
		this.reset();
		this.clearResetMark();
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		return stepExecution.getExitStatus();
	}

	protected abstract void doReset();

	protected abstract T  doRead() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException;
}
