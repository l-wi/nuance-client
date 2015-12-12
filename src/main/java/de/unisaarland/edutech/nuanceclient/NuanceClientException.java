package de.unisaarland.edutech.nuanceclient;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;

public class NuanceClientException extends IOException {

	public NuanceClientException(NuanceCredentials creds, Exception ex) {
		super("There was an error in the request with credentials:\n " + creds
				+ "\nSee incapsuled exception below for more details", ex);
	}

	public NuanceClientException(HttpResponse resp, NuanceCredentials creds) {
		super("There was no data in the response for the request with credentials:\n " + creds
				+ "\n see the response for more details:\n----------------" + resp + "-----------\n\n");
	}

	public NuanceClientException(StatusLine statusLine, NuanceCredentials creds, String body) {
		super("Bad response status" + statusLine + " \t with creds: " + creds + "message body" + body);
	}
}
