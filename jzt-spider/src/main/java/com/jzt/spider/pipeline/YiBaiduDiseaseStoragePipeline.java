package com.jzt.spider.pipeline;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pz998.rpc.model.entity.BdDiseaseRpc;
import com.pz998.rpc.model.entity.SmsPoolRpc;
import com.pz998.rpc.service.BdDiseaseDoctorRelaRpcService;
import com.pz998.rpc.service.BdDiseaseHospitalRecommendRpcService;
import com.pz998.rpc.service.BdDiseaseRpcService;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import com.pz998.rpc.model.entity.BdDiseaseDoctorRelaRpc;
import com.pz998.rpc.model.entity.BdDiseaseHospitalRecommendRpc;
@Component("yiBaiduDiseaseStoragePipeline")
public class YiBaiduDiseaseStoragePipeline implements Pipeline{
	
	@Autowired
	private BdDiseaseRpcService bdDiseaseRpcService;
	
	@Autowired
	private BdDiseaseHospitalRecommendRpcService bdDiseaseHospitalRecommendRpcService;
	
	@Autowired
	private BdDiseaseDoctorRelaRpcService bdDiseaseDoctorRelaRpcService;
	
	public static final String SOURCE_TYPE_GZJQWHZYY = "12";
	public static final String SOURCE_TYPE_BDYS = "11";
	
	//@Override
	public void process(ResultItems resultItems, Task task) {
		//疾病基本信息保存
		BdDiseaseRpc bdDiseaseRpc = resultItems.get("bdDiseaseRpc");
		if(bdDiseaseRpc != null){
			bdDiseaseRpc.setCreateBy(SmsPoolRpc.CREATE_BY_QZ);
			bdDiseaseRpc.setCreateTime(new Date());
			bdDiseaseRpc.setSourceType(SOURCE_TYPE_BDYS);
			bdDiseaseRpcService.save(bdDiseaseRpc);
		}	
		//跟新疾病简介信息
		BdDiseaseRpc diseaseContextRpc = resultItems.get("diseaseContextRpc");
		if(diseaseContextRpc!=null){
			diseaseContextRpc.setUpdateBy(SmsPoolRpc.CREATE_BY_QZ);
			diseaseContextRpc.setUpdateTime(new Date());
			bdDiseaseRpcService.updateByName(diseaseContextRpc);
		}
		//更新疾病推荐医院数据
		List<BdDiseaseHospitalRecommendRpc> bdDiseaseHospitalRecommendRpcList = resultItems.get("bdDiseaseHospitalRecommendRpcList");
		if(CollectionUtils.isNotEmpty(bdDiseaseHospitalRecommendRpcList)){
			for(BdDiseaseHospitalRecommendRpc bdDiseaseHospitalRecommendRpc:bdDiseaseHospitalRecommendRpcList){
				bdDiseaseHospitalRecommendRpc.setCreateBy(SmsPoolRpc.CREATE_BY_QZ);
				bdDiseaseHospitalRecommendRpc.setCreateTime(new Date());
				System.out.println("更新推荐医院："+bdDiseaseHospitalRecommendRpc.getDiseaseSourceId()+"  "+bdDiseaseHospitalRecommendRpc.getHospitalSourceId());
				bdDiseaseHospitalRecommendRpcService.save(bdDiseaseHospitalRecommendRpc);
			}
		}
		
		//更新疾病推荐医生数据
		List<BdDiseaseDoctorRelaRpc> bdDiseaseDoctorRelaRpcList = resultItems.get("bdDiseaseDoctorRelaRpcList");
		if(CollectionUtils.isNotEmpty(bdDiseaseDoctorRelaRpcList)){
			for(BdDiseaseDoctorRelaRpc bdDiseaseDoctorRelaRpc:bdDiseaseDoctorRelaRpcList){
				bdDiseaseDoctorRelaRpc.setCreateBy(SmsPoolRpc.CREATE_BY_QZ);
				bdDiseaseDoctorRelaRpc.setCreateTime(new Date());
				bdDiseaseDoctorRelaRpc.setSourceType(SOURCE_TYPE_BDYS);
				System.out.println("更新推荐医生："+bdDiseaseDoctorRelaRpc.getDiseaseSourceId()+"  "+bdDiseaseDoctorRelaRpc.getDoctorSourceId());
				bdDiseaseDoctorRelaRpcService.save(bdDiseaseDoctorRelaRpc);
			 }
		}
	}

}
