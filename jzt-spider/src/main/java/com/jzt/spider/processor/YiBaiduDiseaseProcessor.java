package com.jzt.spider.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.eclipse.jetty.util.MultiMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.jzt.spider.common.UrlConfig;
import com.pz998.rpc.model.entity.BdDiseaseDoctorRelaRpc;
import com.pz998.rpc.model.entity.BdDiseaseHospitalRecommendRpc;
import com.pz998.rpc.model.entity.BdDiseaseRpc;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.JsonPathSelector;

public class YiBaiduDiseaseProcessor implements PageProcessor{
	
	private Site site = Site.me();
                                               		
    
	public static final Map<String,String> CITY_MAP = new HashMap<String,String>();
	
	static{
		CITY_MAP.put("371", "武汉");
		CITY_MAP.put("1", "北京");
		CITY_MAP.put("2", "上海");
		CITY_MAP.put("196","济南");
		CITY_MAP.put("322","郑州");
	}
    
    //@Override
	public void process(Page page) {
    	String url = page.getUrl().toString();
		if(page.getUrl().regex(UrlConfig.DISEASE_URL).match()){
			MultiMap<String> resultMap = com.jzt.spider.util.StringUtils.getUrlParamer(url);
            String diseaseName = resultMap.getString("diseaseName");
			
			List<String> tabList = new JsonPathSelector("$.data.tabList[*]").selectList(page.getRawText());
			BdDiseaseRpc bdDiseaseRpc = new BdDiseaseRpc();
			bdDiseaseRpc.setName(diseaseName);
			if(CollectionUtils.isNotEmpty(tabList)){
				
				for(String obj:tabList){
					JSONObject tabJo = JSON.parseObject(obj);
					String tabName = (String)tabJo.get("tabName");
					String text = (String)tabJo.get("text");
	
					System.out.println("tabName:"+tabName);
					if(UrlConfig.TAB_NAME_DIAGNOSIS.equals(tabName)){
						bdDiseaseRpc.setDiagnosisDescr(text);
						System.out.println("诊断："+text);
					}
					if(UrlConfig.TAB_NAME_REASON.equals(tabName)){
						bdDiseaseRpc.setPathogenyDescr(text);
						System.out.println("病因："+text);
					}
					if(UrlConfig.TAB_NAME_TREATMENT.equals(tabName)){
						bdDiseaseRpc.setTreatmentDescr(text);
						System.out.println("治疗："+text);
					}
					if(UrlConfig.TAB_NAME_PHENOMENON.equals(tabName)){
						bdDiseaseRpc.setPhenomenonDescr(text);
						System.out.println("临床表现："+text);
					}
				}
				page.putField("bdDiseaseRpc", bdDiseaseRpc);
			}
		}else if(page.getUrl().regex(UrlConfig.DISEASE_INDEX_URL).match()){
			String name = page.getHtml().xpath("div[@class='disease-name']/h1[@class='ys-util-text-bigger ys-util-font-strong']/text()").toString();
			String context = page.getHtml().xpath("div[@id='colligation']/div[@class='container-disease-info ys-util-margin-b20']/p[@class='ys-util-text-smaller ys-util-margin-t10']/text()").toString();
			BdDiseaseRpc diseaseContextRpc = new BdDiseaseRpc();
			diseaseContextRpc.setName(name);
			diseaseContextRpc.setIntro(context);
			
			page.putField("diseaseContextRpc", diseaseContextRpc);
			System.out.println("疾病简介:"+context);
		}else if(page.getUrl().regex(UrlConfig.DISEASE_TO_HOSPITAL_LIST_URL).match()){
			MultiMap<String> resultMap = com.jzt.spider.util.StringUtils.getUrlParamer(url);
            String cityId = resultMap.getString("cityId");
            String cityName = CITY_MAP.get(cityId);
            
			List<String> hospitalList = new JsonPathSelector("$.data.hospitalList[*]").selectList(page.getRawText());
			List<BdDiseaseHospitalRecommendRpc> bdDiseaseHospitalRecommendRpcList = new ArrayList<BdDiseaseHospitalRecommendRpc>();
			
			if(CollectionUtils.isNotEmpty(hospitalList)){
				for(String o:hospitalList){
					JSONObject obj = JSON.parseObject(o);
					String hospitalName = (String)obj.get("hospitalName");
					String diseaseName = (String)obj.get("diseaseName");
					String treatScore = (String)obj.get("treatScore");
					System.out.println("水平："+treatScore);
					BdDiseaseHospitalRecommendRpc bdDiseaseHospitalRecommendRpc = new BdDiseaseHospitalRecommendRpc();
					bdDiseaseHospitalRecommendRpc.setHospitalSourceId(hospitalName);
					bdDiseaseHospitalRecommendRpc.setDiseaseSourceId(diseaseName);
					bdDiseaseHospitalRecommendRpc.setTreatScore(treatScore);
					bdDiseaseHospitalRecommendRpc.setCity(cityName);
					bdDiseaseHospitalRecommendRpcList.add(bdDiseaseHospitalRecommendRpc);
				}
				page.putField("bdDiseaseHospitalRecommendRpcList", bdDiseaseHospitalRecommendRpcList);
			}
		}else if(page.getUrl().regex(UrlConfig.DISEASE_TO_DOCTOR_LIST_URL).match()){
			MultiMap<String> resultMap = com.jzt.spider.util.StringUtils.getUrlParamer(url);
            String diseaseName = resultMap.getString("diseaseName");
			
			List<String> doctorList = new JsonPathSelector("$.data.doctorList[*]").selectList(page.getRawText());
			List<BdDiseaseDoctorRelaRpc> bdDiseaseDoctorRelaRpcList = new ArrayList<BdDiseaseDoctorRelaRpc>();
			
			if(CollectionUtils.isNotEmpty(doctorList)){
				for(String o:doctorList){
					JSONObject obj = JSON.parseObject(o);
					String doctorName = (String)obj.get("doctorName");
					String allTimeHref = (String)obj.get("allTimeHref");
					System.out.println("医生："+doctorName+" 疾病:"+diseaseName);
					
					MultiMap<String> map = com.jzt.spider.util.StringUtils.getUrlParamer(allTimeHref);
					
					BdDiseaseDoctorRelaRpc bdDiseaseDoctorRelaRpc = new BdDiseaseDoctorRelaRpc();
					bdDiseaseDoctorRelaRpc.setDiseaseSourceId(diseaseName);
					String doctorSourceId = map.getString("doctorId");
					System.out.println("医生id："+doctorSourceId);
					bdDiseaseDoctorRelaRpc.setDoctorSourceId(doctorSourceId);
					bdDiseaseDoctorRelaRpcList.add(bdDiseaseDoctorRelaRpc);
				}
				page.putField("bdDiseaseDoctorRelaRpcList", bdDiseaseDoctorRelaRpcList);
			}
		}
	}

	//@Override
	public Site getSite() {
		return site;
	}
	public static void main(String[] args) {
		Spider spider = Spider.create(new YiBaiduDiseaseProcessor());
//		spider.addUrl("https://yi.baidu.com/pc/disease/category?diseaseName=湿疹&provId=16&cityId=371&regionId=0&diseaseId=0");
//		spider.addUrl("https://yi.baidu.com/disease/关节扭伤.html?zt=&zt_ext=&pvid=1471049532280499&provId=16&cityId=371&regionId=0");
//		String url = "https://yi.baidu.com/pc/disease/doctorlist?regionId=0&doctorTitle=0&hospitalLevel=0&serviceType=0&sortType=0&isInsurance=&date=all&page=1&pageSize=50&cityId=371&diseaseId=0&diseaseName=2%E5%9E%8B%E7%B3%96%E5%B0%BF%E7%97%85";
//		spider.addUrl(url);
		String url = "https://yi.baidu.com/pc/disease/hospitallist?regionId=0&hospitalType=0&hospitalLevel=0&serviceType=0&sortType=1&isInsurance=0&page=1&pageSize=50&cityId=367&diseaseId=0&diseaseName=糖尿病";
		spider.addUrl(url);
		spider.run();
		
	}
}
