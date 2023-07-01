
public enum SocketMethod: String {
    case HEAD
    case GET

    case POST
    case CONNECT
}

public extension URL {
    static var socketBaseURL: URL!
}

public struct SocketConstant {
    public static let kCmd = "cmd"
    public static let kCid = "cid"
    public static let kService = "service"
    public static let kMethod = "method"
    public static let kContent = "content"
    public static let kError = "error"
    
    public static let socketDidOpendNotification = Notification.Name(rawValue: "com.socketDidOpendNotfication")
}

public enum RequestCmd: Int32 {
    case quotationRequest = 1
    case quotationStopRequest = 3
    case symbolRateRequest = 5

    case currencyTokensRequest = 7
    case currencyTokensListRequest = 9
    case currentCurrencyTokensRequest = 11
}

public enum ResponseCmd: Int32 {
    case quotationResponse = 2
    case quotationStopResponse = 4
    case symbolRateResponse = 6

    case currencyTokensRespone = 8
    case currencyTokensListResponse = 10
    case currentCurrencyTokensResponse = 12
}

public enum ReqeustService: String {
    case price = "PriceRPC"
    case currency = "CurrencyRPC"
}
