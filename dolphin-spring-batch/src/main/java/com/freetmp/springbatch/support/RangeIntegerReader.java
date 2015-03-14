package com.freetmp.springbatch.support;

import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

/*
 * 可重置范围整数读取器
 * @author Pin Liu
 * @编写日期 2014年11月12日下午2:42:45
 */
public class RangeIntegerReader extends ResettableItemReaderSupport<Integer>{
	
	//范围内不包含最大值
	private int max;
	
	//范围内包含最小值
	private int min;
	
	private volatile int count;

	public int getMax() {
		return max;
	}

	public int getMin() {
		return min;
	}

	@Override
	protected void doReset() {
		this.count = min;
	}

	/*
	 * 构造一个范围为[min,max)的读取器
	 * @param min
	 * @param max
	 */
	public RangeIntegerReader(int min, int max) {
		super();
		this.max = max;
		this.min = min;
		this.count = min;
	}
	
	public static RangeIntegerReader range(int min,int max){
		return new RangeIntegerReader(min, max);
	}

	@Override
	protected Integer doRead() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		if(count >= max) return null;
		return count++;
	}

}
