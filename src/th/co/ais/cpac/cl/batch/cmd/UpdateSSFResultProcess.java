package th.co.ais.cpac.cl.batch.cmd;

import java.math.BigDecimal;
import java.util.HashMap;

import th.co.ais.cpac.cl.batch.Constants;
import th.co.ais.cpac.cl.batch.bean.UpdateResultSSFBean;
import th.co.ais.cpac.cl.batch.db.CLBatch;
import th.co.ais.cpac.cl.batch.db.CLBatch.CLBatchInfo;
import th.co.ais.cpac.cl.batch.db.CLOrder;
import th.co.ais.cpac.cl.batch.db.CLOrder.CLOrderInfoResponse;
import th.co.ais.cpac.cl.batch.db.CLOrder.CLOrderTreatementInfo;
import th.co.ais.cpac.cl.batch.db.CLTreatment;
import th.co.ais.cpac.cl.batch.template.ProcessTemplate;
import th.co.ais.cpac.cl.batch.util.ValidateUtil;
import th.co.ais.cpac.cl.common.Context;
public class UpdateSSFResultProcess extends ProcessTemplate{
	@Override
	protected String getPathDatabase() {
		// TODO Auto-generated method stub
		  return "C:\\cpac\\database.properties";
	}
	public void executeProcess(Context context,String jobType){ //suspendJobType=S,terminateJobType=T,reconnectJobType=R
		execute();
		readFile(context,jobType);
	}
	
	
	public void readFile(Context context,String jobType){
		 //อ่าน file ทีละ record 
		 //เช็คว่า record แรกของไฟล์เป็น order type ไหน
		 //จบไฟล์ค่อย update treatement โดย grouping ตาม BA,update batch ต่อ

		 try{
			 if(Constants.suspendJobType.equals(jobType)||Constants.terminateJobType.equals(jobType)||Constants.reconnectJobType.equals(jobType)){
				
				 /*****START LOOP******/
				 //1.for loop file ใน Sync
				 //2.อ่านไฟล์ ok และ .err เพื่อหา BATCH_ID โดยดึงจาก record แรกของไฟล์ 01|DMSRequest_20150817_232541.dat ตัดไฟล์ DMSRequest_20150817_232541.dat เป็นค่า fileName
				 String successFileName=""; //ได้จากข้อ 2
				 String failFileName="";
				 String fileName="";
				 if(ValidateUtil.isNull(successFileName)){
					 fileName=successFileName;
					 System.out.println("Error not found batch ID");
				 }else if(ValidateUtil.isNull(failFileName)){
					 fileName=failFileName;
				 }else if(ValidateUtil.isEqual(successFileName, failFileName)){
					 fileName=successFileName;
				 }else{
					 System.out.println("Error not found batch ID");
					  throw new Exception();
				 }

				 //3.Find BATCH_ID From File Name -> ที่ INBOUND_STATUS =1 (pending)				
				 CLBatch batchDB=new CLBatch(context.getLogger());
				 CLBatchInfo result= batchDB.getBatchInfoByFileName(Constants.batchInprogressStatus, fileName);
				 String username=getusername(jobType);
				 if(result!=null)
				 {
					 //4. Update CL_BATCH INBOUND_STATUS = 2 (Received) 
					 batchDB.updateInboundReceiveStatus(Constants.batchReceiveStatus, result.getBatchId(), successFileName+fileName, username);
					 HashMap<BigDecimal,String> treatmentIDlist=new HashMap<BigDecimal,String> ();
					 for(int i=0;i<10;i++){//for loop file ใน sync
						 for(int j=0;j<10;j++){ //for loop file 
							 String filenameDoProcess="";
							 boolean fileSuccess=true;
							
							 if("err".equals(filenameDoProcess)){//5. Check ว่าอันนีเป็นไฟล์ Success หรือ Fail 
								 fileSuccess=false;
							 }
							 for(int k=0;k<2;k++){
								 /* ค่าที่อ่านจากไฟล์*/
								 UpdateResultSSFBean request=new UpdateResultSSFBean();
								 request.setMobileNo("0800901088"); /* ค่าที่อ่านจากไฟล์*/
								 request.setSffOrderNo("123456"); /* ค่าที่อ่านจากไฟล์*/
								 request.setOrderType("Suspend-Debt"); /* ค่าที่อ่านจากไฟล์*/
								 request.setSuspendType("Outgoing"); /* ค่าที่อ่านจากไฟล์*/
								 if(fileSuccess){
									 request.setFileName(successFileName);
									 request.setActionStatus(Constants.actSuccessStatus);
								 }else{
									 request.setFileName(failFileName);
									 request.setFailReason("error desc"); /* ค่าที่อ่านจากไฟล์*/
									 request.setActionStatus(Constants.actFailStatus);
								 }
								 /* ค่าที่อ่านจากไฟล์*/
								 request.setActionID(getActionID(jobType));
								 request.setBatchID(result.getBatchId());
								 CLOrderTreatementInfo orderInfo=updateOrder(request,context,username);
								 if(orderInfo!=null){
									 treatmentIDlist.put(orderInfo.getTreatementId(),"");
								 }
							 }			
						 }
					 }
					 
					 /*****Update Treatment by Treatment ID*****/
					 for (BigDecimal key : treatmentIDlist.keySet()) {
						 CLOrder tbl = new CLOrder(context.getLogger());
						 //Select Action Status by TREATMENT_ID
						 CLOrderInfoResponse orderResult=tbl.getOrderTreatementInfoByTreatmentID(key);
						 if(result!=null&&orderResult.getResponse()!=null&&orderResult.getResponse().size()>0){
							 boolean successFlag=false;
							 boolean failFlag=false;
							 boolean incomplete=false;
							 for(int i=0;i<orderResult.getResponse().size();i++){
								 CLOrderTreatementInfo orderTreat=orderResult.getResponse().get(i);
								 if(orderTreat.getActStatus()==Constants.actSuccessStatus){
									 successFlag=true;
								 }else if(orderTreat.getActStatus()==Constants.actFailStatus){
									 failFlag=true;
								 }else{
									 incomplete=true;
								 }
							 }
							 /*Summary Status*/
							 int treatResult=0;
							 if(incomplete){
								 treatResult=Constants.treatIncompleteStatus;
							 }else if(failFlag){
								 treatResult=Constants.treatFailStatus;
							 }else{
								 treatResult=Constants.treatSuccessStatus;
							 }
							 
							 /*Update Treatment*/
							 CLTreatment treatmentDB=new CLTreatment(context.getLogger());
							 treatmentDB.updateTreatmentReceive(treatResult, key, username);
							 /*Update Treatment*/
							 
						 }else{
							 System.out.println("Not Found Treatement");
						 }
					 }
					 /*****Update Treatment by Treatment ID*****/
					 /*****Update Batch Success*****/
					 batchDB.updateInboundCompleteStatus(result.getBatchId(), username);
					 /*****Update Batch Success*****/
					 
				 }else{
					 throw new Exception();
				 }
			 }
		 }catch(Exception e){
			 e.printStackTrace();                         
		 }finally{
			 
		 }
		 context.getLogger().debug("End Read File....");
	}
	
	private int getActionID(String orderType){
		if(Constants.suspendJobType.equals(orderType)){
			return Constants.suspendOrderActionID;
		}else if(Constants.terminateJobType.equals(orderType)){
			return Constants.terminateOrderActionID;
		}else{
			return Constants.reconnectOrderID;
		}
	}
	private String getusername(String jobType){
		if(Constants.suspendJobType.equals(jobType)){
			return Constants.suspendUsername;
		}else if(Constants.terminateJobType.equals(jobType)){
			return Constants.terminateUsername;
		}else{
			return Constants.reconnectUsername;
		}
	}
	public  CLOrderTreatementInfo updateOrder(UpdateResultSSFBean request,Context context,String username){
		/**********************
		1.Get Record Inprocess
		2.Update Order Success Status
		*/
		CLOrderTreatementInfo orderInfo=null;
		CLOrder tbl = new CLOrder(context.getLogger());
		//Criteria ORDER_ACTION_ID = Suspend,Reconnect,Terminate Action_status= inprocess
		orderInfo=tbl.getOrderTreatementInfo(request.getMobileNo(),request.getBatchID(),Constants.actInprogressStatus); 
		if(orderInfo!=null){
			tbl.updateOrderStatus( request.getMobileNo(),request.getBatchID(),request.getActionStatus(), request.getSffOrderNo(), request.getFailReason(),username);
		}else{
			System.out.println("no orderInfo -> "+request.toString());
		}
		return orderInfo;
	}
	public  CLOrderTreatementInfo updateSuspendOrderFail(UpdateResultSSFBean request,Context context){
		CLOrderTreatementInfo orderInfo=null;
		return orderInfo;
	}


}
