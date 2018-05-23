package sun.font;

import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator.Attribute;

public enum EAttribute
{
  EFAMILY(TextAttribute.FAMILY),  EWEIGHT(TextAttribute.WEIGHT),  EWIDTH(TextAttribute.WIDTH),  EPOSTURE(TextAttribute.POSTURE),  ESIZE(TextAttribute.SIZE),  ETRANSFORM(TextAttribute.TRANSFORM),  ESUPERSCRIPT(TextAttribute.SUPERSCRIPT),  EFONT(TextAttribute.FONT),  ECHAR_REPLACEMENT(TextAttribute.CHAR_REPLACEMENT),  EFOREGROUND(TextAttribute.FOREGROUND),  EBACKGROUND(TextAttribute.BACKGROUND),  EUNDERLINE(TextAttribute.UNDERLINE),  ESTRIKETHROUGH(TextAttribute.STRIKETHROUGH),  ERUN_DIRECTION(TextAttribute.RUN_DIRECTION),  EBIDI_EMBEDDING(TextAttribute.BIDI_EMBEDDING),  EJUSTIFICATION(TextAttribute.JUSTIFICATION),  EINPUT_METHOD_HIGHLIGHT(TextAttribute.INPUT_METHOD_HIGHLIGHT),  EINPUT_METHOD_UNDERLINE(TextAttribute.INPUT_METHOD_UNDERLINE),  ESWAP_COLORS(TextAttribute.SWAP_COLORS),  ENUMERIC_SHAPING(TextAttribute.NUMERIC_SHAPING),  EKERNING(TextAttribute.KERNING),  ELIGATURES(TextAttribute.LIGATURES),  ETRACKING(TextAttribute.TRACKING),  EBASELINE_TRANSFORM(null);
  
  final int mask = 1 << ordinal();
  final TextAttribute att;
  static final EAttribute[] atts = (EAttribute[])EAttribute.class.getEnumConstants();
  
  private EAttribute(TextAttribute paramTextAttribute)
  {
    att = paramTextAttribute;
  }
  
  public static EAttribute forAttribute(AttributedCharacterIterator.Attribute paramAttribute)
  {
    for (EAttribute localEAttribute : atts) {
      if (att == paramAttribute) {
        return localEAttribute;
      }
    }
    return null;
  }
  
  public String toString()
  {
    return name().substring(1).toLowerCase();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\font\EAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */