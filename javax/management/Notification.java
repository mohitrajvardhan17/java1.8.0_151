package javax.management;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.security.AccessController;
import java.util.Date;
import java.util.EventObject;

public class Notification
  extends EventObject
{
  private static final long oldSerialVersionUID = 1716977971058914352L;
  private static final long newSerialVersionUID = -7516092053498031989L;
  private static final ObjectStreamField[] oldSerialPersistentFields = { new ObjectStreamField("message", String.class), new ObjectStreamField("sequenceNumber", Long.TYPE), new ObjectStreamField("source", Object.class), new ObjectStreamField("sourceObjectName", ObjectName.class), new ObjectStreamField("timeStamp", Long.TYPE), new ObjectStreamField("type", String.class), new ObjectStreamField("userData", Object.class) };
  private static final ObjectStreamField[] newSerialPersistentFields = { new ObjectStreamField("message", String.class), new ObjectStreamField("sequenceNumber", Long.TYPE), new ObjectStreamField("source", Object.class), new ObjectStreamField("timeStamp", Long.TYPE), new ObjectStreamField("type", String.class), new ObjectStreamField("userData", Object.class) };
  private static final long serialVersionUID;
  private static final ObjectStreamField[] serialPersistentFields;
  private static boolean compat = false;
  private String type;
  private long sequenceNumber;
  private long timeStamp;
  private Object userData = null;
  private String message = "";
  protected Object source = null;
  
  public Notification(String paramString, Object paramObject, long paramLong)
  {
    super(paramObject);
    source = paramObject;
    type = paramString;
    sequenceNumber = paramLong;
    timeStamp = new Date().getTime();
  }
  
  public Notification(String paramString1, Object paramObject, long paramLong, String paramString2)
  {
    super(paramObject);
    source = paramObject;
    type = paramString1;
    sequenceNumber = paramLong;
    timeStamp = new Date().getTime();
    message = paramString2;
  }
  
  public Notification(String paramString, Object paramObject, long paramLong1, long paramLong2)
  {
    super(paramObject);
    source = paramObject;
    type = paramString;
    sequenceNumber = paramLong1;
    timeStamp = paramLong2;
  }
  
  public Notification(String paramString1, Object paramObject, long paramLong1, long paramLong2, String paramString2)
  {
    super(paramObject);
    source = paramObject;
    type = paramString1;
    sequenceNumber = paramLong1;
    timeStamp = paramLong2;
    message = paramString2;
  }
  
  public void setSource(Object paramObject)
  {
    source = paramObject;
    source = paramObject;
  }
  
  public long getSequenceNumber()
  {
    return sequenceNumber;
  }
  
  public void setSequenceNumber(long paramLong)
  {
    sequenceNumber = paramLong;
  }
  
  public String getType()
  {
    return type;
  }
  
  public long getTimeStamp()
  {
    return timeStamp;
  }
  
  public void setTimeStamp(long paramLong)
  {
    timeStamp = paramLong;
  }
  
  public String getMessage()
  {
    return message;
  }
  
  public Object getUserData()
  {
    return userData;
  }
  
  public void setUserData(Object paramObject)
  {
    userData = paramObject;
  }
  
  public String toString()
  {
    return super.toString() + "[type=" + type + "][message=" + message + "]";
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    source = source;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    if (compat)
    {
      ObjectOutputStream.PutField localPutField = paramObjectOutputStream.putFields();
      localPutField.put("type", type);
      localPutField.put("sequenceNumber", sequenceNumber);
      localPutField.put("timeStamp", timeStamp);
      localPutField.put("userData", userData);
      localPutField.put("message", message);
      localPutField.put("source", source);
      paramObjectOutputStream.writeFields();
    }
    else
    {
      paramObjectOutputStream.defaultWriteObject();
    }
  }
  
  static
  {
    try
    {
      GetPropertyAction localGetPropertyAction = new GetPropertyAction("jmx.serial.form");
      String str = (String)AccessController.doPrivileged(localGetPropertyAction);
      compat = (str != null) && (str.equals("1.0"));
    }
    catch (Exception localException) {}
    if (compat)
    {
      serialPersistentFields = oldSerialPersistentFields;
      serialVersionUID = 1716977971058914352L;
    }
    else
    {
      serialPersistentFields = newSerialPersistentFields;
      serialVersionUID = -7516092053498031989L;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\Notification.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */