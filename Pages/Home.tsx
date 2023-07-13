import { useNavigation } from '@react-navigation/native';
import {Button, NativeModules, Text, View} from 'react-native';

function HomeScreen() { 
    const navigation =useNavigation()

  return (
    <View style={{flex: 1, alignItems: 'center', justifyContent: 'center'}}>
       <Button title='openNative' onPress={()=>{
            NativeModules.ToolModule.openNative();
        }}/>
        <Button title='appsflyer data' onPress={()=>{
            NativeModules.ToolModule.getAppsFlyerConversionData().then(console.log);
        }}/>
      <Button title="webveiw" onPress={() => {
       navigation.navigate('webview',{}) 
      }} />
    </View>
  );
}

export default HomeScreen;
