package javax.management;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.Objects;

public class MBeanFeatureInfo
  implements Serializable, DescriptorRead
{
  static final long serialVersionUID = 3952882688968447265L;
  protected String name;
  protected String description;
  private transient Descriptor descriptor;
  
  public MBeanFeatureInfo(String paramString1, String paramString2)
  {
    this(paramString1, paramString2, null);
  }
  
  public MBeanFeatureInfo(String paramString1, String paramString2, Descriptor paramDescriptor)
  {
    name = paramString1;
    description = paramString2;
    descriptor = paramDescriptor;
  }
  
  public String getName()
  {
    return name;
  }
  
  public String getDescription()
  {
    return description;
  }
  
  public Descriptor getDescriptor()
  {
    return (Descriptor)ImmutableDescriptor.nonNullDescriptor(descriptor).clone();
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof MBeanFeatureInfo)) {
      return false;
    }
    MBeanFeatureInfo localMBeanFeatureInfo = (MBeanFeatureInfo)paramObject;
    return (Objects.equals(localMBeanFeatureInfo.getName(), getName())) && (Objects.equals(localMBeanFeatureInfo.getDescription(), getDescription())) && (Objects.equals(localMBeanFeatureInfo.getDescriptor(), getDescriptor()));
  }
  
  public int hashCode()
  {
    return getName().hashCode() ^ getDescription().hashCode() ^ getDescriptor().hashCode();
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    if ((descriptor != null) && (descriptor.getClass() == ImmutableDescriptor.class))
    {
      paramObjectOutputStream.write(1);
      String[] arrayOfString = descriptor.getFieldNames();
      paramObjectOutputStream.writeObject(arrayOfString);
      paramObjectOutputStream.writeObject(descriptor.getFieldValues(arrayOfString));
    }
    else
    {
      paramObjectOutputStream.write(0);
      paramObjectOutputStream.writeObject(descriptor);
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    switch (paramObjectInputStream.read())
    {
    case 1: 
      String[] arrayOfString = (String[])paramObjectInputStream.readObject();
      Object[] arrayOfObject = (Object[])paramObjectInputStream.readObject();
      descriptor = (arrayOfString.length == 0 ? ImmutableDescriptor.EMPTY_DESCRIPTOR : new ImmutableDescriptor(arrayOfString, arrayOfObject));
      break;
    case 0: 
      descriptor = ((Descriptor)paramObjectInputStream.readObject());
      if (descriptor == null) {
        descriptor = ImmutableDescriptor.EMPTY_DESCRIPTOR;
      }
      break;
    case -1: 
      descriptor = ImmutableDescriptor.EMPTY_DESCRIPTOR;
      break;
    default: 
      throw new StreamCorruptedException("Got unexpected byte.");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\MBeanFeatureInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */