package th.co.ais.cpac.cl.batch.bean;

import java.math.BigDecimal;

public class UpdateResultWaiveBatchBean {
	private int actionStatus;
	private int actionID;
	private String failReason;
	private String fileName;
	private BigDecimal batchAdjDtlID;
	private BigDecimal batchID;
	private String baNo;
	private String invoiceNumb;
	private BigDecimal invoiceID;
	private BigDecimal amount;
	private String adjStatus;
	public int getActionStatus() {
		return actionStatus;
	}
	public void setActionStatus(int actionStatus) {
		this.actionStatus = actionStatus;
	}
	
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public int getActionID() {
		return actionID;
	}
	public void setActionID(int actionID) {
		this.actionID = actionID;
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
	public BigDecimal getBatchAdjDtlID() {
		return batchAdjDtlID;
	}
	public void setBatchAdjDtlID(BigDecimal batchAdjDtlID) {
		this.batchAdjDtlID = batchAdjDtlID;
	}
	public BigDecimal getBatchID() {
		return batchID;
	}
	public void setBatchID(BigDecimal batchID) {
		this.batchID = batchID;
	}
	public String getBaNo() {
		return baNo;
	}
	public void setBaNo(String baNo) {
		this.baNo = baNo;
	}
	public String getInvoiceNumb() {
		return invoiceNumb;
	}
	public void setInvoiceNumb(String invoiceNumb) {
		this.invoiceNumb = invoiceNumb;
	}
	public BigDecimal getInvoiceID() {
		return invoiceID;
	}
	public void setInvoiceID(BigDecimal invoiceID) {
		this.invoiceID = invoiceID;
	}
	public String getAdjStatus() {
		return adjStatus;
	}
	public void setAdjStatus(String adjStatus) {
		this.adjStatus = adjStatus;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UpdateResultWaiveBatchBean [actionStatus=");
		builder.append(actionStatus);
		builder.append(", actionID=");
		builder.append(actionID);
		builder.append(", failReason=");
		builder.append(failReason);
		builder.append(", fileName=");
		builder.append(fileName);
		builder.append(", batchAdjDtlID=");
		builder.append(batchAdjDtlID);
		builder.append(", batchID=");
		builder.append(batchID);
		builder.append(", baNo=");
		builder.append(baNo);
		builder.append(", invoiceNumb=");
		builder.append(invoiceNumb);
		builder.append(", invoiceID=");
		builder.append(invoiceID);
		builder.append(", amount=");
		builder.append(amount);
		builder.append(", adjStatus=");
		builder.append(adjStatus);
		builder.append("]");
		return builder.toString();
	}
}
