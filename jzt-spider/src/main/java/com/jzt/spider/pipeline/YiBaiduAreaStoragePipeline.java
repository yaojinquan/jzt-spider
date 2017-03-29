package com.jzt.spider.pipeline;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pz998.rpc.model.entity.BdRegionRpc;
import com.pz998.rpc.service.BdRegionRpcService;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
@Component("yiBaiduAreaStoragePipeline")
public class YiBaiduAreaStoragePipeline implements Pipeline{
	
	@Autowired
	private BdRegionRpcService bdRegionRpcService;
	
	//@Override
	public void process(ResultItems resultItems, Task task) {
		List<BdRegionRpc> regionList = resultItems.get("regionList");
		if(CollectionUtils.isNotEmpty(regionList)){
			for(BdRegionRpc region:regionList){
				bdRegionRpcService.save(region);
			}
		}
	}

}
