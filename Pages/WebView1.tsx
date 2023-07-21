import {Alert, SafeAreaView, Text, View} from 'react-native';
import React, {Component, useRef} from 'react';
import WebView, {WebViewMessageEvent} from 'react-native-webview';
import {
  RouteParamsList,
  RouteProp,
  useNavigation,
  useRoute,
} from '@react-navigation/native';

const jscode = ``;

// const po=`window.open=()=>alert(123)`

type ProfileScreenRouteProp = RouteProp<RouteParamsList, 'webview1'>;

export default () => {
  const router = useRoute<ProfileScreenRouteProp>();

  const navigation = useNavigation();

  const url = router?.params?.url || 'https://bc.game';
  const params: any = router?.params;
  console.log(params, '参数');

  React.useEffect(() => {
    setTimeout(() => {
      navigation.setOptions({
        headerShown: false,
        headerBackTitleVisible: false,
        headerBackTitle: 'fsafs',
      });
    }, 100);

    if (params?.type == 'login') {
      Alert.alert('登陆');
      webRef.current.postMessage(JSON.stringify(params));
    }
  }, [params]);

  const webRef = useRef<any>(null);

  const onMessage = (event: WebViewMessageEvent) => {
    console.log(event.nativeEvent,'sdfdsfsdkfghdsfhskdhf');

    
    try {
      if (typeof event.nativeEvent.data != 'string') return;
      
      let data = JSON.parse(event.nativeEvent.data);
      if (!data.type) {
        data.type = 'login';
        navigation.navigate('webview', data);
        return;
      }
      if (data.type == 1) {
        navigation.navigate('webview1', {url: data.url});
      }
    } catch (error) {}
  };

  return (
    <View style={{flex: 1, backgroundColor: '#25272C'}}>
      <SafeAreaView style={{flex: 1}}>
        <WebView
          source={{uri: url}}
          ref={webRef}
          enableApplePay={false}
          messagingWithWebViewKeyEnabled={true}
          javaScriptCanOpenWindowsAutomatically={true}
          javaScriptEnabled
          mixedContentMode="always"
          injectedJavaScript="function loginCancel(e) {e.preventDefault();window.ReactNativeWebView.postMessage('close');} ; window.opener = {postMessage: (e)=>{window.ReactNativeWebView.postMessage(e)}} "
          injectedJavaScriptBeforeContentLoaded="window.postMessage = function (e) {window.ReactNativeWebView.postMessage(e)};window.opener = {postMessage: (e)=>{window.ReactNativeWebView.postMessage(e)}}"
          style={{flex: 1, backgroundColor: '#25272C'}}
          onMessage={onMessage}
          injectedJavaScriptForMainFrameOnly={false}
          setSupportMultipleWindows={true}
          // onNavigationStateChange={e => {
          //   // console.log(e);
          //   webRef?.current.injectJavaScript(` setTimeout(() => {
          //     window.open=null
          //     alert('完成')
          //   }, 3000);`);
          // }}
          // onLoadProgress={({ nativeEvent }) => {
          //   // this.loadingProgress = nativeEvent.progress;
          //   console.log(nativeEvent);

          // }}

          // onLoadEnd={({nativeEvent}) => {
          //   if (nativeEvent.url == 'https://bc.game/#/login') {
          //     webRef?.current.injectJavaScript(`
          //     setTimeout(() => {
          //       window.open=()=>{
          //         alert(999)
          //       };
          //       alert(123)
          //     }, 1000)`);
          //     console.log(nativeEvent, '加载结束');
          //   }
          // }}
        />
      </SafeAreaView>
    </View>
  );
};
