package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.sourcemodel.AssertionData;
import java.util.Collection;

public abstract class SimpleAssertion
  extends PolicyAssertion
{
  protected SimpleAssertion() {}
  
  protected SimpleAssertion(AssertionData paramAssertionData, Collection<? extends PolicyAssertion> paramCollection)
  {
    super(paramAssertionData, paramCollection);
  }
  
  public final boolean hasNestedPolicy()
  {
    return false;
  }
  
  public final NestedPolicy getNestedPolicy()
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\SimpleAssertion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */