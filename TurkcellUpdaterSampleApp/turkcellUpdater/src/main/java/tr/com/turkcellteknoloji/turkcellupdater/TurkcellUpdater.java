package tr.com.turkcellteknoloji.turkcellupdater;

import android.app.Activity;
import android.net.Uri;

import java.net.URI;

import tr.com.turkcellteknoloji.turkcellupdater.UpdateManager.UpdateCheckListener;
import tr.com.turkcellteknoloji.turkcellupdater.UpdaterDialogManager.UpdaterUiListener;

/**
 * Turkcell Android uygulamalarinin guncelleme durumlarini kontrol eder ve otomatik yonlendirmeler saglar.
 *
 * @author Ibrahim Menekse
 */
public final class TurkcellUpdater {

    /**
     * Guncelleme kontrolu yapildiktan sonra tetiklenen metodlari barindirir.
     * Bu callback, SDK'nin varsayilan dialog'u gosterilmeyip guncelleme durumlari manual islenecegi durumda TurkcellUpdater instance'ina set'lenir.
     */
    public interface TurkcellUpdaterCallback {

        /**
         * Zorunlu guncelleme oldugunda tetiklenir.
         *
         * @param message
         * @param warnings
         * @param whatIsNew
         */
        void onForceUpdateReceive(String message, String warnings, String whatIsNew);

        /**
         * Hizmet kullanilamadigi durumda tetiklenir.
         *
         * @param message
         * @param warnings
         * @param whatIsNew
         */
        void onForceExitReceive(String message, String warnings, String whatIsNew);

        /**
         * Zorunlu olmayan guncelleme durumunda tetiklenir.
         *
         * @param message
         * @param warnings
         * @param whatIsNew
         */
        void onNonForceUpdateReceive(String message, String warnings, String whatIsNew);

        /**
         * Kullaniciya gosterilmesi gereken mesaj geldiginde tetiklenir.
         *
         * @param title
         * @param message
         * @param imageUrl
         * @param redirectionUri
         */
        void onMessageReceive(String title, String message, String imageUrl, Uri redirectionUri);

        /**
         * Hicbir guncelleme ve mesaj olmadigi durumda tetiklenir.
         */
        void onUpdateNotFoundReceive();

        /**
         * Guncelleme sunucusuna baglanilamadiginda yada bir hata ile karsilasildiginda tetiklenir.
         *
         * @param e
         */
        void onUpdaterErrorReceive(Exception e);
    }

    private static final String GOOGLE_PLAY_BASE_URL = "http://play.google.com/store/apps/details?id=";

    private Activity mActivity;
    private Properties mProperties;
    private UpdateManager mUpdateManager;
    private String mUpdateServerUrl;
    private TurkcellUpdaterCallback mTurkcellUpdaterCallback;
    private UpdaterUiListener mDefaultDialogCallback;

    public TurkcellUpdater(Activity activity, String updaterServerUrl) {
        mActivity = activity;
        mUpdateServerUrl = updaterServerUrl;
        mProperties = new Properties(mActivity);
        mUpdateManager = new UpdateManager();
    }

    /**
     * Kontrol sonrasi islemler manual yapilacaksa bu metod ile {@link TurkcellUpdaterCallback) callback'i set'lenir.
     *
     * @param callback
     */
    public void setTurkcellUpdaterCallback(TurkcellUpdaterCallback callback) {
        mTurkcellUpdaterCallback = callback;
    }

    /**
     * SDK'nin varsayilan dialog'u gosterilerek otomatik yonlendirme yapilacaksa bu metod ile {@link UpdaterUiListener} callback'i set'lenir.
     *
     * @param listener
     */
    public void setDefaultDialogCallback(UpdaterUiListener listener) {
        mDefaultDialogCallback = listener;
    }

    /**
     * Guncelleme kontrolunu baslatir. Bu metod cagrilmadan once callback set'lenmelidir.
     *
     * @param showDefaultDialog SDK'nin varsayilan dialog'u gosterilerek otomatik yonlendirme yapilacaksa true,
     *                          kontrol sonrasi islemler manual yapilacaksa false gonderilmesi gerekli.
     */
    public void check(boolean showDefaultDialog) {
        if (showDefaultDialog) {
            if (mDefaultDialogCallback == null) {
                throw new RuntimeException("You should set UpdaterUiListener (default dialog callback) before check");
            }
            UpdaterDialogManager updaterUI = new UpdaterDialogManager(mUpdateServerUrl);
            updaterUI.startUpdateCheck(mActivity, mDefaultDialogCallback);
        } else {
            if (mTurkcellUpdaterCallback == null) {
                throw new RuntimeException("You should set TurkcellUpdaterCallback before check");
            }
            try {
                URI versionServerUri = new URI(mUpdateServerUrl);
                mUpdateManager.checkUpdates(mActivity, versionServerUri, mProperties, true, mUpdateCheckListener);
            } catch (Exception e) {
                if (mTurkcellUpdaterCallback != null) {
                    mTurkcellUpdaterCallback.onUpdaterErrorReceive(e);
                }
            }
        }
    }

    private UpdateCheckListener mUpdateCheckListener = new UpdateCheckListener() {
        @Override
        public void onUpdateCheckCompleted(UpdateManager manager, Update update) {
            UpdateDescription description = update.description;
            String warnings = null;
            String message = null;
            String whatIsNew = null;
            if (description != null) {
                warnings = update.description.get(UpdateDescription.KEY_WARNINGS);
                message = update.description.get(UpdateDescription.KEY_MESSAGE);
                whatIsNew = update.description.get(UpdateDescription.KEY_WHAT_IS_NEW);
            }
            if (update.forceExit) {
                if (mTurkcellUpdaterCallback != null) {
                    mTurkcellUpdaterCallback.onForceExitReceive(message, warnings, whatIsNew);
                }
            } else if (update.forceUpdate) {
                if (mTurkcellUpdaterCallback != null) {
                    mTurkcellUpdaterCallback.onForceUpdateReceive(message, warnings, whatIsNew);
                }
            } else {
                if (mTurkcellUpdaterCallback != null) {
                    mTurkcellUpdaterCallback.onNonForceUpdateReceive(message, warnings, whatIsNew);
                }
            }
        }

        @Override
        public void onUpdateCheckCompleted(UpdateManager manager, Message message) {
            MessageDescription description = message.description;
            String title = null;
            String messageText = null;
            String imageUrl = null;
            Uri redirectionUri = null;
            if (description != null) {
                title = description.get(MessageDescription.KEY_TITLE);
                messageText = description.get(MessageDescription.KEY_MESSAGE);
                imageUrl = description.get(MessageDescription.KEY_IMAGE_URL);
            }
            if (message.targetGooglePlay && message.targetPackageName != null) {
                String packageName = message.targetPackageName;
                redirectionUri = Uri.parse(GOOGLE_PLAY_BASE_URL + packageName);
            } else if (message.targetWebsiteUrl != null) {
                redirectionUri = Uri.parse(message.targetWebsiteUrl.toExternalForm());
            }
            if (mTurkcellUpdaterCallback != null) {
                mTurkcellUpdaterCallback.onMessageReceive(title, messageText, imageUrl, redirectionUri);
            }
        }

        @Override
        public void onUpdateCheckCompleted(UpdateManager manager) {
            if (mTurkcellUpdaterCallback != null) {
                mTurkcellUpdaterCallback.onUpdateNotFoundReceive();
            }
        }

        @Override
        public void onUpdateCheckFailed(UpdateManager manager, Exception exception) {
            if (mTurkcellUpdaterCallback != null) {
                mTurkcellUpdaterCallback.onUpdaterErrorReceive(exception);
            }
        }
    };
}
