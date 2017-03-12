package th.co.ais.cpac.cl.batch.db;

import java.sql.SQLException;
import java.util.ArrayList;

import th.co.ais.cpac.cl.batch.ConstantsBatchReceiveResult;
import th.co.ais.cpac.cl.common.UtilityLogger;
import th.co.ais.cpac.cl.template.database.DBConnectionPools;
import th.co.ais.cpac.cl.template.database.DBTemplateCallableStatement;
import th.co.ais.cpac.cl.template.database.DBTemplatesResponse;

public class PMSUpdateWriteOff {
	 public PMSUpdateWriteOff(UtilityLogger logger) {
		    this.logger = logger;
		  }
		  protected final UtilityLogger logger;
	public class PMSUpdateWriteOffInfo {
		private String  retCode;
	    private String retMsg;
		public String getRetCode() {
			return retCode;
		}
		public void setRetCode(String retCode) {
			this.retCode = retCode;
		}
		public String getRetMsg() {
			return retMsg;
		}
		public void setRetMsg(String retMsg) {
			this.retMsg = retMsg;
		}
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("PMSUpdateWriteOffInfo [");
			if (retCode != null) {
				builder.append("retCode=");
				builder.append(retCode);
				builder.append(", ");
			}
			if (retMsg != null) {
				builder.append("retMsg=");
				builder.append(retMsg);
			}
			builder.append("]");
			return builder.toString();
		} 
	}
	  public class PMSUpdateWriteOffInfoResponse extends DBTemplatesResponse<ArrayList<PMSUpdateWriteOffInfo>> {

		    @Override
		    protected ArrayList<PMSUpdateWriteOffInfo> createResponse() {
		      return new ArrayList<>();
		    }
	 }
	  protected class UpdatePMWriteOffAction extends DBTemplateCallableStatement<PMSUpdateWriteOffInfoResponse, UtilityLogger, DBConnectionPools> {
		  	private String baNo;
		  	private String invoiceNum;
		    public UpdatePMWriteOffAction(UtilityLogger logger) {
		      super(logger);
		    }
			@Override
			protected PMSUpdateWriteOffInfoResponse createResponse() {
				return new PMSUpdateWriteOffInfoResponse();
			}
		    @Override
		    protected void setParameter() throws SQLException {
		      statement.setQueryTimeout(1800);
		      statement.setString(1, baNo);
		      statement.setString(2, invoiceNum);
		      statement.registerOutParameter(3, java.sql.Types.VARCHAR);
		      statement.registerOutParameter(4, java.sql.Types.VARCHAR);
		    }

		    @Override
		    protected void setReturnValue() throws SQLException {
		    	PMSUpdateWriteOffInfo updateWriteOffResult=new PMSUpdateWriteOffInfo();
		    	updateWriteOffResult.setRetCode((String) statement.getObject(3));
		    	updateWriteOffResult.setRetMsg((String) statement.getObject(4));
		        response.getResponse().add(updateWriteOffResult);
		        countRecord();
		    }

		    @Override
		    protected StringBuilder createSqlProcess() {
		      return new StringBuilder("{call PMDB..PM_S_UPDATE_WRITE_OFF (?, ?, ?,?)}");

		    }

		    protected PMSUpdateWriteOffInfoResponse query(String baNo,String invoiceNum) {
		    	this.baNo = baNo;
		    	this.invoiceNum = invoiceNum;
		    	return executeQuery(ConstantsBatchReceiveResult.getDBConnectionPools(logger), true);
		    }

		  }

		  public PMSUpdateWriteOffInfoResponse updateWriteOff(String baNo,String invoiceNum) {
		    return new UpdatePMWriteOffAction(logger).query(baNo,invoiceNum);
		  }
	
}
