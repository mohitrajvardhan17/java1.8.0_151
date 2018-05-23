package sun.text;

import java.text.Normalizer.Form;
import sun.text.normalizer.NormalizerBase;
import sun.text.normalizer.NormalizerImpl;

public final class Normalizer
{
  public static final int UNICODE_3_2 = 262432;
  
  private Normalizer() {}
  
  public static String normalize(CharSequence paramCharSequence, Normalizer.Form paramForm, int paramInt)
  {
    return NormalizerBase.normalize(paramCharSequence.toString(), paramForm, paramInt);
  }
  
  public static boolean isNormalized(CharSequence paramCharSequence, Normalizer.Form paramForm, int paramInt)
  {
    return NormalizerBase.isNormalized(paramCharSequence.toString(), paramForm, paramInt);
  }
  
  public static final int getCombiningClass(int paramInt)
  {
    return NormalizerImpl.getCombiningClass(paramInt);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\text\Normalizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */