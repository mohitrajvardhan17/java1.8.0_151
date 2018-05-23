package com.sun.xml.internal.ws.policy;

public final class EffectivePolicyModifier
  extends PolicyMapMutator
{
  public static EffectivePolicyModifier createEffectivePolicyModifier()
  {
    return new EffectivePolicyModifier();
  }
  
  private EffectivePolicyModifier() {}
  
  public void setNewEffectivePolicyForServiceScope(PolicyMapKey paramPolicyMapKey, Policy paramPolicy)
  {
    getMap().setNewEffectivePolicyForScope(PolicyMap.ScopeType.SERVICE, paramPolicyMapKey, paramPolicy);
  }
  
  public void setNewEffectivePolicyForEndpointScope(PolicyMapKey paramPolicyMapKey, Policy paramPolicy)
  {
    getMap().setNewEffectivePolicyForScope(PolicyMap.ScopeType.ENDPOINT, paramPolicyMapKey, paramPolicy);
  }
  
  public void setNewEffectivePolicyForOperationScope(PolicyMapKey paramPolicyMapKey, Policy paramPolicy)
  {
    getMap().setNewEffectivePolicyForScope(PolicyMap.ScopeType.OPERATION, paramPolicyMapKey, paramPolicy);
  }
  
  public void setNewEffectivePolicyForInputMessageScope(PolicyMapKey paramPolicyMapKey, Policy paramPolicy)
  {
    getMap().setNewEffectivePolicyForScope(PolicyMap.ScopeType.INPUT_MESSAGE, paramPolicyMapKey, paramPolicy);
  }
  
  public void setNewEffectivePolicyForOutputMessageScope(PolicyMapKey paramPolicyMapKey, Policy paramPolicy)
  {
    getMap().setNewEffectivePolicyForScope(PolicyMap.ScopeType.OUTPUT_MESSAGE, paramPolicyMapKey, paramPolicy);
  }
  
  public void setNewEffectivePolicyForFaultMessageScope(PolicyMapKey paramPolicyMapKey, Policy paramPolicy)
  {
    getMap().setNewEffectivePolicyForScope(PolicyMap.ScopeType.FAULT_MESSAGE, paramPolicyMapKey, paramPolicy);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\EffectivePolicyModifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */