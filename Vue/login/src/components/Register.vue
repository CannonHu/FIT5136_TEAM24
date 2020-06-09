<template>
	<div id="RegisterFrame" class="rough-glass">
		<div class="rb-blacken-layer">
			<div class="frame-title container">
				<h1>Candidate Register</h1>
			</div>
			<div class="register-info-box">
				<div id="register-basic-info" v-show="step == 1">
					<div class="input-line container">
						<div class="input-notice container">
							<span>{{ nameCheck }}</span>
						</div>
						<div class="container input-attr-name">
							<p>Name</p>
						</div>
						<div class="input-box container">
							<input type="text" name="name" placeholder="enter your name" v-model="name"/>
						</div>
					</div>
					<div class="input-line container">
						<div class="input-notice container">
							<span>{{ genderCheck }}</span>
						</div>
						<div class="container input-attr-name">
							<p>Gender</p>
						</div>
						<div class="radio-box-container">
							<div class="radio-box">
								<el-radio label="male" v-model="gender">Male</el-radio>			
							</div>
							<div class="radio-box">
								<el-radio label="female" v-model="gender">Female</el-radio>
							</div>		
						</div>
					</div>
					<div class="input-line container">
						<div class="input-notice container">
							<span>{{ dobCheck }}</span>
						</div>
						<div class="container input-attr-name">
							<p>Date of Birth</p>
						</div>
						<DateSelect @setdate="GetDoB"></DateSelect>
					</div>
					<div class="input-line container">
						<div class="input-notice container">
							<span>{{ nationCheck }}</span>
						</div>
						<div class="container input-attr-name">
							<p>Nation</p>
						</div>
						<el-select id="NationSelector" v-model="nation" placeholder="Country" style="margin-left: 0.6rem;">
							<el-option v-for="item in countries" :key="item" :value="item"></el-option>
						</el-select>
				
					</div>
					<div class="input-line container"  style = "align-items: flex-start;">
						<div class="input-notice container">
							<span>{{ addressCheck }}</span>
						</div>
						<div class="container input-attr-name">
							<p>Address</p>
						</div>
						<textarea type="test" name="address" placeholder="enter your address" v-model="address"/>
					</div>
					<div class="input-line container" style="padding-top: 0;">
						<div class="input-notice container">
							<span>{{ postcodeCheck }}</span>
						</div>
						<div class="container input-attr-name">
							<p>Postcode</p>
						</div>
						<div class="input-box container">
							<input type="text" name="postcode" placeholder="enter your postcode" v-model="postcode"/>
						</div>
					</div>
					<div class="input-line container" style="padding-top: 0.4rem;">
						<div class="input-notice container">
							<span>{{ identificationCheck }}</span>
						</div>
						<div class="container input-attr-name">
							<p>Identification</p>
						</div>
						<div class="input-box container">
							<input type="text" name="identification" placeholder="enter your identification" v-model="identification"/>
						</div>
					</div>
				</div>
				
				<div id="register-extend-info" v-show="step == 2">
					<div class="input-line container">
						<div class="input-notice container">
							<span>{{ allergiesCheck }}</span>
						</div>
						<div class="container input-attr-name">
							<p>Allergies</p>
						</div>
						<el-select id="AllergiesSelector" v-model="allergies" placeholder="Allergies" style="margin-left: 0.6rem;">
							<el-option v-for="item in allergiesList" :key="item" :value="item"></el-option>
						</el-select>
					</div>
					<div class="input-line container">
						<div class="input-notice container">
							<span>{{ foodPrefCheck }}</span>
						</div>
						<div class="container input-attr-name">
							<p style="font-size: 1.3rem;">Food Preference</p>
						</div>
						<el-select id="FoodPrefSelector" v-model="foodPreference" placeholder="FoodPreference" style="margin-left: 0.6rem;">
							<el-option v-for="item in foodPrefs" :key="item" :value="item"></el-option>
						</el-select>
					</div>
					<div class="input-line container" style="padding-top: 0.2rem;">
						<div class="input-notice container">
							<span>{{ qualificationCheck }}</span>
						</div>
						<div class="container input-attr-name">
							<p>Qualification</p>
						</div>
						<div class="input-box container">
							<input type="text" name="qualification" placeholder="enter your qualification" v-model="qualifications"/>
						</div>
					</div>
					<div class="input-line container">
						<div class="input-notice container">
							<span>{{ computerSkillCheck }}</span>
						</div>
						<div class="container input-attr-name">
							<p style="font-size: 1.3rem;">Computer Skills</p>
						</div>
						<el-select id="ComputerSkillSelector" v-model="computerSkill" placeholder="Computer Skills" style="margin-left: 0.6rem;">
							<el-option v-for="item in computerSkills" :key="item" :value="item"></el-option>
						</el-select>
					</div>
					<div class="input-line container" style="padding-top: 0.3rem;">
						<div class="input-notice container">
							<span>{{ occupationCheck }}</span>
						</div>
						<div class="container input-attr-name">
							<p>Occupations</p>
						</div>
						<div class="input-box container">
							<input type="text" name="occupations" placeholder="enter your occupations" v-model="occupations" style="width: 22rem; font-size: 1.3rem;"/>
						</div>
					</div>
					<div class="input-line container" style="padding-top: 0.3rem;">
						<div class="input-notice container">
							<span>{{ workyearCheck }}</span>
						</div>
						<div class="container input-attr-name">
							<p style="font-size: 1.25rem">Work Experience Years</p>
						</div>
						<div class="input-box container">
							<input type="text" name="workYears" placeholder="enter your work experience years" v-model="workYears" style="width: 22rem; font-size: 1.3rem;"/>
						</div>
					</div>
					<div class="input-line container" style="padding-top: 0.2rem;">
						<div class="input-notice container">
							<span>{{ languagesCheck }}</span>
						</div>
						<div class="container input-attr-name">
							<p style="font-size: 1.3rem;">Languages Known</p>
						</div>
						<div class="input-box container">
							<input type="text" name="languages" placeholder="enter your known languages" v-model="languages" style="width: 22rem; font-size: 1.3rem;"/>
						</div>
					</div>
				</div>
				
				<div id="register-extend-info" v-show="step == 3">
					<div class="input-line container">
						<div class="input-notice container">
							<span>{{ usernameCheck }}</span>
						</div>
						<div class="container input-attr-name">
							<p>Username</p>
						</div>
						<div class="input-box container">
							<input type="text" name="username" placeholder="enter your username" v-model="username"/>
						</div>
					</div>
					<div class="input-line container">
						<div class="input-notice container">
							<span>{{ passwdCheck }}</span>
						</div>
						<div class="container input-attr-name">
							<p>Password</p>
						</div>
						<div class="input-box container">
							<input type="password" name="passwd" placeholder="enter your password" v-model="passwd"/>
						</div>
					</div>
					<div class="input-line container">
						<div class="input-notice container">
							<span>{{ passwdConfirmCheck }}</span>
						</div>
						<div class="container input-attr-name">
							<p style="font-size: 1.35rem;">Confirm Password</p>
						</div>
						<div class="input-box container">
							<input type="password" name="passwdConfirm" placeholder="confirm your password" v-model="passwdConfirm"/>
						</div>
					</div>
				</div>
				
			</div>
				
			<div class="buttons-line">
				<div class="buttons-container">
					<div class="main-button" v-on:click="PrevRegister" >
						<span>{{ prevButton }}</span>
					</div>		
					<div class="main-button" v-on:click="NextRegister">
						<span>{{ nextButton }}</span>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
import DateSelect from './dateselect.vue'
import { hex_sha256 } from '../js/sha256.js'

export default {
	data() {
		return {
			step: 1,
			
			gender: '',
			name: '',
			nation: '',
			dob: '',
			address: '',
			postcode: '',
			identification: '',
			
			countries: [],
			
			nameCheck: '',
			genderCheck: '',
			dobCheck: '',
			nationCheck: '',
			addressCheck: '',
			postcodeCheck: '',
			identificationCheck: '',
			
			allergiesList: [],
			foodPrefs: [], 
			computerSkills: [],
			
			allergies: '',
			foodPreference: '',
			qualifications: '',
			computerSkill: '', 
			occupations: '',
			workYears: '',
			languages: '',
			
			allergiesCheck: '',
			foodPrefCheck: '',
			qualificationCheck: '',
			computerSkillCheck: '',
			occupationCheck: '',
			workyearCheck: '',
			languagesCheck: '',
			
			username: '',
			passwd: '',
			passwdConfirm: '',
			hashed: '',
			
			usernameCheck: '',
			passwdCheck: '',
			passwdConfirmCheck: '',
			
			prevButton: 'LOGIN',
			nextButton: 'NEXT',
		}
	}, 
	components: {
		DateSelect 
	},
	mounted() {
		this.GetBasicRegInfo()
	}, 
	watch: {
		'step':function(nVal, oVal) {
			if (nVal != 1) {
				this.prevButton = 'PREV'
			} else {
				this.prevButton = 'LOGIN'
			}
			if (nVal == 3) {
				this.nextButton = 'SIGNUP'
			}
		}
	}, 
	methods: {
		GetBasicRegInfo:function() {
			this.$axios.get('/MarsEmployFast/signup', {
			  params: {type: 'GetInfo'}
			}).then((resp) => {
				this.countries = resp.data.countries
				this.allergiesList = resp.data.allergies
				this.foodPrefs = resp.data.foodPreferences
				this.computerSkills = resp.data.computerSkills
			}).catch((error) => {
			  console.log(error)
			})
		}, 
		NextRegister:function() {
			if (this.step == 1) {
				this.CheckBasicInfo()
			} else if (this.step == 2) {
				this.CheckExtendInfo()
			} else if (this.step == 3){
				this.CheckUserInfo()
			}
		},
		PrevRegister:function() {
			if (this.step == 1) {
				this.$router.push({name:'Login'})
			} else {
				this.step--
			}
		},

		CheckBasicInfo:function() {
			this.nameCheck = this.genderCheck = this.nationCheck = this.dobCheck = this.addressCheck = this.postcodeCheck = this.identificationCheck = ''
			
			var notBlank = 'cannot be blank'
			var passed = true;
			if (this.name == '') {
				this.nameCheck = notBlank
				passed = false
			}
			if (this.gender == '') {
				this.genderCheck = notBlank
				passed = false
			}
			if (this.nation == '') {
				this.nationCheck = notBlank
				passed = false
			}
			if (this.dob == '') {
				this.dobCheck = notBlank
				passed = false
			}
			if (this.address == '') {
				this.addressCheck = notBlank
				passed = false
			}
			if (this.postcode == '') {
				this.postcodeCheck = notBlank
				passed = false
			}
			if (this.identification == '') {
				this.identificationCheck = notBlank
				passed = false
			}
			
			if (passed) {
				return this.UserExistCheck()
			} else {
				return false
			}
		}, 
		UserExistCheck:function() {
			this.$axios.post('/MarsEmployFast/signup', {
			  type: 'DuplicateCheck',
			  name: this.name,
			  gender: this.gender,
			  dob: this.dob,
			  country: this.nation,
			  postal: this.postcode,
			}).then((resp) => {
				console.log(resp.data)
				if (resp.data.exist) {
					this.nameCheck = 'User Existed'
				} else {
					this.step++
				}
			}).catch((error) => {
			  console.log(error)
			})
		}, 
		ExperiencesYears:function() {
			var invalid = false
			var years = this.workYears.split(',')
			var jobs = this.occupations.split(',')
			var i = 0
			
			if (years.length != jobs.length) {
				this.occupationCheck = 'years and jobs are not equivalent'
				return false
			}
			var yearsStr = ''
			var jobsStr = ''
			for(i = 0; i < years.length; i++) {
				years[i] = years[i].trim()
				
				if (!(/^\d+$/.test(years[i]))) {
					this.workyearCheck = 'invalid character detected'
					return false
				}
				
				yearsStr += years[i]
				jobsStr += jobs[i].trim()
				if (i != years.length - 1) {
					yearsStr += ','
					jobsStr += ','
				}
			}
			this.workYears = yearsStr
			this.occupation = jobsStr
			return true
		},
		CheckExtendInfo:function() {
			var passed = true
			var notBlank = 'cannot be blank'
			
			this.allergiesCheck = this.foodPrefCheck = this.qualificationCheck = this.computerSkillCheck = this.occupationCheck = this.workyearCheck = this.languagesCheck = ''
			
			this.ExperiencesYears()
			if (this.allergies == '') {
				this.allergiesCheck = notBlank
				passed = false
			}
			if (this.foodPreference == '') {
				this.foodPrefCheck = notBlank
				passed = false
			}
			if (this.qualifications == '') {
				this.qualificationCheck = notBlank
				passed = false
			}
			if (this.computerSkill == '') {
				this.computerSkillCheck = notBlank
				passed = false
			}
			if ((this.occupations == '' && this.workYears != '') ||
			   (this.occupations != '' && this.workYears == '')) {
				this.occupationCheck = "occupations and work years not equivalent"
				passed = false
			}
			if (this.languages == '') {
				this.languagesCheck = notBlank
				passed = false
			}
			
			if (passed) {
				if(this.ExperiencesYears()) {
					this.step++
				}
			}
		},
		CheckUserInfo: function() {
			var passed = true
			var notBlank = 'cannot be blank'
			
			this.usernameCheck = this.passwdCheck = this.passwdConfirmCheck = ''
			if (this.username == '') {
				this.usernameCheck = notBlank
				passed = false
			}
			if (this.passwd == '') {
				this.passwdCheck = notBlank
				passed = false;
			}
			if (this.passwdConfirm == '') {
				this.passwdConfirmCheck = notBlank
				passed = false
			}
			
			if (this.passwdConfirm != this.passwd) {
				this.passwdConfirm = "The confirmed password is not the same"
				passed = false
			}
			
			if (passed) {
				return this.UsernameCheck()
			} else {
				return false
			}
		}, 
		UsernameCheck:function() {
			this.$axios.post('/MarsEmployFast/signup', {
			  type: 'UsernameCheck',
			  username: this.username,
			 }).then((resp) => {
				 if(resp.data.exist == 'true') {
					 this.usernameCheck = 'username existed'
				 } else {
					 var salt = resp.data.salt
					 this.hashed = hex_sha256(this.passwd + salt)
					 console.log("hashed " + this.hashed)
					 this.SubmitRegister()
				 }
			 }).catch((error) => {
			  console.log(error)
			})
		},
		SubmitRegister: function() {
			this.$axios.post('/MarsEmployFast/signup', {
			  type: 'SignUp', usertype: 'candidate',
			  username: this.username, passwd: this.hashed,
			  name: this.name, gender: this.gender, dob: this.dob, country: this.nation,
			  postal: this.postcode, address: this.address, identification: this.identification,
			  allergies: this.allergies, foodPreference: this.foodPreference,
			  qualifications: this.qualifications, 
			  'years of work experience': this.workYears, occupations: this.occupations, 
			  'computer skill': this.computerSkill, 'languages known': this.languages
			  
			}).then((resp) => {
				if (resp.data.state) {
					this.$router.push('./transition')
				} else {
					console.log("Register Fail")
				}
			}).catch((error) => {
			  console.log(error)
			})
		}, 
		GetDoB:function(date) {
			this.dob = date
		}
	}
}
</script>

<style scoped>
	@import "../../static/css/register.css";
	@import "../../static/css/basic.css";
	
	.input-attr-name p {
		font-size: 1.38rem !important;
	}
	
	#RegisterFrame {
		border-color: rgba(240, 248, 255, 0.8);
	}
	
	#RegisterFrame:after {
	    background-image: url(../../static/img/BG.jpg);
	}
	
	.register-info-box {
		margin-top: 0;
	}
	
	.radio-box-container .radio-box {
		padding-left: 0.6rem;
		margin-top: 0.3rem;
		width: 8rem;
		line-height: 1.5rem;
		font-size: 1.5rem;
		
		display: flex;
		align-items: baseline;
	}
	
	.radio-box input {
		width: 2rem;
	}
	
	
	.buttons-line {
		width : 100%;
	}
	
	.buttons-container {
		display: flex;
		align-content: flex-end;
		margin-top: 0.6rem;
		margin-left: 25.5rem;
		margin-right: 6rem;
	}
	
	.main-button {
		width: 7.2rem;
		height: 2.4rem;
		font-size: 1.3rem;
		line-height: 2.4rem;
		cursor: pointer;
		
		margin:auto;
	}
		
	#register-extend-info {
		margin-top: 1rem;
	}
		
</style>
