package th.co.ais.cpac.cl.batch.db;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import th.co.ais.cpac.cl.batch.Constants;
import th.co.ais.cpac.cl.common.UtilityLogger;
import th.co.ais.cpac.cl.template.database.DBConnectionPools;
import th.co.ais.cpac.cl.template.database.DBTemplatesExecuteQuery;
import th.co.ais.cpac.cl.template.database.DBTemplatesResponse;
import th.co.ais.cpac.cl.template.database.DBTemplatesUpdate;

/**
 *
 * @author Sirirat
 */
public class CLOrder {

	protected final UtilityLogger logger;

	public CLOrder(UtilityLogger logger) {
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

	public class CLOrderInfo {
		protected CLOrderInfo() {
		}

		private BigDecimal orderId;
		private String baNumber;
		private String mobileNumber;
		private BigDecimal orderActionId;
		private BigDecimal orderCriteriaId;

		private String orderType;
		private String orderReason;
		private String suspendType;

		private String networkType;
		private BigDecimal actionStatus;
		private Date actionStatusDtm;
		private String actionRemark;
		private BigDecimal negoId;
		private BigDecimal batchId;
		private String sffOrderNumber;

		private Character endRequestBoo;

		private Date created;
		private String createdBy;
		private Date lastUpd;
		private String lastUpdBy;

		public BigDecimal getOrderId() {
			return orderId;
		}

		public void setOrderId(BigDecimal orderId) {
			this.orderId = orderId;
		}

		public String getBaNumber() {
			return baNumber;
		}

		public void setBaNumber(String baNumber) {
			this.baNumber = baNumber;
		}

		public String getMobileNumber() {
			return mobileNumber;
		}

		public void setMobileNumber(String mobileNumber) {
			this.mobileNumber = mobileNumber;
		}

		public BigDecimal getOrderActionId() {
			return orderActionId;
		}

		public void setOrderActionId(BigDecimal orderActionId) {
			this.orderActionId = orderActionId;
		}

		public BigDecimal getOrderCriteriaId() {
			return orderCriteriaId;
		}

		public void setOrderCriteriaId(BigDecimal orderCriteriaId) {
			this.orderCriteriaId = orderCriteriaId;
		}

		public String getOrderType() {
			return orderType;
		}

		public void setOrderType(String orderType) {
			this.orderType = orderType;
		}

		public String getOrderReason() {
			return orderReason;
		}

		public void setOrderReason(String orderReason) {
			this.orderReason = orderReason;
		}

		public String getSuspendType() {
			return suspendType;
		}

		public void setSuspendType(String suspendType) {
			this.suspendType = suspendType;
		}

		public String getNetworkType() {
			return networkType;
		}

		public void setNetworkType(String networkType) {
			this.networkType = networkType;
		}

		public BigDecimal getActionStatus() {
			return actionStatus;
		}

		public void setActionStatus(BigDecimal actionStatus) {
			this.actionStatus = actionStatus;
		}

		public Date getActionStatusDtm() {
			return actionStatusDtm;
		}

		public void setActionStatusDtm(Date actionStatusDtm) {
			this.actionStatusDtm = actionStatusDtm;
		}

		public String getActionRemark() {
			return actionRemark;
		}

		public void setActionRemark(String actionRemark) {
			this.actionRemark = actionRemark;
		}

		public BigDecimal getNegoId() {
			return negoId;
		}

		public void setNegoId(BigDecimal negoId) {
			this.negoId = negoId;
		}

		public BigDecimal getBatchId() {
			return batchId;
		}

		public void setBatchId(BigDecimal batchId) {
			this.batchId = batchId;
		}

		public String getSffOrderNumber() {
			return sffOrderNumber;
		}

		public void setSffOrderNumber(String sffOrderNumber) {
			this.sffOrderNumber = sffOrderNumber;
		}

		public Date getCreated() {
			return created;
		}

		public void setCreated(Date created) {
			this.created = created;
		}

		public String getCreatedBy() {
			return createdBy;
		}

		public void setCreatedBy(String createdBy) {
			this.createdBy = createdBy;
		}

		public Date getLastUpd() {
			return lastUpd;
		}

		public void setLastUpd(Date lastUpd) {
			this.lastUpd = lastUpd;
		}

		public String getLastUpdBy() {
			return lastUpdBy;
		}

		public void setLastUpdBy(String lastUpdBy) {
			this.lastUpdBy = lastUpdBy;
		}

		public Character getEndRequestBoo() {
			return endRequestBoo;
		}

		public void setEndRequestBoo(Character endRequestBoo) {
			this.endRequestBoo = endRequestBoo;
		}

	}

	public class CLOrderInfoResponse extends DBTemplatesResponse<ArrayList<CLOrderInfo>> {

		@Override
		protected ArrayList<CLOrderInfo> createResponse() {
			return new ArrayList<>();
		}

	}

	protected class GetOrderInfoByMobileAndAction
			extends DBTemplatesExecuteQuery<CLOrderInfoResponse, UtilityLogger, DBConnectionPools> {
		private String mobileNo;
		private int orderActionID;

		public GetOrderInfoByMobileAndAction(UtilityLogger logger) {
			super(logger);
		}

		@Override
		protected CLOrderInfoResponse createResponse() {
			return new CLOrderInfoResponse();
		}

		@Override
		protected StringBuilder createSqlProcess() {
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT").append(Constants.END_LINE);
			sql.append(" ORDER_ID,BA_NO ").append(Constants.END_LINE);
			sql.append(" FROM CL_ORDER").append(Constants.END_LINE);
			sql.append(" WHERE MOBILE_NO = ('").append(mobileNo).append("') ").append(Constants.END_LINE);
			sql.append(" and ORDER_ACTION_ID = (").append(orderActionID).append(")").append(Constants.END_LINE);
			sql.append(" and ACTION_STATUS = (").append(Constants.suspendInprogressStatus).append(")")
					.append(Constants.END_LINE);
			return sql;
		}

		@Override
		protected void setReturnValue(ResultSet resultSet) throws SQLException {
			CLOrderInfo temp = new CLOrderInfo();
			temp.setBaNumber(resultSet.getString("BA_NO"));
			temp.setOrderId(resultSet.getBigDecimal("ORDER_ID"));
			response.getResponse().add(temp);
		}

		protected CLOrderInfoResponse execute(String mobileNo, int orderActionID) {
			this.mobileNo = mobileNo;
			this.orderActionID = orderActionID;
			return executeQuery(Constants.getDBConnectionPools(logger), true);
		}
	}

	public CLOrderInfo getOrderInfo(String mobileNo, int orderActionID) {
		CLOrderInfo orderInfo = null;
		CLOrderInfoResponse response = new GetOrderInfoByMobileAndAction(logger).execute(mobileNo, orderActionID);
		if (response.getResponse() != null && response.getResponse().size() > 0) {
			orderInfo = response.getResponse().get(0);
		}
		return orderInfo;
	}

	protected class UpdateOrderStatus extends DBTemplatesUpdate<ExecuteResponse, UtilityLogger, DBConnectionPools> {
		BigDecimal orderID;
		int actionStatus;
		String sffOrderNo;
		String failReason;
		String updateBy;
	    public UpdateOrderStatus(UtilityLogger logger) {
	      super(logger);
	    }

	    @Override
	    protected StringBuilder createSqlProcess() {
	      StringBuilder sql = new StringBuilder();
	      sql.append("UPDATE dbo.CL_ORDER ").append(Constants.END_LINE);
	      sql.append("SET LAST_UPD= getdate() , LAST_UPD_BY='").append(updateBy).append("'").append(Constants.END_LINE);
	      sql.append(",ACTION_STATUS = ").append(actionStatus).append(Constants.END_LINE);	
	      sql.append(", ACTION_STATUS_DTM = getdate() ").append(Constants.END_LINE);
	      sql.append(",SFF_ORDER_NO = '").append(sffOrderNo).append("'").append(Constants.END_LINE);
	      if (failReason != null) {
	        sql.append(", ACTION_REMARK = '").append(failReason).append("'").append(Constants.END_LINE);
	      }
	      sql.append(" WHERE ORDER_ID = ").append(orderID).append(Constants.END_LINE);
	      return sql;
	    }

	    @Override
	    protected ExecuteResponse createResponse() {
	      return new ExecuteResponse();
	    }

	    protected ExecuteResponse execute(BigDecimal orderID, int actionStatus, String sffOrderNo,String failReason,String updateBy) {
	      this.orderID = orderID;
	      this.actionStatus = actionStatus;
	      this.sffOrderNo = sffOrderNo;
	      this.updateBy = updateBy;
	      return executeUpdate(Constants.getDBConnectionPools(logger), true); // case adjust false;
	    }
	}
    public ExecuteResponse updateOrderStatus(BigDecimal orderID, int actionStatus, String sffOrderNo,String failReason,String updateBy) {
        return new UpdateOrderStatus(logger).execute(orderID,actionStatus,sffOrderNo,failReason,updateBy);
    }

}
