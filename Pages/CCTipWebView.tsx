import React, {useEffect, useRef} from 'react';
import {
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

// const CCTipWebView: any = requireNativeComponent('CCTipWebView');
const CCTipWebView = requireNativeComponent('NativeWebView');
// const createFragment = (viewId) =>
//     UIManager.dispatchViewManagerCommand(
//     viewId,
//     UIManager.CCTipWebView.Commands.create.toString(), // we are calling the 'create' command
//     [viewId]
// );

type ProfileScreenRouteProp = RouteProp<RouteParamsList, 'webview2'>;
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

  return (
    <View style={{flex: 1, backgroundColor: '#24262B'}}>
      <SafeAreaView style={{flex:1}}>
        <CCTipWebView
          style={{flex: 1}}
          url="https://bc.game/"
          onMessage={(e: any) => {
            console.log('onMessage', e.nativeEvent.message);
          }}
          ref={ref}
        />
      </SafeAreaView>
    </View>
  );
};
