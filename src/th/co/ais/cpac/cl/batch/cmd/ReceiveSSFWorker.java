package th.co.ais.cpac.cl.batch.cmd;

import th.co.ais.cpac.cl.common.Context;

public class ReceiveSSFWorker {

	public static void main(String[] args) {
		 Context context = new Context();
		 context.initailLogger("LoggerWorkerReceive", "XXXX|YYYYY");
		// TODO Auto-generated method stub
		 context.getLogger().debug("Start WorkerReceive....");
		//suspendJobType=S,terminateJobType=T,reconnectJobType=R
		 String jobType="";//From Parameter
		 execute(context,jobType);
		 context.getLogger().debug("End WorkerReceive....");
	}
	
	public static void execute(Context context,String jobType){
		 context.getLogger().debug("Start Execute....");
		 //Check จากชื่อไฟล์ว่าเป็น batch suspense/reconnect/terminate
		
		 context.getLogger().debug("Trigger Update SSF Result Process....");
		 new UpdateSSFResultProcess().executeProcess(context,jobType);
		 context.getLogger().debug("End Execute....");
	}
	

}
