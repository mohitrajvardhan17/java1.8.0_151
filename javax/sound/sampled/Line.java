package javax.sound.sampled;

public abstract interface Line
  extends AutoCloseable
{
  public abstract Info getLineInfo();
  
  public abstract void open()
    throws LineUnavailableException;
  
  public abstract void close();
  
  public abstract boolean isOpen();
  
  public abstract Control[] getControls();
  
  public abstract boolean isControlSupported(Control.Type paramType);
  
  public abstract Control getControl(Control.Type paramType);
  
  public abstract void addLineListener(LineListener paramLineListener);
  
  public abstract void removeLineListener(LineListener paramLineListener);
  
  public static class Info
  {
    private final Class lineClass;
    
    public Info(Class<?> paramClass)
    {
      if (paramClass == null) {
        lineClass = Line.class;
      } else {
        lineClass = paramClass;
      }
    }
    
    public Class<?> getLineClass()
    {
      return lineClass;
    }
    
    public boolean matches(Info paramInfo)
    {
      if (!getClass().isInstance(paramInfo)) {
        return false;
      }
      return getLineClass().isAssignableFrom(paramInfo.getLineClass());
    }
    
    public String toString()
    {
      String str1 = "javax.sound.sampled.";
      String str2 = new String(getLineClass().toString());
      int i = str2.indexOf(str1);
      String str3;
      if (i != -1) {
        str3 = str2.substring(0, i) + str2.substring(i + str1.length(), str2.length());
      } else {
        str3 = str2;
      }
      return str3;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sound\sampled\Line.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */