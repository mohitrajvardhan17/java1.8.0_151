package org.omg.PortableInterceptor;

import org.omg.CORBA.Policy;
import org.omg.IOP.TaggedComponent;

public abstract interface IORInfoOperations
{
  public abstract Policy get_effective_policy(int paramInt);
  
  public abstract void add_ior_component(TaggedComponent paramTaggedComponent);
  
  public abstract void add_ior_component_to_profile(TaggedComponent paramTaggedComponent, int paramInt);
  
  public abstract int manager_id();
  
  public abstract short state();
  
  public abstract ObjectReferenceTemplate adapter_template();
  
  public abstract ObjectReferenceFactory current_factory();
  
  public abstract void current_factory(ObjectReferenceFactory paramObjectReferenceFactory);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\PortableInterceptor\IORInfoOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */