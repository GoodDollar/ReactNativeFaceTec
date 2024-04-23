import FaceTecModule from './FaceTecModule'

// read and export constants defined in the native code
export const {
  FaceTecUxEvent,
  FaceTecSDKStatus,
  FaceTecSessionStatus
} = FaceTecModule.getConstants()
