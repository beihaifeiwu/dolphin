package com.freetmp.springbatch.support.composite;

/*
 * @author Pin Liu
 * @编写日期 2014年11月13日下午1:22:34
 */
public enum MultipleAccessPolicy {
	IMMEDIATELY, //立即返回
	GREEDY_RESET, //贪婪重置
	GREEDY_REPEAT //贪婪重复
}
