package com.hash.coinconvert;

/**
 * Constants files
 * create by duxl 2021/2/20
 */
public interface Constants {

    /*** psge size */
    int PAGE_SIZE = 20;

    /*** coin symbol */
    String RMB_SYMBOL = "¥";

    /*** RATE REFRESH */
    long RATE_UI_REFRESH = 1500;
    long QUOTE_UI_REFRESH = 100;
    long QUOTE_DATA_REFRESH = 500;

    int MAX_FREQUENTLY_TOKEN = 12;

    /*** socket address */
//    String SOCKET_SERVER = "ws://sea.linkflower.link:2100/wss";
    String SOCKET_SERVER = "ws://api.exchange2currency.com/wss";

    /*** CURRENCY TYPE */
    interface CURRENCY_TYPE {
        /*** currency  */
        String CURRENCY = "currency";
        /*** digital  */
        String DIGITAL = "digital";
        /*** futures  */
        String FUTURES = "futures";
    }

    interface SP {
        /*** key value */
        interface KEY {
            /*** DEFAULT_LOCAL_CURRENCY */
            String DEFAULT_LOCAL_CURRENCY = "key_default_local_currency";
            /*** DEFAULT_CURRENCY_SYMBOL */
            String DEFAULT_CURRENCY_SYMBOL = "key_default_currency_symbol";
            /*** DEFAULT_CURRENCY_VALUE */
            String DEFAULT_CURRENCY_VALUE = "key_default_currency_value";
            /*** DECIMICAL_LEGAL_TENDER */
            String DECIMICAL_LEGAL_TENDER = "decimical_legal_tender";
            /*** DECIMICAL_CRYPTOCURRENCY */
            String DECIMICAL_CRYPTOCURRENCY = "decimical_cryptocurrency";
            /*** Market display color: 0 anthurium, green down, 1 green up, red down */
            String QUOTE_COLOR = "quote_color";
            /*** KEYBOARD_SOUND */
            String KEYBOARD_SOUND = "keyboard_sound";
            /*** LOCATION_ACCESS */
            String LOCATION_ACCESS = "location_access";
            /*** LOCATION_COUNTRY_CODE */
            String LOCATION_COUNTRY_CODE = "location_Country_Code";
            /*** The token list on the home page stores the json data of the token list */
            String HOME_TOKEN_LIST = "home_token_list";
            /*** FREQUENTLY_TOKEN_LIST */
            String FREQUENTLY_TOKEN_LIST = "frequently_token_list";
            String TIME_TYPE = "time_type";
            String CURRENT_NAME = "CURRENT_NAME";
            String CURRENT_SLIDE_TYPE = "CURRENT_SLIDE_TYPE";
            String CURRENT_TYPE = "CURRENT_TYPE";
            String CURRENT_ICON = "CURRENT_ICON";
            String CURRENT_FIRST = "CURRENT_FIRST";
            /***CURRENT_BACK*/
            String CURRENT_BACK = "CURRENT_BACK";
            /***CURRENT_BACK_NAME*/
            String CURRENT_BACK_NAME = "CURRENT_BACK_NAME";
            /*** The number of exchange rate items on the homepage (dynamically calculated and cached according to different mobile phones) */
            String RATE_ITEM_COUNT = "rate_item_count";
            String ISCURRENT = "ISCURRENT";
            String CHANGEVALUE = "CHANGEVALUE";
            String ORIGINCURRENCYCOUNT = "OriginCurrencyCount";
            String SWIFTH_STATUS = "SWIFTH_STATUS";
            /**
             * user info
             */
            String USER_INFO = "usr";
        }

        /*** cache default */
        interface DEFAULT {
            /*** DEFAULT_LOCAL_CURRENCY */
            boolean DEFAULT_LOCAL_CURRENCY = true;
            /*** DEFAULT_CURRENCY_SYMBOL */
            boolean DEFAULT_CURRENCY_SYMBOL = true;
            /*** DEFAULT_CURRENCY_VALUE */
            int DEFAULT_CURRENCY_VALUE = 100;
            /*** DECIMICAL_LEGAL_TENDER */
            int DECIMICAL_LEGAL_TENDER = 4;
            /*** DECIMICAL_CRYPTOCURRENCY */
            int DECIMICAL_CRYPTOCURRENCY = 6;
            /*** Market display color: 0 anthurium, green down, 1 green up, red down */
            int QUOTE_COLOR = 0;
            /*** KEYBOARD_SOUND */
            boolean KEYBOARD_SOUND = true;
            /*** RATE_ITEM_COUNT */
            int RATE_ITEM_COUNT = 6;
        }
    }

    /*** 接口响应code */
    interface HttpResponseCode {
        int DEFAULT = 0; // success
        int APP_USER_LOGOUT = 10; // This account has already been logged in on another device, please log in again
        int AUTHORIZATION_INVALID = 11; // Authorization code invalid (need to re-authorize)
        int AUTHORIZATION_NONE = 12; //Authorization code invalid (need to re-authorize)
    }
}
