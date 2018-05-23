package javax.sound.sampled.spi;

import javax.sound.sampled.Mixer;
import javax.sound.sampled.Mixer.Info;

public abstract class MixerProvider
{
  public MixerProvider() {}
  
  public boolean isMixerSupported(Mixer.Info paramInfo)
  {
    Mixer.Info[] arrayOfInfo = getMixerInfo();
    for (int i = 0; i < arrayOfInfo.length; i++) {
      if (paramInfo.equals(arrayOfInfo[i])) {
        return true;
      }
    }
    return false;
  }
  
  public abstract Mixer.Info[] getMixerInfo();
  
  public abstract Mixer getMixer(Mixer.Info paramInfo);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sound\sampled\spi\MixerProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */