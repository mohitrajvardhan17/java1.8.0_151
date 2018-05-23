package javax.management.modelmbean;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.Descriptor;
import javax.management.DescriptorAccess;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanParameterInfo;
import javax.management.RuntimeOperationsException;

public class ModelMBeanConstructorInfo
  extends MBeanConstructorInfo
  implements DescriptorAccess
{
  private static final long oldSerialVersionUID = -4440125391095574518L;
  private static final long newSerialVersionUID = 3862947819818064362L;
  private static final ObjectStreamField[] oldSerialPersistentFields = { new ObjectStreamField("consDescriptor", Descriptor.class), new ObjectStreamField("currClass", String.class) };
  private static final ObjectStreamField[] newSerialPersistentFields = { new ObjectStreamField("consDescriptor", Descriptor.class) };
  private static final long serialVersionUID;
  private static final ObjectStreamField[] serialPersistentFields;
  private static boolean compat = false;
  private Descriptor consDescriptor = validDescriptor(null);
  private static final String currClass = "ModelMBeanConstructorInfo";
  
  public ModelMBeanConstructorInfo(String paramString, Constructor<?> paramConstructor)
  {
    super(paramString, paramConstructor);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanConstructorInfo.class.getName(), "ModelMBeanConstructorInfo(String,Constructor)", "Entry");
    }
    consDescriptor = validDescriptor(null);
  }
  
  public ModelMBeanConstructorInfo(String paramString, Constructor<?> paramConstructor, Descriptor paramDescriptor)
  {
    super(paramString, paramConstructor);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanConstructorInfo.class.getName(), "ModelMBeanConstructorInfo(String,Constructor,Descriptor)", "Entry");
    }
    consDescriptor = validDescriptor(paramDescriptor);
  }
  
  public ModelMBeanConstructorInfo(String paramString1, String paramString2, MBeanParameterInfo[] paramArrayOfMBeanParameterInfo)
  {
    super(paramString1, paramString2, paramArrayOfMBeanParameterInfo);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanConstructorInfo.class.getName(), "ModelMBeanConstructorInfo(String,String,MBeanParameterInfo[])", "Entry");
    }
    consDescriptor = validDescriptor(null);
  }
  
  public ModelMBeanConstructorInfo(String paramString1, String paramString2, MBeanParameterInfo[] paramArrayOfMBeanParameterInfo, Descriptor paramDescriptor)
  {
    super(paramString1, paramString2, paramArrayOfMBeanParameterInfo);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanConstructorInfo.class.getName(), "ModelMBeanConstructorInfo(String,String,MBeanParameterInfo[],Descriptor)", "Entry");
    }
    consDescriptor = validDescriptor(paramDescriptor);
  }
  
  ModelMBeanConstructorInfo(ModelMBeanConstructorInfo paramModelMBeanConstructorInfo)
  {
    super(paramModelMBeanConstructorInfo.getName(), paramModelMBeanConstructorInfo.getDescription(), paramModelMBeanConstructorInfo.getSignature());
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanConstructorInfo.class.getName(), "ModelMBeanConstructorInfo(ModelMBeanConstructorInfo)", "Entry");
    }
    consDescriptor = validDescriptor(consDescriptor);
  }
  
  public Object clone()
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanConstructorInfo.class.getName(), "clone()", "Entry");
    }
    return new ModelMBeanConstructorInfo(this);
  }
  
  public Descriptor getDescriptor()
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanConstructorInfo.class.getName(), "getDescriptor()", "Entry");
    }
    if (consDescriptor == null) {
      consDescriptor = validDescriptor(null);
    }
    return (Descriptor)consDescriptor.clone();
  }
  
  public void setDescriptor(Descriptor paramDescriptor)
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanConstructorInfo.class.getName(), "setDescriptor()", "Entry");
    }
    consDescriptor = validDescriptor(paramDescriptor);
  }
  
  public String toString()
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanConstructorInfo.class.getName(), "toString()", "Entry");
    }
    String str = "ModelMBeanConstructorInfo: " + getName() + " ; Description: " + getDescription() + " ; Descriptor: " + getDescriptor() + " ; Signature: ";
    MBeanParameterInfo[] arrayOfMBeanParameterInfo = getSignature();
    for (int i = 0; i < arrayOfMBeanParameterInfo.length; i++) {
      str = str.concat(arrayOfMBeanParameterInfo[i].getType() + ", ");
    }
    return str;
  }
  
  private Descriptor validDescriptor(Descriptor paramDescriptor)
    throws RuntimeOperationsException
  {
    int i = paramDescriptor == null ? 1 : 0;
    Object localObject;
    if (i != 0)
    {
      localObject = new DescriptorSupport();
      JmxProperties.MODELMBEAN_LOGGER.finer("Null Descriptor, creating new.");
    }
    else
    {
      localObject = (Descriptor)paramDescriptor.clone();
    }
    if ((i != 0) && (((Descriptor)localObject).getFieldValue("name") == null))
    {
      ((Descriptor)localObject).setField("name", getName());
      JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor name to " + getName());
    }
    if ((i != 0) && (((Descriptor)localObject).getFieldValue("descriptorType") == null))
    {
      ((Descriptor)localObject).setField("descriptorType", "operation");
      JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting descriptorType to \"operation\"");
    }
    if (((Descriptor)localObject).getFieldValue("displayName") == null)
    {
      ((Descriptor)localObject).setField("displayName", getName());
      JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor displayName to " + getName());
    }
    if (((Descriptor)localObject).getFieldValue("role") == null)
    {
      ((Descriptor)localObject).setField("role", "constructor");
      JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor role field to \"constructor\"");
    }
    if (!((Descriptor)localObject).isValid()) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The isValid() method of the Descriptor object itself returned false,one or more required fields are invalid. Descriptor:" + localObject.toString());
    }
    if (!getName().equalsIgnoreCase((String)((Descriptor)localObject).getFieldValue("name"))) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The Descriptor \"name\" field does not match the object described.  Expected: " + getName() + " , was: " + ((Descriptor)localObject).getFieldValue("name"));
    }
    if (!"operation".equalsIgnoreCase((String)((Descriptor)localObject).getFieldValue("descriptorType"))) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The Descriptor \"descriptorType\" field does not match the object described.  Expected: \"operation\" , was: " + ((Descriptor)localObject).getFieldValue("descriptorType"));
    }
    if (!((String)((Descriptor)localObject).getFieldValue("role")).equalsIgnoreCase("constructor")) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The Descriptor \"role\" field does not match the object described.  Expected: \"constructor\" , was: " + ((Descriptor)localObject).getFieldValue("role"));
    }
    return (Descriptor)localObject;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    if (compat)
    {
      ObjectOutputStream.PutField localPutField = paramObjectOutputStream.putFields();
      localPutField.put("consDescriptor", consDescriptor);
      localPutField.put("currClass", "ModelMBeanConstructorInfo");
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
      serialVersionUID = -4440125391095574518L;
    }
    else
    {
      serialPersistentFields = newSerialPersistentFields;
      serialVersionUID = 3862947819818064362L;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\modelmbean\ModelMBeanConstructorInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */