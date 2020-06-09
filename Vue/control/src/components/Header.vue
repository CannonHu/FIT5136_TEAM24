<template>
  <div id="header-nav" class="navigator">
  	<div class="page-name">
  	  <h1>Employ Fast</h1>
  	  <h2>Mission to Mars</h2>
  	</div>
    <div class="user-info">
	  <div class="nav-user-info-box">
	    <p>Hi, {{user.name}}</p>
        <p>Login as {{user.type}}</p>
	  </div>
	  <div id="logout-btn" v-on:click="Logout">
		  <p>Log Out</p>
	  </div>
    </div>
  </div>
</template>

<script>

export default {
  name: 'Header',
  props: ['user'], 
  methods: {
	  Logout:function() {
		  this.$emit('logout')
	  }
  }
}
</script>

<style lang="scss">
  @import "../../static/css/navigator.scss";

  #header-nav {
    background-color: rgba(15, 15, 15, 0.3);
  }

  #header-nav:after {
    background-image: url(../../static/img/BG.jpg);
  }
  

  
</style>
