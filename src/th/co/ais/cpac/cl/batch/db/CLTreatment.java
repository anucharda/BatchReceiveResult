package th.co.ais.cpac.cl.batch.db;

import java.math.BigDecimal;

import th.co.ais.cpac.cl.batch.Constants;
import th.co.ais.cpac.cl.batch.db.CLBatch.ExecuteResponse;
import th.co.ais.cpac.cl.batch.db.CLBatch.UpdateBatchReceiveAction;
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
	
	protected class UpdateTreatmentReceiveAction extends DBTemplatesUpdate<ExecuteResponse, UtilityLogger, DBConnectionPools> {
		private int actStatus;
		private BigDecimal treatmentID;
		private String username;

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
			sql.append("UPDATE dbo.CL_TREATMENT ").append(Constants.END_LINE);
			sql.append("SET LAST_UPD= getdate() , LAST_UPD_BY='").append(username).append("'").append(Constants.END_LINE);
			sql.append(",ACTION_STATUS = ").append(actStatus).append(Constants.END_LINE);
			sql.append(", ACTION_STATUS_DTM  = getdate() ").append(Constants.END_LINE);
			sql.append(" WHERE TREATMENT_ID  = ").append(treatmentID).append(Constants.END_LINE);
			return sql;
		}


		protected ExecuteResponse execute(int actStatus,  BigDecimal treatmentID,String username) {
			this.actStatus = actStatus;
			this.treatmentID = treatmentID;
			this.username = username;
			return executeUpdate(Constants.getDBConnectionPools(logger), true);
		}
	}

	public ExecuteResponse updateTreatmentReceive(int actStatus,  BigDecimal treatmentID,String username) {
		return new UpdateTreatmentReceiveAction(logger).execute(actStatus, treatmentID,username);
	}

}
