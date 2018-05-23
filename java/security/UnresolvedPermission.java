package java.security;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import sun.misc.IOUtils;
import sun.security.util.Debug;

public final class UnresolvedPermission
  extends Permission
  implements Serializable
{
  private static final long serialVersionUID = -4821973115467008846L;
  private static final Debug debug = Debug.getInstance("policy,access", "UnresolvedPermission");
  private String type;
  private String name;
  private String actions;
  private transient Certificate[] certs;
  private static final Class[] PARAMS0 = new Class[0];
  private static final Class[] PARAMS1 = { String.class };
  private static final Class[] PARAMS2 = { String.class, String.class };
  
  public UnresolvedPermission(String paramString1, String paramString2, String paramString3, Certificate[] paramArrayOfCertificate)
  {
    super(paramString1);
    if (paramString1 == null) {
      throw new NullPointerException("type can't be null");
    }
    type = paramString1;
    name = paramString2;
    actions = paramString3;
    if (paramArrayOfCertificate != null)
    {
      for (int i = 0; i < paramArrayOfCertificate.length; i++) {
        if (!(paramArrayOfCertificate[i] instanceof X509Certificate))
        {
          certs = ((Certificate[])paramArrayOfCertificate.clone());
          break;
        }
      }
      if (certs == null)
      {
        i = 0;
        int j = 0;
        while (i < paramArrayOfCertificate.length)
        {
          j++;
          while ((i + 1 < paramArrayOfCertificate.length) && (((X509Certificate)paramArrayOfCertificate[i]).getIssuerDN().equals(((X509Certificate)paramArrayOfCertificate[(i + 1)]).getSubjectDN()))) {
            i++;
          }
          i++;
        }
        if (j == paramArrayOfCertificate.length) {
          certs = ((Certificate[])paramArrayOfCertificate.clone());
        }
        if (certs == null)
        {
          ArrayList localArrayList = new ArrayList();
          for (i = 0; i < paramArrayOfCertificate.length; i++)
          {
            localArrayList.add(paramArrayOfCertificate[i]);
            while ((i + 1 < paramArrayOfCertificate.length) && (((X509Certificate)paramArrayOfCertificate[i]).getIssuerDN().equals(((X509Certificate)paramArrayOfCertificate[(i + 1)]).getSubjectDN()))) {
              i++;
            }
          }
          certs = new Certificate[localArrayList.size()];
          localArrayList.toArray(certs);
        }
      }
    }
  }
  
  Permission resolve(Permission paramPermission, Certificate[] paramArrayOfCertificate)
  {
    if (certs != null)
    {
      if (paramArrayOfCertificate == null) {
        return null;
      }
      for (int j = 0; j < certs.length; j++)
      {
        int i = 0;
        for (int k = 0; k < paramArrayOfCertificate.length; k++) {
          if (certs[j].equals(paramArrayOfCertificate[k]))
          {
            i = 1;
            break;
          }
        }
        if (i == 0) {
          return null;
        }
      }
    }
    try
    {
      Class localClass = paramPermission.getClass();
      if ((name == null) && (actions == null)) {
        try
        {
          Constructor localConstructor1 = localClass.getConstructor(PARAMS0);
          return (Permission)localConstructor1.newInstance(new Object[0]);
        }
        catch (NoSuchMethodException localNoSuchMethodException2)
        {
          try
          {
            Constructor localConstructor4 = localClass.getConstructor(PARAMS1);
            return (Permission)localConstructor4.newInstance(new Object[] { name });
          }
          catch (NoSuchMethodException localNoSuchMethodException4)
          {
            Constructor localConstructor6 = localClass.getConstructor(PARAMS2);
            return (Permission)localConstructor6.newInstance(new Object[] { name, actions });
          }
        }
      }
      if ((name != null) && (actions == null)) {
        try
        {
          Constructor localConstructor2 = localClass.getConstructor(PARAMS1);
          return (Permission)localConstructor2.newInstance(new Object[] { name });
        }
        catch (NoSuchMethodException localNoSuchMethodException3)
        {
          Constructor localConstructor5 = localClass.getConstructor(PARAMS2);
          return (Permission)localConstructor5.newInstance(new Object[] { name, actions });
        }
      }
      Constructor localConstructor3 = localClass.getConstructor(PARAMS2);
      return (Permission)localConstructor3.newInstance(new Object[] { name, actions });
    }
    catch (NoSuchMethodException localNoSuchMethodException1)
    {
      if (debug != null)
      {
        debug.println("NoSuchMethodException:\n  could not find proper constructor for " + type);
        localNoSuchMethodException1.printStackTrace();
      }
      return null;
    }
    catch (Exception localException)
    {
      if (debug != null)
      {
        debug.println("unable to instantiate " + name);
        localException.printStackTrace();
      }
    }
    return null;
  }
  
  public boolean implies(Permission paramPermission)
  {
    return false;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof UnresolvedPermission)) {
      return false;
    }
    UnresolvedPermission localUnresolvedPermission = (UnresolvedPermission)paramObject;
    if (!type.equals(type)) {
      return false;
    }
    if (name == null)
    {
      if (name != null) {
        return false;
      }
    }
    else if (!name.equals(name)) {
      return false;
    }
    if (actions == null)
    {
      if (actions != null) {
        return false;
      }
    }
    else if (!actions.equals(actions)) {
      return false;
    }
    if (((certs == null) && (certs != null)) || ((certs != null) && (certs == null)) || ((certs != null) && (certs != null) && (certs.length != certs.length))) {
      return false;
    }
    int k;
    int j;
    for (int i = 0; (certs != null) && (i < certs.length); i++)
    {
      k = 0;
      for (j = 0; j < certs.length; j++) {
        if (certs[i].equals(certs[j]))
        {
          k = 1;
          break;
        }
      }
      if (k == 0) {
        return false;
      }
    }
    for (i = 0; (certs != null) && (i < certs.length); i++)
    {
      k = 0;
      for (j = 0; j < certs.length; j++) {
        if (certs[i].equals(certs[j]))
        {
          k = 1;
          break;
        }
      }
      if (k == 0) {
        return false;
      }
    }
    return true;
  }
  
  public int hashCode()
  {
    int i = type.hashCode();
    if (name != null) {
      i ^= name.hashCode();
    }
    if (actions != null) {
      i ^= actions.hashCode();
    }
    return i;
  }
  
  public String getActions()
  {
    return "";
  }
  
  public String getUnresolvedType()
  {
    return type;
  }
  
  public String getUnresolvedName()
  {
    return name;
  }
  
  public String getUnresolvedActions()
  {
    return actions;
  }
  
  public Certificate[] getUnresolvedCerts()
  {
    return certs == null ? null : (Certificate[])certs.clone();
  }
  
  public String toString()
  {
    return "(unresolved " + type + " " + name + " " + actions + ")";
  }
  
  public PermissionCollection newPermissionCollection()
  {
    return new UnresolvedPermissionCollection();
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    if ((certs == null) || (certs.length == 0))
    {
      paramObjectOutputStream.writeInt(0);
    }
    else
    {
      paramObjectOutputStream.writeInt(certs.length);
      for (int i = 0; i < certs.length; i++)
      {
        Certificate localCertificate = certs[i];
        try
        {
          paramObjectOutputStream.writeUTF(localCertificate.getType());
          byte[] arrayOfByte = localCertificate.getEncoded();
          paramObjectOutputStream.writeInt(arrayOfByte.length);
          paramObjectOutputStream.write(arrayOfByte);
        }
        catch (CertificateEncodingException localCertificateEncodingException)
        {
          throw new IOException(localCertificateEncodingException.getMessage());
        }
      }
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    Hashtable localHashtable = null;
    ArrayList localArrayList = null;
    paramObjectInputStream.defaultReadObject();
    if (type == null) {
      throw new NullPointerException("type can't be null");
    }
    int i = paramObjectInputStream.readInt();
    if (i > 0)
    {
      localHashtable = new Hashtable(3);
      localArrayList = new ArrayList(i > 20 ? 20 : i);
    }
    else if (i < 0)
    {
      throw new IOException("size cannot be negative");
    }
    for (int j = 0; j < i; j++)
    {
      String str = paramObjectInputStream.readUTF();
      CertificateFactory localCertificateFactory;
      if (localHashtable.containsKey(str))
      {
        localCertificateFactory = (CertificateFactory)localHashtable.get(str);
      }
      else
      {
        try
        {
          localCertificateFactory = CertificateFactory.getInstance(str);
        }
        catch (CertificateException localCertificateException1)
        {
          throw new ClassNotFoundException("Certificate factory for " + str + " not found");
        }
        localHashtable.put(str, localCertificateFactory);
      }
      byte[] arrayOfByte = IOUtils.readNBytes(paramObjectInputStream, paramObjectInputStream.readInt());
      ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
      try
      {
        localArrayList.add(localCertificateFactory.generateCertificate(localByteArrayInputStream));
      }
      catch (CertificateException localCertificateException2)
      {
        throw new IOException(localCertificateException2.getMessage());
      }
      localByteArrayInputStream.close();
    }
    if (localArrayList != null) {
      certs = ((Certificate[])localArrayList.toArray(new Certificate[i]));
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\UnresolvedPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */