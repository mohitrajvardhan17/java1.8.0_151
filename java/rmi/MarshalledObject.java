package java.rmi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.ObjectInputFilter;
import sun.misc.ObjectInputFilter.Config;
import sun.rmi.server.MarshalInputStream;
import sun.rmi.server.MarshalOutputStream;

public final class MarshalledObject<T>
  implements Serializable
{
  private byte[] objBytes = null;
  private byte[] locBytes = null;
  private int hash;
  private transient ObjectInputFilter objectInputFilter = null;
  private static final long serialVersionUID = 8988374069173025854L;
  
  public MarshalledObject(T paramT)
    throws IOException
  {
    if (paramT == null)
    {
      hash = 13;
      return;
    }
    ByteArrayOutputStream localByteArrayOutputStream1 = new ByteArrayOutputStream();
    ByteArrayOutputStream localByteArrayOutputStream2 = new ByteArrayOutputStream();
    MarshalledObjectOutputStream localMarshalledObjectOutputStream = new MarshalledObjectOutputStream(localByteArrayOutputStream1, localByteArrayOutputStream2);
    localMarshalledObjectOutputStream.writeObject(paramT);
    localMarshalledObjectOutputStream.flush();
    objBytes = localByteArrayOutputStream1.toByteArray();
    locBytes = (localMarshalledObjectOutputStream.hadAnnotations() ? localByteArrayOutputStream2.toByteArray() : null);
    int i = 0;
    for (int j = 0; j < objBytes.length; j++) {
      i = 31 * i + objBytes[j];
    }
    hash = i;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    objectInputFilter = ObjectInputFilter.Config.getObjectInputFilter(paramObjectInputStream);
  }
  
  public T get()
    throws IOException, ClassNotFoundException
  {
    if (objBytes == null) {
      return null;
    }
    ByteArrayInputStream localByteArrayInputStream1 = new ByteArrayInputStream(objBytes);
    ByteArrayInputStream localByteArrayInputStream2 = locBytes == null ? null : new ByteArrayInputStream(locBytes);
    MarshalledObjectInputStream localMarshalledObjectInputStream = new MarshalledObjectInputStream(localByteArrayInputStream1, localByteArrayInputStream2, objectInputFilter);
    Object localObject = localMarshalledObjectInputStream.readObject();
    localMarshalledObjectInputStream.close();
    return (T)localObject;
  }
  
  public int hashCode()
  {
    return hash;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if ((paramObject != null) && ((paramObject instanceof MarshalledObject)))
    {
      MarshalledObject localMarshalledObject = (MarshalledObject)paramObject;
      if ((objBytes == null) || (objBytes == null)) {
        return objBytes == objBytes;
      }
      if (objBytes.length != objBytes.length) {
        return false;
      }
      for (int i = 0; i < objBytes.length; i++) {
        if (objBytes[i] != objBytes[i]) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
  
  private static class MarshalledObjectInputStream
    extends MarshalInputStream
  {
    private ObjectInputStream locIn;
    
    MarshalledObjectInputStream(InputStream paramInputStream1, InputStream paramInputStream2, final ObjectInputFilter paramObjectInputFilter)
      throws IOException
    {
      super();
      locIn = (paramInputStream2 == null ? null : new ObjectInputStream(paramInputStream2));
      if (paramObjectInputFilter != null) {
        AccessController.doPrivileged(new PrivilegedAction()
        {
          public Void run()
          {
            ObjectInputFilter.Config.setObjectInputFilter(MarshalledObject.MarshalledObjectInputStream.this, paramObjectInputFilter);
            if (locIn != null) {
              ObjectInputFilter.Config.setObjectInputFilter(locIn, paramObjectInputFilter);
            }
            return null;
          }
        });
      }
    }
    
    protected Object readLocation()
      throws IOException, ClassNotFoundException
    {
      return locIn == null ? null : locIn.readObject();
    }
  }
  
  private static class MarshalledObjectOutputStream
    extends MarshalOutputStream
  {
    private ObjectOutputStream locOut;
    private boolean hadAnnotations;
    
    MarshalledObjectOutputStream(OutputStream paramOutputStream1, OutputStream paramOutputStream2)
      throws IOException
    {
      super();
      useProtocolVersion(2);
      locOut = new ObjectOutputStream(paramOutputStream2);
      hadAnnotations = false;
    }
    
    boolean hadAnnotations()
    {
      return hadAnnotations;
    }
    
    protected void writeLocation(String paramString)
      throws IOException
    {
      hadAnnotations |= paramString != null;
      locOut.writeObject(paramString);
    }
    
    public void flush()
      throws IOException
    {
      super.flush();
      locOut.flush();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\MarshalledObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */