import { FaceTecSDK } from './src/FaceTecSDK'
import FaceTecModule from './src/FaceTecModule'

export const sdk = new FaceTecSDK(FaceTecModule)

export * from './src/FaceTecPublicApi'
export default { sdk }
