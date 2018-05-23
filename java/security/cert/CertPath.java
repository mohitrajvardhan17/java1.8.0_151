package java.security.cert;

import java.io.ByteArrayInputStream;
import java.io.NotSerializableException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public abstract class CertPath
  implements Serializable
{
  private static final long serialVersionUID = 6068470306649138683L;
  private String type;
  
  protected CertPath(String paramString)
  {
    type = paramString;
  }
  
  public String getType()
  {
    return type;
  }
  
  public abstract Iterator<String> getEncodings();
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof CertPath)) {
      return false;
    }
    CertPath localCertPath = (CertPath)paramObject;
    if (!localCertPath.getType().equals(type)) {
      return false;
    }
    List localList1 = getCertificates();
    List localList2 = localCertPath.getCertificates();
    return localList1.equals(localList2);
  }
  
  public int hashCode()
  {
    int i = type.hashCode();
    i = 31 * i + getCertificates().hashCode();
    return i;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    Iterator localIterator = getCertificates().iterator();
    localStringBuffer.append("\n" + type + " Cert Path: length = " + getCertificates().size() + ".\n");
    localStringBuffer.append("[\n");
    for (int i = 1; localIterator.hasNext(); i++)
    {
      localStringBuffer.append("=========================================================Certificate " + i + " start.\n");
      Certificate localCertificate = (Certificate)localIterator.next();
      localStringBuffer.append(localCertificate.toString());
      localStringBuffer.append("\n=========================================================Certificate " + i + " end.\n\n\n");
    }
    localStringBuffer.append("\n]");
    return localStringBuffer.toString();
  }
  
  public abstract byte[] getEncoded()
    throws CertificateEncodingException;
  
  public abstract byte[] getEncoded(String paramString)
    throws CertificateEncodingException;
  
  public abstract List<? extends Certificate> getCertificates();
  
  protected Object writeReplace()
    throws ObjectStreamException
  {
    try
    {
      return new CertPathRep(type, getEncoded());
    }
    catch (CertificateException localCertificateException)
    {
      NotSerializableException localNotSerializableException = new NotSerializableException("java.security.cert.CertPath: " + type);
      localNotSerializableException.initCause(localCertificateException);
      throw localNotSerializableException;
    }
  }
  
  protected static class CertPathRep
    implements Serializable
  {
    private static final long serialVersionUID = 3015633072427920915L;
    private String type;
    private byte[] data;
    
    protected CertPathRep(String paramString, byte[] paramArrayOfByte)
    {
      type = paramString;
      data = paramArrayOfByte;
    }
    
    protected Object readResolve()
      throws ObjectStreamException
    {
      try
      {
        CertificateFactory localCertificateFactory = CertificateFactory.getInstance(type);
        return localCertificateFactory.generateCertPath(new ByteArrayInputStream(data));
      }
      catch (CertificateException localCertificateException)
      {
        NotSerializableException localNotSerializableException = new NotSerializableException("java.security.cert.CertPath: " + type);
        localNotSerializableException.initCause(localCertificateException);
        throw localNotSerializableException;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\cert\CertPath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */