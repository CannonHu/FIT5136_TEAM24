<template>
  <div class="home-box container">
    <div class="home-options">
		<p v-on:click="CreateMission">Create a Mission</p>
	</div>
	<div class="home-options">
		<p>Edit a Mission</p>
	</div>
  </div>
</template>

<script>
	export default {
		name: 'CoordinateHome',
		methods: {
			CreateMission:function() {
				this.$router.push({name:'Mission'})
			}
		}
	}
</script>

<style lang="scss" scoped>
	@import "../../static/css/basic.css";
	@import  "../../static/css/home.scss";
	
	
	
</style>
