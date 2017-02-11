package th.co.ais.cpac.cl.batch.db;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import th.co.ais.cpac.cl.batch.Constants;
import th.co.ais.cpac.cl.batch.db.CLOrder.CLOrderInfoResponse;
import th.co.ais.cpac.cl.batch.db.CLOrder.CLOrderTreatementInfo;
import th.co.ais.cpac.cl.batch.db.CLOrder.GetOrderTreatementInfoByMobileAndAction;
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
		private Date writeOffDtm;
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

		public Date getWriteOffDtm() {
			return writeOffDtm;
		}

		public void setWriteOffDtm(Date writeOffDtm) {
			this.writeOffDtm = writeOffDtm;
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
			sql.append(" SELECT").append(Constants.END_LINE);
			sql.append(" WT.TREATMENT_ID, W.BA_NO, W.WRITEOFF_DATE, W.WRITEOFF_TYPE_ID ").append(Constants.END_LINE);
			sql.append(" FROM dbo.CL_WRITEOFF W ").append(Constants.END_LINE);
			sql.append(" INNER JOIN dbo.CL_WRITEOFF_TREATMENT WT ").append(Constants.END_LINE);
			sql.append(" ON W.WRITEOFF_ID = WT.WRITEOFF_ID ").append(Constants.END_LINE);
			sql.append(" WHERE W.BATCH_ID  = (").append(batchID).append(")").append(Constants.END_LINE);
			return sql;
		}

		@Override
		protected void setReturnValue(ResultSet resultSet) throws SQLException {
			CLWriteOffTreatementInfo temp = new CLWriteOffTreatementInfo();			
			temp.setTreatementId(resultSet.getBigDecimal("TREATMENT_ID"));
			temp.setBaNo(resultSet.getString("BA_NO"));
			temp.setWriteOffDtm(resultSet.getTimestamp("WRITEOFF_DATE"));
			temp.setWriteOffTypeId(resultSet.getBigDecimal("WRITEOFF_TYPE_ID"));
			response.getResponse().add(temp);
		}

		protected CLWriteOffInfoResponse execute(BigDecimal batchID) {
			this.batchID = batchID;
			return executeQuery(Constants.getDBConnectionPools(logger), true);
		}
	}

	public CLWriteOffInfoResponse getOrderTreatementInfo(BigDecimal batchID) {
		CLWriteOffInfoResponse response = new GetWriteOffTreatementInfoByBatch(logger).execute(batchID);
		return response;
	}

}
