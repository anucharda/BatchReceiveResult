package th.co.ais.cpac.cl.batch.cmd.updatesffresult;

import th.co.ais.cpac.cl.common.Context;

public class ReceiveSFFWorker {

	public static void main(String[] args) {
		 Context context = new Context();
		 context.initailLogger("LoggerReceiveSFFWorker", "XXXX|YYYYY");
		// TODO Auto-generated method stub
		 context.getLogger().info("Start ReceiveSFFWorker....");
		//suspendJobType=S,terminateJobType=T,reconnectJobType=R
		 String jobType="";//From Parameter
		 String syncFileName="";//Sync File Name
		 execute(context,jobType,syncFileName);///?????????????????
		 context.getLogger().info("End ReceiveSFFWorker....");
	}
	
	public static void execute(Context context,String jobType,String syncFileName){
		 context.getLogger().info("Start UpdateSFFResultProcess Execute....");
		 //Check จากชื่อไฟล์ว่าเป็น batch suspense/reconnect/terminate
		
		 context.getLogger().info("Trigger Update SSF Result Process....");
		 new UpdateSFFResultProcess().executeProcess(context,jobType,syncFileName); ///?????????????????
		 context.getLogger().info("End UpdateSFFResultProcess Execute....");
	}
	

}
