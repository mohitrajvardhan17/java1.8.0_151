package javax.sound.sampled;

import com.sun.media.sound.JDK13Services;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.sound.sampled.spi.AudioFileReader;
import javax.sound.sampled.spi.AudioFileWriter;
import javax.sound.sampled.spi.FormatConversionProvider;
import javax.sound.sampled.spi.MixerProvider;

public class AudioSystem
{
  public static final int NOT_SPECIFIED = -1;
  
  private AudioSystem() {}
  
  public static Mixer.Info[] getMixerInfo()
  {
    List localList = getMixerInfoList();
    Mixer.Info[] arrayOfInfo = (Mixer.Info[])localList.toArray(new Mixer.Info[localList.size()]);
    return arrayOfInfo;
  }
  
  public static Mixer getMixer(Mixer.Info paramInfo)
  {
    Object localObject = null;
    List localList = getMixerProviders();
    for (int i = 0; i < localList.size(); i++) {
      try
      {
        return ((MixerProvider)localList.get(i)).getMixer(paramInfo);
      }
      catch (IllegalArgumentException localIllegalArgumentException1) {}catch (NullPointerException localNullPointerException1) {}
    }
    if (paramInfo == null) {
      for (i = 0; i < localList.size(); i++) {
        try
        {
          MixerProvider localMixerProvider = (MixerProvider)localList.get(i);
          Mixer.Info[] arrayOfInfo = localMixerProvider.getMixerInfo();
          int j = 0;
          while (j < arrayOfInfo.length) {
            try
            {
              return localMixerProvider.getMixer(arrayOfInfo[j]);
            }
            catch (IllegalArgumentException localIllegalArgumentException3)
            {
              j++;
            }
          }
        }
        catch (IllegalArgumentException localIllegalArgumentException2) {}catch (NullPointerException localNullPointerException2) {}
      }
    }
    throw new IllegalArgumentException("Mixer not supported: " + (paramInfo != null ? paramInfo.toString() : "null"));
  }
  
  public static Line.Info[] getSourceLineInfo(Line.Info paramInfo)
  {
    Vector localVector = new Vector();
    Object localObject = null;
    Mixer.Info[] arrayOfInfo = getMixerInfo();
    for (int i = 0; i < arrayOfInfo.length; i++)
    {
      Mixer localMixer = getMixer(arrayOfInfo[i]);
      Line.Info[] arrayOfInfo1 = localMixer.getSourceLineInfo(paramInfo);
      for (j = 0; j < arrayOfInfo1.length; j++) {
        localVector.addElement(arrayOfInfo1[j]);
      }
    }
    Line.Info[] arrayOfInfo2 = new Line.Info[localVector.size()];
    for (int j = 0; j < arrayOfInfo2.length; j++) {
      arrayOfInfo2[j] = ((Line.Info)localVector.get(j));
    }
    return arrayOfInfo2;
  }
  
  public static Line.Info[] getTargetLineInfo(Line.Info paramInfo)
  {
    Vector localVector = new Vector();
    Object localObject = null;
    Mixer.Info[] arrayOfInfo = getMixerInfo();
    for (int i = 0; i < arrayOfInfo.length; i++)
    {
      Mixer localMixer = getMixer(arrayOfInfo[i]);
      Line.Info[] arrayOfInfo1 = localMixer.getTargetLineInfo(paramInfo);
      for (j = 0; j < arrayOfInfo1.length; j++) {
        localVector.addElement(arrayOfInfo1[j]);
      }
    }
    Line.Info[] arrayOfInfo2 = new Line.Info[localVector.size()];
    for (int j = 0; j < arrayOfInfo2.length; j++) {
      arrayOfInfo2[j] = ((Line.Info)localVector.get(j));
    }
    return arrayOfInfo2;
  }
  
  public static boolean isLineSupported(Line.Info paramInfo)
  {
    Mixer.Info[] arrayOfInfo = getMixerInfo();
    for (int i = 0; i < arrayOfInfo.length; i++) {
      if (arrayOfInfo[i] != null)
      {
        Mixer localMixer = getMixer(arrayOfInfo[i]);
        if (localMixer.isLineSupported(paramInfo)) {
          return true;
        }
      }
    }
    return false;
  }
  
  public static Line getLine(Line.Info paramInfo)
    throws LineUnavailableException
  {
    Object localObject = null;
    List localList = getMixerProviders();
    try
    {
      Mixer localMixer1 = getDefaultMixer(localList, paramInfo);
      if ((localMixer1 != null) && (localMixer1.isLineSupported(paramInfo))) {
        return localMixer1.getLine(paramInfo);
      }
    }
    catch (LineUnavailableException localLineUnavailableException1)
    {
      localObject = localLineUnavailableException1;
    }
    catch (IllegalArgumentException localIllegalArgumentException1) {}
    MixerProvider localMixerProvider;
    Mixer.Info[] arrayOfInfo;
    int j;
    for (int i = 0; i < localList.size(); i++)
    {
      localMixerProvider = (MixerProvider)localList.get(i);
      arrayOfInfo = localMixerProvider.getMixerInfo();
      for (j = 0; j < arrayOfInfo.length; j++) {
        try
        {
          Mixer localMixer2 = localMixerProvider.getMixer(arrayOfInfo[j]);
          if (isAppropriateMixer(localMixer2, paramInfo, true)) {
            return localMixer2.getLine(paramInfo);
          }
        }
        catch (LineUnavailableException localLineUnavailableException2)
        {
          localObject = localLineUnavailableException2;
        }
        catch (IllegalArgumentException localIllegalArgumentException2) {}
      }
    }
    for (i = 0; i < localList.size(); i++)
    {
      localMixerProvider = (MixerProvider)localList.get(i);
      arrayOfInfo = localMixerProvider.getMixerInfo();
      for (j = 0; j < arrayOfInfo.length; j++) {
        try
        {
          Mixer localMixer3 = localMixerProvider.getMixer(arrayOfInfo[j]);
          if (isAppropriateMixer(localMixer3, paramInfo, false)) {
            return localMixer3.getLine(paramInfo);
          }
        }
        catch (LineUnavailableException localLineUnavailableException3)
        {
          localObject = localLineUnavailableException3;
        }
        catch (IllegalArgumentException localIllegalArgumentException3) {}
      }
    }
    if (localObject != null) {
      throw ((Throwable)localObject);
    }
    throw new IllegalArgumentException("No line matching " + paramInfo.toString() + " is supported.");
  }
  
  public static Clip getClip()
    throws LineUnavailableException
  {
    AudioFormat localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, -1.0F, 16, 2, 4, -1.0F, true);
    DataLine.Info localInfo = new DataLine.Info(Clip.class, localAudioFormat);
    return (Clip)getLine(localInfo);
  }
  
  public static Clip getClip(Mixer.Info paramInfo)
    throws LineUnavailableException
  {
    AudioFormat localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, -1.0F, 16, 2, 4, -1.0F, true);
    DataLine.Info localInfo = new DataLine.Info(Clip.class, localAudioFormat);
    Mixer localMixer = getMixer(paramInfo);
    return (Clip)localMixer.getLine(localInfo);
  }
  
  public static SourceDataLine getSourceDataLine(AudioFormat paramAudioFormat)
    throws LineUnavailableException
  {
    DataLine.Info localInfo = new DataLine.Info(SourceDataLine.class, paramAudioFormat);
    return (SourceDataLine)getLine(localInfo);
  }
  
  public static SourceDataLine getSourceDataLine(AudioFormat paramAudioFormat, Mixer.Info paramInfo)
    throws LineUnavailableException
  {
    DataLine.Info localInfo = new DataLine.Info(SourceDataLine.class, paramAudioFormat);
    Mixer localMixer = getMixer(paramInfo);
    return (SourceDataLine)localMixer.getLine(localInfo);
  }
  
  public static TargetDataLine getTargetDataLine(AudioFormat paramAudioFormat)
    throws LineUnavailableException
  {
    DataLine.Info localInfo = new DataLine.Info(TargetDataLine.class, paramAudioFormat);
    return (TargetDataLine)getLine(localInfo);
  }
  
  public static TargetDataLine getTargetDataLine(AudioFormat paramAudioFormat, Mixer.Info paramInfo)
    throws LineUnavailableException
  {
    DataLine.Info localInfo = new DataLine.Info(TargetDataLine.class, paramAudioFormat);
    Mixer localMixer = getMixer(paramInfo);
    return (TargetDataLine)localMixer.getLine(localInfo);
  }
  
  public static AudioFormat.Encoding[] getTargetEncodings(AudioFormat.Encoding paramEncoding)
  {
    List localList = getFormatConversionProviders();
    Vector localVector = new Vector();
    AudioFormat.Encoding[] arrayOfEncoding1 = null;
    for (int i = 0; i < localList.size(); i++)
    {
      FormatConversionProvider localFormatConversionProvider = (FormatConversionProvider)localList.get(i);
      if (localFormatConversionProvider.isSourceEncodingSupported(paramEncoding))
      {
        arrayOfEncoding1 = localFormatConversionProvider.getTargetEncodings();
        for (int j = 0; j < arrayOfEncoding1.length; j++) {
          localVector.addElement(arrayOfEncoding1[j]);
        }
      }
    }
    AudioFormat.Encoding[] arrayOfEncoding2 = (AudioFormat.Encoding[])localVector.toArray(new AudioFormat.Encoding[0]);
    return arrayOfEncoding2;
  }
  
  public static AudioFormat.Encoding[] getTargetEncodings(AudioFormat paramAudioFormat)
  {
    List localList = getFormatConversionProviders();
    Vector localVector = new Vector();
    int i = 0;
    int j = 0;
    AudioFormat.Encoding[] arrayOfEncoding1 = null;
    for (int k = 0; k < localList.size(); k++)
    {
      arrayOfEncoding1 = ((FormatConversionProvider)localList.get(k)).getTargetEncodings(paramAudioFormat);
      i += arrayOfEncoding1.length;
      localVector.addElement(arrayOfEncoding1);
    }
    AudioFormat.Encoding[] arrayOfEncoding2 = new AudioFormat.Encoding[i];
    for (int m = 0; m < localVector.size(); m++)
    {
      arrayOfEncoding1 = (AudioFormat.Encoding[])localVector.get(m);
      for (int n = 0; n < arrayOfEncoding1.length; n++) {
        arrayOfEncoding2[(j++)] = arrayOfEncoding1[n];
      }
    }
    return arrayOfEncoding2;
  }
  
  public static boolean isConversionSupported(AudioFormat.Encoding paramEncoding, AudioFormat paramAudioFormat)
  {
    List localList = getFormatConversionProviders();
    for (int i = 0; i < localList.size(); i++)
    {
      FormatConversionProvider localFormatConversionProvider = (FormatConversionProvider)localList.get(i);
      if (localFormatConversionProvider.isConversionSupported(paramEncoding, paramAudioFormat)) {
        return true;
      }
    }
    return false;
  }
  
  public static AudioInputStream getAudioInputStream(AudioFormat.Encoding paramEncoding, AudioInputStream paramAudioInputStream)
  {
    List localList = getFormatConversionProviders();
    for (int i = 0; i < localList.size(); i++)
    {
      FormatConversionProvider localFormatConversionProvider = (FormatConversionProvider)localList.get(i);
      if (localFormatConversionProvider.isConversionSupported(paramEncoding, paramAudioInputStream.getFormat())) {
        return localFormatConversionProvider.getAudioInputStream(paramEncoding, paramAudioInputStream);
      }
    }
    throw new IllegalArgumentException("Unsupported conversion: " + paramEncoding + " from " + paramAudioInputStream.getFormat());
  }
  
  public static AudioFormat[] getTargetFormats(AudioFormat.Encoding paramEncoding, AudioFormat paramAudioFormat)
  {
    List localList = getFormatConversionProviders();
    Vector localVector = new Vector();
    int i = 0;
    int j = 0;
    AudioFormat[] arrayOfAudioFormat1 = null;
    for (int k = 0; k < localList.size(); k++)
    {
      FormatConversionProvider localFormatConversionProvider = (FormatConversionProvider)localList.get(k);
      arrayOfAudioFormat1 = localFormatConversionProvider.getTargetFormats(paramEncoding, paramAudioFormat);
      i += arrayOfAudioFormat1.length;
      localVector.addElement(arrayOfAudioFormat1);
    }
    AudioFormat[] arrayOfAudioFormat2 = new AudioFormat[i];
    for (int m = 0; m < localVector.size(); m++)
    {
      arrayOfAudioFormat1 = (AudioFormat[])localVector.get(m);
      for (int n = 0; n < arrayOfAudioFormat1.length; n++) {
        arrayOfAudioFormat2[(j++)] = arrayOfAudioFormat1[n];
      }
    }
    return arrayOfAudioFormat2;
  }
  
  public static boolean isConversionSupported(AudioFormat paramAudioFormat1, AudioFormat paramAudioFormat2)
  {
    List localList = getFormatConversionProviders();
    for (int i = 0; i < localList.size(); i++)
    {
      FormatConversionProvider localFormatConversionProvider = (FormatConversionProvider)localList.get(i);
      if (localFormatConversionProvider.isConversionSupported(paramAudioFormat1, paramAudioFormat2)) {
        return true;
      }
    }
    return false;
  }
  
  public static AudioInputStream getAudioInputStream(AudioFormat paramAudioFormat, AudioInputStream paramAudioInputStream)
  {
    if (paramAudioInputStream.getFormat().matches(paramAudioFormat)) {
      return paramAudioInputStream;
    }
    List localList = getFormatConversionProviders();
    for (int i = 0; i < localList.size(); i++)
    {
      FormatConversionProvider localFormatConversionProvider = (FormatConversionProvider)localList.get(i);
      if (localFormatConversionProvider.isConversionSupported(paramAudioFormat, paramAudioInputStream.getFormat())) {
        return localFormatConversionProvider.getAudioInputStream(paramAudioFormat, paramAudioInputStream);
      }
    }
    throw new IllegalArgumentException("Unsupported conversion: " + paramAudioFormat + " from " + paramAudioInputStream.getFormat());
  }
  
  public static AudioFileFormat getAudioFileFormat(InputStream paramInputStream)
    throws UnsupportedAudioFileException, IOException
  {
    List localList = getAudioFileReaders();
    AudioFileFormat localAudioFileFormat = null;
    int i = 0;
    while (i < localList.size())
    {
      AudioFileReader localAudioFileReader = (AudioFileReader)localList.get(i);
      try
      {
        localAudioFileFormat = localAudioFileReader.getAudioFileFormat(paramInputStream);
      }
      catch (UnsupportedAudioFileException localUnsupportedAudioFileException)
      {
        i++;
      }
    }
    if (localAudioFileFormat == null) {
      throw new UnsupportedAudioFileException("file is not a supported file type");
    }
    return localAudioFileFormat;
  }
  
  public static AudioFileFormat getAudioFileFormat(URL paramURL)
    throws UnsupportedAudioFileException, IOException
  {
    List localList = getAudioFileReaders();
    AudioFileFormat localAudioFileFormat = null;
    int i = 0;
    while (i < localList.size())
    {
      AudioFileReader localAudioFileReader = (AudioFileReader)localList.get(i);
      try
      {
        localAudioFileFormat = localAudioFileReader.getAudioFileFormat(paramURL);
      }
      catch (UnsupportedAudioFileException localUnsupportedAudioFileException)
      {
        i++;
      }
    }
    if (localAudioFileFormat == null) {
      throw new UnsupportedAudioFileException("file is not a supported file type");
    }
    return localAudioFileFormat;
  }
  
  public static AudioFileFormat getAudioFileFormat(File paramFile)
    throws UnsupportedAudioFileException, IOException
  {
    List localList = getAudioFileReaders();
    AudioFileFormat localAudioFileFormat = null;
    int i = 0;
    while (i < localList.size())
    {
      AudioFileReader localAudioFileReader = (AudioFileReader)localList.get(i);
      try
      {
        localAudioFileFormat = localAudioFileReader.getAudioFileFormat(paramFile);
      }
      catch (UnsupportedAudioFileException localUnsupportedAudioFileException)
      {
        i++;
      }
    }
    if (localAudioFileFormat == null) {
      throw new UnsupportedAudioFileException("file is not a supported file type");
    }
    return localAudioFileFormat;
  }
  
  public static AudioInputStream getAudioInputStream(InputStream paramInputStream)
    throws UnsupportedAudioFileException, IOException
  {
    List localList = getAudioFileReaders();
    AudioInputStream localAudioInputStream = null;
    int i = 0;
    while (i < localList.size())
    {
      AudioFileReader localAudioFileReader = (AudioFileReader)localList.get(i);
      try
      {
        localAudioInputStream = localAudioFileReader.getAudioInputStream(paramInputStream);
      }
      catch (UnsupportedAudioFileException localUnsupportedAudioFileException)
      {
        i++;
      }
    }
    if (localAudioInputStream == null) {
      throw new UnsupportedAudioFileException("could not get audio input stream from input stream");
    }
    return localAudioInputStream;
  }
  
  public static AudioInputStream getAudioInputStream(URL paramURL)
    throws UnsupportedAudioFileException, IOException
  {
    List localList = getAudioFileReaders();
    AudioInputStream localAudioInputStream = null;
    int i = 0;
    while (i < localList.size())
    {
      AudioFileReader localAudioFileReader = (AudioFileReader)localList.get(i);
      try
      {
        localAudioInputStream = localAudioFileReader.getAudioInputStream(paramURL);
      }
      catch (UnsupportedAudioFileException localUnsupportedAudioFileException)
      {
        i++;
      }
    }
    if (localAudioInputStream == null) {
      throw new UnsupportedAudioFileException("could not get audio input stream from input URL");
    }
    return localAudioInputStream;
  }
  
  public static AudioInputStream getAudioInputStream(File paramFile)
    throws UnsupportedAudioFileException, IOException
  {
    List localList = getAudioFileReaders();
    AudioInputStream localAudioInputStream = null;
    int i = 0;
    while (i < localList.size())
    {
      AudioFileReader localAudioFileReader = (AudioFileReader)localList.get(i);
      try
      {
        localAudioInputStream = localAudioFileReader.getAudioInputStream(paramFile);
      }
      catch (UnsupportedAudioFileException localUnsupportedAudioFileException)
      {
        i++;
      }
    }
    if (localAudioInputStream == null) {
      throw new UnsupportedAudioFileException("could not get audio input stream from input file");
    }
    return localAudioInputStream;
  }
  
  public static AudioFileFormat.Type[] getAudioFileTypes()
  {
    List localList = getAudioFileWriters();
    HashSet localHashSet = new HashSet();
    for (int i = 0; i < localList.size(); i++)
    {
      AudioFileWriter localAudioFileWriter = (AudioFileWriter)localList.get(i);
      AudioFileFormat.Type[] arrayOfType2 = localAudioFileWriter.getAudioFileTypes();
      for (int j = 0; j < arrayOfType2.length; j++) {
        localHashSet.add(arrayOfType2[j]);
      }
    }
    AudioFileFormat.Type[] arrayOfType1 = (AudioFileFormat.Type[])localHashSet.toArray(new AudioFileFormat.Type[0]);
    return arrayOfType1;
  }
  
  public static boolean isFileTypeSupported(AudioFileFormat.Type paramType)
  {
    List localList = getAudioFileWriters();
    for (int i = 0; i < localList.size(); i++)
    {
      AudioFileWriter localAudioFileWriter = (AudioFileWriter)localList.get(i);
      if (localAudioFileWriter.isFileTypeSupported(paramType)) {
        return true;
      }
    }
    return false;
  }
  
  public static AudioFileFormat.Type[] getAudioFileTypes(AudioInputStream paramAudioInputStream)
  {
    List localList = getAudioFileWriters();
    HashSet localHashSet = new HashSet();
    for (int i = 0; i < localList.size(); i++)
    {
      AudioFileWriter localAudioFileWriter = (AudioFileWriter)localList.get(i);
      AudioFileFormat.Type[] arrayOfType2 = localAudioFileWriter.getAudioFileTypes(paramAudioInputStream);
      for (int j = 0; j < arrayOfType2.length; j++) {
        localHashSet.add(arrayOfType2[j]);
      }
    }
    AudioFileFormat.Type[] arrayOfType1 = (AudioFileFormat.Type[])localHashSet.toArray(new AudioFileFormat.Type[0]);
    return arrayOfType1;
  }
  
  public static boolean isFileTypeSupported(AudioFileFormat.Type paramType, AudioInputStream paramAudioInputStream)
  {
    List localList = getAudioFileWriters();
    for (int i = 0; i < localList.size(); i++)
    {
      AudioFileWriter localAudioFileWriter = (AudioFileWriter)localList.get(i);
      if (localAudioFileWriter.isFileTypeSupported(paramType, paramAudioInputStream)) {
        return true;
      }
    }
    return false;
  }
  
  public static int write(AudioInputStream paramAudioInputStream, AudioFileFormat.Type paramType, OutputStream paramOutputStream)
    throws IOException
  {
    List localList = getAudioFileWriters();
    int i = 0;
    int j = 0;
    int k = 0;
    while (k < localList.size())
    {
      AudioFileWriter localAudioFileWriter = (AudioFileWriter)localList.get(k);
      try
      {
        i = localAudioFileWriter.write(paramAudioInputStream, paramType, paramOutputStream);
        j = 1;
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        k++;
      }
    }
    if (j == 0) {
      throw new IllegalArgumentException("could not write audio file: file type not supported: " + paramType);
    }
    return i;
  }
  
  public static int write(AudioInputStream paramAudioInputStream, AudioFileFormat.Type paramType, File paramFile)
    throws IOException
  {
    List localList = getAudioFileWriters();
    int i = 0;
    int j = 0;
    int k = 0;
    while (k < localList.size())
    {
      AudioFileWriter localAudioFileWriter = (AudioFileWriter)localList.get(k);
      try
      {
        i = localAudioFileWriter.write(paramAudioInputStream, paramType, paramFile);
        j = 1;
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        k++;
      }
    }
    if (j == 0) {
      throw new IllegalArgumentException("could not write audio file: file type not supported: " + paramType);
    }
    return i;
  }
  
  private static List getMixerProviders()
  {
    return getProviders(MixerProvider.class);
  }
  
  private static List getFormatConversionProviders()
  {
    return getProviders(FormatConversionProvider.class);
  }
  
  private static List getAudioFileReaders()
  {
    return getProviders(AudioFileReader.class);
  }
  
  private static List getAudioFileWriters()
  {
    return getProviders(AudioFileWriter.class);
  }
  
  private static Mixer getDefaultMixer(List paramList, Line.Info paramInfo)
  {
    Class localClass = paramInfo.getLineClass();
    String str1 = JDK13Services.getDefaultProviderClassName(localClass);
    String str2 = JDK13Services.getDefaultInstanceName(localClass);
    Mixer localMixer;
    if (str1 != null)
    {
      MixerProvider localMixerProvider = getNamedProvider(str1, paramList);
      if (localMixerProvider != null) {
        if (str2 != null)
        {
          localMixer = getNamedMixer(str2, localMixerProvider, paramInfo);
          if (localMixer != null) {
            return localMixer;
          }
        }
        else
        {
          localMixer = getFirstMixer(localMixerProvider, paramInfo, false);
          if (localMixer != null) {
            return localMixer;
          }
        }
      }
    }
    if (str2 != null)
    {
      localMixer = getNamedMixer(str2, paramList, paramInfo);
      if (localMixer != null) {
        return localMixer;
      }
    }
    return null;
  }
  
  private static MixerProvider getNamedProvider(String paramString, List paramList)
  {
    for (int i = 0; i < paramList.size(); i++)
    {
      MixerProvider localMixerProvider = (MixerProvider)paramList.get(i);
      if (localMixerProvider.getClass().getName().equals(paramString)) {
        return localMixerProvider;
      }
    }
    return null;
  }
  
  private static Mixer getNamedMixer(String paramString, MixerProvider paramMixerProvider, Line.Info paramInfo)
  {
    Mixer.Info[] arrayOfInfo = paramMixerProvider.getMixerInfo();
    for (int i = 0; i < arrayOfInfo.length; i++) {
      if (arrayOfInfo[i].getName().equals(paramString))
      {
        Mixer localMixer = paramMixerProvider.getMixer(arrayOfInfo[i]);
        if (isAppropriateMixer(localMixer, paramInfo, false)) {
          return localMixer;
        }
      }
    }
    return null;
  }
  
  private static Mixer getNamedMixer(String paramString, List paramList, Line.Info paramInfo)
  {
    for (int i = 0; i < paramList.size(); i++)
    {
      MixerProvider localMixerProvider = (MixerProvider)paramList.get(i);
      Mixer localMixer = getNamedMixer(paramString, localMixerProvider, paramInfo);
      if (localMixer != null) {
        return localMixer;
      }
    }
    return null;
  }
  
  private static Mixer getFirstMixer(MixerProvider paramMixerProvider, Line.Info paramInfo, boolean paramBoolean)
  {
    Mixer.Info[] arrayOfInfo = paramMixerProvider.getMixerInfo();
    for (int i = 0; i < arrayOfInfo.length; i++)
    {
      Mixer localMixer = paramMixerProvider.getMixer(arrayOfInfo[i]);
      if (isAppropriateMixer(localMixer, paramInfo, paramBoolean)) {
        return localMixer;
      }
    }
    return null;
  }
  
  private static boolean isAppropriateMixer(Mixer paramMixer, Line.Info paramInfo, boolean paramBoolean)
  {
    if (!paramMixer.isLineSupported(paramInfo)) {
      return false;
    }
    Class localClass = paramInfo.getLineClass();
    if ((paramBoolean) && ((SourceDataLine.class.isAssignableFrom(localClass)) || (Clip.class.isAssignableFrom(localClass))))
    {
      int i = paramMixer.getMaxLines(paramInfo);
      return (i == -1) || (i > 1);
    }
    return true;
  }
  
  private static List getMixerInfoList()
  {
    List localList = getMixerProviders();
    return getMixerInfoList(localList);
  }
  
  private static List getMixerInfoList(List paramList)
  {
    ArrayList localArrayList = new ArrayList();
    for (int i = 0; i < paramList.size(); i++)
    {
      Mixer.Info[] arrayOfInfo = (Mixer.Info[])((MixerProvider)paramList.get(i)).getMixerInfo();
      for (int j = 0; j < arrayOfInfo.length; j++) {
        localArrayList.add(arrayOfInfo[j]);
      }
    }
    return localArrayList;
  }
  
  private static List getProviders(Class paramClass)
  {
    return JDK13Services.getProviders(paramClass);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sound\sampled\AudioSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */