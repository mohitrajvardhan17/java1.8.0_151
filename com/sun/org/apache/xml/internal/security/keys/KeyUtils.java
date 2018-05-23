package com.sun.org.apache.xml.internal.security.keys;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.KeyName;
import com.sun.org.apache.xml.internal.security.keys.content.KeyValue;
import com.sun.org.apache.xml.internal.security.keys.content.MgmtData;
import com.sun.org.apache.xml.internal.security.keys.content.X509Data;
import java.io.PrintStream;
import java.security.PublicKey;

public class KeyUtils
{
  private KeyUtils() {}
  
  public static void prinoutKeyInfo(KeyInfo paramKeyInfo, PrintStream paramPrintStream)
    throws XMLSecurityException
  {
    Object localObject;
    for (int i = 0; i < paramKeyInfo.lengthKeyName(); i++)
    {
      localObject = paramKeyInfo.itemKeyName(i);
      paramPrintStream.println("KeyName(" + i + ")=\"" + ((KeyName)localObject).getKeyName() + "\"");
    }
    for (i = 0; i < paramKeyInfo.lengthKeyValue(); i++)
    {
      localObject = paramKeyInfo.itemKeyValue(i);
      PublicKey localPublicKey = ((KeyValue)localObject).getPublicKey();
      paramPrintStream.println("KeyValue Nr. " + i);
      paramPrintStream.println(localPublicKey);
    }
    for (i = 0; i < paramKeyInfo.lengthMgmtData(); i++)
    {
      localObject = paramKeyInfo.itemMgmtData(i);
      paramPrintStream.println("MgmtData(" + i + ")=\"" + ((MgmtData)localObject).getMgmtData() + "\"");
    }
    for (i = 0; i < paramKeyInfo.lengthX509Data(); i++)
    {
      localObject = paramKeyInfo.itemX509Data(i);
      paramPrintStream.println("X509Data(" + i + ")=\"" + (((X509Data)localObject).containsCertificate() ? "Certificate " : "") + (((X509Data)localObject).containsIssuerSerial() ? "IssuerSerial " : "") + "\"");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\keys\KeyUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */