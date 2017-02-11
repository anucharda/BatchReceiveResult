package th.co.ais.cpac.cl.batch.db;

import java.math.BigDecimal;
import java.util.Date;

import th.co.ais.cpac.cl.batch.Constants;
import th.co.ais.cpac.cl.common.UtilityLogger;
import th.co.ais.cpac.cl.template.database.DBConnectionPools;
import th.co.ais.cpac.cl.template.database.DBTemplatesResponse;
import th.co.ais.cpac.cl.template.database.DBTemplatesUpdate;

public class CLBaInfo {
	protected final UtilityLogger logger;

	public CLBaInfo(UtilityLogger logger) {
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
	
	protected class UpdateBaInfoAction extends DBTemplatesUpdate<ExecuteResponse, UtilityLogger, DBConnectionPools> {
		private String baNo;
		private Date writeOffDtm;
		private BigDecimal writeOffTypeID;
		private String username;

		public UpdateBaInfoAction(UtilityLogger logger) {
			super(logger);
		}

		@Override
		protected ExecuteResponse createResponse() {
			return new ExecuteResponse();
		}

		@Override
		protected StringBuilder createSqlProcess() {
			StringBuilder sql = new StringBuilder();
			sql.append("UPDATE dbo.CL_BA_INFO B ").append(Constants.END_LINE);
			sql.append("SET LAST_UPD= getdate() , LAST_UPD_BY='").append(username).append("'").append(Constants.END_LINE);
			sql.append(",WRITEOFF_BOO  = 'Y'").append(Constants.END_LINE);
			sql.append(",WRITEOFF_DATE  = ").append(writeOffDtm).append(Constants.END_LINE);
			sql.append(",WRITEOFF_TYPE  = (SELECT A.WRITEOFF_TYPE_CODE FROM CL_WRITEOFF_TYPE A WHERE A.WRITEOFF_TYPE_ID = ").append(writeOffTypeID).append(")").append(Constants.END_LINE);
			sql.append(" WHERE BA_NO  = '").append(baNo).append("'").append(Constants.END_LINE);
			return sql;
		}


		protected ExecuteResponse execute(String baNo,Date writeOffDtm,  BigDecimal writeOffTypeID,String username) {
			this.baNo = baNo;
			this.writeOffDtm = writeOffDtm;
			this.username = username;
			this.writeOffTypeID=writeOffTypeID;
			return executeUpdate(Constants.getDBConnectionPools(logger), true);
		}
	}

	public ExecuteResponse updateTreatmentReceive(String baNo,Date writeOffDtm,  BigDecimal writeOffTypeID,String username) {
		return new UpdateBaInfoAction(logger).execute(baNo, writeOffDtm,writeOffTypeID,username);
	}

}
