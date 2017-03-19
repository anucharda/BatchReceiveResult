package th.co.ais.cpac.cl.batch.util;

import java.math.BigDecimal;

import th.co.ais.cpac.cl.batch.db.CLBatch;
import th.co.ais.cpac.cl.batch.db.CLBatch.CLBatchPathInfo;
import th.co.ais.cpac.cl.batch.db.CLBatch.CLBatchPathResponse;
import th.co.ais.cpac.cl.common.Context;

public class BatchReceiveUtil {
	public static CLBatchPathInfo getBatchPath(Context context, BigDecimal batchTypeId,int environment)throws Exception{
		CLBatch tbl = new CLBatch(context.getLogger());
		CLBatchPathResponse result=tbl.getCLBatchPath(batchTypeId, environment);
		
		if(result==null ||result.getResponse()==null){
			throw new Exception("Error Cannon Get Batch Path --> "+batchTypeId);
		}
		return result.getResponse();
	}
}
