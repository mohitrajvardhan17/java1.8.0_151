package com.sun.xml.internal.ws.policy.spi;

import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.sourcemodel.AssertionData;

public final class AssertionCreationException
  extends PolicyException
{
  private final AssertionData assertionData;
  
  public AssertionCreationException(AssertionData paramAssertionData, String paramString)
  {
    super(paramString);
    assertionData = paramAssertionData;
  }
  
  public AssertionCreationException(AssertionData paramAssertionData, String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
    assertionData = paramAssertionData;
  }
  
  public AssertionCreationException(AssertionData paramAssertionData, Throwable paramThrowable)
  {
    super(paramThrowable);
    assertionData = paramAssertionData;
  }
  
  public AssertionData getAssertionData()
  {
    return assertionData;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\spi\AssertionCreationException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */