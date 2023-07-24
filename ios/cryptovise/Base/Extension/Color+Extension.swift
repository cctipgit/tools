//
//  Color+Extension.swift
//  cryptovise
//
//  Created by fd on 2022/10/28.
//

import Foundation

extension UIColor {
    static var backgroundColor: UIColor {
        return UIColor.color(with: "#FFFFFF", darkHexString: "#17171C")
    }

    static var primaryTextColor: UIColor {
        return UIColor.color(with: "#000000", darkHexString: "#FFFFFF")
    }

    static var secondaryTextColor: UIColor {
        return UIColor.color(with: "#ABABAB", darkHexString: "#ABABAB")
    }

    static var placeholderTextColor: UIColor {
        return UIColor.color(with: "#000000", lightAlpha: 0.4,
                             darkHexString: "#FFFFFF", darkAlpha: 0.4)
    }

    static var textColorOf60Alpha: UIColor {
        return UIColor.color(with: "#000000", lightAlpha: 0.6,
                             darkHexString: "#FFFFFF", darkAlpha: 0.6)
    }

    static var textColorOf50Alpha: UIColor {
        return UIColor.color(with: "#000000", lightAlpha: 0.5,
                             darkHexString: "#FFFFFF", darkAlpha: 0.5)
    }

    static var separatorColor: UIColor {
        return UIColor.color(with: "#000000", lightAlpha: 0.05,
                             darkHexString: "#FFFFFF", darkAlpha: 0.1)
    }

    static var textColorOf30Alpha: UIColor {
        return UIColor.color(with: "#000000", lightAlpha: 0.3,
                             darkHexString: "#FFFFFF", darkAlpha: 0.3)
    }

    static var selectedBackgroundColor: UIColor {
        return .color(with: "#F5F6F7",
                      darkHexString: "#272A31")
    }

    static var selectedShadowColor: UIColor {
        return .color(with: "#000000", lightAlpha: 0.04,
                      darkHexString: "#000000", darkAlpha: 0.04)
    }

    static var selectedViewBorderColor: UIColor {
        return .color(with: "#000000", lightAlpha: 0.05,
                      darkHexString: "#FFFFFF", darkAlpha: 0.05)
    }

    static var keyboardNumberColor: UIColor {
        return .color(with: "#F2F2F5",
                      darkHexString: "#F2F2F5", darkAlpha: 0.10)
    }

    static var keyboardNumberPressedColor: UIColor {
        return .color(with: "#E4E5EB",
                      darkHexString: "#F2F2F5", darkAlpha: 0.35)
    }

    static var keyboardOpeartorColor: UIColor {
        return .color(with: "#1E1E1E",
                      darkHexString: "#B6CEFE",darkAlpha: 0.9)
    }

    static var keyboardOpeartorPressedColor: UIColor {
        return .color(with: "#3D3D3D",
                      darkHexString: "#B6CEFE", darkAlpha: 0.3)
    }

    static var switchLeftTitleColor: UIColor {
        return .color(with: "#B6CEFE", darkHexString: "#B6CEFE")
    }

    static var switchLeftBackgroundColor: UIColor {
        return .color(with: "#000000", darkHexString: "#4482FF")
    }

    static var b6cefeColorOf20Alpha: UIColor {
        return .color(with: "#B6CEFE", lightAlpha: 0.2,
                      darkHexString: "#B6CEFE", darkAlpha: 0.2)
    }

    static var b6cefeColorOf10Alpha: UIColor {
        return .color(with: "#B6CEFE", lightAlpha: 0.1,
                      darkHexString: "#B6CEFE", darkAlpha: 0.1)
    }

    static var primaryBlue: UIColor {
        return .color(with: "#B6CEFE", darkHexString: "#B6CEFE")
    }

    static var grayColor: UIColor {
        return .color(with: "#F2F2F5",
                      darkHexString: "#F2F2F5", darkAlpha: 0.1)
    }

    static var searchBarBorderColor: UIColor {
        return .color(with: "#E7E7EC",
                      darkHexString: "#FFFFFF", darkAlpha: 0.2)
    }

    static var searchBarBackgroundColor: UIColor {
        return .color(with: "F2F2F5",
                      darkHexString: "#FFFFFF", darkAlpha: 0.15)
    }

    static var darkBlueColor: UIColor {
        return .color(with: "#4A80EE", darkHexString: "#4A80EE")
    }

    static var panIndicatorColor: UIColor {
        return .color(with: "#000000",
                      darkHexString: "#FFFFFF", darkAlpha: 0.4)
    }
    
    static var segementViewSelectBackgroundColor:UIColor {
        return .color(with: "#000000", darkHexString: "#B6CEFE")
    }
    
    static var segementViewSelectTitleColor: UIColor {
        return .color(with: "#B6CEFE", darkHexString: "#000000")
    }
    

    static var upColor: UIColor {
        if AppSetting.shared.quoteColor.value {
            return .color(with: "#FA5B64", darkHexString: "#FA5B64")
        } else {
            return .color(with: "#AAFA5B", darkHexString: "#AAFA5B")
        }
    }

    static var downColor: UIColor {
        if AppSetting.shared.quoteColor.value {
            return .color(with: "#AAFA5B", darkHexString: "#AAFA5B")
        } else {
            return .color(with: "#FA5B64", darkHexString: "#FA5B64")
        }
    }
    
    static func color(with lightHexString: String, lightAlpha: CGFloat = 1,
                      darkHexString: String, darkAlpha: CGFloat = 1) -> UIColor {
        let lightColor = UIColor(hexString: lightHexString, transparency: lightAlpha)
        let darkColor = UIColor(hexString: darkHexString, transparency: darkAlpha)

        return UIColor(.dm, light: lightColor!, dark: darkColor!)
    }

    func alpha(_ alpha: CGFloat) -> UIColor {
        return withAlphaComponent(alpha)
    }
    
    // MARK: New Color
    
    static var contentSecondary: UIColor {
        return .color(with: "#A1A1AA", darkHexString: "#A1A1AA")
    }
    
    static var primaryBranding: UIColor {
        return .color(with: "#7747F2", darkHexString: "#7747F2")
    }
    
    static var basicBlk: UIColor {
        return .color(with: "#000000", darkHexString: "#FFFFFF")
    }
    
    static var basicPurpleLight: UIColor {
        return .color(with: "#8458F3", darkHexString: "#8458F3")
    }
    
    static var bgDisabled: UIColor {
        return .color(with: "#E4E4E7", darkHexString: "#E4E4E7")
    }
    
    static var bgSecondary: UIColor {
        return .color(with: "#FAFAFA", darkHexString: "FAFAFA")
    }
    
    static var contentDisabled: UIColor {
        return .color(with: "#A1A1AA", darkHexString: "#A1A1AA")
    }
    
    static var bgGroupBranding: UIColor {
        return .color(with: "#AB8DF7", darkHexString: "#AB8DF7")
    }
    
    static var primaryYellow: UIColor {
        return .color(with: "#F9A01E", darkHexString: "#F9A01E")
    }
    
    static var separatorDefault: UIColor {
        return .color(with: "#F4F4F5", darkHexString: "#F4F4F5")
    }
    
    static var primaryPositive: UIColor {
        return .color(with: "#53F245", darkHexString: "#53F245")
    }
    
    static var primaryWarning: UIColor {
        return .color(with: "#F2AA45", darkHexString: "#F2AA45")
    }
    
    static var primaryNegative: UIColor {
        return .color(with: "#F24E45", darkHexString: "#F24E45")
    }
    
    static var primarySuccess: UIColor {
        return .color(with: "#0CC96A", darkHexString: "#0CC96A")
    }
    
    static var toolViewBGColor: UIColor {
        return .color(with: "#24262B", darkHexString: "#24262B")
    }
}
