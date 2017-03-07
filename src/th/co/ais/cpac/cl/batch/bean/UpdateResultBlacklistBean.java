package th.co.ais.cpac.cl.batch.bean;

import java.math.BigDecimal;

public class UpdateResultBlacklistBean {
	private String customerID;
	private String orderType;  
	private String baNo;
	private String mobileNo;
	private String blacklistDtm;
	private String blacklistType;
	private String blacklistSubType;
	private String source;
	private String dlFlag;
	private String dlReason;
	private String blacklistEndDtm;
	private String remark;
	private String blUserLogin;
	private String blDivisionID;
	private int actionID;
	private BigDecimal batchID;
	
	public String getCustomerID() {
		return customerID;
	}
	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public String getBaNo() {
		return baNo;
	}
	public void setBaNo(String baNo) {
		this.baNo = baNo;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getBlacklistDtm() {
		return blacklistDtm;
	}
	public void setBlacklistDtm(String blacklistDtm) {
		this.blacklistDtm = blacklistDtm;
	}
	public String getBlacklistType() {
		return blacklistType;
	}
	public void setBlacklistType(String blacklistType) {
		this.blacklistType = blacklistType;
	}
	public String getBlacklistSubType() {
		return blacklistSubType;
	}
	public void setBlacklistSubType(String blacklistSubType) {
		this.blacklistSubType = blacklistSubType;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getDlFlag() {
		return dlFlag;
	}
	public void setDlFlag(String dlFlag) {
		this.dlFlag = dlFlag;
	}
	public String getDlReason() {
		return dlReason;
	}
	public void setDlReason(String dlReason) {
		this.dlReason = dlReason;
	}
	public String getBlacklistEndDtm() {
		return blacklistEndDtm;
	}
	public void setBlacklistEndDtm(String blacklistEndDtm) {
		this.blacklistEndDtm = blacklistEndDtm;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getBlUserLogin() {
		return blUserLogin;
	}
	public void setBlUserLogin(String blUserLogin) {
		this.blUserLogin = blUserLogin;
	}
	public String getBlDivisionID() {
		return blDivisionID;
	}
	public void setBlDivisionID(String blDivisionID) {
		this.blDivisionID = blDivisionID;
	}
	public int getActionID() {
		return actionID;
	}
	public void setActionID(int actionID) {
		this.actionID = actionID;
	}
	public BigDecimal blacklistInfo() {
		return batchID;
	}
	public void setBatchID(BigDecimal batchID) {
		this.batchID = batchID;
	}
	public BigDecimal getBatchID() {
		return batchID;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UpdateResultBlacklistBean [customerID=");
		builder.append(customerID);
		builder.append(", orderType=");
		builder.append(orderType);
		builder.append(", baNo=");
		builder.append(baNo);
		builder.append(", mobileNo=");
		builder.append(mobileNo);
		builder.append(", blacklistDtm=");
		builder.append(blacklistDtm);
		builder.append(", blacklistType=");
		builder.append(blacklistType);
		builder.append(", blacklistSubType=");
		builder.append(blacklistSubType);
		builder.append(", source=");
		builder.append(source);
		builder.append(", dlFlag=");
		builder.append(dlFlag);
		builder.append(", dlReason=");
		builder.append(dlReason);
		builder.append(", blacklistEndDtm=");
		builder.append(blacklistEndDtm);
		builder.append(", remark=");
		builder.append(remark);
		builder.append(", blUserLogin=");
		builder.append(blUserLogin);
		builder.append(", blDivisionID=");
		builder.append(blDivisionID);
		builder.append(", actionID=");
		builder.append(actionID);
		builder.append(", batchID=");
		builder.append(batchID);
		builder.append("]");
		return builder.toString();
	}
	
}
