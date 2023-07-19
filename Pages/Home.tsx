import { useNavigation } from '@react-navigation/native';
import {Button, NativeModules, Text, View} from 'react-native';
import InAppBrowser from 'react-native-inappbrowser-reborn';

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
      <Button title="inbow" onPress={ async() => {
        const url='https://accounts.google.com/o/oauth2/auth/oauthchooseaccount?client_id=204798707060-8e13rv348l0o9vc1cphpu19dnt9vud6e.apps.googleusercontent.com&redirect_uri=https%3A%2F%2Fgiveaway.com%2Flogin_callback.html&response_type=code&scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile%20https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email&state=state&service=lso&o2v=1&flowName=GeneralOAuthFlow'
      //  navigation.navigate('webview',{}) 
      // setTimeout(()=>{
      //   InAppBrowser.closeAuth()
      // },5000)
    let a=await  InAppBrowser.openAuth(url,'https://giveaway.com') 
 
    console.log(a);
    

      }} />
    </View>
  );
}

export default HomeScreen;
