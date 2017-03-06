package th.co.ais.cpac.cl.batch.cmd.waivebatch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.HashMap;

import th.co.ais.cpac.cl.batch.ConstantsBatchReceiveResult;
import th.co.ais.cpac.cl.batch.bean.UpdateResultSSFBean;
import th.co.ais.cpac.cl.batch.bean.UpdateResultWaiveBatchBean;
import th.co.ais.cpac.cl.batch.cnf.CNFDatabase;
import th.co.ais.cpac.cl.batch.db.CLBatch;
import th.co.ais.cpac.cl.batch.db.CLBatch.CLBatchInfo;
import th.co.ais.cpac.cl.batch.db.CLOrder.CLOrderTreatementInfo;
import th.co.ais.cpac.cl.batch.db.CLTreatment;
import th.co.ais.cpac.cl.batch.db.CLWaive;
import th.co.ais.cpac.cl.batch.db.CLWaive.CLWaiveInfoResponse;
import th.co.ais.cpac.cl.batch.db.CLWaive.CLWaiveTreatementInfo;
import th.co.ais.cpac.cl.batch.db.PMBatchAdjDtl;
import th.co.ais.cpac.cl.batch.db.PMBatchAdjDtl.PMBatchAdjInfoResponse;
import th.co.ais.cpac.cl.batch.db.PMInvoice;
import th.co.ais.cpac.cl.batch.db.PMInvoice.PMInvoiceInfoResponse;
import th.co.ais.cpac.cl.batch.template.ProcessTemplate;
import th.co.ais.cpac.cl.batch.util.FileUtil;
import th.co.ais.cpac.cl.batch.util.Utility;
import th.co.ais.cpac.cl.batch.util.ValidateUtil;
import th.co.ais.cpac.cl.common.Context;
import th.co.ais.cpac.cl.common.UtilityLogger;
import th.co.ais.cpac.cl.template.database.DBConnectionPools;

public class UpdateWaiveBatchResultProcess extends ProcessTemplate {
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
	public void executeProcess(Context context, String processPath,String syncFileName,String inboundFileName) { // suspendJobType=S,terminateJobType=T,reconnectJobType=R
		execute();
		readFile(context, processPath,syncFileName,inboundFileName);
	}

	public void readFile(Context context,String processPath,String syncFileName,String inboundFileName) {
		// อ่าน file ทีละ record
		// เช็คว่า record แรกของไฟล์เป็น order type ไหน
		// จบไฟล์ค่อย update treatement โดย grouping ตาม BA,update batch ต่อ

		try {
			context.getLogger().info("Start WorkerReceive....");
			context.getLogger().info("jobType->" + Utility.getJobName(ConstantsBatchReceiveResult.waiveBatchJobType));
			context.getLogger().info("SyncFileName->" + syncFileName);
			/***** START LOOP ******/
			// 1.Rename file.sync to .dat

			BigDecimal batchID = null;
			// 2. Find Batch ID by fileDatExtName
			CLBatch batchDB = new CLBatch(context.getLogger());
			CLBatchInfo result = batchDB.getBatchInfoByFileName(ConstantsBatchReceiveResult.batchInprogressStatus, inboundFileName,context);
			String username = Utility.getusername(ConstantsBatchReceiveResult.waiveBatchJobType);
			if (result != null) {
				batchID = result.getBatchId();
				batchDB.updateInboundReceiveStatus(ConstantsBatchReceiveResult.batchReceiveStatus, batchID, syncFileName, username,context);

			} else {
				throw new Exception("Not Find Batch ID : " + inboundFileName);
			}
			// 2. read .dat
			int recordNum = 1;
			HashMap<BigDecimal, String> treatmentIDlist = new HashMap<BigDecimal, String>();
			String filePath = processPath + "/" + inboundFileName;
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(filePath));
				String sCurrentLine;
				while ((sCurrentLine = br.readLine()) != null) {
					String dataContent = sCurrentLine;
					if (!ValidateUtil.isNull(dataContent)) {
						String[] dataContentArr = dataContent.split(ConstantsBatchReceiveResult.PIPE);
						if (dataContentArr != null && dataContentArr.length > 0) {
							// 3.Find Batch ID & Update Batch to Receive
							// Status from header file
							if ("01".equals(dataContentArr[0])) {
								context.getLogger().info("Header Data : " + dataContent);
							} else if ("02".equals(dataContentArr[0])) {
								// 4.Read Body File
								// 4.1 Read per record
								if (dataContentArr.length == 19) {
									UpdateResultWaiveBatchBean request = new UpdateResultWaiveBatchBean();
									request.setBaNo(dataContentArr[3]);
									request.setBatchID(batchID);
									request.setInvoiceNumb(dataContentArr[4]);
									request.setAmount(new BigDecimal(dataContentArr[15]));
									request.setFileName(syncFileName);
									if ("Y".equals(dataContentArr[16])) {
										request.setActionStatus(ConstantsBatchReceiveResult.actSuccessStatus);
										request.setAdjStatus(ConstantsBatchReceiveResult.adjCompleteStatus);
									} else {
										request.setActionStatus(ConstantsBatchReceiveResult.actFailStatus);
										request.setAdjStatus(ConstantsBatchReceiveResult.adjFailStatus);
										request.setFailReason(dataContentArr[17] + ":" + dataContentArr[18]);
									}
									// 4.2 Find INVOICE_ID
									request = findInvoiceIDAndBatchDtlID(request);
									if (request.getInvoiceID() != null && request.getBatchAdjDtlID() != null) {
										// 4.3 update waive status
										CLWaiveTreatementInfo waiveInfo = updateWaive(request, username);
										if (waiveInfo != null) {
											treatmentIDlist.put(waiveInfo.getTreatementId(), "");
										}
									} else {
										context.getLogger()
												.info("Not Found Invoice ID or BatchAdjDtlID in Invoice Numb :"
														+ request.getInvoiceNumb());
									}

								} else {
									throw new Exception("File Wrong format body " + recordNum + ": " + dataContent);
								}
								recordNum++;
							} else if ("09".equals(dataContentArr[0])) {
								context.getLogger().info("Footer Data : " + dataContent);
							} else {
								context.getLogger().info("Wrong record type : " + dataContent);
							}
						} else {
							throw new Exception("Not Found |:" + dataContent);
						}

					} else {
						throw new Exception("Not Find Content in record ");
					}
				}
			} finally {
				if (br != null)
					br.close();
			}

			// Update Treatement
			// 5.Update Treatment by Treatment ID
			if (treatmentIDlist != null && treatmentIDlist.size() > 0) {
				for (BigDecimal key : treatmentIDlist.keySet()) {
					CLWaive tbl = new CLWaive(context.getLogger());
					// Select Action Status by TREATMENT_ID
					CLWaiveInfoResponse waiveResult = tbl.getWaiveTreatementInfoByTreatmentID(key,context);
					if (waiveResult != null && waiveResult.getResponse() != null
							&& waiveResult.getResponse().size() > 0) {
						boolean successFlag = false;
						boolean failFlag = false;
						boolean incomplete = false;
						for (int i = 0; i < waiveResult.getResponse().size(); i++) {
							CLWaiveTreatementInfo waiveTreat = waiveResult.getResponse().get(i);
							if (waiveTreat.getActStatus() == ConstantsBatchReceiveResult.actSuccessStatus) {
								successFlag = true;
							} else if (waiveTreat.getActStatus() == ConstantsBatchReceiveResult.actFailStatus) {
								failFlag = true;
							} else {
								incomplete = true;
							}
						}
						/* Summary Status */
						int treatResult = 0;
						if (incomplete) {
							treatResult = ConstantsBatchReceiveResult.treatIncompleteStatus;
						} else if (failFlag) {
							treatResult = ConstantsBatchReceiveResult.treatFailStatus;
						} else {
							treatResult = ConstantsBatchReceiveResult.treatSuccessStatus;
						}

						/* Update Treatment */
						CLTreatment treatmentDB = new CLTreatment(context.getLogger());
						treatmentDB.updateTreatmentReceive(treatResult, key, username, "",context);
					} else {
						context.getLogger().info("not found treatment");
					}

				}
			} else {
				context.getLogger().info("treatmentIDlist size =0");
			}

			/* 6. Update Batch Status to Complete */
			batchDB.updateInboundCompleteStatus(batchID, username,context);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		context.getLogger().debug("End Read File....");
	}

	public UpdateResultWaiveBatchBean findInvoiceIDAndBatchDtlID(UpdateResultWaiveBatchBean request) throws Exception {
		PMInvoice pmInvoiceDB = new PMInvoice(context.getLogger());
		PMInvoiceInfoResponse pmInvoiceResult = pmInvoiceDB.getInvoiceIDByInvoiceNum(request.getInvoiceNumb(),context);
		if (pmInvoiceResult != null && pmInvoiceResult.getResponse() != null
				&& pmInvoiceResult.getResponse().size() > 0) {
			if (pmInvoiceResult.getResponse().size() == 1) {
				request.setInvoiceID(pmInvoiceResult.getResponse().get(1).getInvoiceID());
				PMBatchAdjDtl pmBatchAdjDtlDB = new PMBatchAdjDtl(context.getLogger());
				PMBatchAdjInfoResponse pmBatchAdjResult = pmBatchAdjDtlDB.getBatchDtlIDByInvoiceID(
						pmInvoiceResult.getResponse().get(1).getInvoiceID(), request.getAmount(),
						request.getAdjStatus(),context);
				if (pmBatchAdjResult != null && pmBatchAdjResult.getResponse() != null
						&& pmBatchAdjResult.getResponse().size() > 0) {
					if (pmBatchAdjResult.getResponse().size() == 1) {
						request.setBatchAdjDtlID(pmBatchAdjResult.getResponse().get(1).getBatchDtlID());
					} else {
						context.getLogger().info(
								"More than record Invoice ID :" + pmInvoiceResult.getResponse().get(1).getInvoiceID());
					}
				} else {
					context.getLogger()
							.info("No record Invoice ID :" + pmInvoiceResult.getResponse().get(1).getInvoiceID());
				}
			} else {
				context.getLogger().info("More than record Invoice Numb :" + request.getInvoiceNumb());
			}
		} else {
			context.getLogger().info("No record Invoice Numb :" + request.getInvoiceNumb());
		}
		return request;
	}

	public CLWaiveTreatementInfo updateWaive(UpdateResultWaiveBatchBean request, String username) throws Exception {
		/**********************
		 * 1.Get Record Inprocess 2.Update Waive Success Status
		 */
		CLWaiveTreatementInfo waiveInfo = null;
		CLWaive tbl = new CLWaive(context.getLogger());

		waiveInfo = tbl.getWaiveTreatementInfo(request.getBaNo(), request.getBatchID(), request.getInvoiceID(),
				ConstantsBatchReceiveResult.actInprogressStatus,context);
		if (waiveInfo != null) {
			tbl.updateWaiveStatus(request.getBaNo(), request.getBatchID(), request.getInvoiceID(),
					request.getBatchAdjDtlID(), request.getActionStatus(), request.getFailReason(), username,context);
		} else {
			context.getLogger().info("no orderInfo -> " + request.toString());
		}
		return waiveInfo;
	}

}
