package com.jzt.spider.pipeline;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pz998.rpc.model.entity.BdDepartmentRpc;
import com.pz998.rpc.model.entity.BdPlatformHospitalDeptRelaRpc;
import com.pz998.rpc.model.entity.SmsPoolRpc;
import com.pz998.rpc.service.BdDepartmentRpcService;
import com.pz998.rpc.service.BdHospitalRpcService;
import com.pz998.rpc.service.BdPlatformHospitalDeptRelaRpcService;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
@Component("yiBaiduDepartmentRelaStoragePipeline")
public class YiBaiduDepartmentRelaStoragePipeline implements Pipeline{
		
	@Autowired
	private BdDepartmentRpcService bdDepartmentRpcService;
	
	@Autowired
	private BdHospitalRpcService bdHospitalRpcService;
	
	@Autowired
	private BdPlatformHospitalDeptRelaRpcService  bdPlatformHospitalDeptRelaRpcService;
	
	public static final String SOURCE_TYPE_GZJQWHZYY = "12";
	public static final String SOURCE_TYPE_BDYS = "11";
	
	//@Override
	public void process(ResultItems resultItems, Task task) {
		List<BdDepartmentRpc> platformDeptList =  resultItems.get("platformDeptList");
		if(CollectionUtils.isNotEmpty(platformDeptList)){
			for(BdDepartmentRpc bdDepartmentRpc:platformDeptList){
				bdDepartmentRpc.setCreateBy(SmsPoolRpc.CREATE_BY_QZ);
				bdDepartmentRpc.setCreateTime(new Date());
				bdDepartmentRpc.setSourceType(SOURCE_TYPE_BDYS);
				bdDepartmentRpcService.save(bdDepartmentRpc);
			}
		}
		
		
		
		List<Map<String,String>> platdeptRelaMapList = resultItems.get("platdeptRelaMapList");
		List<Map<String,String>> resultList =  removal(platdeptRelaMapList);
		
		
		if(CollectionUtils.isNotEmpty(resultList)){
			for(Map<String,String> platdeptRelaMap:resultList){
				 String hospitalName = platdeptRelaMap.get("hospitalName");
				 Map<String,String> reMap =  bdHospitalRpcService.queryHospital("9");
				 String hosCode = reMap.get(hospitalName);
				 platdeptRelaMap.put("hospitalCode", hosCode);
				 String departName =  platdeptRelaMap.get("departName");
				 String departLevel2Id = platdeptRelaMap.get("departLevel2Id");
				 String level = platdeptRelaMap.get("level");
				 
				 
				 List<BdDepartmentRpc> deptList =  bdDepartmentRpcService.queryDepartmentListByHospitalNameAndDeptname(platdeptRelaMap);
				 BdDepartmentRpc bdDept = deptList.size()>0?deptList.get(0):null;
				 if(bdDept==null){
					 continue;
				 }
				 String deptCode = bdDept.getCode();
				 String hospitalCode = bdDept.getHospitalCode();
				 
				 Map<String,String> paramMap = new HashMap<String,String>();
				 paramMap.put("deptCode", deptCode);
				 paramMap.put("hospitalCode", hospitalCode);
				 paramMap.put("departLevel2Id", departLevel2Id); 
				 
				 List<BdDepartmentRpc> relaList =  bdPlatformHospitalDeptRelaRpcService.querydeptRelaByDeptcodeAndHospitalCode(paramMap);
				 if(CollectionUtils.isNotEmpty(relaList)){
					 continue;
				 }
				 System.out.println("更新关系 医院科室："+deptCode+" 平台科室："+departLevel2Id);
				 BdPlatformHospitalDeptRelaRpc bdPlatformHospitalDeptRelaRpc = new BdPlatformHospitalDeptRelaRpc();
				 bdPlatformHospitalDeptRelaRpc.setCreateBy(SmsPoolRpc.CREATE_BY_QZ);
				 bdPlatformHospitalDeptRelaRpc.setCreateTime(new Date());
				 bdPlatformHospitalDeptRelaRpc.setSourceType(SOURCE_TYPE_BDYS);
				 bdPlatformHospitalDeptRelaRpc.setPlatformDeptSourceId(departLevel2Id);
				 bdPlatformHospitalDeptRelaRpc.setHospitalDeptCode(deptCode);
				 bdPlatformHospitalDeptRelaRpc.setHospitalCode(hospitalCode);
				 bdPlatformHospitalDeptRelaRpc.setPlatformDeptLevel(level);
				 bdPlatformHospitalDeptRelaRpc.setHospitalDeptLevel(BdDepartmentRpc.LEVEL_FIRST);
				 bdPlatformHospitalDeptRelaRpcService.save(bdPlatformHospitalDeptRelaRpc);
			}
		}
		//更新平台科室编码
		//bdDepartmentRpcService.updateDeptCode();
		

	}
	
	private List<Map<String,String>> removal(List<Map<String,String>> platdeptRelaMapList){
		
		List<Map<String,String>> resultList = new ArrayList<Map<String,String>>();
		Set<String> set = new HashSet<String>();
		String departLevel2Id = "";
		String level="";
		if(CollectionUtils.isNotEmpty(platdeptRelaMapList)){
			
			for(Map<String,String> platdeptRelaMap:platdeptRelaMapList){
				String hospitalName = platdeptRelaMap.get("hospitalName");
				 String departName =  platdeptRelaMap.get("departName");
				 departLevel2Id = platdeptRelaMap.get("departLevel2Id");
				 level = platdeptRelaMap.get("level");
				 String s = hospitalName+"_"+departName;
				 set.add(s); 
			}
			
		}
		
		if(CollectionUtils.isNotEmpty(set)){
			for(String hosAndDept:set){
				String[] aa = hosAndDept.split("_");
				Map<String,String> remap = new HashMap<String,String>();
				remap.put("hospitalName",aa[0]);
				remap.put("departName",aa[1]);
				remap.put("departLevel2Id", departLevel2Id);
				remap.put("level", level);
				
				resultList.add(remap);
			}
		}
		return resultList;
	}

}
