package javax.xml.crypto.dsig;

import java.security.spec.AlgorithmParameterSpec;

public abstract interface CanonicalizationMethod
  extends Transform
{
  public static final String INCLUSIVE = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
  public static final String INCLUSIVE_WITH_COMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments";
  public static final String EXCLUSIVE = "http://www.w3.org/2001/10/xml-exc-c14n#";
  public static final String EXCLUSIVE_WITH_COMMENTS = "http://www.w3.org/2001/10/xml-exc-c14n#WithComments";
  
  public abstract AlgorithmParameterSpec getParameterSpec();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\crypto\dsig\CanonicalizationMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */