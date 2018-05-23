package sun.security.util;

import java.security.spec.AlgorithmParameterSpec;

public class ECKeySizeParameterSpec
  implements AlgorithmParameterSpec
{
  private int keySize;
  
  public ECKeySizeParameterSpec(int paramInt)
  {
    keySize = paramInt;
  }
  
  public int getKeySize()
  {
    return keySize;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\util\ECKeySizeParameterSpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */