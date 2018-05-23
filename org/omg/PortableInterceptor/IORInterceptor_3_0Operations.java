package org.omg.PortableInterceptor;

public abstract interface IORInterceptor_3_0Operations
  extends IORInterceptorOperations
{
  public abstract void components_established(IORInfo paramIORInfo);
  
  public abstract void adapter_manager_state_changed(int paramInt, short paramShort);
  
  public abstract void adapter_state_changed(ObjectReferenceTemplate[] paramArrayOfObjectReferenceTemplate, short paramShort);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\PortableInterceptor\IORInterceptor_3_0Operations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */