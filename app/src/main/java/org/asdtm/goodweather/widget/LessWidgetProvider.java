package org.asdtm.goodweather.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import org.asdtm.goodweather.MainActivity;
import org.asdtm.goodweather.R;
import org.asdtm.goodweather.utils.AppPreference;
import org.asdtm.goodweather.utils.Constants;
import org.asdtm.goodweather.utils.Utils;

import java.util.Locale;


public class LessWidgetProvider extends AppWidgetProvider {

    private static final String TAG = "WidgetLessInfo";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase(Constants.ACTION_FORCED_APPWIDGET_UPDATE)) {
            context.startService(new Intent(context, LessWidgetService.class));
        } else if (intent.getAction().equalsIgnoreCase(Intent.ACTION_LOCALE_CHANGED)) {
            AppPreference.setLocale(context, Constants.APP_SETTINGS_NAME);
            context.startService(new Intent(context, LessWidgetService.class));
        } else {
            super.onReceive(context, intent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        context.startService(new Intent(context, LessWidgetService.class));
        for (int appWidgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                                                      R.layout.widget_less_3x1);

            preLoadWeather(context, remoteViews);

            Intent intent = new Intent(context, LessWidgetProvider.class);
            intent.setAction(Constants.ACTION_FORCED_APPWIDGET_UPDATE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            remoteViews.setOnClickPendingIntent(R.id.widget_button_refresh, pendingIntent);

            Intent intentStartActivity = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0,
                                                                     intentStartActivity, 0);
            remoteViews.setOnClickPendingIntent(R.id.widget_root, pendingIntent2);

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }

    private void preLoadWeather(Context context, RemoteViews remoteViews) {
        SharedPreferences weatherPref = context.getSharedPreferences(Constants.PREF_WEATHER_NAME,
                                                                     Context.MODE_PRIVATE);
        String[] cityAndCountryArray = AppPreference.getCityAndCode(context,
                                                                    Constants.APP_SETTINGS_NAME);
        String cityAndCountry = cityAndCountryArray[0] + ", " + cityAndCountryArray[1];
        String temperatureScale = Utils.getTemperatureScale(context);

        String temperature = String.format(Locale.getDefault(), "%.0f", weatherPref
                .getFloat(Constants.WEATHER_DATA_TEMPERATURE, 0));
        String description = weatherPref.getString(Constants.WEATHER_DATA_DESCRIPTION, "clear sky");
        String iconId = weatherPref.getString(Constants.WEATHER_DATA_ICON, "01d");
        String weatherIcon = Utils.getStrIcon(context, iconId);

        remoteViews.setTextViewText(R.id.widget_city, cityAndCountry);
        remoteViews.setTextViewText(R.id.widget_temperature,
                                    temperature + temperatureScale);
        remoteViews.setTextViewText(R.id.widget_description, description);
        remoteViews.setImageViewBitmap(R.id.widget_icon,
                                       Utils.createWeatherIcon(context, weatherIcon));
    }
}
