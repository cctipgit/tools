//
//  TCSectorModel.swift
//  cryptovise
//
//  Created by admin on 2023/7/3.
//

import Foundation
import UIKit
import Kingfisher
import Then

class TCPrizeContentLayer: NSObject {
    var tcShape = CAShapeLayer()
    fileprivate var tcPrizeImage = UIImageView()
    fileprivate var tcMarkImage = CALayer()
    fileprivate var tcDescLabel = UILabel().then { v in
        v.font = .monomaniacOneRegular(with: 26)
        v.textColor = .white
        v.textAlignment = .center
    }
    
    override init() {
        super.init()

        tcShape.masksToBounds = true
        
        tcPrizeImage.contentMode = .scaleAspectFit
        tcPrizeImage.clipsToBounds = true
        
        tcMarkImage.contentsGravity = CALayerContentsGravity.resizeAspect
        tcMarkImage.contentsScale = UIScreen.main.scale
        tcMarkImage.masksToBounds = true
        
        tcShape.addSublayer(tcPrizeImage.layer)
        tcShape.addSublayer(tcMarkImage)
        tcShape.addSublayer(tcDescLabel.layer)
    }
    
    func setPrizeImage(_ image: UIImage?, url: String?) {
        self.tcPrizeImage.image = nil
        if let image = image {
            self.tcPrizeImage.image = image
        } else if let url = url {
            self.tcPrizeImage.kf.setImage(with: URL(string: url))
        }
    }
    
    func setMarkImage(_ image: UIImage?) {
        self.tcMarkImage.contents = image?.cgImage
    }
    
    func setDescLabel(desc: String?) {
        self.tcDescLabel.text = desc
    }
}

class TCSectorModel {
    static var sectorCount  = 8
    static var circleRadius: CGFloat = 150
    static let centerRadius: CGFloat = 0
    static var sectorRadian: CGFloat { return (CGFloat.pi * 2) / CGFloat(self.sectorCount) }
    
    static func tcSectorLayers() -> [TCPrizeContentLayer] {
        var layers: Array<TCPrizeContentLayer> = []
        let radian = sectorRadian / 2
        let rectWidth_2 = circleRadius * tan(radian)
        let xValue = rectWidth_2 - circleRadius * sin(radian)
        let yValue = circleRadius - circleRadius * cos(radian)
        let ccXValue = centerRadius * sin(radian)
        let ccYValue = centerRadius * cos(radian)
        let startPoint = CGPoint(x: xValue, y: yValue)
        for index in 0..<self.sectorCount {
            let tcShapeLayer = TCPrizeContentLayer()
            let spLayer = tcShapeLayer.tcShape
            
            spLayer.frame = CGRect(x: 0, y: 0, width: rectWidth_2 * 2, height: circleRadius)
            tcShapeLayer.tcPrizeImage.frame = CGRect(x: 0, y: 0, width: rectWidth_2 * 2, height: circleRadius / 2)
            // fix
            let fixOffset: [CGFloat] = [4, 7, 7, 2, -4, -8, -10, -6]
            if index < 8 {
                tcShapeLayer.tcPrizeImage.frame = CGRect(x: (rectWidth_2 * 2 - 50 ) / 2.0 + fixOffset[index] , y: circleRadius / 2 - 50 + 16, width: 50 , height: 50)
                tcShapeLayer.tcDescLabel.frame = CGRect(x: fixOffset[index], y: 10, width: rectWidth_2 * 2, height: 26)
            }
            
            tcShapeLayer.tcMarkImage.frame = CGRect(x: 0, y: 30, width: rectWidth_2 * 2, height: 30)
            let path = UIBezierPath()
            path.move(to: startPoint)
            path.addArc(withCenter: CGPoint(x: rectWidth_2, y: circleRadius),
                        radius: circleRadius,
                        startAngle: CGFloat.pi / 2 * 3 - radian ,
                        endAngle: CGFloat.pi / 2 * 3 + radian , clockwise: true)
            path.addLine(to: CGPoint(x: rectWidth_2 + ccXValue, y: circleRadius - ccYValue))
            path.addArc(withCenter: CGPoint(x: rectWidth_2, y: circleRadius),
                        radius: centerRadius,
                        startAngle: CGFloat.pi / 2 * 3 + radian,
                        endAngle: CGFloat.pi / 2 * 3 - radian, clockwise: false)
            path.close()
            
            spLayer.path = path.cgPath
            let tempR = circleRadius / 2
            let x = circleRadius + tempR * sin(sectorRadian * CGFloat(index))
            let y = circleRadius - tempR * cos(sectorRadian * CGFloat(index))
            spLayer.position = CGPoint(x: x, y: y)
            spLayer.fillColor = UIColor.clear.cgColor
            spLayer.strokeColor = UIColor.clear.cgColor
            
            var transform = CATransform3DIdentity
            transform = CATransform3DMakeRotation(sectorRadian * CGFloat(index) , 0, 0, 1)
            spLayer.transform = transform
            
            let mask = CAShapeLayer()
            mask.path = path.cgPath
            spLayer.mask = mask
            
            layers.append(tcShapeLayer)
        }
        return layers
    }
}

extension CGPoint {
    func tcRotate(angle: CGFloat, clockwise: Bool = true) -> CGPoint {
        var x1 = x
        var y1 = y
        if clockwise {
            x1 = x * cos(angle) + y * sin(angle)
            y1 = y * cos(angle) - x * sin(angle)
        } else {
            x1 = x * cos(angle) - y * sin(angle)
            y1 = x * sin(angle) + y * cos(angle)
        }
        return CGPoint(x: x1, y: y1)
    }
    
    func tcSum(point: CGPoint) -> CGPoint {
        return CGPoint(x: x + point.x, y: y + point.y)
    }
}
