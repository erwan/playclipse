package org.playframework.playclipse.tests;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.eclipse.swt.widgets.Display;

class HttpThread extends Thread {
	private IHttpListener listener;
	public String message;
	private String url;
	private String callId;

	public HttpThread(IHttpListener listener, String url, String callId) {
		this.listener = listener;
		this.url = url;
		this.callId = callId;
	}

	@Override
	public void run() {
		try {
			// final String result = getPageContent("http://localhost:9000/@tests?select=all&auto=yes");
			final String result = getPageContent(url);
			Display.getDefault().asyncExec(new Runnable() {
				 public void run() {
					listener.onSuccess(result, callId);
				}
			});
		} catch (HttpHostConnectException e) {
			message = "Application not running. Make sure your application is running in test mode.";
			System.out.println(message);
			listener.onError(0, callId);
		} catch (HttpResponseException e) {
			if (e.getStatusCode() == 404) {
				// 404 probably means the server is not running in test mode
				message = "Error 404. Your application may be running but not in test mode.";
				System.out.println(message);
			}
			listener.onError(e.getStatusCode(), callId);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message = "Unknown error: " + e.getMessage();
			System.out.println(message);
			listener.onError(0, callId);
		}
	}

	private String getPageContent(String url) throws ClientProtocolException, IOException {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		String responseBody = httpclient.execute(httpget, responseHandler);
		httpclient.getConnectionManager().shutdown();
		return responseBody;
	}

}
