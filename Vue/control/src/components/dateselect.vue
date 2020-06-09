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
		props: {
			DateType: {
				type: String,
				default: 'past'
			}, 
			InitDate: {
				type: String
			}
		},
		data() {
			return {
				thisYear: '',
				year: '',
				month: 0,
				monthStr: '',
				dayStr: '',
				dayofmonth: '',
				
				years: [],
				months: ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],
				constMonths: ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],
				days: [],
				bigMonths: [1, 3, 5, 7, 8, 10, 12]
			}
		}, 
		created() {
			var date = new Date()
			var i = 0
			this.thisYear = date.getFullYear()
			
			if (this.InitDate != '') {
				this.ShowDate(this.InitDate)
			}
			
			if (this.DateType == 'past') {
				for (i = 1930; i < this.thisYear;  i++) {
					this.years.push(i)
				}
			} else if (this.DateType == 'future') {
				for (i = 0; i < 50; i++) {
					this.years.push(this.thisYear + i)
				}
			}
			
			for (i = 1; i <= 28; i++) {
				this.days.push(i)
			}
		}, 
		watch: {
			"year" : function(nVal) {
				if (this.DateType == 'future') {
					if (nVal == this.thisYear) {
						var date = new Date()
						this.months = this.months.slice(0, 0)
						
						var thisMonth = date.getMonth()
						var i
						for(i = thisMonth; i < 12; i++) {
							this.months.push(this.constMonths[i])
						}
						var thisDate = date.getDate()
						var maxDate = this.GetMonthDay(thisMonth + 1)
						
						this.days = this.days.slice(0,0)
						for(i = thisDate; i <= maxDate; i++) {
							this.days.push(i)
						}
						
					} else {
						this.months = this.months.slice(0, 0)
						for(i = 0; i < 12; i++) {
							this.months.push(this.constMonths[i])
						}
						
						this.days = this.days.slice(0,0)
						for(i = 1; i <= 28; i++) {
							this.days.push(i)
						}
					}
				} else if (this.month == 2) {
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
			"monthStr" : function(nVal) {
				var i = 0
				for (i = 0; i < this.months.length; i++) {
					if (this.months[i] == nVal) {
						this.month = i + 1
						break
					}
				}

				this.SetMonthDay(this.GetMonthDay(this.month))
				this.ReturnSetDate()
			}, 
			"dayStr":function(nVal) {
				this.dayofmonth = parseInt(nVal)
				this.ReturnSetDate()
			}
		}, 
		methods: {
			SetMonthDay:function(monthDay) {
				if (this.days.length > monthDay) {
					while (this.days.length > monthDay) {
						this.days.pop()
					}
				} else {
					while (this.days.length < monthDay) {
						this.days.push(this.days.length + 1)
					}
				}
			}, 
			GetMonthDay:function(monthNumber) {
				var bigMonth = false
				var maxDay
				if (monthNumber == 2) {
					if (this.CheckRunYear(this.year)) {
						return 29
					} else {
						return 28
					}
				}
				var i
				for (i = 0; i < this.bigMonths.length; i++) {
					if (monthNumber == this.bigMonths[i]) {
						maxDay = this.days.length
						while (maxDay < 31) {
							this.days.push(++maxDay)
						}
						bigMonth = true
						break
					}
				}
				return bigMonth ? 31 : 30
			}, 
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
			ShowDate:function(dateStr) {
				var splits = dateStr.split('/')
				if (splits.length == 3) {
					this.dayofmonth = parseInt(splits[1])
					this.dayStr = this.dayofmonth
					
					this.month = parseInt(splits[0])
					this.monthStr = this.constMonths[this.month - 1]
					
					this.year = parseInt(splits[2])
				}
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
