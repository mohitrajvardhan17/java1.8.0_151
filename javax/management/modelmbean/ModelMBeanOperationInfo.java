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
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.RuntimeOperationsException;

public class ModelMBeanOperationInfo
  extends MBeanOperationInfo
  implements DescriptorAccess
{
  private static final long oldSerialVersionUID = 9087646304346171239L;
  private static final long newSerialVersionUID = 6532732096650090465L;
  private static final ObjectStreamField[] oldSerialPersistentFields = { new ObjectStreamField("operationDescriptor", Descriptor.class), new ObjectStreamField("currClass", String.class) };
  private static final ObjectStreamField[] newSerialPersistentFields = { new ObjectStreamField("operationDescriptor", Descriptor.class) };
  private static final long serialVersionUID;
  private static final ObjectStreamField[] serialPersistentFields;
  private static boolean compat = false;
  private Descriptor operationDescriptor = validDescriptor(null);
  private static final String currClass = "ModelMBeanOperationInfo";
  
  public ModelMBeanOperationInfo(String paramString, Method paramMethod)
  {
    super(paramString, paramMethod);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanOperationInfo.class.getName(), "ModelMBeanOperationInfo(String,Method)", "Entry");
    }
    operationDescriptor = validDescriptor(null);
  }
  
  public ModelMBeanOperationInfo(String paramString, Method paramMethod, Descriptor paramDescriptor)
  {
    super(paramString, paramMethod);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanOperationInfo.class.getName(), "ModelMBeanOperationInfo(String,Method,Descriptor)", "Entry");
    }
    operationDescriptor = validDescriptor(paramDescriptor);
  }
  
  public ModelMBeanOperationInfo(String paramString1, String paramString2, MBeanParameterInfo[] paramArrayOfMBeanParameterInfo, String paramString3, int paramInt)
  {
    super(paramString1, paramString2, paramArrayOfMBeanParameterInfo, paramString3, paramInt);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanOperationInfo.class.getName(), "ModelMBeanOperationInfo(String,String,MBeanParameterInfo[],String,int)", "Entry");
    }
    operationDescriptor = validDescriptor(null);
  }
  
  public ModelMBeanOperationInfo(String paramString1, String paramString2, MBeanParameterInfo[] paramArrayOfMBeanParameterInfo, String paramString3, int paramInt, Descriptor paramDescriptor)
  {
    super(paramString1, paramString2, paramArrayOfMBeanParameterInfo, paramString3, paramInt);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanOperationInfo.class.getName(), "ModelMBeanOperationInfo(String,String,MBeanParameterInfo[],String,int,Descriptor)", "Entry");
    }
    operationDescriptor = validDescriptor(paramDescriptor);
  }
  
  public ModelMBeanOperationInfo(ModelMBeanOperationInfo paramModelMBeanOperationInfo)
  {
    super(paramModelMBeanOperationInfo.getName(), paramModelMBeanOperationInfo.getDescription(), paramModelMBeanOperationInfo.getSignature(), paramModelMBeanOperationInfo.getReturnType(), paramModelMBeanOperationInfo.getImpact());
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanOperationInfo.class.getName(), "ModelMBeanOperationInfo(ModelMBeanOperationInfo)", "Entry");
    }
    Descriptor localDescriptor = paramModelMBeanOperationInfo.getDescriptor();
    operationDescriptor = validDescriptor(localDescriptor);
  }
  
  public Object clone()
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanOperationInfo.class.getName(), "clone()", "Entry");
    }
    return new ModelMBeanOperationInfo(this);
  }
  
  public Descriptor getDescriptor()
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanOperationInfo.class.getName(), "getDescriptor()", "Entry");
    }
    if (operationDescriptor == null) {
      operationDescriptor = validDescriptor(null);
    }
    return (Descriptor)operationDescriptor.clone();
  }
  
  public void setDescriptor(Descriptor paramDescriptor)
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanOperationInfo.class.getName(), "setDescriptor(Descriptor)", "Entry");
    }
    operationDescriptor = validDescriptor(paramDescriptor);
  }
  
  public String toString()
  {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanOperationInfo.class.getName(), "toString()", "Entry");
    }
    String str = "ModelMBeanOperationInfo: " + getName() + " ; Description: " + getDescription() + " ; Descriptor: " + getDescriptor() + " ; ReturnType: " + getReturnType() + " ; Signature: ";
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
    Object localObject1;
    if (i != 0)
    {
      localObject1 = new DescriptorSupport();
      JmxProperties.MODELMBEAN_LOGGER.finer("Null Descriptor, creating new.");
    }
    else
    {
      localObject1 = (Descriptor)paramDescriptor.clone();
    }
    if ((i != 0) && (((Descriptor)localObject1).getFieldValue("name") == null))
    {
      ((Descriptor)localObject1).setField("name", getName());
      JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor name to " + getName());
    }
    if ((i != 0) && (((Descriptor)localObject1).getFieldValue("descriptorType") == null))
    {
      ((Descriptor)localObject1).setField("descriptorType", "operation");
      JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting descriptorType to \"operation\"");
    }
    if (((Descriptor)localObject1).getFieldValue("displayName") == null)
    {
      ((Descriptor)localObject1).setField("displayName", getName());
      JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor displayName to " + getName());
    }
    if (((Descriptor)localObject1).getFieldValue("role") == null)
    {
      ((Descriptor)localObject1).setField("role", "operation");
      JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor role field to \"operation\"");
    }
    if (!((Descriptor)localObject1).isValid()) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The isValid() method of the Descriptor object itself returned false,one or more required fields are invalid. Descriptor:" + localObject1.toString());
    }
    if (!getName().equalsIgnoreCase((String)((Descriptor)localObject1).getFieldValue("name"))) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The Descriptor \"name\" field does not match the object described.  Expected: " + getName() + " , was: " + ((Descriptor)localObject1).getFieldValue("name"));
    }
    if (!"operation".equalsIgnoreCase((String)((Descriptor)localObject1).getFieldValue("descriptorType"))) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The Descriptor \"descriptorType\" field does not match the object described.  Expected: \"operation\" , was: " + ((Descriptor)localObject1).getFieldValue("descriptorType"));
    }
    String str = (String)((Descriptor)localObject1).getFieldValue("role");
    if ((!str.equalsIgnoreCase("operation")) && (!str.equalsIgnoreCase("setter")) && (!str.equalsIgnoreCase("getter"))) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The Descriptor \"role\" field does not match the object described.  Expected: \"operation\", \"setter\", or \"getter\" , was: " + ((Descriptor)localObject1).getFieldValue("role"));
    }
    Object localObject2 = ((Descriptor)localObject1).getFieldValue("targetType");
    if ((localObject2 != null) && (!(localObject2 instanceof String))) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The Descriptor field \"targetValue\" is invalid class.  Expected: java.lang.String,  was: " + localObject2.getClass().getName());
    }
    return (Descriptor)localObject1;
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
      localPutField.put("operationDescriptor", operationDescriptor);
      localPutField.put("currClass", "ModelMBeanOperationInfo");
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
      serialVersionUID = 9087646304346171239L;
    }
    else
    {
      serialPersistentFields = newSerialPersistentFields;
      serialVersionUID = 6532732096650090465L;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\modelmbean\ModelMBeanOperationInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */