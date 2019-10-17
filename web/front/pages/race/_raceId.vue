<template>
  <section class="section">
    <div class="container">
      <b-field grouped v-if="!joined()">
        <b-input placeholder="Name" v-model="name" />
        <p class="control">
          <button class="button is-primary" @click="joinRace()">
            Join Race
          </button>
        </p>
      </b-field>
      <p v-if="joined()">Your Name: {{ name }}</p>
    </div>
    <div v-if="race.state === 'COUNTDOWN'" class="container">
      <h1 class="title">{{ countdown }}</h1>
    </div>
    <div class="container" v-if="race.state === 'WAITING' && joined()">
      <b-button size="is-large" @click="requestStart()">
        START COUNTDOWN
      </b-button>
    </div>
    <div
      class="panel-problem"
      v-if="race.state === 'PLAYING' || race.state === 'FINISHED'"
    >
      <div class="container problem-kanji">
        <div id="card-kanji" class="card">
          <div class="card-content">
            <span>{{ text }}</span>
          </div>
        </div>
      </div>
      <div class="container problem-hiragana">
        <div id="card-hiragana" class="card">
          <div class="card-content">
            <span class="done">{{ doneText }}</span>
            <span>{{ remainText }}</span>
          </div>
        </div>
      </div>
    </div>
    <!--    <div class="container problem-romaji">-->
    <!--      <div class="card">-->
    <!--        <div class="card-content">-->
    <!--          <span class="done">{{ doneRomajis }}</span>-->
    <!--          <span>{{ remainRomajis }}</span>-->
    <!--        </div>-->
    <!--      </div>-->
    <!--    </div>-->
    <div class="container">
      <div id="players" ref="players">
        <div
          class="lane"
          v-for="player in race.players"
          v-bind:key="player.name"
        >
          <span
            class="player"
            :style="
              'margin-left: ' + computePlayerLeftMargin(player.position) + 'px;'
            "
          >
            <b-icon pack="fas" icon="user" />
            <span>({{ player.name }})</span>
          </span>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
import { KEY_CODES, MORA_KEY_CANDIDATES } from '~/models/race.js'

export default {
  data() {
    return {
      countdown: 10,
      laneWidth: 0,
      doneMoras: [],
      doneRomajis: '',
      inputBuffer: ''
    }
  },
  async asyncData({ app, params, query }) {
    const name = query.name
    let race = await app.$axios.$get(`race/${params.raceId}`)
    if (name) {
      race = await app.$axios.$post(`race/${params.raceId}/join`, {
        name
      })
    }

    const remainMoras = []
    race.problem.moras.forEach((mora) => {
      remainMoras.push(mora.text)
    })

    let text = ''
    race.problem.tokens.forEach((token) => {
      text += token.text
    })
    return {
      name: query.name,
      race,
      text,
      remainMoras
    }
  },
  computed: {
    doneText() {
      return this.doneMoras.join('')
    },
    remainText() {
      return this.remainMoras.join('')
    },
    allHiraganas() {
      let result = 0
      this.race.problem.moras.forEach((mora) => {
        result += mora.text.length
      })
      return result
    },
    remainRomajis() {
      let result = ''

      if (this.remainMoras.length === 0) {
        return result
      }

      const current = this.getCandidates(0).filter(
        (c) => c.candidate.indexOf(this.inputBuffer) === 0
      )[0]
      result += current.candidate.substring(this.inputBuffer.length)

      let i = current.length
      while (i < this.remainMoras.length) {
        const candidate = this.getCandidates(i)[0]

        result += candidate.candidate
        i += candidate.length
      }

      return result
    }
  },
  beforeRouteLeave(to, from, next) {
    this.dispose()
    next()
  },
  mounted() {
    this.laneWidth = this.$refs.players.offsetWidth
    if (this.race.state !== 'FINISHED') {
      this.openRaceStream()
    }
    window.addEventListener('keydown', this.captureKeyDown)
  },
  beforeDestroy() {
    this.dispose()
    window.removeEventListener('keydown', this.captureKeyDown)
  },
  watch: {
    doneMoras(val) {
      let doneHiraganas = 0
      this.doneMoras.forEach((mora) => {
        doneHiraganas += mora.length
      })

      const self = this
      this.$axios
        .$post(`race/${this.race.raceId}/input`, {
          name: this.name,
          doneCount: doneHiraganas
        })
        .then((res) => {
          self.race = res
        })
    }
  },
  methods: {
    requestStart() {
      this.$axios.$post(`race/${this.race.raceId}/start`)
    },
    openRaceStream() {
      const baseURL = this.$axios.defaults.baseURL
      this.eventSource = new EventSource(
        `${baseURL}/stream/race/${this.race.raceId}`
      )
      this.eventSource.onmessage = this.onMessage
      this.eventSource.onerror = () => {
        this.eventSource.close()
      }
    },
    onMessage(e) {
      const event = JSON.parse(e.data)
      const eventType = event.eventType

      if (eventType === 'COUNTDOWN') {
        this.race.state = 'COUNTDOWN'
        this.countdown = event.content
      } else if (eventType === 'START') {
        this.race.state = 'PLAYING'
      } else if (eventType === 'FINISHED') {
        this.race.state = 'FINISHED'
        this.$buefy.dialog.alert(`WINNER : ${event.content}`)
      } else if (eventType === 'PLAYERS_CHANGED') {
        this.race.players = event.content
      } else if (eventType === 'INPUT') {
        this.race.players = event.content
      }
    },
    dispose() {
      if (this.eventSource) {
        this.eventSource.close()
        this.eventSource = null
      }
    },
    computePlayerLeftMargin(position) {
      const ratio = position / this.allHiraganas
      return this.laneWidth * ratio
    },
    async joinRace() {
      const name = this.name
      if (!name) {
        return
      }

      const race = await this.$axios.$post(`race/${this.race.raceId}/join`, {
        name
      })
      this.race = race

      this.$router.push({
        name: 'race-raceId',
        params: { raceId: this.race.raceId },
        query: { name }
      })
    },
    joined() {
      if (this.$route.query.name) {
        return true
      }
      return false
    },
    captureKeyDown(event) {
      const key = KEY_CODES[event.keyCode]

      // unsupported keycode
      if (!key) {
        return
      }
      // ended
      if (this.remainMoras.length === 0) {
        return
      }
      // not playing
      if (this.race.state !== 'PLAYING' || !this.joined()) {
        return
      }

      const candidates = this.getCandidates(0).filter(
        (c) => c.candidate.indexOf(this.inputBuffer) === 0
      )
      const nextBuffer = this.inputBuffer + key

      for (let i = 0; i < candidates.length; i++) {
        const candidate = candidates[i]

        // you-on splitted
        if (candidate.firstMora.length > 1) {
          const splittedCandidates = MORA_KEY_CANDIDATES[candidate.firstMora[0]]
          for (let j = 0; j < splittedCandidates.length; j++) {
            const c = splittedCandidates[j]
            if (nextBuffer === c) {
              this.remainMoras.splice(0, 1, candidate.firstMora[0])
              this.remainMoras.splice(1, 0, candidate.firstMora.substring(1))
              this.doneMoras.push(this.remainMoras.shift())
              this.doneRomajis += key
              this.inputBuffer = ''
              return
            }
          }
        }

        // otherwise
        if (candidate.candidate.indexOf(nextBuffer) === 0) {
          // commit mora
          if (nextBuffer === candidate.candidate) {
            for (let k = 0; k < candidate.length; k++) {
              this.doneMoras.push(this.remainMoras.shift())
            }
            this.doneRomajis += key
            this.inputBuffer = ''
            return
          }
          // commit part of composite mora
          if (nextBuffer.length > candidate.committablePart.length) {
            this.doneMoras.push(this.remainMoras.shift())
            this.doneRomajis += key
            this.inputBuffer = nextBuffer.substring(
              candidate.committablePart.length
            )
            return
          }
          // commit buffer
          if (candidate.candidate.indexOf(nextBuffer) === 0) {
            this.inputBuffer = nextBuffer
            this.doneRomajis += key
            return
          }
        }
      }

      document.getElementById('card-kanji').className = 'card'
      window.requestAnimationFrame(() => {
        window.requestAnimationFrame(() => {
          document.getElementById('card-kanji').className = 'card miss'
        })
      })
    },
    getCandidates(moraPosition) {
      const mora = this.remainMoras[moraPosition]
      if (mora === 'ん') {
        // 撥音
        if (moraPosition >= this.remainMoras.length - 1) {
          return MORA_KEY_CANDIDATES[mora].map((c) => {
            return {
              candidate: c,
              committablePart: c,
              length: 1,
              firstMora: mora
            }
          })
        }
        const result = []
        const nextMora = this.remainMoras[moraPosition + 1]
        MORA_KEY_CANDIDATES[nextMora].forEach((c) => {
          if (c.indexOf('N') !== 0) {
            result.push({
              candidate: 'N' + c,
              committablePart: c.length === 1 ? 'N' + c : 'N',
              length: 2,
              firstMora: mora
            })
          }
        })
        result.push({
          candidate: 'NN',
          committablePart: 'NN',
          length: 1,
          firstMora: mora
        })
        return result
      } else if (mora === 'っ') {
        // 促音
        if (this.remainMoras.length < 2) {
          return MORA_KEY_CANDIDATES[mora].map((c) => {
            return {
              candidate: c,
              committablePart: c,
              length: 1,
              firstMora: mora
            }
          })
        }
        const result = []
        const nextMora = this.remainMoras[moraPosition + 1]
        MORA_KEY_CANDIDATES[nextMora].forEach((c) => {
          result.push({
            candidate: c[0] + c,
            committablePart: c[0],
            length: 2,
            firstMora: mora
          })
        })
        MORA_KEY_CANDIDATES[mora].forEach((c) => {
          result.push({
            candidate: c,
            committablePart: c,
            length: 1,
            firstMora: mora
          })
        })
        return result
      } else {
        return MORA_KEY_CANDIDATES[mora].map((c) => {
          return {
            candidate: c,
            committablePart: c,
            length: 1,
            firstMora: mora
          }
        })
      }
    }
  }
}
</script>

<style>
.done {
  color: blue;
}
.container {
  padding: 8px;
}
.problem-romaji .card-content {
  word-break: break-all;
}

@keyframes colorchange {
  0% {
    background: #ff2457;
  }
  100% {
    background: none;
  }
}

.player {
  margin-bottom: 8px;
  display: inline-block;
}

.player .icon {
  background: yellow;
}

.lane {
  border-left: 2px #47494e solid;
  border-right: 2px #47494e solid;
}

.miss {
  animation: colorchange 0.5s;
}
</style>
