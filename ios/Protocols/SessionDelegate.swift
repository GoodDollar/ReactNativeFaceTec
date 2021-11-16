//
//  SessionDelegate.swift
//  FaceTec
//
//  Created by Alex Serdukov on 16.11.2021.
//  Copyright © 2021 Facebook. All rights reserved.
//

import Foundation

protocol SessionDelegate {
  func onFaceTecSessionResult(sessionResult: FaceTecSessionResult, faceScanResultCallback: FaceTecFaceScanResultCallback)

  func onFaceTecSessionDone()
}
