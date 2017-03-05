package th.co.ais.cpac.cl.batch.db;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import th.co.ais.cpac.cl.batch.Constants;
import th.co.ais.cpac.cl.batch.db.CLBatch.CLBatchPathInfo;
import th.co.ais.cpac.cl.batch.db.CLBatch.CLBatchPathResponse;
import th.co.ais.cpac.cl.batch.db.CLBatch.GetCLBatchPath;
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
		private int batchVersionNo;
		private BigDecimal batchId;
		private BigDecimal batchTypeId;
		public BigDecimal getBatchId() {
			return batchId;
		}

		public void setBatchId(BigDecimal batchId) {
			this.batchId = batchId;
		}

		public BigDecimal getBatchTypeId() {
			return batchTypeId;
		}

		public void setBatchTypeId(BigDecimal batchTypeId) {
			this.batchTypeId = batchTypeId;
		}

		public int getBatchVersionNo() {
			return batchVersionNo;
		}

		public void setBatchVersionNo(int batchVersionNo) {
			this.batchVersionNo = batchVersionNo;
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
			sql.append(" BATCH_ID,BATCH_TYPE_ID,BATCH_VERSION_NO ").append(Constants.END_LINE);
			sql.append(" FROM dbo.CL_BATCH ").append(Constants.END_LINE);
			sql.append(" WHERE BATCH_FILE_NAME = ('").append(fileName).append("') ").append(Constants.END_LINE);
			sql.append(" and INBOUND_STATUS = (").append(inboundStatus).append(")").append(Constants.END_LINE);
			return sql;
		}

		@Override
		protected void setReturnValue(ResultSet resultSet) throws SQLException {
			CLBatchInfo temp = new CLBatchInfo();
			temp.setBatchId(resultSet.getBigDecimal("BATCH_ID"));
			temp.setBatchTypeId(resultSet.getBigDecimal("BATCH_TYPE_ID"));
			temp.setBatchVersionNo(resultSet.getInt("BATCH_VERSION_NO"));
			response.getResponse().add(temp);
		}

		protected CLBatchInfoResponse execute(int inboundStatus, String fileName) {
			this.inboundStatus = inboundStatus;
			this.fileName = fileName;
			return executeQuery(Constants.getDBConnectionPools(logger), true);
		}
		
	}

	public CLBatchInfo getBatchInfoByFileName(int inboundStatus, String fileName,Context context) throws Exception {
		CLBatchInfo batchInfo = null;
		
		CLBatchInfoResponse response = new FindBatchIDAction(logger).execute(inboundStatus, fileName);
		context.getLogger().debug("getBatchInfoByFileName->"+response.info().toString());
		
		switch(response.getStatusCode()){
			case CLBatchInfoResponse.STATUS_COMPLETE:{
				batchInfo = response.getResponse().get(0);
				break;
			}
			case CLBatchInfoResponse.STATUS_DATA_NOT_FOUND:{
				break;
			}
			default:{
				throw new Exception("Error : " + response.getErrorMsg());
			}
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

	public ExecuteResponse updateInboundReceiveStatus(int inboundStatus,  BigDecimal batchID,String fileName,String username,Context context) throws Exception {
		ExecuteResponse response=new UpdateBatchReceiveAction(logger).execute(inboundStatus, batchID,fileName,username);
		
		context.getLogger().debug("updateInboundReceiveStatus->"+response.info().toString());
		
		switch(response.getStatusCode()){
			case CLBatchInfoResponse.STATUS_COMPLETE:{
				break;
			}
			case CLBatchInfoResponse.STATUS_DATA_NOT_FOUND:{
				break;
			}
			default:{
				throw new Exception("Error : " + response.getErrorMsg());
			}
		}

		return response; 
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

	public ExecuteResponse updateInboundCompleteStatus(BigDecimal batchID,String username,Context context) throws Exception {
		
		ExecuteResponse response=new UpdateBatchCompleteAction(logger).execute(batchID,username);
		context.getLogger().debug("updateInboundCompleteStatus->"+response.info().toString());
		
		switch(response.getStatusCode()){
			case CLBatchInfoResponse.STATUS_COMPLETE:{
				break;
			}
			case CLBatchInfoResponse.STATUS_DATA_NOT_FOUND:{
				break;
			}
			default:{
				throw new Exception("Error : " + response.getErrorMsg());
			}
		}
		
		return response;
	}
	
	 public class CLBatchPathInfo {

		    protected CLBatchPathInfo() {
		    }
		    private BigDecimal batchTypeId;
		    private Constants.Environment environment;
		    private String pathOutbound;
		    private String pathInbound;

		    public BigDecimal getBatchTypeId() {
		      return batchTypeId;
		    }

		    public void setBatchTypeId(BigDecimal batchTypeId) {
		      this.batchTypeId = batchTypeId;
		    }

		    public Constants.Environment getEnvironment() {
		      return environment;
		    }

		    public void setEnvironment(Constants.Environment environment) {
		      this.environment = environment;
		    }

		    public String getPathOutbound() {
		      return pathOutbound;
		    }

		    public void setPathOutbound(String pathOutbound) {
		      this.pathOutbound = pathOutbound;
		    }

		    public String getPathInbound() {
		      return pathInbound;
		    }

		    public void setPathInbound(String pathInbound) {
		      this.pathInbound = pathInbound;
		    }

		  }

		  public class CLBatchPathResponse extends DBTemplatesResponse< CLBatchPathInfo> {

		    @Override
		    protected CLBatchPathInfo createResponse() {
		      return new CLBatchPathInfo();
		    }

		  }

		  protected class GetCLBatchPath extends DBTemplatesExecuteQuery<CLBatchPathResponse, UtilityLogger, DBConnectionPools> {

		    public GetCLBatchPath(UtilityLogger logger) {
		      super(logger);
		    }

		    @Override
		    protected CLBatchPathResponse createResponse() {
		      return new CLBatchPathResponse();
		    }

		    //
		    @Override
		    protected StringBuilder createSqlProcess() {
		      StringBuilder sql = new StringBuilder();
		      sql.append(" SELECT BATCH_TYPE_ID, ENVIRONMENT, PATH_OUTBOUND, PATH_INBOUND, RECORD_STATUS ");
		      sql.append(" FROM dbo.CL_BATCH_PATH ");
		      sql.append(" WHERE BATCH_TYPE_ID = ").append(batchTypeId.toPlainString()).append(" and RECORD_STATUS = 1 ");
		      return sql;
		    }

		    private BigDecimal batchTypeId;

		    @Override
		    protected void setReturnValue(ResultSet resultSet) throws SQLException {
		      CLBatchPathInfo temp = response.getResponse();
		      temp.setBatchTypeId(resultSet.getBigDecimal("BATCH_TYPE_ID"));
		      temp.setEnvironment(Constants.mapEnvironment(resultSet.getBigDecimal("ENVIRONMENT").intValue()));
		      temp.setPathOutbound(resultSet.getString("PATH_OUTBOUND"));
		      temp.setPathInbound(resultSet.getString("PATH_INBOUND"));
		    }

		    protected CLBatchPathResponse execute(BigDecimal batchTypeId) {
		      this.batchTypeId = batchTypeId;
		      return executeQuery(Constants.getDBConnectionPools(logger), true);
		    }

		  }

		  public CLBatchPathResponse getCLBatchPath(BigDecimal batchTypeId) {
		    return new GetCLBatchPath(logger).execute(batchTypeId);
		  }
}
