package th.co.ais.cpac.cl.batch.db;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import th.co.ais.cpac.cl.batch.ConstantsBatchReceiveResult;
import th.co.ais.cpac.cl.common.Context;
import th.co.ais.cpac.cl.common.UtilityLogger;
import th.co.ais.cpac.cl.template.database.DBConnectionPools;
import th.co.ais.cpac.cl.template.database.DBTemplatesExecuteQuery;
import th.co.ais.cpac.cl.template.database.DBTemplatesResponse;

public class CLWriteOff {
	protected final UtilityLogger logger;

	public CLWriteOff(UtilityLogger logger) {
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

	public class CLWriteOffTreatementInfo {
		protected CLWriteOffTreatementInfo() {
		}

		private BigDecimal treatementId;
		private String baNo;
		private BigDecimal writeOffTypeId;

		public BigDecimal getTreatementId() {
			return treatementId;
		}

		public void setTreatementId(BigDecimal treatementId) {
			this.treatementId = treatementId;
		}

		public String getBaNo() {
			return baNo;
		}

		public void setBaNo(String baNo) {
			this.baNo = baNo;
		}

		public BigDecimal getWriteOffTypeId() {
			return writeOffTypeId;
		}

		public void setWriteOffTypeId(BigDecimal writeOffTypeId) {
			this.writeOffTypeId = writeOffTypeId;
		}
	}

	public class CLWriteOffInfoResponse extends DBTemplatesResponse<ArrayList<CLWriteOffTreatementInfo>> {

		@Override
		protected ArrayList<CLWriteOffTreatementInfo> createResponse() {
			return new ArrayList<>();
		}

	}

	protected class GetWriteOffTreatementInfoByBatch
			extends DBTemplatesExecuteQuery<CLWriteOffInfoResponse, UtilityLogger, DBConnectionPools> {

		private BigDecimal batchID;

		public GetWriteOffTreatementInfoByBatch(UtilityLogger logger) {
			super(logger);
		}

		@Override
		protected CLWriteOffInfoResponse createResponse() {
			return new CLWriteOffInfoResponse();
		}

		@Override
		protected StringBuilder createSqlProcess() {
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" WT.TREATMENT_ID, W.BA_NO,  W.WRITEOFF_TYPE_ID ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" FROM dbo.CL_WRITEOFF W ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" INNER JOIN dbo.CL_WRITEOFF_TREATMENT WT ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" ON W.WRITEOFF_ID = WT.WRITEOFF_ID ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" WHERE W.BATCH_ID  = (").append(batchID).append(")").append(ConstantsBatchReceiveResult.END_LINE);
			return sql;
		}

		@Override
		protected void setReturnValue(ResultSet resultSet) throws SQLException {
		      if (resultSet != null) {
		  			CLWriteOffTreatementInfo temp = new CLWriteOffTreatementInfo();			
					temp.setTreatementId(resultSet.getBigDecimal("TREATMENT_ID"));
					temp.setBaNo(resultSet.getString("BA_NO"));
					temp.setWriteOffTypeId(resultSet.getBigDecimal("WRITEOFF_TYPE_ID"));
					response.getResponse().add(temp);
		      }
		}

		protected CLWriteOffInfoResponse execute(BigDecimal batchID) {
			this.batchID = batchID;
			return executeQuery(ConstantsBatchReceiveResult.getDBConnectionPools(logger), true);
		}
	}

	public CLWriteOffInfoResponse getOrderTreatementInfo(BigDecimal batchID,Context context) throws Exception {
		CLWriteOffInfoResponse response = new GetWriteOffTreatementInfoByBatch(logger).execute(batchID);	
		context.getLogger().debug("getOrderTreatementInfo->"+response.info().toString());

		switch(response.getStatusCode()){
			case CLWriteOffInfoResponse.STATUS_COMPLETE:{
				break;
			}
			case CLWriteOffInfoResponse.STATUS_DATA_NOT_FOUND:{
				break;
			}
			default:{
				throw new Exception("Error : " + response.getErrorMsg());
			}
		}
		return response;
	}

}
