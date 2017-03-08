package th.co.ais.cpac.cl.batch.db;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import th.co.ais.cpac.cl.batch.ConstantsBatchReceiveResult;
import th.co.ais.cpac.cl.batch.db.CLOrder.CLOrderInfoResponse;
import th.co.ais.cpac.cl.batch.db.CLOrder.ExecuteResponse;
import th.co.ais.cpac.cl.batch.db.CLOrder.UpdateOrderStatus;
import th.co.ais.cpac.cl.common.Context;
import th.co.ais.cpac.cl.common.UtilityLogger;
import th.co.ais.cpac.cl.template.database.DBConnectionPools;
import th.co.ais.cpac.cl.template.database.DBTemplatesExecuteQuery;
import th.co.ais.cpac.cl.template.database.DBTemplatesResponse;
import th.co.ais.cpac.cl.template.database.DBTemplatesUpdate;

/**
 *
 * @author Sirirat
 */
public class CLBlacklist {

	protected final UtilityLogger logger;

	public CLBlacklist(UtilityLogger logger) {
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

	public class CLBlacklistInfo {
		protected CLBlacklistInfo() {
		}

		private BigDecimal blacklistID;
		private String rowID;

		public BigDecimal getBlacklistID() {
			return blacklistID;
		}

		public void setBlacklistID(BigDecimal blacklistID) {
			this.blacklistID = blacklistID;
		}

		public String getRowID() {
			return rowID;
		}

		public void setRowID(String rowID) {
			this.rowID = rowID;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("CLBlacklistInfo [blacklistID=");
			builder.append(blacklistID);
			builder.append(", rowID=");
			builder.append(rowID);
			builder.append("]");
			return builder.toString();
		}

	}

	public class CLBlacklistInfoResponse extends DBTemplatesResponse<ArrayList<CLBlacklistInfo>> {

		@Override
		protected ArrayList<CLBlacklistInfo> createResponse() {
			return new ArrayList<>();
		}

	}

	protected class GetBlacklistAction
			extends DBTemplatesExecuteQuery<CLBlacklistInfoResponse, UtilityLogger, DBConnectionPools> {

		private int actStatus;
		private BigDecimal batchID;
		private String baNo;
		private String blacklistDtm;
		private String blacklistType;
		private String blacklistSubType;
		private String blacklistSource;

		public GetBlacklistAction(UtilityLogger logger) {
			super(logger);
		}

		@Override
		protected CLBlacklistInfoResponse createResponse() {
			return new CLBlacklistInfoResponse();
		}

		@Override
		protected StringBuilder createSqlProcess() {
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" C.BLACKLIST_ID,ROW_ID ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" FROM CL_BLACKLIST C, CPDB..SFF_BLACKLIST S ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" WHERE C.BLACKLIST_OPTION = 1 ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" AND C.BLACKLIST_TYPE = S.BLACKLIST_TYPE ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" AND C.BLACKLIST_SUBTYPE = S.BLACKLIST_SUBTYPE ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" AND C.BLACKLIST_SOURCE = S.BLACKLIST_SOURCE ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" AND C.BLACKLIST_REQUEST_DATE = S.BLACKLIST_START_DT ")
					.append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" AND ACTION_STATUS = (").append(actStatus).append(")")
					.append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" AND BATCH_ID = (").append(batchID).append(")").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" AND S.BILLING_ACCNT_ID = (SELECT B.SFF_ACCOUNT_ID FROM CL_BA_INFO B WHERE B.BA_NO = '")
					.append(baNo).append("') ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" AND C.BA_NO = '").append(baNo).append("' ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(
					" AND convert(Varchar(10),C.BLACKLIST_REQUEST_DATE,112)+ str_replace(convert(Varchar(15),BLACKLIST_REQUEST_DATE,108),':',null) = '")
					.append(blacklistDtm).append("' ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" AND C.BLACKLIST_TYPE = '").append(blacklistType).append("' ")
					.append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" AND C.BLACKLIST_SUBTYPE = '").append(blacklistSubType).append("' ")
					.append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" AND C.BLACKLIST_SOURCE = '").append(blacklistSource).append("' ")
					.append(ConstantsBatchReceiveResult.END_LINE);

			return sql;
		}

		@Override
		protected void setReturnValue(ResultSet resultSet) throws SQLException {
			CLBlacklistInfo temp = new CLBlacklistInfo();
			temp.setBlacklistID(resultSet.getBigDecimal("BLACKLIST_ID"));
			temp.setRowID(resultSet.getString("ROW_ID"));
			response.getResponse().add(temp);
		}

		protected CLBlacklistInfoResponse execute(int actStatus, BigDecimal batchID, String baNo, String blacklistDtm,
				String blacklistType, String blacklistSubType, String blacklistSource) {
			this.actStatus = actStatus;
			this.batchID = batchID;
			this.baNo = baNo;
			this.blacklistDtm = blacklistDtm;
			this.blacklistType = blacklistType;
			this.blacklistSubType = blacklistSubType;
			this.blacklistSource = blacklistSource;
			return executeQuery(ConstantsBatchReceiveResult.getDBConnectionPools(logger), true);
		}
	}

	public CLBlacklistInfo getBlacklistInfo(int actStatus, BigDecimal batchID, String baNo, String blacklistDtm,
			String blacklistType, String blacklistSubType, String blacklistSource, Context context) throws Exception {
		CLBlacklistInfo orderInfo = null;
		CLBlacklistInfoResponse response = new GetBlacklistAction(logger).execute(actStatus, batchID, baNo,
				blacklistDtm, blacklistType, blacklistSubType, blacklistSource);
		context.getLogger().debug("getBlacklistInfo->" + response.info().toString());

		switch (response.getStatusCode()) {
		case CLBlacklistInfoResponse.STATUS_COMPLETE: {
			orderInfo = response.getResponse().get(0);
			break;
		}
		case CLBlacklistInfoResponse.STATUS_DATA_NOT_FOUND: {
			break;
		}
		default: {
			throw new Exception("Error : " + response.getErrorMsg());
		}
		}
		return orderInfo;
	}

	protected class GetDeBlacklistAction
			extends DBTemplatesExecuteQuery<CLBlacklistInfoResponse, UtilityLogger, DBConnectionPools> {

		private int actStatus;
		private BigDecimal batchID;
		private String baNo;
		private String blacklistEndDtm;
		private String blacklistType;
		private String blacklistSubType;
		private String blacklistSource;
		private String reason;

		public GetDeBlacklistAction(UtilityLogger logger) {
			super(logger);
		}

		@Override
		protected CLBlacklistInfoResponse createResponse() {
			return new CLBlacklistInfoResponse();
		}

		@Override
		protected StringBuilder createSqlProcess() {
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" C.BLACKLIST_ID,ROW_ID ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" FROM CL_BLACKLIST C, CPDB..SFF_BLACKLIST S ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" WHERE C.BLACKLIST_OPTION = 2 ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" AND C.BLACKLIST_TYPE = S.BLACKLIST_TYPE ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" AND C.BLACKLIST_SUBTYPE = S.BLACKLIST_SUBTYPE ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" AND C.BLACKLIST_SOURCE = S.BLACKLIST_SOURCE ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" AND C.BLACKLIST_REQUEST_DATE = S.BLACKLIST_END_DT ")
					.append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" AND ACTION_STATUS = (").append(actStatus).append(")")
					.append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" AND BATCH_ID = (").append(batchID).append(")").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" AND S.BILLING_ACCNT_ID = (SELECT B.SFF_ACCOUNT_ID FROM CL_BA_INFO B WHERE B.BA_NO = '")
					.append(baNo).append("') ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" AND C.BA_NO = '").append(baNo).append("' ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(
					" AND convert(Varchar(10),C.BLACKLIST_REQUEST_DATE,112)+ str_replace(convert(Varchar(15),BLACKLIST_REQUEST_DATE,108),':',null) = '")
					.append(blacklistEndDtm).append("' ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" AND C.BLACKLIST_TYPE = '").append(blacklistType).append("' ")
					.append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" AND C.BLACKLIST_SUBTYPE = '").append(blacklistSubType).append("' ")
					.append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" AND C.BLACKLIST_SOURCE = '").append(blacklistSource).append("' ")
					.append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" AND C.BLACKLIST_REASON = '").append(reason).append("' ")
			.append(ConstantsBatchReceiveResult.END_LINE);
			return sql;
		}

		@Override
		protected void setReturnValue(ResultSet resultSet) throws SQLException {
			CLBlacklistInfo temp = new CLBlacklistInfo();
			temp.setBlacklistID(resultSet.getBigDecimal("BLACKLIST_ID"));
			temp.setRowID(resultSet.getString("ROW_ID"));
			response.getResponse().add(temp);
		}

		protected CLBlacklistInfoResponse execute(int actStatus, BigDecimal batchID, String baNo, String blacklistEndDtm,
				String blacklistType, String blacklistSubType, String blacklistSource,String reason) {
			this.actStatus = actStatus;
			this.batchID = batchID;
			this.baNo = baNo;
			this.blacklistEndDtm = blacklistEndDtm;
			this.blacklistType = blacklistType;
			this.blacklistSubType = blacklistSubType;
			this.blacklistSource = blacklistSource;
			this.reason=reason;
			return executeQuery(ConstantsBatchReceiveResult.getDBConnectionPools(logger), true);
		}
	}

	public CLBlacklistInfo getDeBlacklistInfo(int actStatus, BigDecimal batchID, String baNo, String blacklistEndDtm,
			String blacklistType, String blacklistSubType, String blacklistSource,String reason, Context context) throws Exception {
		CLBlacklistInfo orderInfo = null;
		CLBlacklistInfoResponse response = new GetDeBlacklistAction(logger).execute(actStatus, batchID, baNo,
				blacklistEndDtm, blacklistType, blacklistSubType, blacklistSource,reason);
		context.getLogger().debug("getBlacklistInfo->" + response.info().toString());

		switch (response.getStatusCode()) {
		case CLBlacklistInfoResponse.STATUS_COMPLETE: {
			orderInfo = response.getResponse().get(0);
			break;
		}
		case CLBlacklistInfoResponse.STATUS_DATA_NOT_FOUND: {
			break;
		}
		default: {
			throw new Exception("Error : " + response.getErrorMsg());
		}
		}
		return orderInfo;
	}
	protected class UpdateBlacklistStatus extends DBTemplatesUpdate<ExecuteResponse, UtilityLogger, DBConnectionPools> {
		int actionStatus;
		String sffRowID;
		String updateBy;
		BigDecimal blacklistId;

		public UpdateBlacklistStatus(UtilityLogger logger) {
			super(logger);
		}

		@Override
		protected StringBuilder createSqlProcess() {
			StringBuilder sql = new StringBuilder();
			sql.append("UPDATE dbo.CL_BLACKLIST ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append("SET LAST_UPD= getdate() , LAST_UPD_BY='").append(updateBy).append("'")
					.append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(",ACTION_STATUS = ").append(actionStatus).append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(", ACTION_STATUS_DTM = getdate() ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(",SFF_BLACKLIST_ID  = '").append(sffRowID).append("'").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" WHERE BLACKLIST_ID  = ").append(blacklistId).append(ConstantsBatchReceiveResult.END_LINE);
			return sql;
		}

		@Override
		protected ExecuteResponse createResponse() {
			return new ExecuteResponse();
		}

		protected ExecuteResponse execute(int actionStatus, String sffRowID, BigDecimal blacklistId,String updateBy) {
			this.actionStatus = actionStatus;
			this.sffRowID=sffRowID;
			this.actionStatus = actionStatus;
			this.blacklistId = blacklistId;
			this.updateBy = updateBy;
			return executeUpdate(ConstantsBatchReceiveResult.getDBConnectionPools(logger), true); // case
																				// adjust
																				// false;
		}
	}

	public ExecuteResponse updateBlacklistStatus(int actionStatus, String sffRowID, BigDecimal blacklistId,String updateBy,Context context) throws Exception {
		ExecuteResponse response=new UpdateBlacklistStatus(logger).execute(actionStatus,sffRowID,  blacklistId,  updateBy);
		context.getLogger().debug("updateBlacklistStatus->"+response.info().toString());

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
	protected class UpdateBlacklistStatusByBatchID extends DBTemplatesUpdate<ExecuteResponse, UtilityLogger, DBConnectionPools> {
		int actionStatus;
		int resultStatus;
		BigDecimal batchID;
		String updateBy;
		String actRemark;

		public UpdateBlacklistStatusByBatchID(UtilityLogger logger) {
			super(logger);
		}

		@Override
		protected StringBuilder createSqlProcess() {
			StringBuilder sql = new StringBuilder();
			sql.append("UPDATE dbo.CL_BLACKLIST ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append("SET LAST_UPD= getdate() , LAST_UPD_BY='").append(updateBy).append("'")
					.append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(",ACTION_STATUS = ").append(resultStatus).append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(", ACTION_STATUS_DTM = getdate() ").append(ConstantsBatchReceiveResult.END_LINE);		
			sql.append(",ACTION_REMARK = '").append(actRemark).append("'").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" WHERE ACTION_STATUS   = ").append(actionStatus).append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" AND BATCH_ID= ").append(batchID).append(ConstantsBatchReceiveResult.END_LINE);
			return sql;
		}

		@Override
		protected ExecuteResponse createResponse() {
			return new ExecuteResponse();
		}

		protected ExecuteResponse execute(int actionStatus, int resultStatus, BigDecimal batchID,String actRemark,String updateBy) {
			this.actionStatus = actionStatus;
			this.resultStatus=resultStatus;
			this.batchID = batchID;
			this.updateBy = updateBy;
			this.actRemark = actRemark;
			return executeUpdate(ConstantsBatchReceiveResult.getDBConnectionPools(logger), true); // case
																				// adjust
																				// false;
		}
	}

	public ExecuteResponse updateBlacklistByBatchIDStatus(int actionStatus, int resultStatus, BigDecimal batchID,String actRemark,String updateBy,Context context) throws Exception {
		ExecuteResponse response=new UpdateBlacklistStatusByBatchID(logger).execute(actionStatus,resultStatus,  batchID,actRemark,  updateBy);
		context.getLogger().debug("updateBlacklistByBatchIDStatus->"+response.info().toString());

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
