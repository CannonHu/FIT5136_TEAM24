import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/Home.vue'
import Coordinator from '../views/coordinator.vue'
import Mission from '../views/mission.vue'
import MissionBasicInfo from '../components/mission/basicinfo.vue'
import MissionDetail from '../components/mission/detailinfo.vue'
import MissionEmployRequirements from '../components/mission/requirements.vue'
import MissionJobs from '../components/mission/jobs.vue'
import Transition from '../views/transition.vue'
import Admin from '../views/admin.vue'

Vue.use(VueRouter)

  const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
	  path: '/coordinator',
	  name: 'Coordinator', 
	  component: Coordinator,
	  
  },
  {
	  path: 'admin',
	  name: 'Admin',
	  component: Admin
  },
  {
  	  path: '/mission',
  	  name: 'Mission', 
  	  component: Mission,
  	  children: [
		  {
			  path: 'basic',
			  name: 'MissionBasic', 
			  component: MissionBasicInfo,
		  }, 
		  {
			  path: 'detail',
			  name: 'MissionDetail', 
			  component: MissionDetail,
		  },
		  {
			  path: 'requirements',
			  name: 'MissionEmployRequirements',
			  component: MissionEmployRequirements
		  }, 
		  {
			  path: 'jobs',
			  name: 'MissionJobs',
			  component: MissionJobs
		  }
	  ]
  },
  {
	  path: '/transition',
	  name: 'Transition',
	  component: Transition
  },
  {
    path: '/about',
    name: 'About',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: "about" */ '../views/About.vue')
  }
]

const router = new VueRouter({
  mode: 'hash',
  base: process.env.BASE_URL,
  routes
})

export default router
