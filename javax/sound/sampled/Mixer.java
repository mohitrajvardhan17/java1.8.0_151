package javax.sound.sampled;

public abstract interface Mixer
  extends Line
{
  public abstract Info getMixerInfo();
  
  public abstract Line.Info[] getSourceLineInfo();
  
  public abstract Line.Info[] getTargetLineInfo();
  
  public abstract Line.Info[] getSourceLineInfo(Line.Info paramInfo);
  
  public abstract Line.Info[] getTargetLineInfo(Line.Info paramInfo);
  
  public abstract boolean isLineSupported(Line.Info paramInfo);
  
  public abstract Line getLine(Line.Info paramInfo)
    throws LineUnavailableException;
  
  public abstract int getMaxLines(Line.Info paramInfo);
  
  public abstract Line[] getSourceLines();
  
  public abstract Line[] getTargetLines();
  
  public abstract void synchronize(Line[] paramArrayOfLine, boolean paramBoolean);
  
  public abstract void unsynchronize(Line[] paramArrayOfLine);
  
  public abstract boolean isSynchronizationSupported(Line[] paramArrayOfLine, boolean paramBoolean);
  
  public static class Info
  {
    private final String name;
    private final String vendor;
    private final String description;
    private final String version;
    
    protected Info(String paramString1, String paramString2, String paramString3, String paramString4)
    {
      name = paramString1;
      vendor = paramString2;
      description = paramString3;
      version = paramString4;
    }
    
    public final boolean equals(Object paramObject)
    {
      return super.equals(paramObject);
    }
    
    public final int hashCode()
    {
      return super.hashCode();
    }
    
    public final String getName()
    {
      return name;
    }
    
    public final String getVendor()
    {
      return vendor;
    }
    
    public final String getDescription()
    {
      return description;
    }
    
    public final String getVersion()
    {
      return version;
    }
    
    public final String toString()
    {
      return name + ", version " + version;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sound\sampled\Mixer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */