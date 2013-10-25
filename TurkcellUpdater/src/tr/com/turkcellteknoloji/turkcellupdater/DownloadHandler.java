package tr.com.turkcellteknoloji.turkcellupdater;

interface DownloadHandler {
	void onFail(Exception ex);
	void onCancelled();
	void onProgress(Integer percent);
	void onSuccess(byte[] result);
}
