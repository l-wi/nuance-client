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
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				recorder.stop();
			}
		};

		recorder.record(new File("testRecord.wav"));
		Thread t = new Thread(timer);
		t.start();
		t.join();

		
		recorder.stop();

		
	
	}

	private static void nuanceLookup() throws IOException, FileNotFoundException, NuanceClientException {
		Properties props = new Properties();
		props.load(new FileInputStream("credentials.properties"));

		NuanceCredentials creds = new NuanceCredentials(props.getProperty("appKey"), props.getProperty("appId"),
				props.getProperty("deviceId"));

		NuanceClient client = new NuanceClient(creds);
		File f = new File("./test.wav");

		BufferedInputStream fileInputStream = new BufferedInputStream(new FileInputStream(f));

		System.out.println(client.request(fileInputStream, CODEC));
	}
}
