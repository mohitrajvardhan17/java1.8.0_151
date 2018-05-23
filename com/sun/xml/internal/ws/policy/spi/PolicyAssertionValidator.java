package com.sun.xml.internal.ws.policy.spi;

import com.sun.xml.internal.ws.policy.PolicyAssertion;

public abstract interface PolicyAssertionValidator
{
  public abstract Fitness validateClientSide(PolicyAssertion paramPolicyAssertion);
  
  public abstract Fitness validateServerSide(PolicyAssertion paramPolicyAssertion);
  
  public abstract String[] declareSupportedDomains();
  
  public static enum Fitness
  {
    UNKNOWN,  INVALID,  UNSUPPORTED,  SUPPORTED;
    
    private Fitness() {}
    
    public Fitness combine(Fitness paramFitness)
    {
      if (compareTo(paramFitness) < 0) {
        return paramFitness;
      }
      return this;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\spi\PolicyAssertionValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */