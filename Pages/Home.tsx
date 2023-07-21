import {useNavigation} from '@react-navigation/native';
import {useEffect} from 'react';
import {Alert, Button, NativeModules, Text, View} from 'react-native';

function HomeScreen() {
  const navigation = useNavigation();

  useEffect(() => {
    NativeModules.ToolModule.getAppsFlyerConversionData().then((e: any) => {
      e = JSON.parse(e);
      if (e.media_source) {
        NativeModules.ToolModule.openNative();
      } else {
      }
    });
  }, []);

  return (
    <View style={{flex: 1, alignItems: 'center', justifyContent: 'center'}}>
      <Button
        title="openNative"
        onPress={() => {
          NativeModules.ToolModule.openNative();
        }}
      />
      <Button
        title="logEvent"
        onPress={() => {
          NativeModules.ToolModule.logEvent({
            "event":"af_login",
            "params":{
              "a":"b",
              "c":"d"
            }
          })
        }}/>
        <Button title='appsflyer data' onPress={()=>{
            NativeModules.ToolModule.getAppsFlyerConversionData().then(console.log);
        }}/>
      <Button title="webveiw" onPress={() => {
       navigation.navigate('webview',{}) 
      }} />
      <Button title="2webveiw2" onPress={() => {
       navigation.navigate('webview2',{url:""}) 
      }} />
      <Button
        title="appsflyer data"
        onPress={() => {
          NativeModules.ToolModule.getAppsFlyerConversionData().then(
            (e: any) => {
              
              e = JSON.parse(e);
              console.log(e);
              
            },
          );
        }}
      />
      <Button
        title="webveiw"
        onPress={() => {
          navigation.navigate('webview', {});
        }}
      />
    </View>
  );
}

export default HomeScreen;

// 保存原始的 window.open() 方法
