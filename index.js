import { FaceTecSDK } from './src/FaceTecSDK'
import FaceTecModule from './src/FaceTecModule'

// export SDK instance instantiated with the reference to the native code interdace
export const sdk = new FaceTecSDK(FaceTecModule)

// and constants defined in native code
export * from './src/FaceTecPublicApi'
export default { sdk }
