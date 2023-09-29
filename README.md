## Flightraction
Flight tracking app aimed at tourists. Live tracking over flights within nordic airspace with geographical locations and in-depth information of norwegian airports with weather forcasts, arrivals/departures and tourist attraction near the airport.

## Prerequisites
- Latest version of Android Studio
- Emulated virtual Android device with OS Android 7.0 (Nougat) with API-level 24 as target platform or later in Android Studio
- 'local.properties' file added manually after installation of repo

## local.properties
Create a 'local.properties' file in the repo after downloading.
This will contain your Android SDK emulation path, other API keys and credentials.
```bash
## This file must *NOT* be checked into Version Control Systems,
# as it contains information specific to your local configuration.
#
# Location of the SDK. This is only used by Gradle.
# For customization when using a Version Control System, please read the
# header note.
#Tue May 09 17:12:55 CEST 2023
sdk.dir=C:\Users\**username**\AppData\Local\Android\android-sdk

#API-Key IFI-Proxy
API_KEY=<your_API_proxy_key>

#API-key Places API
API_KEY_PLACES=<your_places_API_key>

#OpenSky User Credentials for Flight API Calls
OPENSKY_USERNAME=<your_opensky_username>
OPENSKY_PASSWORD=<your_opensky_password>
```

sdk.dir is usually found by launching android studio and going Tools->Android->SDK Manager

The API key for places can be obtained from google places by registering to aquire your own API key:
https://developers.google.com/maps/documentation/places/web-service/get-api-key

API-key for IFI-proxy is proprietary key owned by the University of Oslo, so a work around is requires here.

OpenSky credentials can be aquired by registering a OpenSky account which is free.
