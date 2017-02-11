package th.co.ais.cpac.cl.batch.cmd.writeoff;

import java.math.BigDecimal;

import th.co.ais.cpac.cl.batch.Constants;
import th.co.ais.cpac.cl.batch.bean.UpdateResultWriteOffBean;
import th.co.ais.cpac.cl.batch.db.CLBaInfo;
import th.co.ais.cpac.cl.batch.db.CLBatch;
import th.co.ais.cpac.cl.batch.db.CLBatch.CLBatchInfo;
import th.co.ais.cpac.cl.batch.db.CLTreatment;
import th.co.ais.cpac.cl.batch.db.CLWriteOff;
import th.co.ais.cpac.cl.batch.db.CLWriteOff.CLWriteOffInfoResponse;
import th.co.ais.cpac.cl.batch.db.CLWriteOff.CLWriteOffTreatementInfo;
import th.co.ais.cpac.cl.batch.template.ProcessTemplate;
import th.co.ais.cpac.cl.batch.util.Utility;
import th.co.ais.cpac.cl.batch.util.ValidateUtil;
import th.co.ais.cpac.cl.common.Context;

public class UpdateWriteOffResultProcess extends ProcessTemplate {
	@Override
	protected String getPathDatabase() {
		// TODO Auto-generated method stub
		return Constants.cldbConfPath;
	}

	public void executeProcess(Context context, String ackFileName) { // suspendJobType=S,terminateJobType=T,reconnectJobType=R
		execute();
		readFile(context, ackFileName);
	}

	public void readFile(Context context, String ackFileName) {
		// อ่าน file ทีละ record
		// เช็คว่า record แรกของไฟล์เป็น order type ไหน
		// จบไฟล์ค่อย update treatement โดย grouping ตาม BA,update batch ต่อ
		

		try {
			context.getLogger().info("Start WorkerReceive....");
			context.getLogger().info("jobType->" + Utility.getJobName(Constants.writeOffJobType));
			context.getLogger().info("AckFileName->" + ackFileName);
			/***** START LOOP ******/
			// 1.Rename file.sync to .dat
			String fileDatExtName = ackFileName.replace(".ack", ".dat");
			BigDecimal batchID = null;
			// 2. Find Batch ID by fileDatExtName
			if(fileDatExtName.indexOf("_WO_")!=-1){
				CLBatch batchDB = new CLBatch(context.getLogger());
				CLBatchInfo result = batchDB.getBatchInfoByFileName(Constants.batchInprogressStatus, fileDatExtName);
				String username = Utility.getusername(Constants.writeOffJobType);
				if (result != null) {
					//2.1 Update File Batch
					batchID = result.getBatchId();
					batchDB.updateInboundReceiveStatus(Constants.batchReceiveStatus, batchID, ackFileName, username);
					//2.2 Read File
					for(int i=1;i<10;i++){
						String dataContent = "";/// ?????????????????อ่านในไฟล์แหละ
						if (!ValidateUtil.isNull(dataContent)) {
							String[] dataContentArr = dataContent.split(Constants.PIPE);
							if (dataContentArr != null && dataContentArr.length > 0) {
								if (dataContentArr.length == 14) {
									boolean successFlag=false;
									if(Constants.writeOffSuccess.equals(dataContentArr[0])||Constants.writeOffFail.equals(dataContentArr[0])){
										if(Constants.writeOffSuccess.equals(dataContentArr[0])){
											successFlag=true;
										}
										UpdateResultWriteOffBean request=new UpdateResultWriteOffBean();
										if(successFlag){
											request.setActionStatus(Constants.actSuccessStatus);
										}else{
											request.setActionStatus(Constants.actFailStatus);
										}
										request.setType(dataContentArr[0]);
										request.setFileName(ackFileName);
										request.setLogMsgNo(dataContentArr[5]);
										request.setMsgV1(dataContentArr[6]);
										request.setMsgV2(dataContentArr[7]);
										request.setMsgV3(dataContentArr[8]);
										request.setMsgV4(dataContentArr[9]);
										request.setBatchID(batchID);
										
										//2.3 Update result
										findAndUpdateWriteOffResult( request, username);
										
										//2.4 Update Batch Result
										batchDB.updateInboundCompleteStatus(batchID, username);
									}else {
										throw new Exception("Wrong Type : " + dataContent);
									}
								}else {
									throw new Exception("File Wrong format body : " + dataContent);
								}
							}else {
								throw new Exception("Not Found |:" + dataContent);
							}
						} else {
							throw new Exception("Not Find Content in record ");
						}
					}
					
				} else {
					throw new Exception("Not Find Batch ID : " + fileDatExtName);
				}
			}else{
				throw new Exception("Skip not write off file : " + fileDatExtName);
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		context.getLogger().debug("End Read File....");
	}

	public void findAndUpdateWriteOffResult(UpdateResultWriteOffBean request,String username) throws Exception {
		CLWriteOff writeOffDB=new CLWriteOff(context.getLogger());
		CLWriteOffInfoResponse writeOffResult=writeOffDB.getOrderTreatementInfo(request.getBatchID());
		
		if(writeOffResult!=null&&writeOffResult.getResponse()!=null&&writeOffResult.getResponse().size()>0){
			for(int i=0;i<writeOffResult.getResponse().size();i++){
				CLWriteOffTreatementInfo writeOffInfo=writeOffResult.getResponse().get(i);
				CLTreatment treatmentDB=new CLTreatment(context.getLogger());
				StringBuilder failMsg = new StringBuilder();
				failMsg.append(request.getLogMsgNo()).append(":").append(request.getMsgV1()).append(request.getMsgV2()).append(request.getMsgV3()).append(request.getMsgV4());
				//1.Update CL_TREATEMENT
				treatmentDB.updateTreatmentReceive(request.getActionStatus(),writeOffInfo.getTreatementId(), username,failMsg.toString());
				if(Constants.writeOffSuccess.equals(request.getType())){
					CLBaInfo baInfoDB=new CLBaInfo(context.getLogger());
					baInfoDB.updateTreatmentReceive(writeOffInfo.getBaNo(), writeOffInfo.getWriteOffDtm(), writeOffInfo.getWriteOffTypeId(), username);
					
				}
			}
			
		}else{
			throw new Exception("Not Find Batch ID : " + request.getBatchID());
		}
	}
}
