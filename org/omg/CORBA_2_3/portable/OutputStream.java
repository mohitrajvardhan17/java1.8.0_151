package org.omg.CORBA_2_3.portable;

import java.io.Serializable;
import java.io.SerializablePermission;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.portable.BoxedValueHelper;

public abstract class OutputStream
  extends org.omg.CORBA.portable.OutputStream
{
  private static final String ALLOW_SUBCLASS_PROP = "jdk.corba.allowOutputStreamSubclass";
  private static final boolean allowSubclass = ((Boolean)AccessController.doPrivileged(new PrivilegedAction()
  {
    public Boolean run()
    {
      String str = System.getProperty("jdk.corba.allowOutputStreamSubclass");
      return Boolean.valueOf(str != null);
    }
  })).booleanValue();
  
  private static Void checkPermission()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if ((localSecurityManager != null) && (!allowSubclass)) {
      localSecurityManager.checkPermission(new SerializablePermission("enableSubclassImplementation"));
    }
    return null;
  }
  
  private OutputStream(Void paramVoid) {}
  
  public OutputStream()
  {
    this(checkPermission());
  }
  
  public void write_value(Serializable paramSerializable)
  {
    throw new NO_IMPLEMENT();
  }
  
  public void write_value(Serializable paramSerializable, Class paramClass)
  {
    throw new NO_IMPLEMENT();
  }
  
  public void write_value(Serializable paramSerializable, String paramString)
  {
    throw new NO_IMPLEMENT();
  }
  
  public void write_value(Serializable paramSerializable, BoxedValueHelper paramBoxedValueHelper)
  {
    throw new NO_IMPLEMENT();
  }
  
  public void write_abstract_interface(Object paramObject)
  {
    throw new NO_IMPLEMENT();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA_2_3\portable\OutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */