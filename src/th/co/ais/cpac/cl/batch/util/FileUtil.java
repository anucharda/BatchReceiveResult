package th.co.ais.cpac.cl.batch.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;

public class FileUtil {
	
	@SuppressWarnings("unchecked")
	public static File[] getAllFilesThatMatchFilenameExtensionAscendingOrder(String path, String fileExtension) throws Exception{
		File dir = new File(path);
		File[] files = dir.listFiles((d, name) -> name.endsWith("."+fileExtension));
		Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_COMPARATOR);
		return files;
	}
	
	public static void copyFile(File source, File dest) throws Exception {
	    FileUtils.copyFile(source, dest);
	}
	
	public static String readFile(String path) throws Exception{
		//Read file sync and move file to process.
		BufferedReader br = null;
		StringBuffer sb = new StringBuffer();
		try{
			br = new BufferedReader(new FileReader(path));
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				sb.append(sCurrentLine);
				sb.append("|");
			}
		}finally{
			if(br!=null)
				br.close();
		}
		return sb.toString();
	}
}
