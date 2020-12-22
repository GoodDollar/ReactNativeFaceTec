import { NativeModules } from 'react-native'
import { FaceTecSDK } from './src/FaceTecSDK'

const { FaceTecModule } = NativeModules

export const sdk = new FaceTecSDK(FaceTecModule)

export const {
  FaceTecUxEvent,
  FaceTecSDKStatus,
  FaceTecSessionStatus
} = FaceTecModule.getConstants()

console.log({
  FaceTecUxEvent,
  FaceTecSDKStatus,
  FaceTecSessionStatus
})

export default { sdk }
