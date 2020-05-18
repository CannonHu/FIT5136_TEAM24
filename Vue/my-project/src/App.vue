<template>
  <div id="app">
    <div><Header></Header></div>
    <div><LoginFrame></LoginFrame></div>
  </div>
</template>

<script>

import Header from './components/Header'
import LoginFrame from './components/Login'

export default {
  name: 'App',
  
  components: {
    Header,
    LoginFrame
  }
}
</script>

<style>

body {
	background-color: black;
	text-align: center;
	background-image: url(../static/img/BG.jpg);
	background-repeat: no-repeat;
	background-attachment: fixed;
	background-size: cover;
	position: relative;
	color: white;
}

#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
}
</style>
