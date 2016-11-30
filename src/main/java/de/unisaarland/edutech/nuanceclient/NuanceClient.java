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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

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

	public void requestAsync(InputStream input, String enc, Consumer<Result> handler) {

		Supplier<Result> s = () -> {
			Result result = new Result();

			try {
				List<String> resultSet = this.request(input, enc);
				result.resultSet = resultSet;
			} catch (NuanceClientException e) {
				result.exception = e;
			}
			return result;
		};

		Function<Throwable, Result> exHandler = (ex) -> {
			Result result = new Result();
			result.exception = ex;
			return result;
		};

		CompletableFuture.supplyAsync(s).exceptionally(exHandler).thenAccept(handler);

	}

	private List<String> parseResponse(HttpResponse response) throws NuanceClientException {
		HttpEntity entity = throwIfNoResponseEntity(response);

		List<String> result = new ArrayList<String>();

		if (response.getStatusLine().getStatusCode() != 200) {
			StringBuilder builder = new StringBuilder();
			parseResultOrThrow(response, entity, (x) -> {
				builder.append(x);
			});
			throw new NuanceClientException(response.getStatusLine(), creds, builder.toString());
		} else
			parseResultOrThrow(response, entity, (x) -> {
				result.add(x);
			});

		return result;
	}

	private void parseResultOrThrow(HttpResponse response, HttpEntity entity, Consumer<String> handler)
			throws NuanceClientException {
		try {
			parseResult(entity, handler);
		} catch (IOException e) {
			throw new NuanceClientException(creds, e);
		}
	}

	private void parseResult(HttpEntity entity, Consumer<String> h) throws IOException {

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()))) {
			String line = null;

			while ((line = reader.readLine()) != null) {
				h.accept(line);
			}

			EntityUtils.consume(entity);

		}
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

	public class Result {
		public String errorMsg;
		public List<String> resultSet;
		public Throwable exception;

		public boolean isSuccessful() {
			return errorMsg == null && exception == null;
		}
	}
}
