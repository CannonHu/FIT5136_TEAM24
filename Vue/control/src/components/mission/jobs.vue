<template>
	<div>
	<div class="mission-info-box">
		<h2>Jobs Employ</h2>
		<div class="jobs-employ-title">
			<div class="input-notice container">
				<span></span>
			</div>
			<div class="job-name">
				<p>Job</p>
			</div>
			<div class="job-number">
				<p>Employ Number</p>
			</div>
		</div>
		<div id="JobsProvidedBox">
		
			<div class="input-line" style="padding-bottom: 0.8rem;" v-for="(index, item) in jobIndex" :key="item">
				<div class="input-notice container">
					<span>{{checks[index]}}</span>
				</div>
				<div class="job-name">
					<div class="input-box">
						<input type="text" name="name" placeholder="enter a job name" v-model="jobs[index]"/>
					</div>
				</div>
				<div class="job-number">
					<div class="el-select-box">
					<el-select v-model="employNumbers[index]" placeholder="Number" style="margin-left: 1.5rem;">
						<el-option v-for="item in numbers" :key="item" :value="item"></el-option>
					</el-select>
					</div>
				</div>
			</div>
		</div>
		<div class="input-line container" style="margin-top: -0.4rem;">
			<div class="input-notice container">
			</div>				
			<div class="container input-attr-name">
				<p>Qualifications Required</p>
			</div>
			<div class="input-box container">
				<input type="text" name="postcode" placeholder="enter qualifications" v-model="qualifications"/>				
			</div>
		</div>
	</div>
	
	<div class="buttons-line">
		<div class="buttons-container">
			<div class="main-button" v-on:click="PrevFunc" >
				<span>PREV</span>
			</div>		
			<div class="main-button" v-on:click="NextFunc">
				<span>NEXT</span>
			</div>
		</div>
	</div>
	</div>
</template>

<script>
	export default {
		props: {
			MissionObject: {
				type: Object
			}
		},
		data() {
			return {
				jobNum: 5,
				jobIndex: [],
							
				numbers: [],
				
				jobs: [],
				employNumbers: [],
				checks: [],
				qualifications: '',
				
				finalJobs: [],
				finalEmployNumbers: []
			}
		}, 
		created() {
			this.InitJobsNumber()
			this.InitNumbers()
			
			var i
			var len = this.MissionObject.qualifications.length
			for(i = 0; i < len; i++) {
				this.qualifications += this.MissionObject.qualifications[i]
				if (i != len - 1) {
					this.qualifications += ','
				}
			}
			
			len = this.MissionObject.occupations.length
			for (i = 0; i < len; i++) {
				this.jobs[i] = this.MissionObject.occupations[i]
			}
			len = this.MissionObject.employNumbers.length
			for (i = 0; i < len; i++) {
				this.employNumbers[i] = this.MissionObject.employNumbers[i]
			}
		},
		methods: {
			InitJobsNumber:function() {
				var i = 0
				for (i = 0; i < this.jobNum; i++) {
					this.jobIndex.push(i)
					this.checks.push('')
					this.jobs.push('')
					this.employNumbers.push('')
				}
			},
			InitNumbers:function() {
				var i
				for (i = 1; i <= 400; i++) {
					this.numbers.push(i)
				}
			},
			CheckJobsComplete:function() {
				var passed = true
				var i
				for(i = 0; i < this.jobNum; i++) {
					this.checks[i] = ''
					if (this.jobs[i] == '') {
						if (this.employNumbers[i] != '') {
							this.checks[i] = "input an occupation"
							this.checks.push('')
							this.checks.pop()
							passed = false
						}
					} else if (this.employNumbers[i] == '') {
						this.checks[i] = "select working year"
						this.checks.push('')
						this.checks.pop()
						passed = false
					} else {
						this.finalJobs.push(this.jobs[i].trim())
						this.finalEmployNumbers.push(this.employNumbers[i])
					}
				}
		
				return passed
			}, 
			PrevFunc:function() {
				this.$router.push({name:'MissionEmployRequirements'})
			}, 
			NextFunc:function() {
				if (this.CheckJobsComplete()) {
					var qarr = this.qualifications.split(',')
					var i
					for(i = 0; i < qarr.length; i++) {
						qarr[i] = qarr[i].trim()
					}
					var obj = {
						jobs: this.finalJobs, 
						employNumbers: this.finalEmployNumbers,
						qualifications: qarr}
					this.$emit('setjobs', obj)
				}
			}
		},
	}
</script>

<style lang="scss">
	@import "../../../static/css/basic.css";
	
	.mission-info-box {
		margin-top: 0.7rem;
		height: 29rem;
	
	}
	
	h2 {
		font-size: 1.38rem;
		color: aliceblue;
		margin: auto;
		margin-top: 0.8rem;
		margin-bottom: 0.3rem;
	}
	
	#JobsProvidedBox {
		margin-top:1rem;
		height: 17rem;
		width: 42rem;
	}
	
	.jobs-employ-title {
		color: white;
		font-size: 1.3rem;
		margin-top: 0.4rem;
		margin-bottom: 0.4rem;
		display:flex;
		align-items: center;
		
		p {
			margin: 0;
			margin-left:1.5rem
		}
	}
	.job-name {
		width: 15rem;
		
		.input-box {
			width: 14rem;
			
			input {
				width: 14rem;
				font-size: 1.25rem;
			}
		}
	}
	.job-number {
		margin-left: 1rem;
		width:12rem;
		
		
		.el-select-box {
			margin-left:1rem;
			width: 10rem;
		}
	}
	
	.input-attr-name p {
		font-size: 1.3rem;
	}
</style>
