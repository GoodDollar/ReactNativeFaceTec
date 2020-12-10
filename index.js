import { NativeModules } from 'react-native'
import { FaceTecSDK } from './src/FaceTecSDK'

const { FaceTecModule } = NativeModules
export const sdk = new FaceTecSDK(FaceTecModule)

const FaceTec = { sdk }

export const {
  FaceTecUxEvent,
  FaceTecSDKStatus,
  FaceTecSessionStatus
} = FaceTecModule

export default FaceTec;
