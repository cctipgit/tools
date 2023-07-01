#
#  Be sure to run `pod spec lint NSObject+Rx.podspec' to ensure this is a
#  valid spec and to remove all comments including this before submitting the spec.
#
#  To learn more about Podspec attributes see https://guides.cocoapods.org/syntax/podspec.html
#  To see working Podspecs in the CocoaPods repo see https://github.com/CocoaPods/Specs/
#

Pod::Spec.new do |spec|

  # ―――  Spec Metadata  ―――――――――――――――――――――――――――――――――――――――――――――――――――――――――― #
  #
  #  These will help people to find your library, and whilst it
  #  can feel like a chore to fill in it's definitely to your advantage. The
  #  summary should be tweet-length, and the description more in depth.
  #

  spec.name         = "NSObject+Rx"
  spec.version      = "0.0.1"
  spec.summary      = "NSObject+Rx."

  # This description is used to generate tags and improve search results.
  #   * Think: What does it do? Why did you write it? What is the focus?
  #   * Try to keep it short, snappy and to the point.
  #   * Write the description between the DESC delimiters below.
  #   * Finally, don't worry about the indent, CocoaPods strips it!
  spec.description  = <<-DESC
  NSObject+Rx
                   DESC

  spec.homepage     = "https://github.com/RxSwiftCommunity/NSObject-Rx"


  spec.author             = { "cctip-fd" => "cctip_fd@proton.me" }
  

  spec.source       = { :git => "", :tag => "#{spec.version}" }

  spec.ios.deployment_target = '9.0'


  spec.source_files  = "Classes", "Classes/**/*.{swift}"
  spec.exclude_files = "Classes/Exclude"
  spec.dependency 'RxSwift', '~> 6.5'

end
