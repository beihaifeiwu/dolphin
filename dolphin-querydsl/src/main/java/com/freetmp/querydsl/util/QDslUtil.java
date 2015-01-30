package com.freetmp.querydsl.util;

import com.mysema.query.types.Path;

import java.util.ArrayList;
import java.util.List;

/**
 * querydsl相关的工具类
 * @author Pin Liu
 * @编写日期 2014年11月17日下午12:03:15
 */
public class QDslUtil {

	/**
	 * 根据dsl提供的路径取出属性名
	 * @author Pin Liu
	 * @编写日期: 2014年11月17日下午12:46:05
	 * @param paths
	 * @return
	 */
	public static String[] toPropertyNames(Path<?> ...paths){
		List<String> properties = new ArrayList<String>();
		if(paths != null){
			for(Path<?> path : paths){
				String str = path.toString();
				int last = str.lastIndexOf('.');
				if(last != -1){
					properties.add(str.substring(last+1,str.length()));
				}else{
					properties.add(str);
				}
			}
			return properties.toArray(new String[]{});
		}
		return null;
	}
}
