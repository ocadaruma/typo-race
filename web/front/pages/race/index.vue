<template>
  <section class="section">
    <div class="container">
      <b-button size="is-large" @click="createNewRace()">
        Create New Race
      </b-button>
    </div>
  </section>
</template>

<script>
export default {
  methods: {
    async createNewRace() {
      const race = await this.$axios.$post('race')
      this.$router.push({
        name: 'race-raceId',
        params: { raceId: race.raceId }
      })
    }
  }
}
</script>
