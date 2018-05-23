package java.util.regex;

import java.security.AccessController;
import sun.security.action.GetPropertyAction;

public class PatternSyntaxException
  extends IllegalArgumentException
{
  private static final long serialVersionUID = -3864639126226059218L;
  private final String desc;
  private final String pattern;
  private final int index;
  private static final String nl = (String)AccessController.doPrivileged(new GetPropertyAction("line.separator"));
  
  public PatternSyntaxException(String paramString1, String paramString2, int paramInt)
  {
    desc = paramString1;
    pattern = paramString2;
    index = paramInt;
  }
  
  public int getIndex()
  {
    return index;
  }
  
  public String getDescription()
  {
    return desc;
  }
  
  public String getPattern()
  {
    return pattern;
  }
  
  public String getMessage()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(desc);
    if (index >= 0)
    {
      localStringBuffer.append(" near index ");
      localStringBuffer.append(index);
    }
    localStringBuffer.append(nl);
    localStringBuffer.append(pattern);
    if (index >= 0)
    {
      localStringBuffer.append(nl);
      for (int i = 0; i < index; i++) {
        localStringBuffer.append(' ');
      }
      localStringBuffer.append('^');
    }
    return localStringBuffer.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\regex\PatternSyntaxException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */