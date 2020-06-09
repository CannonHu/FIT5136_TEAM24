<template>
	<div>
	<div class="mission-info-box">
		<h2>Employ Requirements</h2>
		<div class="employ-requirement-title">
			<div class="input-notice container">
				<span></span>
			</div>
			<div class="employ-occupation">
				<p>Occupation</p>
			</div>
			<div class="employ-years">
				<p>Work Experience Years</p>
			</div>
		</div>
		<div id="EmployRequirementsBox">
		
			<div class="input-line" style="padding-bottom: 0.8rem;" v-for="(item, index) in reqIndex" :key="item">
				<div class="input-notice container">
					<span>{{checks[index]}}</span>
				</div>
				<div class="employ-occupation">
					<div class="input-box">
						<input type="text" name="name" placeholder="enter an occupation" v-model="occupations[index]"/>
					</div>
				</div>
				<div class="employ-years">
					<el-select v-model="workExperienceYears[index]" placeholder="Years" style="margin-left: 1.5rem;">
						<el-option v-for="item in workYears" :key="item" :value="item"></el-option>
					</el-select>
				</div>
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
				reqNum: 5,
				reqIndex: [],
				
				workYears: [],
				
				occupations: [],
				checks: [],
				workExperienceYears: [],
				
				finalOccupations: [],
				finalWorkExperienceYears: []
			}
		}, 
		created() {
			this.InitRequirements()
			this.InitWorkYears()
			
			var i
			var len = this.MissionObject.employmentRequirements.length
			for (i = 0; i < len; i++) {
				this.occupations[i] = this.MissionObject.employmentRequirements[i]
			}
			
			len = this.MissionObject.workExperienceYears.length
			for (i = 0; i < len; i++) {
				this.workExperienceYears[i] = this.MissionObject.workExperienceYears [i]
			}
		},
		methods: {
			InitRequirements:function() {
				var i = 0
				for (i = 0; i < this.reqNum; i++) {
					this.reqIndex.push(i)
					this.checks.push('')
					this.workExperienceYears.push('')
					this.occupations.push('')
				}
			},
			InitWorkYears:function() {
				var i
				for (i = 1; i <= 40; i++) {
					this.workYears.push(i)
				}
			},
			CheckEmployReqComplete:function() {
				var passed = true
				var i
				for(i = 0; i < this.reqNum; i++) {
					this.checks[i] = ''
					if (this.occupations[i] == '') {
						if (this.workExperienceYears[i] != '') {
							this.checks[i] = "input an occupation"
							this.checks.push('')
							this.checks.pop()
							passed = false
						}
					} else if (this.workExperienceYears[i] == '') {
						this.checks[i] = "select working year"
						this.checks.push('')
						this.checks.pop()
						passed = false
					} else {
						this.finalOccupations.push(this.occupations[i].trim())
						this.finalWorkExperienceYears.push(this.workExperienceYears[i])
					}
				}
		
				return passed
			}, 
			PrevFunc:function() {
				this.$router.push({name:'MissionDetail'})
			}, 
			NextFunc:function() {
				if (this.CheckEmployReqComplete()) {
					var obj = {
						occupations: this.finalOccupations, 
						workYears: this.finalWorkExperienceYears}
					this.$emit('employreq', obj)
					this.$router.push({name:'MissionJobs'})
				}
			}
		},
	}
</script>

<style lang="scss" scoped>
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
	
	#EmployRequirementsBox {
		margin-top:1rem;
		height: 17rem;
		width: 42rem;
	}
	
	.employ-requirement-title {
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
	.employ-occupation {
		width: 15rem;
		
		.input-box {
			width: 14rem;
			
			input {
				width: 14rem;
				font-size: 1.25rem;
			}
		}
	}
	.employ-years {
		margin-left: 1rem;
		width:15rem;
	}
	
	
</style>
