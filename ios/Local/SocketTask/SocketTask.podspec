#
# Be sure to run `pod lib lint SocketTask.podspec' to ensure this is a
# valid spec before submitting.
#
# Any lines starting with a # are optional, but their use is encouraged
# To learn more about a Podspec see https://guides.cocoapods.org/syntax/podspec.html
#

Pod::Spec.new do |s|
  s.name             = 'SocketTask'
  s.version          = '0.1.0'
  s.summary          = 'A Socket warpper'

# This description is used to generate tags and improve search results.
#   * Think: What does it do? Why did you write it? What is the focus?
#   * Try to keep it short, snappy and to the point.
#   * Write the description between the DESC delimiters below.
#   * Finally, don't worry about the indent, CocoaPods strips it!

  s.description      = <<-DESC
TODO: Add long description of the pod here.
                       DESC

  s.homepage         = 'https://github.com/cctip-fd/SocketTask'
  # s.screenshots     = 'www.example.com/screenshots_1', 'www.example.com/screenshots_2'
  s.author           = { 'cctip-fd' => 'cctip_fd@proton.me' }
  s.source           = { :git => 'https://github.com/cctip-fd/SocketTask.git', :tag => s.version.to_s }
  # s.social_media_url = 'https://twitter.com/<TWITTER_USERNAME>'

  s.ios.deployment_target = '13.0'

  s.source_files  = "Classes", "Classes/**/*.{swift}"

  
  # s.resource_bundles = {
  #   'SocketTask' => ['SocketTask/Assets/*.png']
  # }

  # s.public_header_files = 'Pod/Classes/**/*.h'

  s.dependency 'Starscream'
  s.dependency 'SwiftProtobuf'
  s.dependency 'ReachabilitySwift'
end
