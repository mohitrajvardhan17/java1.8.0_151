package javax.sound.midi;

public abstract class SoundbankResource
{
  private final Soundbank soundBank;
  private final String name;
  private final Class dataClass;
  
  protected SoundbankResource(Soundbank paramSoundbank, String paramString, Class<?> paramClass)
  {
    soundBank = paramSoundbank;
    name = paramString;
    dataClass = paramClass;
  }
  
  public Soundbank getSoundbank()
  {
    return soundBank;
  }
  
  public String getName()
  {
    return name;
  }
  
  public Class<?> getDataClass()
  {
    return dataClass;
  }
  
  public abstract Object getData();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sound\midi\SoundbankResource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */