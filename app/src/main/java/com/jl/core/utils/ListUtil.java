package com.jl.core.utils;

import java.util.List;

/**
 * Created by MingXuan on 2016/3/30.
 */
public class ListUtil {

	/**
	 * 返回集合size，如果为null或者size==0，则返回0
	 */
	public static <V> int getSize(List<V> sourceList) {
		return sourceList == null ? 0 : sourceList.size();
	}

	/**
	 * 如果集合为null或者size==0则返回true，反之返回false
	 */
	public static <V> boolean isEmpty(List<V> sourceList) {
		return (sourceList == null || sourceList.size() == 0);
	}

}
