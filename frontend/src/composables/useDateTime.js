import { ref } from 'vue'

function formatTimestamp(date = new Date()) {
  return date.toLocaleString('en-US', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    hour12: true
  })
}

export function useDateTime() {
  const currentTime = ref(formatTimestamp())

  return {
    formatTimestamp,
    currentTime
  }
}

