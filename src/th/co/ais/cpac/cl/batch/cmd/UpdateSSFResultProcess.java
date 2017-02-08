package th.co.ais.cpac.cl.batch.cmd;

import java.math.BigDecimal;
import java.util.HashMap;

import th.co.ais.cpac.cl.batch.Constants;
import th.co.ais.cpac.cl.batch.bean.UpdateResultSSFBean;
import th.co.ais.cpac.cl.batch.db.CLOrder;
import th.co.ais.cpac.cl.batch.db.CLOrder.CLOrderInfoResponse;
import th.co.ais.cpac.cl.batch.db.CLOrder.CLOrderTreatementInfo;
import th.co.ais.cpac.cl.batch.template.ProcessTemplate;
import th.co.ais.cpac.cl.common.Context;
public class UpdateSSFResultProcess extends ProcessTemplate{
	@Override
	protected String getPathDatabase() {
		// TODO Auto-generated method stub
		  return "C:\\cpac\\database.properties";
	}
	public void executeProcess(Context context){
		execute();
		readFile(context);
	}
	
	
	public void readFile(Context context){
		 //อ่าน file ทีละ record 
		 //เช็คว่า record แรกของไฟล์เป็น order type ไหน
		 //จบไฟล์ค่อย update treatement โดย grouping ตาม BA,update batch ต่อ
		 
		 String orderType=Constants.suspendOrderType;//Dummy
		 
		 try{
			 if(Constants.suspendOrderType.equals(orderType)||Constants.reconnectType.equals(orderType)||Constants.terminateOrderType.equals(orderType)){
				
				 /*****START LOOP******/
				 //for loop file ใน Sync
				 //for loop file ทำทีละ record และเก็บ list BA ไว้เพื่อ update ตอนสิ้น
				 boolean fileSuccess=true;
				 HashMap<BigDecimal,BigDecimal> batchIDList=new HashMap<BigDecimal,BigDecimal> ();
				 HashMap<BigDecimal,BigDecimal> treatmentIDlist=new HashMap<BigDecimal,BigDecimal> ();
				 if(fileSuccess){
					 for(int i=0;i<2;i++){
						 UpdateResultSSFBean request=new UpdateResultSSFBean();
						 request.setMobileNo("0800901088");
						 request.setSffOrderNo("123456");
						 request.setOrderType("Suspend-Debt"); //example suspense-debt...
						 request.setSuspendType("Outgoing");//outgoing	
						 request.setActionID(getActionID(orderType));
						 CLOrderTreatementInfo orderInfo=updateSuspendOrderSuccess(request,context);
						 if(orderInfo!=null){
							 batchIDList.put(orderInfo.getBatchId(),orderInfo.getBatchId());
							 treatmentIDlist.put(orderInfo.getTreatementId(),orderInfo.getTreatementId());
						 }
					 }
				 }else{
					 for(int i=0;i<2;i++){
						 UpdateResultSSFBean request=new UpdateResultSSFBean();
						 request.setMobileNo("");
						 request.setSffOrderNo("");
						 request.setOrderType("");
						 request.setSuspendType("");
						 request.setFailReason("");
						 CLOrderTreatementInfo orderInfo=updateSuspendOrderFail(request,context);
						 if(orderInfo!=null){
							 batchIDList.put(orderInfo.getBatchId(),orderInfo.getBatchId());
							 treatmentIDlist.put(orderInfo.getTreatementId(),orderInfo.getTreatementId());
						 }
					 }
				 }				 
				 /*****END LOOP******/
				 /*****Update Treatment by Treatment ID*****/
				 for (BigDecimal key : treatmentIDlist.keySet()) {
					 CLOrder tbl = new CLOrder(context.getLogger());
					 //Select Action Status by TREATMENT_ID
					 CLOrderInfoResponse result=tbl.getOrderTreatementInfoByTreatmentID(treatmentIDlist.get(key));
					 if(result!=null&&result.getResponse()!=null&&result.getResponse().size()>0){
						 boolean successFlag=false;
						 boolean failFlag=false;
						 boolean incomplete=false;
						 for(int i=0;i<result.getResponse().size();i++){
							 CLOrderTreatementInfo orderTreat=result.getResponse().get(i);
							 if(orderTreat.getActStatus()==Constants.inboundSuccessStatus){
								 successFlag=true;
							 }else if(orderTreat.getActStatus()==Constants.inboundFailStatus){
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
						 
						 /*Update Treatment*/
						 
					 }else{
						 System.out.println("Not Found Treatement");
					 }
				
				 }

				 /*****Update Treatment*****/
			 }
		 }catch(Exception e){
			 e.printStackTrace();                         
		 }finally{
			 
		 }
		 context.getLogger().debug("End Read File....");
	}
	
	private int getActionID(String orderType){
		if(Constants.suspendOrderType.equals(orderType)){
			return Constants.suspendOrderActionID;
		}else if(Constants.terminateOrderType.equals(orderType)){
			return Constants.terminateOrderActionID;
		}else{
			return Constants.reconnectOrderID;
		}
	}
	
	public  CLOrderTreatementInfo updateSuspendOrderSuccess(UpdateResultSSFBean request,Context context){
		/**********************
		1.Get Record Inprocess
		2.Update Order Success Status
		*/
		CLOrderTreatementInfo orderInfo=null;
		CLOrder tbl = new CLOrder(context.getLogger());
		//Criteria ORDER_ACTION_ID = Suspend,Reconnect,Terminate Action_status= inprocess
		orderInfo=tbl.getOrderTreatementInfo(request.getMobileNo(),request.getActionID()); 
		if(orderInfo!=null){
			tbl.updateOrderStatus(orderInfo.getOrderId(), Constants.inboundSuccessStatus, request.getSffOrderNo(), request.getFailReason(),"updateSuspendOrderSuccess");
		}
		return orderInfo;
	}
	public  CLOrderTreatementInfo updateSuspendOrderFail(UpdateResultSSFBean request,Context context){
		CLOrderTreatementInfo orderInfo=null;
		return orderInfo;
	}


}
