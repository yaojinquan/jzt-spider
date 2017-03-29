package com.jzt.spider.common;

public class UrlConfig {
	public static final String Gzjqwhzyy_START_URL = "http://gzjqwhzyy.yihugz.com/IntelligentGuide/IllnessChoose.aspx?bodyId=31&sex=1&platformType=1&sourceType=1&sourceId=1001";
	
	public static final String DISEASE_LIST_URL = "http://gzjqwhzyy\\.yihugz\\.com/Action/IntelligentGuide/IllnessChooseHandler\\.ashx\\?platformType=1&sourceType=1&sourceId=1001&t=1470123962761&action=QueryDiseaseList&sysAttrIds=\\d+,455&bodyId=\\d+";
	    
	public static final String CHILDE_DISEASE_LIST_URL = "http://gzjqwhzyy\\.yihugz\\.com/Action/IntelligentGuide/IllnessChooseHandler\\.ashx\\?platformType=1&sourceType=1&sourceId=1001&t=1470123962761&action=QueryDiseaseList&sysAttrIds=\\d+,454&bodyId=\\d+";

	public static final String WOMAN_DISEASE_LIST_URL = "http://gzjqwhzyy\\.yihugz\\.com/Action/IntelligentGuide/IllnessChooseHandler\\.ashx\\?platformType=1&sourceType=1&sourceId=1001&t=1470123962761&action=QueryDiseaseList&sysAttrIds=\\d+,456&bodyId=\\d+";

	public static final String SUB_BODY_PART_LIST_URL = "http://gzjqwhzyy\\.yihugz\\.com/Action/IntelligentGuide/IllnessChooseHandler\\.ashx\\?platformType=1&sourceType=1&sourceId=1001&t=1470123603496&action=QueryBodyPartList&parentId=\\d+";
	    
	public static final String DISEASE_DETAIL_URL = "http://gzjqwhzyy\\.yihugz\\.com/Action/IntelligentGuide/IntelligentGuideHandler\\.ashx\\?t=1470124230736&platformType=1&sourceType=1&sourceId=1001&action=GetDiseaseDetail&diseaseId=\\d+";
	    
	public static final String START_URL ="http://gzjqwhzyy.yihugz.com/Action/IntelligentGuide/IntelligentGuideHandler.ashx?t=1470123392066&platformType=1&sourceType=1&sourceId=1001&action=GetBodyPartList";
	    
	public static final int REQUEST_CODE_SUCESS = 10000;
	    
	public static final Integer ATTR_INTRO_ID = 13;
	    
	public static final Integer ATTR_TREATMENT_DESC_ID = 14;
	    
	public static final Integer ATTR_PATHOGENY_DESCR_ID = 40;
	    
	public static final Integer ATTR_DIAGNOSIS_DESCR_ID = 41;
	
	public static final String SYMPTOM_INFO_URL ="http://jib\\.xywy\\.com/il_sii/symptom/\\d+.htm";
	
	public static final String AREA_LIST_URL="https://yi\\.baidu\\.com/pc/hospital/listpage\\?zt=pcpinzhuan&zt_ext=&pvid=\\d+&provId=\\d+&cityId=\\d+"; 
	
	public static final String DOCTOR_START_URL = "https://yi\\.baidu\\.com/pc/doctor/listFanzhi\\?zt=pcpinzhuan&zt_ext=&pvid=1470879584839362&provId=0&cityId=\\d+&regionId=0#fastTop";

	public static final String DEPT_DOCTOR_LIST = "https://yi\\.baidu\\.com/pc/doctor/listdata\\?key=&date=&cityId=\\d+&regionId=0&departLevel1Id=\\d+&departLevel2Id=\\d*&medTitle=0&pageSize=\\d+&serviceType=0&page=\\d+&zt=pcpinzhuan&zt_ext=&pvid=1470879591059493&seed=seed_1470967184529";


	public static final String ALL_DEPT = "0";
	
	public static final String DISEASE_URL = "https://yi\\.baidu\\.com/pc/disease/category\\?diseaseName=\\S+&provId=16&cityId=\\d+&regionId=0&diseaseId=0";     	
	
	public static final String DISEASE_INDEX_URL = "https://yi\\.baidu\\.com/disease/\\S+\\.html\\?zt=&zt_ext=&pvid=1471049532280499&provId=16&cityId=\\d+&regionId=0";
    
	public static final String DISEASE_TO_DOCTOR_LIST_URL = "https://yi\\.baidu\\.com/pc/disease/doctorlist\\?regionId=0&doctorTitle=0&hospitalLevel=0&serviceType=0&sortType=0&isInsurance=&date=all&page=\\d+&pageSize=50&cityId=\\d+&diseaseId=0&diseaseName=\\S+";

	public static final String DISEASE_TO_HOSPITAL_LIST_URL = "https://yi\\.baidu\\.com/pc/disease/hospitallist\\?regionId=0&hospitalType=0&hospitalLevel=0&serviceType=0&sortType=1&isInsurance=0&page=\\d+&pageSize=50&cityId=\\d+&diseaseId=0&diseaseName=\\S+";
    
	public static final String TAB_NAME_DIAGNOSIS = "诊断";
    
	public static final String TAB_NAME_REASON = "病因";
    
	public static final String TAB_NAME_TREATMENT = "治疗";
    
	public static final String TAB_NAME_PHENOMENON = "临床表现";
	
	public static final String HOSPITAL_START_URL = "https://yi.baidu.com/pc/hospital/list?cityId=371&pageSize=10&page=1";
	
	public static final String HOSPITAL_DETAIL_URL = "https://yi\\.baidu\\.com/pc/hospital/index\\?zt=pcpinzhuan&zt_ext=&pvid=\\d+&key=\\S+";

	public static final String HOSPITAL_LIST_URL = "https://yi\\.baidu\\.com/pc/hospital/list\\?provId=\\d+&cityId=\\d+&regionId=\\d*&pageSize=10&page=\\d++";

	
	public static final String HOSPITAL_INFO_URL ="https://yi\\.baidu\\.com/pc/hospital/info\\?key=\\S+";
	
	public static final String DEPT_INFO_URL = "https://yi\\.baidu\\.com/pc/admindepartment/detail\\?zt=\\w+&zt_ext=&pvid=\\d+&hosId=\\d+&adminDepartId=\\d+";
	
	public static final String HOSPITAL_DEPT_URL ="https://yi\\.baidu\\.com/pc/hospital/alldep\\?key=\\S+";
	
	public static final String DOCTOR_LIST_URL = "https://yi\\.baidu\\.com/pc/admindepartment/doctorlist\\?diseaseId=0&medTitle=0&serviceType=0&page=\\d+&pageSize=8&provId=0&cityId=0&regionId=0&adminDepartId=\\d+&hosId=\\d+";

	public static final String DOCTOR_INFO_URL = "https://yi\\.baidu\\.com/pc/doctor/detailpage\\?zt=\\w+&zt_ext=&pvid=0&doctorId=\\d+";


}
