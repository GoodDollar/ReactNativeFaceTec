//
//  FaceVerificationError.swift
//  FaceTec
//
//  Created by Alex Serdukov on 25.12.2020.
//  Copyright © 2020 Facebook. All rights reserved.
//

// Exception representing different reasons
// In Swift errors(exceptions) are enum-like entities
// with causes described as cases
enum FaceVerificationError: Error {
    case unexpectedResponse
    case emptyResponse
    case failedResponse(_ serverError: String) // also some of the cases could have params
    
    // and we could define instance props (e.g. error.message getter)
    var message: String {
        get {
            switch self {
            case .failedResponse(let serverError): // if cause have extra props we could read them
                return serverError
            case .emptyResponse:
                return "An empty response received during the face verification API call"
            default:
                return "An unexpected issue during the face verification API call"
            }
        }
    }
}
