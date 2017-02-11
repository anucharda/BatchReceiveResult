package th.co.ais.cpac.cl.batch.db;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import th.co.ais.cpac.cl.batch.Constants;
import th.co.ais.cpac.cl.common.UtilityLogger;
import th.co.ais.cpac.cl.template.database.DBConnectionPools;
import th.co.ais.cpac.cl.template.database.DBTemplatesExecuteQuery;
import th.co.ais.cpac.cl.template.database.DBTemplatesResponse;

public class PMBatchAdjDtl {
	protected final UtilityLogger logger;

	public PMBatchAdjDtl(UtilityLogger logger) {
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

	public class PMBatchAdjDtlInfo {
		protected PMBatchAdjDtlInfo() {
		}

		private BigDecimal invoiceID;
		private BigDecimal batchDtlID;
		public BigDecimal getInvoiceID() {
			return invoiceID;
		}

		public void setInvoiceID(BigDecimal invoiceID) {
			this.invoiceID = invoiceID;
		}

		public BigDecimal getBatchDtlID() {
			return batchDtlID;
		}

		public void setBatchDtlID(BigDecimal batchDtlID) {
			this.batchDtlID = batchDtlID;
		}

	}

	public class PMBatchAdjInfoResponse extends DBTemplatesResponse<ArrayList<PMBatchAdjDtlInfo>> {

		@Override
		protected ArrayList<PMBatchAdjDtlInfo> createResponse() {
			return new ArrayList<>();
		}
	}

	protected class FindPMBatchAdjDtlAction
			extends DBTemplatesExecuteQuery<PMBatchAdjInfoResponse, UtilityLogger, DBConnectionPools> {
		private BigDecimal invoiceID;
		private BigDecimal amount;
		private String adjStatus;
		public FindPMBatchAdjDtlAction(UtilityLogger logger) {
			super(logger);
		}

		@Override
		protected PMBatchAdjInfoResponse createResponse() {
			return new PMBatchAdjInfoResponse();
		}

		@Override
		protected StringBuilder createSqlProcess() {
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT").append(Constants.END_LINE);
			sql.append(" BATCH_DTL_ID ").append(Constants.END_LINE);
			sql.append(" PM_BATCH_ADJ_DTL ").append(Constants.END_LINE);
			sql.append(" WHERE INVOICE_ID = ").append(invoiceID).append(Constants.END_LINE);
			sql.append(" AND REQ_NON_VAT_AMT + REQ_NET_VAT_AMT + REQ_VAT_AMT = ").append(amount).append(Constants.END_LINE);
			sql.append(" AND AND ADJ_STATUS  = ").append(adjStatus).append(Constants.END_LINE);
			return sql;
		}

		@Override
		protected void setReturnValue(ResultSet resultSet) throws SQLException {
			PMBatchAdjDtlInfo temp = new PMBatchAdjDtlInfo();
			temp.setBatchDtlID(resultSet.getBigDecimal("BATCH_DTL_ID"));
			response.getResponse().add(temp);
		}

		protected PMBatchAdjInfoResponse execute(BigDecimal invoiceID,BigDecimal amount,String adjStatus) {
			this.invoiceID = invoiceID;
			this.amount=amount;
			this.adjStatus=adjStatus;
			return executeQuery(Constants.getDBConnectionPools(logger), true);
		}
	}

	public PMBatchAdjInfoResponse getBatchDtlIDByInvoiceID(BigDecimal invoiceID,BigDecimal amount,String adjStatus) {
		PMBatchAdjInfoResponse response = new FindPMBatchAdjDtlAction(logger).execute(invoiceID,amount,adjStatus);
		return response;
	}

}
