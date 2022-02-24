/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package FileHelper;

import java.util.ArrayList;

/**
 *
 * @author rahuldub
 */
public class ArrayProcessHelper
{
    public ArrayList<ArrayList<String>>  getMissInCount(String []allDataOfTable, String aTableName)
    {
        ArrayList<String> listOfAdditionalInSource=new ArrayList<String>();
        boolean firstIndexTaken=false;
        for(String str : allDataOfTable)
        {
            if(str.contains("Source DBs has some additional data as"))
            {
                firstIndexTaken=true;
                continue;
            }
            if(firstIndexTaken && (str.length()>0) && (str.contains("==") || str.contains("**")))
            {
                    break;
            }
            if(firstIndexTaken && !str.contains("***") && !str.contains("=="))
                            listOfAdditionalInSource.add(str);
            //System.out.println("str : "+str);
        }
        for(String str : listOfAdditionalInSource)
        {
        	//System.out.println("content: "+str);
        }

        ArrayList<String> listOfAdditionalInTarget=new ArrayList<String>();
        boolean firstITaken=false;
        for(String str : allDataOfTable)
        {
            if(str.contains("Target DBs has some additional data"))
            {
                firstITaken=true;
                continue;
            }
            if(firstITaken && !(str.length()>0) && (str.contains("==") || str.contains("**")))
            {
                    break;
            }
            if(firstITaken && !str.contains("***") && !str.contains("=="))
            //if(firstITaken)
                listOfAdditionalInTarget.add(str);
        }

        //ArrayList<String> additionalArray[]=new ArrayList<String>(listOfAdditionalInSource, listOfAdditionalInTarget);
        ArrayList<ArrayList<String>> additionalArray=new ArrayList<ArrayList<String>>();
        additionalArray.add(listOfAdditionalInSource);
        additionalArray.add(listOfAdditionalInTarget);

        //System.out.println("tname : "+aTableName + "listOfAdditionalInSource "+listOfAdditionalInSource.size());
        //System.out.println("tname : "+aTableName + "listOfAdditionalInTarget "+listOfAdditionalInTarget.size());

        return additionalArray;
        //return String.valueOf(i)+","+String.valueOf(j);

    }

    public ArrayList<ArrayList<String>>  getMismatchList(String []allDataOfTable, String aTableName)
    {
        ArrayList<String> sourceData=new ArrayList<String>();
        ArrayList<String> TargetData=new ArrayList<String>();
        for(String str : allDataOfTable)
        {
            if(str.contains("Source -- "))
            {
                str=str.replaceAll("Source -- ", "");
                sourceData.add(str);
            }
            if(str.contains("Target -- "))
            {
                str=str.replaceAll("Target -- ", "");
                TargetData.add(str);
            }
        }

        ArrayList<ArrayList<String>> list=new ArrayList<ArrayList<String>>();
        list.add(sourceData);
        list.add(TargetData);
        return list;
    }
    public ArrayList<String>  getMismatchListForNonFlatData(String []allDataOfTable, String aTableName)
    {
        ArrayList<String> data=new ArrayList<String>();
        boolean newElementStarted=true;
        String recordToAdd="";
        for(String str : allDataOfTable)
        {
            if(str.contains("Error in RECORD - ") && newElementStarted)
            {
                str=str.replaceAll("Error in RECORD - ", "");
                recordToAdd=str;
            }
            if(str.contains("Source CLOB File - ") && newElementStarted)
                recordToAdd=recordToAdd+" | "+str;
            if(str.contains("Target CLOB File - ") && newElementStarted)
                recordToAdd=recordToAdd+"#@#"+str;
            if(str.contains("- - - - - - ") && newElementStarted)
            {
                newElementStarted=true;
                data.add(recordToAdd);
            }
        }
        return data;
    }
}
