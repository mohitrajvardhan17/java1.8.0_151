package javax.sound.midi;

import java.util.List;

public abstract interface MidiDevice
  extends AutoCloseable
{
  public abstract Info getDeviceInfo();
  
  public abstract void open()
    throws MidiUnavailableException;
  
  public abstract void close();
  
  public abstract boolean isOpen();
  
  public abstract long getMicrosecondPosition();
  
  public abstract int getMaxReceivers();
  
  public abstract int getMaxTransmitters();
  
  public abstract Receiver getReceiver()
    throws MidiUnavailableException;
  
  public abstract List<Receiver> getReceivers();
  
  public abstract Transmitter getTransmitter()
    throws MidiUnavailableException;
  
  public abstract List<Transmitter> getTransmitters();
  
  public static class Info
  {
    private String name;
    private String vendor;
    private String description;
    private String version;
    
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
      return name;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sound\midi\MidiDevice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */