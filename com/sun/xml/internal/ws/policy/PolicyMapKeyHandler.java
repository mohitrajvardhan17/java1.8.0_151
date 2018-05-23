package com.sun.xml.internal.ws.policy;

abstract interface PolicyMapKeyHandler
{
  public abstract boolean areEqual(PolicyMapKey paramPolicyMapKey1, PolicyMapKey paramPolicyMapKey2);
  
  public abstract int generateHashCode(PolicyMapKey paramPolicyMapKey);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\PolicyMapKeyHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */