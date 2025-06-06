//
//  PromiseDelegate.swift
//  FaceTec
//
//  Created by Alex Serdukov on 12/21/20.
//  Copyright © 2020 Facebook. All rights reserved.
//

// extensions over basic Promise wrapper util
// to reject with failed sdk or session status
// in Swift we use protocol (e.g. interface) extensions
// this works as method overloads in Java (see RCTPromise.java)

protocol PromiseDelegate {
    func resolve(_ result: Any?) -> Void
    func reject(_ with: FaceTecSessionStatus) -> Void
    func reject(_ with: FaceTecSDKStatus) -> Void
    func reject(_ with: FaceTecSessionStatus, _ customMessage: String?) -> Void
    func reject(_ with: FaceTecSDKStatus, _ customMessage: String?) -> Void
}

extension PromiseDelegate {
    func reject(_ with: FaceTecSessionStatus) -> Void {
        reject(with, nil)
    }
    
    func reject(_ with: FaceTecSDKStatus) -> Void {
        reject(with, nil)
    }
}
