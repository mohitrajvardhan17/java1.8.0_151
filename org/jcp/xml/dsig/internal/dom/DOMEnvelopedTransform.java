package org.jcp.xml.dsig.internal.dom;

import java.security.InvalidAlgorithmParameterException;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;

public final class DOMEnvelopedTransform
  extends ApacheTransform
{
  public DOMEnvelopedTransform() {}
  
  public void init(TransformParameterSpec paramTransformParameterSpec)
    throws InvalidAlgorithmParameterException
  {
    if (paramTransformParameterSpec != null) {
      throw new InvalidAlgorithmParameterException("params must be null");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMEnvelopedTransform.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */