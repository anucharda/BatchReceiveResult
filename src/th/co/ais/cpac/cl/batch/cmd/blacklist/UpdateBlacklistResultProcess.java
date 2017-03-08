package th.co.ais.cpac.cl.batch.cmd.blacklist;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;

import th.co.ais.cpac.cl.batch.ConstantsBatchReceiveResult;
import th.co.ais.cpac.cl.batch.bean.UpdateResultBlacklistBean;
import th.co.ais.cpac.cl.batch.db.CLBatch;
import th.co.ais.cpac.cl.batch.db.CLBatch.CLBatchInfo;
import th.co.ais.cpac.cl.batch.db.CLBlacklist;
import th.co.ais.cpac.cl.batch.db.CLBlacklist.CLBlacklistInfo;
import th.co.ais.cpac.cl.batch.db.CLTreatment;
import th.co.ais.cpac.cl.batch.template.ProcessTemplate;
import th.co.ais.cpac.cl.batch.util.FileUtil;
import th.co.ais.cpac.cl.batch.util.Utility;
import th.co.ais.cpac.cl.batch.util.ValidateUtil;
import th.co.ais.cpac.cl.common.Context;

public class UpdateBlacklistResultProcess extends ProcessTemplate {
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

	public void executeProcess(Context context, String jobType, String fileName,String processPath) { // suspendJobType=S,terminateJobType=T,reconnectJobType=R
		context.getLogger().info("Start UpdateBlacklistResultProcess.executeProcess");
		execute();
		readFile(context, jobType, fileName,processPath);
		context.getLogger().info("End UpdateBlacklistResultProcess.executeProcess");
	}

	public void readFile(Context context, String jobType, String fileName,String processPath) {
		// อ่าน file ทีละ record
		// เช็คว่า record แรกของไฟล์เป็น order type ไหน
		// จบไฟล์ค่อย update treatement โดย grouping ตาม BA,update batch ต่อ

		try {
			context.getLogger().info("Start UpdateBlacklistResultProcess.readFile");
			context.getLogger().info("jobType->" +  Utility.getJobName(jobType));
			context.getLogger().info("fileName->" + fileName);

			if (ConstantsBatchReceiveResult.blacklistJobType.equals(jobType)) {

				String batchFileName = fileName;
				BigDecimal batchID = null;
				String username = Utility.getusername(jobType);	
				
				String originalFile=batchFileName.replaceAll("Extract", "");
				
				/***Find BatchID****/
				CLBatch batchDB = new CLBatch(context.getLogger());
				CLBatchInfo result = batchDB.getBatchInfoByFileName(ConstantsBatchReceiveResult.batchInprogressStatus, originalFile,context);

				if (result != null) {
				// 3.2 Update Batch Status to
				// Receive
					batchID = result.getBatchId();
					batchDB.updateInboundReceiveStatus(ConstantsBatchReceiveResult.batchReceiveStatus,batchID, batchFileName, username,context);
				}else {
					throw new Exception("Not Find Batch ID : " + fileName);
				}
				String filePath = processPath +"/"+batchFileName;
				BufferedReader br = null;
				try{
							br = new BufferedReader(new FileReader(filePath));
							String sCurrentLine;
							while ((sCurrentLine = br.readLine()) != null) {
								String dataContent = sCurrentLine;
								if (!ValidateUtil.isNull(dataContent)) {
									String[] dataContentArr = dataContent.split(ConstantsBatchReceiveResult.PIPE);
									if (dataContentArr != null && dataContentArr.length > 0) {
										// 3.Find Batch ID & Update Batch to Receive
										// Status from header file
										
										if ("01".equals(dataContentArr[0])) {// && !firstFile
											context.getLogger().info("Header Data : "+dataContent);
										} else if ("02".equals(dataContentArr[0])) {
											// 4.Read Body File
											UpdateResultBlacklistBean request = new UpdateResultBlacklistBean();
											request.setCustomerID(dataContentArr[1]);
												request.setBaNo(dataContentArr[2]);
												request.setMobileNo(dataContentArr[3]);
												request.setBlacklistDtm(dataContentArr[4]);
												request.setBlacklistType(dataContentArr[5]);
												request.setBlacklistSubType(dataContentArr[6]);
												request.setSource(dataContentArr[7]);
												request.setDlFlag(dataContentArr[8]);
												request.setDlReason(dataContentArr[9]);
												request.setBlacklistEndDtm(dataContentArr[10]);
												request.setRemark(dataContentArr[11]);
												request.setBlUserLogin(dataContentArr[12]);
												request.setBlDivisionID(dataContentArr[13]);
												request.setBatchID(batchID);
												// 4.1.Update CL_BLACKLIST and CL_TREATMENT (success)
												CLBlacklistInfo blacklistInfo = updateBlacklist(request, context, username);
											
											//recordNum++;
										} else if ("09".equals(dataContentArr[0])) {
											context.getLogger().info("Footer Data : "+dataContent);
										}else {
											context.getLogger().info("Wrong record type : "+dataContent);
										}
									} else {
										throw new Exception("Not Found |:" + dataContent);
									}

								} else {
									throw new Exception("Not Find Content in record ");
								}
							}
						}finally{
							if(br!=null)
								br.close();
						}
				
						//update pending record to fail
						updateFailStatus(batchID,context,username);
				
				/*6. Update Batch Status to Complete*/
				batchDB = new CLBatch(context.getLogger());
				batchDB.updateInboundCompleteStatus(batchID, username,context);
			}
		} catch (Exception e) {
			context.getLogger().info("Error->"+e.getMessage()+": "+e.getCause().toString());
		} finally {
			context.getLogger().info("End UpdateBlacklistResultProcess.readFile");
		}
	}


	public CLBlacklistInfo updateBlacklist(UpdateResultBlacklistBean request, Context context, String username) throws Exception {
		/**********************
		 * 1.Get Record Inprocess 2.Update Order Success Status
		 */
		
		
		CLBlacklistInfo blacklistInfo = null;
		CLBlacklist tbl = new CLBlacklist(context.getLogger());
		CLTreatment tblTreat = new CLTreatment(context.getLogger());
		// Criteria ORDER_ACTION_ID = Suspend,Reconnect,Terminate Action_status=
		// inprocess
		if(!"Y".equals(request.getDlFlag())){
			blacklistInfo = tbl.getBlacklistInfo(ConstantsBatchReceiveResult.actInprogressStatus,request.getBatchID(),request.getBaNo(),request.getBlacklistDtm(),request.getBlacklistType(),request.getBlacklistSubType(),request.getSource(),context);	
		}else{
			blacklistInfo = tbl.getDeBlacklistInfo(ConstantsBatchReceiveResult.actInprogressStatus,request.getBatchID(),request.getBaNo(),request.getBlacklistEndDtm(),request.getBlacklistType(),request.getBlacklistSubType(),request.getSource(),request.getDlReason(),context);	

		}
		if (blacklistInfo != null) {
			
			tbl.updateBlacklistStatus(ConstantsBatchReceiveResult.actSuccessStatus,blacklistInfo.getRowID(), blacklistInfo.getBlacklistID(),username,context);		
			tblTreat.updateBlackListResult(ConstantsBatchReceiveResult.actSuccessStatus,  blacklistInfo.getBlacklistID(),username,context);
		} else {
			context.getLogger().info("no orderInfo -> " + request.toString());
		}
		return blacklistInfo;
	}
	public void updateFailStatus(BigDecimal batchID, Context context, String username) throws Exception {
		CLBlacklist tbl = new CLBlacklist(context.getLogger());
		CLTreatment tblTreat = new CLTreatment(context.getLogger());
		tbl.updateBlacklistByBatchIDStatus(ConstantsBatchReceiveResult.actInprogressStatus, ConstantsBatchReceiveResult.actFailStatus,batchID,ConstantsBatchReceiveResult.blacklistFailReason,username,context);
		tblTreat.updateBlackListByBatchIDResult(ConstantsBatchReceiveResult.actInprogressStatus, ConstantsBatchReceiveResult.actFailStatus,batchID,ConstantsBatchReceiveResult.blacklistFailReason,username,context);
	}
}
