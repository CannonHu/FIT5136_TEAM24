<template>
	<div class="transition-inform-box rough-glass">
		<div class="rb-blacken-layer">
			<div class="container">
				<p>Sign Up Successful!</p>
				<p>Redirect to the <span id="LoginPageHref" v-on:click="GotoLogin">Login Page</span> in {{second}} seconds</p>
			</div>
		</div>
	</div>
</template>

<script>
	export default {
	  name: 'TransitionInform',
	  data() {
		  return {
			  second: '4',
		  }
	  },
	  created() {
		  this.CountLogin()
	  }, 
	  methods: {
		  CountLogin:function() {
			  const maxSecond = 4
			if(!this.timer){
				this.second = 4
				this.show = false
				this.timer=setInterval(()=>{
					if(this.second > 0 && this.second <= maxSecond){
					    this.second--
					}else{
					    this.show = true
					    clearInterval(this.timer)
					    this.timer = null
						this.GotoLogin()
					}
				}, 1000)
			}
		  }, 
		  GotoLogin:function() {
			 this.$router.push({name:'Login'})
		  },
	  }
	}
</script>

<style>
	@import "../../static/css/basic.css";
	
	.transition-inform-box {
		margin: auto;
		margin-top: 6rem;
		width: 34rem;
		height: 7rem;
		border-color: rgba(240, 248, 255, 0.8);
		border-radius: 1.5rem;
	}
	
	.transition-inform-box:after {
	    background-image: url(../../static/img/BG.jpg);
	}
	
	.transition-inform-box .rb-blacken-layer {
		border-radius: 1.5rem;
	}
	
	.container p {
		font-size: 1.2rem;
		margin-bottom: 0rem;
		margin-top: 1rem;
	}
	
	#LoginPageHref {
		font-weight: bold;
		text-decoration: underline;
		margin-left: 0.2rem;
		margin-right: 0.2rem;
		cursor: pointer;
	}
	
	@media screen and (max-width: 1080px) {
		.transition-inform-box:after {
			background-size: 67.5rem;
		}
	}
	
</style>
