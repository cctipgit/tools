import {Alert, SafeAreaView, Text, View} from 'react-native';
import React, {Component, useRef} from 'react';
import WebView, {WebViewMessageEvent} from 'react-native-webview';
import {
  RouteParamsList,
  RouteProp,
  useNavigation,
  useRoute,
} from '@react-navigation/native';

const jscode = `var obj = {
    href: "",
    ...window.location,
  };
  Object.defineProperty(obj, "href", {
    set: function (newVal) {
      let data={
        type:1,
        url:newVal
      }
      data=JSON.stringify(data)
      window.ReactNativeWebView.postMessage(data)
  
    },
  });
  window.open = function () {
    return {
      location: obj,
    };
  };`;

type ProfileScreenRouteProp = RouteProp<RouteParamsList, 'webview1'>;

export default () => {
  const router = useRoute<ProfileScreenRouteProp>();
  const navigation = useNavigation();

  const url = router?.params?.url || 'https://cwallet.com';
  const params: any = router?.params;
  console.log(params, '参数');

  React.useEffect(() => {
    if (params?.type == 'login') {
      Alert.alert('登陆');
      webRef.current.postMessage(JSON.stringify(params));
    }
  }, [params]);

  const webRef = useRef<any>(null);

  //   React.useEffect(() => {
  //     setTimeout(() => {
  //       webRef?.current.injectJavaScript(jscode);
  //     }, 3000);
  //   }, []);

  const onMessage = (event: WebViewMessageEvent) => {
    let data = JSON.parse(event.nativeEvent.data);
    console.log(data);
    if (!data.type) {
      data.type = 'login';
      navigation.navigate('webview', data);
      return;
    }
    if (data.type == 1) {
      navigation.navigate('webview1', {url: data.url});
    }
  };

  return (
    <View style={{flex: 1}}>
      <SafeAreaView style={{flex: 1}}>
        <WebView
          source={{uri: url}}
          ref={webRef}
          enableApplePay={false}
          messagingWithWebViewKeyEnabled={true}
          javaScriptCanOpenWindowsAutomatically={true}
          mixedContentMode="always"
          injectedJavaScriptBeforeContentLoaded={jscode}
          style={{flex: 1}}
          onMessage={onMessage}
        />
      </SafeAreaView>
    </View>
  );
};
