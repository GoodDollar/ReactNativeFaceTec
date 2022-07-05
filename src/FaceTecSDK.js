import { NativeEventEmitter } from 'react-native'

import { FaceTecUxEvent } from './FaceTecPublicApi'

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
        return await FaceTecSDK.prototype[method].apply(this, args)
      } catch ({ code, message }) {
        // RCTBridge doesn't returns/rejects with JS Error object
        // it returns just object literal with the Error-like shape
        // also, codes are returning as strings (but actually FaceTec statuses are numbers)
        // so we have to use this method to convert codes to numbers
        // and convert error-like shape to the JS Error object
        const exception = new Error(message)

        exception.code = Number(code)
        throw exception
      }
    })
  }

  // eslint-disable-line require-await
  async initialize(serverUrl, jsonWebToken, licenseKey, encryptionKey = null, licenseText = null) {
    const { module } = this

    // we're passing current JWT to the native code allowing it to call GoodServer for verification
    // unfortunately we couldn't pass callback which could return some data back to the native code
    // so it's only way to integrate FaceTec on native - to reimplement all logic about calling server
    return module.initializeSDK(serverUrl, jsonWebToken, licenseKey, encryptionKey, licenseText)
  }

  async enroll(enrollmentIdentifier, maxRetries = -1, timeout = -1, sessionToken) {
    const { module } = this

    return module.faceVerification(enrollmentIdentifier, maxRetries, timeout, sessionToken)
  }

  addListener(event, handler) {
    const { _subscriptions } = this
    let subscriptionsMap = _subscriptions[event]
    const subscription = this.subscribeTo(event, handler)

    if (!subscriptionsMap) {
      subscriptionsMap = new WeakMap()
      _subscriptions[event] = subscriptionsMap
    }

    subscriptionsMap.set(handler, subscription)
    return () => this.removeListener(event, handler)
  }

  removeListener(event, handler) {
    const { _subscriptions } = this
    const subscriptionsMap = _subscriptions[event]

    if (!subscriptionsMap || !subscriptionsMap.has(handler)) {
      return
    }

    subscriptionsMap.get(handler).remove()
    subscriptionsMap.delete(handler)
  }

  // some events could contain error objects inside event data
  // as React bridge doesn't returns JS errors (see above)
  // we have to convert event data for those specific events
  // before call the original event handling callback
  subscribeTo(event, handler) {
    const { eventEmitter } = this
    let eventHandler = handler

    if (FaceTecUxEvent.FV_RETRY === event) {
      eventHandler = eventData => {
        const { reason, ...failureFlags } = eventData

        handler({ reason: new Error(reason), ...failureFlags })
      }
    }

    return eventEmitter.addListener(event, eventHandler)
  }

  async setFonts(fonts) {
    const { module } = this
    return  module.setFonts(fonts)
  }
}
