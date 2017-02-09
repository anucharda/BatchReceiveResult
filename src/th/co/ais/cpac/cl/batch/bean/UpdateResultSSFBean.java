package th.co.ais.cpac.cl.batch.bean;

import java.math.BigDecimal;

public class UpdateResultSSFBean {
	private String mobileNo;
	private String orderType;  
	private String suspendType;
	private String sffOrderNo;
	private String failReason;
	private String fileName;
	private int actionID;
	private BigDecimal batchID;
	private int actionStatus;
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public String getSuspendType() {
		return suspendType;
	}
	public void setSuspendType(String suspendType) {
		this.suspendType = suspendType;
	}
	
	
	public String getSffOrderNo() {
		return sffOrderNo;
	}
	public void setSffOrderNo(String sffOrderNo) {
		this.sffOrderNo = sffOrderNo;
	}
	public String getFailReason() {
		return failReason;
	}
	public void setFailReason(String failReason) {
		this.failReason = failReason;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public int getActionID() {
		return actionID;
	}
	public void setActionID(int actionID) {
		this.actionID = actionID;
	}
	public BigDecimal getBatchID() {
		return batchID;
	}
	public void setBatchID(BigDecimal batchID) {
		this.batchID = batchID;
	}
	public int getActionStatus() {
		return actionStatus;
	}
	public void setActionStatus(int actionStatus) {
		this.actionStatus = actionStatus;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UpdateResultSSFBean [mobileNo=");
		builder.append(mobileNo);
		builder.append(", orderType=");
		builder.append(orderType);
		builder.append(", suspendType=");
		builder.append(suspendType);
		builder.append(", sffOrderNo=");
		builder.append(sffOrderNo);
		builder.append(", failReason=");
		builder.append(failReason);
		builder.append(", fileName=");
		builder.append(fileName);
		builder.append(", actionID=");
		builder.append(actionID);
		builder.append(", batchID=");
		builder.append(batchID);
		builder.append(", actionStatus=");
		builder.append(actionStatus);
		builder.append("]");
		return builder.toString();
	}
	
}
