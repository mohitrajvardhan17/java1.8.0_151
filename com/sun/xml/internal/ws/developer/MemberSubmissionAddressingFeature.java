package com.sun.xml.internal.ws.developer;

import com.sun.org.glassfish.gmbal.ManagedAttribute;
import com.sun.org.glassfish.gmbal.ManagedData;
import com.sun.xml.internal.ws.api.FeatureConstructor;
import javax.xml.ws.WebServiceFeature;

@ManagedData
public class MemberSubmissionAddressingFeature
  extends WebServiceFeature
{
  public static final String ID = "http://java.sun.com/xml/ns/jaxws/2004/08/addressing";
  public static final String IS_REQUIRED = "ADDRESSING_IS_REQUIRED";
  private boolean required;
  private MemberSubmissionAddressing.Validation validation = MemberSubmissionAddressing.Validation.LAX;
  
  public MemberSubmissionAddressingFeature()
  {
    enabled = true;
  }
  
  public MemberSubmissionAddressingFeature(boolean paramBoolean)
  {
    enabled = paramBoolean;
  }
  
  public MemberSubmissionAddressingFeature(boolean paramBoolean1, boolean paramBoolean2)
  {
    enabled = paramBoolean1;
    required = paramBoolean2;
  }
  
  @FeatureConstructor({"enabled", "required", "validation"})
  public MemberSubmissionAddressingFeature(boolean paramBoolean1, boolean paramBoolean2, MemberSubmissionAddressing.Validation paramValidation)
  {
    enabled = paramBoolean1;
    required = paramBoolean2;
    validation = paramValidation;
  }
  
  @ManagedAttribute
  public String getID()
  {
    return "http://java.sun.com/xml/ns/jaxws/2004/08/addressing";
  }
  
  @ManagedAttribute
  public boolean isRequired()
  {
    return required;
  }
  
  public void setRequired(boolean paramBoolean)
  {
    required = paramBoolean;
  }
  
  public void setValidation(MemberSubmissionAddressing.Validation paramValidation)
  {
    validation = paramValidation;
  }
  
  @ManagedAttribute
  public MemberSubmissionAddressing.Validation getValidation()
  {
    return validation;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\developer\MemberSubmissionAddressingFeature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */