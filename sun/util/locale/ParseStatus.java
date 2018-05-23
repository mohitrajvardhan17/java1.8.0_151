package sun.util.locale;

public class ParseStatus
{
  int parseLength;
  int errorIndex;
  String errorMsg;
  
  public ParseStatus()
  {
    reset();
  }
  
  public void reset()
  {
    parseLength = 0;
    errorIndex = -1;
    errorMsg = null;
  }
  
  public boolean isError()
  {
    return errorIndex >= 0;
  }
  
  public int getErrorIndex()
  {
    return errorIndex;
  }
  
  public int getParseLength()
  {
    return parseLength;
  }
  
  public String getErrorMessage()
  {
    return errorMsg;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\locale\ParseStatus.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */