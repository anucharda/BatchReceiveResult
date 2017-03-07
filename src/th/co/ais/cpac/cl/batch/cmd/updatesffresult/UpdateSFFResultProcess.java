package th.co.ais.cpac.cl.batch.cmd.updatesffresult;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.HashMap;

import th.co.ais.cpac.cl.batch.ConstantsBatchReceiveResult;
import th.co.ais.cpac.cl.batch.bean.UpdateResultSSFBean;
import th.co.ais.cpac.cl.batch.db.CLBatch;
import th.co.ais.cpac.cl.batch.db.CLBatch.CLBatchInfo;
import th.co.ais.cpac.cl.batch.db.CLOrder;
import th.co.ais.cpac.cl.batch.db.CLOrder.CLOrderInfoResponse;
import th.co.ais.cpac.cl.batch.db.CLOrder.CLOrderTreatementInfo;
import th.co.ais.cpac.cl.batch.db.CLTreatment;
import th.co.ais.cpac.cl.batch.template.ProcessTemplate;
import th.co.ais.cpac.cl.batch.util.FileUtil;
import th.co.ais.cpac.cl.batch.util.Utility;
import th.co.ais.cpac.cl.batch.util.ValidateUtil;
import th.co.ais.cpac.cl.common.Context;

public class UpdateSFFResultProcess extends ProcessTemplate {
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

	public void executeProcess(Context context, String jobType, String[] fileNames,String processPath) { // suspendJobType=S,terminateJobType=T,reconnectJobType=R
		context.getLogger().info("Start UpdateSFFResultProcess.executeProcess");
		execute();
		readFile(context, jobType, fileNames,processPath);
		context.getLogger().info("End UpdateSFFResultProcess.executeProcess");
	}

	public void readFile(Context context, String jobType, String[] fileNames,String processPath) {
		// อ่าน file ทีละ record
		// เช็คว่า record แรกของไฟล์เป็น order type ไหน
		// จบไฟล์ค่อย update treatement โดย grouping ตาม BA,update batch ต่อ
		HashMap<BigDecimal, String> treatmentIDlist = null;

		try {
			context.getLogger().info("Start UpdateSFFResultProcess.readFile");
			context.getLogger().info("jobType->" +  Utility.getJobName(jobType));
			context.getLogger().info("fileNames->" + fileNames.toString());

			if (ConstantsBatchReceiveResult.suspendJobType.equals(jobType) || ConstantsBatchReceiveResult.terminateJobType.equals(jobType)
					|| ConstantsBatchReceiveResult.reconnectJobType.equals(jobType)) {

				/***** START LOOP ******/
				// 1.for loop file ใน Sync
				//String successFileName = ""; // ได้จากข้อ 2
			//	String failFileName = "";
				String batchFileName = "";
				BigDecimal batchID = null;
				String username = Utility.getusername(jobType);								
				// 2. อ่านชื่อไฟล์จากไฟล์ .sync
				for(int i=0;i<fileNames.length;i++) {// อ่านไฟล์ sync
					String outputFileName = fileNames[i];
					if (outputFileName.indexOf(ConstantsBatchReceiveResult.sffOKExt) != -1) {
						batchFileName = batchFileName + outputFileName;
					} else if (outputFileName.indexOf(ConstantsBatchReceiveResult.sffErrExt) != -1) {
						batchFileName = batchFileName +":"+ outputFileName;
					} else {
						context.getLogger().info("File extension not support->" + outputFileName);
					}
				}


				// 2.1. //........ read file ok & err
				treatmentIDlist = new HashMap<BigDecimal, String>();
				boolean firstFile = false;
				for (int j = 0; j < fileNames.length; j++) { // for loop file
					String filePath = processPath +"/"+fileNames[j];
					boolean successFile = false;
					if (filePath.indexOf(ConstantsBatchReceiveResult.sffOKExt) != -1) {// 5. Check
						successFile = true;
					}
					if (!ValidateUtil.isNull(filePath)) {
						context.getLogger()
								.info("Start Read File ->" + j + 1 + "/" + fileNames.length + "->" + filePath);
						int recordNum = 1;
						
						BufferedReader br = null;
						try{
							br = new BufferedReader(new FileReader(filePath));
							String sCurrentLine;
							String fileName="";
							while ((sCurrentLine = br.readLine()) != null) {
								String dataContent = sCurrentLine;
								if (!ValidateUtil.isNull(dataContent)) {
									String[] dataContentArr = dataContent.split(ConstantsBatchReceiveResult.PIPE);
									if (dataContentArr != null && dataContentArr.length > 0) {
										// 3.Find Batch ID & Update Batch to Receive
										// Status from header file
										
										if ("01".equals(dataContentArr[0])) {// && !firstFile
											if (dataContentArr.length == 2) {
												// 3.1.Find BATCH_ID From File Name
												// -> ที่ INBOUND_STATUS =1
												fileName=dataContentArr[1];
											
												if(!firstFile){
													CLBatch batchDB = new CLBatch(context.getLogger());
													CLBatchInfo result = batchDB.getBatchInfoByFileName(ConstantsBatchReceiveResult.batchInprogressStatus, fileName,context);

													if (result != null) {
													// 3.2 Update Batch Status to
													// Receive
														batchID = result.getBatchId();
														batchDB.updateInboundReceiveStatus(ConstantsBatchReceiveResult.batchReceiveStatus,
															batchID, batchFileName, username,context);
														firstFile = true;
													} else {
														throw new Exception("Not Find Batch ID : " + fileName);
													}
												}
											} else {
												throw new Exception("Wrong format header : " + dataContent);
											}
										} else if ("02".equals(dataContentArr[0])) {
											// 4.Read Body File
											UpdateResultSSFBean request = new UpdateResultSSFBean();
											if (successFile) {
												request.setMobileNo(dataContentArr[1]);
												request.setOrderType(dataContentArr[2]);
												request.setSuspendType(dataContentArr[3]);
												request.setFileName(fileName);
												request.setSffOrderNo(dataContentArr[dataContentArr.length-1]);
												request.setActionStatus(ConstantsBatchReceiveResult.actSuccessStatus);
											} else {
												request.setMobileNo(dataContentArr[1]);
												request.setOrderType(dataContentArr[2]);
												request.setSuspendType(dataContentArr[3]);
												request.setFileName(fileName);
												request.setActionStatus(ConstantsBatchReceiveResult.actFailStatus);
												request.setFailReason(dataContentArr[dataContentArr.length-1]);
											}
											request.setActionID(Utility.getActionID(jobType));
											request.setBatchID(batchID);
											// 4.1.Update CL_ORDER to Success/Fail
											CLOrderTreatementInfo orderInfo = updateOrder(request, context, username);
											if (orderInfo != null) {
												treatmentIDlist.put(orderInfo.getTreatementId(), "");
											}
											recordNum++;
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
						
					}
				}

				// 5.Update Treatment by Treatment ID
				if (treatmentIDlist != null && treatmentIDlist.size() > 0) {
					for (BigDecimal key : treatmentIDlist.keySet()) {
						CLOrder tbl = new CLOrder(context.getLogger());
						// Select Action Status by TREATMENT_ID
						CLOrderInfoResponse orderResult = tbl.getOrderTreatementInfoByTreatmentID(key,context);
						if (orderResult != null && orderResult.getResponse() != null
								&& orderResult.getResponse().size() > 0) {
							boolean successFlag = false;
							boolean failFlag = false;
							boolean incomplete = false;
							for (int i = 0; i < orderResult.getResponse().size(); i++) {
								CLOrderTreatementInfo orderTreat = orderResult.getResponse().get(i);
								if (orderTreat.getActStatus() == ConstantsBatchReceiveResult.actSuccessStatus) {
									successFlag = true;
								} else if (orderTreat.getActStatus() == ConstantsBatchReceiveResult.actFailStatus) {
									failFlag = true;
								} else {
									incomplete = true;
								}
							}
							/* Summary Status */
							int treatResult = 0;
							if (incomplete) {
								treatResult = ConstantsBatchReceiveResult.treatIncompleteStatus;
							} else if (failFlag&!successFlag) {
								treatResult = ConstantsBatchReceiveResult.treatFailStatus;
							} else if (!failFlag&successFlag) {
								treatResult = ConstantsBatchReceiveResult.treatSuccessStatus;
							} else{
								treatResult = ConstantsBatchReceiveResult.treatIncompleteStatus;
							}

							/* Update Treatment */
							CLTreatment treatmentDB = new CLTreatment(context.getLogger());
							treatmentDB.updateTreatmentReceive(treatResult, key, username,"",context);
						} else {
							context.getLogger().info("not found treatment");
						}

					}
				} else {
					context.getLogger().info("treatmentIDlist size =0");
				}
				
				/*6. Update Batch Status to Complete*/
				CLBatch batchDB = new CLBatch(context.getLogger());
				batchDB.updateInboundCompleteStatus(batchID, username,context);
			}
		} catch (Exception e) {
			context.getLogger().info("Error->"+e.getMessage()+": "+e.getCause().toString());
		} finally {
			context.getLogger().info("End UpdateSFFResultProcess.readFile");
		}
	}


	public CLOrderTreatementInfo updateOrder(UpdateResultSSFBean request, Context context, String username) throws Exception {
		/**********************
		 * 1.Get Record Inprocess 2.Update Order Success Status
		 */
		CLOrderTreatementInfo orderInfo = null;
		CLOrder tbl = new CLOrder(context.getLogger());
		// Criteria ORDER_ACTION_ID = Suspend,Reconnect,Terminate Action_status=
		// inprocess
		orderInfo = tbl.getOrderTreatementInfo(request.getMobileNo(), request.getBatchID(),
				ConstantsBatchReceiveResult.actInprogressStatus,context);
		if (orderInfo != null) {
			tbl.updateOrderStatus(request.getMobileNo(), request.getBatchID(), request.getActionStatus(),
					request.getSffOrderNo(), request.getFailReason(), username,context);
		} else {
			context.getLogger().info("no orderInfo -> " + request.toString());
		}
		return orderInfo;
	}


}
