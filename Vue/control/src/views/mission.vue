<template>
	<div id="MissionFrame" class="rough-glass">
		<div class="rb-blacken-layer">
			<div class="frame-title container">
				<h1>Create a Mission</h1>
			</div>
			<router-view :Info="InfoObject" :MissionObject="MissionObject" @setbasic="SetBasicInfo" @setdetail="SetDetailInfo" @employreq="SetEmployRequirements" @setjobs="SetJobs"></router-view>
		</div>	
	</div>
</template>

<script>
	export default {
		name: 'mission',
		data() {
			return {
				InfoObject:{
					countries: [],
					statusList: [],
					cargoForList: [],
					computerSkills: []
				},
				MissionObject: {
					missionId: 0,
					missionName: '', 
					missionDescription: '', 
					contact: '',
					launchDate: '',
					originCountry: '',
					missionDuration: '',
					missionType: '',
					computerSkillRequired: '',
					languageRequired: '',
					secondaryLanguages: [], 
					occupations: [], 
					employNumbers: [],
					cargoFor: '',
					cargoWeight: '',
					missionStatus: '',
					qualifications: [],
					employmentRequirements: [],
					workExperienceYears: []},
			}
		},
		created() {
			this.GetInfo()
			this.MissionBasicInfo()
		},
		methods: {
			GetInfo:function() {
				this.$axios.get('/MarsEmployFast/mission', {
				  params: {type: 'GetInfo'}
				}).then((resp) => {
					if (resp.data.state) {
						
						this.InfoObject.countries = resp.data.countries
						this.InfoObject.statusList = resp.data.statusList
						this.InfoObject.cargoForList = resp.data.cargoForList
						this.InfoObject.computerSkills = resp.data.computerSkills
					} else {
						alert("You should Login First")
						window.location.href="../login"
					}
				}).catch((error) => {
				  console.log(error)
				})
			}, 
			MissionBasicInfo:function() {
				this.$router.push({name:'MissionBasic'})
			},
			DateConvert:function(date) {
				var splits = date.split('/')
				if (splits.length == 3) {
					var newDate = splits[2] + '/' + splits[0] + '/' + splits[1]
					return new Date(newDate).getTime()
				}
			},
			SubmitMission:function() {
				this.$axios.post('/MarsEmployFast/mission', {
				  type: 'CreateMission',
				  mission: JSON.stringify(this.MissionObject)
				}).then((resp) => {
					if (resp.data.state) {
						console.log("666")
						this.$router.push({name: 'Transition', params: {msg: 'Create Mission Successfully', topage: 'Coordinator', pagename: 'Coordinator Home'}})
					} else {
						console.log("Create Fail")
						alert("Create Mission Error")
					}
				}).catch((error) => {
				  console.log(error)
				})
			}, 
			SetBasicInfo:function(mission) {
				this.MissionObject.missionName = mission.missionName
				this.MissionObject.missionDescription = mission.missionDesc
				this.MissionObject.missionType = mission.missionType
				this.MissionObject.contact = mission.contact
				this.MissionObject.launchDate = mission.launchDate
				this.MissionObject.originCountry = mission.originCountry
				this.MissionObject.missionDuration = mission.duration
			}, 
			SetDetailInfo:function(mission) {
				this.MissionObject.cargoFor = mission.cargoFor
				this.MissionObject.cargoWeight = mission.cargoWeight
				this.MissionObject.missionStatus = mission.status
				this.MissionObject.languageRequired = mission.language
				this.MissionObject.secondaryLanguages = mission.secondaryLanguages
				this.MissionObject.minAge = mission.minAge
				this.MissionObject.maxAge = mission.maxAge
				this.MissionObject.computerSkillRequired = mission.computerSkill
			},
			SetEmployRequirements:function(mission) {
				this.MissionObject.employmentRequirements = mission.occupations
				this.MissionObject.workExperienceYears = mission.workYears
			},
			SetJobs:function(mission) {
				this.MissionObject.occupations = mission.jobs
				this.MissionObject.employNumbers = mission.employNumbers
				this.MissionObject.qualifications = mission.qualifications
				this.SubmitMission()
			}
		}
	}
</script>

<style lang="scss">
	@import "../../static/css/basic.css";
	
	#MissionFrame {
		width: 50rem;
		height: 41.5rem;
		margin: auto;
		margin-top: 2rem;
		border-radius: 1.5rem;
		border-color: rgba(240, 248, 255, 0.8);
	}
	
	#MissionFrame:after {
		background-image: url(../../static/img/BG.jpg);
	}
	
	.mission-info-box {
		margin-top: 0.7rem;
	}
	
	.frame-title h1 {
		font-size: 2rem;
	}
	
	.buttons-line {
		width : 100%;
	}
	
	.buttons-container {
		display: flex;
		align-content: flex-end;
		margin-top: 0.6rem;
		margin-left: 26rem;
		margin-right: 5rem;
	}
	
	.main-button {
		width: 7.2rem;
		height: 2.4rem;
		font-size: 1.3rem;
		line-height: 2.4rem;
		cursor: pointer;
		
		margin:auto;
	}
	
	
	@media screen and (max-width: 1080px) {
		#MissionFrame:after {
			background-size: 67.5rem;
		}
	}
</style>
