import { NativeEventEmitter } from 'react-native'

// sdk class
export class FaceTecSDK {
  _subscriptions = {}

  constructor(module) {
    const wrapMethods = ['initialize', 'enroll']

    this.module = module
    this.eventEmitter = new NativeEventEmitter(module)

    // wrap methods
    wrapMethods.forEach(method => this[method] = async (...args) => {
      try {
        const result = await FaceTecSDK.prototype[method](...args)

        return result
      } catch ({ code, message }) {
        // RCTBridge doesn't returns/rejects with JS Error object
        // it returns just object literal with the Error-like shape
        // also, codes are returning as strings (but actually FaceTec statuses are numbers)
        // so we have to use this method to convert codes to numbers
        // and convert error-like shape to the JS Error object
        const exception = new Error(message)

        exception.code = Number(code)
        log.warn(`${logPrefix} failed`, { exception })

        throw exception
      }
    })

    // pre-create subscriptions maps
    Object.values(module.FaceTecUxEvent).forEach(event => this._subscriptions[event] = new WeakMap())
  }

  // eslint-disable-line require-await
  async initialize(serverUrl, jsonWebToken, licenseKey, encryptionKey = null, licenseText = null) {
    const { module } = this

    // we're passing current JWT to the native code allowing it to call GoodServer for verification
    // unfortunately we couldn't pass callback which could return some data back to the native code
    // so it's only way to integrate FaceTec on native - to reimplement all logic about calling server
    return module.initialize(serverUrl, jsonWebToken, licenseKey, encryptionKey, licenseText)
  }

  async enroll(enrollmentIdentifier, maxRetries = -1) {
    const { module } = this

    return module.faceVerification(enrollmentIdentifier, maxRetries)
  }

  addListener(event, handler) {
    const { eventEmitter, _subscriptions } = this
    const subscriptionsMap = _subscriptions[event]
    const subscription = eventEmitter.addListener(event, handler)

    subscriptionsMap.set(handler, subscription)
    return () => this.removeListener(event, handler)
  }

  removeListener(event, handler) {
    const { _subscriptions } = this
    const subscriptionsMap = _subscriptions[event]

    if (!subscriptionsMap.has(handler)) {
      return
    }

    subscriptionsMap.get(handler).remove()
    subscriptionsMap.remove(handler)
  }
}
