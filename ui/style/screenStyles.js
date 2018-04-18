'use strict';
var React = require('react-native');
import { StyleSheet } from 'react-native';


//https://stackoverflow.com/questions/32930680/is-it-possible-to-separate-var-styles-stylesheet-create-from-react-component-i
var screenStyle = React.StyleSheet.create({
  mainContainer: {
    flex: 1
  },
  
})

module.exports = screenStyle;