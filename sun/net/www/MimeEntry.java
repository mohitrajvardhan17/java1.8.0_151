package sun.net.www;

import java.io.File;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.StringTokenizer;

public class MimeEntry
  implements Cloneable
{
  private String typeName;
  private String tempFileNameTemplate;
  private int action;
  private String command;
  private String description;
  private String imageFileName;
  private String[] fileExtensions;
  boolean starred;
  public static final int UNKNOWN = 0;
  public static final int LOAD_INTO_BROWSER = 1;
  public static final int SAVE_TO_FILE = 2;
  public static final int LAUNCH_APPLICATION = 3;
  static final String[] actionKeywords = { "unknown", "browser", "save", "application" };
  
  public MimeEntry(String paramString)
  {
    this(paramString, 0, null, null, null);
  }
  
  MimeEntry(String paramString1, String paramString2, String paramString3)
  {
    typeName = paramString1.toLowerCase();
    action = 0;
    command = null;
    imageFileName = paramString2;
    setExtensions(paramString3);
    starred = isStarred(typeName);
  }
  
  MimeEntry(String paramString1, int paramInt, String paramString2, String paramString3)
  {
    typeName = paramString1.toLowerCase();
    action = paramInt;
    command = paramString2;
    imageFileName = null;
    fileExtensions = null;
    tempFileNameTemplate = paramString3;
  }
  
  MimeEntry(String paramString1, int paramInt, String paramString2, String paramString3, String[] paramArrayOfString)
  {
    typeName = paramString1.toLowerCase();
    action = paramInt;
    command = paramString2;
    imageFileName = paramString3;
    fileExtensions = paramArrayOfString;
    starred = isStarred(paramString1);
  }
  
  public synchronized String getType()
  {
    return typeName;
  }
  
  public synchronized void setType(String paramString)
  {
    typeName = paramString.toLowerCase();
  }
  
  public synchronized int getAction()
  {
    return action;
  }
  
  public synchronized void setAction(int paramInt, String paramString)
  {
    action = paramInt;
    command = paramString;
  }
  
  public synchronized void setAction(int paramInt)
  {
    action = paramInt;
  }
  
  public synchronized String getLaunchString()
  {
    return command;
  }
  
  public synchronized void setCommand(String paramString)
  {
    command = paramString;
  }
  
  public synchronized String getDescription()
  {
    return description != null ? description : typeName;
  }
  
  public synchronized void setDescription(String paramString)
  {
    description = paramString;
  }
  
  public String getImageFileName()
  {
    return imageFileName;
  }
  
  public synchronized void setImageFileName(String paramString)
  {
    File localFile = new File(paramString);
    if (localFile.getParent() == null) {
      imageFileName = System.getProperty("java.net.ftp.imagepath." + paramString);
    } else {
      imageFileName = paramString;
    }
    if (paramString.lastIndexOf('.') < 0) {
      imageFileName += ".gif";
    }
  }
  
  public String getTempFileTemplate()
  {
    return tempFileNameTemplate;
  }
  
  public synchronized String[] getExtensions()
  {
    return fileExtensions;
  }
  
  public synchronized String getExtensionsAsList()
  {
    String str = "";
    if (fileExtensions != null) {
      for (int i = 0; i < fileExtensions.length; i++)
      {
        str = str + fileExtensions[i];
        if (i < fileExtensions.length - 1) {
          str = str + ",";
        }
      }
    }
    return str;
  }
  
  public synchronized void setExtensions(String paramString)
  {
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ",");
    int i = localStringTokenizer.countTokens();
    String[] arrayOfString = new String[i];
    for (int j = 0; j < i; j++)
    {
      String str = (String)localStringTokenizer.nextElement();
      arrayOfString[j] = str.trim();
    }
    fileExtensions = arrayOfString;
  }
  
  private boolean isStarred(String paramString)
  {
    return (paramString != null) && (paramString.length() > 0) && (paramString.endsWith("/*"));
  }
  
  public Object launch(URLConnection paramURLConnection, InputStream paramInputStream, MimeTable paramMimeTable)
    throws ApplicationLaunchException
  {
    switch (action)
    {
    case 2: 
      try
      {
        return paramInputStream;
      }
      catch (Exception localException1)
      {
        return "Load to file failed:\n" + localException1;
      }
    case 1: 
      try
      {
        return paramURLConnection.getContent();
      }
      catch (Exception localException2)
      {
        return null;
      }
    case 3: 
      String str = command;
      int i = str.indexOf(' ');
      if (i > 0) {
        str = str.substring(0, i);
      }
      return new MimeLauncher(this, paramURLConnection, paramInputStream, paramMimeTable.getTempFileTemplate(), str);
    case 0: 
      return null;
    }
    return null;
  }
  
  public boolean matches(String paramString)
  {
    if (starred) {
      return paramString.startsWith(typeName);
    }
    return paramString.equals(typeName);
  }
  
  public Object clone()
  {
    MimeEntry localMimeEntry = new MimeEntry(typeName);
    action = action;
    command = command;
    description = description;
    imageFileName = imageFileName;
    tempFileNameTemplate = tempFileNameTemplate;
    fileExtensions = fileExtensions;
    return localMimeEntry;
  }
  
  public synchronized String toProperty()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    String str1 = "; ";
    int i = 0;
    int j = getAction();
    if (j != 0)
    {
      localStringBuffer.append("action=" + actionKeywords[j]);
      i = 1;
    }
    String str2 = getLaunchString();
    if ((str2 != null) && (str2.length() > 0))
    {
      if (i != 0) {
        localStringBuffer.append(str1);
      }
      localStringBuffer.append("application=" + str2);
      i = 1;
    }
    if (getImageFileName() != null)
    {
      if (i != 0) {
        localStringBuffer.append(str1);
      }
      localStringBuffer.append("icon=" + getImageFileName());
      i = 1;
    }
    String str3 = getExtensionsAsList();
    if (str3.length() > 0)
    {
      if (i != 0) {
        localStringBuffer.append(str1);
      }
      localStringBuffer.append("file_extensions=" + str3);
      i = 1;
    }
    String str4 = getDescription();
    if ((str4 != null) && (!str4.equals(getType())))
    {
      if (i != 0) {
        localStringBuffer.append(str1);
      }
      localStringBuffer.append("description=" + str4);
    }
    return localStringBuffer.toString();
  }
  
  public String toString()
  {
    return "MimeEntry[contentType=" + typeName + ", image=" + imageFileName + ", action=" + action + ", command=" + command + ", extensions=" + getExtensionsAsList() + "]";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\MimeEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */