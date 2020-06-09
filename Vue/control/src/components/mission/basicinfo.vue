<template>
	<div>
	<div class="mission-info-box">
		<div class="input-line container">
			<div class="input-notice container">
				<span>{{ nameCheck }}</span>
			</div>
			<div class="container input-attr-name">
				<p>Name</p>
			</div>
			<div class="input-box container">
				<input type="text" name="name" placeholder="enter your name" v-model="missionBasic.missionName"/>
			</div>
		</div>
		
		<div class="input-line container"  style = "align-items: flex-start;">
			<div class="input-notice container">
				<span>{{ descCheck }}</span>
			</div>
			<div class="container input-attr-name">
				<p>Description</p>
			</div>
			<textarea type="test" name="address" placeholder="enter the description" v-model="missionBasic.missionDesc"/>
		</div>
		<div class="input-line container" style="padding-top: 0;">
			<div class="input-notice container">
				<span>{{ coordiatorContactCheck }}</span>
			</div>				
			<div class="container input-attr-name">
				<p style="margin-top:-0.2rem">Coordiator Contact</p>
			</div>
			<div class="input-box container">
				<input id="contactInput" type="text" name="postcode" placeholder="enter the coordiator's contact" v-model="missionBasic.contact"/>				
			</div>
		</div>
		<div class="input-line container" style="padding-top: 0.4rem;">
			<div class="input-notice container">
				<span>{{ countryCheck }}</span>
			</div>
			<div class="container input-attr-name">
				<p>Origin Country</p>
			</div>
			<el-select id="NationSelector" v-model="missionBasic.originCountry" placeholder="Country" style="margin-left: 1.5rem;">
				<el-option v-for="item in Info.countries" :key="item" :value="item"></el-option>
			</el-select>
		</div>
		<div class="input-line container">
			<div class="input-notice container">
				<span>{{ launchDateCheck }}</span>
			</div>
			<div class="container input-attr-name">
				<p>Launch Date</p>
			</div>
			<DateSelect :DateType="datetype" :InitDate="missionBasic.launchDate" @setdate="GetLaunchDate" style="margin-left: 1.4rem"></DateSelect>
		</div>
		<div class="input-line container" style="padding-top: 0.4rem;">
			<div class="input-notice container">
				<span>{{ durationCheck }}</span>
			</div>
			<div class="container input-attr-name">
				<p>Duration (months)</p>
			</div>
			<el-select id="DurationSelector" v-model="missionBasic.duration" placeholder="Duration" style="margin-left: 1.5rem;">
				<el-option v-for="item in durations" :key="item" :value="item"></el-option>
			</el-select>
		</div>
		<div class="input-line container" style="margin-top: -0.3rem;">
			<div class="input-notice container">
				<span>{{ typeCheck }}</span>
			</div>
			<div class="container input-attr-name">
				<p>Mission Type</p>
			</div>
			<div class="input-box container">
				<input type="text" name="type" placeholder="enter mission type" v-model="missionBasic.missionType"/>
			</div>
		</div>
	</div>
	<div class="buttons-line">
		<div class="buttons-container">
			<div class="main-button" v-on:click="PrevFunc" >
				<span>HOME</span>
			</div>		
			<div class="main-button" v-on:click="NextFunc">
				<span>NEXT</span>
			</div>
		</div>
	</div>
	</div>
		
</template>

<script>
	import DateSelect from '../dateselect.vue';
	
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
				datetype: 'future',
				
				durations: [],
				
				missionBasic: {
					missionName: '',
					missionDesc: '',
					launchDate: '',
					originCountry: '',
					contact: '',
					duration: '',
					missionType: '',
				},
	
				nameCheck: '',
				descCheck: '',
				launchDateCheck: '',
				countryCheck: '',
				coordiatorContactCheck: '',
				durationCheck: '',
				typeCheck: '',
			}
		}, 
		created() {
			this.InitDurations()
			
			this.missionBasic.missionName = this.MissionObject.missionName
			this.missionBasic.missionDesc = this.MissionObject.missionDescription
			this.missionBasic.missionType = this.MissionObject.missionType 
			this.missionBasic.contact = this.MissionObject.contact
			this.missionBasic.launchDate = this.MissionObject.launchDate
			this.missionBasic.originCountry = this.MissionObject.originCountry 
			this.missionBasic.duration = this.MissionObject.missionDuration
		}, 
		methods: {
			InitDurations:function() {
				var i = 0
				for(i = 1; i <= 200; i++) {
					this.durations.push(i)
				}
			}, 
			GetLaunchDate:function(date) {
				this.missionBasic.launchDate = date
			}, 
			CheckBasicInfoComplete:function() {
				this.nameCheck = this.descCheck = this.coordiatorContactCheck = this.countryCheck = this.launchDateCheck = this.durationCheck = ''
				var notBlank = "cannot be blank"
				var passed = true
				if (this.missionBasic.missionName == '') {
					this.nameCheck = notBlank
					passed = false
				}
				if (this.missionBasic.missionDesc == '') {
					this.descCheck = notBlank
					passed = false
				}
				if (this.missionBasic.contact == '') {
					this.coordiatorContactCheck = notBlank
					passed = false
				}
				if (this.missionBasic.originCountry == '') {
					this.countryCheck = notBlank
					passed = false
				}
				if (this.missionBasic.launchDate == '') {
					this.launchDateCheck = notBlank
					passed = false
				}
				if (this.missionBasic.duration == '') {
					this.durationCheck = notBlank
					passed = false
				}
				if (this.missionBasic.missionType == '') {
					this.typeCheck = notBlank
					passed = false
				}
				return passed
			},
			MissionNameExist:function() {
				this.$axios.post('/MarsEmployFast/mission', {
				  type: 'MissionNameExist',
				  name: this.missionBasic.missionName
				}).then((resp) => {
					if (resp.data.exist) {
						this.nameCheck = 'Mission Name Existed'
					} else {
						this.$emit('setbasic', this.missionBasic)
						this.$router.push({name:'MissionDetail'})
					}
					
				}).catch((error) => {
				  console.log(error)
				})
			},
			PrevFunc:function() {
				this.$router.push({name:''})
			}, 
			NextFunc:function() {
				if (this.CheckBasicInfoComplete()) {
					this.MissionNameExist()
						
				}
			}
		},
		components: {
			DateSelect
		}
	}
</script>

<style lang="scss" scoped>
	@import "../../../static/css/basic.css";
	
	.mission-info-box {
		margin-top: 0.7rem;
		height: 29rem;
	}
	
	.input-line textarea {
		margin-left: 1.5rem;
		height: 4.5rem;
	}
	
	.input-attr-name p {
		font-size: 1.4rem;
	}
	
	#contactInput {
		font-size: 1.25rem;
		width: 21rem;
	}
	
	#DurationSelector {
		width: 9rem;
	}
	
	
</style>
