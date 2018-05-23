package java.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class URLPermission
  extends Permission
{
  private static final long serialVersionUID = -2702463814894478682L;
  private transient String scheme;
  private transient String ssp;
  private transient String path;
  private transient List<String> methods;
  private transient List<String> requestHeaders;
  private transient Authority authority;
  private String actions;
  
  public URLPermission(String paramString1, String paramString2)
  {
    super(paramString1);
    init(paramString2);
  }
  
  private void init(String paramString)
  {
    parseURI(getName());
    int i = paramString.indexOf(':');
    if (paramString.lastIndexOf(':') != i) {
      throw new IllegalArgumentException("Invalid actions string: \"" + paramString + "\"");
    }
    String str1;
    String str2;
    if (i == -1)
    {
      str1 = paramString;
      str2 = "";
    }
    else
    {
      str1 = paramString.substring(0, i);
      str2 = paramString.substring(i + 1);
    }
    List localList = normalizeMethods(str1);
    Collections.sort(localList);
    methods = Collections.unmodifiableList(localList);
    localList = normalizeHeaders(str2);
    Collections.sort(localList);
    requestHeaders = Collections.unmodifiableList(localList);
    actions = actions();
  }
  
  public URLPermission(String paramString)
  {
    this(paramString, "*:*");
  }
  
  public String getActions()
  {
    return actions;
  }
  
  public boolean implies(Permission paramPermission)
  {
    if (!(paramPermission instanceof URLPermission)) {
      return false;
    }
    URLPermission localURLPermission = (URLPermission)paramPermission;
    if ((!((String)methods.get(0)).equals("*")) && (Collections.indexOfSubList(methods, methods) == -1)) {
      return false;
    }
    if ((requestHeaders.isEmpty()) && (!requestHeaders.isEmpty())) {
      return false;
    }
    if ((!requestHeaders.isEmpty()) && (!((String)requestHeaders.get(0)).equals("*")) && (Collections.indexOfSubList(requestHeaders, requestHeaders) == -1)) {
      return false;
    }
    if (!scheme.equals(scheme)) {
      return false;
    }
    if (ssp.equals("*")) {
      return true;
    }
    if (!authority.implies(authority)) {
      return false;
    }
    if (path == null) {
      return path == null;
    }
    if (path == null) {
      return false;
    }
    String str1;
    if (path.endsWith("/-"))
    {
      str1 = path.substring(0, path.length() - 1);
      return path.startsWith(str1);
    }
    if (path.endsWith("/*"))
    {
      str1 = path.substring(0, path.length() - 1);
      if (!path.startsWith(str1)) {
        return false;
      }
      String str2 = path.substring(str1.length());
      if (str2.indexOf('/') != -1) {
        return false;
      }
      return !str2.equals("-");
    }
    return path.equals(path);
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof URLPermission)) {
      return false;
    }
    URLPermission localURLPermission = (URLPermission)paramObject;
    if (!scheme.equals(scheme)) {
      return false;
    }
    if (!getActions().equals(localURLPermission.getActions())) {
      return false;
    }
    if (!authority.equals(authority)) {
      return false;
    }
    if (path != null) {
      return path.equals(path);
    }
    return path == null;
  }
  
  public int hashCode()
  {
    return getActions().hashCode() + scheme.hashCode() + authority.hashCode() + (path == null ? 0 : path.hashCode());
  }
  
  private List<String> normalizeMethods(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    StringBuilder localStringBuilder = new StringBuilder();
    for (int i = 0; i < paramString.length(); i++)
    {
      char c = paramString.charAt(i);
      if (c == ',')
      {
        String str2 = localStringBuilder.toString();
        if (str2.length() > 0) {
          localArrayList.add(str2);
        }
        localStringBuilder = new StringBuilder();
      }
      else
      {
        if ((c == ' ') || (c == '\t')) {
          throw new IllegalArgumentException("White space not allowed in methods: \"" + paramString + "\"");
        }
        if ((c >= 'a') && (c <= 'z')) {
          c = (char)(c - ' ');
        }
        localStringBuilder.append(c);
      }
    }
    String str1 = localStringBuilder.toString();
    if (str1.length() > 0) {
      localArrayList.add(str1);
    }
    return localArrayList;
  }
  
  private List<String> normalizeHeaders(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 1;
    for (int j = 0; j < paramString.length(); j++)
    {
      char c = paramString.charAt(j);
      if ((c >= 'a') && (c <= 'z'))
      {
        if (i != 0)
        {
          c = (char)(c - ' ');
          i = 0;
        }
        localStringBuilder.append(c);
      }
      else
      {
        if ((c == ' ') || (c == '\t')) {
          throw new IllegalArgumentException("White space not allowed in headers: \"" + paramString + "\"");
        }
        if (c == '-')
        {
          i = 1;
          localStringBuilder.append(c);
        }
        else if (c == ',')
        {
          String str2 = localStringBuilder.toString();
          if (str2.length() > 0) {
            localArrayList.add(str2);
          }
          localStringBuilder = new StringBuilder();
          i = 1;
        }
        else
        {
          i = 0;
          localStringBuilder.append(c);
        }
      }
    }
    String str1 = localStringBuilder.toString();
    if (str1.length() > 0) {
      localArrayList.add(str1);
    }
    return localArrayList;
  }
  
  private void parseURI(String paramString)
  {
    int i = paramString.length();
    int j = paramString.indexOf(':');
    if ((j == -1) || (j + 1 == i)) {
      throw new IllegalArgumentException("Invalid URL string: \"" + paramString + "\"");
    }
    scheme = paramString.substring(0, j).toLowerCase();
    ssp = paramString.substring(j + 1);
    if (!ssp.startsWith("//"))
    {
      if (!ssp.equals("*")) {
        throw new IllegalArgumentException("Invalid URL string: \"" + paramString + "\"");
      }
      authority = new Authority(scheme, "*");
      return;
    }
    String str1 = ssp.substring(2);
    j = str1.indexOf('/');
    String str2;
    if (j == -1)
    {
      path = "";
      str2 = str1;
    }
    else
    {
      str2 = str1.substring(0, j);
      path = str1.substring(j);
    }
    authority = new Authority(scheme, str2.toLowerCase());
  }
  
  private String actions()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    Iterator localIterator = methods.iterator();
    String str;
    while (localIterator.hasNext())
    {
      str = (String)localIterator.next();
      localStringBuilder.append(str);
    }
    localStringBuilder.append(":");
    localIterator = requestHeaders.iterator();
    while (localIterator.hasNext())
    {
      str = (String)localIterator.next();
      localStringBuilder.append(str);
    }
    return localStringBuilder.toString();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    String str = (String)localGetField.get("actions", null);
    init(str);
  }
  
  static class Authority
  {
    HostPortrange p;
    
    Authority(String paramString1, String paramString2)
    {
      int i = paramString2.indexOf('@');
      if (i == -1) {
        p = new HostPortrange(paramString1, paramString2);
      } else {
        p = new HostPortrange(paramString1, paramString2.substring(i + 1));
      }
    }
    
    boolean implies(Authority paramAuthority)
    {
      return (impliesHostrange(paramAuthority)) && (impliesPortrange(paramAuthority));
    }
    
    private boolean impliesHostrange(Authority paramAuthority)
    {
      String str1 = p.hostname();
      String str2 = p.hostname();
      if ((p.wildcard()) && (str1.equals(""))) {
        return true;
      }
      if ((p.wildcard()) && (str2.equals(""))) {
        return false;
      }
      if (str1.equals(str2)) {
        return true;
      }
      if (p.wildcard()) {
        return str2.endsWith(str1);
      }
      return false;
    }
    
    private boolean impliesPortrange(Authority paramAuthority)
    {
      int[] arrayOfInt1 = p.portrange();
      int[] arrayOfInt2 = p.portrange();
      if (arrayOfInt1[0] == -1) {
        return true;
      }
      return (arrayOfInt1[0] <= arrayOfInt2[0]) && (arrayOfInt1[1] >= arrayOfInt2[1]);
    }
    
    boolean equals(Authority paramAuthority)
    {
      return p.equals(p);
    }
    
    public int hashCode()
    {
      return p.hashCode();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\URLPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */