package th.co.ais.cpac.cl.batch.bean;

import java.math.BigDecimal;

public class UpdateResultWriteOffBean {
	private int actionStatus;
	private String fileName;
	private String type;
	private String logMsgNo;
	private String msgV1;
	private String msgV2;
	private String msgV3;
	private String msgV4;
	private BigDecimal batchID;
	public int getActionStatus() {
		return actionStatus;
	}
	public void setActionStatus(int actionStatus) {
		this.actionStatus = actionStatus;
	}


	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getLogMsgNo() {
		return logMsgNo;
	}
	public void setLogMsgNo(String logMsgNo) {
		this.logMsgNo = logMsgNo;
	}
	public String getMsgV1() {
		return msgV1;
	}
	public void setMsgV1(String msgV1) {
		this.msgV1 = msgV1;
	}
	public String getMsgV2() {
		return msgV2;
	}
	public void setMsgV2(String msgV2) {
		this.msgV2 = msgV2;
	}
	public String getMsgV3() {
		return msgV3;
	}
	public void setMsgV3(String msgV3) {
		this.msgV3 = msgV3;
	}
	public String getMsgV4() {
		return msgV4;
	}
	public void setMsgV4(String msgV4) {
		this.msgV4 = msgV4;
	}
	public BigDecimal getBatchID() {
		return batchID;
	}
	public void setBatchID(BigDecimal batchID) {
		this.batchID = batchID;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UpdateResultWriteOffBean [actionStatus=");
		builder.append(actionStatus);
		builder.append(", fileName=");
		builder.append(fileName);
		builder.append(", type=");
		builder.append(type);
		builder.append(", logMsgNo=");
		builder.append(logMsgNo);
		builder.append(", msgV1=");
		builder.append(msgV1);
		builder.append(", msgV2=");
		builder.append(msgV2);
		builder.append(", msgV3=");
		builder.append(msgV3);
		builder.append(", msgV4=");
		builder.append(msgV4);
		builder.append(", batchID=");
		builder.append(batchID);
		builder.append("]");
		return builder.toString();
	}
	
}
