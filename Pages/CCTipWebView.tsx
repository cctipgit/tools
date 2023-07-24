import React, {useEffect, useRef} from 'react';
import {
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

const CCTipWebView: any = requireNativeComponent('CCTipWebView');

type ProfileScreenRouteProp = RouteProp<RouteParamsList, 'webview2'>;
export default () => {
  const ref = useRef(null);
  const navigation = useNavigation();
  React.useEffect(() => {
    setTimeout(() => {
      navigation.setOptions({
        headerShown: false,
        headerBackTitleVisible: false,
        headerBackTitle: 'fsafs',
      });
    }, 200);
  }, []);

  return (
    <View style={{flex: 1, backgroundColor: '#24262B'}}>
      {/* <View style={{height:300,backgroundColor:'red'}}></View> */}
      <CCTipWebView
        style={{flex: 1}}
        url="https://bc.game/"
        onMessage={(e: any) => {
          console.log('onMessage', e.nativeEvent.message);
        }}
        ref={ref}
      />
    </View>
  );
};
