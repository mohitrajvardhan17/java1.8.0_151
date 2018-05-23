package javax.script;

public class ScriptException
  extends Exception
{
  private static final long serialVersionUID = 8265071037049225001L;
  private String fileName;
  private int lineNumber;
  private int columnNumber;
  
  public ScriptException(String paramString)
  {
    super(paramString);
    fileName = null;
    lineNumber = -1;
    columnNumber = -1;
  }
  
  public ScriptException(Exception paramException)
  {
    super(paramException);
    fileName = null;
    lineNumber = -1;
    columnNumber = -1;
  }
  
  public ScriptException(String paramString1, String paramString2, int paramInt)
  {
    super(paramString1);
    fileName = paramString2;
    lineNumber = paramInt;
    columnNumber = -1;
  }
  
  public ScriptException(String paramString1, String paramString2, int paramInt1, int paramInt2)
  {
    super(paramString1);
    fileName = paramString2;
    lineNumber = paramInt1;
    columnNumber = paramInt2;
  }
  
  public String getMessage()
  {
    String str = super.getMessage();
    if (fileName != null)
    {
      str = str + " in " + fileName;
      if (lineNumber != -1) {
        str = str + " at line number " + lineNumber;
      }
      if (columnNumber != -1) {
        str = str + " at column number " + columnNumber;
      }
    }
    return str;
  }
  
  public int getLineNumber()
  {
    return lineNumber;
  }
  
  public int getColumnNumber()
  {
    return columnNumber;
  }
  
  public String getFileName()
  {
    return fileName;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\script\ScriptException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */