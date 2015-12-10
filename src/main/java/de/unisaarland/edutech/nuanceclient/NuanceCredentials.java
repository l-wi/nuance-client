package de.unisaarland.edutech.nuanceclient;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;

public class NuanceCredentials {

	private static final String ENCODING = "UTF-8";
	private static final String PROTOCOL = "https";
	public final String APP_KEY;
	public final String APP_ID;
	public final String DEVICE_ID;

	private String host = "dictation.nuancemobility.net";
	private String servlet = "/NMDPAsrCmdServlet/dictation";
	private int port = 443;

	private String lang = "de_DE";
	private String resultFormat = "text/plain";
	private String topic = "Dictation";

	public NuanceCredentials(String apiKey, String appId, String deviceId) {
		DEVICE_ID = deviceId;
		APP_KEY = apiKey;
		APP_ID = appId;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getServlet() {
		return servlet;
	}

	public void setServlet(String servlet) {
		this.servlet = servlet;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getResultFormat() {
		return resultFormat;
	}

	public void setResultFormat(String resultFormat) {
		this.resultFormat = resultFormat;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public URI getURI() throws URISyntaxException {
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();

		qparams.add(new BasicNameValuePair("appId", this.APP_ID));
		qparams.add(new BasicNameValuePair("appKey", this.APP_KEY));
		qparams.add(new BasicNameValuePair("id", this.DEVICE_ID));

		return new URIBuilder().setScheme(PROTOCOL).setHost(host).setPort(port).setPath(servlet).addParameters(qparams)
				.setCharset(Charset.forName(ENCODING)).build();

	}

	public static NuanceCredentials construct() throws IOException {
		Properties props = new Properties();
		props.load(new FileInputStream("credentials.properties"));
		return new NuanceCredentials(props.getProperty("appKey"), props.getProperty("appId"),
				props.getProperty("deviceId"));
	}

	@Override
	public String toString() {
		return "NuanceCredentials [APP_KEY=" + APP_KEY + ", APP_ID=" + APP_ID + ", DEVICE_ID=" + DEVICE_ID + ", host="
				+ host + ", servlet=" + servlet + ", port=" + port + ", lang=" + lang + ", resultFormat=" + resultFormat
				+ ", topic=" + topic + "]";
	}

}
