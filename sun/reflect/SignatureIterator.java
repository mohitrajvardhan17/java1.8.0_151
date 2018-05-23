package sun.reflect;

public class SignatureIterator
{
  private final String sig;
  private int idx;
  
  public SignatureIterator(String paramString)
  {
    sig = paramString;
    reset();
  }
  
  public void reset()
  {
    idx = 1;
  }
  
  public boolean atEnd()
  {
    return sig.charAt(idx) == ')';
  }
  
  public String next()
  {
    if (atEnd()) {
      return null;
    }
    int i = sig.charAt(idx);
    if ((i != 91) && (i != 76))
    {
      idx += 1;
      return new String(new char[] { i });
    }
    int j = idx;
    if (i == 91) {
      while ((i = sig.charAt(j)) == '[') {
        j++;
      }
    }
    if (i == 76) {
      while (sig.charAt(j) != ';') {
        j++;
      }
    }
    int k = idx;
    idx = (j + 1);
    return sig.substring(k, idx);
  }
  
  public String returnType()
  {
    if (!atEnd()) {
      throw new InternalError("Illegal use of SignatureIterator");
    }
    return sig.substring(idx + 1, sig.length());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\SignatureIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */