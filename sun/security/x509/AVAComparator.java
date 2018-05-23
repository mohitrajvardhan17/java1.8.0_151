package sun.security.x509;

import java.util.Comparator;

class AVAComparator
  implements Comparator<AVA>
{
  private static final Comparator<AVA> INSTANCE = new AVAComparator();
  
  private AVAComparator() {}
  
  static Comparator<AVA> getInstance()
  {
    return INSTANCE;
  }
  
  public int compare(AVA paramAVA1, AVA paramAVA2)
  {
    boolean bool1 = paramAVA1.hasRFC2253Keyword();
    boolean bool2 = paramAVA2.hasRFC2253Keyword();
    if (bool1 == bool2) {
      return paramAVA1.toRFC2253CanonicalString().compareTo(paramAVA2.toRFC2253CanonicalString());
    }
    if (bool1) {
      return -1;
    }
    return 1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\AVAComparator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */