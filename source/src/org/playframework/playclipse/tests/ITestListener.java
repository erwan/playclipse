package org.playframework.playclipse.tests;

public interface ITestListener {

	public void onSuccess(String status, String callId);

	public void onError(int status, String callId);

}
