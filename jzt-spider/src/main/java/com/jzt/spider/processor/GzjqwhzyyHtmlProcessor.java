package com.jzt.spider.processor;

import java.util.List;

import org.springframework.util.CollectionUtils;

import com.jzt.spider.common.UrlConfig;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

public class GzjqwhzyyHtmlProcessor implements PageProcessor{
	
	private Site site = Site.me();
	//@Override
	public void process(Page page) {
		System.out.println(page.getHtml());
		String url=page.getUrl().toString();
		if(UrlConfig.Gzjqwhzyy_START_URL.equals(url)){
			List<String> contextList = page.getHtml().xpath("ul[@id='ulDiseaseList']/li/text()").all();
			if(!CollectionUtils.isEmpty(contextList)){
				for(String s:contextList){
					System.out.println(s);
				}
			}
		}else{
			
		}
		
	}

	//@Override
	public Site getSite() {
		return site;
	}
	
	public static void main(String[] args) {
		Spider.create(new GzjqwhzyyHtmlProcessor()).addUrl(UrlConfig.Gzjqwhzyy_START_URL).thread(10).run();
	}

}
