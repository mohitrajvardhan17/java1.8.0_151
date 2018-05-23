package org.omg.PortableInterceptor;

public abstract interface ObjectReferenceTemplate
  extends ObjectReferenceFactory
{
  public abstract String server_id();
  
  public abstract String orb_id();
  
  public abstract String[] adapter_name();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\PortableInterceptor\ObjectReferenceTemplate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */