package jdk.internal.org.objectweb.asm.util;

import java.util.Map;
import jdk.internal.org.objectweb.asm.Label;

public abstract interface Textifiable
{
  public abstract void textify(StringBuffer paramStringBuffer, Map<Label, String> paramMap);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\util\Textifiable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */