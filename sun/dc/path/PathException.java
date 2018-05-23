package sun.dc.path;

public class PathException
  extends Exception
{
  public static final String BAD_PATH_endPath = "endPath: bad path";
  public static final String BAD_PATH_useProxy = "useProxy: bad path";
  public static final String DUMMY = "";
  
  public PathException() {}
  
  public PathException(String paramString)
  {
    super(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\dc\path\PathException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */