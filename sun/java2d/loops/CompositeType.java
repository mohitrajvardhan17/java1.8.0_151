package sun.java2d.loops;

import java.awt.AlphaComposite;
import java.util.HashMap;

public final class CompositeType
{
  private static int unusedUID = 1;
  private static final HashMap<String, Integer> compositeUIDMap = new HashMap(100);
  public static final String DESC_ANY = "Any CompositeContext";
  public static final String DESC_XOR = "XOR mode";
  public static final String DESC_CLEAR = "Porter-Duff Clear";
  public static final String DESC_SRC = "Porter-Duff Src";
  public static final String DESC_DST = "Porter-Duff Dst";
  public static final String DESC_SRC_OVER = "Porter-Duff Src Over Dst";
  public static final String DESC_DST_OVER = "Porter-Duff Dst Over Src";
  public static final String DESC_SRC_IN = "Porter-Duff Src In Dst";
  public static final String DESC_DST_IN = "Porter-Duff Dst In Src";
  public static final String DESC_SRC_OUT = "Porter-Duff Src HeldOutBy Dst";
  public static final String DESC_DST_OUT = "Porter-Duff Dst HeldOutBy Src";
  public static final String DESC_SRC_ATOP = "Porter-Duff Src Atop Dst";
  public static final String DESC_DST_ATOP = "Porter-Duff Dst Atop Src";
  public static final String DESC_ALPHA_XOR = "Porter-Duff Xor";
  public static final String DESC_SRC_NO_EA = "Porter-Duff Src, No Extra Alpha";
  public static final String DESC_SRC_OVER_NO_EA = "Porter-Duff SrcOverDst, No Extra Alpha";
  public static final String DESC_ANY_ALPHA = "Any AlphaComposite Rule";
  public static final CompositeType Any = new CompositeType(null, "Any CompositeContext");
  public static final CompositeType General = Any;
  public static final CompositeType AnyAlpha = General.deriveSubType("Any AlphaComposite Rule");
  public static final CompositeType Xor = General.deriveSubType("XOR mode");
  public static final CompositeType Clear = AnyAlpha.deriveSubType("Porter-Duff Clear");
  public static final CompositeType Src = AnyAlpha.deriveSubType("Porter-Duff Src");
  public static final CompositeType Dst = AnyAlpha.deriveSubType("Porter-Duff Dst");
  public static final CompositeType SrcOver = AnyAlpha.deriveSubType("Porter-Duff Src Over Dst");
  public static final CompositeType DstOver = AnyAlpha.deriveSubType("Porter-Duff Dst Over Src");
  public static final CompositeType SrcIn = AnyAlpha.deriveSubType("Porter-Duff Src In Dst");
  public static final CompositeType DstIn = AnyAlpha.deriveSubType("Porter-Duff Dst In Src");
  public static final CompositeType SrcOut = AnyAlpha.deriveSubType("Porter-Duff Src HeldOutBy Dst");
  public static final CompositeType DstOut = AnyAlpha.deriveSubType("Porter-Duff Dst HeldOutBy Src");
  public static final CompositeType SrcAtop = AnyAlpha.deriveSubType("Porter-Duff Src Atop Dst");
  public static final CompositeType DstAtop = AnyAlpha.deriveSubType("Porter-Duff Dst Atop Src");
  public static final CompositeType AlphaXor = AnyAlpha.deriveSubType("Porter-Duff Xor");
  public static final CompositeType SrcNoEa = Src.deriveSubType("Porter-Duff Src, No Extra Alpha");
  public static final CompositeType SrcOverNoEa = SrcOver.deriveSubType("Porter-Duff SrcOverDst, No Extra Alpha");
  public static final CompositeType OpaqueSrcOverNoEa = SrcOverNoEa.deriveSubType("Porter-Duff Src").deriveSubType("Porter-Duff Src, No Extra Alpha");
  private int uniqueID;
  private String desc;
  private CompositeType next;
  
  public CompositeType deriveSubType(String paramString)
  {
    return new CompositeType(this, paramString);
  }
  
  public static CompositeType forAlphaComposite(AlphaComposite paramAlphaComposite)
  {
    switch (paramAlphaComposite.getRule())
    {
    case 1: 
      return Clear;
    case 2: 
      if (paramAlphaComposite.getAlpha() >= 1.0F) {
        return SrcNoEa;
      }
      return Src;
    case 9: 
      return Dst;
    case 3: 
      if (paramAlphaComposite.getAlpha() >= 1.0F) {
        return SrcOverNoEa;
      }
      return SrcOver;
    case 4: 
      return DstOver;
    case 5: 
      return SrcIn;
    case 6: 
      return DstIn;
    case 7: 
      return SrcOut;
    case 8: 
      return DstOut;
    case 10: 
      return SrcAtop;
    case 11: 
      return DstAtop;
    case 12: 
      return AlphaXor;
    }
    throw new InternalError("Unrecognized alpha rule");
  }
  
  private CompositeType(CompositeType paramCompositeType, String paramString)
  {
    next = paramCompositeType;
    desc = paramString;
    uniqueID = makeUniqueID(paramString);
  }
  
  public static final synchronized int makeUniqueID(String paramString)
  {
    Integer localInteger = (Integer)compositeUIDMap.get(paramString);
    if (localInteger == null)
    {
      if (unusedUID > 255) {
        throw new InternalError("composite type id overflow");
      }
      localInteger = Integer.valueOf(unusedUID++);
      compositeUIDMap.put(paramString, localInteger);
    }
    return localInteger.intValue();
  }
  
  public int getUniqueID()
  {
    return uniqueID;
  }
  
  public String getDescriptor()
  {
    return desc;
  }
  
  public CompositeType getSuperType()
  {
    return next;
  }
  
  public int hashCode()
  {
    return desc.hashCode();
  }
  
  public boolean isDerivedFrom(CompositeType paramCompositeType)
  {
    CompositeType localCompositeType = this;
    do
    {
      if (desc == desc) {
        return true;
      }
      localCompositeType = next;
    } while (localCompositeType != null);
    return false;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof CompositeType)) {
      return uniqueID == uniqueID;
    }
    return false;
  }
  
  public String toString()
  {
    return desc;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\loops\CompositeType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */