package com.algonquincollege.trop0008.doorsopenottawa.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.algonquincollege.trop0008.doorsopenottawa.model.BuildingPOJO;
import com.algonquincollege.trop0008.doorsopenottawa.utils.HttpHelper;
import com.algonquincollege.trop0008.doorsopenottawa.utils.RequestPackage;
import com.google.gson.Gson;

import java.io.IOException;

/**
 * Open Doors Ottawa App
 *
 * @author Marjan Tropper (trop0008@algonquinlive.com)
 */
/**
 * Class MyService.
 *
 * Fetch the data at URI.
 * Return an array of Building[] as a broadcast message.
 *
 * @author David Gasner
 */
public class MyService extends IntentService {

    public static final String MY_SERVICE_MESSAGE = "myServiceMessage";
    public static final String MY_SERVICE_PAYLOAD = "myServicePayload";
    public static final String MY_SERVICE_RESPONSE = "myServiceResponse";
    public static final String MY_SERVICE_EXCEPTION = "myServiceException";
    public static final String REQUEST_PACKAGE = "requestPackage";

    public MyService() {
        super("MyService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        RequestPackage requestPackage = (RequestPackage) intent.getParcelableExtra(REQUEST_PACKAGE);

        String response;
        try {
            // TODO: change to your AC username + password :)

            response = HttpHelper.downloadUrl(requestPackage, "trop0008", "password");
        } catch (IOException e) {
            e.printStackTrace();
            Intent messageIntent = new Intent(MY_SERVICE_MESSAGE);
            messageIntent.putExtra(MY_SERVICE_EXCEPTION, e.getMessage());
            LocalBroadcastManager manager =
                    LocalBroadcastManager.getInstance(getApplicationContext());
            manager.sendBroadcast(messageIntent);
            return;
        }

        Intent messageIntent = new Intent(MY_SERVICE_MESSAGE);
        Gson gson = new Gson();
        switch (requestPackage.getMethod()) {
            case GET:
                BuildingPOJO[] buildingsArray = gson.fromJson(response, BuildingPOJO[].class);
                messageIntent.putExtra(MY_SERVICE_PAYLOAD, buildingsArray);
                break;

            case POST:
            case PUT:
            case DELETE:
                BuildingPOJO building = gson.fromJson(response, BuildingPOJO.class);
                messageIntent.putExtra(MY_SERVICE_RESPONSE, building);
                break;
        }
        LocalBroadcastManager manager =
                LocalBroadcastManager.getInstance(getApplicationContext());
        manager.sendBroadcast(messageIntent);
    }
}
