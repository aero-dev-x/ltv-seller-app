<template>
  <div id="app">
    <AnalyticsView />
  </div>
</template>

<script>
import AnalyticsView from './views/AnalyticsView.vue'
import './styles/global.css'

export default {
  name: 'App',
  components: {
    AnalyticsView
  }
}
</script>
