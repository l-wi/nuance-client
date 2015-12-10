package de.unisaarland.edutech.nuanceclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class NuanceClient {

	private NuanceCredentials creds;

	private HttpClient client;

	public NuanceClient(NuanceCredentials creds) {
		this.creds = creds;
		this.client = HttpClientBuilder.create().build();

	}

	public List<String> request(InputStream input, String enc) throws NuanceClientException {
		try {
			// create a connection to the API
			HttpPost postReq = newPostRequest(enc);

			// execute query
			InputStreamEntity entity = createInputStreamEntity(input, enc);
			postReq.setEntity(entity);

			HttpResponse response = client.execute(postReq);

			return parseResponse(response);
		} catch (Exception ex) {
			throw new NuanceClientException(creds, ex);
		}

	}

	private List<String> parseResponse(HttpResponse response) throws UnsupportedOperationException, IOException {
		HttpEntity entity = throwIfNoResponseEntity(response);

		List<String> result = new ArrayList<String>();

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()))) {
			String line = null;

			while ((line = reader.readLine()) != null) {
				result.add(line);
			}

			EntityUtils.consume(entity);

		}

		// TODO what if there is no result?
		return result;
	}

	private HttpEntity throwIfNoResponseEntity(HttpResponse response) throws NuanceClientException {
		HttpEntity entity = response.getEntity();

		if (entity == null)
			throw new NuanceClientException(response, creds);

		return entity;
	}

	private HttpPost newPostRequest(String enc) throws URISyntaxException {

		HttpPost httppost = new HttpPost(creds.getURI());

		httppost.addHeader("Content-Type", enc);
		httppost.addHeader("Content-Language", creds.getLang());
		httppost.addHeader("Accept-Language", creds.getLang());
		httppost.addHeader("Accept", creds.getResultFormat());
		httppost.addHeader("Accept-Topic", creds.getTopic());

		return httppost;

	}

	private InputStreamEntity createInputStreamEntity(InputStream stream, String enc) {
		InputStreamEntity entity = new InputStreamEntity(stream);

		entity.setChunked(true);
		entity.setContentType(enc);
		return entity;
	}

}
