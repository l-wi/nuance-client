package de.unisaarland.edutech.nuanceclient;

public class RecordingException extends Exception {

	public RecordingException(Exception recordingException) {
		super("Recording did not work because of: " ,recordingException);
	}

}
