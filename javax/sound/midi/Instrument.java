package javax.sound.midi;

public abstract class Instrument
  extends SoundbankResource
{
  private final Patch patch;
  
  protected Instrument(Soundbank paramSoundbank, Patch paramPatch, String paramString, Class<?> paramClass)
  {
    super(paramSoundbank, paramString, paramClass);
    patch = paramPatch;
  }
  
  public Patch getPatch()
  {
    return patch;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sound\midi\Instrument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */