package javax.sql.rowset.serial;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.PrintStream;
import java.io.Serializable;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Ref;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.Arrays;
import java.util.Map;
import java.util.Vector;

public class SerialStruct
  implements Struct, Serializable, Cloneable
{
  private String SQLTypeName;
  private Object[] attribs;
  static final long serialVersionUID = -8322445504027483372L;
  
  public SerialStruct(Struct paramStruct, Map<String, Class<?>> paramMap)
    throws SerialException
  {
    try
    {
      SQLTypeName = paramStruct.getSQLTypeName();
      System.out.println("SQLTypeName: " + SQLTypeName);
      attribs = paramStruct.getAttributes(paramMap);
      mapToSerial(paramMap);
    }
    catch (SQLException localSQLException)
    {
      throw new SerialException(localSQLException.getMessage());
    }
  }
  
  public SerialStruct(SQLData paramSQLData, Map<String, Class<?>> paramMap)
    throws SerialException
  {
    try
    {
      SQLTypeName = paramSQLData.getSQLTypeName();
      Vector localVector = new Vector();
      paramSQLData.writeSQL(new SQLOutputImpl(localVector, paramMap));
      attribs = localVector.toArray();
    }
    catch (SQLException localSQLException)
    {
      throw new SerialException(localSQLException.getMessage());
    }
  }
  
  public String getSQLTypeName()
    throws SerialException
  {
    return SQLTypeName;
  }
  
  public Object[] getAttributes()
    throws SerialException
  {
    Object[] arrayOfObject = attribs;
    return arrayOfObject == null ? null : Arrays.copyOf(arrayOfObject, arrayOfObject.length);
  }
  
  public Object[] getAttributes(Map<String, Class<?>> paramMap)
    throws SerialException
  {
    Object[] arrayOfObject = attribs;
    return arrayOfObject == null ? null : Arrays.copyOf(arrayOfObject, arrayOfObject.length);
  }
  
  private void mapToSerial(Map<String, Class<?>> paramMap)
    throws SerialException
  {
    try
    {
      for (int i = 0; i < attribs.length; i++) {
        if ((attribs[i] instanceof Struct)) {
          attribs[i] = new SerialStruct((Struct)attribs[i], paramMap);
        } else if ((attribs[i] instanceof SQLData)) {
          attribs[i] = new SerialStruct((SQLData)attribs[i], paramMap);
        } else if ((attribs[i] instanceof Blob)) {
          attribs[i] = new SerialBlob((Blob)attribs[i]);
        } else if ((attribs[i] instanceof Clob)) {
          attribs[i] = new SerialClob((Clob)attribs[i]);
        } else if ((attribs[i] instanceof Ref)) {
          attribs[i] = new SerialRef((Ref)attribs[i]);
        } else if ((attribs[i] instanceof Array)) {
          attribs[i] = new SerialArray((Array)attribs[i], paramMap);
        }
      }
    }
    catch (SQLException localSQLException)
    {
      throw new SerialException(localSQLException.getMessage());
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof SerialStruct))
    {
      SerialStruct localSerialStruct = (SerialStruct)paramObject;
      return (SQLTypeName.equals(SQLTypeName)) && (Arrays.equals(attribs, attribs));
    }
    return false;
  }
  
  public int hashCode()
  {
    return (31 + Arrays.hashCode(attribs)) * 31 * 31 + SQLTypeName.hashCode();
  }
  
  public Object clone()
  {
    try
    {
      SerialStruct localSerialStruct = (SerialStruct)super.clone();
      attribs = Arrays.copyOf(attribs, attribs.length);
      return localSerialStruct;
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
    Object[] arrayOfObject = (Object[])localGetField.get("attribs", null);
    attribs = (arrayOfObject == null ? null : (Object[])arrayOfObject.clone());
    SQLTypeName = ((String)localGetField.get("SQLTypeName", null));
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException, ClassNotFoundException
  {
    ObjectOutputStream.PutField localPutField = paramObjectOutputStream.putFields();
    localPutField.put("attribs", attribs);
    localPutField.put("SQLTypeName", SQLTypeName);
    paramObjectOutputStream.writeFields();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sql\rowset\serial\SerialStruct.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */