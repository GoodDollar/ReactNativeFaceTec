//
//  FaceVerification.swift
//  FaceTec
//
//  Created by Alex Serdukov on 24.12.2020.
//  Copyright © 2020 Facebook. All rights reserved.
//

import UIKit
import Foundation

enum FaceVerificationError: Error {
    case unexpectedResponse
    case emptyResponse
    case failedResponse(_ serverError: String)
}

class FaceVerification {
    // singletone instance
    static let shared = FaceVerification()
    static let unexpectedMessage = "An unexpected issue during the face verification API call"
    
    private var serverURL: String?
    private var jwtAccessToken: String?
    
    private let succeedProperty = "success"
    private let errorMessageProperty = "error"
    private let sessionTokenProperty = "sessionToken"
    
    private init() {}
    
    func register(_ serverURL: String, _ jwtAccessToken: String) -> Void {
        self.serverURL = serverURL
        self.jwtAccessToken = jwtAccessToken
    }
    
    func getSessionToken(sessionTokenCallback: @escaping (String?, Error?) -> Void) -> Void {
        request("/verify/face/session", "GET", [:], nil) { response, error in
            if error != nil {
                sessionTokenCallback(nil, error)
                return
            }
            
            guard let sessionToken = response?[self.sessionTokenProperty] as? String else {
                sessionTokenCallback(nil, FaceVerificationError.emptyResponse)
                return
            }
            
            sessionTokenCallback(sessionToken, nil)
        }
    }
    
    func enroll(
        _ enrollmentIdentifier: String,
        _ payload: [String : Any],
        _ withDelegate: URLSessionDelegate? = nil,
        enrollmentResultCallback: @escaping ([String: AnyObject]?, Error?) -> Void
    ) -> Void {
        let encodedEnrollmentId = enrollmentIdentifier.addingPercentEncoding(withAllowedCharacters:  NSMutableCharacterSet.urlQueryAllowed)!
        
        request("/verify/face/" + encodedEnrollmentId, "PUT", payload, withDelegate) { response, error in
            enrollmentResultCallback(response, error)
        }
    }
        
    private func request(
        _ url: String,
        _ method: String,
        _ parameters: [String : Any] = [:],
        _ withDelegate: URLSessionDelegate? = nil,
        _ resultCallback: @escaping ([String: AnyObject]?, Error?) -> Void
    ) {
        let config = URLSessionConfiguration.default
        let request = createRequest(url, method, parameters)
        
        let session = withDelegate == nil ? URLSession(configuration: config)
            : URLSession(configuration: config, delegate: withDelegate, delegateQueue: OperationQueue.main)
        
        let task = session.dataTask(with: request as URLRequest) { data, response, error in
            var json: [String: AnyObject]?
            
            do {
                json = try self.parseResponse(data, error)
            } catch {
                resultCallback(nil, error)
            }
            
            if (json?[self.succeedProperty] as! Bool == false) {
                let errorMessage = json?[self.errorMessageProperty] as? String ?? Self.unexpectedMessage
                                
                resultCallback(json, FaceVerificationError.failedResponse(errorMessage))
                return
            }
            
            resultCallback(json, nil)
        }
        
        task.resume()
    }
    
    private func createRequest(_ url: String, _ method: String, _ parameters: [String : Any] = [:]) -> URLRequest {
        let request = NSMutableURLRequest(url: NSURL(string: serverURL! + url)! as URL)
        
        request.httpMethod = method.uppercased()
        
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("Bearer " + jwtAccessToken!, forHTTPHeaderField: "Authorization")
        
        request.httpBody = try! JSONSerialization.data(withJSONObject: parameters, options: JSONSerialization.WritingOptions(rawValue: 0))
        
        return request as URLRequest
    }
    
    private func parseResponse(_ data: Data?, _ error: Error?) throws -> [String: AnyObject] {
        if (error != nil) {
            throw error!
        }
        
        guard let data = data else {
            throw FaceVerificationError.unexpectedResponse
        }
        
        guard let json = try? JSONSerialization.jsonObject(
            with: data,
            options: JSONSerialization.ReadingOptions.allowFragments
        ) as? [String: AnyObject] else {
            throw FaceVerificationError.unexpectedResponse
        }
        
        if !(json[succeedProperty] is Bool) {
            throw FaceVerificationError.unexpectedResponse
        }
        
        return json
    }
}
