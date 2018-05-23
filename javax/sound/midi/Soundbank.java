package javax.sound.midi;

public abstract interface Soundbank
{
  public abstract String getName();
  
  public abstract String getVersion();
  
  public abstract String getVendor();
  
  public abstract String getDescription();
  
  public abstract SoundbankResource[] getResources();
  
  public abstract Instrument[] getInstruments();
  
  public abstract Instrument getInstrument(Patch paramPatch);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sound\midi\Soundbank.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */