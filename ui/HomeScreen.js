import React from 'react';
import { View, Button, Text, TouchableOpacity, Image } from 'react-native';
var navStyle = require('./style/navStyles');
var screenStyle = require('./style/screenStyles');


export default class HomeScreen extends React.Component {
  constructor() {
    super();

    this.state = {
      loading: true,
      error: '',
      comments: [],
      count: 0,
    };
  }
  static navigationOptions = ({navigation})=> {
      const { params = {} } = navigation.state;
      return {
      title: 'Auth Nativer',
      headerRight: ( 
        <View style={navStyle.twoButtonView}>
          <TouchableOpacity onPress={() => navigation.state.params.handleAccount()}  style={{ marginRight: 6, }}>
            <Image source={require('../img/ic_account_circle_white.png')}  />
          </TouchableOpacity>
        </View>
      ),      
    }};

      componentDidMount() {
        console.log('HomeScreen.componentDidMount: ' );

        this.props.navigation.setParams({
          handleAccount: this.onAccount.bind(this),
          });
      }

      onAccount() {
        console.log('HomeScreen.onAccount')
        console.log('onAccount: ' );
        this.props.navigation.navigate('Login')
      
      }


    render() {
   
        return (
          
          <View style={{ flex: 1,  justifyContent: 'center' }}>  
            <View style={{ alignItems: 'center', justifyContent: 'center' }}>
              <Text>Home Screen</Text>
              <Text>Click account icon in toolbar to login/logout</Text>
             </View>
          </View>
        );
      }

}