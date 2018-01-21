
package com.dabit1;

import static java.lang.System.out;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ActivityEventListener;

import android.app.Activity;
import android.content.Intent;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import android.support.annotation.NonNull;
import android.content.IntentSender;

public class RNReactNativeLocationServicesSettingsModule extends ReactContextBaseJavaModule implements ActivityEventListener {

  private final ReactApplicationContext reactContext;
  private Callback enablingCallback = null;

  public RNReactNativeLocationServicesSettingsModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    reactContext.addActivityEventListener(this);
  }

  private Exception locationException = null;

  final static int REQUEST_LOCATION = 100;
  int REQUEST_CHECK_SETTINGS = 100;

  @Override
  public String getName() {
    return "RNReactNativeLocationServicesSettings";
  }

  @Override
  public void onNewIntent(Intent intent) {
  }

  private LocationRequest createLocationRequest(String priority) {
    int locationPriority = 0;
    switch (priority) {
      case "high_accuracy":
        locationPriority = LocationRequest.PRIORITY_HIGH_ACCURACY;
        break;
      case "balanced_power_accuracy":
        locationPriority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
        break;
      case "low_power":
        locationPriority = LocationRequest.PRIORITY_LOW_POWER;
        break;
    }

    LocationRequest mLocationRequest = new LocationRequest();
    mLocationRequest.setInterval(10000);
    mLocationRequest.setFastestInterval(5000);
    mLocationRequest.setPriority(locationPriority);
    
    return mLocationRequest;
  }

  @ReactMethod
  public void checkStatus(String priority, final Promise promise) {
    LocationRequest mLocationRequest = createLocationRequest(priority);

    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
      .addLocationRequest(mLocationRequest);

    SettingsClient client = LocationServices.getSettingsClient(reactContext);
    Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

    task.addOnSuccessListener(reactContext.getCurrentActivity(), new OnSuccessListener<LocationSettingsResponse>() {
      @Override
      public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
        WritableMap result = Arguments.createMap();
        result.putBoolean("enabled", true);
        promise.resolve(result);
        // All location settings are satisfied. The client can initialize
        // location requests here.
        // ...
      }
    });

    task.addOnFailureListener(reactContext.getCurrentActivity(), new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception e) {
        WritableMap result = Arguments.createMap();
        result.putBoolean("enabled", false);
        promise.resolve(result);
        if (e instanceof ResolvableApiException) {
          // Location settings are not satisfied, but this can be fixed
          // by showing the user a dialog.
          locationException = e;
        }
      }
    });
  }

  @ReactMethod
  public void askForEnabling(Callback callback) {
    enablingCallback = callback;
    try {
      // Show the dialog by calling startResolutionForResult(),
      // and check the result in onActivityResult().
      ResolvableApiException resolvable = (ResolvableApiException) locationException;
      resolvable.startResolutionForResult(reactContext.getCurrentActivity(),
              REQUEST_CHECK_SETTINGS);
    } catch (IntentSender.SendIntentException sendEx) {
      // Ignore the error.
    }
  }

  @Override
  public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
      case REQUEST_LOCATION:
        switch (resultCode) {
          case Activity.RESULT_OK: {
            enablingCallback.invoke(true);
            break;
          }
          case Activity.RESULT_CANCELED:{
            enablingCallback.invoke(false);
            break;
          }
        }
      break;
    }
  }
}