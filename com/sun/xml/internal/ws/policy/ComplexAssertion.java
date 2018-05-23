package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.sourcemodel.AssertionData;
import java.util.Collection;

public abstract class ComplexAssertion
  extends PolicyAssertion
{
  private final NestedPolicy nestedPolicy;
  
  protected ComplexAssertion()
  {
    nestedPolicy = NestedPolicy.createNestedPolicy(AssertionSet.emptyAssertionSet());
  }
  
  protected ComplexAssertion(AssertionData paramAssertionData, Collection<? extends PolicyAssertion> paramCollection, AssertionSet paramAssertionSet)
  {
    super(paramAssertionData, paramCollection);
    AssertionSet localAssertionSet = paramAssertionSet != null ? paramAssertionSet : AssertionSet.emptyAssertionSet();
    nestedPolicy = NestedPolicy.createNestedPolicy(localAssertionSet);
  }
  
  public final boolean hasNestedPolicy()
  {
    return true;
  }
  
  public final NestedPolicy getNestedPolicy()
  {
    return nestedPolicy;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\ComplexAssertion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */