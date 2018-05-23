package sun.swing.plaf.synth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.plaf.synth.SynthStyle;

public class StyleAssociation
{
  private SynthStyle _style;
  private Pattern _pattern;
  private Matcher _matcher;
  private int _id;
  
  public static StyleAssociation createStyleAssociation(String paramString, SynthStyle paramSynthStyle)
    throws PatternSyntaxException
  {
    return createStyleAssociation(paramString, paramSynthStyle, 0);
  }
  
  public static StyleAssociation createStyleAssociation(String paramString, SynthStyle paramSynthStyle, int paramInt)
    throws PatternSyntaxException
  {
    return new StyleAssociation(paramString, paramSynthStyle, paramInt);
  }
  
  private StyleAssociation(String paramString, SynthStyle paramSynthStyle, int paramInt)
    throws PatternSyntaxException
  {
    _style = paramSynthStyle;
    _pattern = Pattern.compile(paramString);
    _id = paramInt;
  }
  
  public int getID()
  {
    return _id;
  }
  
  public synchronized boolean matches(CharSequence paramCharSequence)
  {
    if (_matcher == null) {
      _matcher = _pattern.matcher(paramCharSequence);
    } else {
      _matcher.reset(paramCharSequence);
    }
    return _matcher.matches();
  }
  
  public String getText()
  {
    return _pattern.pattern();
  }
  
  public SynthStyle getStyle()
  {
    return _style;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\swing\plaf\synth\StyleAssociation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */