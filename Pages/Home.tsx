import {useAsyncStorage} from '@react-native-async-storage/async-storage';
import {useNavigation} from '@react-navigation/native';
import {useEffect} from 'react';
import {Alert, Button, NativeModules, Text, View} from 'react-native';

function HomeScreen() {
  const navigation = useNavigation();

  const locastorege = useAsyncStorage('from');
  const goApp = async () => {
    const from = await locastorege.getItem();
    if (from) {
      NativeModules.ToolModule.openNative();

      return;
    }
    NativeModules.ToolModule.getAppsFlyerConversionData().then(
      async (e: any) => {
        e = JSON.parse(e);
        console.log(e);

        if (e.media_source) {
          
          locastorege.setItem('true');
          navigation.replace('webview2', {url: ''});
        } else {
          NativeModules.ToolModule.openNative();
        }
      },
    );
  };

  useEffect(() => {
    // navigation.replace('webview2', {url: ''});
    goApp()
  }, []);

  return (
    <View
      style={{
        flex: 1,
        alignItems: 'center',
        justifyContent: 'center',
        backgroundColor: '#24262B',
      }}>
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
            event: 'af_login',
            params: {
              a: 'b',
              c: 'd',
            },
          });
        }}
      />
      <Button
        title="appsflyer data"
        onPress={() => {
          NativeModules.ToolModule.getAppsFlyerConversionData().then(
            console.log,
          );
        }}
      />
      <Button
        title="webveiw"
        onPress={() => {
          navigation.navigate('webview', {});
        }}
      />
      <Button
        title="2webveiw2"
        onPress={() => {
          navigation.replace('webview2', {url: ''});
        }}
      />
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
