package java.security;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.cert.CertPath;
import java.util.Date;
import java.util.List;

public final class Timestamp
  implements Serializable
{
  private static final long serialVersionUID = -5502683707821851294L;
  private Date timestamp;
  private CertPath signerCertPath;
  private transient int myhash = -1;
  
  public Timestamp(Date paramDate, CertPath paramCertPath)
  {
    if ((paramDate == null) || (paramCertPath == null)) {
      throw new NullPointerException();
    }
    timestamp = new Date(paramDate.getTime());
    signerCertPath = paramCertPath;
  }
  
  public Date getTimestamp()
  {
    return new Date(timestamp.getTime());
  }
  
  public CertPath getSignerCertPath()
  {
    return signerCertPath;
  }
  
  public int hashCode()
  {
    if (myhash == -1) {
      myhash = (timestamp.hashCode() + signerCertPath.hashCode());
    }
    return myhash;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject == null) || (!(paramObject instanceof Timestamp))) {
      return false;
    }
    Timestamp localTimestamp = (Timestamp)paramObject;
    if (this == localTimestamp) {
      return true;
    }
    return (timestamp.equals(localTimestamp.getTimestamp())) && (signerCertPath.equals(localTimestamp.getSignerCertPath()));
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("(");
    localStringBuffer.append("timestamp: " + timestamp);
    List localList = signerCertPath.getCertificates();
    if (!localList.isEmpty()) {
      localStringBuffer.append("TSA: " + localList.get(0));
    } else {
      localStringBuffer.append("TSA: <empty>");
    }
    localStringBuffer.append(")");
    return localStringBuffer.toString();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    myhash = -1;
    timestamp = new Date(timestamp.getTime());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\Timestamp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */