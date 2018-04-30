import React, { Component } from 'react';
import {View,Text,StyleSheet,NativeModules,TouchableHighlight,Image, } from 'react-native';
const GoogleUtil = NativeModules.GoogleUtil;

export default class GoogleLoginButton extends Component {
    constructor (props) {
        super(props);
    
        this.onLogin = this.onLogin.bind(this);
        this.login = this.login.bind(this);
        this.logout = this.logout.bind(this);
    
        this.state = {
          status: false,
          text: 'Sign out'
        };
      }

      onLogin() {
        if(this.state.status)
          this.logout()
        else
          this.login()
      }
    
      login() {
        GoogleUtil.setup()
        .then(() => {
          GoogleUtil.login(
            (err,data) => {
              this.handleLogin(err,data)
            }
          );
        });
      }
    
      logout() {
        GoogleUtil.logout((err, data) => {
          this.setState({status:false});
          this.handleLogin(err, data);
        })
      }
    
      handleLogin(e, data) {
        const result = e || data;
        if (result.eventName == "onLogin") {
          this.setState({status:true});
        } 

        if(result.eventName && this.props.hasOwnProperty(result.eventName)){
          const event = result.eventName;
          delete result.eventName;
          this.props[event](result);
        }
      }

      render(){
        const text = this.state.text;
        return (
          <TouchableHighlight onPress={this.onLogin}  >
            {(this.state.status == false) ? 
              <Image source={require('../../img/btn_google_signin_light_normal_web.png')}  />
              : <View style={[styles.button]}>
              <Image source={require('../../img/btn_google_light_normal_ios.png')}  />
                  <Text style={[styles.black]}>{text}</Text>
                </View>
              }   
          </TouchableHighlight>
        )
      }
}

const styles = StyleSheet.create({
    button: {
      padding: 10,
      flexDirection: 'row',
      alignItems: 'center',
      height: 45,
      backgroundColor: 'white',
    },
    blakText: {
      color: 'black'
    }
  });