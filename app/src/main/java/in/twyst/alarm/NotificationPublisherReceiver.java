package in.twyst.alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import in.twyst.activities.DiscoverActivity;
import in.twyst.activities.OfferDetailActivity;
import in.twyst.activities.OutletDetailsActivity;
import in.twyst.model.Offer;
import in.twyst.model.Outlet;
import in.twyst.util.AppConstants;

/**
 * Created by Vipul Sharma on 9/16/2015.
 */
public class NotificationPublisherReceiver extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";
    public static String OUTLET_ID = "outlet_id";
    public static String OFFER_ID  = "offer_id";

    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        String outlet_id = intent.getStringExtra(OUTLET_ID);
        String offer_id =  intent.getStringExtra(OFFER_ID);
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);

        Intent notificationIntent = new Intent(context, OfferDetailActivity.class);
        notificationIntent.putExtra(AppConstants.INTENT_PARAM_OUTLET_ID, outlet_id);
        notificationIntent.putExtra(AppConstants.INTENT_PARAM_OFFER_ID, offer_id);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notification.contentIntent = PendingIntent.getActivity(context, id,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(id, notification);

    }


}
