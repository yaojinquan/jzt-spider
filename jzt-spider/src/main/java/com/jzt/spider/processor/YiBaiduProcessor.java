package com.jzt.spider.processor;


import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.util.MultiMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jzt.spider.common.UrlConfig;
import com.pz998.rpc.model.entity.BdDepartmentDiseaseRelaRpc;
import com.pz998.rpc.model.entity.BdDepartmentRpc;
import com.pz998.rpc.model.entity.BdDiseaseDoctorRelaRpc;
import com.pz998.rpc.model.entity.BdDoctorRpc;
import com.pz998.rpc.model.entity.BdHospitalRpc;


import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.JsonPathSelector;
import us.codecraft.xsoup.Xsoup;

public class YiBaiduProcessor implements PageProcessor{
	
	private Site site = Site.me();
	
	
	public static final String STATE_SUCCESS = "0";
	

	//@Override
	public void process(Page page) {
		String url=page.getUrl().toString();
		if(page.getUrl().regex(UrlConfig.HOSPITAL_LIST_URL).match()){
			try{
				String state = new JsonPathSelector("$.status").select(page.getRawText());
				if(STATE_SUCCESS.equals(state)){
					List<String> hospitalList = new JsonPathSelector("$.data.hospitalList[*]").selectList(page.getRawText());
					MultiMap<String> resultMap = com.jzt.spider.util.StringUtils.getUrlParamer(url);
	                String cityId = resultMap.getString("cityId");
	                String regionId = resultMap.getString("regionId");
	                
					if(CollectionUtils.isNotEmpty(hospitalList)){
						List<BdHospitalRpc> bdHospitalList = new ArrayList<BdHospitalRpc>();
						for(String obj:hospitalList){
							JSONObject jsonObj = JSON.parseObject(obj);
							String name = (String)jsonObj.get("name");
							System.out.println("name:"+name);
							String address = (String)jsonObj.get("address");
							String level = (String)jsonObj.get("level");
							Integer insurance = (Integer)jsonObj.get("insurance");
							String phone = (String)jsonObj.get("phone");
							String grade = (String)jsonObj.get("grade");
							Integer doctorNum = (Integer)jsonObj.get("doctorNum");
							String imageUrl = (String)jsonObj.get("logo");
							Integer serveNum = (Integer)jsonObj.get("serveNum");
							Integer commentNum = (Integer)jsonObj.get("commentNum");
							String routeLink = (String)jsonObj.get("routeLink");
							
							MultiMap<String> routeLinkMap = com.jzt.spider.util.StringUtils.getUrlParamer(routeLink);
			                String location = routeLinkMap.getString("location");
			                String latitude = "";
			                String longitude = "";
			                if(StringUtils.isNotEmpty(location)){
			                	String[] locationArray = location.split(",");
			                	latitude = locationArray.length>0?locationArray[0]:"";
			                	longitude = locationArray.length>1?locationArray[1]:"";
			                }
							BdHospitalRpc bdHospitalRpc = new  BdHospitalRpc();
							bdHospitalRpc.setSourceId(name);
							bdHospitalRpc.setName(name);
							bdHospitalRpc.setAddress(address);
							bdHospitalRpc.setLevel(level);
							bdHospitalRpc.setPhone(phone);
							bdHospitalRpc.setImageUrl(imageUrl);
							bdHospitalRpc.setLatitude(latitude);
							bdHospitalRpc.setLongitude(longitude);
							bdHospitalRpc.setScore(grade);
//							String city = CITY_MAP.get(cityId);
//							bdHospitalRpc.setCity(city);
							bdHospitalRpc.setCitySourceId(cityId);
							String insuranceStr = insurance==null?"":insurance.toString();
							bdHospitalRpc.setIsMedicalInsurance(insuranceStr);
							String doctorNumStr = doctorNum==null?"":doctorNum.toString();
							bdHospitalRpc.setHighQualityDoctorNum(doctorNumStr);
							
							String serveNumStr = serveNum==null?"":serveNum.toString();
							bdHospitalRpc.setFinishedServiceNum(serveNumStr);
							
							String commentNumStr=commentNum==null?"":commentNum.toString();
							bdHospitalRpc.setPatientCommentNum(commentNumStr);
							
							bdHospitalRpc.setRegionSourceId(regionId);
							bdHospitalList.add(bdHospitalRpc);
	
							String infoUrl = "https://yi.baidu.com/pc/hospital/info?key="+name;
							String allDeptUrl = "https://yi.baidu.com/pc/hospital/alldep?key="+name;
							page.addTargetRequest(infoUrl); 
							page.addTargetRequest(allDeptUrl);
						}
						
						page.putField("bdHospitalList", bdHospitalList);
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		} 
			else if(page.getUrl().regex(UrlConfig.HOSPITAL_INFO_URL).match()){
			try{
				MultiMap<String> resultMap = com.jzt.spider.util.StringUtils.getUrlParamer(url);
                String hosName = resultMap.getString("key");
                BdHospitalRpc bdHospitalRpc = new BdHospitalRpc();
				List<String> contextList = page.getHtml().xpath("ul[@class='container-list-info']/li[@class='ys-util-margin-b35']/p[@class='ys-util-text-smaller ys-util-margin-t9 ys-util-margin-b30']/text()").all();
				if(CollectionUtils.isNotEmpty(contextList)){
					String context1 = contextList.size()>=1?contextList.get(0):"";
					String context2 = contextList.size()>=2?contextList.get(1):"";
					String context3 = contextList.size()>=3?contextList.get(2):"";
					String context4 = contextList.size()>=4?contextList.get(3):"";
					String context5 = contextList.size()>=5?contextList.get(4):"";
					
					bdHospitalRpc.setContent(context1);
					bdHospitalRpc.setHistory(context2);
					bdHospitalRpc.setCharacteristicDept(context3);
					bdHospitalRpc.setTeam(context4);
					bdHospitalRpc.setHonor(context5);
//					System.out.println("医院概况:"+context1);
//					System.out.println("历史沿革:"+context2);
//					System.out.println("特色科室:"+context3);
//					System.out.println("医护团队:"+context4);
//					System.out.println("医院荣誉:"+context5);
				}
				
				bdHospitalRpc.setSourceId(hosName);
				page.putField("bdHospitalRpc", bdHospitalRpc);
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}else if(page.getUrl().regex(UrlConfig.HOSPITAL_DEPT_URL).match()){
			try{
            	MultiMap<String> resultMap = com.jzt.spider.util.StringUtils.getUrlParamer(url);
                String hosName = resultMap.getString("key");
                String topDepts = "";
                List<String> tableHtml  = page.getHtml().xpath("div[@class='container-common-office']/table[@class='ys-util-margin-b15 list-office ys-util-border-big']").all();
				List<BdDepartmentRpc> departmentList = new ArrayList<BdDepartmentRpc>();
				for(String html:tableHtml){
					 Document document = Jsoup.parse(html);
					 String platDept = Xsoup.select(document, "td[@class='primary-office']/h4/text()").get();
					 List<String> hospitalDepts = Xsoup.select(document, "td[@class='secondary-office']/dl/dd/h4/a[@class='a-hover ys-util-text-normal']").list();
					 List<String> hospitalDeptNames = Xsoup.select(document, "td[@class='secondary-office']/dl/dd/h4/a[@class='a-hover ys-util-text-normal']/text()").list();

					 //重点科室信息
					 if(StringUtils.isEmpty(platDept)){
					       topDepts = com.jzt.spider.util.StringUtils.listToString(hospitalDeptNames);
					 //医院科室信息 	 
					 }else{
						 for(String d:hospitalDepts){
							 Document deptDocument = Jsoup.parse(d);
							 String deptName = Xsoup.select(deptDocument, "a/text()").get();
							 String deptHref = Xsoup.select(deptDocument, "a/@href").get();
							 MultiMap<String> deptResultMap = com.jzt.spider.util.StringUtils.getUrlParamer(deptHref);
				             String deptId = deptResultMap.getString("adminDepartId");
							 String hosId = deptResultMap.getString("hosId");
							 BdDepartmentRpc bdDepart = new BdDepartmentRpc();
							 bdDepart.setSourceId(deptId);
							 bdDepart.setName(deptName);
							 bdDepart.setParentSource(platDept);
							 bdDepart.setHospitalSource(hosName);	 
							 departmentList.add(bdDepart);
							 //将科室详情地址放入目标采集队列
							 
							 
							 page.addTargetRequest(deptHref);
							 //将科室下医生列表链接放入队列
							 for(int i=1;i<6;i++){
								 String doctorUrl = "https://yi.baidu.com/pc/admindepartment/doctorlist?diseaseId=0&medTitle=0&serviceType=0&page="+i+"&pageSize=8&provId=0&cityId=0&regionId=0&adminDepartId="+deptId+"&hosId="+hosId;
								
								 
								 page.addTargetRequest(doctorUrl);
							 }
							 
						 }
					 }
				}
				
				BdHospitalRpc bdHospitalRpc = new BdHospitalRpc();
				bdHospitalRpc.setSourceId(hosName);
			    System.out.println("重点科室："+topDepts);
				bdHospitalRpc.setCharacteristicFaculty(topDepts);
				page.putField("hosTopDept", bdHospitalRpc);
				page.putField("departmentList", departmentList);
				
				//	System.out.println(page.getHtml().toString());
			}catch(Exception e){
				e.printStackTrace();
			}
		//采集科室信息	
		}
		else if(page.getUrl().regex(UrlConfig.DEPT_INFO_URL).match()){
			String deptPhone = page.getHtml().xpath("div[@class='summary-left']/div[@class='summary-row ys-util-margin-t12 ys-util-text-normal-height']/label[@class='ys-util-text-normal ys-util-margin-l10']/text()").toString();
			String deptAddress = page.getHtml().xpath("div[@class='summary-left']/div[@class='summary-row ys-util-margin-t8 ys-util-text-normal']/label[@class='ys-util-text-normal ys-util-margin-l10']/text()").toString();
			String content = page.getHtml().xpath("div[@class='office-info']/p[@class='ys-util-text-smaller ys-util-margin-t15 office-info-total']/text()").toString();
			String titleDescr = page.getHtml().xpath("div[@class='summary-left']/div[@class='summary-row ys-util-margin-t12 ys-util-text-min-height']/h3[@class='ys-util-text-min ys-util-margin-r12']/text()").toString();
			
			MultiMap<String> deptResultMap = com.jzt.spider.util.StringUtils.getUrlParamer(url);
            String deptId = deptResultMap.getString("adminDepartId");
			String hosId = deptResultMap.getString("hosId");
			
			BdDepartmentRpc bdDepartmentRpc = new BdDepartmentRpc();
			bdDepartmentRpc.setAddress(deptAddress);
			bdDepartmentRpc.setPhone(deptPhone);
			bdDepartmentRpc.setContent(content);
			bdDepartmentRpc.setSourceId(deptId);
			bdDepartmentRpc.setTitleDescr(titleDescr);	
			page.putField("bdDepartmentRpc", bdDepartmentRpc);
			
		}
		else if(page.getUrl().regex(UrlConfig.DOCTOR_LIST_URL).match()){
			String status = new JsonPathSelector("$.status").select(page.getRawText());
			if(STATE_SUCCESS.equals(status)){
				String data = new JsonPathSelector("$.data[*]").select(page.getRawText());
				if(data!=null){
					MultiMap<String> deptResultMap = com.jzt.spider.util.StringUtils.getUrlParamer(url);
		            String deptId = deptResultMap.getString("adminDepartId");
					String hosId = deptResultMap.getString("hosId");
					String pageNum = deptResultMap.getString("page");
					List<BdDepartmentDiseaseRelaRpc> BdDepartmentDiseaseRelaRpcList = new ArrayList<BdDepartmentDiseaseRelaRpc>();
					
					JSONObject dataJo = JSON.parseObject(data);
					
					if("1".equals(pageNum)){
						JSONArray diseaseArray= dataJo==null?null:(JSONArray)dataJo.getJSONArray("selectorList");
						if(CollectionUtils.isNotEmpty(diseaseArray)){
							JSONObject obj =  (JSONObject)diseaseArray.get(0);
							JSONArray diseaseList = (JSONArray)obj.getJSONArray("list");
							if(CollectionUtils.isNotEmpty(diseaseList)){
								for(Object disease:diseaseList){
									JSONObject diseaseJo=(JSONObject)disease;
									String itemName = (String)diseaseJo.get("itemName");
									if("全部".equals(itemName)){
										continue;
									}
									BdDepartmentDiseaseRelaRpc bdDepartmentDiseaseRelaRpc = new BdDepartmentDiseaseRelaRpc();
									bdDepartmentDiseaseRelaRpc.setHospitalSourceId(hosId);
									bdDepartmentDiseaseRelaRpc.setDepartmentSourceId(deptId);
									bdDepartmentDiseaseRelaRpc.setDiseaseSource(itemName);
									BdDepartmentDiseaseRelaRpcList.add(bdDepartmentDiseaseRelaRpc);
								}
							}
							
						}
					}
					
					page.putField("bdDepartmentDiseaseRelaRpcList", BdDepartmentDiseaseRelaRpcList);
					if(dataJo.containsKey("doctorList")){
						List<String> doctorList = new JsonPathSelector("$.data.doctorList[*]").selectList(page.getRawText());
						if(CollectionUtils.isNotEmpty(doctorList)){
							//收集医生信息
							List<BdDoctorRpc> bdDoctorList = new ArrayList<BdDoctorRpc>();
							//收集医生与疾病关系信息
							List<BdDiseaseDoctorRelaRpc> bdDiseaseDoctorRelaList = new ArrayList<BdDiseaseDoctorRelaRpc>();
							for(String o:doctorList){
								JSONObject doctorJo = JSON.parseObject(o);
								//医生认证信息
								String identifyMarkStr = "";
								if(doctorJo.containsKey("doctorIdentify")){
									List<String> identifyMarkList = new JsonPathSelector("$.doctorIdentify[*].identifyMark").selectList(doctorJo.toJSONString());
									identifyMarkStr =  com.jzt.spider.util.StringUtils.listToString(identifyMarkList);
								}
								
								String doctorName = (String)doctorJo.get("doctorName");
								String doctorTitle= (String)doctorJo.get("doctorTitle");
								Object commentScore = doctorJo.get("commentScore");
								String doctorSkill = (String)doctorJo.get("doctorSkill");
								String allTimeHref = (String)doctorJo.get("allTimeHref");
								String doctorPhoto = (String)doctorJo.get("doctorPhoto");
								//医生详情页加入目标采集
								
								page.addTargetRequest(allTimeHref);
								MultiMap<String> resultMap = com.jzt.spider.util.StringUtils.getUrlParamer(allTimeHref);
				                String doctorId = resultMap.getString("doctorId");
				                
				                BdDoctorRpc bdDoctorRpc = new BdDoctorRpc();
				                bdDoctorRpc.setHospitalSourceId(hosId);
				                bdDoctorRpc.setDepartmentSourceId(deptId);
				                bdDoctorRpc.setSourceId(doctorId);
				                bdDoctorRpc.setName(doctorName);
				                bdDoctorRpc.setPracticeTitle(doctorTitle);
				                String commentScoreStr = commentScore==null?"":commentScore.toString();
				                bdDoctorRpc.setRecommendScore(commentScoreStr);
				                bdDoctorRpc.setDiseaseTag(doctorSkill);
				                bdDoctorRpc.setImageUrl(doctorPhoto);
				                bdDoctorRpc.setIdentifyMark(identifyMarkStr);
				                bdDoctorList.add(bdDoctorRpc);
				    			
								JSONArray treatPatientArray = (JSONArray)doctorJo.get("treatPatient");
								if(CollectionUtils.isNotEmpty(treatPatientArray)){
									for(Object treatPatient:treatPatientArray){
										JSONObject treatPatientJo = (JSONObject)treatPatient;
										String diseaseName = (String)treatPatientJo.get("diseaseName");
										BdDiseaseDoctorRelaRpc bdDiseaseDoctorRelaRpc = new BdDiseaseDoctorRelaRpc();
										bdDiseaseDoctorRelaRpc.setDiseaseSourceId(diseaseName);
										bdDiseaseDoctorRelaRpc.setDoctorSourceId(doctorId);
										bdDiseaseDoctorRelaList.add(bdDiseaseDoctorRelaRpc);
									}
								}
							}
							
							page.putField("bdDiseaseDoctorRelaList", bdDiseaseDoctorRelaList);
							page.putField("bdDoctorList", bdDoctorList);
						}
					}		
				}
			}
				
		}else if(page.getUrl().regex(UrlConfig.DOCTOR_INFO_URL).match()){
			MultiMap<String> deptResultMap = com.jzt.spider.util.StringUtils.getUrlParamer(url);
            String doctorId = deptResultMap.getString("doctorId");
			
            BdDoctorRpc bdDoctorRpc = new BdDoctorRpc();
            bdDoctorRpc.setSourceId(doctorId);
            
            String experience = page.getHtml().xpath("div[@class='doctor-experience']/div[@class='ys-util-text-smaller ys-util-margin-t10']/text()").toString();
            if(StringUtils.isEmpty(experience)){
                 experience = page.getHtml().xpath("div[@class='doctor-experience']/div[@class='ys-util-text-smaller ys-util-margin-t10 doctor-info-total']/text()").toString();
            }
            bdDoctorRpc.setIntro(experience);
            System.out.println("experience:"+experience);
 
            String diseaseTag = page.getHtml().xpath("label[@id='goodat-label']/text()").toString();
            bdDoctorRpc.setDiseaseTag(diseaseTag);
            System.out.println("diseaseTag:"+diseaseTag);
            String recommendScore = page.getHtml().xpath("div[@class='summary-row ys-util-margin-t12']/span[@class='ys-util-text-primary ys-util-text-medium summary-comment-score ys-util-margin-r20']/text()").toString();
           ///span[@class='ys-util-margin-l6 ys-util-text-primary']/text()
          List<String> commentList = page.getHtml().xpath("article[@class='container-frame']/section[@class='container-summary']/div[@class='summary-list']/div[@class='summary-row ys-util-margin-t12']").all();
	      if(CollectionUtils.isNotEmpty(commentList)){  
		      	String commnet1 = commentList.size()>=1?commentList.get(0):"";
		      	Document scoreHtml = Jsoup.parse(commnet1);
		      	List<String> scoreSpanHtml =  Xsoup.select(scoreHtml, "label[@class='ys-util-text-secondary']/span[@class='ys-util-margin-l6 ys-util-text-primary']/text()").list();
		      	if(CollectionUtils.isNotEmpty(scoreSpanHtml)){
			      	bdDoctorRpc.setRecommendScore(recommendScore);
			      	bdDoctorRpc.setTreatmentEffectScore(scoreSpanHtml.get(1));
			      	bdDoctorRpc.setAttitudeScore(scoreSpanHtml.get(0));
			      	System.out.println("recommendScore:"+recommendScore);
			        System.out.println("treatmentEffectScore:"+scoreSpanHtml.get(1));
			        System.out.println("attitudeScore:"+scoreSpanHtml.get(0));
		      	}

	      }
	      	
	        
            page.putField("bdDoctorRpc", bdDoctorRpc);
            
	    }
		
		
	}	

	//@Override
	public Site getSite() {
//		List<String[]> ipList = IpProxyUtil.getIpProxyCollection();
//		site.setHttpProxyPool(ipList);
		return site;
	}
	
	public static void main(String[] args) {
		//String url = "https://yi.baidu.com/pc/hospital/list?provId=25&cityId=196&regionId=370102&pageSize=10&page=1";
					//  https://yi.baidu.com/pc/hospital/list?provId=16&cityId=372&regionId=&pageSize=10&page=1
		String url = "https://yi.baidu.com/pc/hospital/list?provId=16&cityId=372&regionId=&pageSize=10&page=1";
		Spider ocSpider =Spider.create(new YiBaiduProcessor()).addUrl(url).thread(10);
		ocSpider.start();
//		SpiderMonitor.instance().register(ocSpider);
//		ocSpider.start();
	}
}
