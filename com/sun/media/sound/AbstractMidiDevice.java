package com.sun.media.sound;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiDeviceReceiver;
import javax.sound.midi.MidiDeviceTransmitter;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

abstract class AbstractMidiDevice
  implements MidiDevice, ReferenceCountingDevice
{
  private static final boolean TRACE_TRANSMITTER = false;
  private ArrayList<Receiver> receiverList;
  private TransmitterList transmitterList;
  private final Object traRecLock = new Object();
  private final MidiDevice.Info info;
  private boolean open = false;
  private int openRefCount;
  private List openKeepingObjects;
  protected long id = 0L;
  
  protected AbstractMidiDevice(MidiDevice.Info paramInfo)
  {
    info = paramInfo;
    openRefCount = 0;
  }
  
  public final MidiDevice.Info getDeviceInfo()
  {
    return info;
  }
  
  public final void open()
    throws MidiUnavailableException
  {
    synchronized (this)
    {
      openRefCount = -1;
      doOpen();
    }
  }
  
  private void openInternal(Object paramObject)
    throws MidiUnavailableException
  {
    synchronized (this)
    {
      if (openRefCount != -1)
      {
        openRefCount += 1;
        getOpenKeepingObjects().add(paramObject);
      }
      doOpen();
    }
  }
  
  private void doOpen()
    throws MidiUnavailableException
  {
    synchronized (this)
    {
      if (!isOpen())
      {
        implOpen();
        open = true;
      }
    }
  }
  
  public final void close()
  {
    synchronized (this)
    {
      doClose();
      openRefCount = 0;
    }
  }
  
  public final void closeInternal(Object paramObject)
  {
    synchronized (this)
    {
      if ((getOpenKeepingObjects().remove(paramObject)) && (openRefCount > 0))
      {
        openRefCount -= 1;
        if (openRefCount == 0) {
          doClose();
        }
      }
    }
  }
  
  public final void doClose()
  {
    synchronized (this)
    {
      if (isOpen())
      {
        implClose();
        open = false;
      }
    }
  }
  
  public final boolean isOpen()
  {
    return open;
  }
  
  protected void implClose()
  {
    synchronized (traRecLock)
    {
      if (receiverList != null)
      {
        for (int i = 0; i < receiverList.size(); i++) {
          ((Receiver)receiverList.get(i)).close();
        }
        receiverList.clear();
      }
      if (transmitterList != null) {
        transmitterList.close();
      }
    }
  }
  
  public long getMicrosecondPosition()
  {
    return -1L;
  }
  
  public final int getMaxReceivers()
  {
    if (hasReceivers()) {
      return -1;
    }
    return 0;
  }
  
  public final int getMaxTransmitters()
  {
    if (hasTransmitters()) {
      return -1;
    }
    return 0;
  }
  
  public final Receiver getReceiver()
    throws MidiUnavailableException
  {
    Receiver localReceiver;
    synchronized (traRecLock)
    {
      localReceiver = createReceiver();
      getReceiverList().add(localReceiver);
    }
    return localReceiver;
  }
  
  public final List<Receiver> getReceivers()
  {
    List localList;
    synchronized (traRecLock)
    {
      if (receiverList == null) {
        localList = Collections.unmodifiableList(new ArrayList(0));
      } else {
        localList = Collections.unmodifiableList((List)receiverList.clone());
      }
    }
    return localList;
  }
  
  public final Transmitter getTransmitter()
    throws MidiUnavailableException
  {
    Transmitter localTransmitter;
    synchronized (traRecLock)
    {
      localTransmitter = createTransmitter();
      getTransmitterList().add(localTransmitter);
    }
    return localTransmitter;
  }
  
  public final List<Transmitter> getTransmitters()
  {
    List localList;
    synchronized (traRecLock)
    {
      if ((transmitterList == null) || (transmitterList.transmitters.size() == 0)) {
        localList = Collections.unmodifiableList(new ArrayList(0));
      } else {
        localList = Collections.unmodifiableList((List)transmitterList.transmitters.clone());
      }
    }
    return localList;
  }
  
  final long getId()
  {
    return id;
  }
  
  public final Receiver getReceiverReferenceCounting()
    throws MidiUnavailableException
  {
    Receiver localReceiver;
    synchronized (traRecLock)
    {
      localReceiver = getReceiver();
      openInternal(localReceiver);
    }
    return localReceiver;
  }
  
  public final Transmitter getTransmitterReferenceCounting()
    throws MidiUnavailableException
  {
    Transmitter localTransmitter;
    synchronized (traRecLock)
    {
      localTransmitter = getTransmitter();
      openInternal(localTransmitter);
    }
    return localTransmitter;
  }
  
  private synchronized List getOpenKeepingObjects()
  {
    if (openKeepingObjects == null) {
      openKeepingObjects = new ArrayList();
    }
    return openKeepingObjects;
  }
  
  private List<Receiver> getReceiverList()
  {
    synchronized (traRecLock)
    {
      if (receiverList == null) {
        receiverList = new ArrayList();
      }
    }
    return receiverList;
  }
  
  protected boolean hasReceivers()
  {
    return false;
  }
  
  protected Receiver createReceiver()
    throws MidiUnavailableException
  {
    throw new MidiUnavailableException("MIDI IN receiver not available");
  }
  
  final TransmitterList getTransmitterList()
  {
    synchronized (traRecLock)
    {
      if (transmitterList == null) {
        transmitterList = new TransmitterList();
      }
    }
    return transmitterList;
  }
  
  protected boolean hasTransmitters()
  {
    return false;
  }
  
  protected Transmitter createTransmitter()
    throws MidiUnavailableException
  {
    throw new MidiUnavailableException("MIDI OUT transmitter not available");
  }
  
  protected abstract void implOpen()
    throws MidiUnavailableException;
  
  protected final void finalize()
  {
    close();
  }
  
  abstract class AbstractReceiver
    implements MidiDeviceReceiver
  {
    private boolean open = true;
    
    AbstractReceiver() {}
    
    public final synchronized void send(MidiMessage paramMidiMessage, long paramLong)
    {
      if (!open) {
        throw new IllegalStateException("Receiver is not open");
      }
      implSend(paramMidiMessage, paramLong);
    }
    
    abstract void implSend(MidiMessage paramMidiMessage, long paramLong);
    
    public final void close()
    {
      open = false;
      synchronized (traRecLock)
      {
        AbstractMidiDevice.this.getReceiverList().remove(this);
      }
      closeInternal(this);
    }
    
    public final MidiDevice getMidiDevice()
    {
      return AbstractMidiDevice.this;
    }
    
    final boolean isOpen()
    {
      return open;
    }
  }
  
  class BasicTransmitter
    implements MidiDeviceTransmitter
  {
    private Receiver receiver = null;
    AbstractMidiDevice.TransmitterList tlist = null;
    
    protected BasicTransmitter() {}
    
    private void setTransmitterList(AbstractMidiDevice.TransmitterList paramTransmitterList)
    {
      tlist = paramTransmitterList;
    }
    
    public final void setReceiver(Receiver paramReceiver)
    {
      if ((tlist != null) && (receiver != paramReceiver))
      {
        AbstractMidiDevice.TransmitterList.access$400(tlist, this, receiver, paramReceiver);
        receiver = paramReceiver;
      }
    }
    
    public final Receiver getReceiver()
    {
      return receiver;
    }
    
    public final void close()
    {
      closeInternal(this);
      if (tlist != null)
      {
        AbstractMidiDevice.TransmitterList.access$400(tlist, this, receiver, null);
        AbstractMidiDevice.TransmitterList.access$500(tlist, this);
        tlist = null;
      }
    }
    
    public final MidiDevice getMidiDevice()
    {
      return AbstractMidiDevice.this;
    }
  }
  
  final class TransmitterList
  {
    private final ArrayList<Transmitter> transmitters = new ArrayList();
    private MidiOutDevice.MidiOutReceiver midiOutReceiver;
    private int optimizedReceiverCount = 0;
    
    TransmitterList() {}
    
    private void add(Transmitter paramTransmitter)
    {
      synchronized (transmitters)
      {
        transmitters.add(paramTransmitter);
      }
      if ((paramTransmitter instanceof AbstractMidiDevice.BasicTransmitter)) {
        ((AbstractMidiDevice.BasicTransmitter)paramTransmitter).setTransmitterList(this);
      }
    }
    
    private void remove(Transmitter paramTransmitter)
    {
      synchronized (transmitters)
      {
        int i = transmitters.indexOf(paramTransmitter);
        if (i >= 0) {
          transmitters.remove(i);
        }
      }
    }
    
    private void receiverChanged(AbstractMidiDevice.BasicTransmitter paramBasicTransmitter, Receiver paramReceiver1, Receiver paramReceiver2)
    {
      synchronized (transmitters)
      {
        if (midiOutReceiver == paramReceiver1) {
          midiOutReceiver = null;
        }
        if ((paramReceiver2 != null) && ((paramReceiver2 instanceof MidiOutDevice.MidiOutReceiver)) && (midiOutReceiver == null)) {
          midiOutReceiver = ((MidiOutDevice.MidiOutReceiver)paramReceiver2);
        }
        optimizedReceiverCount = (midiOutReceiver != null ? 1 : 0);
      }
    }
    
    void close()
    {
      synchronized (transmitters)
      {
        for (int i = 0; i < transmitters.size(); i++) {
          ((Transmitter)transmitters.get(i)).close();
        }
        transmitters.clear();
      }
    }
    
    void sendMessage(int paramInt, long paramLong)
    {
      try
      {
        synchronized (transmitters)
        {
          int i = transmitters.size();
          if (optimizedReceiverCount == i)
          {
            if (midiOutReceiver != null) {
              midiOutReceiver.sendPackedMidiMessage(paramInt, paramLong);
            }
          }
          else {
            for (int j = 0; j < i; j++)
            {
              Receiver localReceiver = ((Transmitter)transmitters.get(j)).getReceiver();
              if (localReceiver != null) {
                if (optimizedReceiverCount > 0)
                {
                  if ((localReceiver instanceof MidiOutDevice.MidiOutReceiver)) {
                    ((MidiOutDevice.MidiOutReceiver)localReceiver).sendPackedMidiMessage(paramInt, paramLong);
                  } else {
                    localReceiver.send(new FastShortMessage(paramInt), paramLong);
                  }
                }
                else {
                  localReceiver.send(new FastShortMessage(paramInt), paramLong);
                }
              }
            }
          }
        }
      }
      catch (InvalidMidiDataException localInvalidMidiDataException) {}
    }
    
    void sendMessage(byte[] paramArrayOfByte, long paramLong)
    {
      try
      {
        synchronized (transmitters)
        {
          int i = transmitters.size();
          for (int j = 0; j < i; j++)
          {
            Receiver localReceiver = ((Transmitter)transmitters.get(j)).getReceiver();
            if (localReceiver != null) {
              localReceiver.send(new FastSysexMessage(paramArrayOfByte), paramLong);
            }
          }
        }
      }
      catch (InvalidMidiDataException localInvalidMidiDataException) {}
    }
    
    void sendMessage(MidiMessage paramMidiMessage, long paramLong)
    {
      if ((paramMidiMessage instanceof FastShortMessage))
      {
        sendMessage(((FastShortMessage)paramMidiMessage).getPackedMsg(), paramLong);
        return;
      }
      synchronized (transmitters)
      {
        int i = transmitters.size();
        if (optimizedReceiverCount == i)
        {
          if (midiOutReceiver != null) {
            midiOutReceiver.send(paramMidiMessage, paramLong);
          }
        }
        else {
          for (int j = 0; j < i; j++)
          {
            Receiver localReceiver = ((Transmitter)transmitters.get(j)).getReceiver();
            if (localReceiver != null) {
              localReceiver.send(paramMidiMessage, paramLong);
            }
          }
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\AbstractMidiDevice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */