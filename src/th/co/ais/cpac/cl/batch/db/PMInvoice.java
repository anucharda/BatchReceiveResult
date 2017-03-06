package th.co.ais.cpac.cl.batch.db;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import th.co.ais.cpac.cl.batch.ConstantsBatchReceiveResult;
import th.co.ais.cpac.cl.batch.db.PMBatchAdjDtl.PMBatchAdjInfoResponse;
import th.co.ais.cpac.cl.common.Context;
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
			sql.append(" SELECT").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" INVOICE_ID ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" PMDB..PM_INVOICE ").append(ConstantsBatchReceiveResult.END_LINE);
			sql.append(" WHERE INVOICE_NUM = ('").append(invoiceNum).append("') ").append(ConstantsBatchReceiveResult.END_LINE);
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
			return executeQuery(ConstantsBatchReceiveResult.getDBConnectionPools(logger), true);
		}
	}

	public PMInvoiceInfoResponse getInvoiceIDByInvoiceNum(String invoiceNum,Context context) throws Exception {
		PMInvoiceInfoResponse response = new FindPMInvoiceIDAction(logger).execute(invoiceNum);
		context.getLogger().debug("getInvoiceIDByInvoiceNum->"+response.info().toString());

		switch(response.getStatusCode()){
			case PMInvoiceInfoResponse.STATUS_COMPLETE:{
				break;
			}
			case PMInvoiceInfoResponse.STATUS_DATA_NOT_FOUND:{
				break;
			}
			default:{
				throw new Exception("Error : " + response.getErrorMsg());
			}
		}
		
		return response;
	}

}
