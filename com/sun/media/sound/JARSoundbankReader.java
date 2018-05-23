package com.sun.media.sound;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Soundbank;
import javax.sound.midi.spi.SoundbankReader;
import sun.reflect.misc.ReflectUtil;

public final class JARSoundbankReader
  extends SoundbankReader
{
  public JARSoundbankReader() {}
  
  private static boolean isZIP(URL paramURL)
  {
    boolean bool = false;
    try
    {
      InputStream localInputStream = paramURL.openStream();
      try
      {
        byte[] arrayOfByte = new byte[4];
        bool = localInputStream.read(arrayOfByte) == 4;
        if (bool) {
          bool = (arrayOfByte[0] == 80) && (arrayOfByte[1] == 75) && (arrayOfByte[2] == 3) && (arrayOfByte[3] == 4);
        }
      }
      finally
      {
        localInputStream.close();
      }
    }
    catch (IOException localIOException) {}
    return bool;
  }
  
  public Soundbank getSoundbank(URL paramURL)
    throws InvalidMidiDataException, IOException
  {
    if (!isZIP(paramURL)) {
      return null;
    }
    ArrayList localArrayList = new ArrayList();
    URLClassLoader localURLClassLoader = URLClassLoader.newInstance(new URL[] { paramURL });
    InputStream localInputStream = localURLClassLoader.getResourceAsStream("META-INF/services/javax.sound.midi.Soundbank");
    if (localInputStream == null) {
      return null;
    }
    try
    {
      localObject1 = new BufferedReader(new InputStreamReader(localInputStream));
      for (localObject2 = ((BufferedReader)localObject1).readLine(); localObject2 != null; localObject2 = ((BufferedReader)localObject1).readLine()) {
        if (!((String)localObject2).startsWith("#")) {
          try
          {
            Class localClass = Class.forName(((String)localObject2).trim(), false, localURLClassLoader);
            if (Soundbank.class.isAssignableFrom(localClass))
            {
              Object localObject3 = ReflectUtil.newInstance(localClass);
              localArrayList.add((Soundbank)localObject3);
            }
          }
          catch (ClassNotFoundException localClassNotFoundException) {}catch (InstantiationException localInstantiationException) {}catch (IllegalAccessException localIllegalAccessException) {}
        }
      }
    }
    finally
    {
      localInputStream.close();
    }
    if (localArrayList.size() == 0) {
      return null;
    }
    if (localArrayList.size() == 1) {
      return (Soundbank)localArrayList.get(0);
    }
    Object localObject1 = new SimpleSoundbank();
    Object localObject2 = localArrayList.iterator();
    while (((Iterator)localObject2).hasNext())
    {
      Soundbank localSoundbank = (Soundbank)((Iterator)localObject2).next();
      ((SimpleSoundbank)localObject1).addAllInstruments(localSoundbank);
    }
    return (Soundbank)localObject1;
  }
  
  public Soundbank getSoundbank(InputStream paramInputStream)
    throws InvalidMidiDataException, IOException
  {
    return null;
  }
  
  public Soundbank getSoundbank(File paramFile)
    throws InvalidMidiDataException, IOException
  {
    return getSoundbank(paramFile.toURI().toURL());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\JARSoundbankReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */