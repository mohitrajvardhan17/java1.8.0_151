package java.security;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.cert.CertPath;
import java.util.List;

public final class CodeSigner
  implements Serializable
{
  private static final long serialVersionUID = 6819288105193937581L;
  private CertPath signerCertPath;
  private Timestamp timestamp;
  private transient int myhash = -1;
  
  public CodeSigner(CertPath paramCertPath, Timestamp paramTimestamp)
  {
    if (paramCertPath == null) {
      throw new NullPointerException();
    }
    signerCertPath = paramCertPath;
    timestamp = paramTimestamp;
  }
  
  public CertPath getSignerCertPath()
  {
    return signerCertPath;
  }
  
  public Timestamp getTimestamp()
  {
    return timestamp;
  }
  
  public int hashCode()
  {
    if (myhash == -1) {
      if (timestamp == null) {
        myhash = signerCertPath.hashCode();
      } else {
        myhash = (signerCertPath.hashCode() + timestamp.hashCode());
      }
    }
    return myhash;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject == null) || (!(paramObject instanceof CodeSigner))) {
      return false;
    }
    CodeSigner localCodeSigner = (CodeSigner)paramObject;
    if (this == localCodeSigner) {
      return true;
    }
    Timestamp localTimestamp = localCodeSigner.getTimestamp();
    if (timestamp == null)
    {
      if (localTimestamp != null) {
        return false;
      }
    }
    else if ((localTimestamp == null) || (!timestamp.equals(localTimestamp))) {
      return false;
    }
    return signerCertPath.equals(localCodeSigner.getSignerCertPath());
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("(");
    localStringBuffer.append("Signer: " + signerCertPath.getCertificates().get(0));
    if (timestamp != null) {
      localStringBuffer.append("timestamp: " + timestamp);
    }
    localStringBuffer.append(")");
    return localStringBuffer.toString();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    myhash = -1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\CodeSigner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */