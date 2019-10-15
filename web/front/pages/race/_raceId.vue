<template>
  <section class="section">
    <div class="container">
      <div class="card">
        <div class="card-content">
          <span class="done">{{ doneText }}</span>
          <span>{{ remainText }}</span>
        </div>
      </div>
      <div class="card">
        <div class="card-content">
          <span class="done">{{ doneRomajis }}</span>
          <span>{{ remainRomajis }}</span>
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
      doneMoras: [],
      doneRomajis: '',
      inputBuffer: ''
    }
  },
  async asyncData({ app, params }) {
    const race = await app.$axios.$get(`race/${params.raceId}`)

    const remainMoras = []
    race.problem.moras.forEach((mora) => {
      remainMoras.push(mora.text)
    })

    let text = ''
    race.problem.tokens.forEach((token) => {
      text += token.text
    })
    return {
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
  mounted() {
    window.addEventListener('keydown', this.captureKeyDown)
  },
  beforeDestroy() {
    window.removeEventListener('keydown', this.captureKeyDown)
  },
  methods: {
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

      console.log('MISS')
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

<style scoped>
.done {
  color: blue;
}
.miss {
  color: red;
}
</style>
