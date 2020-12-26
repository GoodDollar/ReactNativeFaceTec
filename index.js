import { FaceTecSDK } from './src/FaceTecSDK'
import FaceTecModule from './src/FaceTecPublicApi'

export const sdk = new FaceTecSDK(FaceTecModule)

export * from './src/FaceTecPublicApi'
export default { sdk }
