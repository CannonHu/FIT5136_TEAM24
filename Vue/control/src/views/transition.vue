<template>
	<div class="transition-inform-box rough-glass">
		<div class="rb-blacken-layer">
			<div class="container">
				<p>{{msg}}</p>
				<p>Redirect to the <span id="LoginPageHref" v-on:click="GotoLogin">{{pagename}}</span> in {{second}} seconds</p>
			</div>
		</div>
	</div>
</template>

<script>
	export default {
	  name: 'TransitionInform',
	  data() {
		  return {
			  second: 3,
			  target: '',
			  msg: '',
			  pagename: '',
		  }
	  },
	  created() {
		  this.msg = this.$route.params.msg
		  this.target = this.$route.params.topage
		  this.pagename = this.$route.params.pagename
		  
		  this.CountLogin()
	  }, 
	  methods: {
		  CountLogin:function() {
			const maxSecond = 3
			if(!this.timer){
				this.second = 3
				this.show = false
				this.timer=setInterval(()=>{
					if(this.second > 0 && this.second <= maxSecond){
					    this.second--
					}else{
					    this.show = true
					    clearInterval(this.timer)
					    this.timer = null
						this.GotoTarget()
					}
				}, 1000)
			}
		  }, 
		  GotoTarget:function() {
			 this.$router.push({name: this.target})
		  },
	  }
	}
</script>

<style scoped>
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
