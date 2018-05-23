package javax.sql.rowset.serial;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.Serializable;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.Arrays;
import java.util.Map;

public class SerialArray
  implements Array, Serializable, Cloneable
{
  private Object[] elements;
  private int baseType;
  private String baseTypeName;
  private int len;
  static final long serialVersionUID = -8466174297270688520L;
  
  public SerialArray(Array paramArray, Map<String, Class<?>> paramMap)
    throws SerialException, SQLException
  {
    if ((paramArray == null) || (paramMap == null)) {
      throw new SQLException("Cannot instantiate a SerialArray object with null parameters");
    }
    if ((elements = (Object[])paramArray.getArray()) == null) {
      throw new SQLException("Invalid Array object. Calls to Array.getArray() return null value which cannot be serialized");
    }
    elements = ((Object[])paramArray.getArray(paramMap));
    baseType = paramArray.getBaseType();
    baseTypeName = paramArray.getBaseTypeName();
    len = elements.length;
    int i;
    switch (baseType)
    {
    case 2002: 
      for (i = 0; i < len; i++) {
        elements[i] = new SerialStruct((Struct)elements[i], paramMap);
      }
      break;
    case 2003: 
      for (i = 0; i < len; i++) {
        elements[i] = new SerialArray((Array)elements[i], paramMap);
      }
      break;
    case 2004: 
      for (i = 0; i < len; i++) {
        elements[i] = new SerialBlob((Blob)elements[i]);
      }
      break;
    case 2005: 
      for (i = 0; i < len; i++) {
        elements[i] = new SerialClob((Clob)elements[i]);
      }
      break;
    case 70: 
      for (i = 0; i < len; i++) {
        elements[i] = new SerialDatalink((URL)elements[i]);
      }
      break;
    case 2000: 
      for (i = 0; i < len; i++) {
        elements[i] = new SerialJavaObject(elements[i]);
      }
    }
  }
  
  public void free()
    throws SQLException
  {
    if (elements != null)
    {
      elements = null;
      baseTypeName = null;
    }
  }
  
  public SerialArray(Array paramArray)
    throws SerialException, SQLException
  {
    if (paramArray == null) {
      throw new SQLException("Cannot instantiate a SerialArray object with a null Array object");
    }
    if ((elements = (Object[])paramArray.getArray()) == null) {
      throw new SQLException("Invalid Array object. Calls to Array.getArray() return null value which cannot be serialized");
    }
    baseType = paramArray.getBaseType();
    baseTypeName = paramArray.getBaseTypeName();
    len = elements.length;
    int i;
    switch (baseType)
    {
    case 2004: 
      for (i = 0; i < len; i++) {
        elements[i] = new SerialBlob((Blob)elements[i]);
      }
      break;
    case 2005: 
      for (i = 0; i < len; i++) {
        elements[i] = new SerialClob((Clob)elements[i]);
      }
      break;
    case 70: 
      for (i = 0; i < len; i++) {
        elements[i] = new SerialDatalink((URL)elements[i]);
      }
      break;
    case 2000: 
      for (i = 0; i < len; i++) {
        elements[i] = new SerialJavaObject(elements[i]);
      }
    }
  }
  
  public Object getArray()
    throws SerialException
  {
    isValid();
    Object[] arrayOfObject = new Object[len];
    System.arraycopy(elements, 0, arrayOfObject, 0, len);
    return arrayOfObject;
  }
  
  public Object getArray(Map<String, Class<?>> paramMap)
    throws SerialException
  {
    isValid();
    Object[] arrayOfObject = new Object[len];
    System.arraycopy(elements, 0, arrayOfObject, 0, len);
    return arrayOfObject;
  }
  
  public Object getArray(long paramLong, int paramInt)
    throws SerialException
  {
    isValid();
    Object[] arrayOfObject = new Object[paramInt];
    System.arraycopy(elements, (int)paramLong, arrayOfObject, 0, paramInt);
    return arrayOfObject;
  }
  
  public Object getArray(long paramLong, int paramInt, Map<String, Class<?>> paramMap)
    throws SerialException
  {
    isValid();
    Object[] arrayOfObject = new Object[paramInt];
    System.arraycopy(elements, (int)paramLong, arrayOfObject, 0, paramInt);
    return arrayOfObject;
  }
  
  public int getBaseType()
    throws SerialException
  {
    isValid();
    return baseType;
  }
  
  public String getBaseTypeName()
    throws SerialException
  {
    isValid();
    return baseTypeName;
  }
  
  public ResultSet getResultSet(long paramLong, int paramInt)
    throws SerialException
  {
    SerialException localSerialException = new SerialException();
    localSerialException.initCause(new UnsupportedOperationException());
    throw localSerialException;
  }
  
  public ResultSet getResultSet(Map<String, Class<?>> paramMap)
    throws SerialException
  {
    SerialException localSerialException = new SerialException();
    localSerialException.initCause(new UnsupportedOperationException());
    throw localSerialException;
  }
  
  public ResultSet getResultSet()
    throws SerialException
  {
    SerialException localSerialException = new SerialException();
    localSerialException.initCause(new UnsupportedOperationException());
    throw localSerialException;
  }
  
  public ResultSet getResultSet(long paramLong, int paramInt, Map<String, Class<?>> paramMap)
    throws SerialException
  {
    SerialException localSerialException = new SerialException();
    localSerialException.initCause(new UnsupportedOperationException());
    throw localSerialException;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof SerialArray))
    {
      SerialArray localSerialArray = (SerialArray)paramObject;
      return (baseType == baseType) && (baseTypeName.equals(baseTypeName)) && (Arrays.equals(elements, elements));
    }
    return false;
  }
  
  public int hashCode()
  {
    return (((31 + Arrays.hashCode(elements)) * 31 + len) * 31 + baseType) * 31 + baseTypeName.hashCode();
  }
  
  public Object clone()
  {
    try
    {
      SerialArray localSerialArray = (SerialArray)super.clone();
      elements = (elements != null ? Arrays.copyOf(elements, len) : null);
      return localSerialArray;
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
    Object[] arrayOfObject = (Object[])localGetField.get("elements", null);
    if (arrayOfObject == null) {
      throw new InvalidObjectException("elements is null and should not be!");
    }
    elements = ((Object[])arrayOfObject.clone());
    len = localGetField.get("len", 0);
    if (elements.length != len) {
      throw new InvalidObjectException("elements is not the expected size");
    }
    baseType = localGetField.get("baseType", 0);
    baseTypeName = ((String)localGetField.get("baseTypeName", null));
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException, ClassNotFoundException
  {
    ObjectOutputStream.PutField localPutField = paramObjectOutputStream.putFields();
    localPutField.put("elements", elements);
    localPutField.put("len", len);
    localPutField.put("baseType", baseType);
    localPutField.put("baseTypeName", baseTypeName);
    paramObjectOutputStream.writeFields();
  }
  
  private void isValid()
    throws SerialException
  {
    if (elements == null) {
      throw new SerialException("Error: You cannot call a method on a SerialArray instance once free() has been called.");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sql\rowset\serial\SerialArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */