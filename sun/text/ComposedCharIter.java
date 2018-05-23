package sun.text;

import sun.text.normalizer.NormalizerImpl;

public final class ComposedCharIter
{
  public static final int DONE = -1;
  private static int[] chars;
  private static String[] decomps;
  private static int decompNum = NormalizerImpl.getDecompose(chars, decomps);
  private int curChar = -1;
  
  public ComposedCharIter() {}
  
  public int next()
  {
    if (curChar == decompNum - 1) {
      return -1;
    }
    return chars[(++curChar)];
  }
  
  public String decomposition()
  {
    return decomps[curChar];
  }
  
  static
  {
    int i = 2000;
    chars = new int[i];
    decomps = new String[i];
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\text\ComposedCharIter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */