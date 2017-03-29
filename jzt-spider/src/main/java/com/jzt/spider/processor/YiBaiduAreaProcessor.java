package com.jzt.spider.processor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.util.MultiMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jzt.spider.common.UrlConfig;
import com.pz998.rpc.model.entity.BdRegionRpc;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.xsoup.Xsoup;

public class YiBaiduAreaProcessor implements PageProcessor{
	
	private Site site = Site.me();
	
	Spider ocSpider;
	
	//@Override
	public Site getSite() {
		return site;
	}

	//@Override
	public void process(Page page) {
		String url=page.getUrl().toString();
		if(page.getUrl().regex(UrlConfig.AREA_LIST_URL).match()){
			MultiMap<String> urlMap =  com.jzt.spider.util.StringUtils.getUrlParamer(url);
			String cityId = urlMap.getString("cityId");
			String provId = urlMap.getString("provId");
			
			List<String> ulList = page.getHtml().xpath("div[@id='region']/ul/li").all();
			if(!CollectionUtils.isEmpty(ulList)){
				List<BdRegionRpc> regionList = new ArrayList<BdRegionRpc>();
				for(String ul:ulList){
					BdRegionRpc regionRpc = new BdRegionRpc();
					
					Document document = Jsoup.parse(ul);
					String areaName = Xsoup.select(document, "li/text()").get();
					String areaId = Xsoup.select(document, "li/@data-value").get();
					regionRpc.setName(areaName);
					regionRpc.setSourceId(areaId);
					regionRpc.setCitySourceId(cityId);
					
					
					System.out.println(areaName);
					System.out.println(areaId);
					regionList.add(regionRpc);
				}
				
				page.putField("regionList", regionList);
			}	
		}
		
	}
	
	public void init(){
		ocSpider =Spider.create(new YiBaiduAreaProcessor());
		
		String area = YiBaiduAreaProcessor.class.getResource("/").toString().replace("file:", "") + "area.json"; 
	    try{
	    	area = java.net.URLDecoder.decode(area,"utf-8");  
	    }catch (Exception e){
	        e.printStackTrace(); 
	    }
		
		try {
			String json = FileUtils.readFileToString(new File(area));
			JSONObject jsonJo =  JSON.parseObject(json);
			JSONArray areaArray = jsonJo.getJSONArray("allRegion");
			if(org.apache.commons.collections.CollectionUtils.isNotEmpty(areaArray)){
				for(Object obj:areaArray){
					JSONObject jo = (JSONObject)obj;
					String proviceId = jo.getString("value");
					String name = jo.getString("name");
					JSONArray childrenArray = jo.getJSONArray("children");
					if(org.apache.commons.collections.CollectionUtils.isNotEmpty(childrenArray)){
						for(Object subObj:childrenArray){
							JSONObject subJo = (JSONObject)subObj;
							String cityName = subJo.getString("name");
							String cityId = subJo.getString("value");
							String url = "https://yi.baidu.com/pc/hospital/listpage?zt=pcpinzhuan&zt_ext=&pvid=1474872183055663&provId="+proviceId+"&cityId="+cityId;
							ocSpider.addUrl(url);
						}
					}
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ocSpider.thread(10);
		ocSpider.start();
	}
	
	public static void main(String[] args) {
//		ocSpider =Spider.create(new YiBaiduAreaProcessor()).addUrl("https://yi.baidu.com/pc/hospital/listpage?zt=pcpinzhuan&zt_ext=&pvid=1474872183055663&provId=5&cityId=81").thread(10);
//		
//		ocSpider.start();
		YiBaiduAreaProcessor p = new YiBaiduAreaProcessor();
		p.init();
	}
	
}
