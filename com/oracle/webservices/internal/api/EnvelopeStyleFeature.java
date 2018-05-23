package com.oracle.webservices.internal.api;

import javax.xml.ws.WebServiceFeature;

public class EnvelopeStyleFeature
  extends WebServiceFeature
{
  private EnvelopeStyle.Style[] styles;
  
  public EnvelopeStyleFeature(EnvelopeStyle.Style... paramVarArgs)
  {
    styles = paramVarArgs;
  }
  
  public EnvelopeStyle.Style[] getStyles()
  {
    return styles;
  }
  
  public String getID()
  {
    return EnvelopeStyleFeature.class.getName();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\oracle\webservices\internal\api\EnvelopeStyleFeature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */