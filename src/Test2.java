import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import th.co.ais.cpac.cl.batch.util.FileUtil;
import th.co.ais.cpac.cl.batch.util.PropertiesReader;

public class Test2 {

	public static void main(String[] args) {
		try{
			PropertiesReader reader = new PropertiesReader("th.co.ais.cpac.cl.batch.properties.resource","SystemConfigPath");
			String inboundPath = reader.get("inboundPath");
			String processPath = reader.get("processPath");
			File[] files = FileUtil.getAllFilesThatMatchFilenameExtensionAscendingOrder(inboundPath, "sync");
			System.out.println(files.length);
			String syncFileName = "";
			if(files!=null && files.length>0){
				String syncFile = files[0].getPath();
				syncFileName = files[0].getName();
				System.out.println("Sync file is --> "+syncFileName);
				BufferedReader br = new BufferedReader(new FileReader(syncFile));
				String sCurrentLine;
				StringBuffer sb = new StringBuffer();
				while ((sCurrentLine = br.readLine()) != null) {
					sb.append(sCurrentLine);
					sb.append("|");
				}
				String[] fileNames = sb.toString().split("\\|");
				//Loop for copy file from inbound to process directory.
				for(int i=0; i<fileNames.length; i++){
					File source = new File(inboundPath+"/"+fileNames[i]);
					File dest = new File(processPath+"/"+fileNames[i]);
					FileUtil.copyFile(source, dest);
					System.out.println("Copy file to process directory successed --> "+fileNames[i]);
					//Delete source file after copy to process directory completed.
					if(source.delete()){
						System.out.println("Delete file from inbound directory successed --> "+source.getParent());
					}else{
						throw new Exception("Error occur delete file from inbound directory --> "+source.getParent());
					}
				}
				//Copy sync file to process directory.
				File sourceSyncFile = new File(syncFile);
				File destSyncFile = new File(processPath+"/"+syncFileName);
				FileUtil.copyFile(sourceSyncFile, destSyncFile);
				//Delete source file after copy to process directory completed.
				if(sourceSyncFile.delete()){
					System.out.println("Delete file from inbound directory successed --> "+sourceSyncFile.getParent());
				}else{
					throw new Exception("Error occur delete file from inbound directory --> "+sourceSyncFile.getParent());
				}
				
				System.out.println("Sync file -->"+destSyncFile.getPath());
			}
		}catch(Exception e){
			e.printStackTrace();
		}

	}

}
