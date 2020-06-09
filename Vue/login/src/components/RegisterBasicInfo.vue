<template>
	<div class="register-info-box">
		<div id="register-basic-info">
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
				<div class="input-notice container"></div>
				<div class="container input-attr-name">
					<p>Gender</p>
				</div>
				<div class="radio-box-container">
					<div class="radio-box">
						<el-radio label="male" v-model="sex">Male</el-radio>			
					</div>
					<div class="radio-box">
						<el-radio label="female" v-model="sex">Female</el-radio>
					</div>		
				</div>
			</div>
			<div class="input-line container">
				<div class="input-notice container"></div>
				<div class="container input-attr-name">
					<p>Date of Birth</p>
				</div>
				<DateSelect></DateSelect>
			</div>
			<div class="input-line container">
				<div class="input-notice container"></div>
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
				<textarea type="test" name="address" placeholder="enter your address"/>
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
	</div>
</template>

<script>
import DateSelect from './dateselect.vue'
	
export default {
	data() {
		return {
			countries: [],
			
			nameCheck: '',
			addressCheck: '',
			postcodeCheck: '',
			identificationCheck: '',
		}	
	},
	props: ['basicInfo'], 
	components: {
		DateSelect 
	},
}
	
</script>

<style>
	.register-info-box {
		margin-top: 0;
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
	
	.input-line textarea {
		background-color: rgba(0, 0, 0, 0);
		border-width: medium;
		border-color: #2d8db99C;
		color: aliceblue;
		font-size: 1.4rem;
		width: 20rem;
		height: 3.2rem;
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
	
	.el-radio {
		margin-left: 0.6rem;
		display: flex;
		align-items: center;
		
	}
	
	.el-radio__input.is-checked .el-radio__inner {
		border-color: #9ca4a8;
		background: #9ca4a8;
	}
	
	.el-radio__label {
		margin-left: 0.5rem;
		font-size: 1.5rem;
		color: #FFFFFF;
	}
	
	.el-radio__input.is-checked+.el-radio__label {
		color: #a896a8;
	}
</style>
