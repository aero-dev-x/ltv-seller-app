<template>
  <div class="notification-panel">
    <div class="panel-header">
      <h3 class="panel-title">Active Alerts</h3>
      <div class="panel-count" v-if="hasNotifications">
        {{ notificationCount }} {{ alertLabel.toUpperCase() }}
      </div>
    </div>

    <div class="panel-content">
      <div v-if="isEmpty" class="no-notifications">
        <div class="no-notifications-icon">
          <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
            <polyline points="22 4 12 14.01 9 11.01"/>
          </svg>
        </div>
        <div class="no-notifications-text">All Clear</div>
        <div class="no-notifications-subtext">No alerts detected. All systems operating normally.</div>
      </div>

      <div v-else class="notifications-list">
        <div 
          v-for="(notification, index) in normalizedNotifications" 
          :key="getNotificationKey(notification, index)"
          class="notification-item"
        >
          <div class="notification-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
              <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
              <line x1="12" y1="9" x2="12" y2="13"/>
              <line x1="12" y1="17" x2="12.01" y2="17"/>
            </svg>
          </div>
          <div class="notification-content">
            <div class="notification-text">{{ getNotificationText(notification) }}</div>
            <div class="notification-timestamp">{{ getFormattedTimestamp(index) }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { computed } from 'vue'
import { useDateTime } from '../composables/useDateTime.js'
import '../styles/components/NotificationPanel.css'

export default {
  name: 'NotificationPanel',
  props: {
    notifications: {
      type: Array,
      default: () => [],
      validator: (value) => {
        return Array.isArray(value) && value.every(item => 
          typeof item === 'string' || typeof item === 'object'
        )
      }
    }
  },
  setup(props) {
    const { formatTimestamp } = useDateTime()

    const normalizedNotifications = computed(() => {
      if (!props.notifications || !Array.isArray(props.notifications)) {
        return []
      }
      return props.notifications.filter(Boolean)
    })

    const hasNotifications = computed(() => {
      return normalizedNotifications.value.length > 0
    })

    const isEmpty = computed(() => {
      return !hasNotifications.value
    })

    const notificationCount = computed(() => {
      return normalizedNotifications.value.length
    })

    const alertLabel = computed(() => {
      return notificationCount.value === 1 ? 'alert' : 'alerts'
    })

    function getNotificationKey(notification, index) {
      if (typeof notification === 'string') {
        return `alert-${index}-${notification.substring(0, 10)}`
      }
      if (notification && notification.id) {
        return `alert-${notification.id}`
      }
      return `alert-${index}-${Date.now()}`
    }

    function getNotificationText(notification) {
      if (typeof notification === 'string') {
        return notification
      }
      if (notification && notification.message) {
        return notification.message
      }
      if (notification && notification.text) {
        return notification.text
      }
      return String(notification)
    }

    function getFormattedTimestamp(index) {
      const now = new Date()
      const timestamp = new Date(now.getTime() - (index * 60000))
      return formatTimestamp(timestamp)
    }

    return {
      normalizedNotifications,
      hasNotifications,
      isEmpty,
      notificationCount,
      alertLabel,
      getNotificationKey,
      getNotificationText,
      getFormattedTimestamp
    }
  }
}
</script>
