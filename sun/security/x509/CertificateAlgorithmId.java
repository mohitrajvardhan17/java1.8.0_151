package sun.security.x509;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class CertificateAlgorithmId
  implements CertAttrSet<String>
{
  private AlgorithmId algId;
  public static final String IDENT = "x509.info.algorithmID";
  public static final String NAME = "algorithmID";
  public static final String ALGORITHM = "algorithm";
  
  public CertificateAlgorithmId(AlgorithmId paramAlgorithmId)
  {
    algId = paramAlgorithmId;
  }
  
  public CertificateAlgorithmId(DerInputStream paramDerInputStream)
    throws IOException
  {
    DerValue localDerValue = paramDerInputStream.getDerValue();
    algId = AlgorithmId.parse(localDerValue);
  }
  
  public CertificateAlgorithmId(InputStream paramInputStream)
    throws IOException
  {
    DerValue localDerValue = new DerValue(paramInputStream);
    algId = AlgorithmId.parse(localDerValue);
  }
  
  public String toString()
  {
    if (algId == null) {
      return "";
    }
    return algId.toString() + ", OID = " + algId.getOID().toString() + "\n";
  }
  
  public void encode(OutputStream paramOutputStream)
    throws IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    algId.encode(localDerOutputStream);
    paramOutputStream.write(localDerOutputStream.toByteArray());
  }
  
  public void set(String paramString, Object paramObject)
    throws IOException
  {
    if (!(paramObject instanceof AlgorithmId)) {
      throw new IOException("Attribute must be of type AlgorithmId.");
    }
    if (paramString.equalsIgnoreCase("algorithm")) {
      algId = ((AlgorithmId)paramObject);
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet:CertificateAlgorithmId.");
    }
  }
  
  public AlgorithmId get(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("algorithm")) {
      return algId;
    }
    throw new IOException("Attribute name not recognized by CertAttrSet:CertificateAlgorithmId.");
  }
  
  public void delete(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("algorithm")) {
      algId = null;
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet:CertificateAlgorithmId.");
    }
  }
  
  public Enumeration<String> getElements()
  {
    AttributeNameEnumeration localAttributeNameEnumeration = new AttributeNameEnumeration();
    localAttributeNameEnumeration.addElement("algorithm");
    return localAttributeNameEnumeration.elements();
  }
  
  public String getName()
  {
    return "algorithmID";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\CertificateAlgorithmId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */