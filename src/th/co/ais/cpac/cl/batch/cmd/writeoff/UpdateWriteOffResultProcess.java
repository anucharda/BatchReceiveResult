package th.co.ais.cpac.cl.batch.cmd.writeoff;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.Date;

import th.co.ais.cpac.cl.batch.ConstantsBatchReceiveResult;
import th.co.ais.cpac.cl.batch.ConstantsBusinessUtil;
import th.co.ais.cpac.cl.batch.bean.UpdateResultWriteOffBean;
import th.co.ais.cpac.cl.batch.db.CLBaInfo;
import th.co.ais.cpac.cl.batch.db.CLBatch;
import th.co.ais.cpac.cl.batch.db.CLBatch.CLBatchInfo;
import th.co.ais.cpac.cl.batch.db.CLTreatment;
import th.co.ais.cpac.cl.batch.db.CLWriteOff;
import th.co.ais.cpac.cl.batch.db.CLWriteOff.CLWriteOffInfoResponse;
import th.co.ais.cpac.cl.batch.db.CLWriteOff.CLWriteOffTreatementInfo;
import th.co.ais.cpac.cl.batch.db.PMInvoice;
import th.co.ais.cpac.cl.batch.db.PMInvoice.PMInvoiceNumResponse;
import th.co.ais.cpac.cl.batch.db.PMSUpdateWriteOff;
import th.co.ais.cpac.cl.batch.db.PMSUpdateWriteOff.PMSUpdateWriteOffInfoResponse;
import th.co.ais.cpac.cl.batch.template.ProcessTemplate;
import th.co.ais.cpac.cl.batch.util.FileUtil;
import th.co.ais.cpac.cl.batch.util.Utility;
import th.co.ais.cpac.cl.batch.util.ValidateUtil;
import th.co.ais.cpac.cl.common.Context;

public class UpdateWriteOffResultProcess extends ProcessTemplate {
	@Override
	protected String getPathDatabase() {
		// TODO Auto-generated method stub
		String dbPath="";
		try{
			dbPath=FileUtil.getDBPath();
		}catch(Exception e){
			context.getLogger().info("Error->"+e.getMessage()+": "+e.getCause().toString());
		}
		return  dbPath;
	}

	public void executeProcess(Context context, String processPath, String ackFileName, String inboundFileName) { // suspendJobType=S,terminateJobType=T,reconnectJobType=R
		execute();
		readFile(context, processPath, ackFileName, inboundFileName);
	}

	public void readFile(Context context, String processPath, String ackFileName, String inboundFileName) {

		try {
			context.getLogger().info("Start WorkerReceive....");
			context.getLogger().info("jobType->" + Utility.getJobName(ConstantsBusinessUtil.writeOffJobType));
			context.getLogger().info("AckFileName->" + ackFileName);
			/***** START LOOP ******/
			// 1.Rename file.sync to .dat
			BigDecimal batchID = null;

			// 2. Find Batch ID by fileDatExtName
			if (inboundFileName.indexOf("_WO_") != -1) {
				CLBatch batchDB = new CLBatch(context.getLogger());
				CLBatchInfo result = batchDB.getBatchInfoByFileName(ConstantsBatchReceiveResult.batchInprogressStatus, inboundFileName,context);
				
				
				
				String username = Utility.getusername(ConstantsBusinessUtil.writeOffJobType);
				if (result != null) {
					// 2.1 Update File Batch
					batchID = result.getBatchId();
					batchDB.updateInboundReceiveStatus(ConstantsBatchReceiveResult.batchReceiveStatus, batchID, ackFileName, username,context);
					// 2.2 Read File
					String filePath = processPath + "/" + inboundFileName;
					BufferedReader br = null;

					try {
						br = new BufferedReader(new FileReader(filePath));
						String sCurrentLine;
						int writeOffStatus =0;
						String writeOffErrorMsg="";
						boolean keepResult=false;
						while ((sCurrentLine = br.readLine()) != null) {
							String dataContent = sCurrentLine;
							if (!ValidateUtil.isNull(dataContent)) {
								String[] dataContentArr = dataContent.split(ConstantsBatchReceiveResult.PIPE);
								if (dataContentArr != null && dataContentArr.length > 0) {
									if (dataContentArr.length == 14) {
											if(!keepResult){
												boolean successFlag = false;
												if (ConstantsBatchReceiveResult.writeOffSuccess.equals(dataContentArr[0])
														|| ConstantsBatchReceiveResult.writeOffFail.equals(dataContentArr[0])) {
													if (ConstantsBatchReceiveResult.writeOffSuccess.equals(dataContentArr[0])) {
														successFlag = true;
													}
													if (successFlag) {
														writeOffStatus=ConstantsBatchReceiveResult.actSuccessStatus;
														
													} else {
														writeOffStatus=ConstantsBatchReceiveResult.actFailStatus;
														StringBuilder failMsg = new StringBuilder();
														failMsg.append(dataContentArr[5]).append(":").append(dataContentArr[6]).append(dataContentArr[7])
																.append(dataContentArr[8]).append(dataContentArr[9]);
														writeOffErrorMsg=failMsg.toString();
													}
												}
												keepResult=true;
											}
											if(writeOffStatus==ConstantsBatchReceiveResult.actFailStatus){
												UpdateResultWriteOffBean request = new UpdateResultWriteOffBean();
												request.setActionStatus(writeOffStatus);
												request.setType(dataContentArr[0]);
												request.setFileName(ackFileName);
												request.setFailMsg(writeOffErrorMsg);
												request.setBatchID(batchID);
												// 2.3 Update Fail Result
												updateWriteOffFail(request,username);
											}
									} else {
											throw new Exception("File Wrong format body : " + dataContent);
									}	
								} else {
									throw new Exception("Not Found |:" + dataContent);
								}
							} else {
								throw new Exception("Not Find Content in record ");
							}
						}
						if(writeOffStatus==ConstantsBatchReceiveResult.actSuccessStatus){
							int index = inboundFileName.indexOf("WO_");
							int endIndex = inboundFileName.indexOf(".");
							String writeOffStr = inboundFileName.substring(index+3, endIndex).replace("_", "");
							Date writeOffDate=Utility.convertStringToDate(writeOffStr);
							updateWriteOffSuccess(batchID,username,writeOffDate);
						}
						
						/*2.4. Update Batch Status to Complete*/
						batchDB = new CLBatch(context.getLogger());
						batchDB.updateInboundCompleteStatus(batchID, username,context);
					} finally {
						if (br != null)
							br.close();
					}

				} else {
					throw new Exception("Not Find Batch ID : " + inboundFileName);
				}
			} else {
				throw new Exception("Skip not write off file : " + inboundFileName);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		context.getLogger().debug("End Read File....");
	}
	public void updateWriteOffFail(UpdateResultWriteOffBean request, String username) throws Exception {
		CLTreatment treatmentDB = new CLTreatment(context.getLogger());
		treatmentDB.updateWriteOffFail(request.getActionStatus(),request.getBatchID(),username,request.getFailMsg(),context);
		
		
	}
	public void updateWriteOffSuccess(BigDecimal batchID, String username,Date writeOffDate) throws Exception {
		
		CLWriteOff writeOffDB = new CLWriteOff(context.getLogger());
		CLWriteOffInfoResponse writeOffResult = writeOffDB.getOrderTreatementInfo(batchID,context);

		if (writeOffResult != null && writeOffResult.getResponse() != null && writeOffResult.getResponse().size() > 0) {
			for (int i = 0; i < writeOffResult.getResponse().size(); i++) {
				CLWriteOffTreatementInfo writeOffInfo = writeOffResult.getResponse().get(i);
				/*Find Invoice Num by BA_NO*/
				PMInvoice pmInvoiceDB = new PMInvoice(context.getLogger());
				PMInvoiceNumResponse invoiceNumResult=pmInvoiceDB.getInvoiceNumbByBaNo(writeOffInfo.getBaNo(), context);
				/*if(invoiceNumResult!=null && invoiceNumResult.getResponse()!=null && invoiceNumResult.getResponse().size()>0){
					StringBuffer invoiceNum=new StringBuffer();
					for(int j=0;j<invoiceNumResult.getResponse().size();j++){
						if(j==0){
							invoiceNum.append(invoiceNumResult.getResponse().get(j));
						}else if(j==invoiceNumResult.getResponse().size()-1){
							invoiceNum.append("|").append(invoiceNumResult.getResponse().get(j)).append("|");
						}else{
							invoiceNum.append("|").append(invoiceNumResult.getResponse().get(j));
						}
					}*/
					/*Call Store Procedure*/
					/*PMSUpdateWriteOff pmsUpdateWriteOffDB =new PMSUpdateWriteOff(context.getLogger());
					PMSUpdateWriteOffInfoResponse updatePMWriteOff=pmsUpdateWriteOffDB.updateWriteOff(writeOffInfo.getBaNo(),invoiceNum.toString());
					*/
					/*Update Treatment*/
					CLTreatment treatmentDB = new CLTreatment(context.getLogger());
					/*Update Treatment Success*/
					treatmentDB.updateTreatmentReceive(ConstantsBatchReceiveResult.actSuccessStatus, writeOffInfo.getTreatementId(), username, "", context);
					/*Update BA Info*/
					CLBaInfo baInfoDB = new CLBaInfo(context.getLogger());
					baInfoDB.updateTreatmentReceive(writeOffInfo.getBaNo(), writeOffDate,writeOffInfo.getWriteOffTypeId(), username,context);
				/*}else{
					context.getLogger().info("No Found Invoice Number BA->" +writeOffInfo.getBaNo());
				}*/
			}
		}
		else {
			throw new Exception("Not Find Batch ID : " + batchID);
		}
	}
}
