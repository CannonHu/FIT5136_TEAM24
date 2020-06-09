<template>
	<div>
	<div class="mission-info-box">
		<div class="input-line container" style="padding-top: 0.4rem;">
			<div class="input-notice container">
				<span>{{ cargoForCheck }}</span>
			</div>
			<div class="container input-attr-name">
				<p>Cargo For</p>
			</div>
			<el-select id="CargoSelector" v-model="missionDetail.cargoFor" placeholder="Cargo For" style="margin-left: 1.5rem;">
				<el-option v-for="item in Info.cargoForList" :key="item" :value="item"></el-option>
			</el-select>
		</div>
		<div class="input-line container" style="padding-top: 0;">
			<div class="input-notice container">
				<span>{{ cargoWeightCheck }}</span>
			</div>				
			<div class="container input-attr-name">
				<p>Cargo Weight (kg)</p>
			</div>
			<div class="input-box container">
				<input type="text" name="postcode" placeholder="enter the cargo weight" v-model="missionDetail.cargoWeight"/>				
			</div>
		</div>
		
		<div class="input-line container" style="padding-top: 0.7rem;">
			<div class="input-notice container">
				<span>{{ statusCheck }}</span>
			</div>
			<div class="container input-attr-name">
				<p>Status</p>
			</div>
			<el-select id="StatusSelector" v-model="missionStatus" placeholder="Mission Status" style="margin-left: 1.5rem;">
				<el-option v-for="item in Info.statusList" :key="item" :value="item"></el-option>
			</el-select>
		</div>
		<div class="input-line container" style="margin-top: -0.4rem;">
			<div class="input-notice container">
				<span>{{ languageCheck }}</span>
			</div>				
			<div class="container input-attr-name">
				<p>Language Required</p>
			</div>
			<div class="input-box container">
				<input type="text" name="postcode" placeholder="enter language required" v-model="missionDetail.language"/>				
			</div>
		</div>
		<div class="input-line container" style="margin-top: -0.5rem;">
			<div class="input-notice container">
			</div>				
			<div class="container input-attr-name">
				<p>Secondary Languages</p>
			</div>
			<div class="input-box container">
				<input style="width: 20rem" type="text" name="postcode" placeholder="enter secondary languages" v-model="secondaryLanguages"/>						
			</div>
		</div>
		<div id="age-range" class="input-line container" style="margin-top: -0.3rem;">
			<div class="input-notice container">
				<span>{{ ageCheck }}</span>
			</div>				
			<div class="container input-attr-name">
				<p>Age Required</p>
			</div>
			<div style="width:10rem">
				<el-select id="MinAgeSelector" v-model="missionDetail.minAge" placeholder="Min Age" style="margin-left: 1.5rem;">
					<el-option v-for="item in ageList" :key="item" :value="item"></el-option>
				</el-select>
			</div>
			<div style="width:10rem">
				<el-select id="MaxAgeSelector" v-model="missionDetail.maxAge" placeholder="Max Age" style="margin-left: 1.5rem;">
					<el-option v-for="item in ageList" :key="item" :value="item"></el-option>
				</el-select>
			</div>
		</div>
		<div class="input-line container" style="padding-top: 0.4rem;">
			<div class="input-notice container">
				<span>{{ computerSkillCheck }}</span>
			</div>
			<div class="container input-attr-name">
				<p>Computer Skill Required</p>
			</div>
			<el-select id="ComputerSkillSelector" v-model="missionDetail.computerSkill" placeholder="Computer Skill" style="margin-left: 1.5rem;">
				<el-option v-for="item in Info.computerSkills" :key="item" :value="item"></el-option>
			</el-select>
		</div>
	</div>
	<div class="buttons-line">
		<div class="buttons-container">
			<div class="main-button" v-on:click="PrevFunc">
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
			Info: {
				type: Object,
				required: true
			},
			MissionObject: {
				type: Object
			}
		},
		data() {
			return {
				ageList: [],
				
				missionDetail: {
					cargoFor: '',
					cargoWeight: '',
					status: '',
					language: '',
					secondaryLanguages: [],
					minAge: '',
					maxAge: '',
					computerSkill: ''
				},
				secondaryLanguages: '',
				missionStatus: '',
				
				cargoForCheck: '',
				statusCheck: '',
				languageCheck: '',
				ageCheck: ''
			}
		}, 
		created() {
			this.InitAgeRange()
			
			this.missionDetail.cargoFor = this.MissionObject.cargoFor
			this.missionDetail.cargoWeight = this.MissionObject.cargoWeight
			this.missionStatus = this.Info.statusList[this.MissionObject.missionStatus]
			this.missionDetail.language = this.MissionObject.languageRequired 
			this.missionDetail.minAge = this.MissionObject.minAge
			this.missionDetail.maxAge = this.MissionObject.maxAge
			this.missionDetail.computerSkill = this.MissionObject.computerSkillRequired
			
			var i
			var len = this.MissionObject.secondaryLanguages.length
			for(i = 0; i < len; i++) {
				this.secondaryLanguages += this.MissionObject.secondaryLanguages[i]
				if (i != len - 1) {
					this.secondaryLanguages += ','
				}
			}
		},
		methods: {
			InitAgeRange:function() {
				var age = 18
				for (age = 18; age <= 80; age++) {
					this.ageList.push(age)
				}
			}, 
			CheckDetailComplete:function() {
				this.cargoForCheck = this.cargoWeightCheck = this.occupationsCheck = this.statusCheck = this.languageCheck = this.ageCheck = ''
				var notBlank = "cannot be blank"
				var passed = true
				if (this.missionDetail.cargoFor == '') {
					this.cargoForCheck = notBlank
					passed = false
				}
				if (this.missionDetail.cargoWeight == '') {
					this.cargoWeightCheck = notBlank
					passed = false
				}
				if (this.missionStatus == '') {
					this.statusCheck = notBlank
					passed = false
				}
				if (this.missionDetail.language == '') {
					this.languageCheck = notBlank
					passed = false
				}
				if (this.missionDetail.minAge == '') {
					this.ageCheck = "Min Age cannot blank"
					passed = false
				}
				if (this.missionDetail.maxAge == '') {
					this.ageCheck = "Max Age cannot blank"
					passed = false
				}
				return passed
			},
			PrevFunc:function() {
				this.$router.push({name:'MissionBasic'})
			}, 
			NextFunc:function() {
				if (this.CheckDetailComplete()) {
					var i
					if (this.secondaryLanguages != '') {
						var array = [];
						array = this.secondaryLanguages.split(',')
						
						for(i = 0; i < array.length; i++) {
							this.missionDetail.secondaryLanguages.push(array[i].trim())
						}
					}
					
					for (i = 0; i < this.Info.statusList.length; i++) {
						if (this.Status == this.Info.statusList[i]) {
							this.missionDetail.status = i
						}
					}
					this.$emit('setdetail', this.missionDetail)
					this.$router.push({name:'MissionEmployRequirements'})
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
	
	.input-attr-name p {
		font-size: 1.4rem;
	}

	
	#OccupationsInput {
		width: 20rem;
	}
	

	
</style>
