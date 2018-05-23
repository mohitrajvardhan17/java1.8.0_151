package javax.sql.rowset.serial;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Vector;
import javax.sql.rowset.RowSetWarning;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.reflect.misc.ReflectUtil;

public class SerialJavaObject
  implements Serializable, Cloneable
{
  private Object obj;
  private transient Field[] fields;
  static final long serialVersionUID = -1465795139032831023L;
  Vector<RowSetWarning> chain;
  
  public SerialJavaObject(Object paramObject)
    throws SerialException
  {
    Class localClass = paramObject.getClass();
    if (!(paramObject instanceof Serializable)) {
      setWarning(new RowSetWarning("Warning, the object passed to the constructor does not implement Serializable"));
    }
    fields = localClass.getFields();
    if (hasStaticFields(fields)) {
      throw new SerialException("Located static fields in object instance. Cannot serialize");
    }
    obj = paramObject;
  }
  
  public Object getObject()
    throws SerialException
  {
    return obj;
  }
  
  @CallerSensitive
  public Field[] getFields()
    throws SerialException
  {
    if (fields != null)
    {
      Class localClass1 = obj.getClass();
      SecurityManager localSecurityManager = System.getSecurityManager();
      if (localSecurityManager != null)
      {
        Class localClass2 = Reflection.getCallerClass();
        if (ReflectUtil.needsPackageAccessCheck(localClass2.getClassLoader(), localClass1.getClassLoader())) {
          ReflectUtil.checkPackageAccess(localClass1);
        }
      }
      return localClass1.getFields();
    }
    throw new SerialException("SerialJavaObject does not contain a serialized object instance");
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof SerialJavaObject))
    {
      SerialJavaObject localSerialJavaObject = (SerialJavaObject)paramObject;
      return obj.equals(obj);
    }
    return false;
  }
  
  public int hashCode()
  {
    return 31 + obj.hashCode();
  }
  
  public Object clone()
  {
    try
    {
      SerialJavaObject localSerialJavaObject = (SerialJavaObject)super.clone();
      fields = ((Field[])Arrays.copyOf(fields, fields.length));
      if (chain != null) {
        chain = new Vector(chain);
      }
      return localSerialJavaObject;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError();
    }
  }
  
  private void setWarning(RowSetWarning paramRowSetWarning)
  {
    if (chain == null) {
      chain = new Vector();
    }
    chain.add(paramRowSetWarning);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    Vector localVector = (Vector)localGetField.get("chain", null);
    if (localVector != null) {
      chain = new Vector(localVector);
    }
    obj = localGetField.get("obj", null);
    if (obj != null)
    {
      fields = obj.getClass().getFields();
      if (hasStaticFields(fields)) {
        throw new IOException("Located static fields in object instance. Cannot serialize");
      }
    }
    else
    {
      throw new IOException("Object cannot be null!");
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    ObjectOutputStream.PutField localPutField = paramObjectOutputStream.putFields();
    localPutField.put("obj", obj);
    localPutField.put("chain", chain);
    paramObjectOutputStream.writeFields();
  }
  
  private static boolean hasStaticFields(Field[] paramArrayOfField)
  {
    for (Field localField : paramArrayOfField) {
      if (localField.getModifiers() == 8) {
        return true;
      }
    }
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sql\rowset\serial\SerialJavaObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */