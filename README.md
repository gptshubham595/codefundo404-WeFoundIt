[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Finger%20Print%20Auth%20Helper-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/4493) [ ![Download](https://api.bintray.com/packages/multidots/md-maven/fingerprint-auth/images/download.svg) ](https://bintray.com/multidots/md-maven/fingerprint-auth/_latestVersion) [![Build Status](https://travis-ci.org/multidots/android-fingerprint-authentication.svg?branch=master)](https://travis-ci.org/multidots/android-fingerprint-authentication)

# Fingerprint Authentication Helper

Finger print authentication helper is an android library to simplify the authentication with finger print process. This will provide a common 
platform for finger print authentication for all the android versions.

## Screenshot
![Authentication Demo](/screens/screen-capture.gif)

## Dependency
- Add the dependencies to your app level build.gradle file:
```
dependencies {
    compile 'com.multidots:fingerprint-auth:1.0.1'
}
```

## Usage
- Inside your activity or fragment create `FingerPrintAuthHelper` instance and call `startAuth()` in the `onResume()` to enable finger print scanning.
```
@Override
protected void onCreate(Bundle savedInstanceState) {
    mFingerPrintAuthHelper = FingerPrintAuthHelper.getHelper(this, this);
    //.... 
    //....        
}
   
@Override
protected void onResume() {
    super.onResume();
    //start finger print authentication
    mFingerPrintAuthHelper.startAuth();
}
```

- Don't forget to disable the finger print scanner in `onPause()` of your activity/fragment.
```
@Override
protected void onPause() {
    super.onPause();
    mFingerPrintAuthHelper.stopAuth();
}
```

** Callbacks **

- You need to implement *FingerPrintAuthCallback* in your activity/fragment to receive the callbacks from the helper class.
```
@Override
public void onNoFingerPrintHardwareFound() {
    //Device does not have finger print scanner.
}

@Override
public void onNoFingerPrintRegistered() {
    //There are no finger prints registered on this device.
}

@Override
public void onBelowMarshmallow() {
    //Device running below API 23 version of android that does not support finger print authentication.
}

@Override
public void onAuthSuccess(FingerprintManager.CryptoObject cryptoObject) {
    //Authentication sucessful.
}

@Override
public void onAuthFailed(int errorCode, String errorMessage) {
    switch (errorCode) {    //Parse the error code for recoverable/non recoverable error.
        case AuthErrorCodes.CANNOT_RECOGNIZE_ERROR:
            //Cannot recognize the fingerprint scanned.
            break;
        case AuthErrorCodes.NON_RECOVERABLE_ERROR:
            //This is not recoverable error. Try other options for user authentication. like pin, password.
            break;
        case AuthErrorCodes.RECOVERABLE_ERROR:
            //Any recoverable error. Display message to the user.
            break;
    }
}
```

## Demo application
Here is the link to the [demo](/apk/sample.apk) application.

## Questions?
- If you have some problems with using this library or something doesn't work correctly, feel free to open an [issue](https://github.com/multidots/android-fingerprint-authentication/issues/new).

## How to contribute?
#### Simple 3 step to contribute into this repo:

1. Fork the project. 
2. Make required changes and commit. 
3. Generate pull request. Mention all the required description regarding changes you made.

### [Medium Article](https://medium.com/@multidots/authenticate-your-user-with-fingerprint-de876618ce8d)
