package javax.management.modelmbean;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.security.AccessController;

public class InvalidTargetObjectTypeException
  extends Exception
{
  private static final long oldSerialVersionUID = 3711724570458346634L;
  private static final long newSerialVersionUID = 1190536278266811217L;
  private static final ObjectStreamField[] oldSerialPersistentFields = { new ObjectStreamField("msgStr", String.class), new ObjectStreamField("relatedExcept", Exception.class) };
  private static final ObjectStreamField[] newSerialPersistentFields = { new ObjectStreamField("exception", Exception.class) };
  private static final long serialVersionUID;
  private static final ObjectStreamField[] serialPersistentFields;
  private static boolean compat = false;
  Exception exception;
  
  public InvalidTargetObjectTypeException()
  {
    super("InvalidTargetObjectTypeException: ");
    exception = null;
  }
  
  public InvalidTargetObjectTypeException(String paramString)
  {
    super("InvalidTargetObjectTypeException: " + paramString);
    exception = null;
  }
  
  public InvalidTargetObjectTypeException(Exception paramException, String paramString)
  {
    super("InvalidTargetObjectTypeException: " + paramString + (paramException != null ? "\n\t triggered by:" + paramException.toString() : ""));
    exception = paramException;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    if (compat)
    {
      ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
      exception = ((Exception)localGetField.get("relatedExcept", null));
      if (localGetField.defaulted("relatedExcept")) {
        throw new NullPointerException("relatedExcept");
      }
    }
    else
    {
      paramObjectInputStream.defaultReadObject();
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    if (compat)
    {
      ObjectOutputStream.PutField localPutField = paramObjectOutputStream.putFields();
      localPutField.put("relatedExcept", exception);
      localPutField.put("msgStr", exception != null ? exception.getMessage() : "");
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
      serialVersionUID = 3711724570458346634L;
    }
    else
    {
      serialPersistentFields = newSerialPersistentFields;
      serialVersionUID = 1190536278266811217L;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\modelmbean\InvalidTargetObjectTypeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */