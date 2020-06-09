<template>
	<div id="app">	
		<Header :user="userInfo" @logout="UserLogOut"></Header>
		<router-view></router-view>
	</div>
</template>

<script>

import Header from './components/Header'

export default {
  name: 'App',
  data() {
	  return {
		  userInfo: {type: '', name: ''},
	  }
  },
  components: {
    Header,
  }, 
  created() {
	  this.GetUserInfo()
  }, 
  methods: {
	  GetUserInfo:function() {
		this.$axios.get('/MarsEmployFast/home', {
			params: {type: 'UserInfo'}
		}).then((resp) => {
			if (resp.data.state) {
				this.userInfo.type = resp.data.usertype
				this.userInfo.name = resp.data.username
				
				if (this.userInfo.type == "coordinator") {
					this.$router.push({name:"Coordinator"})
				}
			} else {
				alert("You should Login First")
				window.location.href="/"
			}
		}).catch((error) => {
			console.log(error)
		})
	  },
	  UserLogOut:function() {
		  this.$axios.post('/MarsEmployFast/home', {
			  type: 'Logout'
		  }).then((resp) => {
			if (resp.data.state) {
				console.log("logout")	
			}
			window.location.href="../login"
		}).catch((error) => {
			console.log(error)
		})
	  }
  }
}
</script>

<style lang="scss">
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

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


@media screen and (max-width: 1080px) {
	body {
		background-size: 67.5rem;
	}
}
</style>
