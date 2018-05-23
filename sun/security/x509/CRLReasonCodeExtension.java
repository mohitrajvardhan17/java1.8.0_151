package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.CRLReason;
import java.util.Enumeration;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class CRLReasonCodeExtension
  extends Extension
  implements CertAttrSet<String>
{
  public static final String NAME = "CRLReasonCode";
  public static final String REASON = "reason";
  private static CRLReason[] values = ;
  private int reasonCode = 0;
  
  private void encodeThis()
    throws IOException
  {
    if (reasonCode == 0)
    {
      extensionValue = null;
      return;
    }
    DerOutputStream localDerOutputStream = new DerOutputStream();
    localDerOutputStream.putEnumerated(reasonCode);
    extensionValue = localDerOutputStream.toByteArray();
  }
  
  public CRLReasonCodeExtension(int paramInt)
    throws IOException
  {
    this(false, paramInt);
  }
  
  public CRLReasonCodeExtension(boolean paramBoolean, int paramInt)
    throws IOException
  {
    extensionId = PKIXExtensions.ReasonCode_Id;
    critical = paramBoolean;
    reasonCode = paramInt;
    encodeThis();
  }
  
  public CRLReasonCodeExtension(Boolean paramBoolean, Object paramObject)
    throws IOException
  {
    extensionId = PKIXExtensions.ReasonCode_Id;
    critical = paramBoolean.booleanValue();
    extensionValue = ((byte[])paramObject);
    DerValue localDerValue = new DerValue(extensionValue);
    reasonCode = localDerValue.getEnumerated();
  }
  
  public void set(String paramString, Object paramObject)
    throws IOException
  {
    if (!(paramObject instanceof Integer)) {
      throw new IOException("Attribute must be of type Integer.");
    }
    if (paramString.equalsIgnoreCase("reason")) {
      reasonCode = ((Integer)paramObject).intValue();
    } else {
      throw new IOException("Name not supported by CRLReasonCodeExtension");
    }
    encodeThis();
  }
  
  public Integer get(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("reason")) {
      return new Integer(reasonCode);
    }
    throw new IOException("Name not supported by CRLReasonCodeExtension");
  }
  
  public void delete(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("reason")) {
      reasonCode = 0;
    } else {
      throw new IOException("Name not supported by CRLReasonCodeExtension");
    }
    encodeThis();
  }
  
  public String toString()
  {
    return super.toString() + "    Reason Code: " + getReasonCode();
  }
  
  public void encode(OutputStream paramOutputStream)
    throws IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    if (extensionValue == null)
    {
      extensionId = PKIXExtensions.ReasonCode_Id;
      critical = false;
      encodeThis();
    }
    super.encode(localDerOutputStream);
    paramOutputStream.write(localDerOutputStream.toByteArray());
  }
  
  public Enumeration<String> getElements()
  {
    AttributeNameEnumeration localAttributeNameEnumeration = new AttributeNameEnumeration();
    localAttributeNameEnumeration.addElement("reason");
    return localAttributeNameEnumeration.elements();
  }
  
  public String getName()
  {
    return "CRLReasonCode";
  }
  
  public CRLReason getReasonCode()
  {
    if ((reasonCode > 0) && (reasonCode < values.length)) {
      return values[reasonCode];
    }
    return CRLReason.UNSPECIFIED;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\CRLReasonCodeExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */