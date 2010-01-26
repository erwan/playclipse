package org.playframework.playclipse.tests;

public interface IHttpListener {

	/**
	 * Called when the http call is successful.
	 * @param result the body of the response
	 * @param callId the id of the call, as passed to the constructor of HttpThread
	 * @see org.playframework.playclipse.tests.HttpThread
	 */
	public void onSuccess(String result, String callId);

	public void onError(int status, String callId);

}
