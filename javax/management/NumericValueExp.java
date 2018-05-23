package javax.management;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.security.AccessController;

class NumericValueExp
  extends QueryEval
  implements ValueExp
{
  private static final long oldSerialVersionUID = -6227876276058904000L;
  private static final long newSerialVersionUID = -4679739485102359104L;
  private static final ObjectStreamField[] oldSerialPersistentFields = { new ObjectStreamField("longVal", Long.TYPE), new ObjectStreamField("doubleVal", Double.TYPE), new ObjectStreamField("valIsLong", Boolean.TYPE) };
  private static final ObjectStreamField[] newSerialPersistentFields = { new ObjectStreamField("val", Number.class) };
  private static final long serialVersionUID;
  private static final ObjectStreamField[] serialPersistentFields;
  private Number val = Double.valueOf(0.0D);
  private static boolean compat = false;
  
  public NumericValueExp() {}
  
  NumericValueExp(Number paramNumber)
  {
    val = paramNumber;
  }
  
  public double doubleValue()
  {
    if (((val instanceof Long)) || ((val instanceof Integer))) {
      return val.longValue();
    }
    return val.doubleValue();
  }
  
  public long longValue()
  {
    if (((val instanceof Long)) || ((val instanceof Integer))) {
      return val.longValue();
    }
    return val.doubleValue();
  }
  
  public boolean isLong()
  {
    return ((val instanceof Long)) || ((val instanceof Integer));
  }
  
  public String toString()
  {
    if (val == null) {
      return "null";
    }
    if (((val instanceof Long)) || ((val instanceof Integer))) {
      return Long.toString(val.longValue());
    }
    double d = val.doubleValue();
    if (Double.isInfinite(d)) {
      return d > 0.0D ? "(1.0 / 0.0)" : "(-1.0 / 0.0)";
    }
    if (Double.isNaN(d)) {
      return "(0.0 / 0.0)";
    }
    return Double.toString(d);
  }
  
  public ValueExp apply(ObjectName paramObjectName)
    throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException
  {
    return this;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    if (compat)
    {
      ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
      double d = localGetField.get("doubleVal", 0.0D);
      if (localGetField.defaulted("doubleVal")) {
        throw new NullPointerException("doubleVal");
      }
      long l = localGetField.get("longVal", 0L);
      if (localGetField.defaulted("longVal")) {
        throw new NullPointerException("longVal");
      }
      boolean bool = localGetField.get("valIsLong", false);
      if (localGetField.defaulted("valIsLong")) {
        throw new NullPointerException("valIsLong");
      }
      if (bool) {
        val = Long.valueOf(l);
      } else {
        val = Double.valueOf(d);
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
      localPutField.put("doubleVal", doubleValue());
      localPutField.put("longVal", longValue());
      localPutField.put("valIsLong", isLong());
      paramObjectOutputStream.writeFields();
    }
    else
    {
      paramObjectOutputStream.defaultWriteObject();
    }
  }
  
  @Deprecated
  public void setMBeanServer(MBeanServer paramMBeanServer)
  {
    super.setMBeanServer(paramMBeanServer);
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
      serialVersionUID = -6227876276058904000L;
    }
    else
    {
      serialPersistentFields = newSerialPersistentFields;
      serialVersionUID = -4679739485102359104L;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\NumericValueExp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */