package com.jzt.spider.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.util.MultiMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jzt.spider.common.UrlConfig;
import com.pz998.rpc.model.entity.BdDepartmentRpc;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.JsonPathSelector;
import us.codecraft.xsoup.Xsoup;

public class YiBaiduDepartmentRelaProcessor implements PageProcessor{
	private Site site = Site.me();
	
	//@Override
	public void process(Page page) {
		// TODO Auto-generated method stub
		String url=page.getUrl().toString();
		if(page.getUrl().regex(UrlConfig.START_URL).match()){
			MultiMap<String> resultMap = com.jzt.spider.util.StringUtils.getUrlParamer(url);
            String cityId = resultMap.getString("cityId");
			
			List<BdDepartmentRpc>  platformDeptList = new ArrayList<BdDepartmentRpc>();
			
			//一级科室
			Map<String,BdDepartmentRpc> parentDeptMap = new HashMap<String,BdDepartmentRpc>();
			List<String> deptList = page.getHtml().xpath("ul[@id='depart-level1-ul']/li").all();
			if(CollectionUtils.isNotEmpty(deptList)){
				for(String dept:deptList){
					 Document deptDocument = Jsoup.parse(dept);
					 String deptName = Xsoup.select(deptDocument, "li/text()").get();
					 String deptId = Xsoup.select(deptDocument, "li/@data-value").get();
					 
					 BdDepartmentRpc platformDept = new BdDepartmentRpc();
					 platformDept.setName(deptName);
					 platformDept.setSourceId(deptId);
					 platformDept.setLevel(BdDepartmentRpc.LEVEL_FIRST);
					 platformDept.setType(BdDepartmentRpc.TYPE_PLATFORM);
					 platformDeptList.add(platformDept);
					 parentDeptMap.put(deptId,platformDept);
					//System.out.println("deptName:"+deptName+" deptId:"+deptId);
				}
			}
			
			//二级科室
			List<String> secondDepartList = page.getHtml().xpath("div[@id='second-depart']/ul").all();
			if(CollectionUtils.isNotEmpty(secondDepartList)){
				for(String secDept:secondDepartList){
					Document secDeptDocument = Jsoup.parse(secDept);
					String  parentDeptId = Xsoup.select(secDeptDocument, "ul/@data-value").get();
					List<String> deptLiList = Xsoup.select(secDeptDocument, "ul/li").list();
					if(UrlConfig.ALL_DEPT.equals(parentDeptId)){
						continue;
					}
					
					if(CollectionUtils.isNotEmpty(deptLiList)){
						parentDeptMap.remove(parentDeptId);
						
						for(String li:deptLiList){
							Document liDocument = Jsoup.parse(li);
							String secDeptName = Xsoup.select(liDocument, "li/text()").get();
							String secDeptId = Xsoup.select(liDocument, "li/@data-value").get();
							if(UrlConfig.ALL_DEPT.equals(secDeptId)){
								continue;
							}
							BdDepartmentRpc bdDepartmentRpc = new BdDepartmentRpc();
							bdDepartmentRpc.setName(secDeptName);
							bdDepartmentRpc.setSourceId(secDeptId);
							bdDepartmentRpc.setParentSource(parentDeptId);
							bdDepartmentRpc.setLevel(BdDepartmentRpc.LEVEL_SECOND);
							bdDepartmentRpc.setType(BdDepartmentRpc.TYPE_PLATFORM);
							
							platformDeptList.add(bdDepartmentRpc);
							// https://yi.baidu.com/pc/doctor/listdata?key=&date=&cityId=371&provId=16&regionId=0&departLevel1Id=1&departLevel2Id=2&medTitle=0&pageSize=1500&serviceType=0&page=1&zt=pcpinzhuan&zt_ext=&pvid=1470879591059493&seed=seed_1470967184529
							for(int i=1;i<26;i++){
								String docListUrl = "https://yi.baidu.com/pc/doctor/listdata?key=&date=&cityId="+cityId+"&regionId=0&departLevel1Id="+parentDeptId+"&departLevel2Id="+secDeptId+"&medTitle=0&pageSize=100&serviceType=0&page="+i+"&zt=pcpinzhuan&zt_ext=&pvid=1470879591059493&seed=seed_1470967184529";
								page.addTargetRequest(docListUrl);
							}

							System.out.println("secDeptName:"+secDeptName+ " secDeptId:"+secDeptId+" parentDeptId:"+parentDeptId);
						}
					}
				}
			}
			//没有二级科室的一级科室特殊处理
			if(parentDeptMap!=null&&!parentDeptMap.isEmpty()){
				Set<String> parentDeptIds =  parentDeptMap.keySet();
				for(String parentDeptId:parentDeptIds){
					System.out.println("没有二级科室的科室id:"+parentDeptId);
					for(int i=1;i<26;i++){
						String docListUrl = "https://yi.baidu.com/pc/doctor/listdata?key=&date=&cityId="+cityId+"&regionId=0&departLevel1Id="+parentDeptId+"&departLevel2Id=&medTitle=0&pageSize=100&serviceType=0&page="+i+"&zt=pcpinzhuan&zt_ext=&pvid=1470879591059493&seed=seed_1470967184529";
						page.addTargetRequest(docListUrl);
					}
				}
			}
			
			//page.putField("platformDeptList", platformDeptList);
		}else if(page.getUrl().regex(UrlConfig.DEPT_DOCTOR_LIST).match()){
			List<String> doctorList = new JsonPathSelector("$.data.doctorList[*]").selectList(page.getRawText());
			if(CollectionUtils.isNotEmpty(doctorList)){
				List<Map<String,String>> platdeptRelaMapList = new ArrayList<Map<String,String>>();
				for(String obj:doctorList){
					
					JSONObject docJo = JSON.parseObject(obj);
					String hospitalName = (String)docJo.get("hospitalName");
					String departName = (String)docJo.get("departName");
					
					MultiMap<String> resultMap = com.jzt.spider.util.StringUtils.getUrlParamer(url);
	                String departLevel2Id = resultMap.getString("departLevel2Id");
	                String departLevel1Id = resultMap.getString("departLevel1Id");
	                String level = "2";
	                if(StringUtils.isEmpty(departLevel2Id)){
	                	departLevel2Id = departLevel1Id;
	                	level = "1";
	                }
	                System.out.println("level:"+level);
	                Map<String,String> platdeptRelaMap = new HashMap<String,String>();
	                platdeptRelaMap.put("hospitalName", hospitalName);
	                platdeptRelaMap.put("departName", departName);
	                platdeptRelaMap.put("departLevel2Id", departLevel2Id);
	                platdeptRelaMap.put("level", level);
	                
	                System.out.println("hospitalName:"+hospitalName+" departName:"+departName+" departLevel2Id:"+departLevel2Id);
	                platdeptRelaMapList.add(platdeptRelaMap);
				}
				page.putField("platdeptRelaMapList", platdeptRelaMapList);
			}
		}
	}

	//@Override
	public Site getSite() {
		return site;
	}
	public static void main(String[] args) {
		
		String url = "https://yi.baidu.com/pc/doctor/listFanzhi?zt=pcpinzhuan&zt_ext=&pvid=1470879584839362&provId=0&cityId=84&regionId=0#fastTop";
		Spider.create(new YiBaiduDepartmentRelaProcessor()).addUrl(url).run();
	}
}
