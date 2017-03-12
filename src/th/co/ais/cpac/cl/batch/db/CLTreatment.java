package th.co.ais.cpac.cl.batch.db;

import java.math.BigDecimal;

import th.co.ais.cpac.cl.batch.ConstantsBatchReceiveResult;
import th.co.ais.cpac.cl.batch.db.CLOrder.CLOrderInfoResponse;
import th.co.ais.cpac.cl.batch.util.ValidateUtil;
import th.co.ais.cpac.cl.common.Context;
import th.co.ais.cpac.cl.common.UtilityLogger;
import th.co.ais.cpac.cl.template.database.DBConnectionPools;
import th.co.ais.cpac.cl.template.database.DBTemplatesResponse;
import th.co.ais.cpac.cl.template.database.DBTemplatesUpdate;

public class CLTreatment {
	protected final UtilityLogger logger;

	public CLTreatment(UtilityLogger logger) {
		this.logger = logger;
	}

	public class ExecuteResponse extends DBTemplatesResponse<Boolean> {

		@Override
		protected Boolean createResponse() {
			return false;
		}

		@Override
		public void setResponse(Boolean boo) {
			response = boo;
		}
	}
	
	protected class UpdateWriteOffFailAction extends DBTemplatesUpdate<ExecuteResponse, UtilityLogger, DBConnectionPools> {
		private BigDecimal batchID;
		private String username;
		private String failReason;
		private int actStatus;
		public UpdateWriteOffFailAction(UtilityLogger logger) {
			super(logger);
		}

		@Override
		protected ExecuteResponse createResponse() {
			return new ExecuteResponse();
		}

		@Override
		protected StringBuilder createSqlProcess() {
			StringBuilder sql = new StringBuilder();
			sql.append("UPDATE dbo.CL_TREATMENT ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append("SET LAST_UPD= getdate() , LAST_UPD_BY='").append(username).append("'").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(",ACTION_STATUS = ").append(actStatus).append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(", ACTION_STATUS_DTM  = getdate() ").append(ConstantsBatchReceiveResult.END_LINE);
			if(!ValidateUtil.isNull(failReason)){
				sql.append(", ACTION_REMARK   ='").append(failReason).append("'").append(ConstantsBatchReceiveResult.END_LINE);
			}
			sql.append("FROM CL_TREATMENT T ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append("JOIN CL_WRITEOFF_TREATMENT WT ON T.TREATMENT_ID = WT.TREATMENT_ID ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append("JOIN CL_WRITEOFF W ON WT.WRITEOFF_ID = W.WRITEOFF_ID ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append("WHERE T.ACTION_STATUS = 3 ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append("AND W.BATCH_ID = ").append(batchID).append(ConstantsBatchReceiveResult.END_LINE);
			return sql;
		}


		protected ExecuteResponse execute(int actStatus,  BigDecimal batchID,String username,String failReason) {
			this.actStatus = actStatus;
			this.batchID = batchID;
			this.username = username;
			this.failReason=failReason;
			return executeUpdate(ConstantsBatchReceiveResult.getDBConnectionPools(logger), true);
		}
	}

	public ExecuteResponse updateWriteOffFail(int actStatus,  BigDecimal batchID,String username,String failReason,Context context) throws Exception {
		ExecuteResponse response= new UpdateWriteOffFailAction(logger).execute(actStatus, batchID,username,failReason);
		context.getLogger().debug("updateWriteOffFail->"+response.info().toString());

		switch(response.getStatusCode()){
			case CLOrderInfoResponse.STATUS_COMPLETE:{
				break;
			}
			case CLOrderInfoResponse.STATUS_DATA_NOT_FOUND:{
				break;
			}
			default:{
				throw new Exception("Error : " + response.getErrorMsg());
			}
		}
		
		return response;
	}
	protected class UpdateTreatmentReceiveAction extends DBTemplatesUpdate<ExecuteResponse, UtilityLogger, DBConnectionPools> {
		private int actStatus;
		private BigDecimal treatmentID;
		private String username;
		private String failReason;

		public UpdateTreatmentReceiveAction(UtilityLogger logger) {
			super(logger);
		}

		@Override
		protected ExecuteResponse createResponse() {
			return new ExecuteResponse();
		}

		@Override
		protected StringBuilder createSqlProcess() {
			StringBuilder sql = new StringBuilder();
			sql.append("UPDATE dbo.CL_TREATMENT ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append("SET LAST_UPD= getdate() , LAST_UPD_BY='").append(username).append("'").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(",ACTION_STATUS = ").append(actStatus).append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(", ACTION_STATUS_DTM  = getdate() ").append(ConstantsBatchReceiveResult.END_LINE);
			if(!ValidateUtil.isNull(failReason)){
				sql.append(", ACTION_REMARK   ='").append(failReason).append("'").append(ConstantsBatchReceiveResult.END_LINE);
			}
			sql.append(" WHERE ACTION_STATUS = 3 ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" AND TREATMENT_ID  = ").append(treatmentID).append(ConstantsBatchReceiveResult.END_LINE);

			return sql;
		}


		protected ExecuteResponse execute(int actStatus,  BigDecimal treatmentID,String username,String failReason) {
			this.actStatus = actStatus;
			this.treatmentID = treatmentID;
			this.username = username;
			this.failReason=failReason;
			return executeUpdate(ConstantsBatchReceiveResult.getDBConnectionPools(logger), true);
		}
	}

	public ExecuteResponse updateTreatmentReceive(int actStatus,  BigDecimal treatmentID,String username,String failReason,Context context) throws Exception {
		ExecuteResponse response= new UpdateTreatmentReceiveAction(logger).execute(actStatus, treatmentID,username,failReason);
		context.getLogger().debug("updateTreatmentReceive->"+response.info().toString());

		switch(response.getStatusCode()){
			case CLOrderInfoResponse.STATUS_COMPLETE:{
				break;
			}
			case CLOrderInfoResponse.STATUS_DATA_NOT_FOUND:{
				break;
			}
			default:{
				throw new Exception("Error : " + response.getErrorMsg());
			}
		}
		
		return response;
	}
	protected class UpdateTreatmentByBlacklistIDAction extends DBTemplatesUpdate<ExecuteResponse, UtilityLogger, DBConnectionPools> {
		private int actStatus;
		private BigDecimal blacklistID;
		private String username;

		public UpdateTreatmentByBlacklistIDAction(UtilityLogger logger) {
			super(logger);
		}

		@Override
		protected ExecuteResponse createResponse() {
			return new ExecuteResponse();
		}

		@Override
		protected StringBuilder createSqlProcess() {
			StringBuilder sql = new StringBuilder();
			sql.append("UPDATE dbo.CL_TREATMENT ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append("SET LAST_UPD= getdate() , LAST_UPD_BY='").append(username).append("'").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(",ACTION_STATUS = ").append(actStatus).append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(", ACTION_STATUS_DTM  = getdate() ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" FROM CL_TREATMENT T ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append("JOIN CL_BLACKLIST_TREATMENT BT  on T.TREATMENT_ID=BT.TREATMENT_ID AND BT.BLACKLIST_ID = (").append(blacklistID).append(") ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append("JOIN CL_BLACKLIST B on B.BLACKLIST_ID = BT.BLACKLIST_ID ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append("JOIN CL_ACTION A ON T.ACTION_ID = A.ACTION_ID AND A.ACTION_MODE IN (10,11) ").append(ConstantsBatchReceiveResult.END_LINE);

			return sql;
		}


		protected ExecuteResponse execute(int actStatus,  BigDecimal blacklistID,String username) {
			this.actStatus = actStatus;
			this.blacklistID = blacklistID;
			this.username = username;
			return executeUpdate(ConstantsBatchReceiveResult.getDBConnectionPools(logger), true);
		}
	}

	public ExecuteResponse updateBlackListResult(int actStatus,  BigDecimal blacklistID,String username,Context context) throws Exception {
		ExecuteResponse response= new UpdateTreatmentByBlacklistIDAction(logger).execute(actStatus, blacklistID,username);
		context.getLogger().debug("UpdateTreatmentByBlacklistIDAction->"+response.info().toString());

		switch(response.getStatusCode()){
			case CLOrderInfoResponse.STATUS_COMPLETE:{
				break;
			}
			case CLOrderInfoResponse.STATUS_DATA_NOT_FOUND:{
				break;
			}
			default:{
				throw new Exception("Error : " + response.getErrorMsg());
			}
		}
		
		return response;
	}
	protected class UpdateTreatmentByBatchIDAction extends DBTemplatesUpdate<ExecuteResponse, UtilityLogger, DBConnectionPools> {
		private int actStatus;
		private int actResultStatus;
		private BigDecimal batchID;
		private String username;
		private String actRemark;

		public UpdateTreatmentByBatchIDAction(UtilityLogger logger) {
			super(logger);
		}

		@Override
		protected ExecuteResponse createResponse() {
			return new ExecuteResponse();
		}

		@Override
		protected StringBuilder createSqlProcess() {
			StringBuilder sql = new StringBuilder();
			sql.append("UPDATE dbo.CL_TREATMENT ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append("SET LAST_UPD= getdate() , LAST_UPD_BY='").append(username).append("'").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(",ACTION_STATUS = ").append(actResultStatus).append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(", ACTION_STATUS_DTM  = getdate() ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(", ACTION_REMARK   ='").append(actRemark).append("'").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" FROM dbo.CL_TREATMENT  T ").append(ConstantsBatchReceiveResult.END_LINE);			
			sql.append(" JOIN CL_BLACKLIST_TREATMENT BT on T.TREATMENT_ID=BT.TREATMENT_ID ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" JOIN CL_BLACKLIST  B  on BT.BLACKLIST_ID=B.BLACKLIST_ID ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" JOIN CL_ACTION A ON T.ACTION_ID = A.ACTION_ID AND A.ACTION_MODE IN (10,11) ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" WHERE T.ACTION_STATUS   = ").append(actStatus).append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" AND B.BATCH_ID= ").append(batchID).append(ConstantsBatchReceiveResult.END_LINE);
			return sql;
		}


		protected ExecuteResponse execute(int actStatus, int actResultStatus, BigDecimal batchID,String actRemark,String username) {
			this.actStatus = actStatus;
			this.actResultStatus = actResultStatus;
			this.batchID = batchID;
			this.actRemark = actRemark;
			this.username = username;
			return executeUpdate(ConstantsBatchReceiveResult.getDBConnectionPools(logger), true);
		}
	}

	public ExecuteResponse updateBlackListByBatchIDResult(int actStatus, int actResultStatus, BigDecimal batchID,String actRemark,String username,Context context) throws Exception {
		ExecuteResponse response= new UpdateTreatmentByBatchIDAction(logger).execute(actStatus, actResultStatus,batchID,actRemark,username);
		context.getLogger().debug("updateBlackListByBatchIDResult->"+response.info().toString());

		switch(response.getStatusCode()){
			case CLOrderInfoResponse.STATUS_COMPLETE:{
				break;
			}
			case CLOrderInfoResponse.STATUS_DATA_NOT_FOUND:{
				break;
			}
			default:{
				throw new Exception("Error : " + response.getErrorMsg());
			}
		}
		
		return response;
	}
}
