package chakuy.tsf_2022;

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.onesignal.OneSignal;
import com.robotemplates.kozuza.BaseApplication;
import com.robotemplates.kozuza.Kozuza;
import chakuy.tsf_2022.ads.AdMobUtility;
import chakuy.tsf_2022.fcm.OneSignalNotificationOpenedHandler;
import chakuy.tsf_2022.utility.Preferences;

import org.alfonz.utility.Logcat;

public class WebViewAppApplication extends BaseApplication {
	@Override
	public void onCreate() {
		super.onCreate();

		// init logcat
		Logcat.init(WebViewAppConfig.LOGS, "WEBVIEWAPP");

		// init analytics
		FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(!WebViewAppConfig.DEV_ENVIRONMENT);

		// init AdMob
		MobileAds.initialize(this);
		MobileAds.setRequestConfiguration(AdMobUtility.createRequestConfiguration());

		// init OneSignal
		initOneSignal(getString(R.string.onesignal_app_id));
	}

	@Override
	public String getPurchaseCode() {
		return WebViewAppConfig.PURCHASE_CODE;
	}

	@Override
	public String getProduct() {
		return Kozuza.PRODUCT_WEBVIEWAPP;
	}

	private void initOneSignal(String oneSignalAppId) {
		if (!oneSignalAppId.equals("")) {
			OneSignal.initWithContext(this);
			OneSignal.setAppId(oneSignalAppId);
			OneSignal.setNotificationOpenedHandler(new OneSignalNotificationOpenedHandler());
			OneSignal.addSubscriptionObserver(stateChanges -> {
				if (stateChanges.getTo().isSubscribed()) {
					String userId = stateChanges.getTo().getUserId();
					saveOneSignalUserId(userId);
				}
			});
			saveOneSignalUserId(OneSignal.getDeviceState().getUserId());
		}
	}

	private void saveOneSignalUserId(String userId) {
		if (userId != null) {
			Logcat.d("userId = " + userId);
			Preferences preferences = new Preferences();
			preferences.setOneSignalUserId(userId);
		}
	}
}
