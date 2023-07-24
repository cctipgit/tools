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

const CCTipWebView = requireNativeComponent('CCTipWebView');
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
        headerBackTitle: 'fsafs',
      });
    }, 200);
  }, []);

  return (
    <View style={{flex: 1, backgroundColor: '#24262B'}}>
      <CCTipWebView
        style={{flex: 1}}
        url="https://bc.game/"
        onMessage={e => {
          console.log('onMessage', e.nativeEvent.message);
        }}
        ref={ref}
      />
    </View>
  );
};
