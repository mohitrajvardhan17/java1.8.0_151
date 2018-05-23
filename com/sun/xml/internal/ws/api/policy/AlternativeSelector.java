package com.sun.xml.internal.ws.api.policy;

import com.sun.xml.internal.ws.policy.EffectiveAlternativeSelector;
import com.sun.xml.internal.ws.policy.EffectivePolicyModifier;
import com.sun.xml.internal.ws.policy.PolicyException;

public class AlternativeSelector
  extends EffectiveAlternativeSelector
{
  public AlternativeSelector() {}
  
  public static void doSelection(EffectivePolicyModifier paramEffectivePolicyModifier)
    throws PolicyException
  {
    ValidationProcessor localValidationProcessor = ValidationProcessor.getInstance();
    selectAlternatives(paramEffectivePolicyModifier, localValidationProcessor);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\policy\AlternativeSelector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */