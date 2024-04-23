import { NativeModules } from 'react-native'

const { FaceTecModule } = NativeModules

// export JS interface to the native code (iOS & Android)
export default FaceTecModule
