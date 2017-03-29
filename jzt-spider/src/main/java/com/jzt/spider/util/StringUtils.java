package com.jzt.spider.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.UrlEncoded;
import org.springframework.util.CollectionUtils;

import com.pz998.rpc.model.vo.CustomerOrderListVo;


public class StringUtils {

	/**
	 * 按ASCII码从小到大排序(字典序)
	 * 
	 * @param list
	 */
	public static List<String> getSort(List<String> list) {
		Collections.sort(list);
		return list;
	}

	/**
	 * 生成随机数
	 * 
	 * @return String
	 */
	public static String genNonceStr() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000))
				.getBytes());
	}
	
	public static MultiMap<String> getUrlParamer(String url){
		MultiMap<String> res = new MultiMap<String>();
	    int questionMarkIndex = url.indexOf('?');
	    if (questionMarkIndex == -1)
	    {
	        return res;
	    }
	    int poundIndex = url.indexOf('#');
	    if (poundIndex == -1)
	    {
	        poundIndex = url.length();
	    }
	    UrlEncoded.decodeUtf8To(url, questionMarkIndex+1,
	                poundIndex - questionMarkIndex - 1, res);
	    return res;
	}

	public static String listToString(List<String> depts) {

		if(CollectionUtils.isEmpty(depts)){
			return "";
		}
		
	
		StringBuffer stringBuf = new StringBuffer();
		for(int i=0;i<depts.size();i++){
			String s = depts.get(i);
			stringBuf.append(s);
			if(i<depts.size()-1){
				stringBuf.append(",");
			}
		}
		
		return stringBuf.toString();
	
	}
}
