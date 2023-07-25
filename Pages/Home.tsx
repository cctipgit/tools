import {useAsyncStorage} from '@react-native-async-storage/async-storage';

import {useEffect, useState} from 'react';
import {
  NativeModules,
  SafeAreaView,
  View,
  requireNativeComponent,
} from 'react-native';
const CCTipWebView: any = requireNativeComponent('NativeWebView');
function HomeScreen() {
  const locastorege = useAsyncStorage('from');
  const [isAf, setIsAf] = useState(true);
  const goApp = async () => {
    const from = await locastorege.getItem();
    if (from) {
      setIsAf(true);
      return;
    }
    NativeModules.ToolModule.getAppsFlyerConversionData().then(
      async (e: any) => {
        e = JSON.parse(e);
        console.log(e);

        if (e.media_source) {
          setIsAf(true);
          locastorege.setItem('true');
        } else {
          setIsAf(false);
          NativeModules.ToolModule.openNative();
        }
      },
    );
  };

  useEffect(() => {
    goApp();
  }, []);

  if (isAf) return <NativeWebview />;

  return (
    <View
      style={{
        flex: 1,
        alignItems: 'center',
        justifyContent: 'center',
        backgroundColor: '#24262B',
      }}></View>
  );
}

export default HomeScreen;

const NativeWebview = () => {
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
            reporting(e.nativeEvent.message);
          }}
        />
      </SafeAreaView>
    </View>
  );
};
