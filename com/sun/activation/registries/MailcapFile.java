package com.sun.activation.registries;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class MailcapFile
{
  private Map type_hash = new HashMap();
  private Map fallback_hash = new HashMap();
  private Map native_commands = new HashMap();
  private static boolean addReverse = false;
  
  public MailcapFile(String paramString)
    throws IOException
  {
    if (LogSupport.isLoggable()) {
      LogSupport.log("new MailcapFile: file " + paramString);
    }
    FileReader localFileReader = null;
    try
    {
      localFileReader = new FileReader(paramString);
      parse(new BufferedReader(localFileReader));
      return;
    }
    finally
    {
      if (localFileReader != null) {
        try
        {
          localFileReader.close();
        }
        catch (IOException localIOException2) {}
      }
    }
  }
  
  public MailcapFile(InputStream paramInputStream)
    throws IOException
  {
    if (LogSupport.isLoggable()) {
      LogSupport.log("new MailcapFile: InputStream");
    }
    parse(new BufferedReader(new InputStreamReader(paramInputStream, "iso-8859-1")));
  }
  
  public MailcapFile()
  {
    if (LogSupport.isLoggable()) {
      LogSupport.log("new MailcapFile: default");
    }
  }
  
  public Map getMailcapList(String paramString)
  {
    Object localObject = null;
    Map localMap = null;
    localObject = (Map)type_hash.get(paramString);
    int i = paramString.indexOf('/');
    String str1 = paramString.substring(i + 1);
    if (!str1.equals("*"))
    {
      String str2 = paramString.substring(0, i + 1) + "*";
      localMap = (Map)type_hash.get(str2);
      if (localMap != null) {
        if (localObject != null) {
          localObject = mergeResults((Map)localObject, localMap);
        } else {
          localObject = localMap;
        }
      }
    }
    return (Map)localObject;
  }
  
  public Map getMailcapFallbackList(String paramString)
  {
    Object localObject = null;
    Map localMap = null;
    localObject = (Map)fallback_hash.get(paramString);
    int i = paramString.indexOf('/');
    String str1 = paramString.substring(i + 1);
    if (!str1.equals("*"))
    {
      String str2 = paramString.substring(0, i + 1) + "*";
      localMap = (Map)fallback_hash.get(str2);
      if (localMap != null) {
        if (localObject != null) {
          localObject = mergeResults((Map)localObject, localMap);
        } else {
          localObject = localMap;
        }
      }
    }
    return (Map)localObject;
  }
  
  public String[] getMimeTypes()
  {
    HashSet localHashSet = new HashSet(type_hash.keySet());
    localHashSet.addAll(fallback_hash.keySet());
    localHashSet.addAll(native_commands.keySet());
    String[] arrayOfString = new String[localHashSet.size()];
    arrayOfString = (String[])localHashSet.toArray(arrayOfString);
    return arrayOfString;
  }
  
  public String[] getNativeCommands(String paramString)
  {
    String[] arrayOfString = null;
    List localList = (List)native_commands.get(paramString.toLowerCase(Locale.ENGLISH));
    if (localList != null)
    {
      arrayOfString = new String[localList.size()];
      arrayOfString = (String[])localList.toArray(arrayOfString);
    }
    return arrayOfString;
  }
  
  private Map mergeResults(Map paramMap1, Map paramMap2)
  {
    Iterator localIterator = paramMap2.keySet().iterator();
    HashMap localHashMap = new HashMap(paramMap1);
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      Object localObject = (List)localHashMap.get(str);
      if (localObject == null)
      {
        localHashMap.put(str, paramMap2.get(str));
      }
      else
      {
        List localList = (List)paramMap2.get(str);
        localObject = new ArrayList((Collection)localObject);
        ((List)localObject).addAll(localList);
        localHashMap.put(str, localObject);
      }
    }
    return localHashMap;
  }
  
  public void appendToMailcap(String paramString)
  {
    if (LogSupport.isLoggable()) {
      LogSupport.log("appendToMailcap: " + paramString);
    }
    try
    {
      parse(new StringReader(paramString));
    }
    catch (IOException localIOException) {}
  }
  
  private void parse(Reader paramReader)
    throws IOException
  {
    BufferedReader localBufferedReader = new BufferedReader(paramReader);
    String str1 = null;
    String str2 = null;
    while ((str1 = localBufferedReader.readLine()) != null)
    {
      str1 = str1.trim();
      try
      {
        if (str1.charAt(0) != '#') {
          if (str1.charAt(str1.length() - 1) == '\\')
          {
            if (str2 != null) {
              str2 = str2 + str1.substring(0, str1.length() - 1);
            } else {
              str2 = str1.substring(0, str1.length() - 1);
            }
          }
          else if (str2 != null)
          {
            str2 = str2 + str1;
            try
            {
              parseLine(str2);
            }
            catch (MailcapParseException localMailcapParseException1) {}
            str2 = null;
          }
          else
          {
            try
            {
              parseLine(str1);
            }
            catch (MailcapParseException localMailcapParseException2) {}
          }
        }
      }
      catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException) {}
    }
  }
  
  protected void parseLine(String paramString)
    throws MailcapParseException, IOException
  {
    MailcapTokenizer localMailcapTokenizer = new MailcapTokenizer(paramString);
    localMailcapTokenizer.setIsAutoquoting(false);
    if (LogSupport.isLoggable()) {
      LogSupport.log("parse: " + paramString);
    }
    int i = localMailcapTokenizer.nextToken();
    if (i != 2) {
      reportParseError(2, i, localMailcapTokenizer.getCurrentTokenValue());
    }
    String str1 = localMailcapTokenizer.getCurrentTokenValue().toLowerCase(Locale.ENGLISH);
    String str2 = "*";
    i = localMailcapTokenizer.nextToken();
    if ((i != 47) && (i != 59)) {
      reportParseError(47, 59, i, localMailcapTokenizer.getCurrentTokenValue());
    }
    if (i == 47)
    {
      i = localMailcapTokenizer.nextToken();
      if (i != 2) {
        reportParseError(2, i, localMailcapTokenizer.getCurrentTokenValue());
      }
      str2 = localMailcapTokenizer.getCurrentTokenValue().toLowerCase(Locale.ENGLISH);
      i = localMailcapTokenizer.nextToken();
    }
    String str3 = str1 + "/" + str2;
    if (LogSupport.isLoggable()) {
      LogSupport.log("  Type: " + str3);
    }
    LinkedHashMap localLinkedHashMap = new LinkedHashMap();
    if (i != 59) {
      reportParseError(59, i, localMailcapTokenizer.getCurrentTokenValue());
    }
    localMailcapTokenizer.setIsAutoquoting(true);
    i = localMailcapTokenizer.nextToken();
    localMailcapTokenizer.setIsAutoquoting(false);
    if ((i != 2) && (i != 59)) {
      reportParseError(2, 59, i, localMailcapTokenizer.getCurrentTokenValue());
    }
    if (i == 2)
    {
      Object localObject1 = (List)native_commands.get(str3);
      if (localObject1 == null)
      {
        localObject1 = new ArrayList();
        ((List)localObject1).add(paramString);
        native_commands.put(str3, localObject1);
      }
      else
      {
        ((List)localObject1).add(paramString);
      }
    }
    if (i != 59) {
      i = localMailcapTokenizer.nextToken();
    }
    if (i == 59)
    {
      int j = 0;
      Object localObject4;
      Object localObject5;
      do
      {
        i = localMailcapTokenizer.nextToken();
        if (i != 2) {
          reportParseError(2, i, localMailcapTokenizer.getCurrentTokenValue());
        }
        localObject2 = localMailcapTokenizer.getCurrentTokenValue().toLowerCase(Locale.ENGLISH);
        i = localMailcapTokenizer.nextToken();
        if ((i != 61) && (i != 59) && (i != 5)) {
          reportParseError(61, 59, 5, i, localMailcapTokenizer.getCurrentTokenValue());
        }
        if (i == 61)
        {
          localMailcapTokenizer.setIsAutoquoting(true);
          i = localMailcapTokenizer.nextToken();
          localMailcapTokenizer.setIsAutoquoting(false);
          if (i != 2) {
            reportParseError(2, i, localMailcapTokenizer.getCurrentTokenValue());
          }
          localObject3 = localMailcapTokenizer.getCurrentTokenValue();
          if (((String)localObject2).startsWith("x-java-"))
          {
            localObject4 = ((String)localObject2).substring(7);
            if ((((String)localObject4).equals("fallback-entry")) && (((String)localObject3).equalsIgnoreCase("true")))
            {
              j = 1;
            }
            else
            {
              if (LogSupport.isLoggable()) {
                LogSupport.log("    Command: " + (String)localObject4 + ", Class: " + (String)localObject3);
              }
              localObject5 = (List)localLinkedHashMap.get(localObject4);
              if (localObject5 == null)
              {
                localObject5 = new ArrayList();
                localLinkedHashMap.put(localObject4, localObject5);
              }
              if (addReverse) {
                ((List)localObject5).add(0, localObject3);
              } else {
                ((List)localObject5).add(localObject3);
              }
            }
          }
          i = localMailcapTokenizer.nextToken();
        }
      } while (i == 59);
      Object localObject2 = j != 0 ? fallback_hash : type_hash;
      Object localObject3 = (Map)((Map)localObject2).get(str3);
      if (localObject3 == null)
      {
        ((Map)localObject2).put(str3, localLinkedHashMap);
      }
      else
      {
        if (LogSupport.isLoggable()) {
          LogSupport.log("Merging commands for type " + str3);
        }
        localObject4 = ((Map)localObject3).keySet().iterator();
        List localList1;
        while (((Iterator)localObject4).hasNext())
        {
          localObject5 = (String)((Iterator)localObject4).next();
          localList1 = (List)((Map)localObject3).get(localObject5);
          List localList2 = (List)localLinkedHashMap.get(localObject5);
          if (localList2 != null)
          {
            Iterator localIterator = localList2.iterator();
            while (localIterator.hasNext())
            {
              String str4 = (String)localIterator.next();
              if (!localList1.contains(str4)) {
                if (addReverse) {
                  localList1.add(0, str4);
                } else {
                  localList1.add(str4);
                }
              }
            }
          }
        }
        localObject4 = localLinkedHashMap.keySet().iterator();
        while (((Iterator)localObject4).hasNext())
        {
          localObject5 = (String)((Iterator)localObject4).next();
          if (!((Map)localObject3).containsKey(localObject5))
          {
            localList1 = (List)localLinkedHashMap.get(localObject5);
            ((Map)localObject3).put(localObject5, localList1);
          }
        }
      }
    }
    else if (i != 5)
    {
      reportParseError(5, 59, i, localMailcapTokenizer.getCurrentTokenValue());
    }
  }
  
  protected static void reportParseError(int paramInt1, int paramInt2, String paramString)
    throws MailcapParseException
  {
    throw new MailcapParseException("Encountered a " + MailcapTokenizer.nameForToken(paramInt2) + " token (" + paramString + ") while expecting a " + MailcapTokenizer.nameForToken(paramInt1) + " token.");
  }
  
  protected static void reportParseError(int paramInt1, int paramInt2, int paramInt3, String paramString)
    throws MailcapParseException
  {
    throw new MailcapParseException("Encountered a " + MailcapTokenizer.nameForToken(paramInt3) + " token (" + paramString + ") while expecting a " + MailcapTokenizer.nameForToken(paramInt1) + " or a " + MailcapTokenizer.nameForToken(paramInt2) + " token.");
  }
  
  protected static void reportParseError(int paramInt1, int paramInt2, int paramInt3, int paramInt4, String paramString)
    throws MailcapParseException
  {
    if (LogSupport.isLoggable()) {
      LogSupport.log("PARSE ERROR: Encountered a " + MailcapTokenizer.nameForToken(paramInt4) + " token (" + paramString + ") while expecting a " + MailcapTokenizer.nameForToken(paramInt1) + ", a " + MailcapTokenizer.nameForToken(paramInt2) + ", or a " + MailcapTokenizer.nameForToken(paramInt3) + " token.");
    }
    throw new MailcapParseException("Encountered a " + MailcapTokenizer.nameForToken(paramInt4) + " token (" + paramString + ") while expecting a " + MailcapTokenizer.nameForToken(paramInt1) + ", a " + MailcapTokenizer.nameForToken(paramInt2) + ", or a " + MailcapTokenizer.nameForToken(paramInt3) + " token.");
  }
  
  static
  {
    try
    {
      addReverse = Boolean.getBoolean("javax.activation.addreverse");
    }
    catch (Throwable localThrowable) {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\activation\registries\MailcapFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */