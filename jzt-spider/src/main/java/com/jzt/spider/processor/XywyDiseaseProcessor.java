package com.jzt.spider.processor;

import org.eclipse.jetty.util.MultiMap;

import com.jzt.spider.common.UrlConfig;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

public class XywyDiseaseProcessor implements PageProcessor{
	
	private Site site = Site.me();
	
	//@Override
	public void process(Page page) {
		String url=page.getUrl().toString();
		if(page.getUrl().regex(UrlConfig.SYMPTOM_INFO_URL).match()){
			MultiMap<String> resultMap = com.jzt.spider.util.StringUtils.getUrlParamer(url);
            String hosName = resultMap.getString("key");
            
            String text = page.getHtml().xpath("div[@class='jib-articl fr f14 jib-lh-articl']/strong[@class='db fb pb10 mb5 tc f20 fYaHei lh240']/text()").toString();
            System.out.println(text);
		}
	}

	//@Override
	public Site getSite() {
		return site;
	}
	public static void main(String[] args) {
		String url = "http://jib.xywy.com/il_sii/symptom/9911.htm";
		Spider ocSpider =Spider.create(new XywyDiseaseProcessor()).addUrl(url).thread(10);
		ocSpider.start();
	}
}
