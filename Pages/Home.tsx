import AsyncStorage, {
  useAsyncStorage,
} from '@react-native-async-storage/async-storage';
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
        if (e.media_source) {
          locastorege.setItem('true');
          NativeModules.ToolModule.openNative();
        } else {
        }
      },
    );
  };

  useEffect(() => {
    goApp();
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
          locastorege.setItem('true');
          NativeModules.ToolModule.getAppsFlyerConversionData().then(
            (e: any) => {
              e = JSON.parse(e);
              console.log(e);
            },
          );
        }}
      />
      {/* <Button
        title="取消"
        onPress={ async () => {
         console.log(  await locastorege.getItem())
          locastorege.removeItem()
        }}
      /> */}
    </View>
  );
}

export default HomeScreen;

// 保存原始的 window.open() 方法
