import React, { Component } from 'react';
import { StyleSheet, View, Text, Image } from 'react-native';
import FacebookLogin from './auth/facebook/LoginButton';
import GoogleLogin from './auth/GoogleLoginButton';
 
export default class LoginScreen extends React.Component {
  constructor() {
    super();

    this.state = {
      user: null,
    };
  }
  static navigationOptions = ({navigation})=> {
    const { params = {} } = navigation.state;
    return {
    title: 'Login', 
  }};

  render() {
    return (
      <View style={styles.container}>
      <FacebookLogin 
          onLogin={
            (result) => {
              console.log('RN onLogin')
              if (result.message) {
                alert('error: ' + result.message)
                //alert("Login error: " + result.error);
              } else if (result.isCancelled) {
                alert("Login cancelled");
              } else {
                alert("Login was successful " + result.profile.name +  ' - ' + result.profile.email)
              }
            }
          }
          onLogout={() => alert("logged out")}
      />
      <GoogleLogin 
          onLogin={
            (result) => {
              console.log('Google onLogin')
              if (result.message) {
                alert('error: ' + result.message)
              } else {
                alert("Login was successful " + result.name +  ' - ' + result.email)
              }
            }
          }
          onLogout={() => alert("logged out")}
          onError={
            (result) => {
              if (result.error) {
                alert('error: ' + result.error)
              } else {
                alert("error")
              }
            }
          }
      />
    </View>
  );
}
}


const styles = StyleSheet.create({
container: {
  flex: 1,
  justifyContent: 'center',
  alignItems: 'center',
  backgroundColor: '#F5FCFF',
},

});
