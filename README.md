# Aty Client Mobile

```
NOTE: When you build the app for the first time in your smartphone. You need to disable 'Instant Run'
feature from Android Studio. You can find this property on:
File -> Settings -> Build, Execution, Deployment -> Instant Run
```

### Key Store

Commands for Generate hashes

```bash
keytool -exportcert -alias androiddebugkey -keystore ~/.android/debug.keystore | openssl sha1 -binary | openssl base64
```

Password: `android`

Commands for Get Certificate Fingerprint

```bash
keytool -exportcert -list -v -alias androiddebugkey -keystore ~/.android/debug.keystore
```

### Documentation for Auth

You need to visit this [link](https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md#facebook).