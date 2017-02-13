package th.co.ais.cpac.cl.batch.cmd.updatesffresult;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import th.co.ais.cpac.cl.batch.util.FileUtil;
import th.co.ais.cpac.cl.batch.util.PropertiesReader;
import th.co.ais.cpac.cl.common.Context;

public class ReceiveSFFWorker {

	public static void main(String[] args) {
		try{
			Context context = new Context();
			context.initailLogger("LoggerReceiveSFFWorker", "XXXX|YYYYY");
			// TODO Auto-generated method stub
			context.getLogger().info("----------------------- Start ReceiveSFFWorker -----------------------");
			context.getLogger().info("Load configure....");
			PropertiesReader reader = new PropertiesReader("th.co.ais.cpac.cl.batch","SystemConfigPath");
			//suspendJobType=S,terminateJobType=T,reconnectJobType=R
			String jobType=args[0];//From Parameter
		
			//Find sync file on path.
			String inboundPath = reader.get("inboundPath");
			String processPath = reader.get("processPath");
			File[] files = FileUtil.getAllFilesThatMatchFilenameExtensionAscendingOrder(inboundPath, "sync");
			context.getLogger().info("Sync file size --> "+files.length);
			String syncFileName = "";
			if(files!=null && files.length>0){
				String syncFile = files[0].getPath();
				syncFileName = files[0].getName();
				context.getLogger().info("Sync file is --> "+syncFile);
				String sb = FileUtil.readFile(syncFile);
				context.getLogger().info("All files name in sync file--> "+sb.toString());
				if(!"".equals(sb)){
					String[] fileNames = sb.toString().split("\\|");
					//Loop for copy file from inbound to process directory.
					for(int i=0; i<fileNames.length; i++){
						File source = new File(inboundPath+"/"+fileNames[i]);
						if(source.exists()){
							File dest = new File(processPath+"/"+fileNames[i]);
							FileUtil.copyFile(source, dest);
							context.getLogger().info("Copy file to process directory successed --> "+fileNames[i]);
							//Delete source file after copy to process directory completed.
							if(source.delete()){
								context.getLogger().info("Delete file from inbound directory successed --> "+source.getParent());
							}else{
								throw new Exception("Error occur delete file from inbound directory --> "+source.getParent());
							}
						}else{
							//Skip to next file.
							continue;
						}
					}
					//Copy sync file to process directory.
					File sourceSyncFile = new File(syncFile);
					File destSyncFile = new File(processPath+"/"+syncFileName);
					FileUtil.copyFile(sourceSyncFile, destSyncFile);
					//Delete source file after copy to process directory completed.
					if(sourceSyncFile.delete()){
						context.getLogger().info("Delete file from inbound directory successed --> "+sourceSyncFile.getParent());
					}else{
						throw new Exception("Error occur delete file from inbound directory --> "+sourceSyncFile.getParent());
					}
					
					execute(context, jobType, destSyncFile.getPath());
				}else{
					context.getLogger().info("Not found content in sync file --> "+syncFile);
				}
			}else{
				context.getLogger().info("Not found sync file in directory --> "+inboundPath);
			}
			
			context.getLogger().info("----------------------- End ReceiveSFFWorker ----------------------- ");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void execute(Context context,String jobType,String syncFileName){
		 context.getLogger().info("Start UpdateSFFResultProcess Execute....");
		 context.getLogger().info("Trigger Update SSF Result Process....");
		 new UpdateSFFResultProcess().executeProcess(context,jobType,syncFileName); ///?????????????????
		 context.getLogger().info("End UpdateSFFResultProcess Execute....");
	}
	
	
}