package th.co.ais.cpac.cl.batch.util;

import java.math.BigDecimal;

import th.co.ais.cpac.cl.batch.ConstantsBatchReceiveResult;
import th.co.ais.cpac.cl.batch.db.CLBatch;
import th.co.ais.cpac.cl.batch.db.CLBatch.CLBatchPathInfo;
import th.co.ais.cpac.cl.batch.db.CLBatch.CLBatchPathResponse;
import th.co.ais.cpac.cl.common.Context;

public class BatchUtil {
	public static CLBatchPathInfo getBatchPath(Context context, BigDecimal batchTypeId,int environment)throws Exception{
		CLBatch tbl = new CLBatch(context.getLogger());
		CLBatchPathResponse result=tbl.getCLBatchPath(batchTypeId, environment);
		
		if(result==null ||result.getResponse()==null){
			throw new Exception("Error Cannon Get Batch Path --> "+batchTypeId);
		}
		return result.getResponse();
	}
	
	public static  BigDecimal getBatchTypeId(String jobType) throws Exception{
		BigDecimal batchTypeId=new BigDecimal("-99");
		PropertiesReader reader = new PropertiesReader("th.co.ais.cpac.cl.batch.properties.resource","SystemConfigPath");
		if(ConstantsBatchReceiveResult.suspendJobType.equals(jobType)){
			batchTypeId= new BigDecimal(reader.get("suspend.batchTypeID"));
		}else if(ConstantsBatchReceiveResult.terminateJobType.equals(jobType)){
			batchTypeId= new BigDecimal(reader.get("terminate.batchTypeID"));
		}else if(ConstantsBatchReceiveResult.reconnectJobType.equals(jobType)){
			batchTypeId= new BigDecimal(reader.get("reconnect.batchTypeID"));
		}else if(ConstantsBatchReceiveResult.waiveBatchJobType.equals(jobType)){
			batchTypeId= new BigDecimal(reader.get("waiveBatch.batchTypeID"));
		}else if(ConstantsBatchReceiveResult.writeOffJobType.equals(jobType)){
			batchTypeId= new BigDecimal(reader.get("writeOff.batchTypeID"));
		}else if(ConstantsBatchReceiveResult.blacklistJobType.equals(jobType)){
			batchTypeId= new BigDecimal(reader.get("blacklist.batchTypeID"));
		}
		if(batchTypeId==new BigDecimal("-99")){
			throw new Exception("Error Cannon Get Batch Type ID --> "+batchTypeId);
		}
		return batchTypeId;
	}
	public static int getEnvionment()  throws Exception{
		PropertiesReader reader = new PropertiesReader("th.co.ais.cpac.cl.batch.properties.resource","SystemConfigPath");
		return Integer.parseInt(reader.get("env"));
	}
}
