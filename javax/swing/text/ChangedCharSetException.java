package javax.swing.text;

import java.io.IOException;

public class ChangedCharSetException
  extends IOException
{
  String charSetSpec;
  boolean charSetKey;
  
  public ChangedCharSetException(String paramString, boolean paramBoolean)
  {
    charSetSpec = paramString;
    charSetKey = paramBoolean;
  }
  
  public String getCharSetSpec()
  {
    return charSetSpec;
  }
  
  public boolean keyEqualsCharSet()
  {
    return charSetKey;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\ChangedCharSetException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */