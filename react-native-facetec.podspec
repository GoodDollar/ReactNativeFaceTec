require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "react-native-facetec"
  s.version      = package["version"]
  s.summary      = package["description"]

  s.description  = <<-DESC
    GoodDollar is a decentralized economic framework with a Universal Basic Income cryptocurrency
  DESC

  s.homepage     = "https://github.com/GoodDollar/ReactNativeFaceTec"
  s.license    = { :type => "MIT", :file => "LICENSE" }
  s.authors      = { "Good Dollar" => "builders@gooddollar.org" }
  s.platforms    = { :ios => "9.0" }
  s.source       = { :git => "https://github.com/GoodDollar/ReactNativeFaceTec.git", :tag => "#{s.version}" }

  # s.vendored_frameworks = "ios/Frameworks/<todo: facetec framework>"
  s.source_files = "ios/**/*.{h,c,m,swift}"
  s.requires_arc = true

  s.dependency "React"
  # ...
  # s.dependency "..."
end

