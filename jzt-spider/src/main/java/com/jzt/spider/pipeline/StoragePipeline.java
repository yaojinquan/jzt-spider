package com.jzt.spider.pipeline;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pz998.rpc.model.entity.BdBodyAreaRpc;
import com.pz998.rpc.model.entity.BdBodySymptomRpc;
import com.pz998.rpc.model.entity.SmsPoolRpc;
import com.pz998.rpc.service.BdBodyAreaRpcService;
//import com.pz998.rpc.service.BdBodyAreaRpcService;
//import com.pz998.rpc.service.BdBodySymptomRpcService;
import com.pz998.rpc.service.BdBodySymptomRpcService;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

@Component("storagePipeline")
public class StoragePipeline implements Pipeline{
	
	public static final String SOURCE_TYPE_GZJQWHZYY = "12";
	public static final String SOURCE_TYPE_BDYS = "11";
	@Autowired
	private BdBodySymptomRpcService bdBodySymptomRpcService;
	
	@Autowired
	private BdBodyAreaRpcService bdBodyAreaRpcService;

	//@Override
	public void process(ResultItems resultItems, Task task) {
		List<BdBodyAreaRpc> bdBodyAreaList = resultItems.get("bdBodyAreaList");
		try{
			if(CollectionUtils.isNotEmpty(bdBodyAreaList)){
				for(BdBodyAreaRpc bdBodyAreaRpc:bdBodyAreaList){
					bdBodyAreaRpc.setCreateBy(SmsPoolRpc.CREATE_BY_QZ);
					bdBodyAreaRpc.setCreateTime(new Date());
					bdBodyAreaRpc.setSourceType(SOURCE_TYPE_GZJQWHZYY);
					bdBodyAreaRpcService.save(bdBodyAreaRpc);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		List<BdBodyAreaRpc> subBdBodyAreaList = resultItems.get("subBdBodyAreaList");
		try{
			if(CollectionUtils.isNotEmpty(subBdBodyAreaList)){
				for(BdBodyAreaRpc sub:subBdBodyAreaList){
					sub.setCreateBy(SmsPoolRpc.CREATE_BY_QZ);
					sub.setCreateTime(new Date());
					sub.setSourceType(SOURCE_TYPE_GZJQWHZYY);
					bdBodyAreaRpcService.save(sub);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		try{
			BdBodySymptomRpc bdBodySymptomRpc = resultItems.get("disease");
			if(bdBodySymptomRpc!=null){
				bdBodySymptomRpc.setUpdateBy(SmsPoolRpc.CREATE_BY_QZ);
				bdBodySymptomRpc.setUpdateTime(new Date());
				bdBodySymptomRpcService.updateBySourceId(bdBodySymptomRpc);
				System.out.println("disease1.bodyId:"+bdBodySymptomRpc.getBodyAreaSourceId()+",disease1.diseaseId"+bdBodySymptomRpc.getSourceId());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try{
			List<BdBodySymptomRpc> bdBodySymptomList = resultItems.get("diseaseList");
			if(CollectionUtils.isNotEmpty(bdBodySymptomList)){
				for(int i=0;i<bdBodySymptomList.size();i++){
					
					BdBodySymptomRpc disease = bdBodySymptomList.get(i);
					
					List<BdBodySymptomRpc> diseaseList = null;
				    bdBodySymptomRpcService.getSymptomBySourceId(disease.getSourceId());
					if(!CollectionUtils.isEmpty(diseaseList)){
						BdBodySymptomRpc hasDisease = diseaseList.get(0);
						if(disease.getIsChild()==1){
							hasDisease.setIsChild(disease.getIsChild());
						}
						if(disease.getIsWoman()==1){
							hasDisease.setIsWoman(1);
						}
						bdBodySymptomRpcService.updateGroupBySourceId(hasDisease);
					}else{
						disease.setCreateBy(SmsPoolRpc.CREATE_BY_QZ);
						disease.setCreateTime(new Date());
						disease.setSourceType(SOURCE_TYPE_GZJQWHZYY);
						
						bdBodySymptomRpcService.save(disease);
						System.out.println("disease+["+i+"]+.bodyId:"+disease.getBodyAreaSourceId()+",disease["+i+"].diseaseId"+disease.getSourceId());

					}
			   }
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		
	}

}
