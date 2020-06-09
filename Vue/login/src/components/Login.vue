
<template>
  <div id="LoginFrame" class="rough-glass">
  	<div class="rb-blacken-layer">
  		<div class="frame-title container">
  			<h1>Log In</h1>
  		</div>
  		<div class="login-info-box container">
  			<div class="input-line container">
  				<div class="input-notice container">
  					<span>{{ usernameCheck }}</span>
  				</div>
  				<div class="container input-attr-name">
  					<p>Username</p>
  				</div>
  				<div class="input-box container">
  					<input type="text" name="username" placeholder="enter your username" v-model="usernameValue"/>
  				</div>
  			</div>
  			<div class="input-line container">
  				<div class="input-notice container">
  					<span>{{ passwordCheck }}</span>
  				</div>
  				<div class="container input-attr-name">
  					<p>Password</p>
  				</div>
  				<div class="input-box container">
  					<input type="password" name="passwd" placeholder="enter your password" v-model="passwdValue"/>
  				</div>
  			</div>
			<div class="button-line">
				<div class="main-button" v-on:click="LoginCheck">
					<span>SIGN IN</span>
				</div>
				<div id="signup-box" class="container" v-on:click="ToRegister">
					<span>I want to REGISTER as a CANDIDATE</span>
				</div>
			</div>
  		</div>
  	</div>
  </div>
</template>

<script>
	import { hex_sha256 } from '../js/sha256.js'
  export default {
    name: 'LoginFrame',
    data() {
      return {
        usernameCheck: '',
        passwordCheck: '',
        usernameValue: '',
        passwdValue: '',
        dynamicSaltValue: '',
        staticSaltValue: '',
      }

    },
    methods: {
    	LoginVerify:function() {
			this.$axios.post('/MarsEmployFast/signin', {
			  type: 'salt'
			}).then((resp) => {
				this.dynamicSaltValue = resp.data.dynamicSalt
				this.staticSaltValue = resp.data.staticSalt
				
				var hashed = hex_sha256(hex_sha256(this.passwdValue + this.staticSaltValue) + this.dynamicSaltValue)
				this.$axios.post('/MarsEmployFast/signin', {
					type: 'verify',
					username: this.usernameValue,
					passwd: hashed
				}).then((resp) => {
					console.log("loginVerify", resp.data)	
					if (resp.data.state == 'Login') {
						window.location.href="../control"
					}
				}).catch((error) => {
					console.log(error)
				})
				}).catch((error) => {
			  console.log(error)
			})
    	},
    	LoginCheck:function() {
    		this.usernameCheck = this.passwordCheck = ''
    		if (this.usernameValue == '') {
    			this.usernameCheck = 'cannot be blank'
    		}
    		else if (this.passwdValue == '') {
    			this.passwordCheck = 'cannot be blank'
    		}
			else {
				this.LoginVerify();
			}
    	}, 
		ToRegister:function() {
			this.$router.push('./register')
		}
    }
  }
</script>

<style scoped>
  @import "../../static/css/login.css";
  @import "../../static/css/basic.css";

  #LoginFrame {
    border-color: rgba(240, 248, 255, 0.8);
  }

  #LoginFrame:after {
      background-image: url(../../static/img/BG.jpg);
  }

  .input-notice {
    color:rgb(183, 54, 10);
  }
  

  .input-line .input-box {
    border-bottom: solid 0.1rem #F0F8FF;
  }

  .input-line input {
    background-color: rgba(0, 0, 0, 0);
    color: aliceblue
  }
  
  .input-attr-name p {
	  font-size: 1.7rem !important;
  }

  #signup-box {
  	color:aliceblue;
  	font-size: 1.2rem;
  	letter-spacing: 0.05rem;
  	margin-top: 0.7rem;
  	padding-left: 0.1rem;
  	padding-right: 0.1rem;
  	cursor: pointer;
  }

  #signup-box span {
  	border-bottom: aliceblue 1px solid;
  }

  #signup-box :hover {
  	color:#666666;
  	border-bottom: #666666 1px solid;
  }
  
  .button-line {
	  margin-top: 2rem;
  }


</style>
