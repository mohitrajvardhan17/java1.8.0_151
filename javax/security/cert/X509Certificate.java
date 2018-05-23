package javax.security.cert;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.security.Security;
import java.util.Date;

public abstract class X509Certificate
  extends Certificate
{
  private static final String X509_PROVIDER = "cert.provider.x509v1";
  private static String X509Provider = (String)AccessController.doPrivileged(new PrivilegedAction()
  {
    public String run()
    {
      return Security.getProperty("cert.provider.x509v1");
    }
  });
  
  public X509Certificate() {}
  
  public static final X509Certificate getInstance(InputStream paramInputStream)
    throws CertificateException
  {
    return getInst(paramInputStream);
  }
  
  public static final X509Certificate getInstance(byte[] paramArrayOfByte)
    throws CertificateException
  {
    return getInst(paramArrayOfByte);
  }
  
  private static final X509Certificate getInst(Object paramObject)
    throws CertificateException
  {
    String str = X509Provider;
    if ((str == null) || (str.length() == 0)) {
      str = "com.sun.security.cert.internal.x509.X509V1CertImpl";
    }
    try
    {
      Class[] arrayOfClass = null;
      if ((paramObject instanceof InputStream)) {
        arrayOfClass = new Class[] { InputStream.class };
      } else if ((paramObject instanceof byte[])) {
        arrayOfClass = new Class[] { paramObject.getClass() };
      } else {
        throw new CertificateException("Unsupported argument type");
      }
      Class localClass = Class.forName(str);
      Constructor localConstructor = localClass.getConstructor(arrayOfClass);
      Object localObject = localConstructor.newInstance(new Object[] { paramObject });
      return (X509Certificate)localObject;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new CertificateException("Could not find class: " + localClassNotFoundException);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new CertificateException("Could not access class: " + localIllegalAccessException);
    }
    catch (InstantiationException localInstantiationException)
    {
      throw new CertificateException("Problems instantiating: " + localInstantiationException);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      throw new CertificateException("InvocationTargetException: " + localInvocationTargetException.getTargetException());
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      throw new CertificateException("Could not find class method: " + localNoSuchMethodException.getMessage());
    }
  }
  
  public abstract void checkValidity()
    throws CertificateExpiredException, CertificateNotYetValidException;
  
  public abstract void checkValidity(Date paramDate)
    throws CertificateExpiredException, CertificateNotYetValidException;
  
  public abstract int getVersion();
  
  public abstract BigInteger getSerialNumber();
  
  public abstract Principal getIssuerDN();
  
  public abstract Principal getSubjectDN();
  
  public abstract Date getNotBefore();
  
  public abstract Date getNotAfter();
  
  public abstract String getSigAlgName();
  
  public abstract String getSigAlgOID();
  
  public abstract byte[] getSigAlgParams();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\security\cert\X509Certificate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */