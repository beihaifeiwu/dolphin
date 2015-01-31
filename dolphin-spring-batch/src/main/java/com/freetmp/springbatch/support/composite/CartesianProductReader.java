package com.freetmp.springbatch.support.composite;

import com.freetmp.springbatch.support.ResettableItemReader;
import com.freetmp.springbatch.support.ResettableItemReaderSupport;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

/**
 * 以笛卡尔乘积的方式读取连个读取器的内容
 * @author Pin Liu
 * @编写日期 2014年11月12日上午11:57:40
 * @param <L>
 * @param <R>
 */
public class CartesianProductReader<L, R> extends ResettableItemReaderSupport<Pair<L, R>> {
	
	private static final Logger log = LoggerFactory.getLogger(CartesianProductReader.class);

	protected ResettableItemReader<L> leftReader;
	
	protected ResettableItemReader<R> rightReader;
	
	protected volatile L left;
	
	public ResettableItemReader<L> getLeftReader() {
		return leftReader;
	}

	public void setLeftReader(ResettableItemReader<L> leftReader) {
		this.leftReader = leftReader;
	}

	public ResettableItemReader<R> getRightReader() {
		return rightReader;
	}

	public void setRightReader(ResettableItemReader<R> rightReader) {
		this.rightReader = rightReader;
	}

	@Override
	protected Pair<L, R> doRead() throws Exception, UnexpectedInputException, 
								ParseException, NonTransientResourceException {
		
		if(left == null){ //初始化重置左读取器和右读取器
			leftReader.reset();
			rightReader.reset();
			left = leftReader.read();
			log.debug("initialize the CartesianProductReader and reset the L R reader");
		}
		
		if(left == null) return null; //读取结束
		
		R right = rightReader.read();
		while(right == null){ //找到非空的右读取器中的值否则结束
			left = leftReader.read();
			if(left == null) return null; //读取结束
			rightReader.reset();
			right = rightReader.read();
		}
		
		return Pair.of(left, right);
	}

	public CartesianProductReader() {
		super();
	}

	public CartesianProductReader(ResettableItemReader<L> leftReader, ResettableItemReader<R> rightReader) {
		super();
		this.leftReader = leftReader;
		this.rightReader = rightReader;
	}
	
	public static <L,R> CartesianProductReader<L,R> of(ResettableItemReader<L> leftReader, ResettableItemReader<R> rightReader){
		return new CartesianProductReader<L, R>(leftReader, rightReader);
	}

	@Override
	protected void doReset() {
		this.left = null;
		this.leftReader.reset();
		this.rightReader.reset();
		log.debug("reset the CartesianProductReader");
	}

}
