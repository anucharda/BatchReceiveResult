package th.co.ais.cpac.cl.batch.db;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import th.co.ais.cpac.cl.batch.Constants;
import th.co.ais.cpac.cl.batch.db.CLOrder.ExecuteResponse;
import th.co.ais.cpac.cl.common.UtilityLogger;
import th.co.ais.cpac.cl.template.database.DBConnectionPools;
import th.co.ais.cpac.cl.template.database.DBTemplatesExecuteQuery;
import th.co.ais.cpac.cl.template.database.DBTemplatesResponse;
import th.co.ais.cpac.cl.template.database.DBTemplatesUpdate;

/**
 *
 * @author Sirirat
 */
public class CLBatch {

	protected final UtilityLogger logger;

	public CLBatch(UtilityLogger logger) {
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

	public class CLBatchInfo {
		protected CLBatchInfo() {
		}

		private BigDecimal batchId;

		public BigDecimal getBatchId() {
			return batchId;
		}

		public void setBatchId(BigDecimal batchId) {
			this.batchId = batchId;
		}

	}

	public class CLBatchInfoResponse extends DBTemplatesResponse<ArrayList<CLBatchInfo>> {

		@Override
		protected ArrayList<CLBatchInfo> createResponse() {
			return new ArrayList<>();
		}

	}

	protected class FindBatchIDAction
			extends DBTemplatesExecuteQuery<CLBatchInfoResponse, UtilityLogger, DBConnectionPools> {
		private int inboundStatus;
		private String fileName;

		public FindBatchIDAction(UtilityLogger logger) {
			super(logger);
		}

		@Override
		protected CLBatchInfoResponse createResponse() {
			return new CLBatchInfoResponse();
		}

		@Override
		protected StringBuilder createSqlProcess() {
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT").append(Constants.END_LINE);
			sql.append(" BATCH_ID ").append(Constants.END_LINE);
			sql.append(" CL_BATCH ").append(Constants.END_LINE);
			sql.append(" WHERE BATCH_FILE_NAME = ('").append(fileName).append("') ").append(Constants.END_LINE);
			sql.append(" and INBOUND_STATUS = (").append(inboundStatus).append(")").append(Constants.END_LINE);
			return sql;
		}

		@Override
		protected void setReturnValue(ResultSet resultSet) throws SQLException {
			CLBatchInfo temp = new CLBatchInfo();
			temp.setBatchId(resultSet.getBigDecimal("BATCH_ID"));
			response.getResponse().add(temp);
		}

		protected CLBatchInfoResponse execute(int inboundStatus, String fileName) {
			this.inboundStatus = inboundStatus;
			this.fileName = fileName;
			return executeQuery(Constants.getDBConnectionPools(logger), true);
		}
	}

	public CLBatchInfo getBatchInfoByFileName(int inboundStatus, String fileName) {
		CLBatchInfo batchInfo = null;
		CLBatchInfoResponse response = new FindBatchIDAction(logger).execute(inboundStatus, fileName);
		if (response.getResponse() != null && response.getResponse().size() > 0) {
			batchInfo = response.getResponse().get(0);
		}
		return batchInfo;
	}

	protected class UpdateBatchReceiveAction extends DBTemplatesUpdate<ExecuteResponse, UtilityLogger, DBConnectionPools> {
		private int inboundStatus;
		private BigDecimal batchID;
		private String fileName;
		private String username;

		public UpdateBatchReceiveAction(UtilityLogger logger) {
			super(logger);
		}

		@Override
		protected ExecuteResponse createResponse() {
			return new ExecuteResponse();
		}

		@Override
		protected StringBuilder createSqlProcess() {
			StringBuilder sql = new StringBuilder();
			sql.append("UPDATE dbo.CL_BATCH ").append(Constants.END_LINE);
			sql.append("SET LAST_UPD= getdate() , LAST_UPD_BY='").append(username).append("'").append(Constants.END_LINE);
			sql.append(",RESPONSE_FILE_NAME = '").append(fileName).append("'").append(Constants.END_LINE);
			sql.append(",INBOUND_STATUS = ").append(inboundStatus).append(Constants.END_LINE);
			sql.append(", INBOUND_STATUS_DTM = getdate() ").append(Constants.END_LINE);
			sql.append(" WHERE BATCH_ID = ").append(batchID).append(Constants.END_LINE);
			return sql;
		}


		protected ExecuteResponse execute(int inboundStatus,  BigDecimal batchID,String fileName,String username) {
			this.inboundStatus = inboundStatus;
			this.batchID = batchID;
			this.fileName = fileName;
			this.username = username;
			return executeUpdate(Constants.getDBConnectionPools(logger), true);
		}
	}

	public ExecuteResponse updateInboundReceiveStatus(int inboundStatus,  BigDecimal batchID,String fileName,String username) {
		return new UpdateBatchReceiveAction(logger).execute(inboundStatus, batchID,fileName,username);
	}
	protected class UpdateBatchCompleteAction extends DBTemplatesUpdate<ExecuteResponse, UtilityLogger, DBConnectionPools> {
		private BigDecimal batchID;
		private String username;
		
		public UpdateBatchCompleteAction(UtilityLogger logger) {
			super(logger);
		}

		@Override
		protected ExecuteResponse createResponse() {
			return new ExecuteResponse();
		}

		@Override
		protected StringBuilder createSqlProcess() {
			StringBuilder sql = new StringBuilder();
			sql.append("UPDATE dbo.CL_BATCH ").append(Constants.END_LINE);
			sql.append("SET LAST_UPD= getdate() , LAST_UPD_BY='").append(username).append("'").append(Constants.END_LINE);
			sql.append(",INBOUND_STATUS = ").append(Constants.batchCompleteStatus).append(Constants.END_LINE);
			sql.append(", INBOUND_STATUS_DTM = getdate() ").append(Constants.END_LINE);
			sql.append(" WHERE BATCH_ID = ").append(batchID).append(Constants.END_LINE);
			return sql;
		}


		protected ExecuteResponse execute(BigDecimal batchID,String username) {
			this.batchID = batchID;
			this.username = username;
			return executeUpdate(Constants.getDBConnectionPools(logger), true);
		}
	}

	public ExecuteResponse updateInboundCompleteStatus(BigDecimal batchID,String username) {
		return new UpdateBatchCompleteAction(logger).execute(batchID,username);
	}
}
