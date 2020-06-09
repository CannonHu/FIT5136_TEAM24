<template>
  <div id="header-nav" class="navigator">
  	<div class="page-name">
  		<h1>Employ Fast</h1>
  		<h2>Mission to Mars</h2>
  	</div>
  </div>
</template>

<script>

export default {
  name: 'Header',
}
</script>

<style scoped>
  @import "../../static/css/navigator.css";

  #header-nav {
    background-color: rgba(15, 15, 15, 0.3);
  }

  #header-nav:after {
    background-image: url(../../static/img/BG.jpg);
  }
</style>
