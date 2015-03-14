package com.freetmp.springbatch.support.composite;

import com.freetmp.springbatch.support.ResettableItemReader;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

/*
 * 三路读取器
 * @author Pin Liu
 * @编写日期 2014年11月10日下午3:17:03
 */
public class TripleAccessReader<L,M,R> extends MultipleAccessReader<Triple<L,M,R>>  {
	
	private static final Logger log = LoggerFactory.getLogger(TripleAccessReader.class);
	
	private ResettableItemReader<L> leftReader;
	
	private ResettableItemReader<M> middleReader;
	
	private ResettableItemReader<R> rightReader;

	public ResettableItemReader<L> getLeftReader() {
		return leftReader;
	}

	public void setLeftReader(ResettableItemReader<L> leftReader) {
		this.leftReader = leftReader;
	}

	public ResettableItemReader<M> getMiddleReader() {
		return middleReader;
	}

	public void setMiddleReader(ResettableItemReader<M> middleReader) {
		this.middleReader = middleReader;
	}

	public ResettableItemReader<R> getRightReader() {
		return rightReader;
	}

	public void setRightReader(ResettableItemReader<R> rightReader) {
		this.rightReader = rightReader;
	}

	@Override
	protected Triple<L, M, R> doReadNormally() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		L left = leftReader.read();
		M middle = middleReader.read();
		R right = rightReader.read();
		return Triple.of(left, middle, right);
	}

	@Override
	protected Triple<L, M, R> resolveReset(Triple<L, M, R> item) throws Exception {
		L left = item.getLeft();
		M middle = item.getMiddle();
		R right = item.getRight();
		if(left == null){
			left = resolveReset(leftReader);
		}
		if(middle == null){
			middle = resolveReset(middleReader);
		}
		if(right == null){
			right = resolveReset(rightReader);
		}
		return Triple.of(left, middle, right);
	}

	@Override
	protected Triple<L, M, R> resolveLast(Triple<L, M, R> item) throws Exception {
		L left = item.getLeft();
		M middle = item.getMiddle();
		R right = item.getRight();
		if(left == null){
			left = resolveLast(leftReader);
		}
		if(middle == null){
			middle = resolveLast(middleReader);
		}
		if(right == null){
			right = resolveLast(rightReader);
		}
		return Triple.of(left, middle, right);
	}

	@Override
	protected boolean isAllNull(Triple<L, M, R> item) {
		if(item.getLeft() != null) return false;
		if(item.getMiddle() != null) return false;
		if(item.getRight() != null) return false;		
		return true;
	}

	@Override
	protected boolean isAnyNull(Triple<L, M, R> item) {
		if(item.getLeft() == null) return true;
		if(item.getMiddle() == null) return true;
		if(item.getRight() == null) return true;
		return false;
	}


	protected boolean isAllReseted() {
		return leftReader.isReseted() && middleReader.isReseted() && rightReader.isReseted();
	}

	protected boolean isAllLasted(Triple<L, M, R> item) {
		if(item.getLeft() != leftReader.last()) return false;
		if(item.getMiddle() != middleReader.last()) return false;
		if(item.getRight() != rightReader.last()) return false;
		return true;
	}

	@Override
	protected void doReset() {
		this.leftReader.reset();
		this.rightReader.reset();
		this.middleReader.reset();
	}

	@Override
	public synchronized void clearResetMark() {
		this.leftReader.clearResetMark();
		this.middleReader.clearResetMark();
		this.rightReader.clearResetMark();
		super.clearResetMark();
	}

	public TripleAccessReader(ResettableItemReader<L> leftReader, ResettableItemReader<M> middleReader, ResettableItemReader<R> rightReader) {
		super();
		this.leftReader = leftReader;
		this.middleReader = middleReader;
		this.rightReader = rightReader;
	}
	
	public static <L,M,R> TripleAccessReader<L, M, R> of(ResettableItemReader<L> leftReader, ResettableItemReader<M> middleReader, ResettableItemReader<R> rightReader){
		return new TripleAccessReader<L, M, R>(leftReader, middleReader, rightReader);
	}

	@Override
	protected boolean isAnyNormal(Triple<L, M, R> item) {
		if(!leftReader.isReseted() && item.getLeft() != null && item.getLeft() != leftReader.last()){
			return true;
		}
		if(!middleReader.isReseted() && item.getMiddle() != null && item.getMiddle() != middleReader.last()){
			return true;
		}
		if(!rightReader.isReseted() && item.getRight() != null && item.getRight() != rightReader.last()){
			return true;
		}
		return false;
	}

}
