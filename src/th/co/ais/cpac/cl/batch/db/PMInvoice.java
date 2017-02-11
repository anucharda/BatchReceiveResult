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

public class PMInvoice {
	protected final UtilityLogger logger;

	public PMInvoice(UtilityLogger logger) {
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

	public class PMInvoiceInfo {
		protected PMInvoiceInfo() {
		}

		private BigDecimal invoiceID;

		public BigDecimal getInvoiceID() {
			return invoiceID;
		}

		public void setInvoiceID(BigDecimal invoiceID) {
			this.invoiceID = invoiceID;
		}

	}

	public class PMInvoiceInfoResponse extends DBTemplatesResponse<ArrayList<PMInvoiceInfo>> {

		@Override
		protected ArrayList<PMInvoiceInfo> createResponse() {
			return new ArrayList<>();
		}
	}

	protected class FindPMInvoiceIDAction
			extends DBTemplatesExecuteQuery<PMInvoiceInfoResponse, UtilityLogger, DBConnectionPools> {
		private String invoiceNum;

		public FindPMInvoiceIDAction(UtilityLogger logger) {
			super(logger);
		}

		@Override
		protected PMInvoiceInfoResponse createResponse() {
			return new PMInvoiceInfoResponse();
		}

		@Override
		protected StringBuilder createSqlProcess() {
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT").append(Constants.END_LINE);
			sql.append(" INVOICE_ID ").append(Constants.END_LINE);
			sql.append(" PMDB..PM_INVOICE ").append(Constants.END_LINE);
			sql.append(" WHERE INVOICE_NUM = ('").append(invoiceNum).append("') ").append(Constants.END_LINE);
			return sql;
		}

		@Override
		protected void setReturnValue(ResultSet resultSet) throws SQLException {
			PMInvoiceInfo temp = new PMInvoiceInfo();
			temp.setInvoiceID(resultSet.getBigDecimal("INVOICE_ID"));
			response.getResponse().add(temp);
		}

		protected PMInvoiceInfoResponse execute(String invoiceNum) {
			this.invoiceNum = invoiceNum;
			return executeQuery(Constants.getDBConnectionPools(logger), true);
		}
	}

	public PMInvoiceInfoResponse getInvoiceIDByInvoiceNum(String invoiceNum) {
		PMInvoiceInfoResponse response = new FindPMInvoiceIDAction(logger).execute(invoiceNum);
		return response;
	}

}
