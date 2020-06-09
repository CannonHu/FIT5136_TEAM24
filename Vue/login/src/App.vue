<template>
  <div id="app">
    <Header></Header>
    <router-view></router-view>
  </div>
</template>

<script>

import Header from './components/Header'

export default {
  name: 'App',
  
  components: {
    Header,
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

@media screen and (max-width: 1080px) {
	body {
		background-size: 67.5rem;
	}
}
</style>
