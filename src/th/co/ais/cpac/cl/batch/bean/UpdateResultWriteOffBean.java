package th.co.ais.cpac.cl.batch.bean;

import java.math.BigDecimal;

public class UpdateResultWriteOffBean {
	private int actionStatus;
	private String fileName;
	private String type;
	private String failMsg;
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

	public BigDecimal getBatchID() {
		return batchID;
	}
	public void setBatchID(BigDecimal batchID) {
		this.batchID = batchID;
	}
	public String getFailMsg() {
		return failMsg;
	}
	public void setFailMsg(String failMsg) {
		this.failMsg = failMsg;
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
		builder.append(", failMsg=");
		builder.append(failMsg);
		builder.append(", batchID=");
		builder.append(batchID);
		builder.append("]");
		return builder.toString();
	}
	
}
