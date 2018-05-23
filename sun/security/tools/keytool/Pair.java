package sun.security.tools.keytool;

import java.util.Objects;

class Pair<A, B>
{
  public final A fst;
  public final B snd;
  
  public Pair(A paramA, B paramB)
  {
    fst = paramA;
    snd = paramB;
  }
  
  public String toString()
  {
    return "Pair[" + fst + "," + snd + "]";
  }
  
  public boolean equals(Object paramObject)
  {
    return ((paramObject instanceof Pair)) && (Objects.equals(fst, fst)) && (Objects.equals(snd, snd));
  }
  
  public int hashCode()
  {
    if (fst == null) {
      return snd == null ? 0 : snd.hashCode() + 1;
    }
    if (snd == null) {
      return fst.hashCode() + 2;
    }
    return fst.hashCode() * 17 + snd.hashCode();
  }
  
  public static <A, B> Pair<A, B> of(A paramA, B paramB)
  {
    return new Pair(paramA, paramB);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\keytool\Pair.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */