package de.unisaarland.edutech.nuanceclient;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Main {

	private static final String CODEC = "audio/x-wav;codec=pcm;bit=16;rate=16000";

	public static void main(String[] args) throws IOException, NuanceClientException {

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
