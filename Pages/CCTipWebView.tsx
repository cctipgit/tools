import React, {useEffect, useRef} from 'react';
import {
  NativeModules,
  SafeAreaView,
  UIManager,
  View,
  findNodeHandle,
  requireNativeComponent,
} from 'react-native';
import {
  RouteParamsList,
  RouteProp,
  useNavigation,
  useRoute,
} from '@react-navigation/native';

const CCTipWebView: any = requireNativeComponent('NativeWebView');

const Af_Type = {
  af_login: 1,
  af_complete_registration: 2,
  af_purchase: 3,
};

export default () => {
  const ref = useRef(null);
  const navigation = useNavigation();
  React.useEffect(() => {
    setTimeout(() => {
      navigation.setOptions({
        headerShown: false,
        headerBackTitleVisible: false,
        headerBackTitle: '',
      });
    }, 200);
  }, []);

  const reporting = (text: string) => {
    console.log(text);

    try {
      const data = JSON.parse(text);

      NativeModules.ToolModule.logEvent({
        event: data,
        params: {},
      });
    } catch (error) {}
  };
  return (
    <View style={{flex: 1, backgroundColor: '#24262B'}}>
      <SafeAreaView style={{flex: 1}}>
        <CCTipWebView
          style={{flex: 1}}
          url="https://bc.game/"
          onMessage={(e: any) => {
            // console.log('onMessage', e.nativeEvent.message);
            reporting(e.nativeEvent.message);
          }}
          ref={ref}
        />
      </SafeAreaView>
    </View>
  );
};
