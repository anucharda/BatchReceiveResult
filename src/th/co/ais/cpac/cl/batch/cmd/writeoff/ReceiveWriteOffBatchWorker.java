package th.co.ais.cpac.cl.batch.cmd.writeoff;

import java.io.File;

import th.co.ais.cpac.cl.batch.Constants;
import th.co.ais.cpac.cl.batch.util.FileUtil;
import th.co.ais.cpac.cl.batch.util.PropertiesReader;
import th.co.ais.cpac.cl.common.Context;

public class ReceiveWriteOffBatchWorker {

	public static void main(String[] args) {
		try{
			 Context context = new Context();
				context.initailLogger("LoggerReceiveSFFWorker", "XXXX|YYYYY");
				// TODO Auto-generated method stub
				context.getLogger().info("----------------------- Start ReceiveWriteOffBatchWorker -----------------------");
				context.getLogger().info("Load configure....");
				PropertiesReader reader = new PropertiesReader("th.co.ais.cpac.cl.batch", "SystemConfigPath");
				// suspendJobType=S,terminateJobType=T,reconnectJobType=R
				String jobType = args[0];// From Parameter
				boolean doProcess=true;
				String inboundAckPath="" ;
				String inboundDataPath="" ;
				String processPath="";
				if(Constants.writeOffJobType.equals(jobType)){
					inboundAckPath= reader.get("writeOff.inboundAckPath");
					inboundDataPath =reader.get("writeOff.inboundDataPath");
					processPath = reader.get("writeOff.processPath");
				}else{
					doProcess=false;
					context.getLogger().info("not found job type : "+jobType);
				}
				if(doProcess){
					File[] files = FileUtil.getAllFilesThatMatchFilenameExtensionAscendingOrder(inboundAckPath, "sync");
					context.getLogger().info("Ack file size --> "+files.length);
					if(files!=null && files.length>0){
						//Loop for do all sync file.
						for(int i=0; i<files.length; i++){
							String doPath = processPath;
							String ackFile = files[i].getPath();
							context.getLogger().info("Ack file is --> "+ackFile);
							String ackFileName = files[i].getName();
							String dataFileName = ackFileName.replace(".ack", ".dat");
							context.getLogger().info("Data File Name --> "+dataFileName);
							File source = new File(inboundDataPath+"/"+dataFileName);
							if(source.exists()){
								File dest = new File(processPath+"/"+dataFileName);
								FileUtil.copyFile(source, dest);
								context.getLogger().info("Copy file to process directory successed --> "+dataFileName);
								//Delete source file after copy to process directory completed.
								if(source.delete()){
									context.getLogger().info("Delete file from inbound directory successed --> "+source.getParent());
								}else{
									throw new Exception("Error occur delete file from inbound directory --> "+source.getParent());
								}
							}
							File sourceAckFile = new File(ackFile);
							File destAckFile = new File(processPath+"/"+sourceAckFile);
							FileUtil.copyFile(sourceAckFile, destAckFile);
							//Delete source file after copy to process directory completed.
							if(sourceAckFile.delete()){
								context.getLogger().info("Delete file from inbound directory successed --> "+sourceAckFile.getParent());
							}else{
								throw new Exception("Error occur delete file from inbound directory --> "+sourceAckFile.getParent());
							}
							
							//New thread for execute process.
							new Thread ( () -> execute(context, doPath, ackFileName, dataFileName) ).start();
						}
					}else{
						context.getLogger().info("Not found ack file in directory --> "+inboundAckPath);
					}
				}
		
				context.getLogger().info("End ReceiveWriteOffBatchWorker....");
				
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void execute(Context context,String processPath,String ackFileName,String inboundFileName){
		 context.getLogger().info("Start UpdateWriteOffResultProcess Execute....");		
		 context.getLogger().info("Trigger UpdateWriteOffResultProcess....");
		 new UpdateWriteOffResultProcess().executeProcess(context,processPath,ackFileName,inboundFileName); 
		 context.getLogger().info("End UpdateWriteOffResultProcess Execute....");
	}
	

}
