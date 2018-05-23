package com.sun.xml.internal.stream;

import com.sun.org.apache.xerces.internal.impl.PropertyManager;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.util.URI;
import com.sun.org.apache.xerces.internal.util.URI.MalformedURIException;
import com.sun.org.apache.xerces.internal.util.XMLResourceIdentifierImpl;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class XMLEntityStorage
{
  protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  protected static final String WARN_ON_DUPLICATE_ENTITYDEF = "http://apache.org/xml/features/warn-on-duplicate-entitydef";
  protected boolean fWarnDuplicateEntityDef;
  protected Map<String, Entity> fEntities = new HashMap();
  protected Entity.ScannedEntity fCurrentEntity;
  private XMLEntityManager fEntityManager;
  protected XMLErrorReporter fErrorReporter;
  protected PropertyManager fPropertyManager;
  protected boolean fInExternalSubset = false;
  private static String gUserDir;
  private static String gEscapedUserDir;
  private static boolean[] gNeedEscaping = new boolean[''];
  private static char[] gAfterEscaping1 = new char[''];
  private static char[] gAfterEscaping2 = new char[''];
  private static char[] gHexChs = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
  
  public XMLEntityStorage(PropertyManager paramPropertyManager)
  {
    fPropertyManager = paramPropertyManager;
  }
  
  public XMLEntityStorage(XMLEntityManager paramXMLEntityManager)
  {
    fEntityManager = paramXMLEntityManager;
  }
  
  public void reset(PropertyManager paramPropertyManager)
  {
    fErrorReporter = ((XMLErrorReporter)paramPropertyManager.getProperty("http://apache.org/xml/properties/internal/error-reporter"));
    fEntities.clear();
    fCurrentEntity = null;
  }
  
  public void reset()
  {
    fEntities.clear();
    fCurrentEntity = null;
  }
  
  public void reset(XMLComponentManager paramXMLComponentManager)
    throws XMLConfigurationException
  {
    fWarnDuplicateEntityDef = paramXMLComponentManager.getFeature("http://apache.org/xml/features/warn-on-duplicate-entitydef", false);
    fErrorReporter = ((XMLErrorReporter)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter"));
    fEntities.clear();
    fCurrentEntity = null;
  }
  
  public Entity getEntity(String paramString)
  {
    return (Entity)fEntities.get(paramString);
  }
  
  public boolean hasEntities()
  {
    return fEntities != null;
  }
  
  public int getEntitySize()
  {
    return fEntities.size();
  }
  
  public Enumeration getEntityKeys()
  {
    return Collections.enumeration(fEntities.keySet());
  }
  
  public void addInternalEntity(String paramString1, String paramString2)
  {
    if (!fEntities.containsKey(paramString1))
    {
      Entity.InternalEntity localInternalEntity = new Entity.InternalEntity(paramString1, paramString2, fInExternalSubset);
      fEntities.put(paramString1, localInternalEntity);
    }
    else if (fWarnDuplicateEntityDef)
    {
      fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ENTITY_DEFINITION", new Object[] { paramString1 }, (short)0);
    }
  }
  
  public void addExternalEntity(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    if (!fEntities.containsKey(paramString1))
    {
      if ((paramString4 == null) && (fCurrentEntity != null) && (fCurrentEntity.entityLocation != null)) {
        paramString4 = fCurrentEntity.entityLocation.getExpandedSystemId();
      }
      fCurrentEntity = fEntityManager.getCurrentEntity();
      Entity.ExternalEntity localExternalEntity = new Entity.ExternalEntity(paramString1, new XMLResourceIdentifierImpl(paramString2, paramString3, paramString4, expandSystemId(paramString3, paramString4)), null, fInExternalSubset);
      fEntities.put(paramString1, localExternalEntity);
    }
    else if (fWarnDuplicateEntityDef)
    {
      fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ENTITY_DEFINITION", new Object[] { paramString1 }, (short)0);
    }
  }
  
  public boolean isExternalEntity(String paramString)
  {
    Entity localEntity = (Entity)fEntities.get(paramString);
    if (localEntity == null) {
      return false;
    }
    return localEntity.isExternal();
  }
  
  public boolean isEntityDeclInExternalSubset(String paramString)
  {
    Entity localEntity = (Entity)fEntities.get(paramString);
    if (localEntity == null) {
      return false;
    }
    return localEntity.isEntityDeclInExternalSubset();
  }
  
  public void addUnparsedEntity(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    fCurrentEntity = fEntityManager.getCurrentEntity();
    if (!fEntities.containsKey(paramString1))
    {
      Entity.ExternalEntity localExternalEntity = new Entity.ExternalEntity(paramString1, new XMLResourceIdentifierImpl(paramString2, paramString3, paramString4, null), paramString5, fInExternalSubset);
      fEntities.put(paramString1, localExternalEntity);
    }
    else if (fWarnDuplicateEntityDef)
    {
      fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ENTITY_DEFINITION", new Object[] { paramString1 }, (short)0);
    }
  }
  
  public boolean isUnparsedEntity(String paramString)
  {
    Entity localEntity = (Entity)fEntities.get(paramString);
    if (localEntity == null) {
      return false;
    }
    return localEntity.isUnparsed();
  }
  
  public boolean isDeclaredEntity(String paramString)
  {
    Entity localEntity = (Entity)fEntities.get(paramString);
    return localEntity != null;
  }
  
  public static String expandSystemId(String paramString)
  {
    return expandSystemId(paramString, null);
  }
  
  private static synchronized String getUserDir()
  {
    String str = "";
    try
    {
      str = SecuritySupport.getSystemProperty("user.dir");
    }
    catch (SecurityException localSecurityException) {}
    if (str.length() == 0) {
      return "";
    }
    if (str.equals(gUserDir)) {
      return gEscapedUserDir;
    }
    gUserDir = str;
    char c = File.separatorChar;
    str = str.replace(c, '/');
    int i = str.length();
    StringBuffer localStringBuffer = new StringBuffer(i * 3);
    int j;
    if ((i >= 2) && (str.charAt(1) == ':'))
    {
      j = Character.toUpperCase(str.charAt(0));
      if ((j >= 65) && (j <= 90)) {
        localStringBuffer.append('/');
      }
    }
    for (int k = 0; k < i; k++)
    {
      j = str.charAt(k);
      if (j >= 128) {
        break;
      }
      if (gNeedEscaping[j] != 0)
      {
        localStringBuffer.append('%');
        localStringBuffer.append(gAfterEscaping1[j]);
        localStringBuffer.append(gAfterEscaping2[j]);
      }
      else
      {
        localStringBuffer.append((char)j);
      }
    }
    if (k < i)
    {
      byte[] arrayOfByte = null;
      try
      {
        arrayOfByte = str.substring(k).getBytes("UTF-8");
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException)
      {
        return str;
      }
      i = arrayOfByte.length;
      for (k = 0; k < i; k++)
      {
        int m = arrayOfByte[k];
        if (m < 0)
        {
          j = m + 256;
          localStringBuffer.append('%');
          localStringBuffer.append(gHexChs[(j >> 4)]);
          localStringBuffer.append(gHexChs[(j & 0xF)]);
        }
        else if (gNeedEscaping[m] != 0)
        {
          localStringBuffer.append('%');
          localStringBuffer.append(gAfterEscaping1[m]);
          localStringBuffer.append(gAfterEscaping2[m]);
        }
        else
        {
          localStringBuffer.append((char)m);
        }
      }
    }
    if (!str.endsWith("/")) {
      localStringBuffer.append('/');
    }
    gEscapedUserDir = localStringBuffer.toString();
    return gEscapedUserDir;
  }
  
  public static String expandSystemId(String paramString1, String paramString2)
  {
    if ((paramString1 == null) || (paramString1.length() == 0)) {
      return paramString1;
    }
    try
    {
      new URI(paramString1);
      return paramString1;
    }
    catch (URI.MalformedURIException localMalformedURIException1)
    {
      String str1 = fixURI(paramString1);
      URI localURI1 = null;
      URI localURI2 = null;
      try
      {
        if ((paramString2 == null) || (paramString2.length() == 0) || (paramString2.equals(paramString1)))
        {
          String str2 = getUserDir();
          localURI1 = new URI("file", "", str2, null, null);
        }
        else
        {
          try
          {
            localURI1 = new URI(fixURI(paramString2));
          }
          catch (URI.MalformedURIException localMalformedURIException2)
          {
            if (paramString2.indexOf(':') != -1)
            {
              localURI1 = new URI("file", "", fixURI(paramString2), null, null);
            }
            else
            {
              String str3 = getUserDir();
              str3 = str3 + fixURI(paramString2);
              localURI1 = new URI("file", "", str3, null, null);
            }
          }
        }
        localURI2 = new URI(localURI1, str1);
      }
      catch (Exception localException) {}
      if (localURI2 == null) {
        return paramString1;
      }
      return localURI2.toString();
    }
  }
  
  protected static String fixURI(String paramString)
  {
    paramString = paramString.replace(File.separatorChar, '/');
    if (paramString.length() >= 2)
    {
      int i = paramString.charAt(1);
      if (i == 58)
      {
        int j = Character.toUpperCase(paramString.charAt(0));
        if ((j >= 65) && (j <= 90)) {
          paramString = "/" + paramString;
        }
      }
      else if ((i == 47) && (paramString.charAt(0) == '/'))
      {
        paramString = "file:" + paramString;
      }
    }
    return paramString;
  }
  
  public void startExternalSubset()
  {
    fInExternalSubset = true;
  }
  
  public void endExternalSubset()
  {
    fInExternalSubset = false;
  }
  
  static
  {
    for (int i = 0; i <= 31; i++)
    {
      gNeedEscaping[i] = true;
      gAfterEscaping1[i] = gHexChs[(i >> 4)];
      gAfterEscaping2[i] = gHexChs[(i & 0xF)];
    }
    gNeedEscaping[127] = true;
    gAfterEscaping1[127] = '7';
    gAfterEscaping2[127] = 'F';
    for (int k : new char[] { ' ', '<', '>', '#', '%', '"', '{', '}', '|', '\\', '^', '~', '[', ']', '`' })
    {
      gNeedEscaping[k] = true;
      gAfterEscaping1[k] = gHexChs[(k >> 4)];
      gAfterEscaping2[k] = gHexChs[(k & 0xF)];
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\XMLEntityStorage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */