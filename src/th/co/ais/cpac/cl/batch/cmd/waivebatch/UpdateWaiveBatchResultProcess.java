package th.co.ais.cpac.cl.batch.cmd.waivebatch;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;

import th.co.ais.cpac.cl.batch.Constants;
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
import th.co.ais.cpac.cl.batch.util.Utility;
import th.co.ais.cpac.cl.batch.util.ValidateUtil;
import th.co.ais.cpac.cl.common.Context;
import th.co.ais.cpac.cl.common.UtilityLogger;
import th.co.ais.cpac.cl.template.database.DBConnectionPools;

public class UpdateWaiveBatchResultProcess extends ProcessTemplate {
	@Override
	protected String getPathDatabase() {
		// TODO Auto-generated method stub
		return Constants.cldbConfPath;
	}

	public void executeProcess(Context context, String syncFileName) { // suspendJobType=S,terminateJobType=T,reconnectJobType=R
		execute();
		readFile(context, syncFileName);
	}

	public void readFile(Context context, String syncFileName) {
		// อ่าน file ทีละ record
		// เช็คว่า record แรกของไฟล์เป็น order type ไหน
		// จบไฟล์ค่อย update treatement โดย grouping ตาม BA,update batch ต่อ
		

		try {
			context.getLogger().info("Start WorkerReceive....");
			context.getLogger().info("jobType->" + Utility.getJobName(Constants.waiveBatchJobType));
			context.getLogger().info("SyncFileName->" + syncFileName);
			/***** START LOOP ******/
			// 1.Rename file.sync to .dat
			String fileDatExtName = syncFileName.replace(".sync", ".dat");
			BigDecimal batchID = null;
			// 2. Find Batch ID by fileDatExtName
			CLBatch batchDB = new CLBatch(context.getLogger());
			CLBatchInfo result = batchDB.getBatchInfoByFileName(Constants.batchInprogressStatus, fileDatExtName);
			String username = Utility.getusername(Constants.waiveBatchJobType);
			if (result != null) {
				batchID = result.getBatchId();
				batchDB.updateInboundReceiveStatus(Constants.batchReceiveStatus, batchID, syncFileName, username);

			} else {
				throw new Exception("Not Find Batch ID : " + fileDatExtName);
			}
			// 2. read .dat
			int recordNum = 1;
			HashMap<BigDecimal, String> treatmentIDlist = new HashMap<BigDecimal, String>();
			for(int i=1;i<10;i++){
				String dataContent = "";/// ?????????????????อ่านในไฟล์แหละ
				if (!ValidateUtil.isNull(dataContent)) {
					String[] dataContentArr = dataContent.split(Constants.PIPE);
					if (dataContentArr != null && dataContentArr.length > 0) {
						// 3.Find Batch ID & Update Batch to Receive
						// Status from header file
						if (dataContent.indexOf("01|") != -1) {
							context.getLogger().info("Header Data : " + dataContent);
						} else if (dataContent.indexOf("02|") != -1) {
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
									request.setActionStatus(Constants.actSuccessStatus);
									request.setAdjStatus(Constants.adjCompleteStatus);
								} else {
									request.setActionStatus(Constants.actFailStatus);
									request.setAdjStatus(Constants.adjFailStatus);
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
									context.getLogger().info("Not Found Invoice ID or BatchAdjDtlID in Invoice Numb :"
											+ request.getInvoiceNumb());
								}

							} else {
								throw new Exception("File Wrong format body " + recordNum + ": " + dataContent);
							}
							recordNum++;
						} else if (dataContent.indexOf("09|") != -1) {
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

			//Update Treatement
			// 5.Update Treatment by Treatment ID
			if (treatmentIDlist != null && treatmentIDlist.size() > 0) {
				for (BigDecimal key : treatmentIDlist.keySet()) {
					CLWaive tbl = new CLWaive(context.getLogger());
					// Select Action Status by TREATMENT_ID
					CLWaiveInfoResponse waiveResult = tbl.getWaiveTreatementInfoByTreatmentID(key);
					if (waiveResult != null && waiveResult.getResponse() != null
							&& waiveResult.getResponse().size() > 0) {
						boolean successFlag = false;
						boolean failFlag = false;
						boolean incomplete = false;
						for (int i = 0; i < waiveResult.getResponse().size(); i++) {
							CLWaiveTreatementInfo waiveTreat = waiveResult.getResponse().get(i);
							if (waiveTreat.getActStatus() == Constants.actSuccessStatus) {
								successFlag = true;
							} else if (waiveTreat.getActStatus() == Constants.actFailStatus) {
								failFlag = true;
							} else {
								incomplete = true;
							}
						}
						/* Summary Status */
						int treatResult = 0;
						if (incomplete) {
							treatResult = Constants.treatIncompleteStatus;
						} else if (failFlag) {
							treatResult = Constants.treatFailStatus;
						} else {
							treatResult = Constants.treatSuccessStatus;
						}

						/* Update Treatment */
						CLTreatment treatmentDB = new CLTreatment(context.getLogger());
						treatmentDB.updateTreatmentReceive(treatResult, key, username,"");
					} else {
						context.getLogger().info("not found treatment");
					}

				}
			} else {
				context.getLogger().info("treatmentIDlist size =0");
			}
			
			/*6. Update Batch Status to Complete*/
			batchDB.updateInboundCompleteStatus(batchID, username);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		context.getLogger().debug("End Read File....");
	}

	public UpdateResultWaiveBatchBean findInvoiceIDAndBatchDtlID(UpdateResultWaiveBatchBean request) {
		PMInvoice pmInvoiceDB = new PMInvoice(context.getLogger());
		PMInvoiceInfoResponse pmInvoiceResult = pmInvoiceDB.getInvoiceIDByInvoiceNum(request.getInvoiceNumb());
		if (pmInvoiceResult != null && pmInvoiceResult.getResponse() != null
				&& pmInvoiceResult.getResponse().size() > 0) {
			if (pmInvoiceResult.getResponse().size() == 1) {
				request.setInvoiceID(pmInvoiceResult.getResponse().get(1).getInvoiceID());
				PMBatchAdjDtl pmBatchAdjDtlDB = new PMBatchAdjDtl(context.getLogger());
				PMBatchAdjInfoResponse pmBatchAdjResult = pmBatchAdjDtlDB.getBatchDtlIDByInvoiceID(
						pmInvoiceResult.getResponse().get(1).getInvoiceID(), request.getAmount(),
						request.getAdjStatus());
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

	public CLWaiveTreatementInfo updateWaive(UpdateResultWaiveBatchBean request, String username) {
		/**********************
		 * 1.Get Record Inprocess 2.Update Waive Success Status
		 */
		CLWaiveTreatementInfo waiveInfo = null;
		CLWaive tbl = new CLWaive(context.getLogger());

		waiveInfo = tbl.getWaiveTreatementInfo(request.getBaNo(), request.getBatchID(), request.getInvoiceID(),
				Constants.actInprogressStatus);
		if (waiveInfo != null) {
			tbl.updateWaiveStatus(request.getBaNo(), request.getBatchID(), request.getInvoiceID(),
					request.getBatchAdjDtlID(), request.getActionStatus(), request.getFailReason(), username);
		} else {
			context.getLogger().info("no orderInfo -> " + request.toString());
		}
		return waiveInfo;
	}

}
