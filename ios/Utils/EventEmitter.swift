//
//  RCTEventEmitter.swift
//  FaceTecVerificationNative
//
//  Created by Alex Serdukov on 5/22/20.
//  Copyright © 2020 Facebook. All rights reserved.
//

enum UXEvent: String, CaseIterable {
    case UI_READY = "onUIReady"
    case CAPTURE_DONE = "onCaptureDone"
    case FV_RETRY = "onRetry"
}

class EventEmitter {
    // singletone instance
    public static let shared = EventEmitter()

    // private vars
    private static var rctEventEmitter: RCTEventEmitter!
    private var suspended = true
    private init() {}

    func register(withRCTEventEmitter: RCTEventEmitter) {
        Self.rctEventEmitter = withRCTEventEmitter
        restore()
    }

    func suspend() {
        suspended = true
    }

    func restore() {
        suspended = false
    }

    func dispatch(_ event: UXEvent, _ body: Any? = nil) {
        if suspended {
            return
        }

        Self.rctEventEmitter.sendEvent(withName: event.rawValue, body: body)
    }
}
