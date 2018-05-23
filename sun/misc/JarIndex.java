package sun.misc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.AccessController;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import sun.security.action.GetPropertyAction;

public class JarIndex
{
  private HashMap<String, LinkedList<String>> indexMap = new HashMap();
  private HashMap<String, LinkedList<String>> jarMap = new HashMap();
  private String[] jarFiles;
  public static final String INDEX_NAME = "META-INF/INDEX.LIST";
  private static final boolean metaInfFilenames = "true".equals(AccessController.doPrivileged(new GetPropertyAction("sun.misc.JarIndex.metaInfFilenames")));
  
  public JarIndex() {}
  
  public JarIndex(InputStream paramInputStream)
    throws IOException
  {
    this();
    read(paramInputStream);
  }
  
  public JarIndex(String[] paramArrayOfString)
    throws IOException
  {
    this();
    jarFiles = paramArrayOfString;
    parseJars(paramArrayOfString);
  }
  
  public static JarIndex getJarIndex(JarFile paramJarFile)
    throws IOException
  {
    return getJarIndex(paramJarFile, null);
  }
  
  public static JarIndex getJarIndex(JarFile paramJarFile, MetaIndex paramMetaIndex)
    throws IOException
  {
    JarIndex localJarIndex = null;
    if ((paramMetaIndex != null) && (!paramMetaIndex.mayContain("META-INF/INDEX.LIST"))) {
      return null;
    }
    JarEntry localJarEntry = paramJarFile.getJarEntry("META-INF/INDEX.LIST");
    if (localJarEntry != null) {
      localJarIndex = new JarIndex(paramJarFile.getInputStream(localJarEntry));
    }
    return localJarIndex;
  }
  
  public String[] getJarFiles()
  {
    return jarFiles;
  }
  
  private void addToList(String paramString1, String paramString2, HashMap<String, LinkedList<String>> paramHashMap)
  {
    LinkedList localLinkedList = (LinkedList)paramHashMap.get(paramString1);
    if (localLinkedList == null)
    {
      localLinkedList = new LinkedList();
      localLinkedList.add(paramString2);
      paramHashMap.put(paramString1, localLinkedList);
    }
    else if (!localLinkedList.contains(paramString2))
    {
      localLinkedList.add(paramString2);
    }
  }
  
  public LinkedList<String> get(String paramString)
  {
    LinkedList localLinkedList = null;
    int i;
    if (((localLinkedList = (LinkedList)indexMap.get(paramString)) == null) && ((i = paramString.lastIndexOf("/")) != -1)) {
      localLinkedList = (LinkedList)indexMap.get(paramString.substring(0, i));
    }
    return localLinkedList;
  }
  
  public void add(String paramString1, String paramString2)
  {
    int i;
    String str;
    if ((i = paramString1.lastIndexOf("/")) != -1) {
      str = paramString1.substring(0, i);
    } else {
      str = paramString1;
    }
    addMapping(str, paramString2);
  }
  
  private void addMapping(String paramString1, String paramString2)
  {
    addToList(paramString1, paramString2, indexMap);
    addToList(paramString2, paramString1, jarMap);
  }
  
  private void parseJars(String[] paramArrayOfString)
    throws IOException
  {
    if (paramArrayOfString == null) {
      return;
    }
    String str1 = null;
    for (int i = 0; i < paramArrayOfString.length; i++)
    {
      str1 = paramArrayOfString[i];
      ZipFile localZipFile = new ZipFile(str1.replace('/', File.separatorChar));
      Enumeration localEnumeration = localZipFile.entries();
      while (localEnumeration.hasMoreElements())
      {
        ZipEntry localZipEntry = (ZipEntry)localEnumeration.nextElement();
        String str2 = localZipEntry.getName();
        if ((!str2.equals("META-INF/")) && (!str2.equals("META-INF/INDEX.LIST")) && (!str2.equals("META-INF/MANIFEST.MF"))) {
          if ((!metaInfFilenames) || (!str2.startsWith("META-INF/"))) {
            add(str2, str1);
          } else if (!localZipEntry.isDirectory()) {
            addMapping(str2, str1);
          }
        }
      }
      localZipFile.close();
    }
  }
  
  public void write(OutputStream paramOutputStream)
    throws IOException
  {
    BufferedWriter localBufferedWriter = new BufferedWriter(new OutputStreamWriter(paramOutputStream, "UTF8"));
    localBufferedWriter.write("JarIndex-Version: 1.0\n\n");
    if (jarFiles != null)
    {
      for (int i = 0; i < jarFiles.length; i++)
      {
        String str = jarFiles[i];
        localBufferedWriter.write(str + "\n");
        LinkedList localLinkedList = (LinkedList)jarMap.get(str);
        if (localLinkedList != null)
        {
          Iterator localIterator = localLinkedList.iterator();
          while (localIterator.hasNext()) {
            localBufferedWriter.write((String)localIterator.next() + "\n");
          }
        }
        localBufferedWriter.write("\n");
      }
      localBufferedWriter.flush();
    }
  }
  
  public void read(InputStream paramInputStream)
    throws IOException
  {
    BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(paramInputStream, "UTF8"));
    String str1 = null;
    String str2 = null;
    Vector localVector = new Vector();
    while (((str1 = localBufferedReader.readLine()) != null) && (!str1.endsWith(".jar"))) {}
    while (str1 != null)
    {
      if (str1.length() != 0) {
        if (str1.endsWith(".jar"))
        {
          str2 = str1;
          localVector.add(str2);
        }
        else
        {
          String str3 = str1;
          addMapping(str3, str2);
        }
      }
      str1 = localBufferedReader.readLine();
    }
    jarFiles = ((String[])localVector.toArray(new String[localVector.size()]));
  }
  
  public void merge(JarIndex paramJarIndex, String paramString)
  {
    Iterator localIterator1 = indexMap.entrySet().iterator();
    while (localIterator1.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator1.next();
      String str1 = (String)localEntry.getKey();
      LinkedList localLinkedList = (LinkedList)localEntry.getValue();
      Iterator localIterator2 = localLinkedList.iterator();
      while (localIterator2.hasNext())
      {
        String str2 = (String)localIterator2.next();
        if (paramString != null) {
          str2 = paramString.concat(str2);
        }
        paramJarIndex.addMapping(str1, str2);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\JarIndex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */