// In App.js in a new project

import * as React from 'react';
import {View, Text} from 'react-native';
import {NavigationContainer, RouteProp} from '@react-navigation/native';
import {createNativeStackNavigator} from '@react-navigation/native-stack';
import HomeScreen from '../Pages/Home';
import WebView from '../Pages/WebView';
import CCTipWebViewScreen from '../Pages/CCTipWebView';
import type {
  NativeStackNavigationProp,
  NativeStackScreenProps,
} from '@react-navigation/native-stack';
import WebView1 from '../Pages/WebView1';

type RootStackParamList = {
  Home: {};
  webview: {};
  webview1: {url: string};
  webview2:{url:string};
};

// 定义全局导航器对象类型
declare global {
  namespace ReactNavigation {
    interface RootParamList extends RootStackParamList {}
  }
}

// 定义导航器对象的类型别名
type NavigationType = NativeStackNavigationProp<RootStackParamList>;
type ProfileScreenRouteProp = RouteProp<RootStackParamList, 'webview1'>;

// 在全局范围中添加导航器类型声明
declare module '@react-navigation/native' {
  export function useNavigation<T = NavigationType>(): T;
  export type RouteParamsList = RootStackParamList;
}

const Stack = createNativeStackNavigator<RootStackParamList>();

function Route() {
  return (
    <NavigationContainer>
      <Stack.Navigator
        screenOptions={{
          headerShown: false,
          headerBackTitleVisible: false,
          headerBackTitle: '',
       
        }}>
        <Stack.Screen name="Home" component={HomeScreen} />
         <Stack.Screen
          name="webview"
          component={WebView}
          options={{
            headerShown: false,
            headerBackTitleVisible: false,
          }}
        />
        <Stack.Screen
          name="webview1"
          component={WebView1}
          options={{
            headerShown: false,
            headerBackTitleVisible: false,
            presentation: 'modal',
          }}
        />
        <Stack.Screen
          name="webview2"
          component={CCTipWebViewScreen}
          options={{
            headerShown: false,
            headerBackTitleVisible: false,
            presentation: 'modal',
          }}
        />
      </Stack.Navigator>
    </NavigationContainer>
  );
}

export default Route;
