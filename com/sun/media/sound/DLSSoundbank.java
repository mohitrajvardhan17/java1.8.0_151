package com.sun.media.sound;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import javax.sound.midi.Instrument;
import javax.sound.midi.Patch;
import javax.sound.midi.Soundbank;
import javax.sound.midi.SoundbankResource;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public final class DLSSoundbank
  implements Soundbank
{
  private static final int DLS_CDL_AND = 1;
  private static final int DLS_CDL_OR = 2;
  private static final int DLS_CDL_XOR = 3;
  private static final int DLS_CDL_ADD = 4;
  private static final int DLS_CDL_SUBTRACT = 5;
  private static final int DLS_CDL_MULTIPLY = 6;
  private static final int DLS_CDL_DIVIDE = 7;
  private static final int DLS_CDL_LOGICAL_AND = 8;
  private static final int DLS_CDL_LOGICAL_OR = 9;
  private static final int DLS_CDL_LT = 10;
  private static final int DLS_CDL_LE = 11;
  private static final int DLS_CDL_GT = 12;
  private static final int DLS_CDL_GE = 13;
  private static final int DLS_CDL_EQ = 14;
  private static final int DLS_CDL_NOT = 15;
  private static final int DLS_CDL_CONST = 16;
  private static final int DLS_CDL_QUERY = 17;
  private static final int DLS_CDL_QUERYSUPPORTED = 18;
  private static final DLSID DLSID_GMInHardware = new DLSID(395259684L, 50020, 4561, 167, 96, 0, 0, 248, 117, 172, 18);
  private static final DLSID DLSID_GSInHardware = new DLSID(395259685L, 50020, 4561, 167, 96, 0, 0, 248, 117, 172, 18);
  private static final DLSID DLSID_XGInHardware = new DLSID(395259686L, 50020, 4561, 167, 96, 0, 0, 248, 117, 172, 18);
  private static final DLSID DLSID_SupportsDLS1 = new DLSID(395259687L, 50020, 4561, 167, 96, 0, 0, 248, 117, 172, 18);
  private static final DLSID DLSID_SupportsDLS2 = new DLSID(-247096859L, 18057, 4562, 175, 166, 0, 170, 0, 36, 216, 182);
  private static final DLSID DLSID_SampleMemorySize = new DLSID(395259688L, 50020, 4561, 167, 96, 0, 0, 248, 117, 172, 18);
  private static final DLSID DLSID_ManufacturersID = new DLSID(-1338109567L, 32917, 4562, 161, 239, 0, 96, 8, 51, 219, 216);
  private static final DLSID DLSID_ProductID = new DLSID(-1338109566L, 32917, 4562, 161, 239, 0, 96, 8, 51, 219, 216);
  private static final DLSID DLSID_SamplePlaybackRate = new DLSID(714209043L, 42175, 4562, 187, 223, 0, 96, 8, 51, 219, 216);
  private long major = -1L;
  private long minor = -1L;
  private final DLSInfo info = new DLSInfo();
  private final List<DLSInstrument> instruments = new ArrayList();
  private final List<DLSSample> samples = new ArrayList();
  private boolean largeFormat = false;
  private File sampleFile;
  private Map<DLSRegion, Long> temp_rgnassign = new HashMap();
  
  public DLSSoundbank() {}
  
  /* Error */
  public DLSSoundbank(java.net.URL paramURL)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 897	java/lang/Object:<init>	()V
    //   4: aload_0
    //   5: ldc2_w 410
    //   8: putfield 792	com/sun/media/sound/DLSSoundbank:major	J
    //   11: aload_0
    //   12: ldc2_w 410
    //   15: putfield 793	com/sun/media/sound/DLSSoundbank:minor	J
    //   18: aload_0
    //   19: new 440	com/sun/media/sound/DLSInfo
    //   22: dup
    //   23: invokespecial 812	com/sun/media/sound/DLSInfo:<init>	()V
    //   26: putfield 795	com/sun/media/sound/DLSSoundbank:info	Lcom/sun/media/sound/DLSInfo;
    //   29: aload_0
    //   30: new 467	java/util/ArrayList
    //   33: dup
    //   34: invokespecial 907	java/util/ArrayList:<init>	()V
    //   37: putfield 806	com/sun/media/sound/DLSSoundbank:instruments	Ljava/util/List;
    //   40: aload_0
    //   41: new 467	java/util/ArrayList
    //   44: dup
    //   45: invokespecial 907	java/util/ArrayList:<init>	()V
    //   48: putfield 807	com/sun/media/sound/DLSSoundbank:samples	Ljava/util/List;
    //   51: aload_0
    //   52: iconst_0
    //   53: putfield 794	com/sun/media/sound/DLSSoundbank:largeFormat	Z
    //   56: aload_0
    //   57: new 469	java/util/HashMap
    //   60: dup
    //   61: invokespecial 909	java/util/HashMap:<init>	()V
    //   64: putfield 808	com/sun/media/sound/DLSSoundbank:temp_rgnassign	Ljava/util/Map;
    //   67: aload_1
    //   68: invokevirtual 906	java/net/URL:openStream	()Ljava/io/InputStream;
    //   71: astore_2
    //   72: aload_0
    //   73: aload_2
    //   74: invokespecial 838	com/sun/media/sound/DLSSoundbank:readSoundbank	(Ljava/io/InputStream;)V
    //   77: aload_2
    //   78: invokevirtual 894	java/io/InputStream:close	()V
    //   81: goto +10 -> 91
    //   84: astore_3
    //   85: aload_2
    //   86: invokevirtual 894	java/io/InputStream:close	()V
    //   89: aload_3
    //   90: athrow
    //   91: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	92	0	this	DLSSoundbank
    //   0	92	1	paramURL	java.net.URL
    //   71	15	2	localInputStream	InputStream
    //   84	6	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   72	77	84	finally
  }
  
  /* Error */
  public DLSSoundbank(File paramFile)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 897	java/lang/Object:<init>	()V
    //   4: aload_0
    //   5: ldc2_w 410
    //   8: putfield 792	com/sun/media/sound/DLSSoundbank:major	J
    //   11: aload_0
    //   12: ldc2_w 410
    //   15: putfield 793	com/sun/media/sound/DLSSoundbank:minor	J
    //   18: aload_0
    //   19: new 440	com/sun/media/sound/DLSInfo
    //   22: dup
    //   23: invokespecial 812	com/sun/media/sound/DLSInfo:<init>	()V
    //   26: putfield 795	com/sun/media/sound/DLSSoundbank:info	Lcom/sun/media/sound/DLSInfo;
    //   29: aload_0
    //   30: new 467	java/util/ArrayList
    //   33: dup
    //   34: invokespecial 907	java/util/ArrayList:<init>	()V
    //   37: putfield 806	com/sun/media/sound/DLSSoundbank:instruments	Ljava/util/List;
    //   40: aload_0
    //   41: new 467	java/util/ArrayList
    //   44: dup
    //   45: invokespecial 907	java/util/ArrayList:<init>	()V
    //   48: putfield 807	com/sun/media/sound/DLSSoundbank:samples	Ljava/util/List;
    //   51: aload_0
    //   52: iconst_0
    //   53: putfield 794	com/sun/media/sound/DLSSoundbank:largeFormat	Z
    //   56: aload_0
    //   57: new 469	java/util/HashMap
    //   60: dup
    //   61: invokespecial 909	java/util/HashMap:<init>	()V
    //   64: putfield 808	com/sun/media/sound/DLSSoundbank:temp_rgnassign	Ljava/util/Map;
    //   67: aload_0
    //   68: iconst_1
    //   69: putfield 794	com/sun/media/sound/DLSSoundbank:largeFormat	Z
    //   72: aload_0
    //   73: aload_1
    //   74: putfield 805	com/sun/media/sound/DLSSoundbank:sampleFile	Ljava/io/File;
    //   77: new 457	java/io/FileInputStream
    //   80: dup
    //   81: aload_1
    //   82: invokespecial 893	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   85: astore_2
    //   86: aload_0
    //   87: aload_2
    //   88: invokespecial 838	com/sun/media/sound/DLSSoundbank:readSoundbank	(Ljava/io/InputStream;)V
    //   91: aload_2
    //   92: invokevirtual 894	java/io/InputStream:close	()V
    //   95: goto +10 -> 105
    //   98: astore_3
    //   99: aload_2
    //   100: invokevirtual 894	java/io/InputStream:close	()V
    //   103: aload_3
    //   104: athrow
    //   105: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	106	0	this	DLSSoundbank
    //   0	106	1	paramFile	File
    //   85	15	2	localFileInputStream	java.io.FileInputStream
    //   98	6	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   86	91	98	finally
  }
  
  public DLSSoundbank(InputStream paramInputStream)
    throws IOException
  {
    readSoundbank(paramInputStream);
  }
  
  private void readSoundbank(InputStream paramInputStream)
    throws IOException
  {
    RIFFReader localRIFFReader = new RIFFReader(paramInputStream);
    if (!localRIFFReader.getFormat().equals("RIFF")) {
      throw new RIFFInvalidFormatException("Input stream is not a valid RIFF stream!");
    }
    if (!localRIFFReader.getType().equals("DLS ")) {
      throw new RIFFInvalidFormatException("Input stream is not a valid DLS soundbank!");
    }
    while (localRIFFReader.hasNextChunk())
    {
      localObject = localRIFFReader.nextChunk();
      if (((RIFFReader)localObject).getFormat().equals("LIST"))
      {
        if (((RIFFReader)localObject).getType().equals("INFO")) {
          readInfoChunk((RIFFReader)localObject);
        }
        if (((RIFFReader)localObject).getType().equals("lins")) {
          readLinsChunk((RIFFReader)localObject);
        }
        if (((RIFFReader)localObject).getType().equals("wvpl")) {
          readWvplChunk((RIFFReader)localObject);
        }
      }
      else
      {
        if ((((RIFFReader)localObject).getFormat().equals("cdl ")) && (!readCdlChunk((RIFFReader)localObject))) {
          throw new RIFFInvalidFormatException("DLS file isn't supported!");
        }
        if ((!((RIFFReader)localObject).getFormat().equals("colh")) || ((!((RIFFReader)localObject).getFormat().equals("ptbl")) || (((RIFFReader)localObject).getFormat().equals("vers"))))
        {
          major = ((RIFFReader)localObject).readUnsignedInt();
          minor = ((RIFFReader)localObject).readUnsignedInt();
        }
      }
    }
    Object localObject = temp_rgnassign.entrySet().iterator();
    while (((Iterator)localObject).hasNext())
    {
      Map.Entry localEntry = (Map.Entry)((Iterator)localObject).next();
      getKeysample = ((DLSSample)samples.get((int)((Long)localEntry.getValue()).longValue()));
    }
    temp_rgnassign = null;
  }
  
  private boolean cdlIsQuerySupported(DLSID paramDLSID)
  {
    return (paramDLSID.equals(DLSID_GMInHardware)) || (paramDLSID.equals(DLSID_GSInHardware)) || (paramDLSID.equals(DLSID_XGInHardware)) || (paramDLSID.equals(DLSID_SupportsDLS1)) || (paramDLSID.equals(DLSID_SupportsDLS2)) || (paramDLSID.equals(DLSID_SampleMemorySize)) || (paramDLSID.equals(DLSID_ManufacturersID)) || (paramDLSID.equals(DLSID_ProductID)) || (paramDLSID.equals(DLSID_SamplePlaybackRate));
  }
  
  private long cdlQuery(DLSID paramDLSID)
  {
    if (paramDLSID.equals(DLSID_GMInHardware)) {
      return 1L;
    }
    if (paramDLSID.equals(DLSID_GSInHardware)) {
      return 0L;
    }
    if (paramDLSID.equals(DLSID_XGInHardware)) {
      return 0L;
    }
    if (paramDLSID.equals(DLSID_SupportsDLS1)) {
      return 1L;
    }
    if (paramDLSID.equals(DLSID_SupportsDLS2)) {
      return 1L;
    }
    if (paramDLSID.equals(DLSID_SampleMemorySize)) {
      return Runtime.getRuntime().totalMemory();
    }
    if (paramDLSID.equals(DLSID_ManufacturersID)) {
      return 0L;
    }
    if (paramDLSID.equals(DLSID_ProductID)) {
      return 0L;
    }
    if (paramDLSID.equals(DLSID_SamplePlaybackRate)) {
      return 44100L;
    }
    return 0L;
  }
  
  private boolean readCdlChunk(RIFFReader paramRIFFReader)
    throws IOException
  {
    Stack localStack = new Stack();
    while (paramRIFFReader.available() != 0)
    {
      int i = paramRIFFReader.readUnsignedShort();
      long l1;
      long l2;
      DLSID localDLSID;
      switch (i)
      {
      case 1: 
        l1 = ((Long)localStack.pop()).longValue();
        l2 = ((Long)localStack.pop()).longValue();
        localStack.push(Long.valueOf((l1 != 0L) && (l2 != 0L) ? 1L : 0L));
        break;
      case 2: 
        l1 = ((Long)localStack.pop()).longValue();
        l2 = ((Long)localStack.pop()).longValue();
        localStack.push(Long.valueOf((l1 != 0L) || (l2 != 0L) ? 1L : 0L));
        break;
      case 3: 
        l1 = ((Long)localStack.pop()).longValue();
        l2 = ((Long)localStack.pop()).longValue();
        localStack.push(Long.valueOf(((l1 != 0L ? 1 : 0) ^ (l2 != 0L ? 1 : 0)) != 0 ? 1L : 0L));
        break;
      case 4: 
        l1 = ((Long)localStack.pop()).longValue();
        l2 = ((Long)localStack.pop()).longValue();
        localStack.push(Long.valueOf(l1 + l2));
        break;
      case 5: 
        l1 = ((Long)localStack.pop()).longValue();
        l2 = ((Long)localStack.pop()).longValue();
        localStack.push(Long.valueOf(l1 - l2));
        break;
      case 6: 
        l1 = ((Long)localStack.pop()).longValue();
        l2 = ((Long)localStack.pop()).longValue();
        localStack.push(Long.valueOf(l1 * l2));
        break;
      case 7: 
        l1 = ((Long)localStack.pop()).longValue();
        l2 = ((Long)localStack.pop()).longValue();
        localStack.push(Long.valueOf(l1 / l2));
        break;
      case 8: 
        l1 = ((Long)localStack.pop()).longValue();
        l2 = ((Long)localStack.pop()).longValue();
        localStack.push(Long.valueOf((l1 != 0L) && (l2 != 0L) ? 1L : 0L));
        break;
      case 9: 
        l1 = ((Long)localStack.pop()).longValue();
        l2 = ((Long)localStack.pop()).longValue();
        localStack.push(Long.valueOf((l1 != 0L) || (l2 != 0L) ? 1L : 0L));
        break;
      case 10: 
        l1 = ((Long)localStack.pop()).longValue();
        l2 = ((Long)localStack.pop()).longValue();
        localStack.push(Long.valueOf(l1 < l2 ? 1L : 0L));
        break;
      case 11: 
        l1 = ((Long)localStack.pop()).longValue();
        l2 = ((Long)localStack.pop()).longValue();
        localStack.push(Long.valueOf(l1 <= l2 ? 1L : 0L));
        break;
      case 12: 
        l1 = ((Long)localStack.pop()).longValue();
        l2 = ((Long)localStack.pop()).longValue();
        localStack.push(Long.valueOf(l1 > l2 ? 1L : 0L));
        break;
      case 13: 
        l1 = ((Long)localStack.pop()).longValue();
        l2 = ((Long)localStack.pop()).longValue();
        localStack.push(Long.valueOf(l1 >= l2 ? 1L : 0L));
        break;
      case 14: 
        l1 = ((Long)localStack.pop()).longValue();
        l2 = ((Long)localStack.pop()).longValue();
        localStack.push(Long.valueOf(l1 == l2 ? 1L : 0L));
        break;
      case 15: 
        l1 = ((Long)localStack.pop()).longValue();
        l2 = ((Long)localStack.pop()).longValue();
        localStack.push(Long.valueOf(l1 == 0L ? 1L : 0L));
        break;
      case 16: 
        localStack.push(Long.valueOf(paramRIFFReader.readUnsignedInt()));
        break;
      case 17: 
        localDLSID = DLSID.read(paramRIFFReader);
        localStack.push(Long.valueOf(cdlQuery(localDLSID)));
        break;
      case 18: 
        localDLSID = DLSID.read(paramRIFFReader);
        localStack.push(Long.valueOf(cdlIsQuerySupported(localDLSID) ? 1L : 0L));
      }
    }
    if (localStack.isEmpty()) {
      return false;
    }
    return ((Long)localStack.pop()).longValue() == 1L;
  }
  
  private void readInfoChunk(RIFFReader paramRIFFReader)
    throws IOException
  {
    info.name = null;
    while (paramRIFFReader.hasNextChunk())
    {
      RIFFReader localRIFFReader = paramRIFFReader.nextChunk();
      String str = localRIFFReader.getFormat();
      if (str.equals("INAM")) {
        info.name = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ICRD")) {
        info.creationDate = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("IENG")) {
        info.engineers = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("IPRD")) {
        info.product = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ICOP")) {
        info.copyright = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ICMT")) {
        info.comments = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ISFT")) {
        info.tools = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("IARL")) {
        info.archival_location = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("IART")) {
        info.artist = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ICMS")) {
        info.commissioned = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("IGNR")) {
        info.genre = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("IKEY")) {
        info.keywords = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("IMED")) {
        info.medium = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ISBJ")) {
        info.subject = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ISRC")) {
        info.source = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ISRF")) {
        info.source_form = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ITCH")) {
        info.technician = localRIFFReader.readString(localRIFFReader.available());
      }
    }
  }
  
  private void readLinsChunk(RIFFReader paramRIFFReader)
    throws IOException
  {
    while (paramRIFFReader.hasNextChunk())
    {
      RIFFReader localRIFFReader = paramRIFFReader.nextChunk();
      if ((localRIFFReader.getFormat().equals("LIST")) && (localRIFFReader.getType().equals("ins "))) {
        readInsChunk(localRIFFReader);
      }
    }
  }
  
  private void readInsChunk(RIFFReader paramRIFFReader)
    throws IOException
  {
    DLSInstrument localDLSInstrument = new DLSInstrument(this);
    while (paramRIFFReader.hasNextChunk())
    {
      RIFFReader localRIFFReader = paramRIFFReader.nextChunk();
      String str = localRIFFReader.getFormat();
      if (str.equals("LIST"))
      {
        if (localRIFFReader.getType().equals("INFO")) {
          readInsInfoChunk(localDLSInstrument, localRIFFReader);
        }
        Object localObject1;
        Object localObject2;
        if (localRIFFReader.getType().equals("lrgn")) {
          while (localRIFFReader.hasNextChunk())
          {
            localObject1 = localRIFFReader.nextChunk();
            if (((RIFFReader)localObject1).getFormat().equals("LIST"))
            {
              if (((RIFFReader)localObject1).getType().equals("rgn "))
              {
                localObject2 = new DLSRegion();
                if (readRgnChunk((DLSRegion)localObject2, (RIFFReader)localObject1)) {
                  localDLSInstrument.getRegions().add(localObject2);
                }
              }
              if (((RIFFReader)localObject1).getType().equals("rgn2"))
              {
                localObject2 = new DLSRegion();
                if (readRgnChunk((DLSRegion)localObject2, (RIFFReader)localObject1)) {
                  localDLSInstrument.getRegions().add(localObject2);
                }
              }
            }
          }
        }
        if (localRIFFReader.getType().equals("lart"))
        {
          localObject1 = new ArrayList();
          while (localRIFFReader.hasNextChunk())
          {
            localObject2 = localRIFFReader.nextChunk();
            if ((localRIFFReader.getFormat().equals("cdl ")) && (!readCdlChunk(localRIFFReader)))
            {
              ((List)localObject1).clear();
              break;
            }
            if (((RIFFReader)localObject2).getFormat().equals("art1")) {
              readArt1Chunk((List)localObject1, (RIFFReader)localObject2);
            }
          }
          localDLSInstrument.getModulators().addAll((Collection)localObject1);
        }
        if (localRIFFReader.getType().equals("lar2"))
        {
          localObject1 = new ArrayList();
          while (localRIFFReader.hasNextChunk())
          {
            localObject2 = localRIFFReader.nextChunk();
            if ((localRIFFReader.getFormat().equals("cdl ")) && (!readCdlChunk(localRIFFReader)))
            {
              ((List)localObject1).clear();
              break;
            }
            if (((RIFFReader)localObject2).getFormat().equals("art2")) {
              readArt2Chunk((List)localObject1, (RIFFReader)localObject2);
            }
          }
          localDLSInstrument.getModulators().addAll((Collection)localObject1);
        }
      }
      else
      {
        if (str.equals("dlid"))
        {
          guid = new byte[16];
          localRIFFReader.readFully(guid);
        }
        if (str.equals("insh"))
        {
          localRIFFReader.readUnsignedInt();
          int i = localRIFFReader.read();
          i += ((localRIFFReader.read() & 0x7F) << 7);
          localRIFFReader.read();
          int j = localRIFFReader.read();
          int k = localRIFFReader.read() & 0x7F;
          localRIFFReader.read();
          localRIFFReader.read();
          localRIFFReader.read();
          bank = i;
          preset = k;
          druminstrument = ((j & 0x80) > 0);
        }
      }
    }
    instruments.add(localDLSInstrument);
  }
  
  private void readArt1Chunk(List<DLSModulator> paramList, RIFFReader paramRIFFReader)
    throws IOException
  {
    long l1 = paramRIFFReader.readUnsignedInt();
    long l2 = paramRIFFReader.readUnsignedInt();
    if (l1 - 8L != 0L) {
      paramRIFFReader.skipBytes(l1 - 8L);
    }
    for (int i = 0; i < l2; i++)
    {
      DLSModulator localDLSModulator = new DLSModulator();
      version = 1;
      source = paramRIFFReader.readUnsignedShort();
      control = paramRIFFReader.readUnsignedShort();
      destination = paramRIFFReader.readUnsignedShort();
      transform = paramRIFFReader.readUnsignedShort();
      scale = paramRIFFReader.readInt();
      paramList.add(localDLSModulator);
    }
  }
  
  private void readArt2Chunk(List<DLSModulator> paramList, RIFFReader paramRIFFReader)
    throws IOException
  {
    long l1 = paramRIFFReader.readUnsignedInt();
    long l2 = paramRIFFReader.readUnsignedInt();
    if (l1 - 8L != 0L) {
      paramRIFFReader.skipBytes(l1 - 8L);
    }
    for (int i = 0; i < l2; i++)
    {
      DLSModulator localDLSModulator = new DLSModulator();
      version = 2;
      source = paramRIFFReader.readUnsignedShort();
      control = paramRIFFReader.readUnsignedShort();
      destination = paramRIFFReader.readUnsignedShort();
      transform = paramRIFFReader.readUnsignedShort();
      scale = paramRIFFReader.readInt();
      paramList.add(localDLSModulator);
    }
  }
  
  private boolean readRgnChunk(DLSRegion paramDLSRegion, RIFFReader paramRIFFReader)
    throws IOException
  {
    while (paramRIFFReader.hasNextChunk())
    {
      RIFFReader localRIFFReader1 = paramRIFFReader.nextChunk();
      String str = localRIFFReader1.getFormat();
      if (str.equals("LIST"))
      {
        ArrayList localArrayList;
        RIFFReader localRIFFReader2;
        if (localRIFFReader1.getType().equals("lart"))
        {
          localArrayList = new ArrayList();
          while (localRIFFReader1.hasNextChunk())
          {
            localRIFFReader2 = localRIFFReader1.nextChunk();
            if ((localRIFFReader1.getFormat().equals("cdl ")) && (!readCdlChunk(localRIFFReader1)))
            {
              localArrayList.clear();
              break;
            }
            if (localRIFFReader2.getFormat().equals("art1")) {
              readArt1Chunk(localArrayList, localRIFFReader2);
            }
          }
          paramDLSRegion.getModulators().addAll(localArrayList);
        }
        if (localRIFFReader1.getType().equals("lar2"))
        {
          localArrayList = new ArrayList();
          while (localRIFFReader1.hasNextChunk())
          {
            localRIFFReader2 = localRIFFReader1.nextChunk();
            if ((localRIFFReader1.getFormat().equals("cdl ")) && (!readCdlChunk(localRIFFReader1)))
            {
              localArrayList.clear();
              break;
            }
            if (localRIFFReader2.getFormat().equals("art2")) {
              readArt2Chunk(localArrayList, localRIFFReader2);
            }
          }
          paramDLSRegion.getModulators().addAll(localArrayList);
        }
      }
      else
      {
        if ((str.equals("cdl ")) && (!readCdlChunk(localRIFFReader1))) {
          return false;
        }
        if (str.equals("rgnh"))
        {
          keyfrom = localRIFFReader1.readUnsignedShort();
          keyto = localRIFFReader1.readUnsignedShort();
          velfrom = localRIFFReader1.readUnsignedShort();
          velto = localRIFFReader1.readUnsignedShort();
          options = localRIFFReader1.readUnsignedShort();
          exclusiveClass = localRIFFReader1.readUnsignedShort();
        }
        if (str.equals("wlnk"))
        {
          fusoptions = localRIFFReader1.readUnsignedShort();
          phasegroup = localRIFFReader1.readUnsignedShort();
          channel = localRIFFReader1.readUnsignedInt();
          long l = localRIFFReader1.readUnsignedInt();
          temp_rgnassign.put(paramDLSRegion, Long.valueOf(l));
        }
        if (str.equals("wsmp"))
        {
          sampleoptions = new DLSSampleOptions();
          readWsmpChunk(sampleoptions, localRIFFReader1);
        }
      }
    }
    return true;
  }
  
  private void readWsmpChunk(DLSSampleOptions paramDLSSampleOptions, RIFFReader paramRIFFReader)
    throws IOException
  {
    long l1 = paramRIFFReader.readUnsignedInt();
    unitynote = paramRIFFReader.readUnsignedShort();
    finetune = paramRIFFReader.readShort();
    attenuation = paramRIFFReader.readInt();
    options = paramRIFFReader.readUnsignedInt();
    long l2 = paramRIFFReader.readInt();
    if (l1 > 20L) {
      paramRIFFReader.skipBytes(l1 - 20L);
    }
    for (int i = 0; i < l2; i++)
    {
      DLSSampleLoop localDLSSampleLoop = new DLSSampleLoop();
      long l3 = paramRIFFReader.readUnsignedInt();
      type = paramRIFFReader.readUnsignedInt();
      start = paramRIFFReader.readUnsignedInt();
      length = paramRIFFReader.readUnsignedInt();
      loops.add(localDLSSampleLoop);
      if (l3 > 16L) {
        paramRIFFReader.skipBytes(l3 - 16L);
      }
    }
  }
  
  private void readInsInfoChunk(DLSInstrument paramDLSInstrument, RIFFReader paramRIFFReader)
    throws IOException
  {
    info.name = null;
    while (paramRIFFReader.hasNextChunk())
    {
      RIFFReader localRIFFReader = paramRIFFReader.nextChunk();
      String str = localRIFFReader.getFormat();
      if (str.equals("INAM")) {
        info.name = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ICRD")) {
        info.creationDate = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("IENG")) {
        info.engineers = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("IPRD")) {
        info.product = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ICOP")) {
        info.copyright = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ICMT")) {
        info.comments = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ISFT")) {
        info.tools = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("IARL")) {
        info.archival_location = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("IART")) {
        info.artist = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ICMS")) {
        info.commissioned = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("IGNR")) {
        info.genre = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("IKEY")) {
        info.keywords = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("IMED")) {
        info.medium = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ISBJ")) {
        info.subject = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ISRC")) {
        info.source = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ISRF")) {
        info.source_form = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ITCH")) {
        info.technician = localRIFFReader.readString(localRIFFReader.available());
      }
    }
  }
  
  private void readWvplChunk(RIFFReader paramRIFFReader)
    throws IOException
  {
    while (paramRIFFReader.hasNextChunk())
    {
      RIFFReader localRIFFReader = paramRIFFReader.nextChunk();
      if ((localRIFFReader.getFormat().equals("LIST")) && (localRIFFReader.getType().equals("wave"))) {
        readWaveChunk(localRIFFReader);
      }
    }
  }
  
  private void readWaveChunk(RIFFReader paramRIFFReader)
    throws IOException
  {
    DLSSample localDLSSample = new DLSSample(this);
    while (paramRIFFReader.hasNextChunk())
    {
      RIFFReader localRIFFReader = paramRIFFReader.nextChunk();
      String str = localRIFFReader.getFormat();
      if (str.equals("LIST"))
      {
        if (localRIFFReader.getType().equals("INFO")) {
          readWaveInfoChunk(localDLSSample, localRIFFReader);
        }
      }
      else
      {
        if (str.equals("dlid"))
        {
          guid = new byte[16];
          localRIFFReader.readFully(guid);
        }
        int j;
        if (str.equals("fmt "))
        {
          int i = localRIFFReader.readUnsignedShort();
          if ((i != 1) && (i != 3)) {
            throw new RIFFInvalidDataException("Only PCM samples are supported!");
          }
          j = localRIFFReader.readUnsignedShort();
          long l = localRIFFReader.readUnsignedInt();
          localRIFFReader.readUnsignedInt();
          int m = localRIFFReader.readUnsignedShort();
          int n = localRIFFReader.readUnsignedShort();
          AudioFormat localAudioFormat = null;
          if (i == 1) {
            if (n == 8) {
              localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, (float)l, n, j, m, (float)l, false);
            } else {
              localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, (float)l, n, j, m, (float)l, false);
            }
          }
          if (i == 3) {
            localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, (float)l, n, j, m, (float)l, false);
          }
          format = localAudioFormat;
        }
        if (str.equals("data")) {
          if (largeFormat)
          {
            localDLSSample.setData(new ModelByteBuffer(sampleFile, localRIFFReader.getFilePointer(), localRIFFReader.available()));
          }
          else
          {
            byte[] arrayOfByte = new byte[localRIFFReader.available()];
            localDLSSample.setData(arrayOfByte);
            j = 0;
            int k = localRIFFReader.available();
            while (j != k) {
              if (k - j > 65536)
              {
                localRIFFReader.readFully(arrayOfByte, j, 65536);
                j += 65536;
              }
              else
              {
                localRIFFReader.readFully(arrayOfByte, j, k - j);
                j = k;
              }
            }
          }
        }
        if (str.equals("wsmp"))
        {
          sampleoptions = new DLSSampleOptions();
          readWsmpChunk(sampleoptions, localRIFFReader);
        }
      }
    }
    samples.add(localDLSSample);
  }
  
  private void readWaveInfoChunk(DLSSample paramDLSSample, RIFFReader paramRIFFReader)
    throws IOException
  {
    info.name = null;
    while (paramRIFFReader.hasNextChunk())
    {
      RIFFReader localRIFFReader = paramRIFFReader.nextChunk();
      String str = localRIFFReader.getFormat();
      if (str.equals("INAM")) {
        info.name = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ICRD")) {
        info.creationDate = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("IENG")) {
        info.engineers = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("IPRD")) {
        info.product = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ICOP")) {
        info.copyright = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ICMT")) {
        info.comments = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ISFT")) {
        info.tools = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("IARL")) {
        info.archival_location = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("IART")) {
        info.artist = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ICMS")) {
        info.commissioned = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("IGNR")) {
        info.genre = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("IKEY")) {
        info.keywords = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("IMED")) {
        info.medium = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ISBJ")) {
        info.subject = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ISRC")) {
        info.source = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ISRF")) {
        info.source_form = localRIFFReader.readString(localRIFFReader.available());
      } else if (str.equals("ITCH")) {
        info.technician = localRIFFReader.readString(localRIFFReader.available());
      }
    }
  }
  
  public void save(String paramString)
    throws IOException
  {
    writeSoundbank(new RIFFWriter(paramString, "DLS "));
  }
  
  public void save(File paramFile)
    throws IOException
  {
    writeSoundbank(new RIFFWriter(paramFile, "DLS "));
  }
  
  public void save(OutputStream paramOutputStream)
    throws IOException
  {
    writeSoundbank(new RIFFWriter(paramOutputStream, "DLS "));
  }
  
  private void writeSoundbank(RIFFWriter paramRIFFWriter)
    throws IOException
  {
    RIFFWriter localRIFFWriter1 = paramRIFFWriter.writeChunk("colh");
    localRIFFWriter1.writeUnsignedInt(instruments.size());
    if ((major != -1L) && (minor != -1L))
    {
      localRIFFWriter2 = paramRIFFWriter.writeChunk("vers");
      localRIFFWriter2.writeUnsignedInt(major);
      localRIFFWriter2.writeUnsignedInt(minor);
    }
    writeInstruments(paramRIFFWriter.writeList("lins"));
    RIFFWriter localRIFFWriter2 = paramRIFFWriter.writeChunk("ptbl");
    localRIFFWriter2.writeUnsignedInt(8L);
    localRIFFWriter2.writeUnsignedInt(samples.size());
    long l1 = paramRIFFWriter.getFilePointer();
    for (int i = 0; i < samples.size(); i++) {
      localRIFFWriter2.writeUnsignedInt(0L);
    }
    RIFFWriter localRIFFWriter3 = paramRIFFWriter.writeList("wvpl");
    long l2 = localRIFFWriter3.getFilePointer();
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator1 = samples.iterator();
    while (localIterator1.hasNext())
    {
      DLSSample localDLSSample = (DLSSample)localIterator1.next();
      localArrayList.add(Long.valueOf(localRIFFWriter3.getFilePointer() - l2));
      writeSample(localRIFFWriter3.writeList("wave"), localDLSSample);
    }
    long l3 = paramRIFFWriter.getFilePointer();
    paramRIFFWriter.seek(l1);
    paramRIFFWriter.setWriteOverride(true);
    Iterator localIterator2 = localArrayList.iterator();
    while (localIterator2.hasNext())
    {
      Long localLong = (Long)localIterator2.next();
      paramRIFFWriter.writeUnsignedInt(localLong.longValue());
    }
    paramRIFFWriter.setWriteOverride(false);
    paramRIFFWriter.seek(l3);
    writeInfo(paramRIFFWriter.writeList("INFO"), info);
    paramRIFFWriter.close();
  }
  
  private void writeSample(RIFFWriter paramRIFFWriter, DLSSample paramDLSSample)
    throws IOException
  {
    AudioFormat localAudioFormat = paramDLSSample.getFormat();
    AudioFormat.Encoding localEncoding = localAudioFormat.getEncoding();
    float f1 = localAudioFormat.getSampleRate();
    int i = localAudioFormat.getSampleSizeInBits();
    int j = localAudioFormat.getChannels();
    int k = localAudioFormat.getFrameSize();
    float f2 = localAudioFormat.getFrameRate();
    boolean bool = localAudioFormat.isBigEndian();
    int m = 0;
    if (localAudioFormat.getSampleSizeInBits() == 8)
    {
      if (!localEncoding.equals(AudioFormat.Encoding.PCM_UNSIGNED))
      {
        localEncoding = AudioFormat.Encoding.PCM_UNSIGNED;
        m = 1;
      }
    }
    else
    {
      if (!localEncoding.equals(AudioFormat.Encoding.PCM_SIGNED))
      {
        localEncoding = AudioFormat.Encoding.PCM_SIGNED;
        m = 1;
      }
      if (bool)
      {
        bool = false;
        m = 1;
      }
    }
    if (m != 0) {
      localAudioFormat = new AudioFormat(localEncoding, f1, i, j, k, f2, bool);
    }
    RIFFWriter localRIFFWriter1 = paramRIFFWriter.writeChunk("fmt ");
    int n = 0;
    if (localAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
      n = 1;
    } else if (localAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)) {
      n = 1;
    } else if (localAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_FLOAT)) {
      n = 3;
    }
    localRIFFWriter1.writeUnsignedShort(n);
    localRIFFWriter1.writeUnsignedShort(localAudioFormat.getChannels());
    localRIFFWriter1.writeUnsignedInt(localAudioFormat.getSampleRate());
    long l = localAudioFormat.getFrameRate() * localAudioFormat.getFrameSize();
    localRIFFWriter1.writeUnsignedInt(l);
    localRIFFWriter1.writeUnsignedShort(localAudioFormat.getFrameSize());
    localRIFFWriter1.writeUnsignedShort(localAudioFormat.getSampleSizeInBits());
    localRIFFWriter1.write(0);
    localRIFFWriter1.write(0);
    writeSampleOptions(paramRIFFWriter.writeChunk("wsmp"), sampleoptions);
    RIFFWriter localRIFFWriter2;
    Object localObject;
    if (m != 0)
    {
      localRIFFWriter2 = paramRIFFWriter.writeChunk("data");
      localObject = AudioSystem.getAudioInputStream(localAudioFormat, (AudioInputStream)paramDLSSample.getData());
      byte[] arrayOfByte = new byte['Ѐ'];
      int i1;
      while ((i1 = ((AudioInputStream)localObject).read(arrayOfByte)) != -1) {
        localRIFFWriter2.write(arrayOfByte, 0, i1);
      }
    }
    else
    {
      localRIFFWriter2 = paramRIFFWriter.writeChunk("data");
      localObject = paramDLSSample.getDataBuffer();
      ((ModelByteBuffer)localObject).writeTo(localRIFFWriter2);
    }
    writeInfo(paramRIFFWriter.writeList("INFO"), info);
  }
  
  private void writeInstruments(RIFFWriter paramRIFFWriter)
    throws IOException
  {
    Iterator localIterator = instruments.iterator();
    while (localIterator.hasNext())
    {
      DLSInstrument localDLSInstrument = (DLSInstrument)localIterator.next();
      writeInstrument(paramRIFFWriter.writeList("ins "), localDLSInstrument);
    }
  }
  
  private void writeInstrument(RIFFWriter paramRIFFWriter, DLSInstrument paramDLSInstrument)
    throws IOException
  {
    int i = 0;
    int j = 0;
    Iterator localIterator = paramDLSInstrument.getModulators().iterator();
    while (localIterator.hasNext())
    {
      localObject1 = (DLSModulator)localIterator.next();
      if (version == 1) {
        i++;
      }
      if (version == 2) {
        j++;
      }
    }
    localIterator = regions.iterator();
    while (localIterator.hasNext())
    {
      localObject1 = (DLSRegion)localIterator.next();
      localObject2 = ((DLSRegion)localObject1).getModulators().iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (DLSModulator)((Iterator)localObject2).next();
        if (version == 1) {
          i++;
        }
        if (version == 2) {
          j++;
        }
      }
    }
    int k = 1;
    if (j > 0) {
      k = 2;
    }
    Object localObject1 = paramRIFFWriter.writeChunk("insh");
    ((RIFFWriter)localObject1).writeUnsignedInt(paramDLSInstrument.getRegions().size());
    ((RIFFWriter)localObject1).writeUnsignedInt(bank + (druminstrument ? 2147483648L : 0L));
    ((RIFFWriter)localObject1).writeUnsignedInt(preset);
    Object localObject2 = paramRIFFWriter.writeList("lrgn");
    Object localObject3 = regions.iterator();
    while (((Iterator)localObject3).hasNext())
    {
      DLSRegion localDLSRegion = (DLSRegion)((Iterator)localObject3).next();
      writeRegion((RIFFWriter)localObject2, localDLSRegion, k);
    }
    writeArticulators(paramRIFFWriter, paramDLSInstrument.getModulators());
    writeInfo(paramRIFFWriter.writeList("INFO"), info);
  }
  
  private void writeArticulators(RIFFWriter paramRIFFWriter, List<DLSModulator> paramList)
    throws IOException
  {
    int i = 0;
    int j = 0;
    Object localObject1 = paramList.iterator();
    Object localObject2;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (DLSModulator)((Iterator)localObject1).next();
      if (version == 1) {
        i++;
      }
      if (version == 2) {
        j++;
      }
    }
    Iterator localIterator;
    DLSModulator localDLSModulator;
    if (i > 0)
    {
      localObject1 = paramRIFFWriter.writeList("lart");
      localObject2 = ((RIFFWriter)localObject1).writeChunk("art1");
      ((RIFFWriter)localObject2).writeUnsignedInt(8L);
      ((RIFFWriter)localObject2).writeUnsignedInt(i);
      localIterator = paramList.iterator();
      while (localIterator.hasNext())
      {
        localDLSModulator = (DLSModulator)localIterator.next();
        if (version == 1)
        {
          ((RIFFWriter)localObject2).writeUnsignedShort(source);
          ((RIFFWriter)localObject2).writeUnsignedShort(control);
          ((RIFFWriter)localObject2).writeUnsignedShort(destination);
          ((RIFFWriter)localObject2).writeUnsignedShort(transform);
          ((RIFFWriter)localObject2).writeInt(scale);
        }
      }
    }
    if (j > 0)
    {
      localObject1 = paramRIFFWriter.writeList("lar2");
      localObject2 = ((RIFFWriter)localObject1).writeChunk("art2");
      ((RIFFWriter)localObject2).writeUnsignedInt(8L);
      ((RIFFWriter)localObject2).writeUnsignedInt(j);
      localIterator = paramList.iterator();
      while (localIterator.hasNext())
      {
        localDLSModulator = (DLSModulator)localIterator.next();
        if (version == 2)
        {
          ((RIFFWriter)localObject2).writeUnsignedShort(source);
          ((RIFFWriter)localObject2).writeUnsignedShort(control);
          ((RIFFWriter)localObject2).writeUnsignedShort(destination);
          ((RIFFWriter)localObject2).writeUnsignedShort(transform);
          ((RIFFWriter)localObject2).writeInt(scale);
        }
      }
    }
  }
  
  private void writeRegion(RIFFWriter paramRIFFWriter, DLSRegion paramDLSRegion, int paramInt)
    throws IOException
  {
    RIFFWriter localRIFFWriter1 = null;
    if (paramInt == 1) {
      localRIFFWriter1 = paramRIFFWriter.writeList("rgn ");
    }
    if (paramInt == 2) {
      localRIFFWriter1 = paramRIFFWriter.writeList("rgn2");
    }
    if (localRIFFWriter1 == null) {
      return;
    }
    RIFFWriter localRIFFWriter2 = localRIFFWriter1.writeChunk("rgnh");
    localRIFFWriter2.writeUnsignedShort(keyfrom);
    localRIFFWriter2.writeUnsignedShort(keyto);
    localRIFFWriter2.writeUnsignedShort(velfrom);
    localRIFFWriter2.writeUnsignedShort(velto);
    localRIFFWriter2.writeUnsignedShort(options);
    localRIFFWriter2.writeUnsignedShort(exclusiveClass);
    if (sampleoptions != null) {
      writeSampleOptions(localRIFFWriter1.writeChunk("wsmp"), sampleoptions);
    }
    if ((sample != null) && (samples.indexOf(sample) != -1))
    {
      RIFFWriter localRIFFWriter3 = localRIFFWriter1.writeChunk("wlnk");
      localRIFFWriter3.writeUnsignedShort(fusoptions);
      localRIFFWriter3.writeUnsignedShort(phasegroup);
      localRIFFWriter3.writeUnsignedInt(channel);
      localRIFFWriter3.writeUnsignedInt(samples.indexOf(sample));
    }
    writeArticulators(localRIFFWriter1, paramDLSRegion.getModulators());
    localRIFFWriter1.close();
  }
  
  private void writeSampleOptions(RIFFWriter paramRIFFWriter, DLSSampleOptions paramDLSSampleOptions)
    throws IOException
  {
    paramRIFFWriter.writeUnsignedInt(20L);
    paramRIFFWriter.writeUnsignedShort(unitynote);
    paramRIFFWriter.writeShort(finetune);
    paramRIFFWriter.writeInt(attenuation);
    paramRIFFWriter.writeUnsignedInt(options);
    paramRIFFWriter.writeInt(loops.size());
    Iterator localIterator = loops.iterator();
    while (localIterator.hasNext())
    {
      DLSSampleLoop localDLSSampleLoop = (DLSSampleLoop)localIterator.next();
      paramRIFFWriter.writeUnsignedInt(16L);
      paramRIFFWriter.writeUnsignedInt(type);
      paramRIFFWriter.writeUnsignedInt(start);
      paramRIFFWriter.writeUnsignedInt(length);
    }
  }
  
  private void writeInfoStringChunk(RIFFWriter paramRIFFWriter, String paramString1, String paramString2)
    throws IOException
  {
    if (paramString2 == null) {
      return;
    }
    RIFFWriter localRIFFWriter = paramRIFFWriter.writeChunk(paramString1);
    localRIFFWriter.writeString(paramString2);
    int i = paramString2.getBytes("ascii").length;
    localRIFFWriter.write(0);
    i++;
    if (i % 2 != 0) {
      localRIFFWriter.write(0);
    }
  }
  
  private void writeInfo(RIFFWriter paramRIFFWriter, DLSInfo paramDLSInfo)
    throws IOException
  {
    writeInfoStringChunk(paramRIFFWriter, "INAM", name);
    writeInfoStringChunk(paramRIFFWriter, "ICRD", creationDate);
    writeInfoStringChunk(paramRIFFWriter, "IENG", engineers);
    writeInfoStringChunk(paramRIFFWriter, "IPRD", product);
    writeInfoStringChunk(paramRIFFWriter, "ICOP", copyright);
    writeInfoStringChunk(paramRIFFWriter, "ICMT", comments);
    writeInfoStringChunk(paramRIFFWriter, "ISFT", tools);
    writeInfoStringChunk(paramRIFFWriter, "IARL", archival_location);
    writeInfoStringChunk(paramRIFFWriter, "IART", artist);
    writeInfoStringChunk(paramRIFFWriter, "ICMS", commissioned);
    writeInfoStringChunk(paramRIFFWriter, "IGNR", genre);
    writeInfoStringChunk(paramRIFFWriter, "IKEY", keywords);
    writeInfoStringChunk(paramRIFFWriter, "IMED", medium);
    writeInfoStringChunk(paramRIFFWriter, "ISBJ", subject);
    writeInfoStringChunk(paramRIFFWriter, "ISRC", source);
    writeInfoStringChunk(paramRIFFWriter, "ISRF", source_form);
    writeInfoStringChunk(paramRIFFWriter, "ITCH", technician);
  }
  
  public DLSInfo getInfo()
  {
    return info;
  }
  
  public String getName()
  {
    return info.name;
  }
  
  public String getVersion()
  {
    return major + "." + minor;
  }
  
  public String getVendor()
  {
    return info.engineers;
  }
  
  public String getDescription()
  {
    return info.comments;
  }
  
  public void setName(String paramString)
  {
    info.name = paramString;
  }
  
  public void setVendor(String paramString)
  {
    info.engineers = paramString;
  }
  
  public void setDescription(String paramString)
  {
    info.comments = paramString;
  }
  
  public SoundbankResource[] getResources()
  {
    SoundbankResource[] arrayOfSoundbankResource = new SoundbankResource[samples.size()];
    int i = 0;
    for (int j = 0; j < samples.size(); j++) {
      arrayOfSoundbankResource[(i++)] = ((SoundbankResource)samples.get(j));
    }
    return arrayOfSoundbankResource;
  }
  
  public DLSInstrument[] getInstruments()
  {
    DLSInstrument[] arrayOfDLSInstrument = (DLSInstrument[])instruments.toArray(new DLSInstrument[instruments.size()]);
    Arrays.sort(arrayOfDLSInstrument, new ModelInstrumentComparator());
    return arrayOfDLSInstrument;
  }
  
  public DLSSample[] getSamples()
  {
    return (DLSSample[])samples.toArray(new DLSSample[samples.size()]);
  }
  
  public Instrument getInstrument(Patch paramPatch)
  {
    int i = paramPatch.getProgram();
    int j = paramPatch.getBank();
    boolean bool1 = false;
    if ((paramPatch instanceof ModelPatch)) {
      bool1 = ((ModelPatch)paramPatch).isPercussion();
    }
    Iterator localIterator = instruments.iterator();
    while (localIterator.hasNext())
    {
      Instrument localInstrument = (Instrument)localIterator.next();
      Patch localPatch = localInstrument.getPatch();
      int k = localPatch.getProgram();
      int m = localPatch.getBank();
      if ((i == k) && (j == m))
      {
        boolean bool2 = false;
        if ((localPatch instanceof ModelPatch)) {
          bool2 = ((ModelPatch)localPatch).isPercussion();
        }
        if (bool1 == bool2) {
          return localInstrument;
        }
      }
    }
    return null;
  }
  
  public void addResource(SoundbankResource paramSoundbankResource)
  {
    if ((paramSoundbankResource instanceof DLSInstrument)) {
      instruments.add((DLSInstrument)paramSoundbankResource);
    }
    if ((paramSoundbankResource instanceof DLSSample)) {
      samples.add((DLSSample)paramSoundbankResource);
    }
  }
  
  public void removeResource(SoundbankResource paramSoundbankResource)
  {
    if ((paramSoundbankResource instanceof DLSInstrument)) {
      instruments.remove((DLSInstrument)paramSoundbankResource);
    }
    if ((paramSoundbankResource instanceof DLSSample)) {
      samples.remove((DLSSample)paramSoundbankResource);
    }
  }
  
  public void addInstrument(DLSInstrument paramDLSInstrument)
  {
    instruments.add(paramDLSInstrument);
  }
  
  public void removeInstrument(DLSInstrument paramDLSInstrument)
  {
    instruments.remove(paramDLSInstrument);
  }
  
  public long getMajor()
  {
    return major;
  }
  
  public void setMajor(long paramLong)
  {
    major = paramLong;
  }
  
  public long getMinor()
  {
    return minor;
  }
  
  public void setMinor(long paramLong)
  {
    minor = paramLong;
  }
  
  private static class DLSID
  {
    long i1;
    int s1;
    int s2;
    int x1;
    int x2;
    int x3;
    int x4;
    int x5;
    int x6;
    int x7;
    int x8;
    
    private DLSID() {}
    
    DLSID(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10)
    {
      i1 = paramLong;
      s1 = paramInt1;
      s2 = paramInt2;
      x1 = paramInt3;
      x2 = paramInt4;
      x3 = paramInt5;
      x4 = paramInt6;
      x5 = paramInt7;
      x6 = paramInt8;
      x7 = paramInt9;
      x8 = paramInt10;
    }
    
    public static DLSID read(RIFFReader paramRIFFReader)
      throws IOException
    {
      DLSID localDLSID = new DLSID();
      i1 = paramRIFFReader.readUnsignedInt();
      s1 = paramRIFFReader.readUnsignedShort();
      s2 = paramRIFFReader.readUnsignedShort();
      x1 = paramRIFFReader.readUnsignedByte();
      x2 = paramRIFFReader.readUnsignedByte();
      x3 = paramRIFFReader.readUnsignedByte();
      x4 = paramRIFFReader.readUnsignedByte();
      x5 = paramRIFFReader.readUnsignedByte();
      x6 = paramRIFFReader.readUnsignedByte();
      x7 = paramRIFFReader.readUnsignedByte();
      x8 = paramRIFFReader.readUnsignedByte();
      return localDLSID;
    }
    
    public int hashCode()
    {
      return (int)i1;
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof DLSID)) {
        return false;
      }
      DLSID localDLSID = (DLSID)paramObject;
      return (i1 == i1) && (s1 == s1) && (s2 == s2) && (x1 == x1) && (x2 == x2) && (x3 == x3) && (x4 == x4) && (x5 == x5) && (x6 == x6) && (x7 == x7) && (x8 == x8);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\DLSSoundbank.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */