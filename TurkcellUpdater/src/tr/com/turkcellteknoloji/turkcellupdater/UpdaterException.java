package tr.com.turkcellteknoloji.turkcellupdater;

public class UpdaterException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UpdaterException() {
	}

	public UpdaterException(String detailMessage) {
		super(detailMessage);
	}

	public UpdaterException(Throwable throwable) {
		super(throwable);
	}

	public UpdaterException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

}
