package java.util.regex;

public abstract interface MatchResult
{
  public abstract int start();
  
  public abstract int start(int paramInt);
  
  public abstract int end();
  
  public abstract int end(int paramInt);
  
  public abstract String group();
  
  public abstract String group(int paramInt);
  
  public abstract int groupCount();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\regex\MatchResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */