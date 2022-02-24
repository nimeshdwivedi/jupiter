/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package FileHelper;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author rahuldub
 */
public class ParsingUtils
{

    String[] getArrayOfColumns(String tableName, String fileToRead) throws FileNotFoundException, IOException
    {
        BufferedReader br=new FileHandlingUtils().getBufferedReaderByFileName(fileToRead);
        String line;
        String targetString=null;
        System.out.println("Table Name:"+tableName+" and file:"+fileToRead);
        while ((line = br.readLine()) != null)
        {
            if(line.contains(tableName))
            {
                targetString = line.substring(line.indexOf("("));
                targetString=targetString.replaceAll("\\)", "");
                targetString=targetString.replaceAll("\\(", "");
                targetString=targetString.replaceAll(" ", "");
                break;
            }
        }
        return targetString.split(",");
    }

    String [] getAllLines(String tableName, String fileToRead) throws FileNotFoundException, IOException
    {
        BufferedReader br=new FileHandlingUtils().getBufferedReaderByFileName(fileToRead);
        ArrayList<String> list=new ArrayList<String>();
        String line;
        while ((line = br.readLine()) != null)
        {
            if(line.length()>0)
            {
                list.add(line);
            }
        }
        return list.toArray(new String[list.size()]);
    }
    String [] getAllLinesByTableName(String tableName, String[] arrayToRead) throws FileNotFoundException, IOException
    {
        ArrayList<String> list=new ArrayList<String>();
        boolean startReading=false;
        for(String i : arrayToRead)
        {
            if(i.contains("Results for Table "+tableName))
                startReading=true;
            if(startReading)
                list.add(i);
            if(startReading && i.contains("================"))
                break;
        }
        return list.toArray(new String[list.size()]);
    }
}
