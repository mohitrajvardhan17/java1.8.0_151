package sun.security.tools.policytool;

class AudioPerm
  extends Perm
{
  public AudioPerm()
  {
    super("AudioPermission", "javax.sound.sampled.AudioPermission", new String[] { "play", "record" }, null);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\AudioPerm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */