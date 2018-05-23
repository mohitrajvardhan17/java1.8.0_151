package com.sun.media.sound;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.sound.midi.Instrument;
import javax.sound.midi.Patch;
import javax.sound.midi.Soundbank;
import javax.sound.midi.SoundbankResource;

public final class SF2Soundbank
  implements Soundbank
{
  int major = 2;
  int minor = 1;
  String targetEngine = "EMU8000";
  String name = "untitled";
  String romName = null;
  int romVersionMajor = -1;
  int romVersionMinor = -1;
  String creationDate = null;
  String engineers = null;
  String product = null;
  String copyright = null;
  String comments = null;
  String tools = null;
  private ModelByteBuffer sampleData = null;
  private ModelByteBuffer sampleData24 = null;
  private File sampleFile = null;
  private boolean largeFormat = false;
  private final List<SF2Instrument> instruments = new ArrayList();
  private final List<SF2Layer> layers = new ArrayList();
  private final List<SF2Sample> samples = new ArrayList();
  
  public SF2Soundbank() {}
  
  /* Error */
  public SF2Soundbank(java.net.URL paramURL)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 665	java/lang/Object:<init>	()V
    //   4: aload_0
    //   5: iconst_2
    //   6: putfield 570	com/sun/media/sound/SF2Soundbank:major	I
    //   9: aload_0
    //   10: iconst_1
    //   11: putfield 571	com/sun/media/sound/SF2Soundbank:minor	I
    //   14: aload_0
    //   15: ldc 4
    //   17: putfield 585	com/sun/media/sound/SF2Soundbank:targetEngine	Ljava/lang/String;
    //   20: aload_0
    //   21: ldc 39
    //   23: putfield 582	com/sun/media/sound/SF2Soundbank:name	Ljava/lang/String;
    //   26: aload_0
    //   27: aconst_null
    //   28: putfield 584	com/sun/media/sound/SF2Soundbank:romName	Ljava/lang/String;
    //   31: aload_0
    //   32: iconst_m1
    //   33: putfield 572	com/sun/media/sound/SF2Soundbank:romVersionMajor	I
    //   36: aload_0
    //   37: iconst_m1
    //   38: putfield 573	com/sun/media/sound/SF2Soundbank:romVersionMinor	I
    //   41: aload_0
    //   42: aconst_null
    //   43: putfield 580	com/sun/media/sound/SF2Soundbank:creationDate	Ljava/lang/String;
    //   46: aload_0
    //   47: aconst_null
    //   48: putfield 581	com/sun/media/sound/SF2Soundbank:engineers	Ljava/lang/String;
    //   51: aload_0
    //   52: aconst_null
    //   53: putfield 583	com/sun/media/sound/SF2Soundbank:product	Ljava/lang/String;
    //   56: aload_0
    //   57: aconst_null
    //   58: putfield 579	com/sun/media/sound/SF2Soundbank:copyright	Ljava/lang/String;
    //   61: aload_0
    //   62: aconst_null
    //   63: putfield 578	com/sun/media/sound/SF2Soundbank:comments	Ljava/lang/String;
    //   66: aload_0
    //   67: aconst_null
    //   68: putfield 586	com/sun/media/sound/SF2Soundbank:tools	Ljava/lang/String;
    //   71: aload_0
    //   72: aconst_null
    //   73: putfield 575	com/sun/media/sound/SF2Soundbank:sampleData	Lcom/sun/media/sound/ModelByteBuffer;
    //   76: aload_0
    //   77: aconst_null
    //   78: putfield 576	com/sun/media/sound/SF2Soundbank:sampleData24	Lcom/sun/media/sound/ModelByteBuffer;
    //   81: aload_0
    //   82: aconst_null
    //   83: putfield 577	com/sun/media/sound/SF2Soundbank:sampleFile	Ljava/io/File;
    //   86: aload_0
    //   87: iconst_0
    //   88: putfield 574	com/sun/media/sound/SF2Soundbank:largeFormat	Z
    //   91: aload_0
    //   92: new 336	java/util/ArrayList
    //   95: dup
    //   96: invokespecial 675	java/util/ArrayList:<init>	()V
    //   99: putfield 587	com/sun/media/sound/SF2Soundbank:instruments	Ljava/util/List;
    //   102: aload_0
    //   103: new 336	java/util/ArrayList
    //   106: dup
    //   107: invokespecial 675	java/util/ArrayList:<init>	()V
    //   110: putfield 588	com/sun/media/sound/SF2Soundbank:layers	Ljava/util/List;
    //   113: aload_0
    //   114: new 336	java/util/ArrayList
    //   117: dup
    //   118: invokespecial 675	java/util/ArrayList:<init>	()V
    //   121: putfield 589	com/sun/media/sound/SF2Soundbank:samples	Ljava/util/List;
    //   124: aload_1
    //   125: invokevirtual 674	java/net/URL:openStream	()Ljava/io/InputStream;
    //   128: astore_2
    //   129: aload_0
    //   130: aload_2
    //   131: invokespecial 657	com/sun/media/sound/SF2Soundbank:readSoundbank	(Ljava/io/InputStream;)V
    //   134: aload_2
    //   135: invokevirtual 662	java/io/InputStream:close	()V
    //   138: goto +10 -> 148
    //   141: astore_3
    //   142: aload_2
    //   143: invokevirtual 662	java/io/InputStream:close	()V
    //   146: aload_3
    //   147: athrow
    //   148: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	149	0	this	SF2Soundbank
    //   0	149	1	paramURL	java.net.URL
    //   128	15	2	localInputStream	InputStream
    //   141	6	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   129	134	141	finally
  }
  
  /* Error */
  public SF2Soundbank(File paramFile)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 665	java/lang/Object:<init>	()V
    //   4: aload_0
    //   5: iconst_2
    //   6: putfield 570	com/sun/media/sound/SF2Soundbank:major	I
    //   9: aload_0
    //   10: iconst_1
    //   11: putfield 571	com/sun/media/sound/SF2Soundbank:minor	I
    //   14: aload_0
    //   15: ldc 4
    //   17: putfield 585	com/sun/media/sound/SF2Soundbank:targetEngine	Ljava/lang/String;
    //   20: aload_0
    //   21: ldc 39
    //   23: putfield 582	com/sun/media/sound/SF2Soundbank:name	Ljava/lang/String;
    //   26: aload_0
    //   27: aconst_null
    //   28: putfield 584	com/sun/media/sound/SF2Soundbank:romName	Ljava/lang/String;
    //   31: aload_0
    //   32: iconst_m1
    //   33: putfield 572	com/sun/media/sound/SF2Soundbank:romVersionMajor	I
    //   36: aload_0
    //   37: iconst_m1
    //   38: putfield 573	com/sun/media/sound/SF2Soundbank:romVersionMinor	I
    //   41: aload_0
    //   42: aconst_null
    //   43: putfield 580	com/sun/media/sound/SF2Soundbank:creationDate	Ljava/lang/String;
    //   46: aload_0
    //   47: aconst_null
    //   48: putfield 581	com/sun/media/sound/SF2Soundbank:engineers	Ljava/lang/String;
    //   51: aload_0
    //   52: aconst_null
    //   53: putfield 583	com/sun/media/sound/SF2Soundbank:product	Ljava/lang/String;
    //   56: aload_0
    //   57: aconst_null
    //   58: putfield 579	com/sun/media/sound/SF2Soundbank:copyright	Ljava/lang/String;
    //   61: aload_0
    //   62: aconst_null
    //   63: putfield 578	com/sun/media/sound/SF2Soundbank:comments	Ljava/lang/String;
    //   66: aload_0
    //   67: aconst_null
    //   68: putfield 586	com/sun/media/sound/SF2Soundbank:tools	Ljava/lang/String;
    //   71: aload_0
    //   72: aconst_null
    //   73: putfield 575	com/sun/media/sound/SF2Soundbank:sampleData	Lcom/sun/media/sound/ModelByteBuffer;
    //   76: aload_0
    //   77: aconst_null
    //   78: putfield 576	com/sun/media/sound/SF2Soundbank:sampleData24	Lcom/sun/media/sound/ModelByteBuffer;
    //   81: aload_0
    //   82: aconst_null
    //   83: putfield 577	com/sun/media/sound/SF2Soundbank:sampleFile	Ljava/io/File;
    //   86: aload_0
    //   87: iconst_0
    //   88: putfield 574	com/sun/media/sound/SF2Soundbank:largeFormat	Z
    //   91: aload_0
    //   92: new 336	java/util/ArrayList
    //   95: dup
    //   96: invokespecial 675	java/util/ArrayList:<init>	()V
    //   99: putfield 587	com/sun/media/sound/SF2Soundbank:instruments	Ljava/util/List;
    //   102: aload_0
    //   103: new 336	java/util/ArrayList
    //   106: dup
    //   107: invokespecial 675	java/util/ArrayList:<init>	()V
    //   110: putfield 588	com/sun/media/sound/SF2Soundbank:layers	Ljava/util/List;
    //   113: aload_0
    //   114: new 336	java/util/ArrayList
    //   117: dup
    //   118: invokespecial 675	java/util/ArrayList:<init>	()V
    //   121: putfield 589	com/sun/media/sound/SF2Soundbank:samples	Ljava/util/List;
    //   124: aload_0
    //   125: iconst_1
    //   126: putfield 574	com/sun/media/sound/SF2Soundbank:largeFormat	Z
    //   129: aload_0
    //   130: aload_1
    //   131: putfield 577	com/sun/media/sound/SF2Soundbank:sampleFile	Ljava/io/File;
    //   134: new 326	java/io/FileInputStream
    //   137: dup
    //   138: aload_1
    //   139: invokespecial 661	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   142: astore_2
    //   143: aload_0
    //   144: aload_2
    //   145: invokespecial 657	com/sun/media/sound/SF2Soundbank:readSoundbank	(Ljava/io/InputStream;)V
    //   148: aload_2
    //   149: invokevirtual 662	java/io/InputStream:close	()V
    //   152: goto +10 -> 162
    //   155: astore_3
    //   156: aload_2
    //   157: invokevirtual 662	java/io/InputStream:close	()V
    //   160: aload_3
    //   161: athrow
    //   162: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	163	0	this	SF2Soundbank
    //   0	163	1	paramFile	File
    //   142	15	2	localFileInputStream	java.io.FileInputStream
    //   155	6	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   143	148	155	finally
  }
  
  public SF2Soundbank(InputStream paramInputStream)
    throws IOException
  {
    readSoundbank(paramInputStream);
  }
  
  private void readSoundbank(InputStream paramInputStream)
    throws IOException
  {
    RIFFReader localRIFFReader1 = new RIFFReader(paramInputStream);
    if (!localRIFFReader1.getFormat().equals("RIFF")) {
      throw new RIFFInvalidFormatException("Input stream is not a valid RIFF stream!");
    }
    if (!localRIFFReader1.getType().equals("sfbk")) {
      throw new RIFFInvalidFormatException("Input stream is not a valid SoundFont!");
    }
    while (localRIFFReader1.hasNextChunk())
    {
      RIFFReader localRIFFReader2 = localRIFFReader1.nextChunk();
      if (localRIFFReader2.getFormat().equals("LIST"))
      {
        if (localRIFFReader2.getType().equals("INFO")) {
          readInfoChunk(localRIFFReader2);
        }
        if (localRIFFReader2.getType().equals("sdta")) {
          readSdtaChunk(localRIFFReader2);
        }
        if (localRIFFReader2.getType().equals("pdta")) {
          readPdtaChunk(localRIFFReader2);
        }
      }
    }
  }
  
  private void readInfoChunk(RIFFReader paramRIFFReader)
    throws IOException
  {
    while (paramRIFFReader.hasNextChunk())
    {
      RIFFReader localRIFFReader = paramRIFFReader.nextChunk();
      String str = localRIFFReader.getFormat();
      if (str.equals("ifil"))
      {
        major = localRIFFReader.readUnsignedShort();
        minor = localRIFFReader.readUnsignedShort();
      }
      else if (str.equals("isng"))
      {
        targetEngine = localRIFFReader.readString(localRIFFReader.available());
      }
      else if (str.equals("INAM"))
      {
        name = localRIFFReader.readString(localRIFFReader.available());
      }
      else if (str.equals("irom"))
      {
        romName = localRIFFReader.readString(localRIFFReader.available());
      }
      else if (str.equals("iver"))
      {
        romVersionMajor = localRIFFReader.readUnsignedShort();
        romVersionMinor = localRIFFReader.readUnsignedShort();
      }
      else if (str.equals("ICRD"))
      {
        creationDate = localRIFFReader.readString(localRIFFReader.available());
      }
      else if (str.equals("IENG"))
      {
        engineers = localRIFFReader.readString(localRIFFReader.available());
      }
      else if (str.equals("IPRD"))
      {
        product = localRIFFReader.readString(localRIFFReader.available());
      }
      else if (str.equals("ICOP"))
      {
        copyright = localRIFFReader.readString(localRIFFReader.available());
      }
      else if (str.equals("ICMT"))
      {
        comments = localRIFFReader.readString(localRIFFReader.available());
      }
      else if (str.equals("ISFT"))
      {
        tools = localRIFFReader.readString(localRIFFReader.available());
      }
    }
  }
  
  private void readSdtaChunk(RIFFReader paramRIFFReader)
    throws IOException
  {
    while (paramRIFFReader.hasNextChunk())
    {
      RIFFReader localRIFFReader = paramRIFFReader.nextChunk();
      byte[] arrayOfByte;
      int i;
      int j;
      if (localRIFFReader.getFormat().equals("smpl")) {
        if (!largeFormat)
        {
          arrayOfByte = new byte[localRIFFReader.available()];
          i = 0;
          j = localRIFFReader.available();
          while (i != j) {
            if (j - i > 65536)
            {
              localRIFFReader.readFully(arrayOfByte, i, 65536);
              i += 65536;
            }
            else
            {
              localRIFFReader.readFully(arrayOfByte, i, j - i);
              i = j;
            }
          }
          sampleData = new ModelByteBuffer(arrayOfByte);
        }
        else
        {
          sampleData = new ModelByteBuffer(sampleFile, localRIFFReader.getFilePointer(), localRIFFReader.available());
        }
      }
      if (localRIFFReader.getFormat().equals("sm24")) {
        if (!largeFormat)
        {
          arrayOfByte = new byte[localRIFFReader.available()];
          i = 0;
          j = localRIFFReader.available();
          while (i != j) {
            if (j - i > 65536)
            {
              localRIFFReader.readFully(arrayOfByte, i, 65536);
              i += 65536;
            }
            else
            {
              localRIFFReader.readFully(arrayOfByte, i, j - i);
              i = j;
            }
          }
          sampleData24 = new ModelByteBuffer(arrayOfByte);
        }
        else
        {
          sampleData24 = new ModelByteBuffer(sampleFile, localRIFFReader.getFilePointer(), localRIFFReader.available());
        }
      }
    }
  }
  
  private void readPdtaChunk(RIFFReader paramRIFFReader)
    throws IOException
  {
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    ArrayList localArrayList3 = new ArrayList();
    ArrayList localArrayList4 = new ArrayList();
    ArrayList localArrayList5 = new ArrayList();
    ArrayList localArrayList6 = new ArrayList();
    ArrayList localArrayList7 = new ArrayList();
    ArrayList localArrayList8 = new ArrayList();
    Object localObject5;
    while (paramRIFFReader.hasNextChunk())
    {
      localObject1 = paramRIFFReader.nextChunk();
      localObject2 = ((RIFFReader)localObject1).getFormat();
      int i;
      int j;
      if (((String)localObject2).equals("phdr"))
      {
        if (((RIFFReader)localObject1).available() % 38 != 0) {
          throw new RIFFInvalidDataException();
        }
        i = ((RIFFReader)localObject1).available() / 38;
        for (j = 0; j < i; j++)
        {
          SF2Instrument localSF2Instrument1 = new SF2Instrument(this);
          name = ((RIFFReader)localObject1).readString(20);
          preset = ((RIFFReader)localObject1).readUnsignedShort();
          bank = ((RIFFReader)localObject1).readUnsignedShort();
          localArrayList2.add(Integer.valueOf(((RIFFReader)localObject1).readUnsignedShort()));
          library = ((RIFFReader)localObject1).readUnsignedInt();
          genre = ((RIFFReader)localObject1).readUnsignedInt();
          morphology = ((RIFFReader)localObject1).readUnsignedInt();
          localArrayList1.add(localSF2Instrument1);
          if (j != i - 1) {
            instruments.add(localSF2Instrument1);
          }
        }
      }
      else
      {
        int i8;
        int i9;
        int i10;
        Object localObject7;
        if (((String)localObject2).equals("pbag"))
        {
          if (((RIFFReader)localObject1).available() % 4 != 0) {
            throw new RIFFInvalidDataException();
          }
          i = ((RIFFReader)localObject1).available() / 4;
          j = ((RIFFReader)localObject1).readUnsignedShort();
          int n = ((RIFFReader)localObject1).readUnsignedShort();
          while (localArrayList3.size() < j) {
            localArrayList3.add(null);
          }
          while (localArrayList4.size() < n) {
            localArrayList4.add(null);
          }
          i--;
          if (localArrayList2.isEmpty()) {
            throw new RIFFInvalidDataException();
          }
          j = ((Integer)localArrayList2.get(0)).intValue();
          int i2;
          for (n = 0; n < j; n++)
          {
            if (i == 0) {
              throw new RIFFInvalidDataException();
            }
            i2 = ((RIFFReader)localObject1).readUnsignedShort();
            int i5 = ((RIFFReader)localObject1).readUnsignedShort();
            while (localArrayList3.size() < i2) {
              localArrayList3.add(null);
            }
            while (localArrayList4.size() < i5) {
              localArrayList4.add(null);
            }
            i--;
          }
          for (n = 0; n < localArrayList2.size() - 1; n++)
          {
            i2 = ((Integer)localArrayList2.get(n + 1)).intValue() - ((Integer)localArrayList2.get(n)).intValue();
            SF2Instrument localSF2Instrument2 = (SF2Instrument)localArrayList1.get(n);
            for (i8 = 0; i8 < i2; i8++)
            {
              if (i == 0) {
                throw new RIFFInvalidDataException();
              }
              i9 = ((RIFFReader)localObject1).readUnsignedShort();
              i10 = ((RIFFReader)localObject1).readUnsignedShort();
              localObject7 = new SF2InstrumentRegion();
              regions.add(localObject7);
              while (localArrayList3.size() < i9) {
                localArrayList3.add(localObject7);
              }
              while (localArrayList4.size() < i10) {
                localArrayList4.add(localObject7);
              }
              i--;
            }
          }
        }
        else if (((String)localObject2).equals("pmod"))
        {
          for (i = 0; i < localArrayList4.size(); i++)
          {
            SF2Modulator localSF2Modulator1 = new SF2Modulator();
            sourceOperator = ((RIFFReader)localObject1).readUnsignedShort();
            destinationOperator = ((RIFFReader)localObject1).readUnsignedShort();
            amount = ((RIFFReader)localObject1).readShort();
            amountSourceOperator = ((RIFFReader)localObject1).readUnsignedShort();
            transportOperator = ((RIFFReader)localObject1).readUnsignedShort();
            SF2InstrumentRegion localSF2InstrumentRegion1 = (SF2InstrumentRegion)localArrayList4.get(i);
            if (localSF2InstrumentRegion1 != null) {
              modulators.add(localSF2Modulator1);
            }
          }
        }
        else
        {
          int k;
          if (((String)localObject2).equals("pgen"))
          {
            for (i = 0; i < localArrayList3.size(); i++)
            {
              k = ((RIFFReader)localObject1).readUnsignedShort();
              short s1 = ((RIFFReader)localObject1).readShort();
              SF2InstrumentRegion localSF2InstrumentRegion2 = (SF2InstrumentRegion)localArrayList3.get(i);
              if (localSF2InstrumentRegion2 != null) {
                generators.put(Integer.valueOf(k), Short.valueOf(s1));
              }
            }
          }
          else if (((String)localObject2).equals("inst"))
          {
            if (((RIFFReader)localObject1).available() % 22 != 0) {
              throw new RIFFInvalidDataException();
            }
            i = ((RIFFReader)localObject1).available() / 22;
            for (k = 0; k < i; k++)
            {
              SF2Layer localSF2Layer1 = new SF2Layer(this);
              name = ((RIFFReader)localObject1).readString(20);
              localArrayList6.add(Integer.valueOf(((RIFFReader)localObject1).readUnsignedShort()));
              localArrayList5.add(localSF2Layer1);
              if (k != i - 1) {
                layers.add(localSF2Layer1);
              }
            }
          }
          else if (((String)localObject2).equals("ibag"))
          {
            if (((RIFFReader)localObject1).available() % 4 != 0) {
              throw new RIFFInvalidDataException();
            }
            i = ((RIFFReader)localObject1).available() / 4;
            k = ((RIFFReader)localObject1).readUnsignedShort();
            int i1 = ((RIFFReader)localObject1).readUnsignedShort();
            while (localArrayList7.size() < k) {
              localArrayList7.add(null);
            }
            while (localArrayList8.size() < i1) {
              localArrayList8.add(null);
            }
            i--;
            if (localArrayList6.isEmpty()) {
              throw new RIFFInvalidDataException();
            }
            k = ((Integer)localArrayList6.get(0)).intValue();
            int i3;
            for (i1 = 0; i1 < k; i1++)
            {
              if (i == 0) {
                throw new RIFFInvalidDataException();
              }
              i3 = ((RIFFReader)localObject1).readUnsignedShort();
              int i6 = ((RIFFReader)localObject1).readUnsignedShort();
              while (localArrayList7.size() < i3) {
                localArrayList7.add(null);
              }
              while (localArrayList8.size() < i6) {
                localArrayList8.add(null);
              }
              i--;
            }
            for (i1 = 0; i1 < localArrayList6.size() - 1; i1++)
            {
              i3 = ((Integer)localArrayList6.get(i1 + 1)).intValue() - ((Integer)localArrayList6.get(i1)).intValue();
              SF2Layer localSF2Layer2 = (SF2Layer)layers.get(i1);
              for (i8 = 0; i8 < i3; i8++)
              {
                if (i == 0) {
                  throw new RIFFInvalidDataException();
                }
                i9 = ((RIFFReader)localObject1).readUnsignedShort();
                i10 = ((RIFFReader)localObject1).readUnsignedShort();
                localObject7 = new SF2LayerRegion();
                regions.add(localObject7);
                while (localArrayList7.size() < i9) {
                  localArrayList7.add(localObject7);
                }
                while (localArrayList8.size() < i10) {
                  localArrayList8.add(localObject7);
                }
                i--;
              }
            }
          }
          else if (((String)localObject2).equals("imod"))
          {
            for (i = 0; i < localArrayList8.size(); i++)
            {
              SF2Modulator localSF2Modulator2 = new SF2Modulator();
              sourceOperator = ((RIFFReader)localObject1).readUnsignedShort();
              destinationOperator = ((RIFFReader)localObject1).readUnsignedShort();
              amount = ((RIFFReader)localObject1).readShort();
              amountSourceOperator = ((RIFFReader)localObject1).readUnsignedShort();
              transportOperator = ((RIFFReader)localObject1).readUnsignedShort();
              if ((i < 0) || (i >= localArrayList7.size())) {
                throw new RIFFInvalidDataException();
              }
              SF2LayerRegion localSF2LayerRegion1 = (SF2LayerRegion)localArrayList7.get(i);
              if (localSF2LayerRegion1 != null) {
                modulators.add(localSF2Modulator2);
              }
            }
          }
          else
          {
            int m;
            if (((String)localObject2).equals("igen"))
            {
              for (i = 0; i < localArrayList7.size(); i++)
              {
                m = ((RIFFReader)localObject1).readUnsignedShort();
                short s2 = ((RIFFReader)localObject1).readShort();
                SF2LayerRegion localSF2LayerRegion2 = (SF2LayerRegion)localArrayList7.get(i);
                if (localSF2LayerRegion2 != null) {
                  generators.put(Integer.valueOf(m), Short.valueOf(s2));
                }
              }
            }
            else if (((String)localObject2).equals("shdr"))
            {
              if (((RIFFReader)localObject1).available() % 46 != 0) {
                throw new RIFFInvalidDataException();
              }
              i = ((RIFFReader)localObject1).available() / 46;
              for (m = 0; m < i; m++)
              {
                localObject5 = new SF2Sample(this);
                name = ((RIFFReader)localObject1).readString(20);
                long l1 = ((RIFFReader)localObject1).readUnsignedInt();
                long l2 = ((RIFFReader)localObject1).readUnsignedInt();
                if (sampleData != null) {
                  data = sampleData.subbuffer(l1 * 2L, l2 * 2L, true);
                }
                if (sampleData24 != null) {
                  data24 = sampleData24.subbuffer(l1, l2, true);
                }
                startLoop = (((RIFFReader)localObject1).readUnsignedInt() - l1);
                endLoop = (((RIFFReader)localObject1).readUnsignedInt() - l1);
                if (startLoop < 0L) {
                  startLoop = -1L;
                }
                if (endLoop < 0L) {
                  endLoop = -1L;
                }
                sampleRate = ((RIFFReader)localObject1).readUnsignedInt();
                originalPitch = ((RIFFReader)localObject1).readUnsignedByte();
                pitchCorrection = ((RIFFReader)localObject1).readByte();
                sampleLink = ((RIFFReader)localObject1).readUnsignedShort();
                sampleType = ((RIFFReader)localObject1).readUnsignedShort();
                if (m != i - 1) {
                  samples.add(localObject5);
                }
              }
            }
          }
        }
      }
    }
    Object localObject1 = layers.iterator();
    Object localObject3;
    Object localObject4;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (SF2Layer)((Iterator)localObject1).next();
      localObject3 = regions.iterator();
      localObject4 = null;
      while (((Iterator)localObject3).hasNext())
      {
        localObject5 = (SF2LayerRegion)((Iterator)localObject3).next();
        if (generators.get(Integer.valueOf(53)) != null)
        {
          int i4 = ((Short)generators.get(Integer.valueOf(53))).shortValue();
          generators.remove(Integer.valueOf(53));
          if ((i4 < 0) || (i4 >= samples.size())) {
            throw new RIFFInvalidDataException();
          }
          sample = ((SF2Sample)samples.get(i4));
        }
        else
        {
          localObject4 = localObject5;
        }
      }
      if (localObject4 != null)
      {
        ((SF2Layer)localObject2).getRegions().remove(localObject4);
        localObject5 = new SF2GlobalRegion();
        generators = generators;
        modulators = modulators;
        ((SF2Layer)localObject2).setGlobalZone((SF2GlobalRegion)localObject5);
      }
    }
    Object localObject2 = instruments.iterator();
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (SF2Instrument)((Iterator)localObject2).next();
      localObject4 = regions.iterator();
      localObject5 = null;
      Object localObject6;
      while (((Iterator)localObject4).hasNext())
      {
        localObject6 = (SF2InstrumentRegion)((Iterator)localObject4).next();
        if (generators.get(Integer.valueOf(41)) != null)
        {
          int i7 = ((Short)generators.get(Integer.valueOf(41))).shortValue();
          generators.remove(Integer.valueOf(41));
          if ((i7 < 0) || (i7 >= layers.size())) {
            throw new RIFFInvalidDataException();
          }
          layer = ((SF2Layer)layers.get(i7));
        }
        else
        {
          localObject5 = localObject6;
        }
      }
      if (localObject5 != null)
      {
        ((SF2Instrument)localObject3).getRegions().remove(localObject5);
        localObject6 = new SF2GlobalRegion();
        generators = generators;
        modulators = modulators;
        ((SF2Instrument)localObject3).setGlobalZone((SF2GlobalRegion)localObject6);
      }
    }
  }
  
  public void save(String paramString)
    throws IOException
  {
    writeSoundbank(new RIFFWriter(paramString, "sfbk"));
  }
  
  public void save(File paramFile)
    throws IOException
  {
    writeSoundbank(new RIFFWriter(paramFile, "sfbk"));
  }
  
  public void save(OutputStream paramOutputStream)
    throws IOException
  {
    writeSoundbank(new RIFFWriter(paramOutputStream, "sfbk"));
  }
  
  private void writeSoundbank(RIFFWriter paramRIFFWriter)
    throws IOException
  {
    writeInfo(paramRIFFWriter.writeList("INFO"));
    writeSdtaChunk(paramRIFFWriter.writeList("sdta"));
    writePdtaChunk(paramRIFFWriter.writeList("pdta"));
    paramRIFFWriter.close();
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
  
  private void writeInfo(RIFFWriter paramRIFFWriter)
    throws IOException
  {
    if (targetEngine == null) {
      targetEngine = "EMU8000";
    }
    if (name == null) {
      name = "";
    }
    RIFFWriter localRIFFWriter1 = paramRIFFWriter.writeChunk("ifil");
    localRIFFWriter1.writeUnsignedShort(major);
    localRIFFWriter1.writeUnsignedShort(minor);
    writeInfoStringChunk(paramRIFFWriter, "isng", targetEngine);
    writeInfoStringChunk(paramRIFFWriter, "INAM", name);
    writeInfoStringChunk(paramRIFFWriter, "irom", romName);
    if (romVersionMajor != -1)
    {
      RIFFWriter localRIFFWriter2 = paramRIFFWriter.writeChunk("iver");
      localRIFFWriter2.writeUnsignedShort(romVersionMajor);
      localRIFFWriter2.writeUnsignedShort(romVersionMinor);
    }
    writeInfoStringChunk(paramRIFFWriter, "ICRD", creationDate);
    writeInfoStringChunk(paramRIFFWriter, "IENG", engineers);
    writeInfoStringChunk(paramRIFFWriter, "IPRD", product);
    writeInfoStringChunk(paramRIFFWriter, "ICOP", copyright);
    writeInfoStringChunk(paramRIFFWriter, "ICMT", comments);
    writeInfoStringChunk(paramRIFFWriter, "ISFT", tools);
    paramRIFFWriter.close();
  }
  
  private void writeSdtaChunk(RIFFWriter paramRIFFWriter)
    throws IOException
  {
    byte[] arrayOfByte = new byte[32];
    RIFFWriter localRIFFWriter = paramRIFFWriter.writeChunk("smpl");
    Object localObject1 = samples.iterator();
    Object localObject3;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (SF2Sample)((Iterator)localObject1).next();
      localObject3 = ((SF2Sample)localObject2).getDataBuffer();
      ((ModelByteBuffer)localObject3).writeTo(localRIFFWriter);
      localRIFFWriter.write(arrayOfByte);
      localRIFFWriter.write(arrayOfByte);
    }
    if (major < 2) {
      return;
    }
    if ((major == 2) && (minor < 4)) {
      return;
    }
    localObject1 = samples.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (SF2Sample)((Iterator)localObject1).next();
      localObject3 = ((SF2Sample)localObject2).getData24Buffer();
      if (localObject3 == null) {
        return;
      }
    }
    localObject1 = paramRIFFWriter.writeChunk("sm24");
    Object localObject2 = samples.iterator();
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (SF2Sample)((Iterator)localObject2).next();
      ModelByteBuffer localModelByteBuffer = ((SF2Sample)localObject3).getData24Buffer();
      localModelByteBuffer.writeTo((OutputStream)localObject1);
      localRIFFWriter.write(arrayOfByte);
    }
  }
  
  private void writeModulators(RIFFWriter paramRIFFWriter, List<SF2Modulator> paramList)
    throws IOException
  {
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      SF2Modulator localSF2Modulator = (SF2Modulator)localIterator.next();
      paramRIFFWriter.writeUnsignedShort(sourceOperator);
      paramRIFFWriter.writeUnsignedShort(destinationOperator);
      paramRIFFWriter.writeShort(amount);
      paramRIFFWriter.writeUnsignedShort(amountSourceOperator);
      paramRIFFWriter.writeUnsignedShort(transportOperator);
    }
  }
  
  private void writeGenerators(RIFFWriter paramRIFFWriter, Map<Integer, Short> paramMap)
    throws IOException
  {
    Short localShort1 = (Short)paramMap.get(Integer.valueOf(43));
    Short localShort2 = (Short)paramMap.get(Integer.valueOf(44));
    if (localShort1 != null)
    {
      paramRIFFWriter.writeUnsignedShort(43);
      paramRIFFWriter.writeShort(localShort1.shortValue());
    }
    if (localShort2 != null)
    {
      paramRIFFWriter.writeUnsignedShort(44);
      paramRIFFWriter.writeShort(localShort2.shortValue());
    }
    Iterator localIterator = paramMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      if ((((Integer)localEntry.getKey()).intValue() != 43) && (((Integer)localEntry.getKey()).intValue() != 44))
      {
        paramRIFFWriter.writeUnsignedShort(((Integer)localEntry.getKey()).intValue());
        paramRIFFWriter.writeShort(((Short)localEntry.getValue()).shortValue());
      }
    }
  }
  
  private void writePdtaChunk(RIFFWriter paramRIFFWriter)
    throws IOException
  {
    RIFFWriter localRIFFWriter = paramRIFFWriter.writeChunk("phdr");
    int i = 0;
    Object localObject1 = instruments.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      SF2Instrument localSF2Instrument = (SF2Instrument)((Iterator)localObject1).next();
      localRIFFWriter.writeString(name, 20);
      localRIFFWriter.writeUnsignedShort(preset);
      localRIFFWriter.writeUnsignedShort(bank);
      localRIFFWriter.writeUnsignedShort(i);
      if (localSF2Instrument.getGlobalRegion() != null) {
        i++;
      }
      i += localSF2Instrument.getRegions().size();
      localRIFFWriter.writeUnsignedInt(library);
      localRIFFWriter.writeUnsignedInt(genre);
      localRIFFWriter.writeUnsignedInt(morphology);
    }
    localRIFFWriter.writeString("EOP", 20);
    localRIFFWriter.writeUnsignedShort(0);
    localRIFFWriter.writeUnsignedShort(0);
    localRIFFWriter.writeUnsignedShort(i);
    localRIFFWriter.writeUnsignedInt(0L);
    localRIFFWriter.writeUnsignedInt(0L);
    localRIFFWriter.writeUnsignedInt(0L);
    localObject1 = paramRIFFWriter.writeChunk("pbag");
    int j = 0;
    int k = 0;
    Object localObject2 = instruments.iterator();
    Object localObject5;
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (SF2Instrument)((Iterator)localObject2).next();
      if (((SF2Instrument)localObject3).getGlobalRegion() != null)
      {
        ((RIFFWriter)localObject1).writeUnsignedShort(j);
        ((RIFFWriter)localObject1).writeUnsignedShort(k);
        j += ((SF2Instrument)localObject3).getGlobalRegion().getGenerators().size();
        k += ((SF2Instrument)localObject3).getGlobalRegion().getModulators().size();
      }
      localObject4 = ((SF2Instrument)localObject3).getRegions().iterator();
      while (((Iterator)localObject4).hasNext())
      {
        localObject5 = (SF2InstrumentRegion)((Iterator)localObject4).next();
        ((RIFFWriter)localObject1).writeUnsignedShort(j);
        ((RIFFWriter)localObject1).writeUnsignedShort(k);
        if (layers.indexOf(layer) != -1) {
          j++;
        }
        j += ((SF2InstrumentRegion)localObject5).getGenerators().size();
        k += ((SF2InstrumentRegion)localObject5).getModulators().size();
      }
    }
    ((RIFFWriter)localObject1).writeUnsignedShort(j);
    ((RIFFWriter)localObject1).writeUnsignedShort(k);
    localObject2 = paramRIFFWriter.writeChunk("pmod");
    Object localObject3 = instruments.iterator();
    while (((Iterator)localObject3).hasNext())
    {
      localObject4 = (SF2Instrument)((Iterator)localObject3).next();
      if (((SF2Instrument)localObject4).getGlobalRegion() != null) {
        writeModulators((RIFFWriter)localObject2, ((SF2Instrument)localObject4).getGlobalRegion().getModulators());
      }
      localObject5 = ((SF2Instrument)localObject4).getRegions().iterator();
      while (((Iterator)localObject5).hasNext())
      {
        localObject6 = (SF2InstrumentRegion)((Iterator)localObject5).next();
        writeModulators((RIFFWriter)localObject2, ((SF2InstrumentRegion)localObject6).getModulators());
      }
    }
    ((RIFFWriter)localObject2).write(new byte[10]);
    localObject3 = paramRIFFWriter.writeChunk("pgen");
    Object localObject4 = instruments.iterator();
    Object localObject7;
    while (((Iterator)localObject4).hasNext())
    {
      localObject5 = (SF2Instrument)((Iterator)localObject4).next();
      if (((SF2Instrument)localObject5).getGlobalRegion() != null) {
        writeGenerators((RIFFWriter)localObject3, ((SF2Instrument)localObject5).getGlobalRegion().getGenerators());
      }
      localObject6 = ((SF2Instrument)localObject5).getRegions().iterator();
      while (((Iterator)localObject6).hasNext())
      {
        localObject7 = (SF2InstrumentRegion)((Iterator)localObject6).next();
        writeGenerators((RIFFWriter)localObject3, ((SF2InstrumentRegion)localObject7).getGenerators());
        i1 = layers.indexOf(layer);
        if (i1 != -1)
        {
          ((RIFFWriter)localObject3).writeUnsignedShort(41);
          ((RIFFWriter)localObject3).writeShort((short)i1);
        }
      }
    }
    ((RIFFWriter)localObject3).write(new byte[4]);
    localObject4 = paramRIFFWriter.writeChunk("inst");
    int m = 0;
    Object localObject6 = layers.iterator();
    while (((Iterator)localObject6).hasNext())
    {
      localObject7 = (SF2Layer)((Iterator)localObject6).next();
      ((RIFFWriter)localObject4).writeString(name, 20);
      ((RIFFWriter)localObject4).writeUnsignedShort(m);
      if (((SF2Layer)localObject7).getGlobalRegion() != null) {
        m++;
      }
      m += ((SF2Layer)localObject7).getRegions().size();
    }
    ((RIFFWriter)localObject4).writeString("EOI", 20);
    ((RIFFWriter)localObject4).writeUnsignedShort(m);
    localObject6 = paramRIFFWriter.writeChunk("ibag");
    int n = 0;
    int i1 = 0;
    Object localObject8 = layers.iterator();
    Object localObject11;
    while (((Iterator)localObject8).hasNext())
    {
      localObject9 = (SF2Layer)((Iterator)localObject8).next();
      if (((SF2Layer)localObject9).getGlobalRegion() != null)
      {
        ((RIFFWriter)localObject6).writeUnsignedShort(n);
        ((RIFFWriter)localObject6).writeUnsignedShort(i1);
        n += ((SF2Layer)localObject9).getGlobalRegion().getGenerators().size();
        i1 += ((SF2Layer)localObject9).getGlobalRegion().getModulators().size();
      }
      localObject10 = ((SF2Layer)localObject9).getRegions().iterator();
      while (((Iterator)localObject10).hasNext())
      {
        localObject11 = (SF2LayerRegion)((Iterator)localObject10).next();
        ((RIFFWriter)localObject6).writeUnsignedShort(n);
        ((RIFFWriter)localObject6).writeUnsignedShort(i1);
        if (samples.indexOf(sample) != -1) {
          n++;
        }
        n += ((SF2LayerRegion)localObject11).getGenerators().size();
        i1 += ((SF2LayerRegion)localObject11).getModulators().size();
      }
    }
    ((RIFFWriter)localObject6).writeUnsignedShort(n);
    ((RIFFWriter)localObject6).writeUnsignedShort(i1);
    localObject8 = paramRIFFWriter.writeChunk("imod");
    Object localObject9 = layers.iterator();
    Object localObject12;
    while (((Iterator)localObject9).hasNext())
    {
      localObject10 = (SF2Layer)((Iterator)localObject9).next();
      if (((SF2Layer)localObject10).getGlobalRegion() != null) {
        writeModulators((RIFFWriter)localObject8, ((SF2Layer)localObject10).getGlobalRegion().getModulators());
      }
      localObject11 = ((SF2Layer)localObject10).getRegions().iterator();
      while (((Iterator)localObject11).hasNext())
      {
        localObject12 = (SF2LayerRegion)((Iterator)localObject11).next();
        writeModulators((RIFFWriter)localObject8, ((SF2LayerRegion)localObject12).getModulators());
      }
    }
    ((RIFFWriter)localObject8).write(new byte[10]);
    localObject9 = paramRIFFWriter.writeChunk("igen");
    Object localObject10 = layers.iterator();
    while (((Iterator)localObject10).hasNext())
    {
      localObject11 = (SF2Layer)((Iterator)localObject10).next();
      if (((SF2Layer)localObject11).getGlobalRegion() != null) {
        writeGenerators((RIFFWriter)localObject9, ((SF2Layer)localObject11).getGlobalRegion().getGenerators());
      }
      localObject12 = ((SF2Layer)localObject11).getRegions().iterator();
      while (((Iterator)localObject12).hasNext())
      {
        localObject13 = (SF2LayerRegion)((Iterator)localObject12).next();
        writeGenerators((RIFFWriter)localObject9, ((SF2LayerRegion)localObject13).getGenerators());
        int i2 = samples.indexOf(sample);
        if (i2 != -1)
        {
          ((RIFFWriter)localObject9).writeUnsignedShort(53);
          ((RIFFWriter)localObject9).writeShort((short)i2);
        }
      }
    }
    ((RIFFWriter)localObject9).write(new byte[4]);
    localObject10 = paramRIFFWriter.writeChunk("shdr");
    long l1 = 0L;
    Object localObject13 = samples.iterator();
    while (((Iterator)localObject13).hasNext())
    {
      SF2Sample localSF2Sample = (SF2Sample)((Iterator)localObject13).next();
      ((RIFFWriter)localObject10).writeString(name, 20);
      long l2 = l1;
      l1 += data.capacity() / 2L;
      long l3 = l1;
      long l4 = startLoop + l2;
      long l5 = endLoop + l2;
      if (l4 < l2) {
        l4 = l2;
      }
      if (l5 > l3) {
        l5 = l3;
      }
      ((RIFFWriter)localObject10).writeUnsignedInt(l2);
      ((RIFFWriter)localObject10).writeUnsignedInt(l3);
      ((RIFFWriter)localObject10).writeUnsignedInt(l4);
      ((RIFFWriter)localObject10).writeUnsignedInt(l5);
      ((RIFFWriter)localObject10).writeUnsignedInt(sampleRate);
      ((RIFFWriter)localObject10).writeUnsignedByte(originalPitch);
      ((RIFFWriter)localObject10).writeByte(pitchCorrection);
      ((RIFFWriter)localObject10).writeUnsignedShort(sampleLink);
      ((RIFFWriter)localObject10).writeUnsignedShort(sampleType);
      l1 += 32L;
    }
    ((RIFFWriter)localObject10).writeString("EOS", 20);
    ((RIFFWriter)localObject10).write(new byte[26]);
  }
  
  public String getName()
  {
    return name;
  }
  
  public String getVersion()
  {
    return major + "." + minor;
  }
  
  public String getVendor()
  {
    return engineers;
  }
  
  public String getDescription()
  {
    return comments;
  }
  
  public void setName(String paramString)
  {
    name = paramString;
  }
  
  public void setVendor(String paramString)
  {
    engineers = paramString;
  }
  
  public void setDescription(String paramString)
  {
    comments = paramString;
  }
  
  public SoundbankResource[] getResources()
  {
    SoundbankResource[] arrayOfSoundbankResource = new SoundbankResource[layers.size() + samples.size()];
    int i = 0;
    for (int j = 0; j < layers.size(); j++) {
      arrayOfSoundbankResource[(i++)] = ((SoundbankResource)layers.get(j));
    }
    for (j = 0; j < samples.size(); j++) {
      arrayOfSoundbankResource[(i++)] = ((SoundbankResource)samples.get(j));
    }
    return arrayOfSoundbankResource;
  }
  
  public SF2Instrument[] getInstruments()
  {
    SF2Instrument[] arrayOfSF2Instrument = (SF2Instrument[])instruments.toArray(new SF2Instrument[instruments.size()]);
    Arrays.sort(arrayOfSF2Instrument, new ModelInstrumentComparator());
    return arrayOfSF2Instrument;
  }
  
  public SF2Layer[] getLayers()
  {
    return (SF2Layer[])layers.toArray(new SF2Layer[layers.size()]);
  }
  
  public SF2Sample[] getSamples()
  {
    return (SF2Sample[])samples.toArray(new SF2Sample[samples.size()]);
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
  
  public String getCreationDate()
  {
    return creationDate;
  }
  
  public void setCreationDate(String paramString)
  {
    creationDate = paramString;
  }
  
  public String getProduct()
  {
    return product;
  }
  
  public void setProduct(String paramString)
  {
    product = paramString;
  }
  
  public String getRomName()
  {
    return romName;
  }
  
  public void setRomName(String paramString)
  {
    romName = paramString;
  }
  
  public int getRomVersionMajor()
  {
    return romVersionMajor;
  }
  
  public void setRomVersionMajor(int paramInt)
  {
    romVersionMajor = paramInt;
  }
  
  public int getRomVersionMinor()
  {
    return romVersionMinor;
  }
  
  public void setRomVersionMinor(int paramInt)
  {
    romVersionMinor = paramInt;
  }
  
  public String getTargetEngine()
  {
    return targetEngine;
  }
  
  public void setTargetEngine(String paramString)
  {
    targetEngine = paramString;
  }
  
  public String getTools()
  {
    return tools;
  }
  
  public void setTools(String paramString)
  {
    tools = paramString;
  }
  
  public void addResource(SoundbankResource paramSoundbankResource)
  {
    if ((paramSoundbankResource instanceof SF2Instrument)) {
      instruments.add((SF2Instrument)paramSoundbankResource);
    }
    if ((paramSoundbankResource instanceof SF2Layer)) {
      layers.add((SF2Layer)paramSoundbankResource);
    }
    if ((paramSoundbankResource instanceof SF2Sample)) {
      samples.add((SF2Sample)paramSoundbankResource);
    }
  }
  
  public void removeResource(SoundbankResource paramSoundbankResource)
  {
    if ((paramSoundbankResource instanceof SF2Instrument)) {
      instruments.remove((SF2Instrument)paramSoundbankResource);
    }
    if ((paramSoundbankResource instanceof SF2Layer)) {
      layers.remove((SF2Layer)paramSoundbankResource);
    }
    if ((paramSoundbankResource instanceof SF2Sample)) {
      samples.remove((SF2Sample)paramSoundbankResource);
    }
  }
  
  public void addInstrument(SF2Instrument paramSF2Instrument)
  {
    instruments.add(paramSF2Instrument);
  }
  
  public void removeInstrument(SF2Instrument paramSF2Instrument)
  {
    instruments.remove(paramSF2Instrument);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\SF2Soundbank.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */