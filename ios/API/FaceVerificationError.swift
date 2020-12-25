//
//  FaceVerificationError.swift
//  FaceTec
//
//  Created by Alex Serdukov on 25.12.2020.
//  Copyright © 2020 Facebook. All rights reserved.
//

import Foundation

enum FaceVerificationError: Error {
    case unexpectedResponse
    case emptyResponse
    case failedResponse(_ serverError: String)
    
    var message: String {
        get {
            switch self {
            case .failedResponse(let serverError):
                return serverError
            case .emptyResponse:
                return "An empty response received during the face verification API call"
            default:
                return "An unexpected issue during the face verification API call"
            }
        }
    }
}
