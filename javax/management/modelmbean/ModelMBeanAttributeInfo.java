package javax.management.modelmbean;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.Descriptor;
import javax.management.DescriptorAccess;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.RuntimeOperationsException;

public class ModelMBeanAttributeInfo
  extends MBeanAttributeInfo
  implements DescriptorAccess
{
  private static final long oldSerialVersionUID = 7098036920755973145L;
  private static final long newSerialVersionUID = 6181543027787327345L;
  private static final ObjectStreamField[] oldSerialPersistentFields = { new ObjectStreamField("attrDescriptor", Descriptor.class), new ObjectStreamField("currClass", String.class) };
  private static final ObjectStreamField[] newSerialPersistentFields = { new ObjectStreamField("attrDescriptor", Descriptor.class) };
  private static final long serialVersionUID;
  private static final ObjectStreamField[] serialPersistentFields;
  private static boolean compat = false;
  private Descriptor attrDescriptor = validDescriptor(null);
  private static final String currClass = "ModelMBeanAttributeInfo";
  
  public ModelMBeanAttributeInfo(String paramString1, String paramString2, Method paramMethod1, Method paramMethod2)
    throws IntrospectionException
  {
    super(paramString1, paramString2, paramMethod1, paramMethod2);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanAttributeInfo.class.getName(), "ModelMBeanAttributeInfo(String,String,Method,Method)", "Entry", paramString1);
    }
    attrDescriptor = validDescriptor(null);
  }
  
  public ModelMBeanAttributeInfo(String paramString1, String paramString2, Method paramMethod1, Method paramMethod2, Descriptor paramDescriptor)
    throws IntrospectionException
  {
    super(paramString1, paramString2, paramMethod1, paramMethod2);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanAttributeInfo.class.getName(), "ModelMBeanAttributeInfo(String,String,Method,Method,Descriptor)", "Entry", paramString1);
    }
    attrDescriptor = validDescriptor(paramDescriptor);
  }
  
  public ModelMBeanAttributeInfo(String paramString1, String paramString2, String paramString3, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    super(paramString1, paramString2, paramString3, paramBoolean1, paramBoolean2, paramBoolean3);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanAttributeInfo.class.getName(), "ModelMBeanAttributeInfo(String,String,String,boolean,boolean,boolean)", "Entry", paramString1);
    }
    attrDescriptor = validDescriptor(null);
  }
  
  public ModelMBeanAttributeInfo(String paramString1, String paramString2, String paramString3, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, Descriptor paramDescriptor)
  {
    super(paramString1, paramString2, paramString3, paramBoolean1, paramBoolean2, paramBoolean3);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanAttributeInfo.class.getName(), "ModelMBeanAttributeInfo(String,String,String,boolean,boolean,boolean,Descriptor)", "Entry", paramString1);
    }
    attrDescriptor = validDescriptor(paramDescriptor);
  }
  
  public ModelMBeanAttributeInfo(ModelMBeanAttributeInfo paramModelMBeanAttributeInfo)
  {
    super(paramModelMBeanAttributeInfo.getName(), paramModelMBeanAttributeInfo.getType(), paramModelMBeanAttributeInfo.getDescription(), paramModelMBeanAttributeInfo.isReadable(), paramModelMBeanAttributeInfo.isWritable(), paramModelMBeanAttributeInfo.isIs());
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanAttributeInfo.class.getName(), "ModelMBeanAttributeInfo(ModelMBeanAttributeInfo)", "Entry");
    }
    Descriptor localDescriptor = paramModelMBeanAttributeInfo.getDescriptor();
    attrDescriptor = validDescriptor(localDescriptor);
  }
  
  public Descriptor getDescriptor()
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanAttributeInfo.class.getName(), "getDescriptor()", "Entry");
    }
    if (attrDescriptor == null) {
      attrDescriptor = validDescriptor(null);
    }
    return (Descriptor)attrDescriptor.clone();
  }
  
  public void setDescriptor(Descriptor paramDescriptor)
  {
    attrDescriptor = validDescriptor(paramDescriptor);
  }
  
  public Object clone()
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanAttributeInfo.class.getName(), "clone()", "Entry");
    }
    return new ModelMBeanAttributeInfo(this);
  }
  
  public String toString()
  {
    return "ModelMBeanAttributeInfo: " + getName() + " ; Description: " + getDescription() + " ; Types: " + getType() + " ; isReadable: " + isReadable() + " ; isWritable: " + isWritable() + " ; Descriptor: " + getDescriptor();
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
      ((Descriptor)localObject).setField("descriptorType", "attribute");
      JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting descriptorType to \"attribute\"");
    }
    if (((Descriptor)localObject).getFieldValue("displayName") == null)
    {
      ((Descriptor)localObject).setField("displayName", getName());
      JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor displayName to " + getName());
    }
    if (!((Descriptor)localObject).isValid()) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The isValid() method of the Descriptor object itself returned false,one or more required fields are invalid. Descriptor:" + localObject.toString());
    }
    if (!getName().equalsIgnoreCase((String)((Descriptor)localObject).getFieldValue("name"))) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The Descriptor \"name\" field does not match the object described.  Expected: " + getName() + " , was: " + ((Descriptor)localObject).getFieldValue("name"));
    }
    if (!"attribute".equalsIgnoreCase((String)((Descriptor)localObject).getFieldValue("descriptorType"))) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The Descriptor \"descriptorType\" field does not match the object described.  Expected: \"attribute\" , was: " + ((Descriptor)localObject).getFieldValue("descriptorType"));
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
      localPutField.put("attrDescriptor", attrDescriptor);
      localPutField.put("currClass", "ModelMBeanAttributeInfo");
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
      serialVersionUID = 7098036920755973145L;
    }
    else
    {
      serialPersistentFields = newSerialPersistentFields;
      serialVersionUID = 6181543027787327345L;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\modelmbean\ModelMBeanAttributeInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */