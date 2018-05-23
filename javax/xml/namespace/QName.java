package javax.xml.namespace;

import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class QName
  implements Serializable
{
  private static final long serialVersionUID;
  private static final long defaultSerialVersionUID = -9120448754896609940L;
  private static final long compatibleSerialVersionUID = 4418622981026545151L;
  private static boolean useDefaultSerialVersionUID = true;
  private final String namespaceURI;
  private final String localPart;
  private final String prefix;
  
  public QName(String paramString1, String paramString2)
  {
    this(paramString1, paramString2, "");
  }
  
  public QName(String paramString1, String paramString2, String paramString3)
  {
    if (paramString1 == null) {
      namespaceURI = "";
    } else {
      namespaceURI = paramString1;
    }
    if (paramString2 == null) {
      throw new IllegalArgumentException("local part cannot be \"null\" when creating a QName");
    }
    localPart = paramString2;
    if (paramString3 == null) {
      throw new IllegalArgumentException("prefix cannot be \"null\" when creating a QName");
    }
    prefix = paramString3;
  }
  
  public QName(String paramString)
  {
    this("", paramString, "");
  }
  
  public String getNamespaceURI()
  {
    return namespaceURI;
  }
  
  public String getLocalPart()
  {
    return localPart;
  }
  
  public String getPrefix()
  {
    return prefix;
  }
  
  public final boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if ((paramObject == null) || (!(paramObject instanceof QName))) {
      return false;
    }
    QName localQName = (QName)paramObject;
    return (localPart.equals(localPart)) && (namespaceURI.equals(namespaceURI));
  }
  
  public final int hashCode()
  {
    return namespaceURI.hashCode() ^ localPart.hashCode();
  }
  
  public String toString()
  {
    if (namespaceURI.equals("")) {
      return localPart;
    }
    return "{" + namespaceURI + "}" + localPart;
  }
  
  public static QName valueOf(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("cannot create QName from \"null\" or \"\" String");
    }
    if (paramString.length() == 0) {
      return new QName("", paramString, "");
    }
    if (paramString.charAt(0) != '{') {
      return new QName("", paramString, "");
    }
    if (paramString.startsWith("{}")) {
      throw new IllegalArgumentException("Namespace URI .equals(XMLConstants.NULL_NS_URI), .equals(\"\"), only the local part, \"" + paramString.substring(2 + "".length()) + "\", should be provided.");
    }
    int i = paramString.indexOf('}');
    if (i == -1) {
      throw new IllegalArgumentException("cannot create QName from \"" + paramString + "\", missing closing \"}\"");
    }
    return new QName(paramString.substring(1, i), paramString.substring(i + 1), "");
  }
  
  static
  {
    try
    {
      String str = (String)AccessController.doPrivileged(new PrivilegedAction()
      {
        public Object run()
        {
          return System.getProperty("com.sun.xml.namespace.QName.useCompatibleSerialVersionUID");
        }
      });
      useDefaultSerialVersionUID = (str == null) || (!str.equals("1.0"));
    }
    catch (Exception localException)
    {
      useDefaultSerialVersionUID = true;
    }
    if (useDefaultSerialVersionUID) {
      serialVersionUID = -9120448754896609940L;
    } else {
      serialVersionUID = 4418622981026545151L;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\namespace\QName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */