//
//  FaceVerification.swift
//  FaceTec
//
//  Created by Alex Serdukov on 24.12.2020.
//  Copyright © 2020 Facebook. All rights reserved.
//

import Foundation

class FaceVerification {
    // singletone instance
    static let shared = FaceVerification()
    
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
        request("/verify/face/session", "GET", [:]) { response, error in
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
        enrollmentResultCallback: @escaping ([String: AnyObject]?, Error?) -> Void
    ) -> Void {
        enroll(enrollmentIdentifier, payload, nil, callback: enrollmentResultCallback)
    }
    
    func enroll(
        _ enrollmentIdentifier: String,
        _ payload: [String : Any],
        _ withDelegate: URLSessionDelegate? = nil,
        callback enrollmentResultCallback: @escaping ([String: AnyObject]?, Error?) -> Void
    ) -> Void {
        let enrollmentUri = "/verify/face/" + enrollmentIdentifier.urlEncoded()
        
        request(enrollmentUri, "PUT", payload, withDelegate) { response, error in
            enrollmentResultCallback(response, error)
        }
    }
    
    private func request(
        _ url: String,
        _ method: String,
        _ parameters: [String : Any] = [:],
        _ resultCallback: @escaping ([String: AnyObject]?, Error?) -> Void
    ) -> Void {
        request(url, method, parameters, nil, resultCallback)
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
        
        let task = session.dataTask(with: request as URLRequest) { data, response, httpError in
            if httpError != nil {
                resultCallback(nil, httpError)
                return
            }
            
            guard let json = self.parseResponse(data) else {
                resultCallback(nil, FaceVerificationError.unexpectedResponse)
                return
            }
            
            if (json[self.succeedProperty] as! Bool == false) {
                let errorMessage = json[self.errorMessageProperty] as? String
                let error: FaceVerificationError = errorMessage == nil ? .unexpectedResponse : .failedResponse(errorMessage!)
                                
                resultCallback(json, error)
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
    
    private func parseResponse(_ data: Data?) -> [String: AnyObject]? {
        guard let data = data else {
            return nil
        }
        
        guard let json = try? JSONSerialization.jsonObject(
            with: data,
            options: JSONSerialization.ReadingOptions.allowFragments
        ) as? [String: AnyObject] else {
            return nil
        }
        
        if !(json[succeedProperty] is Bool) {
            return nil
        }
        
        return json
    }
}
