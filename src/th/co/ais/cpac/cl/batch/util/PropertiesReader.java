package th.co.ais.cpac.cl.batch.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

/**
 * The purpose of this class is read the key, value pair from a text file.
 * Because it's easy to manage and change the value. 
 * The classes that use this PropertyReader won't recompile when the value in ini file is changed
 *
 * Some rules :-
 *   - Program can change the comment sign and open/close group sign at the end of the file
 *   - If ini file doesn't define the group name, program will use DEFAULT_GROUP_NAME ("default") 
 *   as the default group name
 *   - If ini file contains the group name as same as DEFAULT_GROUP_NAME ("default"), 
 *   program will join those key and value into that group name
 *   - If any group contains the records of the same key, program will use
 *   the lastest value as the value of that key
 *   - If any group has the comment sign at the beginning of the group name, program will 
 *   ignore all of the key/value pair of that group. ex [#xx]
 *   - If any line of ini file which is not the group defination (not start and end with the open/group sign)
 *   doesn't contain the '=' sign, program will ignore this line
 *   - If any line start with '=', program will ignore this line. Although, there's value with it because
 *   program can not define the appropriate key for that value.
 *   - If any key/value contains no value,
 *     myKey = 
 *   program will use the empty string as the value (""). In this case, myKey = ""
 *   - Program will ignore the empty line, the comment line and other illegal line. 
 *   So if any key/value line and the group name line are separated by these ignore lines, this key/value line still in that group. 
 *   Unless they are separated by the another group name line.
 */

public class PropertiesReader
{
    private static final String CLOSE_GROUP_SIGN = "]";

    // Constant value
    private static final String COMMENT_SIGN = "#";

    private static final String DEFAULT_GROUP_NAME = "default";

    private static final String OPEN_GROUP_SIGN = "[";

    // Internal variables
    private File currentFile;

    private boolean isAutoReload = true;

    private HashMap mainTable;

    private long modifyTime;

    /**
     * Construct a PropertiesRead with default reading file (default.ini).
     * */
    public PropertiesReader()
    {
        currentFile = new File("default.ini");
    }

    /**
     * Construct a PropertiesReader with specified reading file.
     * */
    public PropertiesReader(File fFile)
    {
        currentFile = fFile;
    }
	
	/**
     * Construct a PropertiesReader with specified reading file.
     */
    public PropertiesReader(String fString)
    {
        currentFile = new File(fString);
    }
    
	/**
	 * Construct a PropertiesReader with reading file that from value in key of propertyPath.
     * @author Anuchard.a
     */
    public PropertiesReader(String propertyPath, String key)
    {
        ResourceBundle rb = ResourceBundle.getBundle(propertyPath);
        currentFile = new File(rb.getString(key));
    	//Utility.printCurrentTime();
    	//System.out.println("**** IPay System ----> Debug read file "+currentFile.toString());
        
    }


    /**
     * Get the value from the properties using key 
     * Assume that user want to get the value in default group.<br>
     * &nbsp;&nbsp;[default]<br>
     * &nbsp;&nbsp;key=value
     *
     * @param key will return value of this key in default group.
     * @return value of specified key in default group.
     * 
     * @throw FileNotFoundException - if file doesn't exist
     * @throw IOException - if an I/O error occurs
     */
    public String get(String key) throws FileNotFoundException, IOException
    {
        return get(DEFAULT_GROUP_NAME, key);
    }
    
    /**
     * Get the value from the properties using key and group name.<br>
     * &nbsp;&nbsp;[group]<br>
     * &nbsp;&nbsp;key=value
     * 
     * @param key
     * @param group 
     * @return value of specified key and group
     *
     * @throw FileNotFoundException - if file doen't exist
     * @throw IOException - if an I/O error occurs
     */
    public String get(String group, String key) throws FileNotFoundException, IOException
    {
        if (mainTable == null)
        {
        	//Utility.printCurrentTime();
        	//System.out.println("**** IPay System ----> Debug System Read file");
            read();
        }

        if (isAutoReload && currentFile.lastModified() > modifyTime)
        {
        	//Utility.printCurrentTime();
        	//System.out.println("**** IPay System ----> Debug System Auto Reload Properties file");
            read();
        }

        HashMap groupTable = (HashMap) mainTable.get(group);

        if (groupTable == null)
            return null;

        return (String) groupTable.get(key);
    }
    
    /**
     * Split multiple values into ArrayList.
     * Example of multiple values = value1,value2,value3
     * 
     * @param values
     * @return ArrayList of multiple values
     */
    public ArrayList getArrayList(String values)
    {
        try
        {
            if (values == null || values.equals(""))
            {
                return null;
            }
            StringTokenizer stk = new StringTokenizer(values, ",");
            ArrayList temp = new ArrayList();
            while (stk.hasMoreTokens())
            {
                temp.add(stk.nextToken().trim());
            }
            return temp;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    // Add by Tinnakorn 20110723
    /**
     * Return all group in properties file.
     * Example [default]... [group1]... It return list contains default, group1.
     * 
     * @return ArrayList of group name
     */
    public ArrayList getGroupNames()
    {
    	ArrayList listGroup = new ArrayList();
        try
        {
        	if (mainTable == null)
            {
                read();
            }
            java.util.Set groupNames = mainTable.keySet();
            Iterator it = groupNames.iterator();
            while(it.hasNext()){
            	listGroup.add(it.next());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        return listGroup;
    }
   

    /**
     * Return absolute path of current config file.
     * @return Absolute path of current config file
     */
    public String getFileName()
    {
        return currentFile.getAbsolutePath();
    }
    
    /**
     * Get all value in group from the properties using only group name.
     * 
     * @param group 
     * @return HashMap of all value in group.
     * @throw FileNotFoundException - if file doen't exist
     * @throw IOException - if an I/O error occurs
     */
    public HashMap<String, String> getDefaultGroup() throws FileNotFoundException, IOException
    {
    	//System.out.println(new Date() + "***** Begin Get method *****");
		//System.out.println(new Date() + "ModifyTime : " + modifyTime);
		//System.out.println(new Date() + "lastModified : " + currentFile.lastModified());
        if (mainTable == null)
        {
        	//Utility.printCurrentTime();
			//System.out.println(new Date() + "**** IPay System ----> mainTable");
            read();
        }

        if (isAutoReload && currentFile.lastModified() > modifyTime)
        {
        	//Utility.printCurrentTime();
        	//System.out.println("**** IPay System ----> System Auto Reload Properties file");
            read();
        }

        HashMap<String, String> groupTable = (HashMap<String, String>) mainTable.get(DEFAULT_GROUP_NAME);

		return groupTable;
    }

    
    /**
     * Get all value in group from the properties using only group name.
     * 
     * @param group 
     * @return HashMap of all value in group.
     * @throw FileNotFoundException - if file doen't exist
     * @throw IOException - if an I/O error occurs
     */
    public HashMap getGroup(String group) throws FileNotFoundException, IOException
    {
    	//System.out.println(new Date() + "***** Begin Get method *****");
		//System.out.println(new Date() + "ModifyTime : " + modifyTime);
		//System.out.println(new Date() + "lastModified : " + currentFile.lastModified());
        if (mainTable == null)
        {
        	//Utility.printCurrentTime();
			//System.out.println(new Date() + "**** IPay System ----> mainTable");
            read();
        }

        if (isAutoReload && currentFile.lastModified() > modifyTime)
        {
        	//Utility.printCurrentTime();
        	//System.out.println("**** IPay System ----> System Auto Reload Properties file");
            read();
        }

        HashMap groupTable = (HashMap) mainTable.get(group);

		return groupTable;
    }
    /**
     * Get the multiple values from the properties using key and group name.<br>
     * &nbsp;&nbsp;[group]<br>
     * &nbsp;&nbsp;key=value1,value2,value3
     * 
     * @param group
     * @param key
     * @return ArrayList of multiple values of specified key and group
     * @throw FileNotFoundException - if file doen't exist
     * @throw IOException - if an I/O error occurs
     */
    public ArrayList getMultipleValues(String group, String key) throws FileNotFoundException, IOException
    {
        String x = this.get(group, key);
        return this.getArrayList(x);

    }
    
    /**
     * Get all value in group from the properties using only group name.<br>
     * &nbsp;&nbsp;[GROUP]<br>
     * &nbsp;&nbsp;value1<br>
     * &nbsp;&nbsp;value2<br>
     * &nbsp;&nbsp;value3<br>
     * 
     * @param group
     * @return ArrayList of all value in group.
     * @throws FileNotFoundException
     * @throws IOException
     */
	public ArrayList getValueInGroup(String group) throws FileNotFoundException, IOException
    {
        HashMap h = this.getGroup(group);
        //TreeMap t = new TreeMap(h);
		ArrayList a = new ArrayList();
		if(h != null)
		{
			Iterator i = h.keySet().iterator();
			while(i.hasNext())
			{
				a.add((String)i.next());
			}
		}

		return a;
    }

 
	/**
	 * Get sort by order values in group from the properties using only group name.<br>
	 * &nbsp;&nbsp;[GROUP]<br>
	 * &nbsp;&nbsp;1=value1<br>
	 * &nbsp;&nbsp;2=value2<br>
	 * &nbsp;&nbsp;3=value3<br>
	 * @param group
	 * @return ArrayList of sort by order values in group.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public ArrayList getValueSortByOrder(String group) throws FileNotFoundException, IOException
    {
        HashMap h = this.getGroup(group);
        //TreeMap t = new TreeMap(h);
		ArrayList a = new ArrayList();
		if(h != null)
		{
			for(int i = 1; i <= h.size(); i++)
			{
				String key = (String)h.get(String.valueOf(i));
				if(key != null)
				{
					a.add(key);
				}
			}
		}
		
		return a;
    }
    
    /**
	 * Ask if the object is turn on the "Auto Reload" feature or not.
	 * @return Auto Reload feature Status
	 */
    public boolean isAutoReload()
    {
        return isAutoReload;
    }
    /**
     * Read the ini file and keep the value in HashMap.
     * Organise as 2 layer HashMap.
     * Always be called before getting value of any key. 
     * User can explicitly call this method when user want to refresh the table in case of changing content in ini file.
     * 
     * @throw FileNotFoundException - if file doen't exist
     * @throw IOException - if an I/O error occurs
     */
    public synchronized void read() throws FileNotFoundException, IOException
    {
        //debug
       // System.out.println("[PropertiesReader][read] : Load config file '" + currentFile.getAbsolutePath() + "'");

        // Get the last modify time of the property file for the auto reload feature
        modifyTime = currentFile.lastModified();

        // Prepare reading file
        // BufferedReader currentBReader = new BufferedReader(new FileReader(currentFile));	// Tinnakorn 20110723.
        BufferedReader currentBReader = new BufferedReader(new InputStreamReader(new FileInputStream(currentFile),"UTF-8"));	// Tinnakorn 20110723. Read file in UTF-8 Encoded.

        
        // Prepare the storage
        mainTable = new HashMap();

        // Prepare the variables
        String eachLine;
        String currentGroupName = DEFAULT_GROUP_NAME;
        boolean isIgnoreGroup = false;

        // Read each line of ini file util the end of file
        while ((eachLine = currentBReader.readLine()) != null)
        {
            eachLine = eachLine.trim();

            // Ignore case - empty line or comment line
            if (eachLine.length() <= 0 || eachLine.startsWith(COMMENT_SIGN))
                continue;

            // Check the group name line case
            if (eachLine.startsWith(OPEN_GROUP_SIGN) && eachLine.endsWith(CLOSE_GROUP_SIGN))
            {
                // If it's the group name line ...

                if (eachLine.length() > 2)
                {
                    // Make sure that it's not "[]"

                    String tempGroupName = eachLine.substring(1, eachLine.length() - 1);

                    if (tempGroupName.startsWith(COMMENT_SIGN))
                    {
                        // Yes, it's a ignore group
                        isIgnoreGroup = true;

                        continue;
                    }
                    else
                    {
                        // No, it's a normal group
                        isIgnoreGroup = false; // Don't forget to set the flag back
                        currentGroupName = tempGroupName; // Keep group name
                        //tableName.add(tempGroupName);

                        continue;
                    }
                }
                else
                {
                    // It's "[]", use DEFAULT_GROUP_NAME instead
                    isIgnoreGroup = false; // Don't forget to set the flag back
                    currentGroupName = DEFAULT_GROUP_NAME;

                    continue;
                }
            }
            else
            {
                // If the line is in the ignore group, ignore util new group is defined
                if (isIgnoreGroup)
                    continue;

                // Check the '=' sign
                int indexEqualSign = eachLine.indexOf("=");
				String key;
				String value;
                if (indexEqualSign == -1 || indexEqualSign == 0)
                {
                    // There's no '=' sign or the '=' sign locate at the begin or the end of the line, ignore it
                    //continue;
					key = eachLine;
					value = eachLine;
		        }
				else
				{
					key = eachLine.substring(0, indexEqualSign).trim();
					value = eachLine.substring(indexEqualSign).trim(); // This will cut the '=' into the value too

					// If value is empty, use empty string ("") instead,
					// else cut the '=' sign
					if (value.length() == 1 && value.equals("="))
					{
						value = "";
					}
					else
					{
						value = value.substring(1).trim();
					}
				}

                // Ensure that there's the current group table for keeping the key/value pair
                if (mainTable.get(currentGroupName) == null)
                {
                    mainTable.put(currentGroupName, new HashMap());
                }

                // Everything okay. Put key/value pair into the hashtable
                 ((HashMap) mainTable.get(currentGroupName)).put(key, value);
            }
        }

        // Finish reading the file, close the BufferReader
        currentBReader.close();
    }
    /**
     * Set the "Auto Reload" feature on/off.
     * User can turn on/off this feature anytime.
     */
    public void setAutoReload(boolean b)
    {
        isAutoReload = b;
    }

	/**
     * Set the property file to read property with String indicate the file name.
     */
    public void setPropertyFile(File fileObj)
    {
        currentFile = fileObj;
        mainTable = null;
    }
    
    /**
     * Set the property file to read property with String indicate the file name.
     */
    public void setPropertyFile(String fName)
    {
        currentFile = new File(fName);
        mainTable = null;
    }
    
}