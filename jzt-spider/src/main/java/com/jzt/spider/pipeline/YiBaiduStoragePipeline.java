package com.jzt.spider.pipeline;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pz998.rpc.model.entity.BdDepartmentDiseaseRelaRpc;
import com.pz998.rpc.model.entity.BdDepartmentRpc;
import com.pz998.rpc.model.entity.BdDoctorRpc;
import com.pz998.rpc.model.entity.BdHospitalRpc;
import com.pz998.rpc.model.entity.SmsPoolRpc;
import com.pz998.rpc.service.BdDepartmentDiseaseRelaRpcService;
import com.pz998.rpc.service.BdDepartmentRpcService;
import com.pz998.rpc.service.BdDiseaseDoctorRelaRpcService;
import com.pz998.rpc.service.BdDoctorRpcService;
import com.pz998.rpc.service.BdHospitalRpcService;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
@Component("yiBaiduStoragePipeline")
public class YiBaiduStoragePipeline implements Pipeline{
	
	@Autowired
	private BdHospitalRpcService bdHospitalRpcService;
	
	@Autowired
	private BdDepartmentRpcService bdDepartmentRpcService;
	
	@Autowired
	private BdDoctorRpcService bdDoctorRpcService;
	
	@Autowired
	private BdDiseaseDoctorRelaRpcService bdDiseaseDoctorRelaRpcService;
	
	@Autowired
	private BdDepartmentDiseaseRelaRpcService bdDepartmentDiseaseRelaRpcService;
	
	public static final String SOURCE_TYPE_GZJQWHZYY = "12";
	public static final String SOURCE_TYPE_BDYS = "11";
	
	//@Override
	public void process(ResultItems resultItems, Task task) {
		//医院基本信息保存
		List<BdHospitalRpc> bdHospitalList = (List<BdHospitalRpc>)resultItems.get("bdHospitalList");
		if(CollectionUtils.isNotEmpty(bdHospitalList)){
			for(BdHospitalRpc bdHospitalRpc:bdHospitalList){
				bdHospitalRpc.setCreateBy(SmsPoolRpc.CREATE_BY_QZ);
				bdHospitalRpc.setCreateTime(new Date());
				bdHospitalRpc.setSourceType(SOURCE_TYPE_BDYS);
				try {
					InetAddress addr = InetAddress.getLocalHost();
					String ip=addr.getHostAddress();
					bdHospitalRpc.setSpiderIp(ip);
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				List<BdHospitalRpc> hosList =  bdHospitalRpcService.getHospitalBySourceId(bdHospitalRpc.getSourceId());
				if(CollectionUtils.isNotEmpty(hosList)){
					continue;
				}
				
				bdHospitalRpcService.save(bdHospitalRpc);
				
				bdHospitalRpc.setUpdateBy(SmsPoolRpc.CREATE_BY_QZ);
				bdHospitalRpc.setUpdateTime(new Date());
				bdHospitalRpcService.update(bdHospitalRpc);
			}
		}
		//更新医院概况，历史等信息
		BdHospitalRpc bdHospitalRpc = (BdHospitalRpc)resultItems.get("bdHospitalRpc");
		if(bdHospitalRpc!=null){
			bdHospitalRpc.setUpdateBy(SmsPoolRpc.CREATE_BY_QZ);
			bdHospitalRpc.setUpdateTime(new Date());
			bdHospitalRpcService.updateBySourceId(bdHospitalRpc);
		}
		//更新医院重点科室信息
		BdHospitalRpc hosTopDept = (BdHospitalRpc)resultItems.get("hosTopDept");
		if(hosTopDept != null){
			hosTopDept.setUpdateBy(SmsPoolRpc.CREATE_BY_QZ);
			hosTopDept.setUpdateTime(new Date());
			System.out.println("更新医院："+hosTopDept.getSourceId()+"中的重点科室："+hosTopDept.getCharacteristicFaculty());
			bdHospitalRpcService.updateForTopDept(hosTopDept);
		}
		
		//科室基本信息保存
		List<BdDepartmentRpc> departmentList = resultItems.get("departmentList");
		if(CollectionUtils.isNotEmpty(departmentList)){
			for(BdDepartmentRpc bdDepartmentRpc:departmentList){
				bdDepartmentRpc.setCreateBy(SmsPoolRpc.CREATE_BY_QZ);
				bdDepartmentRpc.setCreateTime(new Date());
				bdDepartmentRpc.setSourceType(SOURCE_TYPE_BDYS);
				bdDepartmentRpc.setLevel(BdDepartmentRpc.LEVEL_FIRST);
				bdDepartmentRpc.setType(BdDepartmentRpc.TYPE_HOSPITAL);
				
				try {
					InetAddress addr = InetAddress.getLocalHost();
					String ip=addr.getHostAddress();
					bdDepartmentRpc.setSpiderIp(ip);
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				List<BdDepartmentRpc> deptList = bdDepartmentRpcService.getDepartmentRpcBySourceId(bdDepartmentRpc.getSourceId());
				if(CollectionUtils.isNotEmpty(deptList)){
					continue;
				}
				
				bdDepartmentRpcService.save(bdDepartmentRpc);
			}
		}
		
		BdDepartmentRpc bdDepartmentRpc = resultItems.get("bdDepartmentRpc");
		if(bdDepartmentRpc != null){
			bdDepartmentRpc.setUpdateBy(SmsPoolRpc.CREATE_BY_QZ);
			bdDepartmentRpc.setUpdateTime(new Date());
			bdDepartmentRpcService.updateBySourceId(bdDepartmentRpc);
		}
		
		//保存医生信息
		List<BdDoctorRpc> bdDoctorList =  resultItems.get("bdDoctorList");
		if(CollectionUtils.isNotEmpty(bdDoctorList)){
			for(BdDoctorRpc bdDoctorRpc:bdDoctorList){
				List<BdDoctorRpc> docList = bdDoctorRpcService.queryBySourceId(bdDoctorRpc);
				if(CollectionUtils.isNotEmpty(docList)){
					continue;
				}
				
				bdDoctorRpc.setCreateBy(SmsPoolRpc.CREATE_BY_QZ);
				bdDoctorRpc.setCreateTime(new Date());
				bdDoctorRpc.setSourceType(SOURCE_TYPE_BDYS);
				bdDoctorRpcService.save(bdDoctorRpc);
			}
			
		}
		
		//疾病与医生关系不在主线中采集
//		List<BdDiseaseDoctorRelaRpc> bdDiseaseDoctorRelaList = resultItems.get("bdDiseaseDoctorRelaList");
//		if(CollectionUtils.isNotEmpty(bdDiseaseDoctorRelaList)){
//			for(BdDiseaseDoctorRelaRpc bdDiseaseDoctorRelaRpc:bdDiseaseDoctorRelaList){
//				List<BdDiseaseDoctorRelaRpc>  list =  bdDiseaseDoctorRelaRpcService.queryByDoctorSourceId(bdDiseaseDoctorRelaRpc);
//				if(CollectionUtils.isNotEmpty(list)){
//					continue;
//				}
//				bdDiseaseDoctorRelaRpc.setCreateBy(SmsPoolRpc.CREATE_BY_QZ);
//				bdDiseaseDoctorRelaRpc.setCreateTime(new Date());
//				bdDiseaseDoctorRelaRpc.setSourceType(SOURCE_TYPE_BDYS);
//				bdDiseaseDoctorRelaRpcService.save(bdDiseaseDoctorRelaRpc);
//			}
//		}
		
		BdDoctorRpc bdDoctorRpc = resultItems.get("bdDoctorRpc");
		if(bdDoctorRpc!=null){
			bdDoctorRpc.setUpdateBy(SmsPoolRpc.CREATE_BY_QZ);
			bdDoctorRpc.setUpdateTime(new Date());
			bdDoctorRpcService.updateBySourceId(bdDoctorRpc);
		}
		
		//保存科室与疾病的对应关系
		List<BdDepartmentDiseaseRelaRpc> bdDepartmentDiseaseRelaRpcList = resultItems.get("bdDepartmentDiseaseRelaRpcList");
		if(CollectionUtils.isNotEmpty(bdDepartmentDiseaseRelaRpcList)){
			for(BdDepartmentDiseaseRelaRpc bdDepartmentDiseaseRela:bdDepartmentDiseaseRelaRpcList){
				//如果已经建立关系 则不再建立
				if(isExistRela(bdDepartmentDiseaseRela)){
					continue;
				}
				bdDepartmentDiseaseRela.setCreateBy(SmsPoolRpc.CREATE_BY_QZ);
				bdDepartmentDiseaseRela.setCreateTime(new Date());
				bdDepartmentDiseaseRela.setSourceType(SOURCE_TYPE_BDYS);
				bdDepartmentDiseaseRelaRpcService.save(bdDepartmentDiseaseRela);
			}
		}
		
	}

	private boolean isExistRela(BdDepartmentDiseaseRelaRpc bdDepartmentDiseaseRela) {
		List<BdDepartmentDiseaseRelaRpc> bdDepartmentDiseaseRelaList =  bdDepartmentDiseaseRelaRpcService.queryDepartmentDiseaseByDiseaseSource(bdDepartmentDiseaseRela);
		if(CollectionUtils.isNotEmpty(bdDepartmentDiseaseRelaList)){
			return true;
		}
		
		return false;
	}
	


}
