<template>
  <div class="seller-selector">
    <div class="selector-header">
      <div class="selector-icon">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2.5">
          <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
          <circle cx="12" cy="7" r="4"/>
        </svg>
      </div>
      <label for="seller-select" class="selector-label">
        CHOOSE SELLER
      </label>
    </div>
    <select 
      id="seller-select"
      v-model="selectedSellerId" 
      @change="handleSelection"
      class="selector-input"
      :disabled="isLoading"
    >
      <option value="" disabled>Select seller to analyze...</option>
      <option 
        v-for="seller in sellers" 
        :key="seller.id" 
        :value="seller.id"
      >
        {{ seller.name }} (ID: {{ seller.id }})
      </option>
    </select>
  </div>
</template>

<script>
import { ref } from 'vue'
import '../styles/components/SellerSelector.css'

export default {
  name: 'SellerSelector',
  props: {
    sellers: {
      type: Array,
      required: true
    },
    isLoading: {
      type: Boolean,
      default: false
    }
  },
  emits: ['seller-selected'],
  setup(props, { emit }) {
    const selectedSellerId = ref('')

    const handleSelection = () => {
      if (selectedSellerId.value) {
        emit('seller-selected', Number(selectedSellerId.value))
      }
    }

    return {
      selectedSellerId,
      handleSelection
    }
  }
}
</script>

