package com.jzt.spider.processor;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.eclipse.jetty.util.MultiMap;

import com.jzt.spider.common.UrlConfig;
import com.jzt.spider.util.StringUtils;


import com.pz998.rpc.model.entity.BdBodySymptomRpc;
import com.pz998.rpc.model.entity.BdBodyAreaRpc;

import net.minidev.json.JSONObject;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.JsonPathSelector;

public class GzjqwhzyyProcessor implements PageProcessor{
    private Site site = Site.me();
    
        
    public void process(Page page) {
    	//起始页
    	String pageUrl = page.getUrl().toString();
    	if(UrlConfig.START_URL.equals(pageUrl)){
    		try{
    			String code = new JsonPathSelector("$.Code").select(page.getRawText());
        		int codeVal = Integer.parseInt(code);
        		if(UrlConfig.REQUEST_CODE_SUCESS==codeVal){
    	            List bodyList = new JsonPathSelector("$.results[*]").selectList(page.getRawText());
    	            if(CollectionUtils.isNotEmpty(bodyList)){
    	            	List<BdBodyAreaRpc> bdBodyAreaList = new ArrayList<BdBodyAreaRpc>();
    	            	for(Object body:bodyList){
    	            		JSONObject objJo = (JSONObject)body;
    	            		Integer bodyId = (Integer)objJo.get("id");
    	            		String name = (String)objJo.get("name");
    	            		
    	            		BdBodyAreaRpc bdBodyAreaRpc = new BdBodyAreaRpc();
    	            		String bodyIdStr = bodyId==null?"":bodyId.toString();
    	            		bdBodyAreaRpc.setSourceId(bodyIdStr);
    	            		bdBodyAreaRpc.setName(name);
    	            		bdBodyAreaRpc.setLevel(BdBodyAreaRpc.TOP_LEVEL);
    	            		bdBodyAreaList.add(bdBodyAreaRpc);
    	            		System.out.println("身体部位id："+bodyId+"  身体部位名称："+name);
    	            		//http://gzjqwhzyy.yihugz.com/Action/IntelligentGuide/IllnessChooseHandler.ashx?platformType=1&sourceType=1&sourceId=1001&t=1470123962761&action=QueryDiseaseList&sysAttrIds=36,455&bodyId=36
    	            		//儿童
    	            		String childdiseaseUrl = "http://gzjqwhzyy.yihugz.com/Action/IntelligentGuide/IllnessChooseHandler.ashx?platformType=1&sourceType=1&sourceId=1001&t=1470123962761&action=QueryDiseaseList&sysAttrIds="+bodyId+",454&bodyId="+bodyId;

    	            		//男性
    	            		//String mandiseaseUrl = "http://gzjqwhzyy.yihugz.com/Action/IntelligentGuide/IllnessChooseHandler.ashx?platformType=1&sourceType=1&sourceId=1001&t=1470123962761&action=QueryDiseaseList&sysAttrIds="+bodyId+",455&bodyId="+bodyId;
    	            		//女性
    	            		String womandiseaseUrl = "http://gzjqwhzyy.yihugz.com/Action/IntelligentGuide/IllnessChooseHandler.ashx?platformType=1&sourceType=1&sourceId=1001&t=1470123962761&action=QueryDiseaseList&sysAttrIds="+bodyId+",456&bodyId="+bodyId;

    	            		String subBodyUrl = "http://gzjqwhzyy.yihugz.com/Action/IntelligentGuide/IllnessChooseHandler.ashx?platformType=1&sourceType=1&sourceId=1001&t=1470123603496&action=QueryBodyPartList&parentId="+bodyId;	
    	            		if(bodyId!=null&&!bodyId.equals(24)&&!bodyId.equals(31)&&!bodyId.equals(40)&&!bodyId.equals(45)){
    	            			page.addTargetRequest(childdiseaseUrl);
    	            			page.addTargetRequest(womandiseaseUrl);
    	            		}

    	            		page.addTargetRequest(subBodyUrl);
    	            	}
    	            	//page.putField("bdBodyAreaList", bdBodyAreaList);
    	            }
        		}
        	}catch(Exception e){
        		e.printStackTrace();
        	}	
            
         //身体部位二级列表    
    	}else if(page.getUrl().regex(UrlConfig.SUB_BODY_PART_LIST_URL).match()){
    		try{
    			String code = new JsonPathSelector("$.Code").select(page.getRawText());
        		int codeVal = Integer.parseInt(code);
        		if(UrlConfig.REQUEST_CODE_SUCESS==codeVal){
        			List subbodys = new JsonPathSelector("$.results[*]").selectList(page.getRawText());
        			String url = page.getUrl().toString();
        			MultiMap<String> resultMap = StringUtils.getUrlParamer(url);
        	        String parentId = resultMap.getString("parentId");
        			if(CollectionUtils.isNotEmpty(subbodys)){
        				List<BdBodyAreaRpc> subBdBodyAreaList = new ArrayList<BdBodyAreaRpc>();
        				for(Object subBody:subbodys){
        					JSONObject objJo = (JSONObject)subBody;
        					Integer subBodyId = (Integer)objJo.get("id");
        					String name = (String)objJo.get("name");
        					BdBodyAreaRpc subBodyArea = new BdBodyAreaRpc();
        					String subBodyIdStr = subBodyId==null?"":subBodyId.toString();
        					subBodyArea.setSourceId(subBodyIdStr);
        					subBodyArea.setName(name);
        					subBodyArea.setParentSourceId(parentId);
        					subBodyArea.setLevel(BdBodyAreaRpc.SUB_LEVEL);
        					
        					subBdBodyAreaList.add(subBodyArea);
    	            		//String diseaseUrl = "http://gzjqwhzyy.yihugz.com/Action/IntelligentGuide/IllnessChooseHandler.ashx?platformType=1&sourceType=1&sourceId=1001&t=1470123962761&action=QueryDiseaseList&sysAttrIds="+subBodyId+",455&bodyId="+subBodyId;
    	            		String childdiseaseUrl = "http://gzjqwhzyy.yihugz.com/Action/IntelligentGuide/IllnessChooseHandler.ashx?platformType=1&sourceType=1&sourceId=1001&t=1470123962761&action=QueryDiseaseList&sysAttrIds="+subBodyId+",454&bodyId="+subBodyId;
    	            		String womandiseaseUrl = "http://gzjqwhzyy.yihugz.com/Action/IntelligentGuide/IllnessChooseHandler.ashx?platformType=1&sourceType=1&sourceId=1001&t=1470123962761&action=QueryDiseaseList&sysAttrIds="+subBodyId+",456&bodyId="+subBodyId;

        					page.addTargetRequest(childdiseaseUrl);
        					page.addTargetRequest(womandiseaseUrl);
        				}
        				//page.putField("subBdBodyAreaList", subBdBodyAreaList);
        			}
        		}
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    		
    	//症状列表	
    	}else if (page.getUrl().regex(UrlConfig.CHILDE_DISEASE_LIST_URL).match()||page.getUrl().regex(UrlConfig.WOMAN_DISEASE_LIST_URL).match()) {
    		try{
    			  Integer isChild =0;
    			  Integer isWoman = 0;
    			  if(page.getUrl().regex(UrlConfig.CHILDE_DISEASE_LIST_URL).match()){
    				  isChild =1;
    			  }
    			  if(page.getUrl().regex(UrlConfig.WOMAN_DISEASE_LIST_URL).match()){
    				  isWoman =1;
    			  }
    			  List<String> ids = new JsonPathSelector("$.Result[*].diseaseId").selectList(page.getRawText());
    	            String url = page.getUrl().toString();
    	            MultiMap<String> resultMap = StringUtils.getUrlParamer(url);
    	            String bodyId = resultMap.getString("bodyId");
    	    		System.out.println("身体部位id:"+bodyId);
    	            if (CollectionUtils.isNotEmpty(ids)) {
    	            	List<BdBodySymptomRpc> diseaseList = new ArrayList<BdBodySymptomRpc>();
    	                for (Object id : ids) {
    	                    page.addTargetRequest("http://gzjqwhzyy.yihugz.com/Action/IntelligentGuide/IntelligentGuideHandler.ashx?t=1470124230736&platformType=1&sourceType=1&sourceId=1001&action=GetDiseaseDetail&diseaseId=" + id);
    	                    BdBodySymptomRpc BdBodySymptomRpc = new BdBodySymptomRpc();
    	                    BdBodySymptomRpc.setBodyAreaSourceId(bodyId);
    	                    BdBodySymptomRpc.setSourceId(id.toString());
    	                    BdBodySymptomRpc.setIsChild(isChild);
    	                    BdBodySymptomRpc.setIsWoman(isWoman);
    	                    diseaseList.add(BdBodySymptomRpc);
//    	                    page.putField("bodyId", bodyId);
//    	                    page.putField("diseaseId",id);
    	                }
    	                page.putField("diseaseList", diseaseList);
    	            }
    		}catch(Exception e){
    			e.printStackTrace();
    		}

        //症状详情页    
        }else if(page.getUrl().regex(UrlConfig.DISEASE_DETAIL_URL).match()){
        	try{
        		String code = new JsonPathSelector("$.Code").select(page.getRawText());
            	String url = page.getUrl().toString();
            	MultiMap<String> resultMap = StringUtils.getUrlParamer(url);
                String diseaseId = resultMap.getString("diseaseId");
            	System.out.println("返回code:"+code);
        		int codeVal = Integer.parseInt(code);
        		if(UrlConfig.REQUEST_CODE_SUCESS==codeVal){
        			String dName = new JsonPathSelector("$.dName").select(page.getRawText());
        			List<String> depts = new JsonPathSelector("$.depts[*].name").selectList(page.getRawText());
        			String deptStr = StringUtils.listToString(depts);
        			
        			BdBodySymptomRpc bdBodySymptomRpc = new BdBodySymptomRpc();
        			bdBodySymptomRpc.setSourceId(diseaseId);
        			bdBodySymptomRpc.setName(dName);
        			bdBodySymptomRpc.setRecommendDepartmentName(deptStr);
        			
        			List resourList = new JsonPathSelector("$.attrs[*]").selectList(page.getRawText());
        			if(CollectionUtils.isNotEmpty(resourList)){
        				for(Object s:resourList){
        	            	JSONObject objJo = (JSONObject)s;
        	            	Integer attrId = (Integer)objJo.get("attrID");
        	            	String des = (String)objJo.get("description");
        	            	if(UrlConfig.ATTR_INTRO_ID.equals(attrId)){
        	            		bdBodySymptomRpc.setIntro(des);
        	            	}
        	            	if(UrlConfig.ATTR_TREATMENT_DESC_ID.equals(attrId)){
        	            		bdBodySymptomRpc.setTreatmentDescr(des);
        	            	}
        	            	if(UrlConfig.ATTR_DIAGNOSIS_DESCR_ID.equals(attrId)){
        	            		bdBodySymptomRpc.setDiagnosisDescr(des);
        	            	}
        	            	if(UrlConfig.ATTR_PATHOGENY_DESCR_ID.equals(attrId)){
        	            		bdBodySymptomRpc.setPathogenyDescr(des);
        	            	}
        	            }
        			}
    			
                    page.putField("disease", bdBodySymptomRpc);
     
        		}
        	}catch(Exception e){
        		e.printStackTrace();
        	}
        	
        	
        }else{
        	System.out.println("无匹配");
        }

    }

   
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
    	
//			URL url = new URL("http://gzjqwhzyy.yihugz.com/Action/IntelligentGuide/IntelligentGuideHandler.ashx?t=1470124230736&platformType=1&sourceType=1&sourceId=1001&action=GetDiseaseDetail&diseaseId=\\d+");
			
			Pattern pattern = Pattern.compile("https://yi\\.baidu\\.com/pc/doctor/detailpage\\?zt=\\w+&zt_ext=&pvid=0&doctorId=\\d+");
			Matcher matcher = pattern.matcher("https://yi.baidu.com/pc/doctor/detailpage?zt=self&zt_ext=&pvid=0&doctorId=202523");
			boolean b= matcher.matches();
			System.out.println(b);
    	
    	
    	
//        Spider.create(new GzjqwhzyyProcessor()).addUrl(START_URL).addPipeline(new StoragePipeline()).run();

    	
    	
//    	List<String> urls = new ArrayList<String>();
//    	urls.add("http://gzjqwhzyy.yihugz.com/Action/IntelligentGuide/IllnessChooseHandler.ashx?platformType=1&sourceType=1&sourceId=1001&t=1470123962761&action=QueryDiseaseList&sysAttrIds=28,455&bodyId=28");
//    	urls.add("http://gzjqwhzyy.yihugz.com/Action/IntelligentGuide/IllnessChooseHandler.ashx?platformType=1&sourceType=1&sourceId=1001&t=1470123962761&action=QueryDiseaseList&sysAttrIds=36,455&bodyId=36");
//    	urls.add("http://gzjqwhzyy.yihugz.com/Action/IntelligentGuide/IllnessChooseHandler.ashx?platformType=1&sourceType=1&sourceId=1001&t=1470123962761&action=QueryDiseaseList&sysAttrIds=38,455&bodyId=38");
//    	urls.add("http://gzjqwhzyy.yihugz.com/Action/IntelligentGuide/IllnessChooseHandler.ashx?platformType=1&sourceType=1&sourceId=1001&t=1470123962761&action=QueryDiseaseList&sysAttrIds=31,455&bodyId=31");    	
//    	Spider spider = Spider.create(new AjaxProcessor());
//    	for(String url:urls){
//    		spider.addUrl(url);
//    	}
//    	spider.run();

    }

}
