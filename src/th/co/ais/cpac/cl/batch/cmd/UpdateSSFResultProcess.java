package th.co.ais.cpac.cl.batch.cmd;

import java.util.ArrayList;
import java.util.Date;

import th.co.ais.cpac.cl.batch.Constants;
import th.co.ais.cpac.cl.batch.bean.UpdateResultSSFBean;
import th.co.ais.cpac.cl.batch.db.CLOrder;
import th.co.ais.cpac.cl.batch.db.CLOrder.CLOrderInfo;
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
				//for loop file ทำทีละ record และเก็บ list BA ไว้เพื่อ update ตอนสิ้น
				 boolean fileSuccess=true;
				 ArrayList baNoList=new ArrayList();

				 if(fileSuccess){
					 for(int i=0;i<2;i++){
						 UpdateResultSSFBean request=new UpdateResultSSFBean();
						 request.setMobileNo("0899010514");
						 request.setSffOrderNo("123456");
						 request.setOrderType("Suspend-Debt"); //example suspense-debt...
						 request.setSuspendType("Outgoing");//outgoing	
						 request.setActionID(getActionID(orderType));
						 CLOrderInfo orderInfo=updateSuspendOrderSuccess(request,context);
						 if(orderInfo!=null){
							 baNoList.add(orderInfo.getBaNumber());
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
						 CLOrderInfo orderInfo=updateSuspendOrderFail(request,context);
						 if(orderInfo!=null){
							 baNoList.add(orderInfo.getBaNumber());
						 }
					 }
				 }
				 
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
	
	public  CLOrderInfo updateSuspendOrderSuccess(UpdateResultSSFBean request,Context context){
		/**********************
		1.Get Record Inprocess
		2.Update Order Success Status
		*/
		CLOrderInfo orderInfo=null;
		CLOrder tbl = new CLOrder(context.getLogger());
		orderInfo=tbl.getOrderInfo(request.getMobileNo(),request.getActionID()); 
		if(orderInfo!=null){
			tbl.updateOrderStatus(orderInfo.getOrderId(), Constants.inboundSuccessStatus, request.getSffOrderNo(), request.getFailReason(),"updateSuspendOrderSuccess");
		}
		return orderInfo;
	}
	public  CLOrderInfo updateSuspendOrderFail(UpdateResultSSFBean request,Context context){
		CLOrderInfo orderInfo=null;
		return orderInfo;
	}


}
