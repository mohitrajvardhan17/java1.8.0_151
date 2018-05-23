package jdk.management.resource.internal.inst;

import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;

@InstrumentationTarget("jdk.management.resource.internal.WrapInstrumentation")
public class WrapInstrumentationRMHooks
{
  public WrapInstrumentationRMHooks() {}
  
  @InstrumentationMethod
  public boolean wrapComplete()
  {
    wrapComplete();
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\management\resource\internal\inst\WrapInstrumentationRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */