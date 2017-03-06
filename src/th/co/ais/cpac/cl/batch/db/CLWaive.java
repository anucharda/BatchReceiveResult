package th.co.ais.cpac.cl.batch.db;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import th.co.ais.cpac.cl.batch.ConstantsBatchReceiveResult;
import th.co.ais.cpac.cl.common.Context;
import th.co.ais.cpac.cl.common.UtilityLogger;
import th.co.ais.cpac.cl.template.database.DBConnectionPools;
import th.co.ais.cpac.cl.template.database.DBTemplatesExecuteQuery;
import th.co.ais.cpac.cl.template.database.DBTemplatesResponse;
import th.co.ais.cpac.cl.template.database.DBTemplatesUpdate;

public class CLWaive {
	protected final UtilityLogger logger;

	public CLWaive(UtilityLogger logger) {
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

	public class CLWaiveTreatementInfo {
		protected CLWaiveTreatementInfo() {
		}

		private BigDecimal treatementId;
		private BigDecimal waiveId;
		private BigDecimal batchId;
		private String baNumber;
		private int actStatus;

		public BigDecimal getTreatementId() {
			return treatementId;
		}

		public void setTreatementId(BigDecimal treatementId) {
			this.treatementId = treatementId;
		}

		public BigDecimal getWaiveId() {
			return waiveId;
		}

		public void setWaiveId(BigDecimal waiveId) {
			this.waiveId = waiveId;
		}

		public BigDecimal getBatchId() {
			return batchId;
		}

		public void setBatchId(BigDecimal batchId) {
			this.batchId = batchId;
		}

		public String getBaNumber() {
			return baNumber;
		}

		public void setBaNumber(String baNumber) {
			this.baNumber = baNumber;
		}

		public int getActStatus() {
			return actStatus;
		}

		public void setActStatus(int actStatus) {
			this.actStatus = actStatus;
		}
	}

	public class CLWaiveInfoResponse extends DBTemplatesResponse<ArrayList<CLWaiveTreatementInfo>> {

		@Override
		protected ArrayList<CLWaiveTreatementInfo> createResponse() {
			return new ArrayList<>();
		}

	}

	protected class GetWaiveTreatementInfoInvoiceIDAndAction
			extends DBTemplatesExecuteQuery<CLWaiveInfoResponse, UtilityLogger, DBConnectionPools> {
		private String baNo;
		private int actStatus;
		private BigDecimal batchID;
		private BigDecimal invoiceID;

		public GetWaiveTreatementInfoInvoiceIDAndAction(UtilityLogger logger) {
			super(logger);
		}

		@Override
		protected CLWaiveInfoResponse createResponse() {
			return new CLWaiveInfoResponse();
		}

		@Override
		protected StringBuilder createSqlProcess() {
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" SELECT A.WAIVE_ID, A.BA_NO, A.BATCH_ID, B.TREATMENT_ID ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" FROM dbo.CL_WAIVE A a ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" INNER JOIN dbo.CL_WAIVE_TREATMEMT b ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" ON A.WAIVE_ID = B.WAIVE_ID ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" WHERE A.BA_NO  = ('").append(baNo).append("') ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" and A.ACTION_STATUS = (").append(actStatus).append(")").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" and A.BATCH_ID = (").append(batchID).append(")").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" and A.INVOICE_ID = (").append(invoiceID).append(")").append(ConstantsBatchReceiveResult.END_LINE);
			return sql;
		}

		@Override
		protected void setReturnValue(ResultSet resultSet) throws SQLException {
			CLWaiveTreatementInfo temp = new CLWaiveTreatementInfo();
			temp.setBaNumber(resultSet.getString("BA_NO"));
			temp.setWaiveId(resultSet.getBigDecimal("WAIVE_ID"));
			temp.setBatchId(resultSet.getBigDecimal("BATCH_ID"));
			temp.setTreatementId(resultSet.getBigDecimal("TREATMENT_ID"));
			response.getResponse().add(temp);
		}

		protected CLWaiveInfoResponse execute(String baNo, BigDecimal batchID, BigDecimal invoiceID, int actStatus) {
			this.baNo = baNo;
			this.actStatus = actStatus;
			this.batchID = batchID;
			this.invoiceID = invoiceID;
			return executeQuery(ConstantsBatchReceiveResult.getDBConnectionPools(logger), true);
		}
	}

	public CLWaiveTreatementInfo getWaiveTreatementInfo(String baNo, BigDecimal batchID, BigDecimal invoiceID,
			int actStatus,Context context) throws Exception {
		CLWaiveTreatementInfo waiveInfo = null;
		CLWaiveInfoResponse response = new GetWaiveTreatementInfoInvoiceIDAndAction(logger).execute(baNo, batchID,
				invoiceID, actStatus);

		context.getLogger().debug("getWaiveTreatementInfo->"+response.info().toString());

		switch(response.getStatusCode()){
			case CLWaiveInfoResponse.STATUS_COMPLETE:{
				waiveInfo = response.getResponse().get(0);
				break;
			}
			case CLWaiveInfoResponse.STATUS_DATA_NOT_FOUND:{
				break;
			}
			default:{
				throw new Exception("Error : " + response.getErrorMsg());
			}
		}

		return waiveInfo;
	}

	protected class UpdateWaiveStatus extends DBTemplatesUpdate<ExecuteResponse, UtilityLogger, DBConnectionPools> {
		String baNo;
		BigDecimal invoiceID;
		BigDecimal batchID;
		BigDecimal batchAdjID;
		int actionStatus;
		String failReason;
		String updateBy;

		public UpdateWaiveStatus(UtilityLogger logger) {
			super(logger);
		}

		@Override
		protected StringBuilder createSqlProcess() {
			StringBuilder sql = new StringBuilder();
			sql.append("UPDATE dbo.CL_WAIVE ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append("SET LAST_UPD= getdate() , LAST_UPD_BY='").append(updateBy).append("'")
					.append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(",ACTION_STATUS = ").append(actionStatus).append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(", ACTION_STATUS_DTM = getdate() ").append(ConstantsBatchReceiveResult.END_LINE);
			if (failReason != null) {
				sql.append(", ACTION_REMARK = '").append(failReason).append("'").append(ConstantsBatchReceiveResult.END_LINE);
			}
			sql.append(",PM_BATCH_ADJ_DTL_ID = ").append(batchAdjID).append(ConstantsBatchReceiveResult.END_LINE);

			sql.append(" WHERE BA_NO   = '").append(baNo).append("'").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" AND BATCH_ID  = ").append(batchID).append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" AND INVOICE_ID   = ").append(invoiceID).append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" AND ACTION_STATUS  = ").append(ConstantsBatchReceiveResult.actInprogressStatus).append(ConstantsBatchReceiveResult.END_LINE);
			return sql;
		}

		@Override
		protected ExecuteResponse createResponse() {
			return new ExecuteResponse();
		}

		protected ExecuteResponse execute(String baNo, BigDecimal batchID, BigDecimal invoiceID, BigDecimal batchAdjID,
				int actionStatus, String failReason, String updateBy) {
			this.baNo = baNo;
			this.batchID = batchID;
			this.actionStatus = actionStatus;
			this.invoiceID = invoiceID;
			this.batchAdjID = batchAdjID;
			this.failReason = failReason;
			this.updateBy = updateBy;
			return executeUpdate(ConstantsBatchReceiveResult.getDBConnectionPools(logger), true);
		}
	}

	public ExecuteResponse updateWaiveStatus(String baNo, BigDecimal batchID, BigDecimal invoiceID,
			BigDecimal batchAdjID, int actionStatus, String failReason, String updateBy,Context context) throws Exception {
		
		ExecuteResponse response=new UpdateWaiveStatus(logger).execute(baNo, batchID, invoiceID, batchAdjID, actionStatus, failReason,
				updateBy);
		context.getLogger().debug("UpdateWaiveStatus->"+response.info().toString());

		switch(response.getStatusCode()){
			case CLWaiveInfoResponse.STATUS_COMPLETE:{
				break;
			}
			case CLWaiveInfoResponse.STATUS_DATA_NOT_FOUND:{
				break;
			}
			default:{
				throw new Exception("Error : " + response.getErrorMsg());
			}
		}
		return  response;
	}

	protected class GetWaiveTreatementInfoByTreatment
			extends DBTemplatesExecuteQuery<CLWaiveInfoResponse, UtilityLogger, DBConnectionPools> {

		private BigDecimal treatmentID;

		public GetWaiveTreatementInfoByTreatment(UtilityLogger logger) {
			super(logger);
		}

		@Override
		protected CLWaiveInfoResponse createResponse() {
			return new CLWaiveInfoResponse();
		}

		@Override
		protected StringBuilder createSqlProcess() {
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" TREATMENT_ID,BA_NO,BATCH_ID,ACTION_STATUS ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" FROM CL_WAIVE_TREATMEMT a ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" INNER JOIN dbo.CL_WAIVE  b ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" ON a.WAIVE_ID =b.WAIVE_ID  ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" WHERE TREATMENT_ID = (").append(treatmentID).append(")").append(ConstantsBatchReceiveResult.END_LINE);
			return sql;
		}

		@Override
		protected void setReturnValue(ResultSet resultSet) throws SQLException {
			CLWaiveTreatementInfo temp = new CLWaiveTreatementInfo();
			temp.setTreatementId(resultSet.getBigDecimal("TREATMENT_ID"));
			temp.setBaNumber(resultSet.getString("BA_NO"));
			temp.setBatchId(resultSet.getBigDecimal("BATCH_ID"));
			temp.setActStatus(resultSet.getInt("ACTION_STATUS"));
			response.getResponse().add(temp);
		}

		protected CLWaiveInfoResponse execute(BigDecimal treatmentID) {
			this.treatmentID = treatmentID;
			return executeQuery(ConstantsBatchReceiveResult.getDBConnectionPools(logger), true);
		}
	}

	public CLWaiveInfoResponse getWaiveTreatementInfoByTreatmentID(BigDecimal treatmentID,Context context) throws Exception {
		CLWaiveInfoResponse waiveList = null;
		waiveList = new GetWaiveTreatementInfoByTreatment(logger).execute(treatmentID);
		
		context.getLogger().debug("UpdateWaiveStatus->"+waiveList.info().toString());

		switch(waiveList.getStatusCode()){
			case CLWaiveInfoResponse.STATUS_COMPLETE:{
				break;
			}
			case CLWaiveInfoResponse.STATUS_DATA_NOT_FOUND:{
				break;
			}
			default:{
				throw new Exception("Error : " + waiveList.getErrorMsg());
			}
		}
		
		return waiveList;
	}

}
