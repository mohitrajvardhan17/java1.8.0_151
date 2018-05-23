package java.security.cert;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.util.List;

public class CertPathValidatorException
  extends GeneralSecurityException
{
  private static final long serialVersionUID = -3083180014971893139L;
  private int index = -1;
  private CertPath certPath;
  private Reason reason = BasicReason.UNSPECIFIED;
  
  public CertPathValidatorException()
  {
    this(null, null);
  }
  
  public CertPathValidatorException(String paramString)
  {
    this(paramString, null);
  }
  
  public CertPathValidatorException(Throwable paramThrowable)
  {
    this(paramThrowable == null ? null : paramThrowable.toString(), paramThrowable);
  }
  
  public CertPathValidatorException(String paramString, Throwable paramThrowable)
  {
    this(paramString, paramThrowable, null, -1);
  }
  
  public CertPathValidatorException(String paramString, Throwable paramThrowable, CertPath paramCertPath, int paramInt)
  {
    this(paramString, paramThrowable, paramCertPath, paramInt, BasicReason.UNSPECIFIED);
  }
  
  public CertPathValidatorException(String paramString, Throwable paramThrowable, CertPath paramCertPath, int paramInt, Reason paramReason)
  {
    super(paramString, paramThrowable);
    if ((paramCertPath == null) && (paramInt != -1)) {
      throw new IllegalArgumentException();
    }
    if ((paramInt < -1) || ((paramCertPath != null) && (paramInt >= paramCertPath.getCertificates().size()))) {
      throw new IndexOutOfBoundsException();
    }
    if (paramReason == null) {
      throw new NullPointerException("reason can't be null");
    }
    certPath = paramCertPath;
    index = paramInt;
    reason = paramReason;
  }
  
  public CertPath getCertPath()
  {
    return certPath;
  }
  
  public int getIndex()
  {
    return index;
  }
  
  public Reason getReason()
  {
    return reason;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException
  {
    paramObjectInputStream.defaultReadObject();
    if (reason == null) {
      reason = BasicReason.UNSPECIFIED;
    }
    if ((certPath == null) && (index != -1)) {
      throw new InvalidObjectException("certpath is null and index != -1");
    }
    if ((index < -1) || ((certPath != null) && (index >= certPath.getCertificates().size()))) {
      throw new InvalidObjectException("index out of range");
    }
  }
  
  public static enum BasicReason
    implements CertPathValidatorException.Reason
  {
    UNSPECIFIED,  EXPIRED,  NOT_YET_VALID,  REVOKED,  UNDETERMINED_REVOCATION_STATUS,  INVALID_SIGNATURE,  ALGORITHM_CONSTRAINED;
    
    private BasicReason() {}
  }
  
  public static abstract interface Reason
    extends Serializable
  {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\cert\CertPathValidatorException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */