# ReactNative FaceTec SDK Integration

## Getting started

`$ npm install @gooddollar/react-native-facetec --save`

### Mostly automatic installation

`$ react-native link @gooddollar/react-native-facetec`

## Usage

```javascript
import FaceTec from '@gooddollar/react-native-facetec';
import Config from 'react-native-config'

const {
  REACT_APP_ZOOM_ENCRYPTION_KEY,
  REACT_APP_ZOOM_LICENSE_KEY,
  REACT_APP_ZOOM_LICENSE_TEXT,
  REACT_APP_SERVER_URL = 'http://localhost:3003',
  REACT_APP_SERVER_TOKEN,
  REACT_APP_ENROLLMENT_IDENTIFIER,
} = Config

// ... some component body
useEffect(() => {
  FaceTec.sdk.initialize(
    REACT_APP_SERVER_URL,
    REACT_APP_SERVER_TOKEN,
    REACT_APP_ZOOM_LICENSE_KEY,
    REACT_APP_ZOOM_ENCRYPTION_KEY,
    REACT_APP_ZOOM_LICENSE_TEXT
  )
  .then(() => { /* on initialized handler here */ })
  .catch(e => { /* initialized failed handler here */ })
}, [])

const onMyButtonClicked = useCallback(() => {
  FaceTec.sdk.enroll(REACT_APP_ENROLLMENT_IDENTIFIER, 3, 60000)
    .then(() => { /* verification successfull handler here */ })
    .catch(() => { /* verification failed handler here */ })
}, [])
```

### Handling exceptions

```javascript
import FaceTec, { FaceTecSDKStatus, FaceTecSessionStatus } from '@gooddollar/react-native-facetec';

// ...
FaceTec.sdk.initialize(...).then(...)
  .catch(exception => {
    // see platform API docs for complete status codes list
    if (exception.code === FaceTecSDKStatus.DeviceNotSupported) {
      // suggest user to use another device
    }
  })

FaceTec.sdk.enroll(...).then(...)
  .catch(exception => {
    // see platform API docs for complete status codes list
    if (exception.code === FaceTecSDKStatus.CameraPermissionDenied) {
     // show some help how to allow camera access on his platform/device
    }
  })
```

### Listening events

```javascript
import FaceTec, { FaceTecUxEvent } from '@gooddollar/react-native-facetec';

// ...
// subscribe
const subscriptions = [
  FaceTec.sdk.addListener(FaceTecUxEvent.UI_READY, () => { /* facetec was initialized and its UI was shown */ })),
  FaceTec.sdk.addListener(FaceTecUxEvent.CAPTURE_DONE, () => { /* user has finished capturing has face and request being sent to the FaceTec Custom Server */ }),
  FaceTec.sdk.addListener(FaceTecUxEvent.FV_RETRY, () => { /* FaceTec is asking user to retry face capture process */ }),
]

// unsubscribe
for (unsubscribe of subscriptions) {
  unsubscribe()
}
```

### Update SDKs

#### Android

1. Goto https://dev.facetec.com/downloads 
2. Download Android SDK
3. Remove `./android/libs/*.aar`
4. Copy `./facetec-sdk-9.X.YY.aar` from android sdk archive to `./android/libs`

#### Web

1. Goto https://dev.facetec.com/downloads 
2. Download Browser SDK
3. Remove `./web/sdk/FaceTecSDK.web.js` `./web/sdk/images/*.png` `./web/sdk/resources/*.*` (**keep** folders)
4. Copy `./core-sdk/FaceTecSDK.js/FaceTecSDK.js` from browser sdk archive to `./web/sdk/FaceTecSDK.web.js` (`.web` suffix is **mandatory**)
5. Copy ``./core-sdk/FaceTecSDK.js/resources/*.*` from browser sdk archive to `./web/sdk/resources/`
6. Copy ``./core-sdk/FaceTec_images/*.png` from browser sdk archive to `./web/sdk/images/`
