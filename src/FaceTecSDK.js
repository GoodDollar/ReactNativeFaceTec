import { NativeEventEmitter } from 'react-native'

import { FaceTecUxEvent } from './FaceTecPublicApi'

// sdk class wrapper
export class FaceTecSDK {
  _subscriptions = {}

  // receives ref to the native code interface as the single argument
  constructor(module) {
    const wrapMethods = ['initialize', 'enroll']

    this.module = module
    this.eventEmitter = new NativeEventEmitter(module)

    // wrap methods to correctly throw JS errors
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

  // Initializes SDK
  //   - serverUrl - an GoodServer uri from the environment GoodDApp is running
  //   - jsonWebToken - user token to auth GoodServer calls
  //   - licenseKey - device key identifier from GoodDapp env/config
  //   - encryptionKey - facemaps encryption key from GoodDapp env/config
  //   - licenseText - production key. receives at the GoodDApp side before initialize SDK
  //   by calling /verify/face/license/native andpoint at GoodServer which proxies call
  //   to the FaceTec CustomServer instance
  //
  // eslint-disable-line require-await
  async initialize(serverUrl, jsonWebToken, licenseKey, encryptionKey = null, licenseText = null) {
    const { module } = this
    const baseUrl = serverUrl.endsWith('/') ? serverUrl.substring(0, serverUrl.length - 1) : serverUrl

    // we're passing current JWT to the native code allowing it to call GoodServer for verification
    // unfortunately we couldn't pass callback which could return some data back to the native code
    // so it's only way to integrate FaceTec on native - to reimplement all logic about calling server
    return module.initializeSDK(baseUrl, jsonWebToken, licenseKey, encryptionKey, licenseText)
  }

  // Runs face verification flow
  //   - enrollmentIdentifier, v1Identifier - face ids generated for user account (wallet address)
  //   - chainId - fuse or celo id
  //   - maxRetries - retry attempts if enrollment failed before show 'switch to another device'
  //   - timeout - enrollment HTTP request to GoodServer timeout (as millis)
  async enroll(enrollmentIdentifier, v1Identifier, chainId = null, maxRetries = -1, timeout = -1) {
    const { module } = this
    const chain = String(chainId || '')

    return module.faceVerification(enrollmentIdentifier, v1Identifier, chain, maxRetries, timeout)
  }

  // Subscribes to event (for analytics)
  //   - event = "onUIReady" | "onCaptureDone" | "onRetry"
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

  // Unsubscribes from event
  removeListener(event, handler) {
    const { _subscriptions } = this
    const subscriptionsMap = _subscriptions[event]

    if (!subscriptionsMap || !subscriptionsMap.has(handler)) {
      return
    }

    subscriptionsMap.get(handler).remove()
    subscriptionsMap.delete(handler)
  }

  // @private
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
}
