package de.unisaarland.edutech.nuanceclient;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class AudioRecorder {

	private Thread recordingThread;
	private AudioInputStream dest;

	public void record(File f) {

		Runnable recordingRunnable = new Runnable() {

			@Override
			public void run() {

				AudioFormat format = getMicAudioFormat();
				DataLine.Info info = getDataLineAndthrowIfUnsupportedFormat(format);

				try (TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info)) {
					line.open(format);

					// Begin audio capture.
					line.start();

					dest = new AudioInputStream(line);

					// start recording
					AudioSystem.write(dest, AudioFileFormat.Type.WAVE, f);

				} catch (LineUnavailableException | IOException ex) {
					// TODO exception handling
					throw new RuntimeException(ex);
				}

			}
		};

		recordingThread = new Thread(recordingRunnable);
		recordingThread.start();

	}

	public void stop() {
		try {

			dest.close();
			recordingThread.join(2000);
		} catch (InterruptedException | IOException e) {
			// TODO better exception handling
			throw new RuntimeException("Recording failed to stop after 2 seconds!", e);
		}
	}

	private DataLine.Info getDataLineAndthrowIfUnsupportedFormat(AudioFormat format) {
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

		if (!AudioSystem.isLineSupported(info))
			// TODO better exception handling
			throw new RuntimeException("Non supported Data line" + info);

		return info;
	}

	private AudioFormat getMicAudioFormat() {
		float sampleRate = 16000.0F;
		int sampleSizeInBits = 16;
		int channels = 1;
		boolean signed = true;
		boolean bigEndian = true;
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);

	}

}
