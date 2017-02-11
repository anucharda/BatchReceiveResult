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
import th.co.ais.cpac.cl.batch.db.CLOrder;
import th.co.ais.cpac.cl.batch.db.CLOrder.CLOrderTreatementInfo;
import th.co.ais.cpac.cl.batch.db.PMBatchAdjDtl;
import th.co.ais.cpac.cl.batch.db.PMBatchAdjDtl.PMBatchAdjInfoResponse;
import th.co.ais.cpac.cl.batch.db.PMInvoice;
import th.co.ais.cpac.cl.batch.db.PMInvoice.PMInvoiceInfoResponse;
import th.co.ais.cpac.cl.batch.template.PMProcessTemplate;
import th.co.ais.cpac.cl.batch.util.Utility;
import th.co.ais.cpac.cl.batch.util.ValidateUtil;
import th.co.ais.cpac.cl.common.Context;
import th.co.ais.cpac.cl.common.UtilityLogger;
import th.co.ais.cpac.cl.template.database.DBConnectionPools;

public class UpdateWaiveBatchResultProcess extends PMProcessTemplate {
	@Override
	protected String getPathDatabase() {
		// TODO Auto-generated method stub
		return Constants.cldbConfPath;
	}
	@Override
	protected String getPMPathDatabase() {
		// TODO Auto-generated method stub
		return Constants.pmdbConfPath;
	}

	public void executeProcess(Context context, String syncFileName) { // suspendJobType=S,terminateJobType=T,reconnectJobType=R
		execute();
		readFile(context, syncFileName);
	}

	public void readFile(Context context, String syncFileName) {
		// อ่าน file ทีละ record
		// เช็คว่า record แรกของไฟล์เป็น order type ไหน
		// จบไฟล์ค่อย update treatement โดย grouping ตาม BA,update batch ต่อ
		HashMap<BigDecimal, String> treatmentIDlist = null;

		try {
			context.getLogger().info("Start WorkerReceive....");
			context.getLogger().info("jobType->" +  Utility.getJobName(Constants.waiveBatchJobType));
			context.getLogger().info("SyncFileName->" + syncFileName);
			/***** START LOOP ******/
			// 1.Rename file.sync to .dat
			String fileDatExtName = syncFileName.replace(".sync", ".dat");
			BigDecimal batchID=null;
			// 2. Find Batch ID by fileDatExtName
			CLBatch batchDB = new CLBatch(context.getLogger());
			CLBatchInfo result = batchDB.getBatchInfoByFileName(Constants.batchInprogressStatus, fileDatExtName);
			String username = Utility.getusername(Constants.waiveBatchJobType);
			if (result != null) {
				batchID = result.getBatchId();
				batchDB.updateInboundReceiveStatus(Constants.batchReceiveStatus, batchID, fileDatExtName, username);

			} else {
				throw new Exception("Not Find Batch ID : " + fileDatExtName);
			}
			//2. read .dat
			int recordNum=1;
			while (true) {
				String dataContent = "";/// ?????????????????อ่านในไฟล์แหละ
				if (!ValidateUtil.isNull(dataContent)) {
					String[] dataContentArr = dataContent.split(Constants.PIPE);
					if (dataContentArr != null && dataContentArr.length > 0) {
						// 3.Find Batch ID & Update Batch to Receive
						// Status from header file
						if (dataContent.indexOf("01|") != -1 ) {
							context.getLogger().info("Header Data : "+dataContent);
						} else if (dataContent.indexOf("02|") != -1) {
							// 4.Read Body File
							// 4.1 Read per record
							if (dataContentArr.length == 19) {
								UpdateResultWaiveBatchBean request=new UpdateResultWaiveBatchBean();
								request.setBaNo(dataContentArr[3]);
								request.setBatchID(batchID);
								request.setInvoiceNumb(dataContentArr[4]);
								request.setAmount(new BigDecimal(dataContentArr[15]));
								request.setFileName(syncFileName);
								if("Y".equals(dataContentArr[16])){
									request.setActionStatus(Constants.actSuccessStatus);
									request.setAdjStatus(Constants.adjCompleteStatus);
								}else{
									request.setActionStatus(Constants.actFailStatus);
									request.setAdjStatus(Constants.adjFailStatus);
									request.setFailReason(dataContentArr[17] + ":" + dataContentArr[18]);
								}
								// 4.2 Find INVOICE_ID
								request=findInvoiceIDAndBatchDtlID(request);
								if(request.getInvoiceID()!=null&&request.getBatchAdjDtlID()!=null){
									
								}else{
									context.getLogger().info("Not Found Invoice ID or BatchAdjDtlID in Invoice Numb :"+request.getInvoiceNumb());
								}
								
							}else{
								throw new Exception(
										"File Wrong format body " + recordNum + ": " + dataContent);
							}
							
							
							recordNum++;
						} else if (dataContent.indexOf("09|") != -1) {
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

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		context.getLogger().debug("End Read File....");
	}
	public UpdateResultWaiveBatchBean findInvoiceIDAndBatchDtlID(UpdateResultWaiveBatchBean request) {
		PMInvoice pmInvoiceDB=new PMInvoice(context.getLogger());
		PMInvoiceInfoResponse pmInvoiceResult=pmInvoiceDB.getInvoiceIDByInvoiceNum(request.getInvoiceNumb());
		if(pmInvoiceResult!=null&&pmInvoiceResult.getResponse()!=null&&pmInvoiceResult.getResponse().size()>0){
			if(pmInvoiceResult.getResponse().size()==1){
				request.setInvoiceID(pmInvoiceResult.getResponse().get(1).getInvoiceID());
				PMBatchAdjDtl pmBatchAdjDtlDB=new PMBatchAdjDtl(context.getLogger());
				PMBatchAdjInfoResponse pmBatchAdjResult = pmBatchAdjDtlDB.getBatchDtlIDByInvoiceID(pmInvoiceResult.getResponse().get(1).getInvoiceID(),request.getAmount(),request.getAdjStatus());
				if(pmBatchAdjResult!=null&&pmBatchAdjResult.getResponse()!=null&&pmBatchAdjResult.getResponse().size()>0){
					if(pmBatchAdjResult.getResponse().size()==1){
						request.setBatchAdjDtlID(pmBatchAdjResult.getResponse().get(1).getBatchDtlID());
					}else{
						context.getLogger().info("More than record Invoice ID :"+pmInvoiceResult.getResponse().get(1).getInvoiceID());
					}
				}else{
					context.getLogger().info("No record Invoice ID :"+pmInvoiceResult.getResponse().get(1).getInvoiceID());
				}
			}else{
				context.getLogger().info("More than record Invoice Numb :"+request.getInvoiceNumb());
			}
		}else{
			context.getLogger().info("No record Invoice Numb :"+request.getInvoiceNumb());
		}
		return request;
	}

	public CLOrderTreatementInfo updateOrder(UpdateResultSSFBean request, String username) {
		/**********************
		 * 1.Get Record Inprocess 2.Update Order Success Status
		 */
		CLOrderTreatementInfo orderInfo = null;
		CLOrder tbl = new CLOrder(context.getLogger());
		// Criteria ORDER_ACTION_ID = Suspend,Reconnect,Terminate Action_status=
		// inprocess
		orderInfo = tbl.getOrderTreatementInfo(request.getMobileNo(), request.getBatchID(),
				Constants.actInprogressStatus);
		if (orderInfo != null) {
			tbl.updateOrderStatus(request.getMobileNo(), request.getBatchID(), request.getActionStatus(),
					request.getSffOrderNo(), request.getFailReason(), username);
		} else {
			System.out.println("no orderInfo -> " + request.toString());
		}
		return orderInfo;
	}

	public CLOrderTreatementInfo updateSuspendOrderFail(UpdateResultSSFBean request, Context context) {
		CLOrderTreatementInfo orderInfo = null;
		return orderInfo;
	}
	
	public boolean pmInitial() {
		String fileConfig=null;
		 if (getPathDatabase() != null) {
			 fileConfig = getPathDatabase();
		 }

		if (fileConfig == null) {
			System.out.println("File Configuration not found.");
			return false;
		}

		File f = new File(fileConfig);
		if (!f.isFile() || !f.canRead()) {
			System.out.println("File configuration can read.");
			return false;
		}
		database = new CNFDatabase(fileConfig);

		context = new Context();
		context.initailLogger("LoggerMasterBatchInfo", System.currentTimeMillis() + "");

		DBConnectionPools<CNFDatabase, UtilityLogger> pool = new DBConnectionPools<>(database, context.getLogger());
		pool.buildeDataSource();

		if (!pool.poolActive()) {
			System.out.println("Database connection pool error.");
			return false;
		}
		return true;
	}


}
