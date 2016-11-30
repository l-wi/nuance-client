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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class Main {

	private static final String CODEC = "audio/x-wav;codec=pcm;bit=16;rate=16000";

	public static void main(String[] args)
			throws IOException, NuanceClientException, InterruptedException, ExecutionException {

		AudioRecorder recorder = new AudioRecorder();

		long sleep = 1000 * 10;

		Runnable timer = new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(sleep);
					recorder.stop();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (RecordingException e) {

					e.printStackTrace();
				}
		
			}
		};

		recorder.record(new File("testRecord.wav"));
		Thread t = new Thread(timer);
		t.start();
		t.join();

		
		

//		nuanceLookup();
	
	}

	private static void nuanceLookup() throws IOException, FileNotFoundException, NuanceClientException {
		Properties props = new Properties();
		props.load(new FileInputStream("credentials.properties"));

		NuanceCredentials creds = new NuanceCredentials(props.getProperty("appKey"), props.getProperty("appId"),
				props.getProperty("deviceID"));

		NuanceClient client = new NuanceClient(creds);
		File f = new File("./test.wav");

		BufferedInputStream fileInputStream = new BufferedInputStream(new FileInputStream(f));

		System.out.println(client.request(fileInputStream, CODEC));
	}
}
