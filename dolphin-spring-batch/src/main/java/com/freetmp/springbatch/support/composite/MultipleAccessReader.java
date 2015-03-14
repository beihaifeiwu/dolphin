package com.freetmp.springbatch.support.composite;

import com.freetmp.springbatch.support.RecordLastReader;
import com.freetmp.springbatch.support.ResettableItemReader;
import com.freetmp.springbatch.support.ResettableItemReaderSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

/*
 * 多路读取器支持基类
 *   * 	有3种读取策略
 * 	 1.任何一个子读取器读取完成则整个读取器读取完成
 *   2.所有子读取器均读写完成则整个读取器读取完成，提前读取完成的子读取器会被重置并继续读取
 *   3.所有子读取器均读写完成则整个读取器读取完成，提前读取完成的子读取器会使用最后被读取到的值继续参与读取
 * @author Pin Liu
 * @编写日期 2014年11月13日上午9:55:46
 * @param <T>
 */
public abstract class MultipleAccessReader<T> extends ResettableItemReaderSupport<T> {
	
	private static final Logger log = LoggerFactory.getLogger(MultipleAccessReader.class);

	public static final int POLICY_IMMEDIATELY = 1;
	public static final int POLICY_GREEDY_RESET = 2;
	public static final int POLICY_GREEDY_REPEAT = 3;
	
	protected MultipleAccessPolicy policy = MultipleAccessPolicy.IMMEDIATELY;

	public MultipleAccessReader() {
		super();
	}
	
	public MultipleAccessPolicy getPolicy() {
		return policy;
	}

	public void setPolicy(MultipleAccessPolicy policy) {
		this.policy = policy;
	}

	@Override
	protected T doRead() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		T item = null;
		switch (policy) {
		case GREEDY_REPEAT:
			item = readRepeatly();
			break;
		case GREEDY_RESET:
			item = readResetly();
			break;
		case IMMEDIATELY:
			item = readImmediately();
			break;
		default:
			break;
		}
		
		return item;
	}
	
	/*
	 * 正常读取所有子读取器的内容
	 * @author Pin Liu
	 * @编写日期: 2014年11月13日上午11:35:43
	 * @return
	 */
	protected abstract T doReadNormally() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException;

	/*
	 * 支持策略 POLICY_IMMEDIATELY 的读取方式
	 * @author Pin Liu
	 */
	protected T readImmediately() throws Exception{
		T item = doReadNormally();
		if(isAnyNull(item)){
			log.debug("found one result is null in the item[{}],shutdown the reader",item);
			item = null;
		}
		return item;
	}

	/*
	 * 支持策略 POLICY_GREEDY_RESEAT 的读取方式
	 * @author Pin Liu
	 */
	protected T readResetly() throws Exception{
		T item = doReadNormally();
		if(isAllNull(item)){
			log.debug("found all reault is null in the item[{}],shutdown the reader",item.getClass());
			item = null;
		}
		if(item != null &&  !isAnyNormal(item)){
			log.debug("found no normal read resutl exist, shutdown the reader");
			item = null;
		}
		if(item != null){
			item = resolveReset(item);
		}
		return item;
	}
	
	/*
	 * 以reset的方式解决空值
	 * @author Pin Liu
	 */
	protected abstract T resolveReset(T item) throws Exception;
	
	protected <I> I resolveReset(ResettableItemReader<I> reader) throws UnexpectedInputException, ParseException, NonTransientResourceException, Exception{
		I item = null;
		reader.reset();
		item = reader.read();
		log.debug("use POLICY_GREEDY_RESET return reset value for the reader[{}]",reader);
		return item;
	}

	/*
	 * 支持策略 POLICY_GREEDY_REPEAT 的读取方式
	 * @author Pin Liu
	 */
	protected T readRepeatly() throws Exception{
		T item = doReadNormally();
		if(isAllNull(item)){
			log.debug("found all reault is null in the item[{}], shutdown the reader",item.getClass());
			item = null;
		}
		if(item != null && !isAnyNormal(item)){
			log.debug("found all result is the last readed item, shutdown the reader");
			item = null;
		}
		if(item != null){
			item = resolveLast(item);
		}
		return item;
	}
	
	/*
	 * 以repeat的方式解决空值
	 * @author Pin Liu
	 */
	protected abstract T resolveLast(T item) throws Exception;
	
	/*
	 * 以repeat的方式为子读取器reader解决空值
	 * @author Pin Liu
	 */
	protected <I> I resolveLast(RecordLastReader<I> reader){
		I item = null;
		item = reader.last();
		log.debug("use POLICY_GREEDY_REPEAT return last value for the reader[{}]",reader);
		return item;
	}

	/*
	 * 是否item中的值都为null
	 * @author Pin Liu
	 */
	protected abstract boolean isAllNull(T item);
	
	/*
	 * 是否item中有值为null
	 * @author Pin Liu
	 */	
	protected abstract boolean isAnyNull(T item);
	
	/*
	 * item中是否有正常读取到的值存在，非重复、非重置且非空
	 * @author Pin Liu
	 */
	protected abstract boolean isAnyNormal(T item);

}