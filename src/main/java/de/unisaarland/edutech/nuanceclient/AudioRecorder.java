/*******************************************************************************
 * nuance-client a simple java client for the nuance cloud ASR service.
 * Copyright (C) Tim Steuer (master's thesis 2016)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, US
 *******************************************************************************/
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
	private Exception recordingException;

	public void record(File f) {

		Runnable recordingRunnable = new Runnable() {

			@Override
			public void run() {


				AudioFormat format = getMicAudioFormat();

				DataLine.Info info = getDataLineInfoOrSetException(format);

				if (info == null)
					return;

				recordOrSetException(f, format, info);

			}
		};

		recordingThread = new Thread(recordingRunnable);
		recordingThread.start();

	}

	public void stop() throws RecordingException {
		try {
			if (recordingException != null)
				throw new RecordingException(recordingException);

			dest.close();
			recordingThread.join(2000);
			
			recordingException = null;
			dest = null;
			recordingThread = null;
			
		} catch (InterruptedException | IOException e) {
			throw new RecordingException(e);
		}
	}

	private void recordOrSetException(File f, AudioFormat format, DataLine.Info info) {

		try (TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info)) {
			line.open(format);

			// Begin audio capture.
			line.start();

			dest = new AudioInputStream(line);

			// start recording
			AudioSystem.write(dest, AudioFileFormat.Type.WAVE, f);

		} catch (LineUnavailableException | IOException ex) {
			recordingException = ex;

		}
	}

	private DataLine.Info getDataLineInfoOrSetException(AudioFormat format) {
		try {
			DataLine.Info info = getDataLineAndthrowIfUnsupportedFormat(format);
			return info;
		} catch (IOException ex) {
			recordingException = ex;
		}
		return null;
	}

	private DataLine.Info getDataLineAndthrowIfUnsupportedFormat(AudioFormat format) throws IOException {
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

		if (!AudioSystem.isLineSupported(info))
			throw new IOException("Non supported Data line" + info);

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

	public String getActiveEncoding() {
		return "audio/x-wav;codec=pcm;bit=16;rate=16000";

	}

}
