package com.sun.management;

import javax.management.openmbean.CompositeData;
import jdk.Exported;
import sun.management.VMOptionCompositeData;

@Exported
public class VMOption
{
  private String name;
  private String value;
  private boolean writeable;
  private Origin origin;
  
  public VMOption(String paramString1, String paramString2, boolean paramBoolean, Origin paramOrigin)
  {
    name = paramString1;
    value = paramString2;
    writeable = paramBoolean;
    origin = paramOrigin;
  }
  
  private VMOption(CompositeData paramCompositeData)
  {
    VMOptionCompositeData.validateCompositeData(paramCompositeData);
    name = VMOptionCompositeData.getName(paramCompositeData);
    value = VMOptionCompositeData.getValue(paramCompositeData);
    writeable = VMOptionCompositeData.isWriteable(paramCompositeData);
    origin = VMOptionCompositeData.getOrigin(paramCompositeData);
  }
  
  public String getName()
  {
    return name;
  }
  
  public String getValue()
  {
    return value;
  }
  
  public Origin getOrigin()
  {
    return origin;
  }
  
  public boolean isWriteable()
  {
    return writeable;
  }
  
  public String toString()
  {
    return "VM option: " + getName() + " value: " + value + "  origin: " + origin + " " + (writeable ? "(read-write)" : "(read-only)");
  }
  
  public static VMOption from(CompositeData paramCompositeData)
  {
    if (paramCompositeData == null) {
      return null;
    }
    if ((paramCompositeData instanceof VMOptionCompositeData)) {
      return ((VMOptionCompositeData)paramCompositeData).getVMOption();
    }
    return new VMOption(paramCompositeData);
  }
  
  @Exported
  public static enum Origin
  {
    DEFAULT,  VM_CREATION,  ENVIRON_VAR,  CONFIG_FILE,  MANAGEMENT,  ERGONOMIC,  OTHER;
    
    private Origin() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\management\VMOption.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */