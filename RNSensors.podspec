require 'json'

package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

Pod::Spec.new do |s|
  s.name                = 'RNSensors'
  s.version             = package['version']
  s.summary             = package['description']
  s.description         = package['description']
  s.homepage            = package['homepage']
  s.license             = package['license']
  s.author              = package['author']
  s.source              = { :git => "https://github.com/react-native-sensors/react-native-sensors.git", :tag => "v#{s.version}" }
  s.platforms           = { :ios => "13.4" }
  s.source_files        = "ios/**/*.{h,m,mm}"
  s.exclude_files       = "ios/RNSensors.xcodeproj/**"
  s.preserve_paths      = "*.js"

  if respond_to?(:install_modules_dependencies, true)
    install_modules_dependencies(s)
  else
    s.dependency 'React-Core'
  end
end
