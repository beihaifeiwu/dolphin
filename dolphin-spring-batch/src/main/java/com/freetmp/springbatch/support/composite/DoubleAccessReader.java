package com.freetmp.springbatch.support.composite;

import com.freetmp.springbatch.support.ResettableItemReader;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

/*
 * 双路读取器，每次返回两个读取器读取到的一对元组
 * @author Pin Liu
 * @编写日期 2014年11月12日下午3:17:26
 * @param <L>
 * @param <R>
 */
public class DoubleAccessReader<L,R> extends MultipleAccessReader<Pair<L,R>> {
	
	private static final Logger log = LoggerFactory.getLogger(DoubleAccessReader.class);
	
	protected ResettableItemReader<L> leftReader;
	
	protected ResettableItemReader<R> rightReader;
	
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
	protected Pair<L, R> doReadNormally() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		L left = leftReader.read();
		R right = rightReader.read();
		return Pair.of(left, right);
	}

	@Override
	protected Pair<L, R> resolveReset(Pair<L, R> item) throws Exception {
		L left = item.getLeft();
		R right = item.getRight();
		if(left == null){
			left = resolveReset(leftReader);
		}
		if(right == null){
			right = resolveReset(rightReader);
		}
		return Pair.of(left, right);
	}

	@Override
	protected Pair<L, R> resolveLast(Pair<L, R> item) throws Exception {
		L left = item.getLeft();
		R right = item.getRight();
		if(left == null){
			left = resolveLast(leftReader);
		}
		if(right == null){
			right = resolveLast(rightReader);
		}
		return Pair.of(left, right);
	}
	
	/*
	 * 是否读取到的所有值均为null
	 * @author Pin Liu
	 * @return
	 */
	protected boolean isAllNull(Pair<L,R> item){
		return (item.getLeft() == null) && (item.getRight() == null);
	}
	
	/*
	 * 是否读取到的值中有null
	 * @author Pin Liu
	 */
	protected boolean isAnyNull(Pair<L,R> item) {
		return (item.getLeft() == null) || (item.getRight() == null);
	}
	
	/*
	 * 是否所有的读取器都已经被重置过
	 * @author Pin Liu
	 * @编写日期: 2014年11月12日下午4:39:14
	 * @return
	 */
	protected boolean isAllReseted(){
		
		return leftReader.isReseted() && rightReader.isReseted();
	}
	
	/*
	 * 是否所有的值均是读取器最后的值
	 * @author Pin Liu
	 * @编写日期: 2014年11月12日下午4:42:51
	 * @return
	 */
	protected boolean isAllLasted(Pair<L,R> item) {
		
		return (leftReader.last() == item.getLeft()) && (rightReader.last() == item.getRight());
	}


	@Override
	protected boolean isAnyNormal(Pair<L, R> item) {
		if(!leftReader.isReseted() && item.getLeft() != null && item.getLeft() != leftReader.last()){
			return true;
		}
		if(!rightReader.isReseted() && item.getRight() != null && item.getRight() != rightReader.last()){
			return true;
		}
		return false;
	}
	
	@Override
	protected void doReset() {
		this.leftReader.reset();
		this.rightReader.reset();
		log.debug("reset the PairAccessReader");
	}

	@Override
	public synchronized void clearResetMark() {
		super.clearResetMark();
		this.leftReader.clearResetMark();
		this.rightReader.clearResetMark();
	}

	public DoubleAccessReader() {
		super();
	}

	public DoubleAccessReader(ResettableItemReader<L> leftReader, ResettableItemReader<R> rightReader) {
		super();
		this.leftReader = leftReader;
		this.rightReader = rightReader;
	}

	public static <L,R> DoubleAccessReader<L,R> of(ResettableItemReader<L> leftReader, ResettableItemReader<R> rightReader){
		return new DoubleAccessReader<L, R>(leftReader, rightReader);
		
	}

}
