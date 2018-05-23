package javax.management;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;

public class BadAttributeValueExpException
  extends Exception
{
  private static final long serialVersionUID = -3105272988410493376L;
  private Object val;
  
  public BadAttributeValueExpException(Object paramObject)
  {
    val = (paramObject == null ? null : paramObject.toString());
  }
  
  public String toString()
  {
    return "BadAttributeValueException: " + val;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    Object localObject = localGetField.get("val", null);
    if (localObject == null) {
      val = null;
    } else if ((localObject instanceof String)) {
      val = localObject;
    } else if ((System.getSecurityManager() == null) || ((localObject instanceof Long)) || ((localObject instanceof Integer)) || ((localObject instanceof Float)) || ((localObject instanceof Double)) || ((localObject instanceof Byte)) || ((localObject instanceof Short)) || ((localObject instanceof Boolean))) {
      val = localObject.toString();
    } else {
      val = (System.identityHashCode(localObject) + "@" + localObject.getClass().getName());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\BadAttributeValueExpException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */