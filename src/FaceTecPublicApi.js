import { NativeModules } from 'react-native'

const { FaceTecModule } = NativeModules

export const {
  FaceTecUxEvent,
  FaceTecSDKStatus,
  FaceTecSessionStatus
} = FaceTecModule.getConstants()

export default FaceTecModule
