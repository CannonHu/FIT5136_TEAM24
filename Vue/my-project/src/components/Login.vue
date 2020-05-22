
<template>
  <div id="LoginFrame">
  	<div class="rb-blacken-layer">
  		<div class="frame-title container">
  			<h1>Log In</h1>
  		</div>
  		<div class="login-info-box container">
  			<div class="login-input-line container">
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
  			<div class="login-input-line container">
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
  			<div class="main-bottom" v-on:click="LoginCheck">
  				<span>SIGN IN</span>
  			</div>
  			<div id="signup-box" class="container">
  				<span>I want to REGISTER as a CANDIDATE</span>
  			</div>
  		</div>
  	</div>
  </div>
</template>

<script>
	import { hex_sha256 } from '../js/sha256.js'
  export default {
    name: 'LoginFrame',
    data: function() {
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
				console.log("passwdHashed", hex_sha256(this.passwdValue))
				var hashed = hex_sha256(hex_sha256(this.passwdValue + this.staticSaltValue) + this.dynamicSaltValue)
				console.log("hashedResult", hashed)
				this.$axios.post('/MarsEmployFast/signin', {
					type: 'verify',
					username: this.usernameValue,
					passwd: hashed
				}).then((resp) => {
							
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
    	}
    }
  }
</script>

<style>
  @import "../../static/css/login.css";
  @import "../../static/css/basic.css";

  #LoginFrame {
    border-color: rgba(240, 248, 255, 0.8);
  }

  #LoginFrame:after {
      background-image: url(../../static/img/BG.jpg);
  }

  .frame-title {
    border-bottom: solid 0.15rem rgba(210, 220, 220, 0.5);
  }

  .input-notice {
    color:rgb(183, 54, 10);
  }

  .login-input-line .input-box {
    border-bottom: solid 0.1rem #F0F8FF;
  }

  .login-input-line input {
    background-color: rgba(0, 0, 0, 0);
    color: aliceblue
  }

  .main-bottom {
    background-color: #000000;
    border-color: aliceblue;
    border-style: solid;
    border-width: 1px;
  }

  .main-bottom:hover {
  	background-color: white;
  	color: rgb(0, 27, 48);
  	border-color: rgb(0, 27, 48);
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


</style>
