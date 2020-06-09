<template>
	<div id="DateSelectContainer">
		<el-select id="YearSelector" v-model="year" placeholder="year">
			<el-option v-for="item in years" :key="item" :value="item"></el-option>
		</el-select>
		<el-select id="MonthSelector" v-model="monthStr" placeholder="month">
			<el-option v-for="item in months" :key="item" :value="item"></el-option>
		</el-select>
		<el-select id="DaySelector" v-model="dayStr" placeholder="day">
			<el-option v-for="item in days" :key="item" :value="item"></el-option>
		</el-select>
	</div>
</template>

<script>
	export default {
		data() {
			return {
				year: '',
				month: 0,
				monthStr: '',
				dayStr: '',
				dayofmonth: '',
				
				years: [],
				months: ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],
				days: [],
				bigMonths: [1, 3, 5, 7, 8, 10, 12]
			}
		}, 
		created() {
			var date = new Date()
			var i = 0
			
			for (i = 1930; i < date.getFullYear();  i++) {
				this.years.push(i)
			}
			
			for (i = 1; i <= 28; i++) {
				this.days.push(i)
			}
		}, 
		watch: {
			"year" : function(nVal, oVal) {
				if (this.month == 2) {
					if (!this.CheckRunYear(nVal)) {
						this.days = this.days.slice(0, 28)
					} else {
						if (this.days.length == 28) {
							this.days.push(29)
						}
					}
				}
				this.ReturnSetDate()
			}, 
			"monthStr" : function(nVal, oVal) {
				var i = 0
				for (i = 0; i < this.months.length; i++) {
					if (this.months[i] == nVal) {
						this.month = i + 1
						break
					}
				}
				
				if (this.month == 2) {
					this.days = this.days.slice(0, 28)
					if (this.CheckRunYear(this.year)) {
						this.days.push(29)
					}
				} else {
					var bigMonth = false;
					for (i = 0; i < this.bigMonths.length; i++) {
						if (this.month == this.bigMonths[i]) {
							var maxDay = this.days.length
							while (maxDay < 31) {
								this.days.push(++maxDay)
							}
							bigMonth = true
							break
						}
					}
					if (!bigMonth) {
						var maxDay = this.days.length
						if (maxDay == 31) {
							this.days.pop()
						}
						while (maxDay < 30) {
							this.days.push(++maxDay)
						}
					}
				}
				this.ReturnSetDate()
			}, 
			"dayStr":function(nVal, oVal) {
				this.dayofmonth = parseInt(nVal)
				this.ReturnSetDate()
			}
		}, 
		methods: {
			CheckRunYear:function(yearStr) {
				var run = false
				var y = parseInt(yearStr)
				if (y % 100 == 0) {
					if (y % 400 == 0) {
						run = true
					}
				} else if (y % 4 == 0) {
					run = true
				}
				return run
			}, 
			GenerateDate:function() {
				if (this.year == '') {
					return ''
				} else if (this.month == 0) {
					return ''
				} else if (this.dayofmonth == 0) {
					return ''
				}
				
				var dateStr = (this.month < 10) ? '0' + this.month : this.month
				dateStr += '/'
				dateStr += (this.dayofmonth < 10) ? '0' + this.dayofmonth : '' + this.dayofmonth
				dateStr += '/' + this.year
				return dateStr
			}, 
			ReturnSetDate:function() {
				var date = this.GenerateDate()
				if (date != '') {
					this.$emit('setdate', date)
				}
			}
		}
	}
</script>

<style>
	@import "../../static/css/basic.css";
	
	.date-part-select {
		display: block;
	}
	
	#DateSelectContainer {
		margin-left: 0.6rem;
	}
	
	#YearSelector{
		width: 6rem;
	}
	
	#MonthSelector{
		width: 6.2rem;
	}
	
	#DaySelector{
		width: 5rem;
	}
	
	.el-input__inner {
		border-style: solid !important;
		border-width: medium;
		border-color: #9fb7cc6F !important;
		background: #0d1f626F !important;
		font-size: 1rem !important;
		font-style: normal !important;
	}
	
	.el-input {
		margin-left: 0.3rem;
	}

</style>
