import React, { useEffect, useRef } from "react";
import {UIManager, findNodeHandle, requireNativeComponent} from 'react-native'
import {
    RouteParamsList,
    RouteProp,
    useNavigation,
    useRoute,
  } from '@react-navigation/native';

const CCTipWebView = requireNativeComponent("CCTipWebView")
// const createFragment = (viewId) =>
//     UIManager.dispatchViewManagerCommand(
//     viewId,
//     UIManager.CCTipWebView.Commands.create.toString(), // we are calling the 'create' command
//     [viewId]
// );

type ProfileScreenRouteProp = RouteProp<RouteParamsList, 'webview2'>;
export default ()=>{
    const ref = useRef(null)
    // useEffect(()=>{
    //     const viewId = findNodeHandle(ref.current)
    //     createFragment(viewId)
    // })
    return (
        <CCTipWebView style={{flex:1}} url='https://giveaway.com/' onMessage={(e)=>{
            console.log("onMessage",e.nativeEvent.message)
        }} ref={ref}/>
    )
}