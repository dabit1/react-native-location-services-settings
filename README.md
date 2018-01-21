
# react-native-react-native-location-services-settings

## Getting started

`$ npm install react-native-react-native-location-services-settings --save`

### Mostly automatic installation

`$ react-native link react-native-react-native-location-services-settings`

### Manual installation


#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNReactNativeLocationServicesSettingsPackage;` to the imports at the top of the file
  - Add `new RNReactNativeLocationServicesSettingsPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
    ```
    include ':react-native-react-native-location-services-settings'
    project(':react-native-react-native-location-services-settings').projectDir = new File(rootProject.projectDir,  '../node_modules/react-native-react-native-location-services-settings/android')
    ```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
    ```
      compile project(':react-native-react-native-location-services-settings')
    ```

## Usage
```javascript

// possible values: 'high_accuracy', 'balanced_power_accuracy', 'low_power'
ReactNativeLocationServicesSettings.checkStatus('high_accuracy').then(res => {
  if (!res.enabled) {
    ReactNativeLocationServicesSettings.askForEnabling(res => {
      if (res) {
        console.log('location services were allowed by the user')
      } else {
        console.log('location services were denied by the user')
      }
    })
  }
})
```
  