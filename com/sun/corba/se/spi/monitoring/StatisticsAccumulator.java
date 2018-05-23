package com.sun.corba.se.spi.monitoring;

public class StatisticsAccumulator
{
  protected double max = Double.MIN_VALUE;
  protected double min = Double.MAX_VALUE;
  private double sampleSum;
  private double sampleSquareSum;
  private long sampleCount;
  protected String unit;
  
  public void sample(double paramDouble)
  {
    sampleCount += 1L;
    if (paramDouble < min) {
      min = paramDouble;
    }
    if (paramDouble > max) {
      max = paramDouble;
    }
    sampleSum += paramDouble;
    sampleSquareSum += paramDouble * paramDouble;
  }
  
  public String getValue()
  {
    return toString();
  }
  
  public String toString()
  {
    return "Minimum Value = " + min + " " + unit + " Maximum Value = " + max + " " + unit + " Average Value = " + computeAverage() + " " + unit + " Standard Deviation = " + computeStandardDeviation() + " " + unit + " Samples Collected = " + sampleCount;
  }
  
  protected double computeAverage()
  {
    return sampleSum / sampleCount;
  }
  
  protected double computeStandardDeviation()
  {
    double d = sampleSum * sampleSum;
    return Math.sqrt((sampleSquareSum - d / sampleCount) / (sampleCount - 1L));
  }
  
  public StatisticsAccumulator(String paramString)
  {
    unit = paramString;
    sampleCount = 0L;
    sampleSum = 0.0D;
    sampleSquareSum = 0.0D;
  }
  
  void clearState()
  {
    min = Double.MAX_VALUE;
    max = Double.MIN_VALUE;
    sampleCount = 0L;
    sampleSum = 0.0D;
    sampleSquareSum = 0.0D;
  }
  
  public void unitTestValidate(String paramString, double paramDouble1, double paramDouble2, long paramLong, double paramDouble3, double paramDouble4)
  {
    if (!paramString.equals(unit)) {
      throw new RuntimeException("Unit is not same as expected Unit\nUnit = " + unit + "ExpectedUnit = " + paramString);
    }
    if (min != paramDouble1) {
      throw new RuntimeException("Minimum value is not same as expected minimum value\nMin Value = " + min + "Expected Min Value = " + paramDouble1);
    }
    if (max != paramDouble2) {
      throw new RuntimeException("Maximum value is not same as expected maximum value\nMax Value = " + max + "Expected Max Value = " + paramDouble2);
    }
    if (sampleCount != paramLong) {
      throw new RuntimeException("Sample count is not same as expected Sample Count\nSampleCount = " + sampleCount + "Expected Sample Count = " + paramLong);
    }
    if (computeAverage() != paramDouble3) {
      throw new RuntimeException("Average is not same as expected Average\nAverage = " + computeAverage() + "Expected Average = " + paramDouble3);
    }
    double d = Math.abs(computeStandardDeviation() - paramDouble4);
    if (d > 1.0D) {
      throw new RuntimeException("Standard Deviation is not same as expected Std Deviation\nStandard Dev = " + computeStandardDeviation() + "Expected Standard Dev = " + paramDouble4);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\monitoring\StatisticsAccumulator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */