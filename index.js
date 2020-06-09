import { NativeModules } from 'react-native'
import { ZoomSDK } from './src/ZoomSDK'

const { ZoomModule } = NativeModules
export const sdk = new ZoomSDK(ZoomModule)

const Zoom = { sdk }

export const {
  ZoomUxEvent,
  ZoomSDKStatus,
  ZoomSessionStatus
} = ZoomModule

export default Zoom;
