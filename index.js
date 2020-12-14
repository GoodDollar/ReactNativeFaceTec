import { NativeModules } from 'react-native'
import { FaceTecSDK } from './src/FaceTecSDK'

const { FaceTec } = NativeModules

export const sdk = new FaceTecSDK(FaceTec)

export const {
  FaceTecUxEvent,
  FaceTecSDKStatus,
  FaceTecSessionStatus
} = FaceTec.getConstants()

export default { sdk }
