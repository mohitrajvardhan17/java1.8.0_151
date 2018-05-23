package sun.net.ftp;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class FtpDirEntry
{
  private final String name;
  private String user = null;
  private String group = null;
  private long size = -1L;
  private Date created = null;
  private Date lastModified = null;
  private Type type = Type.FILE;
  private boolean[][] permissions = (boolean[][])null;
  private HashMap<String, String> facts = new HashMap();
  
  private FtpDirEntry()
  {
    name = null;
  }
  
  public FtpDirEntry(String paramString)
  {
    name = paramString;
  }
  
  public String getName()
  {
    return name;
  }
  
  public String getUser()
  {
    return user;
  }
  
  public FtpDirEntry setUser(String paramString)
  {
    user = paramString;
    return this;
  }
  
  public String getGroup()
  {
    return group;
  }
  
  public FtpDirEntry setGroup(String paramString)
  {
    group = paramString;
    return this;
  }
  
  public long getSize()
  {
    return size;
  }
  
  public FtpDirEntry setSize(long paramLong)
  {
    size = paramLong;
    return this;
  }
  
  public Type getType()
  {
    return type;
  }
  
  public FtpDirEntry setType(Type paramType)
  {
    type = paramType;
    return this;
  }
  
  public Date getLastModified()
  {
    return lastModified;
  }
  
  public FtpDirEntry setLastModified(Date paramDate)
  {
    lastModified = paramDate;
    return this;
  }
  
  public boolean canRead(Permission paramPermission)
  {
    if (permissions != null) {
      return permissions[value][0];
    }
    return false;
  }
  
  public boolean canWrite(Permission paramPermission)
  {
    if (permissions != null) {
      return permissions[value][1];
    }
    return false;
  }
  
  public boolean canExexcute(Permission paramPermission)
  {
    if (permissions != null) {
      return permissions[value][2];
    }
    return false;
  }
  
  public FtpDirEntry setPermissions(boolean[][] paramArrayOfBoolean)
  {
    permissions = paramArrayOfBoolean;
    return this;
  }
  
  public FtpDirEntry addFact(String paramString1, String paramString2)
  {
    facts.put(paramString1.toLowerCase(), paramString2);
    return this;
  }
  
  public String getFact(String paramString)
  {
    return (String)facts.get(paramString.toLowerCase());
  }
  
  public Date getCreated()
  {
    return created;
  }
  
  public FtpDirEntry setCreated(Date paramDate)
  {
    created = paramDate;
    return this;
  }
  
  public String toString()
  {
    if (lastModified == null) {
      return name + " [" + type + "] (" + user + " / " + group + ") " + size;
    }
    return name + " [" + type + "] (" + user + " / " + group + ") {" + size + "} " + DateFormat.getDateInstance().format(lastModified);
  }
  
  public static enum Permission
  {
    USER(0),  GROUP(1),  OTHERS(2);
    
    int value;
    
    private Permission(int paramInt)
    {
      value = paramInt;
    }
  }
  
  public static enum Type
  {
    FILE,  DIR,  PDIR,  CDIR,  LINK;
    
    private Type() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\ftp\FtpDirEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */