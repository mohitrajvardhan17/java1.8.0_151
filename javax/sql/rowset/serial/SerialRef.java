package javax.sql.rowset.serial;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.Serializable;
import java.sql.Ref;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;

public class SerialRef
  implements Ref, Serializable, Cloneable
{
  private String baseTypeName;
  private Object object;
  private Ref reference;
  static final long serialVersionUID = -4727123500609662274L;
  
  public SerialRef(Ref paramRef)
    throws SerialException, SQLException
  {
    if (paramRef == null) {
      throw new SQLException("Cannot instantiate a SerialRef object with a null Ref object");
    }
    reference = paramRef;
    object = paramRef;
    if (paramRef.getBaseTypeName() == null) {
      throw new SQLException("Cannot instantiate a SerialRef object that returns a null base type name");
    }
    baseTypeName = paramRef.getBaseTypeName();
  }
  
  public String getBaseTypeName()
    throws SerialException
  {
    return baseTypeName;
  }
  
  public Object getObject(Map<String, Class<?>> paramMap)
    throws SerialException
  {
    paramMap = new Hashtable(paramMap);
    if (object != null) {
      return paramMap.get(object);
    }
    throw new SerialException("The object is not set");
  }
  
  public Object getObject()
    throws SerialException
  {
    if (reference != null) {
      try
      {
        return reference.getObject();
      }
      catch (SQLException localSQLException)
      {
        throw new SerialException("SQLException: " + localSQLException.getMessage());
      }
    }
    if (object != null) {
      return object;
    }
    throw new SerialException("The object is not set");
  }
  
  public void setObject(Object paramObject)
    throws SerialException
  {
    try
    {
      reference.setObject(paramObject);
    }
    catch (SQLException localSQLException)
    {
      throw new SerialException("SQLException: " + localSQLException.getMessage());
    }
    object = paramObject;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof SerialRef))
    {
      SerialRef localSerialRef = (SerialRef)paramObject;
      return (baseTypeName.equals(baseTypeName)) && (object.equals(object));
    }
    return false;
  }
  
  public int hashCode()
  {
    return (31 + object.hashCode()) * 31 + baseTypeName.hashCode();
  }
  
  public Object clone()
  {
    try
    {
      SerialRef localSerialRef = (SerialRef)super.clone();
      reference = null;
      return localSerialRef;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError();
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    object = localGetField.get("object", null);
    baseTypeName = ((String)localGetField.get("baseTypeName", null));
    reference = ((Ref)localGetField.get("reference", null));
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException, ClassNotFoundException
  {
    ObjectOutputStream.PutField localPutField = paramObjectOutputStream.putFields();
    localPutField.put("baseTypeName", baseTypeName);
    localPutField.put("object", object);
    localPutField.put("reference", (reference instanceof Serializable) ? reference : null);
    paramObjectOutputStream.writeFields();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sql\rowset\serial\SerialRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */