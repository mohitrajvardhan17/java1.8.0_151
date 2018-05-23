package java.text;

import sun.text.normalizer.NormalizerBase;

public final class Normalizer
{
  private Normalizer() {}
  
  public static String normalize(CharSequence paramCharSequence, Form paramForm)
  {
    return NormalizerBase.normalize(paramCharSequence.toString(), paramForm);
  }
  
  public static boolean isNormalized(CharSequence paramCharSequence, Form paramForm)
  {
    return NormalizerBase.isNormalized(paramCharSequence.toString(), paramForm);
  }
  
  public static enum Form
  {
    NFD,  NFC,  NFKD,  NFKC;
    
    private Form() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\text\Normalizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */