package com.sun.jmx.remote.security;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.security.auth.Subject;

public class MBeanServerFileAccessController
  extends MBeanServerAccessController
{
  static final String READONLY = "readonly";
  static final String READWRITE = "readwrite";
  static final String CREATE = "create";
  static final String UNREGISTER = "unregister";
  private Map<String, Access> accessMap;
  private Properties originalProps;
  private String accessFileName;
  
  public MBeanServerFileAccessController(String paramString)
    throws IOException
  {
    accessFileName = paramString;
    Properties localProperties = propertiesFromFile(paramString);
    parseProperties(localProperties);
  }
  
  public MBeanServerFileAccessController(String paramString, MBeanServer paramMBeanServer)
    throws IOException
  {
    this(paramString);
    setMBeanServer(paramMBeanServer);
  }
  
  public MBeanServerFileAccessController(Properties paramProperties)
    throws IOException
  {
    if (paramProperties == null) {
      throw new IllegalArgumentException("Null properties");
    }
    originalProps = paramProperties;
    parseProperties(paramProperties);
  }
  
  public MBeanServerFileAccessController(Properties paramProperties, MBeanServer paramMBeanServer)
    throws IOException
  {
    this(paramProperties);
    setMBeanServer(paramMBeanServer);
  }
  
  public void checkRead()
  {
    checkAccess(AccessType.READ, null);
  }
  
  public void checkWrite()
  {
    checkAccess(AccessType.WRITE, null);
  }
  
  public void checkCreate(String paramString)
  {
    checkAccess(AccessType.CREATE, paramString);
  }
  
  public void checkUnregister(ObjectName paramObjectName)
  {
    checkAccess(AccessType.UNREGISTER, null);
  }
  
  public synchronized void refresh()
    throws IOException
  {
    Properties localProperties;
    if (accessFileName == null) {
      localProperties = originalProps;
    } else {
      localProperties = propertiesFromFile(accessFileName);
    }
    parseProperties(localProperties);
  }
  
  private static Properties propertiesFromFile(String paramString)
    throws IOException
  {
    FileInputStream localFileInputStream = new FileInputStream(paramString);
    try
    {
      Properties localProperties1 = new Properties();
      localProperties1.load(localFileInputStream);
      Properties localProperties2 = localProperties1;
      return localProperties2;
    }
    finally
    {
      localFileInputStream.close();
    }
  }
  
  private synchronized void checkAccess(AccessType paramAccessType, String paramString)
  {
    final AccessControlContext localAccessControlContext = AccessController.getContext();
    Subject localSubject = (Subject)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Subject run()
      {
        return Subject.getSubject(localAccessControlContext);
      }
    });
    if (localSubject == null) {
      return;
    }
    Set localSet = localSubject.getPrincipals();
    String str = null;
    Object localObject1 = localSet.iterator();
    Object localObject2;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Principal)((Iterator)localObject1).next();
      Access localAccess = (Access)accessMap.get(((Principal)localObject2).getName());
      if (localAccess != null)
      {
        boolean bool;
        switch (paramAccessType)
        {
        case READ: 
          bool = true;
          break;
        case WRITE: 
          bool = write;
          break;
        case UNREGISTER: 
          bool = unregister;
          if ((!bool) && (write)) {
            str = "unregister";
          }
          break;
        case CREATE: 
          bool = checkCreateAccess(localAccess, paramString);
          if ((!bool) && (write)) {
            str = "create " + paramString;
          }
          break;
        default: 
          throw new AssertionError();
        }
        if (bool) {
          return;
        }
      }
    }
    localObject1 = new SecurityException("Access denied! Invalid access level for requested MBeanServer operation.");
    if (str != null)
    {
      localObject2 = new SecurityException("Access property for this identity should be similar to: readwrite " + str);
      ((SecurityException)localObject1).initCause((Throwable)localObject2);
    }
    throw ((Throwable)localObject1);
  }
  
  private static boolean checkCreateAccess(Access paramAccess, String paramString)
  {
    for (String str : createPatterns) {
      if (classNameMatch(str, paramString)) {
        return true;
      }
    }
    return false;
  }
  
  private static boolean classNameMatch(String paramString1, String paramString2)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString1, "*", true);
    while (localStringTokenizer.hasMoreTokens())
    {
      String str = localStringTokenizer.nextToken();
      if (str.equals("*")) {
        localStringBuilder.append("[^.]*");
      } else {
        localStringBuilder.append(Pattern.quote(str));
      }
    }
    return paramString2.matches(localStringBuilder.toString());
  }
  
  private void parseProperties(Properties paramProperties)
  {
    accessMap = new HashMap();
    Iterator localIterator = paramProperties.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      String str1 = (String)localEntry.getKey();
      String str2 = (String)localEntry.getValue();
      Access localAccess = Parser.parseAccess(str1, str2);
      accessMap.put(str1, localAccess);
    }
  }
  
  private static class Access
  {
    final boolean write;
    final String[] createPatterns;
    private boolean unregister;
    private final String[] NO_STRINGS = new String[0];
    
    Access(boolean paramBoolean1, boolean paramBoolean2, List<String> paramList)
    {
      write = paramBoolean1;
      int i = paramList == null ? 0 : paramList.size();
      if (i == 0) {
        createPatterns = NO_STRINGS;
      } else {
        createPatterns = ((String[])paramList.toArray(new String[i]));
      }
      unregister = paramBoolean2;
    }
  }
  
  private static enum AccessType
  {
    READ,  WRITE,  CREATE,  UNREGISTER;
    
    private AccessType() {}
  }
  
  private static class Parser
  {
    private static final int EOS = -1;
    private final String identity;
    private final String s;
    private final int len;
    private int i;
    private int c;
    
    private Parser(String paramString1, String paramString2)
    {
      identity = paramString1;
      s = paramString2;
      len = paramString2.length();
      i = 0;
      if (i < len) {
        c = paramString2.codePointAt(i);
      } else {
        c = -1;
      }
    }
    
    static MBeanServerFileAccessController.Access parseAccess(String paramString1, String paramString2)
    {
      return new Parser(paramString1, paramString2).parseAccess();
    }
    
    private MBeanServerFileAccessController.Access parseAccess()
    {
      skipSpace();
      String str = parseWord();
      MBeanServerFileAccessController.Access localAccess;
      if (str.equals("readonly")) {
        localAccess = new MBeanServerFileAccessController.Access(false, false, null);
      } else if (str.equals("readwrite")) {
        localAccess = parseReadWrite();
      } else {
        throw syntax("Expected readonly or readwrite: " + str);
      }
      if (c != -1) {
        throw syntax("Extra text at end of line");
      }
      return localAccess;
    }
    
    private MBeanServerFileAccessController.Access parseReadWrite()
    {
      ArrayList localArrayList = new ArrayList();
      boolean bool = false;
      for (;;)
      {
        skipSpace();
        if (c == -1) {
          break;
        }
        String str = parseWord();
        if (str.equals("unregister")) {
          bool = true;
        } else if (str.equals("create")) {
          parseCreate(localArrayList);
        } else {
          throw syntax("Unrecognized keyword " + str);
        }
      }
      return new MBeanServerFileAccessController.Access(true, bool, localArrayList);
    }
    
    private void parseCreate(List<String> paramList)
    {
      for (;;)
      {
        skipSpace();
        paramList.add(parseClassName());
        skipSpace();
        if (c != 44) {
          break;
        }
        next();
      }
    }
    
    private String parseClassName()
    {
      int j = i;
      int k = 0;
      for (;;)
      {
        if (c == 46)
        {
          if (k == 0) {
            throw syntax("Bad . in class name");
          }
          k = 0;
        }
        else
        {
          if ((c != 42) && (!Character.isJavaIdentifierPart(c))) {
            break;
          }
          k = 1;
        }
        next();
      }
      String str = s.substring(j, i);
      if (k == 0) {
        throw syntax("Bad class name " + str);
      }
      return str;
    }
    
    private void next()
    {
      if (c != -1)
      {
        i += Character.charCount(c);
        if (i < len) {
          c = s.codePointAt(i);
        } else {
          c = -1;
        }
      }
    }
    
    private void skipSpace()
    {
      while (Character.isWhitespace(c)) {
        next();
      }
    }
    
    private String parseWord()
    {
      skipSpace();
      if (c == -1) {
        throw syntax("Expected word at end of line");
      }
      int j = i;
      while ((c != -1) && (!Character.isWhitespace(c))) {
        next();
      }
      String str = s.substring(j, i);
      skipSpace();
      return str;
    }
    
    private IllegalArgumentException syntax(String paramString)
    {
      return new IllegalArgumentException(paramString + " [" + identity + " " + s + "]");
    }
    
    static
    {
      assert (!Character.isWhitespace(-1));
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\remote\security\MBeanServerFileAccessController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */