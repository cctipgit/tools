//
//  TCDrawPrizeView.swift
//  cryptovise
//
//  Created by admin on 2023/7/3.
//

import UIKit
import Then

enum TCPrizeType {
    case normal     // normal
    case drawOut    // prize been take
    case empty      // no prize
}

class TCPrizeModel: NSObject {
    var image: String = ""
    var type: TCPrizeType = TCPrizeType.empty
}

protocol TCDrawPrizeDelegate: AnyObject {
    func tcDrawPrizeStartAction(prizeView: TCDrawPrizeView)
    func tcDrawPrizeEndAction(prizeView: TCDrawPrizeView, prize index: NSInteger)
}

protocol TCDrawPrizeDataSource: AnyObject {
    func numberOfPrize(for drawprizeView: TCDrawPrizeView) -> NSInteger
    func tcDrawPrize(prizeView: TCDrawPrizeView, imageAt index: NSInteger) -> UIImage?
    func tcDrawPrize(prizeView: TCDrawPrizeView, imageUrlAt index: NSInteger) -> String?
    func tcDrawPrize(prizeView: TCDrawPrizeView, drawOutAt index: NSInteger) -> Bool
    func tcDrawPrizeCenterImage(prizeView: TCDrawPrizeView) -> UIImage
    func tcDrawPrizeBackgroundImage(prizeView: TCDrawPrizeView) -> UIImage?
    func tcDrawPrizeScrollBackgroundImage(prizeView: TCDrawPrizeView) -> UIImage?
    func tcDrawPrize(prizeView: TCDrawPrizeView, descAt index: NSInteger) -> String?
}

extension TCDrawPrizeDataSource {
    func tcDrawPrize(prizeView: TCDrawPrizeView, imageAt index: NSInteger) -> UIImage? {
        return nil
    }
    func tcDrawPrize(prizeView: TCDrawPrizeView, imageUrlAt index: NSInteger) -> String? {
        return nil
    }
    func tcDrawPrize(prizeView: TCDrawPrizeView, descAt index: NSInteger) -> String? {
        return nil
    }
}

class TCDrawPrizeView: UIView {
    
    weak var delegate: TCDrawPrizeDelegate?
    weak var dataSource: TCDrawPrizeDataSource? {
        didSet {
            self.reloadData()
        }
    }
    
    fileprivate var prizeIndex = -1
    fileprivate let centerImgView = UIImageView().then { v in
        v.backgroundColor = UIColor.clear
        v.frame = CGRect(x: 0, y: 0, width: 73, height: 73)
        v.contentMode = .scaleAspectFill
    }
    fileprivate let compassImageView = UIImageView().then { v in
        v.image = UIImage(named: "n_compass")
        v.frame = CGRect(x: 0, y: 0, width: 35, height: 61)
    }
    
    let bigBackImage = UIImageView()
    let prizeContentBackImage = UIImageView()
    let prizeContentLayer = CALayer()
    var contentOffset: CGFloat = 55
    
    fileprivate var tcCount = 0 // total count(contain no prize)
    
    fileprivate var tcShapeLayers: Array<TCPrizeContentLayer> = []
    fileprivate var tcDrawOutValue: Dictionary<Int, Bool> = [:]
    
    override init(frame: CGRect) {
        super.init(frame: frame)
    }
    
    convenience init(_ origin: CGPoint, width: CGFloat) {
        self.init(frame: CGRect(x: origin.x, y: origin.y, width: width, height: width))
        self.backgroundColor = UIColor.clear
        self.clipsToBounds = false
        let prizeContentWidth = width - contentOffset * 2
        TCSectorModel.circleRadius = prizeContentWidth / 2.0

        bigBackImage.frame = CGRect(x: 0, y: 0, width: width, height: width)
        bigBackImage.backgroundColor = UIColor.clear
        bigBackImage.contentMode = .scaleAspectFill
        bigBackImage.clipsToBounds = false
      
        prizeContentBackImage.frame = CGRect(origin: CGPoint(x: contentOffset, y: contentOffset), size: CGSize(width: prizeContentWidth, height: prizeContentWidth))
        prizeContentBackImage.backgroundColor = UIColor.clear
        prizeContentBackImage.contentMode = .scaleAspectFill
        prizeContentBackImage.layer.cornerRadius = prizeContentWidth / 2.0
        prizeContentBackImage.layer.masksToBounds = true
        
        // Prize Content
        prizeContentLayer.frame = prizeContentBackImage.bounds
        prizeContentLayer.backgroundColor = UIColor.clear.cgColor
        
        centerImgView.center = CGPoint(x: width / 2, y: width / 2)
        compassImageView.center = CGPoint(x: width / 2, y: 38)
        
        self.prizeContentBackImage.layer.addSublayer(prizeContentLayer)
        self.addSubviews([bigBackImage, prizeContentBackImage, centerImgView, compassImageView])
    }
    
    public func startDrawPrizeAction() {
        self.reset()
        NotificationCenter.default.post(name: Notification.Name(drawPrizeChanceChangedNofification), object: nil)
        delegate?.tcDrawPrizeStartAction(prizeView: self)
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func reloadData() {
        if let dataSouce = dataSource {
            tcCount = dataSouce.numberOfPrize(for: self)
            if tcCount < 3 {
                fatalError("At least 3!")
            }
            
            TCSectorModel.sectorCount = tcCount
            tcShapeLayers.removeAll()
            tcShapeLayers = TCSectorModel.tcSectorLayers()

            // transform to vertical direction
            var transform = CATransform3DIdentity
            transform = CATransform3DMakeRotation(TCSectorModel.sectorRadian / 2 , 0, 0, 1)
            prizeContentLayer.transform = transform

            self.centerImgView.image = dataSouce.tcDrawPrizeCenterImage(prizeView: self)
            let pLayers = self.prizeContentLayer.sublayers
            if let pLayers = pLayers {
                for sl in pLayers {
                    sl.removeFromSuperlayer()
                }
            }
            for n in 0..<tcCount {
                let tempI = (tcCount - 1 - n)
                let spl = tcShapeLayers[tempI]
                spl.setPrizeImage(dataSouce.tcDrawPrize(prizeView: self, imageAt: tempI), url: dataSouce.tcDrawPrize(prizeView: self, imageUrlAt: tempI))
                spl.setDescLabel(desc: dataSouce.tcDrawPrize(prizeView: self, descAt: n))
                if dataSouce.tcDrawPrize(prizeView: self, drawOutAt: tempI) {
                    spl.setMarkImage(#imageLiteral(resourceName: "drawOut"))
                } else {
                    spl.setMarkImage(nil)
                }
                self.prizeContentLayer.addSublayer(spl.tcShape)
            }
            bigBackImage.image = dataSouce.tcDrawPrizeBackgroundImage(prizeView: self)
            if let cbimage = dataSouce.tcDrawPrizeScrollBackgroundImage(prizeView: self) {
                prizeContentBackImage.image = cbimage
            } else {
                prizeContentBackImage.image = nil
                var index = 0
                for shape in tcShapeLayers {
                    shape.tcShape.strokeColor = UIColor.white.cgColor
                    if index % 2 == 0 {
                        shape.tcShape.fillColor = UIColor.init(red: 129 / 255.0, green: 203 / 255.0, blue: 1, alpha: 1).cgColor
                    } else {
                        shape.tcShape.fillColor = UIColor.init(red: 165 / 255.0, green: 218 / 255.0, blue: 1, alpha: 1).cgColor
                    }
                    index += 1
                }
            }
        }
    }
    
    func reset() {
        self.prizeIndex = -1
        self.prizeContentBackImage.layer.transform = CATransform3DIdentity
    }
    
    /// start draw
    /// - Parameters:
    ///   - index: prize index
    ///   - reject: last draw not finish
    func drawPrize(at index: NSInteger, reject:((Bool) -> Void)? = nil) {
        if index < tcCount, index >= 0 {
            if self.isAnimating {
                reject?(true)
                return
            }
            self.isAnimating = true
            reject?(false)
            self.prizeIndex = (self.tcCount - 1 - index)
            self.prizeContentBackImage.layer.add(self.rotateAnimation, forKey: "rotationAnimation")
        } else {
            fatalError("Invalid index")
        }
    }
    
    fileprivate var tcAnimation: CABasicAnimation? = nil
    fileprivate var isAnimating = false
    var rotateAnimation: CABasicAnimation {
        if tcAnimation == nil {
            tcAnimation = CABasicAnimation(keyPath: "transform.rotation.z")
            tcAnimation?.duration = 3
            tcAnimation?.isCumulative = true
            tcAnimation?.timingFunction = CAMediaTimingFunction.init(name: CAMediaTimingFunctionName.easeInEaseOut)
            tcAnimation?.fillMode = CAMediaTimingFillMode.forwards
            tcAnimation?.isRemovedOnCompletion = false
            tcAnimation?.delegate = self
        }
        let fix = TCSectorModel.sectorRadian / 2
        let rotateValue = CGFloat(prizeIndex) * TCSectorModel.sectorRadian + fix
        tcAnimation?.toValue = ((CGFloat.pi * 2) * 3 + rotateValue)
        
        return tcAnimation!
    }
}

extension TCDrawPrizeView: CAAnimationDelegate {
    func animationDidStop(_ anim: CAAnimation, finished flag: Bool) {
        self.isAnimating = false
        delegate?.tcDrawPrizeEndAction(prizeView: self, prize: (self.tcCount - 1 - self.prizeIndex))
    }
}
