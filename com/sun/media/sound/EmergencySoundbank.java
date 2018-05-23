package com.sun.media.sound;

import java.util.List;
import java.util.Random;
import javax.sound.midi.Patch;
import javax.sound.midi.SoundbankResource;
import javax.sound.sampled.AudioFormat;

public final class EmergencySoundbank
{
  private static final String[] general_midi_instruments = { "Acoustic Grand Piano", "Bright Acoustic Piano", "Electric Grand Piano", "Honky-tonk Piano", "Electric Piano 1", "Electric Piano 2", "Harpsichord", "Clavi", "Celesta", "Glockenspiel", "Music Box", "Vibraphone", "Marimba", "Xylophone", "Tubular Bells", "Dulcimer", "Drawbar Organ", "Percussive Organ", "Rock Organ", "Church Organ", "Reed Organ", "Accordion", "Harmonica", "Tango Accordion", "Acoustic Guitar (nylon)", "Acoustic Guitar (steel)", "Electric Guitar (jazz)", "Electric Guitar (clean)", "Electric Guitar (muted)", "Overdriven Guitar", "Distortion Guitar", "Guitar harmonics", "Acoustic Bass", "Electric Bass (finger)", "Electric Bass (pick)", "Fretless Bass", "Slap Bass 1", "Slap Bass 2", "Synth Bass 1", "Synth Bass 2", "Violin", "Viola", "Cello", "Contrabass", "Tremolo Strings", "Pizzicato Strings", "Orchestral Harp", "Timpani", "String Ensemble 1", "String Ensemble 2", "SynthStrings 1", "SynthStrings 2", "Choir Aahs", "Voice Oohs", "Synth Voice", "Orchestra Hit", "Trumpet", "Trombone", "Tuba", "Muted Trumpet", "French Horn", "Brass Section", "SynthBrass 1", "SynthBrass 2", "Soprano Sax", "Alto Sax", "Tenor Sax", "Baritone Sax", "Oboe", "English Horn", "Bassoon", "Clarinet", "Piccolo", "Flute", "Recorder", "Pan Flute", "Blown Bottle", "Shakuhachi", "Whistle", "Ocarina", "Lead 1 (square)", "Lead 2 (sawtooth)", "Lead 3 (calliope)", "Lead 4 (chiff)", "Lead 5 (charang)", "Lead 6 (voice)", "Lead 7 (fifths)", "Lead 8 (bass + lead)", "Pad 1 (new age)", "Pad 2 (warm)", "Pad 3 (polysynth)", "Pad 4 (choir)", "Pad 5 (bowed)", "Pad 6 (metallic)", "Pad 7 (halo)", "Pad 8 (sweep)", "FX 1 (rain)", "FX 2 (soundtrack)", "FX 3 (crystal)", "FX 4 (atmosphere)", "FX 5 (brightness)", "FX 6 (goblins)", "FX 7 (echoes)", "FX 8 (sci-fi)", "Sitar", "Banjo", "Shamisen", "Koto", "Kalimba", "Bag pipe", "Fiddle", "Shanai", "Tinkle Bell", "Agogo", "Steel Drums", "Woodblock", "Taiko Drum", "Melodic Tom", "Synth Drum", "Reverse Cymbal", "Guitar Fret Noise", "Breath Noise", "Seashore", "Bird Tweet", "Telephone Ring", "Helicopter", "Applause", "Gunshot" };
  
  public EmergencySoundbank() {}
  
  public static SF2Soundbank createSoundbank()
    throws Exception
  {
    SF2Soundbank localSF2Soundbank = new SF2Soundbank();
    localSF2Soundbank.setName("Emergency GM sound set");
    localSF2Soundbank.setVendor("Generated");
    localSF2Soundbank.setDescription("Emergency generated soundbank");
    SF2Layer localSF2Layer1 = new_bass_drum(localSF2Soundbank);
    SF2Layer localSF2Layer2 = new_snare_drum(localSF2Soundbank);
    SF2Layer localSF2Layer3 = new_tom(localSF2Soundbank);
    SF2Layer localSF2Layer4 = new_open_hihat(localSF2Soundbank);
    SF2Layer localSF2Layer5 = new_closed_hihat(localSF2Soundbank);
    SF2Layer localSF2Layer6 = new_crash_cymbal(localSF2Soundbank);
    SF2Layer localSF2Layer7 = new_side_stick(localSF2Soundbank);
    SF2Layer[] arrayOfSF2Layer = new SF2Layer[''];
    arrayOfSF2Layer[35] = localSF2Layer1;
    arrayOfSF2Layer[36] = localSF2Layer1;
    arrayOfSF2Layer[38] = localSF2Layer2;
    arrayOfSF2Layer[40] = localSF2Layer2;
    arrayOfSF2Layer[41] = localSF2Layer3;
    arrayOfSF2Layer[43] = localSF2Layer3;
    arrayOfSF2Layer[45] = localSF2Layer3;
    arrayOfSF2Layer[47] = localSF2Layer3;
    arrayOfSF2Layer[48] = localSF2Layer3;
    arrayOfSF2Layer[50] = localSF2Layer3;
    arrayOfSF2Layer[42] = localSF2Layer5;
    arrayOfSF2Layer[44] = localSF2Layer5;
    arrayOfSF2Layer[46] = localSF2Layer4;
    arrayOfSF2Layer[49] = localSF2Layer6;
    arrayOfSF2Layer[51] = localSF2Layer6;
    arrayOfSF2Layer[52] = localSF2Layer6;
    arrayOfSF2Layer[55] = localSF2Layer6;
    arrayOfSF2Layer[57] = localSF2Layer6;
    arrayOfSF2Layer[59] = localSF2Layer6;
    arrayOfSF2Layer[37] = localSF2Layer7;
    arrayOfSF2Layer[39] = localSF2Layer7;
    arrayOfSF2Layer[53] = localSF2Layer7;
    arrayOfSF2Layer[54] = localSF2Layer7;
    arrayOfSF2Layer[56] = localSF2Layer7;
    arrayOfSF2Layer[58] = localSF2Layer7;
    arrayOfSF2Layer[69] = localSF2Layer7;
    arrayOfSF2Layer[70] = localSF2Layer7;
    arrayOfSF2Layer[75] = localSF2Layer7;
    arrayOfSF2Layer[60] = localSF2Layer7;
    arrayOfSF2Layer[61] = localSF2Layer7;
    arrayOfSF2Layer[62] = localSF2Layer7;
    arrayOfSF2Layer[63] = localSF2Layer7;
    arrayOfSF2Layer[64] = localSF2Layer7;
    arrayOfSF2Layer[65] = localSF2Layer7;
    arrayOfSF2Layer[66] = localSF2Layer7;
    arrayOfSF2Layer[67] = localSF2Layer7;
    arrayOfSF2Layer[68] = localSF2Layer7;
    arrayOfSF2Layer[71] = localSF2Layer7;
    arrayOfSF2Layer[72] = localSF2Layer7;
    arrayOfSF2Layer[73] = localSF2Layer7;
    arrayOfSF2Layer[74] = localSF2Layer7;
    arrayOfSF2Layer[76] = localSF2Layer7;
    arrayOfSF2Layer[77] = localSF2Layer7;
    arrayOfSF2Layer[78] = localSF2Layer7;
    arrayOfSF2Layer[79] = localSF2Layer7;
    arrayOfSF2Layer[80] = localSF2Layer7;
    arrayOfSF2Layer[81] = localSF2Layer7;
    SF2Instrument localSF2Instrument1 = new SF2Instrument(localSF2Soundbank);
    localSF2Instrument1.setName("Standard Kit");
    localSF2Instrument1.setPatch(new ModelPatch(0, 0, true));
    localSF2Soundbank.addInstrument(localSF2Instrument1);
    for (int i = 0; i < arrayOfSF2Layer.length; i++) {
      if (arrayOfSF2Layer[i] != null)
      {
        localObject1 = new SF2InstrumentRegion();
        ((SF2InstrumentRegion)localObject1).setLayer(arrayOfSF2Layer[i]);
        ((SF2InstrumentRegion)localObject1).putBytes(43, new byte[] { (byte)i, (byte)i });
        localSF2Instrument1.getRegions().add(localObject1);
      }
    }
    SF2Layer localSF2Layer8 = new_gpiano(localSF2Soundbank);
    Object localObject1 = new_gpiano2(localSF2Soundbank);
    SF2Layer localSF2Layer9 = new_piano_hammer(localSF2Soundbank);
    SF2Layer localSF2Layer10 = new_piano1(localSF2Soundbank);
    SF2Layer localSF2Layer11 = new_epiano1(localSF2Soundbank);
    SF2Layer localSF2Layer12 = new_epiano2(localSF2Soundbank);
    SF2Layer localSF2Layer13 = new_guitar1(localSF2Soundbank);
    SF2Layer localSF2Layer14 = new_guitar_pick(localSF2Soundbank);
    SF2Layer localSF2Layer15 = new_guitar_dist(localSF2Soundbank);
    SF2Layer localSF2Layer16 = new_bass1(localSF2Soundbank);
    SF2Layer localSF2Layer17 = new_bass2(localSF2Soundbank);
    SF2Layer localSF2Layer18 = new_synthbass(localSF2Soundbank);
    SF2Layer localSF2Layer19 = new_string2(localSF2Soundbank);
    SF2Layer localSF2Layer20 = new_orchhit(localSF2Soundbank);
    SF2Layer localSF2Layer21 = new_choir(localSF2Soundbank);
    SF2Layer localSF2Layer22 = new_solostring(localSF2Soundbank);
    SF2Layer localSF2Layer23 = new_organ(localSF2Soundbank);
    SF2Layer localSF2Layer24 = new_ch_organ(localSF2Soundbank);
    SF2Layer localSF2Layer25 = new_bell(localSF2Soundbank);
    SF2Layer localSF2Layer26 = new_flute(localSF2Soundbank);
    SF2Layer localSF2Layer27 = new_timpani(localSF2Soundbank);
    SF2Layer localSF2Layer28 = new_melodic_toms(localSF2Soundbank);
    SF2Layer localSF2Layer29 = new_trumpet(localSF2Soundbank);
    SF2Layer localSF2Layer30 = new_trombone(localSF2Soundbank);
    SF2Layer localSF2Layer31 = new_brass_section(localSF2Soundbank);
    SF2Layer localSF2Layer32 = new_horn(localSF2Soundbank);
    SF2Layer localSF2Layer33 = new_sax(localSF2Soundbank);
    SF2Layer localSF2Layer34 = new_oboe(localSF2Soundbank);
    SF2Layer localSF2Layer35 = new_bassoon(localSF2Soundbank);
    SF2Layer localSF2Layer36 = new_clarinet(localSF2Soundbank);
    SF2Layer localSF2Layer37 = new_reverse_cymbal(localSF2Soundbank);
    SF2Layer localSF2Layer38 = localSF2Layer10;
    newInstrument(localSF2Soundbank, "Piano", new Patch(0, 0), new SF2Layer[] { localSF2Layer8, localSF2Layer9 });
    newInstrument(localSF2Soundbank, "Piano", new Patch(0, 1), new SF2Layer[] { localObject1, localSF2Layer9 });
    newInstrument(localSF2Soundbank, "Piano", new Patch(0, 2), new SF2Layer[] { localSF2Layer10 });
    SF2Instrument localSF2Instrument2 = newInstrument(localSF2Soundbank, "Honky-tonk Piano", new Patch(0, 3), new SF2Layer[] { localSF2Layer10, localSF2Layer10 });
    SF2InstrumentRegion localSF2InstrumentRegion = (SF2InstrumentRegion)localSF2Instrument2.getRegions().get(0);
    localSF2InstrumentRegion.putInteger(8, 80);
    localSF2InstrumentRegion.putInteger(52, 30);
    localSF2InstrumentRegion = (SF2InstrumentRegion)localSF2Instrument2.getRegions().get(1);
    localSF2InstrumentRegion.putInteger(8, 30);
    newInstrument(localSF2Soundbank, "Rhodes", new Patch(0, 4), new SF2Layer[] { localSF2Layer12 });
    newInstrument(localSF2Soundbank, "Rhodes", new Patch(0, 5), new SF2Layer[] { localSF2Layer12 });
    newInstrument(localSF2Soundbank, "Clavinet", new Patch(0, 6), new SF2Layer[] { localSF2Layer11 });
    newInstrument(localSF2Soundbank, "Clavinet", new Patch(0, 7), new SF2Layer[] { localSF2Layer11 });
    newInstrument(localSF2Soundbank, "Rhodes", new Patch(0, 8), new SF2Layer[] { localSF2Layer12 });
    newInstrument(localSF2Soundbank, "Bell", new Patch(0, 9), new SF2Layer[] { localSF2Layer25 });
    newInstrument(localSF2Soundbank, "Bell", new Patch(0, 10), new SF2Layer[] { localSF2Layer25 });
    newInstrument(localSF2Soundbank, "Vibraphone", new Patch(0, 11), new SF2Layer[] { localSF2Layer25 });
    newInstrument(localSF2Soundbank, "Marimba", new Patch(0, 12), new SF2Layer[] { localSF2Layer25 });
    newInstrument(localSF2Soundbank, "Marimba", new Patch(0, 13), new SF2Layer[] { localSF2Layer25 });
    newInstrument(localSF2Soundbank, "Bell", new Patch(0, 14), new SF2Layer[] { localSF2Layer25 });
    newInstrument(localSF2Soundbank, "Rock Organ", new Patch(0, 15), new SF2Layer[] { localSF2Layer23 });
    newInstrument(localSF2Soundbank, "Rock Organ", new Patch(0, 16), new SF2Layer[] { localSF2Layer23 });
    newInstrument(localSF2Soundbank, "Perc Organ", new Patch(0, 17), new SF2Layer[] { localSF2Layer23 });
    newInstrument(localSF2Soundbank, "Rock Organ", new Patch(0, 18), new SF2Layer[] { localSF2Layer23 });
    newInstrument(localSF2Soundbank, "Church Organ", new Patch(0, 19), new SF2Layer[] { localSF2Layer24 });
    newInstrument(localSF2Soundbank, "Accordion", new Patch(0, 20), new SF2Layer[] { localSF2Layer23 });
    newInstrument(localSF2Soundbank, "Accordion", new Patch(0, 21), new SF2Layer[] { localSF2Layer23 });
    newInstrument(localSF2Soundbank, "Accordion", new Patch(0, 22), new SF2Layer[] { localSF2Layer23 });
    newInstrument(localSF2Soundbank, "Accordion", new Patch(0, 23), new SF2Layer[] { localSF2Layer23 });
    newInstrument(localSF2Soundbank, "Guitar", new Patch(0, 24), new SF2Layer[] { localSF2Layer13, localSF2Layer14 });
    newInstrument(localSF2Soundbank, "Guitar", new Patch(0, 25), new SF2Layer[] { localSF2Layer13, localSF2Layer14 });
    newInstrument(localSF2Soundbank, "Guitar", new Patch(0, 26), new SF2Layer[] { localSF2Layer13, localSF2Layer14 });
    newInstrument(localSF2Soundbank, "Guitar", new Patch(0, 27), new SF2Layer[] { localSF2Layer13, localSF2Layer14 });
    newInstrument(localSF2Soundbank, "Guitar", new Patch(0, 28), new SF2Layer[] { localSF2Layer13, localSF2Layer14 });
    newInstrument(localSF2Soundbank, "Distorted Guitar", new Patch(0, 29), new SF2Layer[] { localSF2Layer15 });
    newInstrument(localSF2Soundbank, "Distorted Guitar", new Patch(0, 30), new SF2Layer[] { localSF2Layer15 });
    newInstrument(localSF2Soundbank, "Guitar", new Patch(0, 31), new SF2Layer[] { localSF2Layer13, localSF2Layer14 });
    newInstrument(localSF2Soundbank, "Finger Bass", new Patch(0, 32), new SF2Layer[] { localSF2Layer16 });
    newInstrument(localSF2Soundbank, "Finger Bass", new Patch(0, 33), new SF2Layer[] { localSF2Layer16 });
    newInstrument(localSF2Soundbank, "Finger Bass", new Patch(0, 34), new SF2Layer[] { localSF2Layer16 });
    newInstrument(localSF2Soundbank, "Frettless Bass", new Patch(0, 35), new SF2Layer[] { localSF2Layer17 });
    newInstrument(localSF2Soundbank, "Frettless Bass", new Patch(0, 36), new SF2Layer[] { localSF2Layer17 });
    newInstrument(localSF2Soundbank, "Frettless Bass", new Patch(0, 37), new SF2Layer[] { localSF2Layer17 });
    newInstrument(localSF2Soundbank, "Synth Bass1", new Patch(0, 38), new SF2Layer[] { localSF2Layer18 });
    newInstrument(localSF2Soundbank, "Synth Bass2", new Patch(0, 39), new SF2Layer[] { localSF2Layer18 });
    newInstrument(localSF2Soundbank, "Solo String", new Patch(0, 40), new SF2Layer[] { localSF2Layer19, localSF2Layer22 });
    newInstrument(localSF2Soundbank, "Solo String", new Patch(0, 41), new SF2Layer[] { localSF2Layer19, localSF2Layer22 });
    newInstrument(localSF2Soundbank, "Solo String", new Patch(0, 42), new SF2Layer[] { localSF2Layer19, localSF2Layer22 });
    newInstrument(localSF2Soundbank, "Solo String", new Patch(0, 43), new SF2Layer[] { localSF2Layer19, localSF2Layer22 });
    newInstrument(localSF2Soundbank, "Solo String", new Patch(0, 44), new SF2Layer[] { localSF2Layer19, localSF2Layer22 });
    newInstrument(localSF2Soundbank, "Def", new Patch(0, 45), new SF2Layer[] { localSF2Layer38 });
    newInstrument(localSF2Soundbank, "Harp", new Patch(0, 46), new SF2Layer[] { localSF2Layer25 });
    newInstrument(localSF2Soundbank, "Timpani", new Patch(0, 47), new SF2Layer[] { localSF2Layer27 });
    newInstrument(localSF2Soundbank, "Strings", new Patch(0, 48), new SF2Layer[] { localSF2Layer19 });
    localSF2Instrument2 = newInstrument(localSF2Soundbank, "Slow Strings", new Patch(0, 49), new SF2Layer[] { localSF2Layer19 });
    localSF2InstrumentRegion = (SF2InstrumentRegion)localSF2Instrument2.getRegions().get(0);
    localSF2InstrumentRegion.putInteger(34, 2500);
    localSF2InstrumentRegion.putInteger(38, 2000);
    newInstrument(localSF2Soundbank, "Synth Strings", new Patch(0, 50), new SF2Layer[] { localSF2Layer19 });
    newInstrument(localSF2Soundbank, "Synth Strings", new Patch(0, 51), new SF2Layer[] { localSF2Layer19 });
    newInstrument(localSF2Soundbank, "Choir", new Patch(0, 52), new SF2Layer[] { localSF2Layer21 });
    newInstrument(localSF2Soundbank, "Choir", new Patch(0, 53), new SF2Layer[] { localSF2Layer21 });
    newInstrument(localSF2Soundbank, "Choir", new Patch(0, 54), new SF2Layer[] { localSF2Layer21 });
    Object localObject2 = newInstrument(localSF2Soundbank, "Orch Hit", new Patch(0, 55), new SF2Layer[] { localSF2Layer20, localSF2Layer20, localSF2Layer27 });
    localSF2InstrumentRegion = (SF2InstrumentRegion)((SF2Instrument)localObject2).getRegions().get(0);
    localSF2InstrumentRegion.putInteger(51, -12);
    localSF2InstrumentRegion.putInteger(48, -100);
    newInstrument(localSF2Soundbank, "Trumpet", new Patch(0, 56), new SF2Layer[] { localSF2Layer29 });
    newInstrument(localSF2Soundbank, "Trombone", new Patch(0, 57), new SF2Layer[] { localSF2Layer30 });
    newInstrument(localSF2Soundbank, "Trombone", new Patch(0, 58), new SF2Layer[] { localSF2Layer30 });
    newInstrument(localSF2Soundbank, "Trumpet", new Patch(0, 59), new SF2Layer[] { localSF2Layer29 });
    newInstrument(localSF2Soundbank, "Horn", new Patch(0, 60), new SF2Layer[] { localSF2Layer32 });
    newInstrument(localSF2Soundbank, "Brass Section", new Patch(0, 61), new SF2Layer[] { localSF2Layer31 });
    newInstrument(localSF2Soundbank, "Brass Section", new Patch(0, 62), new SF2Layer[] { localSF2Layer31 });
    newInstrument(localSF2Soundbank, "Brass Section", new Patch(0, 63), new SF2Layer[] { localSF2Layer31 });
    newInstrument(localSF2Soundbank, "Sax", new Patch(0, 64), new SF2Layer[] { localSF2Layer33 });
    newInstrument(localSF2Soundbank, "Sax", new Patch(0, 65), new SF2Layer[] { localSF2Layer33 });
    newInstrument(localSF2Soundbank, "Sax", new Patch(0, 66), new SF2Layer[] { localSF2Layer33 });
    newInstrument(localSF2Soundbank, "Sax", new Patch(0, 67), new SF2Layer[] { localSF2Layer33 });
    newInstrument(localSF2Soundbank, "Oboe", new Patch(0, 68), new SF2Layer[] { localSF2Layer34 });
    newInstrument(localSF2Soundbank, "Horn", new Patch(0, 69), new SF2Layer[] { localSF2Layer32 });
    newInstrument(localSF2Soundbank, "Bassoon", new Patch(0, 70), new SF2Layer[] { localSF2Layer35 });
    newInstrument(localSF2Soundbank, "Clarinet", new Patch(0, 71), new SF2Layer[] { localSF2Layer36 });
    newInstrument(localSF2Soundbank, "Flute", new Patch(0, 72), new SF2Layer[] { localSF2Layer26 });
    newInstrument(localSF2Soundbank, "Flute", new Patch(0, 73), new SF2Layer[] { localSF2Layer26 });
    newInstrument(localSF2Soundbank, "Flute", new Patch(0, 74), new SF2Layer[] { localSF2Layer26 });
    newInstrument(localSF2Soundbank, "Flute", new Patch(0, 75), new SF2Layer[] { localSF2Layer26 });
    newInstrument(localSF2Soundbank, "Flute", new Patch(0, 76), new SF2Layer[] { localSF2Layer26 });
    newInstrument(localSF2Soundbank, "Flute", new Patch(0, 77), new SF2Layer[] { localSF2Layer26 });
    newInstrument(localSF2Soundbank, "Flute", new Patch(0, 78), new SF2Layer[] { localSF2Layer26 });
    newInstrument(localSF2Soundbank, "Flute", new Patch(0, 79), new SF2Layer[] { localSF2Layer26 });
    newInstrument(localSF2Soundbank, "Organ", new Patch(0, 80), new SF2Layer[] { localSF2Layer23 });
    newInstrument(localSF2Soundbank, "Organ", new Patch(0, 81), new SF2Layer[] { localSF2Layer23 });
    newInstrument(localSF2Soundbank, "Flute", new Patch(0, 82), new SF2Layer[] { localSF2Layer26 });
    newInstrument(localSF2Soundbank, "Organ", new Patch(0, 83), new SF2Layer[] { localSF2Layer23 });
    newInstrument(localSF2Soundbank, "Organ", new Patch(0, 84), new SF2Layer[] { localSF2Layer23 });
    newInstrument(localSF2Soundbank, "Choir", new Patch(0, 85), new SF2Layer[] { localSF2Layer21 });
    newInstrument(localSF2Soundbank, "Organ", new Patch(0, 86), new SF2Layer[] { localSF2Layer23 });
    newInstrument(localSF2Soundbank, "Organ", new Patch(0, 87), new SF2Layer[] { localSF2Layer23 });
    newInstrument(localSF2Soundbank, "Synth Strings", new Patch(0, 88), new SF2Layer[] { localSF2Layer19 });
    newInstrument(localSF2Soundbank, "Organ", new Patch(0, 89), new SF2Layer[] { localSF2Layer23 });
    newInstrument(localSF2Soundbank, "Def", new Patch(0, 90), new SF2Layer[] { localSF2Layer38 });
    newInstrument(localSF2Soundbank, "Choir", new Patch(0, 91), new SF2Layer[] { localSF2Layer21 });
    newInstrument(localSF2Soundbank, "Organ", new Patch(0, 92), new SF2Layer[] { localSF2Layer23 });
    newInstrument(localSF2Soundbank, "Organ", new Patch(0, 93), new SF2Layer[] { localSF2Layer23 });
    newInstrument(localSF2Soundbank, "Organ", new Patch(0, 94), new SF2Layer[] { localSF2Layer23 });
    newInstrument(localSF2Soundbank, "Organ", new Patch(0, 95), new SF2Layer[] { localSF2Layer23 });
    newInstrument(localSF2Soundbank, "Organ", new Patch(0, 96), new SF2Layer[] { localSF2Layer23 });
    newInstrument(localSF2Soundbank, "Organ", new Patch(0, 97), new SF2Layer[] { localSF2Layer23 });
    newInstrument(localSF2Soundbank, "Bell", new Patch(0, 98), new SF2Layer[] { localSF2Layer25 });
    newInstrument(localSF2Soundbank, "Organ", new Patch(0, 99), new SF2Layer[] { localSF2Layer23 });
    newInstrument(localSF2Soundbank, "Organ", new Patch(0, 100), new SF2Layer[] { localSF2Layer23 });
    newInstrument(localSF2Soundbank, "Organ", new Patch(0, 101), new SF2Layer[] { localSF2Layer23 });
    newInstrument(localSF2Soundbank, "Def", new Patch(0, 102), new SF2Layer[] { localSF2Layer38 });
    newInstrument(localSF2Soundbank, "Synth Strings", new Patch(0, 103), new SF2Layer[] { localSF2Layer19 });
    newInstrument(localSF2Soundbank, "Def", new Patch(0, 104), new SF2Layer[] { localSF2Layer38 });
    newInstrument(localSF2Soundbank, "Def", new Patch(0, 105), new SF2Layer[] { localSF2Layer38 });
    newInstrument(localSF2Soundbank, "Def", new Patch(0, 106), new SF2Layer[] { localSF2Layer38 });
    newInstrument(localSF2Soundbank, "Def", new Patch(0, 107), new SF2Layer[] { localSF2Layer38 });
    newInstrument(localSF2Soundbank, "Marimba", new Patch(0, 108), new SF2Layer[] { localSF2Layer25 });
    newInstrument(localSF2Soundbank, "Sax", new Patch(0, 109), new SF2Layer[] { localSF2Layer33 });
    newInstrument(localSF2Soundbank, "Solo String", new Patch(0, 110), new SF2Layer[] { localSF2Layer19, localSF2Layer22 });
    newInstrument(localSF2Soundbank, "Oboe", new Patch(0, 111), new SF2Layer[] { localSF2Layer34 });
    newInstrument(localSF2Soundbank, "Bell", new Patch(0, 112), new SF2Layer[] { localSF2Layer25 });
    newInstrument(localSF2Soundbank, "Melodic Toms", new Patch(0, 113), new SF2Layer[] { localSF2Layer28 });
    newInstrument(localSF2Soundbank, "Marimba", new Patch(0, 114), new SF2Layer[] { localSF2Layer25 });
    newInstrument(localSF2Soundbank, "Melodic Toms", new Patch(0, 115), new SF2Layer[] { localSF2Layer28 });
    newInstrument(localSF2Soundbank, "Melodic Toms", new Patch(0, 116), new SF2Layer[] { localSF2Layer28 });
    newInstrument(localSF2Soundbank, "Melodic Toms", new Patch(0, 117), new SF2Layer[] { localSF2Layer28 });
    newInstrument(localSF2Soundbank, "Reverse Cymbal", new Patch(0, 118), new SF2Layer[] { localSF2Layer37 });
    newInstrument(localSF2Soundbank, "Reverse Cymbal", new Patch(0, 119), new SF2Layer[] { localSF2Layer37 });
    newInstrument(localSF2Soundbank, "Guitar", new Patch(0, 120), new SF2Layer[] { localSF2Layer13 });
    newInstrument(localSF2Soundbank, "Def", new Patch(0, 121), new SF2Layer[] { localSF2Layer38 });
    localObject2 = newInstrument(localSF2Soundbank, "Seashore/Reverse Cymbal", new Patch(0, 122), new SF2Layer[] { localSF2Layer37 });
    localSF2InstrumentRegion = (SF2InstrumentRegion)((SF2Instrument)localObject2).getRegions().get(0);
    localSF2InstrumentRegion.putInteger(37, 1000);
    localSF2InstrumentRegion.putInteger(36, 18500);
    localSF2InstrumentRegion.putInteger(38, 4500);
    localSF2InstrumentRegion.putInteger(8, 61036);
    localObject2 = newInstrument(localSF2Soundbank, "Bird/Flute", new Patch(0, 123), new SF2Layer[] { localSF2Layer26 });
    localSF2InstrumentRegion = (SF2InstrumentRegion)((SF2Instrument)localObject2).getRegions().get(0);
    localSF2InstrumentRegion.putInteger(51, 24);
    localSF2InstrumentRegion.putInteger(36, 62536);
    localSF2InstrumentRegion.putInteger(37, 1000);
    newInstrument(localSF2Soundbank, "Def", new Patch(0, 124), new SF2Layer[] { localSF2Layer7 });
    localObject2 = newInstrument(localSF2Soundbank, "Seashore/Reverse Cymbal", new Patch(0, 125), new SF2Layer[] { localSF2Layer37 });
    localSF2InstrumentRegion = (SF2InstrumentRegion)((SF2Instrument)localObject2).getRegions().get(0);
    localSF2InstrumentRegion.putInteger(37, 1000);
    localSF2InstrumentRegion.putInteger(36, 18500);
    localSF2InstrumentRegion.putInteger(38, 4500);
    localSF2InstrumentRegion.putInteger(8, 61036);
    newInstrument(localSF2Soundbank, "Applause/crash_cymbal", new Patch(0, 126), new SF2Layer[] { localSF2Layer6 });
    newInstrument(localSF2Soundbank, "Gunshot/side_stick", new Patch(0, 127), new SF2Layer[] { localSF2Layer7 });
    for (Object localObject3 : localSF2Soundbank.getInstruments())
    {
      Patch localPatch = ((SF2Instrument)localObject3).getPatch();
      if ((!(localPatch instanceof ModelPatch)) || (!((ModelPatch)localPatch).isPercussion())) {
        ((SF2Instrument)localObject3).setName(general_midi_instruments[localPatch.getProgram()]);
      }
    }
    return localSF2Soundbank;
  }
  
  public static SF2Layer new_bell(SF2Soundbank paramSF2Soundbank)
  {
    Random localRandom = new Random(102030201L);
    int i = 8;
    int j = 4096 * i;
    double[] arrayOfDouble = new double[j * 2];
    double d1 = i * 25;
    double d2 = 0.01D;
    double d3 = 0.05D;
    double d4 = 0.2D;
    double d5 = 1.0E-5D;
    double d6 = d4;
    double d7 = Math.pow(d5 / d4, 0.025D);
    for (int k = 0; k < 40; k++)
    {
      double d8 = 1.0D + (localRandom.nextDouble() * 2.0D - 1.0D) * 0.01D;
      double d9 = d2 + (d3 - d2) * (k / 40.0D);
      complexGaussianDist(arrayOfDouble, d1 * (k + 1) * d8, d9, d6);
      d6 *= d7;
    }
    SF2Sample localSF2Sample = newSimpleFFTSample(paramSF2Soundbank, "EPiano", arrayOfDouble, d1);
    SF2Layer localSF2Layer = newLayer(paramSF2Soundbank, "EPiano", localSF2Sample);
    SF2Region localSF2Region = (SF2Region)localSF2Layer.getRegions().get(0);
    localSF2Region.putInteger(54, 1);
    localSF2Region.putInteger(34, 53536);
    localSF2Region.putInteger(38, 0);
    localSF2Region.putInteger(36, 4000);
    localSF2Region.putInteger(37, 1000);
    localSF2Region.putInteger(26, 1200);
    localSF2Region.putInteger(30, 12000);
    localSF2Region.putInteger(11, 56536);
    localSF2Region.putInteger(8, 16000);
    return localSF2Layer;
  }
  
  public static SF2Layer new_guitar1(SF2Soundbank paramSF2Soundbank)
  {
    int i = 8;
    int j = 4096 * i;
    double[] arrayOfDouble1 = new double[j * 2];
    double d1 = i * 25;
    double d2 = 0.01D;
    double d3 = 0.01D;
    double d4 = 2.0D;
    double d5 = 0.01D;
    double d6 = d4;
    double d7 = Math.pow(d5 / d4, 0.025D);
    double[] arrayOfDouble2 = new double[40];
    for (int k = 0; k < 40; k++)
    {
      arrayOfDouble2[k] = d6;
      d6 *= d7;
    }
    arrayOfDouble2[0] = 2.0D;
    arrayOfDouble2[1] = 0.5D;
    arrayOfDouble2[2] = 0.45D;
    arrayOfDouble2[3] = 0.2D;
    arrayOfDouble2[4] = 1.0D;
    arrayOfDouble2[5] = 0.5D;
    arrayOfDouble2[6] = 2.0D;
    arrayOfDouble2[7] = 1.0D;
    arrayOfDouble2[8] = 0.5D;
    arrayOfDouble2[9] = 1.0D;
    arrayOfDouble2[9] = 0.5D;
    arrayOfDouble2[10] = 0.2D;
    arrayOfDouble2[11] = 1.0D;
    arrayOfDouble2[12] = 0.7D;
    arrayOfDouble2[13] = 0.5D;
    arrayOfDouble2[14] = 1.0D;
    for (k = 0; k < 40; k++)
    {
      double d8 = d2 + (d3 - d2) * (k / 40.0D);
      complexGaussianDist(arrayOfDouble1, d1 * (k + 1), d8, arrayOfDouble2[k]);
    }
    SF2Sample localSF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Guitar", arrayOfDouble1, d1);
    SF2Layer localSF2Layer = newLayer(paramSF2Soundbank, "Guitar", localSF2Sample);
    SF2Region localSF2Region = (SF2Region)localSF2Layer.getRegions().get(0);
    localSF2Region.putInteger(54, 1);
    localSF2Region.putInteger(34, 53536);
    localSF2Region.putInteger(38, 0);
    localSF2Region.putInteger(36, 2400);
    localSF2Region.putInteger(37, 1000);
    localSF2Region.putInteger(26, -100);
    localSF2Region.putInteger(30, 12000);
    localSF2Region.putInteger(11, 59536);
    localSF2Region.putInteger(8, 16000);
    localSF2Region.putInteger(48, -20);
    return localSF2Layer;
  }
  
  public static SF2Layer new_guitar_dist(SF2Soundbank paramSF2Soundbank)
  {
    int i = 8;
    int j = 4096 * i;
    double[] arrayOfDouble1 = new double[j * 2];
    double d1 = i * 25;
    double d2 = 0.01D;
    double d3 = 0.01D;
    double d4 = 2.0D;
    double d5 = 0.01D;
    double d6 = d4;
    double d7 = Math.pow(d5 / d4, 0.025D);
    double[] arrayOfDouble2 = new double[40];
    for (int k = 0; k < 40; k++)
    {
      arrayOfDouble2[k] = d6;
      d6 *= d7;
    }
    arrayOfDouble2[0] = 5.0D;
    arrayOfDouble2[1] = 2.0D;
    arrayOfDouble2[2] = 0.45D;
    arrayOfDouble2[3] = 0.2D;
    arrayOfDouble2[4] = 1.0D;
    arrayOfDouble2[5] = 0.5D;
    arrayOfDouble2[6] = 2.0D;
    arrayOfDouble2[7] = 1.0D;
    arrayOfDouble2[8] = 0.5D;
    arrayOfDouble2[9] = 1.0D;
    arrayOfDouble2[9] = 0.5D;
    arrayOfDouble2[10] = 0.2D;
    arrayOfDouble2[11] = 1.0D;
    arrayOfDouble2[12] = 0.7D;
    arrayOfDouble2[13] = 0.5D;
    arrayOfDouble2[14] = 1.0D;
    for (k = 0; k < 40; k++)
    {
      double d8 = d2 + (d3 - d2) * (k / 40.0D);
      complexGaussianDist(arrayOfDouble1, d1 * (k + 1), d8, arrayOfDouble2[k]);
    }
    SF2Sample localSF2Sample = newSimpleFFTSample_dist(paramSF2Soundbank, "Distorted Guitar", arrayOfDouble1, d1, 10000.0D);
    SF2Layer localSF2Layer = newLayer(paramSF2Soundbank, "Distorted Guitar", localSF2Sample);
    SF2Region localSF2Region = (SF2Region)localSF2Layer.getRegions().get(0);
    localSF2Region.putInteger(54, 1);
    localSF2Region.putInteger(34, 53536);
    localSF2Region.putInteger(38, 0);
    localSF2Region.putInteger(8, 8000);
    return localSF2Layer;
  }
  
  public static SF2Layer new_guitar_pick(SF2Soundbank paramSF2Soundbank)
  {
    int i = 2;
    int j = 4096 * i;
    Object localObject2 = new double[2 * j];
    Object localObject3 = new Random(3049912L);
    for (int k = 0; k < localObject2.length; k += 2) {
      localObject2[k] = (2.0D * (((Random)localObject3).nextDouble() - 0.5D));
    }
    fft((double[])localObject2);
    for (k = j / 2; k < localObject2.length; k++) {
      localObject2[k] = 0.0D;
    }
    for (k = 0; k < 2048 * i; k++) {
      localObject2[k] *= (Math.exp(-Math.abs((k - 23) / i) * 1.2D) + Math.exp(-Math.abs((k - 40) / i) * 0.9D));
    }
    randomPhase((double[])localObject2, new Random(3049912L));
    ifft((double[])localObject2);
    normalize((double[])localObject2, 0.8D);
    localObject2 = realPart((double[])localObject2);
    double d = 1.0D;
    for (int m = 0; m < localObject2.length; m++)
    {
      localObject2[m] *= d;
      d *= 0.9994D;
    }
    Object localObject1 = localObject2;
    fadeUp((double[])localObject2, 80);
    SF2Sample localSF2Sample = newSimpleDrumSample(paramSF2Soundbank, "Guitar Noise", (double[])localObject1);
    SF2Layer localSF2Layer = new SF2Layer(paramSF2Soundbank);
    localSF2Layer.setName("Guitar Noise");
    localObject2 = new SF2GlobalRegion();
    localSF2Layer.setGlobalZone((SF2GlobalRegion)localObject2);
    paramSF2Soundbank.addResource(localSF2Layer);
    localObject3 = new SF2LayerRegion();
    ((SF2LayerRegion)localObject3).putInteger(38, 12000);
    ((SF2LayerRegion)localObject3).setSample(localSF2Sample);
    localSF2Layer.getRegions().add(localObject3);
    return localSF2Layer;
  }
  
  public static SF2Layer new_gpiano(SF2Soundbank paramSF2Soundbank)
  {
    int i = 8;
    int j = 4096 * i;
    double[] arrayOfDouble1 = new double[j * 2];
    double d1 = i * 25;
    double d2 = 0.2D;
    double d3 = 0.001D;
    double d4 = d2;
    double d5 = Math.pow(d3 / d2, 0.06666666666666667D);
    double[] arrayOfDouble2 = new double[30];
    for (int k = 0; k < 30; k++)
    {
      arrayOfDouble2[k] = d4;
      d4 *= d5;
    }
    arrayOfDouble2[0] *= 2.0D;
    arrayOfDouble2[4] *= 2.0D;
    arrayOfDouble2[12] *= 0.9D;
    arrayOfDouble2[13] *= 0.7D;
    for (k = 14; k < 30; k++) {
      arrayOfDouble2[k] *= 0.5D;
    }
    for (k = 0; k < 30; k++)
    {
      double d6 = 0.2D;
      double d7 = arrayOfDouble2[k];
      if (k > 10)
      {
        d6 = 5.0D;
        d7 *= 10.0D;
      }
      int m = 0;
      if (k > 5) {
        m = (k - 5) * 7;
      }
      complexGaussianDist(arrayOfDouble1, d1 * (k + 1) + m, d6, d7);
    }
    SF2Sample localSF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Grand Piano", arrayOfDouble1, d1, 200);
    SF2Layer localSF2Layer = newLayer(paramSF2Soundbank, "Grand Piano", localSF2Sample);
    SF2Region localSF2Region = (SF2Region)localSF2Layer.getRegions().get(0);
    localSF2Region.putInteger(54, 1);
    localSF2Region.putInteger(34, 58536);
    localSF2Region.putInteger(38, 0);
    localSF2Region.putInteger(36, 4000);
    localSF2Region.putInteger(37, 1000);
    localSF2Region.putInteger(26, 59536);
    localSF2Region.putInteger(30, 12000);
    localSF2Region.putInteger(11, 60036);
    localSF2Region.putInteger(8, 18000);
    return localSF2Layer;
  }
  
  public static SF2Layer new_gpiano2(SF2Soundbank paramSF2Soundbank)
  {
    int i = 8;
    int j = 4096 * i;
    double[] arrayOfDouble1 = new double[j * 2];
    double d1 = i * 25;
    double d2 = 0.2D;
    double d3 = 0.001D;
    double d4 = d2;
    double d5 = Math.pow(d3 / d2, 0.05D);
    double[] arrayOfDouble2 = new double[30];
    for (int k = 0; k < 30; k++)
    {
      arrayOfDouble2[k] = d4;
      d4 *= d5;
    }
    arrayOfDouble2[0] *= 1.0D;
    arrayOfDouble2[4] *= 2.0D;
    arrayOfDouble2[12] *= 0.9D;
    arrayOfDouble2[13] *= 0.7D;
    for (k = 14; k < 30; k++) {
      arrayOfDouble2[k] *= 0.5D;
    }
    for (k = 0; k < 30; k++)
    {
      double d6 = 0.2D;
      double d7 = arrayOfDouble2[k];
      if (k > 10)
      {
        d6 = 5.0D;
        d7 *= 10.0D;
      }
      int m = 0;
      if (k > 5) {
        m = (k - 5) * 7;
      }
      complexGaussianDist(arrayOfDouble1, d1 * (k + 1) + m, d6, d7);
    }
    SF2Sample localSF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Grand Piano", arrayOfDouble1, d1, 200);
    SF2Layer localSF2Layer = newLayer(paramSF2Soundbank, "Grand Piano", localSF2Sample);
    SF2Region localSF2Region = (SF2Region)localSF2Layer.getRegions().get(0);
    localSF2Region.putInteger(54, 1);
    localSF2Region.putInteger(34, 58536);
    localSF2Region.putInteger(38, 0);
    localSF2Region.putInteger(36, 4000);
    localSF2Region.putInteger(37, 1000);
    localSF2Region.putInteger(26, 59536);
    localSF2Region.putInteger(30, 12000);
    localSF2Region.putInteger(11, 60036);
    localSF2Region.putInteger(8, 18000);
    return localSF2Layer;
  }
  
  public static SF2Layer new_piano_hammer(SF2Soundbank paramSF2Soundbank)
  {
    int i = 2;
    int j = 4096 * i;
    Object localObject2 = new double[2 * j];
    Object localObject3 = new Random(3049912L);
    for (int k = 0; k < localObject2.length; k += 2) {
      localObject2[k] = (2.0D * (((Random)localObject3).nextDouble() - 0.5D));
    }
    fft((double[])localObject2);
    for (k = j / 2; k < localObject2.length; k++) {
      localObject2[k] = 0.0D;
    }
    for (k = 0; k < 2048 * i; k++) {
      localObject2[k] *= Math.exp(-Math.abs((k - 37) / i) * 0.05D);
    }
    randomPhase((double[])localObject2, new Random(3049912L));
    ifft((double[])localObject2);
    normalize((double[])localObject2, 0.6D);
    localObject2 = realPart((double[])localObject2);
    double d = 1.0D;
    for (int m = 0; m < localObject2.length; m++)
    {
      localObject2[m] *= d;
      d *= 0.9997D;
    }
    Object localObject1 = localObject2;
    fadeUp((double[])localObject2, 80);
    SF2Sample localSF2Sample = newSimpleDrumSample(paramSF2Soundbank, "Piano Hammer", (double[])localObject1);
    SF2Layer localSF2Layer = new SF2Layer(paramSF2Soundbank);
    localSF2Layer.setName("Piano Hammer");
    localObject2 = new SF2GlobalRegion();
    localSF2Layer.setGlobalZone((SF2GlobalRegion)localObject2);
    paramSF2Soundbank.addResource(localSF2Layer);
    localObject3 = new SF2LayerRegion();
    ((SF2LayerRegion)localObject3).putInteger(38, 12000);
    ((SF2LayerRegion)localObject3).setSample(localSF2Sample);
    localSF2Layer.getRegions().add(localObject3);
    return localSF2Layer;
  }
  
  public static SF2Layer new_piano1(SF2Soundbank paramSF2Soundbank)
  {
    int i = 8;
    int j = 4096 * i;
    double[] arrayOfDouble1 = new double[j * 2];
    double d1 = i * 25;
    double d2 = 0.2D;
    double d3 = 1.0E-4D;
    double d4 = d2;
    double d5 = Math.pow(d3 / d2, 0.025D);
    double[] arrayOfDouble2 = new double[30];
    for (int k = 0; k < 30; k++)
    {
      arrayOfDouble2[k] = d4;
      d4 *= d5;
    }
    arrayOfDouble2[0] *= 5.0D;
    arrayOfDouble2[2] *= 0.1D;
    arrayOfDouble2[7] *= 5.0D;
    for (k = 0; k < 30; k++)
    {
      double d6 = 0.2D;
      double d7 = arrayOfDouble2[k];
      if (k > 12)
      {
        d6 = 5.0D;
        d7 *= 10.0D;
      }
      int m = 0;
      if (k > 5) {
        m = (k - 5) * 7;
      }
      complexGaussianDist(arrayOfDouble1, d1 * (k + 1) + m, d6, d7);
    }
    complexGaussianDist(arrayOfDouble1, d1 * 15.5D, 1.0D, 0.1D);
    complexGaussianDist(arrayOfDouble1, d1 * 17.5D, 1.0D, 0.01D);
    SF2Sample localSF2Sample = newSimpleFFTSample(paramSF2Soundbank, "EPiano", arrayOfDouble1, d1, 200);
    SF2Layer localSF2Layer = newLayer(paramSF2Soundbank, "EPiano", localSF2Sample);
    SF2Region localSF2Region = (SF2Region)localSF2Layer.getRegions().get(0);
    localSF2Region.putInteger(54, 1);
    localSF2Region.putInteger(34, 53536);
    localSF2Region.putInteger(38, 0);
    localSF2Region.putInteger(36, 4000);
    localSF2Region.putInteger(37, 1000);
    localSF2Region.putInteger(26, 64336);
    localSF2Region.putInteger(30, 12000);
    localSF2Region.putInteger(11, 60036);
    localSF2Region.putInteger(8, 16000);
    return localSF2Layer;
  }
  
  public static SF2Layer new_epiano1(SF2Soundbank paramSF2Soundbank)
  {
    Random localRandom = new Random(302030201L);
    int i = 8;
    int j = 4096 * i;
    double[] arrayOfDouble = new double[j * 2];
    double d1 = i * 25;
    double d2 = 0.05D;
    double d3 = 0.05D;
    double d4 = 0.2D;
    double d5 = 1.0E-4D;
    double d6 = d4;
    double d7 = Math.pow(d5 / d4, 0.025D);
    for (int k = 0; k < 40; k++)
    {
      double d8 = 1.0D + (localRandom.nextDouble() * 2.0D - 1.0D) * 1.0E-4D;
      double d9 = d2 + (d3 - d2) * (k / 40.0D);
      complexGaussianDist(arrayOfDouble, d1 * (k + 1) * d8, d9, d6);
      d6 *= d7;
    }
    SF2Sample localSF2Sample = newSimpleFFTSample(paramSF2Soundbank, "EPiano", arrayOfDouble, d1);
    SF2Layer localSF2Layer = newLayer(paramSF2Soundbank, "EPiano", localSF2Sample);
    SF2Region localSF2Region = (SF2Region)localSF2Layer.getRegions().get(0);
    localSF2Region.putInteger(54, 1);
    localSF2Region.putInteger(34, 53536);
    localSF2Region.putInteger(38, 0);
    localSF2Region.putInteger(36, 4000);
    localSF2Region.putInteger(37, 1000);
    localSF2Region.putInteger(26, 1200);
    localSF2Region.putInteger(30, 12000);
    localSF2Region.putInteger(11, 56536);
    localSF2Region.putInteger(8, 16000);
    return localSF2Layer;
  }
  
  public static SF2Layer new_epiano2(SF2Soundbank paramSF2Soundbank)
  {
    Random localRandom = new Random(302030201L);
    int i = 8;
    int j = 4096 * i;
    double[] arrayOfDouble = new double[j * 2];
    double d1 = i * 25;
    double d2 = 0.01D;
    double d3 = 0.05D;
    double d4 = 0.2D;
    double d5 = 1.0E-5D;
    double d6 = d4;
    double d7 = Math.pow(d5 / d4, 0.025D);
    for (int k = 0; k < 40; k++)
    {
      double d8 = 1.0D + (localRandom.nextDouble() * 2.0D - 1.0D) * 1.0E-4D;
      double d9 = d2 + (d3 - d2) * (k / 40.0D);
      complexGaussianDist(arrayOfDouble, d1 * (k + 1) * d8, d9, d6);
      d6 *= d7;
    }
    SF2Sample localSF2Sample = newSimpleFFTSample(paramSF2Soundbank, "EPiano", arrayOfDouble, d1);
    SF2Layer localSF2Layer = newLayer(paramSF2Soundbank, "EPiano", localSF2Sample);
    SF2Region localSF2Region = (SF2Region)localSF2Layer.getRegions().get(0);
    localSF2Region.putInteger(54, 1);
    localSF2Region.putInteger(34, 53536);
    localSF2Region.putInteger(38, 0);
    localSF2Region.putInteger(36, 8000);
    localSF2Region.putInteger(37, 1000);
    localSF2Region.putInteger(26, 2400);
    localSF2Region.putInteger(30, 12000);
    localSF2Region.putInteger(11, 56536);
    localSF2Region.putInteger(8, 16000);
    localSF2Region.putInteger(48, -100);
    return localSF2Layer;
  }
  
  public static SF2Layer new_bass1(SF2Soundbank paramSF2Soundbank)
  {
    int i = 8;
    int j = 4096 * i;
    double[] arrayOfDouble1 = new double[j * 2];
    double d1 = i * 25;
    double d2 = 0.05D;
    double d3 = 0.05D;
    double d4 = 0.2D;
    double d5 = 0.02D;
    double d6 = d4;
    double d7 = Math.pow(d5 / d4, 0.04D);
    double[] arrayOfDouble2 = new double[25];
    for (int k = 0; k < 25; k++)
    {
      arrayOfDouble2[k] = d6;
      d6 *= d7;
    }
    arrayOfDouble2[0] *= 8.0D;
    arrayOfDouble2[1] *= 4.0D;
    arrayOfDouble2[3] *= 8.0D;
    arrayOfDouble2[5] *= 8.0D;
    for (k = 0; k < 25; k++)
    {
      double d8 = d2 + (d3 - d2) * (k / 40.0D);
      complexGaussianDist(arrayOfDouble1, d1 * (k + 1), d8, arrayOfDouble2[k]);
    }
    SF2Sample localSF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Bass", arrayOfDouble1, d1);
    SF2Layer localSF2Layer = newLayer(paramSF2Soundbank, "Bass", localSF2Sample);
    SF2Region localSF2Region = (SF2Region)localSF2Layer.getRegions().get(0);
    localSF2Region.putInteger(54, 1);
    localSF2Region.putInteger(34, 53536);
    localSF2Region.putInteger(38, 0);
    localSF2Region.putInteger(36, 4000);
    localSF2Region.putInteger(37, 1000);
    localSF2Region.putInteger(26, 62536);
    localSF2Region.putInteger(30, 12000);
    localSF2Region.putInteger(11, 60536);
    localSF2Region.putInteger(8, 11000);
    localSF2Region.putInteger(48, -100);
    return localSF2Layer;
  }
  
  public static SF2Layer new_synthbass(SF2Soundbank paramSF2Soundbank)
  {
    int i = 8;
    int j = 4096 * i;
    double[] arrayOfDouble1 = new double[j * 2];
    double d1 = i * 25;
    double d2 = 0.05D;
    double d3 = 0.05D;
    double d4 = 0.2D;
    double d5 = 0.02D;
    double d6 = d4;
    double d7 = Math.pow(d5 / d4, 0.04D);
    double[] arrayOfDouble2 = new double[25];
    for (int k = 0; k < 25; k++)
    {
      arrayOfDouble2[k] = d6;
      d6 *= d7;
    }
    arrayOfDouble2[0] *= 16.0D;
    arrayOfDouble2[1] *= 4.0D;
    arrayOfDouble2[3] *= 16.0D;
    arrayOfDouble2[5] *= 8.0D;
    for (k = 0; k < 25; k++)
    {
      double d8 = d2 + (d3 - d2) * (k / 40.0D);
      complexGaussianDist(arrayOfDouble1, d1 * (k + 1), d8, arrayOfDouble2[k]);
    }
    SF2Sample localSF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Bass", arrayOfDouble1, d1);
    SF2Layer localSF2Layer = newLayer(paramSF2Soundbank, "Bass", localSF2Sample);
    SF2Region localSF2Region = (SF2Region)localSF2Layer.getRegions().get(0);
    localSF2Region.putInteger(54, 1);
    localSF2Region.putInteger(34, 53536);
    localSF2Region.putInteger(38, 0);
    localSF2Region.putInteger(36, 4000);
    localSF2Region.putInteger(37, 1000);
    localSF2Region.putInteger(26, 62536);
    localSF2Region.putInteger(30, 12000);
    localSF2Region.putInteger(11, 62536);
    localSF2Region.putInteger(9, 100);
    localSF2Region.putInteger(8, 8000);
    localSF2Region.putInteger(48, -100);
    return localSF2Layer;
  }
  
  public static SF2Layer new_bass2(SF2Soundbank paramSF2Soundbank)
  {
    int i = 8;
    int j = 4096 * i;
    double[] arrayOfDouble1 = new double[j * 2];
    double d1 = i * 25;
    double d2 = 0.05D;
    double d3 = 0.05D;
    double d4 = 0.2D;
    double d5 = 0.002D;
    double d6 = d4;
    double d7 = Math.pow(d5 / d4, 0.04D);
    double[] arrayOfDouble2 = new double[25];
    for (int k = 0; k < 25; k++)
    {
      arrayOfDouble2[k] = d6;
      d6 *= d7;
    }
    arrayOfDouble2[0] *= 8.0D;
    arrayOfDouble2[1] *= 4.0D;
    arrayOfDouble2[3] *= 8.0D;
    arrayOfDouble2[5] *= 8.0D;
    for (k = 0; k < 25; k++)
    {
      double d8 = d2 + (d3 - d2) * (k / 40.0D);
      complexGaussianDist(arrayOfDouble1, d1 * (k + 1), d8, arrayOfDouble2[k]);
    }
    SF2Sample localSF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Bass2", arrayOfDouble1, d1);
    SF2Layer localSF2Layer = newLayer(paramSF2Soundbank, "Bass2", localSF2Sample);
    SF2Region localSF2Region = (SF2Region)localSF2Layer.getRegions().get(0);
    localSF2Region.putInteger(54, 1);
    localSF2Region.putInteger(34, 57536);
    localSF2Region.putInteger(38, 0);
    localSF2Region.putInteger(36, 4000);
    localSF2Region.putInteger(37, 1000);
    localSF2Region.putInteger(26, 59536);
    localSF2Region.putInteger(30, 12000);
    localSF2Region.putInteger(8, 5000);
    localSF2Region.putInteger(48, -100);
    return localSF2Layer;
  }
  
  public static SF2Layer new_solostring(SF2Soundbank paramSF2Soundbank)
  {
    int i = 8;
    int j = 4096 * i;
    double[] arrayOfDouble1 = new double[j * 2];
    double d1 = i * 25;
    double d2 = 2.0D;
    double d3 = 2.0D;
    double d4 = 0.2D;
    double d5 = 0.01D;
    double[] arrayOfDouble2 = new double[18];
    double d6 = d4;
    double d7 = Math.pow(d5 / d4, 0.025D);
    for (int k = 0; k < arrayOfDouble2.length; k++)
    {
      d6 *= d7;
      arrayOfDouble2[k] = d6;
    }
    arrayOfDouble2[0] *= 5.0D;
    arrayOfDouble2[1] *= 5.0D;
    arrayOfDouble2[2] *= 5.0D;
    arrayOfDouble2[3] *= 4.0D;
    arrayOfDouble2[4] *= 4.0D;
    arrayOfDouble2[5] *= 3.0D;
    arrayOfDouble2[6] *= 3.0D;
    arrayOfDouble2[7] *= 2.0D;
    for (k = 0; k < arrayOfDouble2.length; k++)
    {
      double d8 = d2 + (d3 - d2) * (k / 40.0D);
      complexGaussianDist(arrayOfDouble1, d1 * (k + 1), d8, d6);
    }
    SF2Sample localSF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Strings", arrayOfDouble1, d1);
    SF2Layer localSF2Layer = newLayer(paramSF2Soundbank, "Strings", localSF2Sample);
    SF2Region localSF2Region = (SF2Region)localSF2Layer.getRegions().get(0);
    localSF2Region.putInteger(54, 1);
    localSF2Region.putInteger(34, 60536);
    localSF2Region.putInteger(38, 1000);
    localSF2Region.putInteger(36, 4000);
    localSF2Region.putInteger(37, -100);
    localSF2Region.putInteger(8, 9500);
    localSF2Region.putInteger(24, 64536);
    localSF2Region.putInteger(6, 15);
    return localSF2Layer;
  }
  
  public static SF2Layer new_orchhit(SF2Soundbank paramSF2Soundbank)
  {
    int i = 8;
    int j = 4096 * i;
    double[] arrayOfDouble = new double[j * 2];
    double d1 = i * 25;
    double d2 = 2.0D;
    double d3 = 80.0D;
    double d4 = 0.2D;
    double d5 = 0.001D;
    double d6 = d4;
    double d7 = Math.pow(d5 / d4, 0.025D);
    for (int k = 0; k < 40; k++)
    {
      double d8 = d2 + (d3 - d2) * (k / 40.0D);
      complexGaussianDist(arrayOfDouble, d1 * (k + 1), d8, d6);
      d6 *= d7;
    }
    complexGaussianDist(arrayOfDouble, d1 * 4.0D, 300.0D, 1.0D);
    SF2Sample localSF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Och Strings", arrayOfDouble, d1);
    SF2Layer localSF2Layer = newLayer(paramSF2Soundbank, "Och Strings", localSF2Sample);
    SF2Region localSF2Region = (SF2Region)localSF2Layer.getRegions().get(0);
    localSF2Region.putInteger(54, 1);
    localSF2Region.putInteger(34, 60536);
    localSF2Region.putInteger(38, 200);
    localSF2Region.putInteger(36, 200);
    localSF2Region.putInteger(37, 1000);
    localSF2Region.putInteger(8, 9500);
    return localSF2Layer;
  }
  
  public static SF2Layer new_string2(SF2Soundbank paramSF2Soundbank)
  {
    int i = 8;
    int j = 4096 * i;
    double[] arrayOfDouble = new double[j * 2];
    double d1 = i * 25;
    double d2 = 2.0D;
    double d3 = 80.0D;
    double d4 = 0.2D;
    double d5 = 0.001D;
    double d6 = d4;
    double d7 = Math.pow(d5 / d4, 0.025D);
    for (int k = 0; k < 40; k++)
    {
      double d8 = d2 + (d3 - d2) * (k / 40.0D);
      complexGaussianDist(arrayOfDouble, d1 * (k + 1), d8, d6);
      d6 *= d7;
    }
    SF2Sample localSF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Strings", arrayOfDouble, d1);
    SF2Layer localSF2Layer = newLayer(paramSF2Soundbank, "Strings", localSF2Sample);
    SF2Region localSF2Region = (SF2Region)localSF2Layer.getRegions().get(0);
    localSF2Region.putInteger(54, 1);
    localSF2Region.putInteger(34, 60536);
    localSF2Region.putInteger(38, 1000);
    localSF2Region.putInteger(36, 4000);
    localSF2Region.putInteger(37, -100);
    localSF2Region.putInteger(8, 9500);
    return localSF2Layer;
  }
  
  public static SF2Layer new_choir(SF2Soundbank paramSF2Soundbank)
  {
    int i = 8;
    int j = 4096 * i;
    double[] arrayOfDouble1 = new double[j * 2];
    double d1 = i * 25;
    double d2 = 2.0D;
    double d3 = 80.0D;
    double d4 = 0.2D;
    double d5 = 0.001D;
    double d6 = d4;
    double d7 = Math.pow(d5 / d4, 0.025D);
    double[] arrayOfDouble2 = new double[40];
    for (int k = 0; k < arrayOfDouble2.length; k++)
    {
      d6 *= d7;
      arrayOfDouble2[k] = d6;
    }
    arrayOfDouble2[5] *= 0.1D;
    arrayOfDouble2[6] *= 0.01D;
    arrayOfDouble2[7] *= 0.1D;
    arrayOfDouble2[8] *= 0.1D;
    for (k = 0; k < arrayOfDouble2.length; k++)
    {
      double d8 = d2 + (d3 - d2) * (k / 40.0D);
      complexGaussianDist(arrayOfDouble1, d1 * (k + 1), d8, arrayOfDouble2[k]);
    }
    SF2Sample localSF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Strings", arrayOfDouble1, d1);
    SF2Layer localSF2Layer = newLayer(paramSF2Soundbank, "Strings", localSF2Sample);
    SF2Region localSF2Region = (SF2Region)localSF2Layer.getRegions().get(0);
    localSF2Region.putInteger(54, 1);
    localSF2Region.putInteger(34, 60536);
    localSF2Region.putInteger(38, 1000);
    localSF2Region.putInteger(36, 4000);
    localSF2Region.putInteger(37, -100);
    localSF2Region.putInteger(8, 9500);
    return localSF2Layer;
  }
  
  public static SF2Layer new_organ(SF2Soundbank paramSF2Soundbank)
  {
    Random localRandom = new Random(102030201L);
    int i = 1;
    int j = 4096 * i;
    double[] arrayOfDouble = new double[j * 2];
    double d1 = i * 15;
    double d2 = 0.01D;
    double d3 = 0.01D;
    double d4 = 0.2D;
    double d5 = 0.001D;
    double d6 = d4;
    double d7 = Math.pow(d5 / d4, 0.025D);
    for (int k = 0; k < 12; k++)
    {
      double d8 = d2 + (d3 - d2) * (k / 40.0D);
      complexGaussianDist(arrayOfDouble, d1 * (k + 1), d8, d6 * (0.5D + 3.0D * localRandom.nextDouble()));
      d6 *= d7;
    }
    SF2Sample localSF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Organ", arrayOfDouble, d1);
    SF2Layer localSF2Layer = newLayer(paramSF2Soundbank, "Organ", localSF2Sample);
    SF2Region localSF2Region = (SF2Region)localSF2Layer.getRegions().get(0);
    localSF2Region.putInteger(54, 1);
    localSF2Region.putInteger(34, 59536);
    localSF2Region.putInteger(38, 64536);
    localSF2Region.putInteger(36, 4000);
    localSF2Region.putInteger(37, -100);
    localSF2Region.putInteger(8, 9500);
    return localSF2Layer;
  }
  
  public static SF2Layer new_ch_organ(SF2Soundbank paramSF2Soundbank)
  {
    int i = 1;
    int j = 4096 * i;
    double[] arrayOfDouble1 = new double[j * 2];
    double d1 = i * 15;
    double d2 = 0.01D;
    double d3 = 0.01D;
    double d4 = 0.2D;
    double d5 = 0.001D;
    double d6 = d4;
    double d7 = Math.pow(d5 / d4, 0.016666666666666666D);
    double[] arrayOfDouble2 = new double[60];
    for (int k = 0; k < arrayOfDouble2.length; k++)
    {
      d6 *= d7;
      arrayOfDouble2[k] = d6;
    }
    arrayOfDouble2[0] *= 5.0D;
    arrayOfDouble2[1] *= 2.0D;
    arrayOfDouble2[2] = 0.0D;
    arrayOfDouble2[4] = 0.0D;
    arrayOfDouble2[5] = 0.0D;
    arrayOfDouble2[7] *= 7.0D;
    arrayOfDouble2[9] = 0.0D;
    arrayOfDouble2[10] = 0.0D;
    arrayOfDouble2[12] = 0.0D;
    arrayOfDouble2[15] *= 7.0D;
    arrayOfDouble2[18] = 0.0D;
    arrayOfDouble2[20] = 0.0D;
    arrayOfDouble2[24] = 0.0D;
    arrayOfDouble2[27] *= 5.0D;
    arrayOfDouble2[29] = 0.0D;
    arrayOfDouble2[30] = 0.0D;
    arrayOfDouble2[33] = 0.0D;
    arrayOfDouble2[36] *= 4.0D;
    arrayOfDouble2[37] = 0.0D;
    arrayOfDouble2[39] = 0.0D;
    arrayOfDouble2[42] = 0.0D;
    arrayOfDouble2[43] = 0.0D;
    arrayOfDouble2[47] = 0.0D;
    arrayOfDouble2[50] *= 4.0D;
    arrayOfDouble2[52] = 0.0D;
    arrayOfDouble2[55] = 0.0D;
    arrayOfDouble2[57] = 0.0D;
    arrayOfDouble2[10] *= 0.1D;
    arrayOfDouble2[11] *= 0.1D;
    arrayOfDouble2[12] *= 0.1D;
    arrayOfDouble2[13] *= 0.1D;
    arrayOfDouble2[17] *= 0.1D;
    arrayOfDouble2[18] *= 0.1D;
    arrayOfDouble2[19] *= 0.1D;
    arrayOfDouble2[20] *= 0.1D;
    for (k = 0; k < 60; k++)
    {
      double d8 = d2 + (d3 - d2) * (k / 40.0D);
      complexGaussianDist(arrayOfDouble1, d1 * (k + 1), d8, arrayOfDouble2[k]);
      d6 *= d7;
    }
    SF2Sample localSF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Organ", arrayOfDouble1, d1);
    SF2Layer localSF2Layer = newLayer(paramSF2Soundbank, "Organ", localSF2Sample);
    SF2Region localSF2Region = (SF2Region)localSF2Layer.getRegions().get(0);
    localSF2Region.putInteger(54, 1);
    localSF2Region.putInteger(34, 55536);
    localSF2Region.putInteger(38, 64536);
    return localSF2Layer;
  }
  
  public static SF2Layer new_flute(SF2Soundbank paramSF2Soundbank)
  {
    int i = 8;
    int j = 4096 * i;
    double[] arrayOfDouble = new double[j * 2];
    double d = i * 15;
    complexGaussianDist(arrayOfDouble, d * 1.0D, 0.001D, 0.5D);
    complexGaussianDist(arrayOfDouble, d * 2.0D, 0.001D, 0.5D);
    complexGaussianDist(arrayOfDouble, d * 3.0D, 0.001D, 0.5D);
    complexGaussianDist(arrayOfDouble, d * 4.0D, 0.01D, 0.5D);
    complexGaussianDist(arrayOfDouble, d * 4.0D, 100.0D, 120.0D);
    complexGaussianDist(arrayOfDouble, d * 6.0D, 100.0D, 40.0D);
    complexGaussianDist(arrayOfDouble, d * 8.0D, 100.0D, 80.0D);
    complexGaussianDist(arrayOfDouble, d * 5.0D, 0.001D, 0.05D);
    complexGaussianDist(arrayOfDouble, d * 6.0D, 0.001D, 0.06D);
    complexGaussianDist(arrayOfDouble, d * 7.0D, 0.001D, 0.04D);
    complexGaussianDist(arrayOfDouble, d * 8.0D, 0.005D, 0.06D);
    complexGaussianDist(arrayOfDouble, d * 9.0D, 0.005D, 0.06D);
    complexGaussianDist(arrayOfDouble, d * 10.0D, 0.01D, 0.1D);
    complexGaussianDist(arrayOfDouble, d * 11.0D, 0.08D, 0.7D);
    complexGaussianDist(arrayOfDouble, d * 12.0D, 0.08D, 0.6D);
    complexGaussianDist(arrayOfDouble, d * 13.0D, 0.08D, 0.6D);
    complexGaussianDist(arrayOfDouble, d * 14.0D, 0.08D, 0.6D);
    complexGaussianDist(arrayOfDouble, d * 15.0D, 0.08D, 0.5D);
    complexGaussianDist(arrayOfDouble, d * 16.0D, 0.08D, 0.5D);
    complexGaussianDist(arrayOfDouble, d * 17.0D, 0.08D, 0.2D);
    complexGaussianDist(arrayOfDouble, d * 1.0D, 10.0D, 8.0D);
    complexGaussianDist(arrayOfDouble, d * 2.0D, 10.0D, 8.0D);
    complexGaussianDist(arrayOfDouble, d * 3.0D, 10.0D, 8.0D);
    complexGaussianDist(arrayOfDouble, d * 4.0D, 10.0D, 8.0D);
    complexGaussianDist(arrayOfDouble, d * 5.0D, 10.0D, 8.0D);
    complexGaussianDist(arrayOfDouble, d * 6.0D, 20.0D, 9.0D);
    complexGaussianDist(arrayOfDouble, d * 7.0D, 20.0D, 9.0D);
    complexGaussianDist(arrayOfDouble, d * 8.0D, 20.0D, 9.0D);
    complexGaussianDist(arrayOfDouble, d * 9.0D, 20.0D, 8.0D);
    complexGaussianDist(arrayOfDouble, d * 10.0D, 30.0D, 8.0D);
    complexGaussianDist(arrayOfDouble, d * 11.0D, 30.0D, 9.0D);
    complexGaussianDist(arrayOfDouble, d * 12.0D, 30.0D, 9.0D);
    complexGaussianDist(arrayOfDouble, d * 13.0D, 30.0D, 8.0D);
    complexGaussianDist(arrayOfDouble, d * 14.0D, 30.0D, 8.0D);
    complexGaussianDist(arrayOfDouble, d * 15.0D, 30.0D, 7.0D);
    complexGaussianDist(arrayOfDouble, d * 16.0D, 30.0D, 7.0D);
    complexGaussianDist(arrayOfDouble, d * 17.0D, 30.0D, 6.0D);
    SF2Sample localSF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Flute", arrayOfDouble, d);
    SF2Layer localSF2Layer = newLayer(paramSF2Soundbank, "Flute", localSF2Sample);
    SF2Region localSF2Region = (SF2Region)localSF2Layer.getRegions().get(0);
    localSF2Region.putInteger(54, 1);
    localSF2Region.putInteger(34, 59536);
    localSF2Region.putInteger(38, 64536);
    localSF2Region.putInteger(36, 4000);
    localSF2Region.putInteger(37, -100);
    localSF2Region.putInteger(8, 9500);
    return localSF2Layer;
  }
  
  public static SF2Layer new_horn(SF2Soundbank paramSF2Soundbank)
  {
    int i = 8;
    int j = 4096 * i;
    double[] arrayOfDouble = new double[j * 2];
    double d1 = i * 15;
    double d2 = 0.5D;
    double d3 = 1.0E-11D;
    double d4 = d2;
    double d5 = Math.pow(d3 / d2, 0.025D);
    for (int k = 0; k < 40; k++)
    {
      if (k == 0) {
        complexGaussianDist(arrayOfDouble, d1 * (k + 1), 0.1D, d4 * 0.2D);
      } else {
        complexGaussianDist(arrayOfDouble, d1 * (k + 1), 0.1D, d4);
      }
      d4 *= d5;
    }
    complexGaussianDist(arrayOfDouble, d1 * 2.0D, 100.0D, 1.0D);
    SF2Sample localSF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Horn", arrayOfDouble, d1);
    SF2Layer localSF2Layer = newLayer(paramSF2Soundbank, "Horn", localSF2Sample);
    SF2Region localSF2Region = (SF2Region)localSF2Layer.getRegions().get(0);
    localSF2Region.putInteger(54, 1);
    localSF2Region.putInteger(34, 59536);
    localSF2Region.putInteger(38, 64536);
    localSF2Region.putInteger(36, 4000);
    localSF2Region.putInteger(37, -100);
    localSF2Region.putInteger(26, 65036);
    localSF2Region.putInteger(30, 12000);
    localSF2Region.putInteger(11, 5000);
    localSF2Region.putInteger(8, 4500);
    return localSF2Layer;
  }
  
  public static SF2Layer new_trumpet(SF2Soundbank paramSF2Soundbank)
  {
    int i = 8;
    int j = 4096 * i;
    double[] arrayOfDouble1 = new double[j * 2];
    double d1 = i * 15;
    double d2 = 0.5D;
    double d3 = 1.0E-5D;
    double d4 = d2;
    double d5 = Math.pow(d3 / d2, 0.0125D);
    double[] arrayOfDouble2 = new double[80];
    for (int k = 0; k < 80; k++)
    {
      arrayOfDouble2[k] = d4;
      d4 *= d5;
    }
    arrayOfDouble2[0] *= 0.05D;
    arrayOfDouble2[1] *= 0.2D;
    arrayOfDouble2[2] *= 0.5D;
    arrayOfDouble2[3] *= 0.85D;
    for (k = 0; k < 80; k++) {
      complexGaussianDist(arrayOfDouble1, d1 * (k + 1), 0.1D, arrayOfDouble2[k]);
    }
    complexGaussianDist(arrayOfDouble1, d1 * 5.0D, 300.0D, 3.0D);
    SF2Sample localSF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Trumpet", arrayOfDouble1, d1);
    SF2Layer localSF2Layer = newLayer(paramSF2Soundbank, "Trumpet", localSF2Sample);
    SF2Region localSF2Region = (SF2Region)localSF2Layer.getRegions().get(0);
    localSF2Region.putInteger(54, 1);
    localSF2Region.putInteger(34, 55536);
    localSF2Region.putInteger(38, 0);
    localSF2Region.putInteger(36, 4000);
    localSF2Region.putInteger(37, -100);
    localSF2Region.putInteger(26, 61536);
    localSF2Region.putInteger(30, 63036);
    localSF2Region.putInteger(11, 5000);
    localSF2Region.putInteger(8, 4500);
    localSF2Region.putInteger(9, 10);
    return localSF2Layer;
  }
  
  public static SF2Layer new_brass_section(SF2Soundbank paramSF2Soundbank)
  {
    int i = 8;
    int j = 4096 * i;
    double[] arrayOfDouble1 = new double[j * 2];
    double d1 = i * 15;
    double d2 = 0.5D;
    double d3 = 0.005D;
    double d4 = d2;
    double d5 = Math.pow(d3 / d2, 0.03333333333333333D);
    double[] arrayOfDouble2 = new double[30];
    for (int k = 0; k < 30; k++)
    {
      arrayOfDouble2[k] = d4;
      d4 *= d5;
    }
    arrayOfDouble2[0] *= 0.8D;
    arrayOfDouble2[1] *= 0.9D;
    double d6 = 5.0D;
    for (int m = 0; m < 30; m++)
    {
      complexGaussianDist(arrayOfDouble1, d1 * (m + 1), 0.1D * d6, arrayOfDouble2[m] * d6);
      d6 += 6.0D;
    }
    complexGaussianDist(arrayOfDouble1, d1 * 6.0D, 300.0D, 2.0D);
    SF2Sample localSF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Brass Section", arrayOfDouble1, d1);
    SF2Layer localSF2Layer = newLayer(paramSF2Soundbank, "Brass Section", localSF2Sample);
    SF2Region localSF2Region = (SF2Region)localSF2Layer.getRegions().get(0);
    localSF2Region.putInteger(54, 1);
    localSF2Region.putInteger(34, 56336);
    localSF2Region.putInteger(38, 64536);
    localSF2Region.putInteger(36, 4000);
    localSF2Region.putInteger(37, -100);
    localSF2Region.putInteger(26, 62536);
    localSF2Region.putInteger(30, 12000);
    localSF2Region.putInteger(11, 5000);
    localSF2Region.putInteger(8, 4500);
    return localSF2Layer;
  }
  
  public static SF2Layer new_trombone(SF2Soundbank paramSF2Soundbank)
  {
    int i = 8;
    int j = 4096 * i;
    double[] arrayOfDouble1 = new double[j * 2];
    double d1 = i * 15;
    double d2 = 0.5D;
    double d3 = 0.001D;
    double d4 = d2;
    double d5 = Math.pow(d3 / d2, 0.0125D);
    double[] arrayOfDouble2 = new double[80];
    for (int k = 0; k < 80; k++)
    {
      arrayOfDouble2[k] = d4;
      d4 *= d5;
    }
    arrayOfDouble2[0] *= 0.3D;
    arrayOfDouble2[1] *= 0.7D;
    for (k = 0; k < 80; k++) {
      complexGaussianDist(arrayOfDouble1, d1 * (k + 1), 0.1D, arrayOfDouble2[k]);
    }
    complexGaussianDist(arrayOfDouble1, d1 * 6.0D, 300.0D, 2.0D);
    SF2Sample localSF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Trombone", arrayOfDouble1, d1);
    SF2Layer localSF2Layer = newLayer(paramSF2Soundbank, "Trombone", localSF2Sample);
    SF2Region localSF2Region = (SF2Region)localSF2Layer.getRegions().get(0);
    localSF2Region.putInteger(54, 1);
    localSF2Region.putInteger(34, 57536);
    localSF2Region.putInteger(38, 64536);
    localSF2Region.putInteger(36, 4000);
    localSF2Region.putInteger(37, -100);
    localSF2Region.putInteger(26, 63536);
    localSF2Region.putInteger(30, 12000);
    localSF2Region.putInteger(11, 5000);
    localSF2Region.putInteger(8, 4500);
    localSF2Region.putInteger(9, 10);
    return localSF2Layer;
  }
  
  public static SF2Layer new_sax(SF2Soundbank paramSF2Soundbank)
  {
    int i = 8;
    int j = 4096 * i;
    double[] arrayOfDouble = new double[j * 2];
    double d1 = i * 15;
    double d2 = 0.5D;
    double d3 = 0.01D;
    double d4 = d2;
    double d5 = Math.pow(d3 / d2, 0.025D);
    for (int k = 0; k < 40; k++)
    {
      if ((k == 0) || (k == 2)) {
        complexGaussianDist(arrayOfDouble, d1 * (k + 1), 0.1D, d4 * 4.0D);
      } else {
        complexGaussianDist(arrayOfDouble, d1 * (k + 1), 0.1D, d4);
      }
      d4 *= d5;
    }
    complexGaussianDist(arrayOfDouble, d1 * 4.0D, 200.0D, 1.0D);
    SF2Sample localSF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Sax", arrayOfDouble, d1);
    SF2Layer localSF2Layer = newLayer(paramSF2Soundbank, "Sax", localSF2Sample);
    SF2Region localSF2Region = (SF2Region)localSF2Layer.getRegions().get(0);
    localSF2Region.putInteger(54, 1);
    localSF2Region.putInteger(34, 59536);
    localSF2Region.putInteger(38, 64536);
    localSF2Region.putInteger(36, 4000);
    localSF2Region.putInteger(37, -100);
    localSF2Region.putInteger(26, 62536);
    localSF2Region.putInteger(30, 12000);
    localSF2Region.putInteger(11, 5000);
    localSF2Region.putInteger(8, 4500);
    return localSF2Layer;
  }
  
  public static SF2Layer new_oboe(SF2Soundbank paramSF2Soundbank)
  {
    int i = 8;
    int j = 4096 * i;
    double[] arrayOfDouble = new double[j * 2];
    double d = i * 15;
    complexGaussianDist(arrayOfDouble, d * 5.0D, 100.0D, 80.0D);
    complexGaussianDist(arrayOfDouble, d * 1.0D, 0.01D, 0.53D);
    complexGaussianDist(arrayOfDouble, d * 2.0D, 0.01D, 0.51D);
    complexGaussianDist(arrayOfDouble, d * 3.0D, 0.01D, 0.48D);
    complexGaussianDist(arrayOfDouble, d * 4.0D, 0.01D, 0.49D);
    complexGaussianDist(arrayOfDouble, d * 5.0D, 0.01D, 5.0D);
    complexGaussianDist(arrayOfDouble, d * 6.0D, 0.01D, 0.51D);
    complexGaussianDist(arrayOfDouble, d * 7.0D, 0.01D, 0.5D);
    complexGaussianDist(arrayOfDouble, d * 8.0D, 0.01D, 0.59D);
    complexGaussianDist(arrayOfDouble, d * 9.0D, 0.01D, 0.61D);
    complexGaussianDist(arrayOfDouble, d * 10.0D, 0.01D, 0.52D);
    complexGaussianDist(arrayOfDouble, d * 11.0D, 0.01D, 0.49D);
    complexGaussianDist(arrayOfDouble, d * 12.0D, 0.01D, 0.51D);
    complexGaussianDist(arrayOfDouble, d * 13.0D, 0.01D, 0.48D);
    complexGaussianDist(arrayOfDouble, d * 14.0D, 0.01D, 0.51D);
    complexGaussianDist(arrayOfDouble, d * 15.0D, 0.01D, 0.46D);
    complexGaussianDist(arrayOfDouble, d * 16.0D, 0.01D, 0.35D);
    complexGaussianDist(arrayOfDouble, d * 17.0D, 0.01D, 0.2D);
    complexGaussianDist(arrayOfDouble, d * 18.0D, 0.01D, 0.1D);
    complexGaussianDist(arrayOfDouble, d * 19.0D, 0.01D, 0.5D);
    complexGaussianDist(arrayOfDouble, d * 20.0D, 0.01D, 0.1D);
    SF2Sample localSF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Oboe", arrayOfDouble, d);
    SF2Layer localSF2Layer = newLayer(paramSF2Soundbank, "Oboe", localSF2Sample);
    SF2Region localSF2Region = (SF2Region)localSF2Layer.getRegions().get(0);
    localSF2Region.putInteger(54, 1);
    localSF2Region.putInteger(34, 59536);
    localSF2Region.putInteger(38, 64536);
    localSF2Region.putInteger(36, 4000);
    localSF2Region.putInteger(37, -100);
    localSF2Region.putInteger(8, 9500);
    return localSF2Layer;
  }
  
  public static SF2Layer new_bassoon(SF2Soundbank paramSF2Soundbank)
  {
    int i = 8;
    int j = 4096 * i;
    double[] arrayOfDouble = new double[j * 2];
    double d = i * 15;
    complexGaussianDist(arrayOfDouble, d * 2.0D, 100.0D, 40.0D);
    complexGaussianDist(arrayOfDouble, d * 4.0D, 100.0D, 20.0D);
    complexGaussianDist(arrayOfDouble, d * 1.0D, 0.01D, 0.53D);
    complexGaussianDist(arrayOfDouble, d * 2.0D, 0.01D, 5.0D);
    complexGaussianDist(arrayOfDouble, d * 3.0D, 0.01D, 0.51D);
    complexGaussianDist(arrayOfDouble, d * 4.0D, 0.01D, 0.48D);
    complexGaussianDist(arrayOfDouble, d * 5.0D, 0.01D, 1.49D);
    complexGaussianDist(arrayOfDouble, d * 6.0D, 0.01D, 0.51D);
    complexGaussianDist(arrayOfDouble, d * 7.0D, 0.01D, 0.5D);
    complexGaussianDist(arrayOfDouble, d * 8.0D, 0.01D, 0.59D);
    complexGaussianDist(arrayOfDouble, d * 9.0D, 0.01D, 0.61D);
    complexGaussianDist(arrayOfDouble, d * 10.0D, 0.01D, 0.52D);
    complexGaussianDist(arrayOfDouble, d * 11.0D, 0.01D, 0.49D);
    complexGaussianDist(arrayOfDouble, d * 12.0D, 0.01D, 0.51D);
    complexGaussianDist(arrayOfDouble, d * 13.0D, 0.01D, 0.48D);
    complexGaussianDist(arrayOfDouble, d * 14.0D, 0.01D, 0.51D);
    complexGaussianDist(arrayOfDouble, d * 15.0D, 0.01D, 0.46D);
    complexGaussianDist(arrayOfDouble, d * 16.0D, 0.01D, 0.35D);
    complexGaussianDist(arrayOfDouble, d * 17.0D, 0.01D, 0.2D);
    complexGaussianDist(arrayOfDouble, d * 18.0D, 0.01D, 0.1D);
    complexGaussianDist(arrayOfDouble, d * 19.0D, 0.01D, 0.5D);
    complexGaussianDist(arrayOfDouble, d * 20.0D, 0.01D, 0.1D);
    SF2Sample localSF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Flute", arrayOfDouble, d);
    SF2Layer localSF2Layer = newLayer(paramSF2Soundbank, "Flute", localSF2Sample);
    SF2Region localSF2Region = (SF2Region)localSF2Layer.getRegions().get(0);
    localSF2Region.putInteger(54, 1);
    localSF2Region.putInteger(34, 59536);
    localSF2Region.putInteger(38, 64536);
    localSF2Region.putInteger(36, 4000);
    localSF2Region.putInteger(37, -100);
    localSF2Region.putInteger(8, 9500);
    return localSF2Layer;
  }
  
  public static SF2Layer new_clarinet(SF2Soundbank paramSF2Soundbank)
  {
    int i = 8;
    int j = 4096 * i;
    double[] arrayOfDouble = new double[j * 2];
    double d = i * 15;
    complexGaussianDist(arrayOfDouble, d * 1.0D, 0.001D, 0.5D);
    complexGaussianDist(arrayOfDouble, d * 2.0D, 0.001D, 0.02D);
    complexGaussianDist(arrayOfDouble, d * 3.0D, 0.001D, 0.2D);
    complexGaussianDist(arrayOfDouble, d * 4.0D, 0.01D, 0.1D);
    complexGaussianDist(arrayOfDouble, d * 4.0D, 100.0D, 60.0D);
    complexGaussianDist(arrayOfDouble, d * 6.0D, 100.0D, 20.0D);
    complexGaussianDist(arrayOfDouble, d * 8.0D, 100.0D, 20.0D);
    complexGaussianDist(arrayOfDouble, d * 5.0D, 0.001D, 0.1D);
    complexGaussianDist(arrayOfDouble, d * 6.0D, 0.001D, 0.09D);
    complexGaussianDist(arrayOfDouble, d * 7.0D, 0.001D, 0.02D);
    complexGaussianDist(arrayOfDouble, d * 8.0D, 0.005D, 0.16D);
    complexGaussianDist(arrayOfDouble, d * 9.0D, 0.005D, 0.96D);
    complexGaussianDist(arrayOfDouble, d * 10.0D, 0.01D, 0.9D);
    complexGaussianDist(arrayOfDouble, d * 11.0D, 0.08D, 1.2D);
    complexGaussianDist(arrayOfDouble, d * 12.0D, 0.08D, 1.8D);
    complexGaussianDist(arrayOfDouble, d * 13.0D, 0.08D, 1.6D);
    complexGaussianDist(arrayOfDouble, d * 14.0D, 0.08D, 1.2D);
    complexGaussianDist(arrayOfDouble, d * 15.0D, 0.08D, 0.9D);
    complexGaussianDist(arrayOfDouble, d * 16.0D, 0.08D, 0.5D);
    complexGaussianDist(arrayOfDouble, d * 17.0D, 0.08D, 0.2D);
    complexGaussianDist(arrayOfDouble, d * 1.0D, 10.0D, 8.0D);
    complexGaussianDist(arrayOfDouble, d * 2.0D, 10.0D, 8.0D);
    complexGaussianDist(arrayOfDouble, d * 3.0D, 10.0D, 8.0D);
    complexGaussianDist(arrayOfDouble, d * 4.0D, 10.0D, 8.0D);
    complexGaussianDist(arrayOfDouble, d * 5.0D, 10.0D, 8.0D);
    complexGaussianDist(arrayOfDouble, d * 6.0D, 20.0D, 9.0D);
    complexGaussianDist(arrayOfDouble, d * 7.0D, 20.0D, 9.0D);
    complexGaussianDist(arrayOfDouble, d * 8.0D, 20.0D, 9.0D);
    complexGaussianDist(arrayOfDouble, d * 9.0D, 20.0D, 8.0D);
    complexGaussianDist(arrayOfDouble, d * 10.0D, 30.0D, 8.0D);
    complexGaussianDist(arrayOfDouble, d * 11.0D, 30.0D, 9.0D);
    complexGaussianDist(arrayOfDouble, d * 12.0D, 30.0D, 9.0D);
    complexGaussianDist(arrayOfDouble, d * 13.0D, 30.0D, 8.0D);
    complexGaussianDist(arrayOfDouble, d * 14.0D, 30.0D, 8.0D);
    complexGaussianDist(arrayOfDouble, d * 15.0D, 30.0D, 7.0D);
    complexGaussianDist(arrayOfDouble, d * 16.0D, 30.0D, 7.0D);
    complexGaussianDist(arrayOfDouble, d * 17.0D, 30.0D, 6.0D);
    SF2Sample localSF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Clarinet", arrayOfDouble, d);
    SF2Layer localSF2Layer = newLayer(paramSF2Soundbank, "Clarinet", localSF2Sample);
    SF2Region localSF2Region = (SF2Region)localSF2Layer.getRegions().get(0);
    localSF2Region.putInteger(54, 1);
    localSF2Region.putInteger(34, 59536);
    localSF2Region.putInteger(38, 64536);
    localSF2Region.putInteger(36, 4000);
    localSF2Region.putInteger(37, -100);
    localSF2Region.putInteger(8, 9500);
    return localSF2Layer;
  }
  
  public static SF2Layer new_timpani(SF2Soundbank paramSF2Soundbank)
  {
    int i = 32768;
    Object localObject3 = new double[2 * i];
    double d1 = 48.0D;
    complexGaussianDist((double[])localObject3, d1 * 2.0D, 0.2D, 1.0D);
    complexGaussianDist((double[])localObject3, d1 * 3.0D, 0.2D, 0.7D);
    complexGaussianDist((double[])localObject3, d1 * 5.0D, 10.0D, 1.0D);
    complexGaussianDist((double[])localObject3, d1 * 6.0D, 9.0D, 1.0D);
    complexGaussianDist((double[])localObject3, d1 * 8.0D, 15.0D, 1.0D);
    complexGaussianDist((double[])localObject3, d1 * 9.0D, 18.0D, 0.8D);
    complexGaussianDist((double[])localObject3, d1 * 11.0D, 21.0D, 0.5D);
    complexGaussianDist((double[])localObject3, d1 * 13.0D, 28.0D, 0.3D);
    complexGaussianDist((double[])localObject3, d1 * 14.0D, 22.0D, 0.1D);
    randomPhase((double[])localObject3, new Random(3049912L));
    ifft((double[])localObject3);
    normalize((double[])localObject3, 0.5D);
    localObject3 = realPart((double[])localObject3);
    double d3 = localObject3.length;
    for (int m = 0; m < localObject3.length; m++)
    {
      double d4 = 1.0D - m / d3;
      localObject3[m] *= d4 * d4;
    }
    fadeUp((double[])localObject3, 40);
    Object localObject1 = localObject3;
    i = 16384;
    localObject3 = new double[2 * i];
    Object localObject4 = new Random(3049912L);
    for (int j = 0; j < localObject3.length; j += 2) {
      localObject3[j] = (2.0D * (((Random)localObject4).nextDouble() - 0.5D) * 0.1D);
    }
    fft((double[])localObject3);
    for (j = i / 2; j < localObject3.length; j++) {
      localObject3[j] = 0.0D;
    }
    for (j = 4096; j < 8192; j++) {
      localObject3[j] = (1.0D - (j - 4096) / 4096.0D);
    }
    for (j = 0; j < 300; j++)
    {
      d3 = 1.0D - j / 300.0D;
      localObject3[j] *= (1.0D + 20.0D * d3 * d3);
    }
    for (j = 0; j < 24; j++) {
      localObject3[j] = 0.0D;
    }
    randomPhase((double[])localObject3, new Random(3049912L));
    ifft((double[])localObject3);
    normalize((double[])localObject3, 0.9D);
    localObject3 = realPart((double[])localObject3);
    double d2 = 1.0D;
    for (int k = 0; k < localObject3.length; k++)
    {
      localObject3[k] *= d2;
      d2 *= 0.9998D;
    }
    Object localObject2 = localObject3;
    for (i = 0; i < localObject2.length; i++) {
      localObject1[i] += localObject2[i] * 0.02D;
    }
    normalize((double[])localObject1, 0.9D);
    SF2Sample localSF2Sample = newSimpleDrumSample(paramSF2Soundbank, "Timpani", (double[])localObject1);
    localObject3 = new SF2Layer(paramSF2Soundbank);
    ((SF2Layer)localObject3).setName("Timpani");
    localObject4 = new SF2GlobalRegion();
    ((SF2Layer)localObject3).setGlobalZone((SF2GlobalRegion)localObject4);
    paramSF2Soundbank.addResource((SoundbankResource)localObject3);
    SF2LayerRegion localSF2LayerRegion = new SF2LayerRegion();
    localSF2LayerRegion.putInteger(38, 12000);
    localSF2LayerRegion.putInteger(48, -100);
    localSF2LayerRegion.setSample(localSF2Sample);
    ((SF2Layer)localObject3).getRegions().add(localSF2LayerRegion);
    return (SF2Layer)localObject3;
  }
  
  public static SF2Layer new_melodic_toms(SF2Soundbank paramSF2Soundbank)
  {
    int i = 16384;
    Object localObject3 = new double[2 * i];
    complexGaussianDist((double[])localObject3, 30.0D, 0.5D, 1.0D);
    randomPhase((double[])localObject3, new Random(3049912L));
    ifft((double[])localObject3);
    normalize((double[])localObject3, 0.8D);
    localObject3 = realPart((double[])localObject3);
    double d1 = localObject3.length;
    for (int k = 0; k < localObject3.length; k++) {
      localObject3[k] *= (1.0D - k / d1);
    }
    Object localObject1 = localObject3;
    i = 16384;
    localObject3 = new double[2 * i];
    Object localObject4 = new Random(3049912L);
    for (int j = 0; j < localObject3.length; j += 2) {
      localObject3[j] = (2.0D * (((Random)localObject4).nextDouble() - 0.5D) * 0.1D);
    }
    fft((double[])localObject3);
    for (j = i / 2; j < localObject3.length; j++) {
      localObject3[j] = 0.0D;
    }
    for (j = 4096; j < 8192; j++) {
      localObject3[j] = (1.0D - (j - 4096) / 4096.0D);
    }
    for (j = 0; j < 200; j++)
    {
      double d3 = 1.0D - j / 200.0D;
      localObject3[j] *= (1.0D + 20.0D * d3 * d3);
    }
    for (j = 0; j < 30; j++) {
      localObject3[j] = 0.0D;
    }
    randomPhase((double[])localObject3, new Random(3049912L));
    ifft((double[])localObject3);
    normalize((double[])localObject3, 0.9D);
    localObject3 = realPart((double[])localObject3);
    double d2 = 1.0D;
    for (int m = 0; m < localObject3.length; m++)
    {
      localObject3[m] *= d2;
      d2 *= 0.9996D;
    }
    Object localObject2 = localObject3;
    for (i = 0; i < localObject2.length; i++) {
      localObject1[i] += localObject2[i] * 0.5D;
    }
    for (i = 0; i < 5; i++) {
      localObject1[i] *= i / 5.0D;
    }
    normalize((double[])localObject1, 0.99D);
    SF2Sample localSF2Sample = newSimpleDrumSample(paramSF2Soundbank, "Melodic Toms", (double[])localObject1);
    localSF2Sample.setOriginalPitch(63);
    localObject3 = new SF2Layer(paramSF2Soundbank);
    ((SF2Layer)localObject3).setName("Melodic Toms");
    localObject4 = new SF2GlobalRegion();
    ((SF2Layer)localObject3).setGlobalZone((SF2GlobalRegion)localObject4);
    paramSF2Soundbank.addResource((SoundbankResource)localObject3);
    SF2LayerRegion localSF2LayerRegion = new SF2LayerRegion();
    localSF2LayerRegion.putInteger(38, 12000);
    localSF2LayerRegion.putInteger(48, -100);
    localSF2LayerRegion.setSample(localSF2Sample);
    ((SF2Layer)localObject3).getRegions().add(localSF2LayerRegion);
    return (SF2Layer)localObject3;
  }
  
  public static SF2Layer new_reverse_cymbal(SF2Soundbank paramSF2Soundbank)
  {
    int i = 16384;
    Object localObject2 = new double[2 * i];
    Object localObject3 = new Random(3049912L);
    for (int j = 0; j < localObject2.length; j += 2) {
      localObject2[j] = (2.0D * (((Random)localObject3).nextDouble() - 0.5D));
    }
    for (j = i / 2; j < localObject2.length; j++) {
      localObject2[j] = 0.0D;
    }
    for (j = 0; j < 100; j++) {
      localObject2[j] = 0.0D;
    }
    for (j = 0; j < 1024; j++)
    {
      double d = j / 1024.0D;
      localObject2[j] = (1.0D - d);
    }
    Object localObject1 = localObject2;
    SF2Sample localSF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Reverse Cymbal", (double[])localObject1, 100.0D, 20);
    localObject2 = new SF2Layer(paramSF2Soundbank);
    ((SF2Layer)localObject2).setName("Reverse Cymbal");
    localObject3 = new SF2GlobalRegion();
    ((SF2Layer)localObject2).setGlobalZone((SF2GlobalRegion)localObject3);
    paramSF2Soundbank.addResource((SoundbankResource)localObject2);
    SF2LayerRegion localSF2LayerRegion = new SF2LayerRegion();
    localSF2LayerRegion.putInteger(34, 65336);
    localSF2LayerRegion.putInteger(36, 53536);
    localSF2LayerRegion.putInteger(54, 1);
    localSF2LayerRegion.putInteger(38, 64536);
    localSF2LayerRegion.putInteger(37, 1000);
    localSF2LayerRegion.setSample(localSF2Sample);
    ((SF2Layer)localObject2).getRegions().add(localSF2LayerRegion);
    return (SF2Layer)localObject2;
  }
  
  public static SF2Layer new_snare_drum(SF2Soundbank paramSF2Soundbank)
  {
    int i = 16384;
    Object localObject3 = new double[2 * i];
    complexGaussianDist((double[])localObject3, 24.0D, 0.5D, 1.0D);
    randomPhase((double[])localObject3, new Random(3049912L));
    ifft((double[])localObject3);
    normalize((double[])localObject3, 0.5D);
    localObject3 = realPart((double[])localObject3);
    double d1 = localObject3.length;
    for (int k = 0; k < localObject3.length; k++) {
      localObject3[k] *= (1.0D - k / d1);
    }
    Object localObject1 = localObject3;
    i = 16384;
    localObject3 = new double[2 * i];
    Object localObject4 = new Random(3049912L);
    for (int j = 0; j < localObject3.length; j += 2) {
      localObject3[j] = (2.0D * (((Random)localObject4).nextDouble() - 0.5D) * 0.1D);
    }
    fft((double[])localObject3);
    for (j = i / 2; j < localObject3.length; j++) {
      localObject3[j] = 0.0D;
    }
    for (j = 4096; j < 8192; j++) {
      localObject3[j] = (1.0D - (j - 4096) / 4096.0D);
    }
    for (j = 0; j < 300; j++)
    {
      double d3 = 1.0D - j / 300.0D;
      localObject3[j] *= (1.0D + 20.0D * d3 * d3);
    }
    for (j = 0; j < 24; j++) {
      localObject3[j] = 0.0D;
    }
    randomPhase((double[])localObject3, new Random(3049912L));
    ifft((double[])localObject3);
    normalize((double[])localObject3, 0.9D);
    localObject3 = realPart((double[])localObject3);
    double d2 = 1.0D;
    for (int m = 0; m < localObject3.length; m++)
    {
      localObject3[m] *= d2;
      d2 *= 0.9998D;
    }
    Object localObject2 = localObject3;
    for (i = 0; i < localObject2.length; i++) {
      localObject1[i] += localObject2[i];
    }
    for (i = 0; i < 5; i++) {
      localObject1[i] *= i / 5.0D;
    }
    SF2Sample localSF2Sample = newSimpleDrumSample(paramSF2Soundbank, "Snare Drum", (double[])localObject1);
    localObject3 = new SF2Layer(paramSF2Soundbank);
    ((SF2Layer)localObject3).setName("Snare Drum");
    localObject4 = new SF2GlobalRegion();
    ((SF2Layer)localObject3).setGlobalZone((SF2GlobalRegion)localObject4);
    paramSF2Soundbank.addResource((SoundbankResource)localObject3);
    SF2LayerRegion localSF2LayerRegion = new SF2LayerRegion();
    localSF2LayerRegion.putInteger(38, 12000);
    localSF2LayerRegion.putInteger(56, 0);
    localSF2LayerRegion.putInteger(48, -100);
    localSF2LayerRegion.setSample(localSF2Sample);
    ((SF2Layer)localObject3).getRegions().add(localSF2LayerRegion);
    return (SF2Layer)localObject3;
  }
  
  public static SF2Layer new_bass_drum(SF2Soundbank paramSF2Soundbank)
  {
    int i = 16384;
    Object localObject3 = new double[2 * i];
    complexGaussianDist((double[])localObject3, 10.0D, 2.0D, 1.0D);
    complexGaussianDist((double[])localObject3, 17.2D, 2.0D, 1.0D);
    randomPhase((double[])localObject3, new Random(3049912L));
    ifft((double[])localObject3);
    normalize((double[])localObject3, 0.9D);
    localObject3 = realPart((double[])localObject3);
    double d1 = localObject3.length;
    for (int k = 0; k < localObject3.length; k++) {
      localObject3[k] *= (1.0D - k / d1);
    }
    Object localObject1 = localObject3;
    i = 4096;
    localObject3 = new double[2 * i];
    Object localObject4 = new Random(3049912L);
    for (int j = 0; j < localObject3.length; j += 2) {
      localObject3[j] = (2.0D * (((Random)localObject4).nextDouble() - 0.5D) * 0.1D);
    }
    fft((double[])localObject3);
    for (j = i / 2; j < localObject3.length; j++) {
      localObject3[j] = 0.0D;
    }
    for (j = 1024; j < 2048; j++) {
      localObject3[j] = (1.0D - (j - 1024) / 1024.0D);
    }
    for (j = 0; j < 512; j++) {
      localObject3[j] = (10 * j / 512.0D);
    }
    for (j = 0; j < 10; j++) {
      localObject3[j] = 0.0D;
    }
    randomPhase((double[])localObject3, new Random(3049912L));
    ifft((double[])localObject3);
    normalize((double[])localObject3, 0.9D);
    localObject3 = realPart((double[])localObject3);
    double d2 = 1.0D;
    for (int m = 0; m < localObject3.length; m++)
    {
      localObject3[m] *= d2;
      d2 *= 0.999D;
    }
    Object localObject2 = localObject3;
    for (i = 0; i < localObject2.length; i++) {
      localObject1[i] += localObject2[i] * 0.5D;
    }
    for (i = 0; i < 5; i++) {
      localObject1[i] *= i / 5.0D;
    }
    SF2Sample localSF2Sample = newSimpleDrumSample(paramSF2Soundbank, "Bass Drum", (double[])localObject1);
    localObject3 = new SF2Layer(paramSF2Soundbank);
    ((SF2Layer)localObject3).setName("Bass Drum");
    localObject4 = new SF2GlobalRegion();
    ((SF2Layer)localObject3).setGlobalZone((SF2GlobalRegion)localObject4);
    paramSF2Soundbank.addResource((SoundbankResource)localObject3);
    SF2LayerRegion localSF2LayerRegion = new SF2LayerRegion();
    localSF2LayerRegion.putInteger(38, 12000);
    localSF2LayerRegion.putInteger(56, 0);
    localSF2LayerRegion.putInteger(48, -100);
    localSF2LayerRegion.setSample(localSF2Sample);
    ((SF2Layer)localObject3).getRegions().add(localSF2LayerRegion);
    return (SF2Layer)localObject3;
  }
  
  public static SF2Layer new_tom(SF2Soundbank paramSF2Soundbank)
  {
    int i = 16384;
    Object localObject3 = new double[2 * i];
    complexGaussianDist((double[])localObject3, 30.0D, 0.5D, 1.0D);
    randomPhase((double[])localObject3, new Random(3049912L));
    ifft((double[])localObject3);
    normalize((double[])localObject3, 0.8D);
    localObject3 = realPart((double[])localObject3);
    double d1 = localObject3.length;
    for (int k = 0; k < localObject3.length; k++) {
      localObject3[k] *= (1.0D - k / d1);
    }
    Object localObject1 = localObject3;
    i = 16384;
    localObject3 = new double[2 * i];
    Object localObject4 = new Random(3049912L);
    for (int j = 0; j < localObject3.length; j += 2) {
      localObject3[j] = (2.0D * (((Random)localObject4).nextDouble() - 0.5D) * 0.1D);
    }
    fft((double[])localObject3);
    for (j = i / 2; j < localObject3.length; j++) {
      localObject3[j] = 0.0D;
    }
    for (j = 4096; j < 8192; j++) {
      localObject3[j] = (1.0D - (j - 4096) / 4096.0D);
    }
    for (j = 0; j < 200; j++)
    {
      double d3 = 1.0D - j / 200.0D;
      localObject3[j] *= (1.0D + 20.0D * d3 * d3);
    }
    for (j = 0; j < 30; j++) {
      localObject3[j] = 0.0D;
    }
    randomPhase((double[])localObject3, new Random(3049912L));
    ifft((double[])localObject3);
    normalize((double[])localObject3, 0.9D);
    localObject3 = realPart((double[])localObject3);
    double d2 = 1.0D;
    for (int m = 0; m < localObject3.length; m++)
    {
      localObject3[m] *= d2;
      d2 *= 0.9996D;
    }
    Object localObject2 = localObject3;
    for (i = 0; i < localObject2.length; i++) {
      localObject1[i] += localObject2[i] * 0.5D;
    }
    for (i = 0; i < 5; i++) {
      localObject1[i] *= i / 5.0D;
    }
    normalize((double[])localObject1, 0.99D);
    SF2Sample localSF2Sample = newSimpleDrumSample(paramSF2Soundbank, "Tom", (double[])localObject1);
    localSF2Sample.setOriginalPitch(50);
    localObject3 = new SF2Layer(paramSF2Soundbank);
    ((SF2Layer)localObject3).setName("Tom");
    localObject4 = new SF2GlobalRegion();
    ((SF2Layer)localObject3).setGlobalZone((SF2GlobalRegion)localObject4);
    paramSF2Soundbank.addResource((SoundbankResource)localObject3);
    SF2LayerRegion localSF2LayerRegion = new SF2LayerRegion();
    localSF2LayerRegion.putInteger(38, 12000);
    localSF2LayerRegion.putInteger(48, -100);
    localSF2LayerRegion.setSample(localSF2Sample);
    ((SF2Layer)localObject3).getRegions().add(localSF2LayerRegion);
    return (SF2Layer)localObject3;
  }
  
  public static SF2Layer new_closed_hihat(SF2Soundbank paramSF2Soundbank)
  {
    int i = 16384;
    Object localObject2 = new double[2 * i];
    Object localObject3 = new Random(3049912L);
    for (int j = 0; j < localObject2.length; j += 2) {
      localObject2[j] = (2.0D * (((Random)localObject3).nextDouble() - 0.5D) * 0.1D);
    }
    fft((double[])localObject2);
    for (j = i / 2; j < localObject2.length; j++) {
      localObject2[j] = 0.0D;
    }
    for (j = 4096; j < 8192; j++) {
      localObject2[j] = (1.0D - (j - 4096) / 4096.0D);
    }
    for (j = 0; j < 2048; j++) {
      localObject2[j] = (0.2D + 0.8D * (j / 2048.0D));
    }
    randomPhase((double[])localObject2, new Random(3049912L));
    ifft((double[])localObject2);
    normalize((double[])localObject2, 0.9D);
    localObject2 = realPart((double[])localObject2);
    double d = 1.0D;
    for (int k = 0; k < localObject2.length; k++)
    {
      localObject2[k] *= d;
      d *= 0.9996D;
    }
    Object localObject1 = localObject2;
    for (i = 0; i < 5; i++) {
      localObject1[i] *= i / 5.0D;
    }
    SF2Sample localSF2Sample = newSimpleDrumSample(paramSF2Soundbank, "Closed Hi-Hat", (double[])localObject1);
    localObject2 = new SF2Layer(paramSF2Soundbank);
    ((SF2Layer)localObject2).setName("Closed Hi-Hat");
    localObject3 = new SF2GlobalRegion();
    ((SF2Layer)localObject2).setGlobalZone((SF2GlobalRegion)localObject3);
    paramSF2Soundbank.addResource((SoundbankResource)localObject2);
    SF2LayerRegion localSF2LayerRegion = new SF2LayerRegion();
    localSF2LayerRegion.putInteger(38, 12000);
    localSF2LayerRegion.putInteger(56, 0);
    localSF2LayerRegion.putInteger(57, 1);
    localSF2LayerRegion.setSample(localSF2Sample);
    ((SF2Layer)localObject2).getRegions().add(localSF2LayerRegion);
    return (SF2Layer)localObject2;
  }
  
  public static SF2Layer new_open_hihat(SF2Soundbank paramSF2Soundbank)
  {
    int i = 16384;
    Object localObject2 = new double[2 * i];
    Object localObject3 = new Random(3049912L);
    for (int j = 0; j < localObject2.length; j += 2) {
      localObject2[j] = (2.0D * (((Random)localObject3).nextDouble() - 0.5D));
    }
    for (j = i / 2; j < localObject2.length; j++) {
      localObject2[j] = 0.0D;
    }
    for (j = 0; j < 200; j++) {
      localObject2[j] = 0.0D;
    }
    for (j = 0; j < 8192; j++)
    {
      double d = j / 8192.0D;
      localObject2[j] = d;
    }
    Object localObject1 = localObject2;
    SF2Sample localSF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Open Hi-Hat", (double[])localObject1, 1000.0D, 5);
    localObject2 = new SF2Layer(paramSF2Soundbank);
    ((SF2Layer)localObject2).setName("Open Hi-Hat");
    localObject3 = new SF2GlobalRegion();
    ((SF2Layer)localObject2).setGlobalZone((SF2GlobalRegion)localObject3);
    paramSF2Soundbank.addResource((SoundbankResource)localObject2);
    SF2LayerRegion localSF2LayerRegion = new SF2LayerRegion();
    localSF2LayerRegion.putInteger(36, 1500);
    localSF2LayerRegion.putInteger(54, 1);
    localSF2LayerRegion.putInteger(38, 1500);
    localSF2LayerRegion.putInteger(37, 1000);
    localSF2LayerRegion.putInteger(56, 0);
    localSF2LayerRegion.putInteger(57, 1);
    localSF2LayerRegion.setSample(localSF2Sample);
    ((SF2Layer)localObject2).getRegions().add(localSF2LayerRegion);
    return (SF2Layer)localObject2;
  }
  
  public static SF2Layer new_crash_cymbal(SF2Soundbank paramSF2Soundbank)
  {
    int i = 16384;
    Object localObject2 = new double[2 * i];
    Object localObject3 = new Random(3049912L);
    for (int j = 0; j < localObject2.length; j += 2) {
      localObject2[j] = (2.0D * (((Random)localObject3).nextDouble() - 0.5D));
    }
    for (j = i / 2; j < localObject2.length; j++) {
      localObject2[j] = 0.0D;
    }
    for (j = 0; j < 100; j++) {
      localObject2[j] = 0.0D;
    }
    for (j = 0; j < 1024; j++)
    {
      double d = j / 1024.0D;
      localObject2[j] = d;
    }
    Object localObject1 = localObject2;
    SF2Sample localSF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Crash Cymbal", (double[])localObject1, 1000.0D, 5);
    localObject2 = new SF2Layer(paramSF2Soundbank);
    ((SF2Layer)localObject2).setName("Crash Cymbal");
    localObject3 = new SF2GlobalRegion();
    ((SF2Layer)localObject2).setGlobalZone((SF2GlobalRegion)localObject3);
    paramSF2Soundbank.addResource((SoundbankResource)localObject2);
    SF2LayerRegion localSF2LayerRegion = new SF2LayerRegion();
    localSF2LayerRegion.putInteger(36, 1800);
    localSF2LayerRegion.putInteger(54, 1);
    localSF2LayerRegion.putInteger(38, 1800);
    localSF2LayerRegion.putInteger(37, 1000);
    localSF2LayerRegion.putInteger(56, 0);
    localSF2LayerRegion.setSample(localSF2Sample);
    ((SF2Layer)localObject2).getRegions().add(localSF2LayerRegion);
    return (SF2Layer)localObject2;
  }
  
  public static SF2Layer new_side_stick(SF2Soundbank paramSF2Soundbank)
  {
    int i = 16384;
    Object localObject2 = new double[2 * i];
    Object localObject3 = new Random(3049912L);
    for (int j = 0; j < localObject2.length; j += 2) {
      localObject2[j] = (2.0D * (((Random)localObject3).nextDouble() - 0.5D) * 0.1D);
    }
    fft((double[])localObject2);
    for (j = i / 2; j < localObject2.length; j++) {
      localObject2[j] = 0.0D;
    }
    for (j = 4096; j < 8192; j++) {
      localObject2[j] = (1.0D - (j - 4096) / 4096.0D);
    }
    for (j = 0; j < 200; j++)
    {
      double d2 = 1.0D - j / 200.0D;
      localObject2[j] *= (1.0D + 20.0D * d2 * d2);
    }
    for (j = 0; j < 30; j++) {
      localObject2[j] = 0.0D;
    }
    randomPhase((double[])localObject2, new Random(3049912L));
    ifft((double[])localObject2);
    normalize((double[])localObject2, 0.9D);
    localObject2 = realPart((double[])localObject2);
    double d1 = 1.0D;
    for (int k = 0; k < localObject2.length; k++)
    {
      localObject2[k] *= d1;
      d1 *= 0.9996D;
    }
    Object localObject1 = localObject2;
    for (i = 0; i < 10; i++) {
      localObject1[i] *= i / 10.0D;
    }
    SF2Sample localSF2Sample = newSimpleDrumSample(paramSF2Soundbank, "Side Stick", (double[])localObject1);
    localObject2 = new SF2Layer(paramSF2Soundbank);
    ((SF2Layer)localObject2).setName("Side Stick");
    localObject3 = new SF2GlobalRegion();
    ((SF2Layer)localObject2).setGlobalZone((SF2GlobalRegion)localObject3);
    paramSF2Soundbank.addResource((SoundbankResource)localObject2);
    SF2LayerRegion localSF2LayerRegion = new SF2LayerRegion();
    localSF2LayerRegion.putInteger(38, 12000);
    localSF2LayerRegion.putInteger(56, 0);
    localSF2LayerRegion.putInteger(48, -50);
    localSF2LayerRegion.setSample(localSF2Sample);
    ((SF2Layer)localObject2).getRegions().add(localSF2LayerRegion);
    return (SF2Layer)localObject2;
  }
  
  public static SF2Sample newSimpleFFTSample(SF2Soundbank paramSF2Soundbank, String paramString, double[] paramArrayOfDouble, double paramDouble)
  {
    return newSimpleFFTSample(paramSF2Soundbank, paramString, paramArrayOfDouble, paramDouble, 10);
  }
  
  public static SF2Sample newSimpleFFTSample(SF2Soundbank paramSF2Soundbank, String paramString, double[] paramArrayOfDouble, double paramDouble, int paramInt)
  {
    int i = paramArrayOfDouble.length / 2;
    AudioFormat localAudioFormat = new AudioFormat(44100.0F, 16, 1, true, false);
    double d1 = paramDouble / i * localAudioFormat.getSampleRate() * 0.5D;
    randomPhase(paramArrayOfDouble);
    ifft(paramArrayOfDouble);
    paramArrayOfDouble = realPart(paramArrayOfDouble);
    normalize(paramArrayOfDouble, 0.9D);
    float[] arrayOfFloat = toFloat(paramArrayOfDouble);
    arrayOfFloat = loopExtend(arrayOfFloat, arrayOfFloat.length + 512);
    fadeUp(arrayOfFloat, paramInt);
    byte[] arrayOfByte = toBytes(arrayOfFloat, localAudioFormat);
    SF2Sample localSF2Sample = new SF2Sample(paramSF2Soundbank);
    localSF2Sample.setName(paramString);
    localSF2Sample.setData(arrayOfByte);
    localSF2Sample.setStartLoop(256L);
    localSF2Sample.setEndLoop(i + 256);
    localSF2Sample.setSampleRate(localAudioFormat.getSampleRate());
    double d2 = 81.0D + 12.0D * Math.log(d1 / 440.0D) / Math.log(2.0D);
    localSF2Sample.setOriginalPitch((int)d2);
    localSF2Sample.setPitchCorrection((byte)(int)(-(d2 - (int)d2) * 100.0D));
    paramSF2Soundbank.addResource(localSF2Sample);
    return localSF2Sample;
  }
  
  public static SF2Sample newSimpleFFTSample_dist(SF2Soundbank paramSF2Soundbank, String paramString, double[] paramArrayOfDouble, double paramDouble1, double paramDouble2)
  {
    int i = paramArrayOfDouble.length / 2;
    AudioFormat localAudioFormat = new AudioFormat(44100.0F, 16, 1, true, false);
    double d1 = paramDouble1 / i * localAudioFormat.getSampleRate() * 0.5D;
    randomPhase(paramArrayOfDouble);
    ifft(paramArrayOfDouble);
    paramArrayOfDouble = realPart(paramArrayOfDouble);
    for (int j = 0; j < paramArrayOfDouble.length; j++) {
      paramArrayOfDouble[j] = ((1.0D - Math.exp(-Math.abs(paramArrayOfDouble[j] * paramDouble2))) * Math.signum(paramArrayOfDouble[j]));
    }
    normalize(paramArrayOfDouble, 0.9D);
    float[] arrayOfFloat = toFloat(paramArrayOfDouble);
    arrayOfFloat = loopExtend(arrayOfFloat, arrayOfFloat.length + 512);
    fadeUp(arrayOfFloat, 80);
    byte[] arrayOfByte = toBytes(arrayOfFloat, localAudioFormat);
    SF2Sample localSF2Sample = new SF2Sample(paramSF2Soundbank);
    localSF2Sample.setName(paramString);
    localSF2Sample.setData(arrayOfByte);
    localSF2Sample.setStartLoop(256L);
    localSF2Sample.setEndLoop(i + 256);
    localSF2Sample.setSampleRate(localAudioFormat.getSampleRate());
    double d2 = 81.0D + 12.0D * Math.log(d1 / 440.0D) / Math.log(2.0D);
    localSF2Sample.setOriginalPitch((int)d2);
    localSF2Sample.setPitchCorrection((byte)(int)(-(d2 - (int)d2) * 100.0D));
    paramSF2Soundbank.addResource(localSF2Sample);
    return localSF2Sample;
  }
  
  public static SF2Sample newSimpleDrumSample(SF2Soundbank paramSF2Soundbank, String paramString, double[] paramArrayOfDouble)
  {
    int i = paramArrayOfDouble.length;
    AudioFormat localAudioFormat = new AudioFormat(44100.0F, 16, 1, true, false);
    byte[] arrayOfByte = toBytes(toFloat(realPart(paramArrayOfDouble)), localAudioFormat);
    SF2Sample localSF2Sample = new SF2Sample(paramSF2Soundbank);
    localSF2Sample.setName(paramString);
    localSF2Sample.setData(arrayOfByte);
    localSF2Sample.setStartLoop(256L);
    localSF2Sample.setEndLoop(i + 256);
    localSF2Sample.setSampleRate(localAudioFormat.getSampleRate());
    localSF2Sample.setOriginalPitch(60);
    paramSF2Soundbank.addResource(localSF2Sample);
    return localSF2Sample;
  }
  
  public static SF2Layer newLayer(SF2Soundbank paramSF2Soundbank, String paramString, SF2Sample paramSF2Sample)
  {
    SF2LayerRegion localSF2LayerRegion = new SF2LayerRegion();
    localSF2LayerRegion.setSample(paramSF2Sample);
    SF2Layer localSF2Layer = new SF2Layer(paramSF2Soundbank);
    localSF2Layer.setName(paramString);
    localSF2Layer.getRegions().add(localSF2LayerRegion);
    paramSF2Soundbank.addResource(localSF2Layer);
    return localSF2Layer;
  }
  
  public static SF2Instrument newInstrument(SF2Soundbank paramSF2Soundbank, String paramString, Patch paramPatch, SF2Layer... paramVarArgs)
  {
    SF2Instrument localSF2Instrument = new SF2Instrument(paramSF2Soundbank);
    localSF2Instrument.setPatch(paramPatch);
    localSF2Instrument.setName(paramString);
    paramSF2Soundbank.addInstrument(localSF2Instrument);
    for (int i = 0; i < paramVarArgs.length; i++)
    {
      SF2InstrumentRegion localSF2InstrumentRegion = new SF2InstrumentRegion();
      localSF2InstrumentRegion.setLayer(paramVarArgs[i]);
      localSF2Instrument.getRegions().add(localSF2InstrumentRegion);
    }
    return localSF2Instrument;
  }
  
  public static void ifft(double[] paramArrayOfDouble)
  {
    new FFT(paramArrayOfDouble.length / 2, 1).transform(paramArrayOfDouble);
  }
  
  public static void fft(double[] paramArrayOfDouble)
  {
    new FFT(paramArrayOfDouble.length / 2, -1).transform(paramArrayOfDouble);
  }
  
  public static void complexGaussianDist(double[] paramArrayOfDouble, double paramDouble1, double paramDouble2, double paramDouble3)
  {
    for (int i = 0; i < paramArrayOfDouble.length / 4; i++) {
      paramArrayOfDouble[(i * 2)] += paramDouble3 * (1.0D / (paramDouble2 * Math.sqrt(6.283185307179586D)) * Math.exp(-0.5D * Math.pow((i - paramDouble1) / paramDouble2, 2.0D)));
    }
  }
  
  public static void randomPhase(double[] paramArrayOfDouble)
  {
    for (int i = 0; i < paramArrayOfDouble.length; i += 2)
    {
      double d1 = Math.random() * 2.0D * 3.141592653589793D;
      double d2 = paramArrayOfDouble[i];
      paramArrayOfDouble[i] = (Math.sin(d1) * d2);
      paramArrayOfDouble[(i + 1)] = (Math.cos(d1) * d2);
    }
  }
  
  public static void randomPhase(double[] paramArrayOfDouble, Random paramRandom)
  {
    for (int i = 0; i < paramArrayOfDouble.length; i += 2)
    {
      double d1 = paramRandom.nextDouble() * 2.0D * 3.141592653589793D;
      double d2 = paramArrayOfDouble[i];
      paramArrayOfDouble[i] = (Math.sin(d1) * d2);
      paramArrayOfDouble[(i + 1)] = (Math.cos(d1) * d2);
    }
  }
  
  public static void normalize(double[] paramArrayOfDouble, double paramDouble)
  {
    double d1 = 0.0D;
    for (int i = 0; i < paramArrayOfDouble.length; i++)
    {
      if (paramArrayOfDouble[i] > d1) {
        d1 = paramArrayOfDouble[i];
      }
      if (-paramArrayOfDouble[i] > d1) {
        d1 = -paramArrayOfDouble[i];
      }
    }
    if (d1 == 0.0D) {
      return;
    }
    double d2 = paramDouble / d1;
    for (int j = 0; j < paramArrayOfDouble.length; j++) {
      paramArrayOfDouble[j] *= d2;
    }
  }
  
  public static void normalize(float[] paramArrayOfFloat, double paramDouble)
  {
    double d1 = 0.5D;
    for (int i = 0; i < paramArrayOfFloat.length; i++)
    {
      if (paramArrayOfFloat[(i * 2)] > d1) {
        d1 = paramArrayOfFloat[(i * 2)];
      }
      if (-paramArrayOfFloat[(i * 2)] > d1) {
        d1 = -paramArrayOfFloat[(i * 2)];
      }
    }
    double d2 = paramDouble / d1;
    for (int j = 0; j < paramArrayOfFloat.length; j++)
    {
      int tmp82_81 = (j * 2);
      paramArrayOfFloat[tmp82_81] = ((float)(paramArrayOfFloat[tmp82_81] * d2));
    }
  }
  
  public static double[] realPart(double[] paramArrayOfDouble)
  {
    double[] arrayOfDouble = new double[paramArrayOfDouble.length / 2];
    for (int i = 0; i < arrayOfDouble.length; i++) {
      arrayOfDouble[i] = paramArrayOfDouble[(i * 2)];
    }
    return arrayOfDouble;
  }
  
  public static double[] imgPart(double[] paramArrayOfDouble)
  {
    double[] arrayOfDouble = new double[paramArrayOfDouble.length / 2];
    for (int i = 0; i < arrayOfDouble.length; i++) {
      arrayOfDouble[i] = paramArrayOfDouble[(i * 2)];
    }
    return arrayOfDouble;
  }
  
  public static float[] toFloat(double[] paramArrayOfDouble)
  {
    float[] arrayOfFloat = new float[paramArrayOfDouble.length];
    for (int i = 0; i < arrayOfFloat.length; i++) {
      arrayOfFloat[i] = ((float)paramArrayOfDouble[i]);
    }
    return arrayOfFloat;
  }
  
  public static byte[] toBytes(float[] paramArrayOfFloat, AudioFormat paramAudioFormat)
  {
    byte[] arrayOfByte = new byte[paramArrayOfFloat.length * paramAudioFormat.getFrameSize()];
    return AudioFloatConverter.getConverter(paramAudioFormat).toByteArray(paramArrayOfFloat, arrayOfByte);
  }
  
  public static void fadeUp(double[] paramArrayOfDouble, int paramInt)
  {
    double d = paramInt;
    for (int i = 0; i < paramInt; i++) {
      paramArrayOfDouble[i] *= i / d;
    }
  }
  
  public static void fadeUp(float[] paramArrayOfFloat, int paramInt)
  {
    double d = paramInt;
    for (int i = 0; i < paramInt; tmp15_13++)
    {
      int tmp15_13 = i;
      paramArrayOfFloat[tmp15_13] = ((float)(paramArrayOfFloat[tmp15_13] * (tmp15_13 / d)));
    }
  }
  
  public static double[] loopExtend(double[] paramArrayOfDouble, int paramInt)
  {
    double[] arrayOfDouble = new double[paramInt];
    int i = paramArrayOfDouble.length;
    int j = 0;
    for (int k = 0; k < arrayOfDouble.length; k++)
    {
      arrayOfDouble[k] = paramArrayOfDouble[j];
      j++;
      if (j == i) {
        j = 0;
      }
    }
    return arrayOfDouble;
  }
  
  public static float[] loopExtend(float[] paramArrayOfFloat, int paramInt)
  {
    float[] arrayOfFloat = new float[paramInt];
    int i = paramArrayOfFloat.length;
    int j = 0;
    for (int k = 0; k < arrayOfFloat.length; k++)
    {
      arrayOfFloat[k] = paramArrayOfFloat[j];
      j++;
      if (j == i) {
        j = 0;
      }
    }
    return arrayOfFloat;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\EmergencySoundbank.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */