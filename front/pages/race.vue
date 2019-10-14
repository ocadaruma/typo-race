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
const keyCodes = {
  48: '0',
  49: '1',
  50: '2',
  51: '3',
  52: '4',
  53: '5',
  54: '6',
  55: '7',
  56: '8',
  57: '9',
  58: ':',
  59: 'semicolon (firefox), equals',
  60: '<',
  61: 'equals (firefox)',
  63: 'ß',
  64: '@ (firefox)',
  65: 'A',
  66: 'B',
  67: 'C',
  68: 'D',
  69: 'E',
  70: 'F',
  71: 'G',
  72: 'H',
  73: 'I',
  74: 'J',
  75: 'K',
  76: 'L',
  77: 'M',
  78: 'N',
  79: 'O',
  80: 'P',
  81: 'Q',
  82: 'R',
  83: 'S',
  84: 'T',
  85: 'U',
  86: 'V',
  87: 'W',
  88: 'X',
  89: 'Y',
  90: 'Z',
  160: '^',
  161: '!',
  162: '؛ (arabic semicolon)',
  163: '#',
  164: '$',
  165: 'ù',
  169: 'closing paren (AZERTY)',
  170: '*',
  171: '~ + * key',
  173: 'minus (firefox), mute/unmute',
  186: 'semi-colon / ñ',
  187: '=',
  188: ',',
  189: '-',
  190: '.',
  191: 'forward slash / ç',
  192: 'grave accent / ñ / æ / ö',
  193: '?, / or °',
  194: 'numpad period (chrome)',
  219: 'open bracket',
  220: 'back slash',
  221: 'close bracket / å',
  222: 'single quote / ø / ä',
  223: '`',
  224: 'left or right ⌘ key (firefox)',
  225: 'altgr',
  226: '< /git >, left back slash',
  231: 'ç'
}

const moraKeyCandidates = {
  あ: ['A'],
  ぁ: ['XA', 'LA'],
  い: ['I'],
  ぃ: ['XI', 'LI', 'XYI'],
  いぇ: ['YE'],
  う: ['U', 'WU'],
  ぅ: ['XU', 'LU'],
  ゐ: ['WYI'],
  うぃ: ['WI', 'WHI'],
  うぇ: ['WE', 'WHE'],
  うぉ: ['WHO'],
  ゔ: ['VU'],
  ゔぁ: ['VA'],
  ゔぃ: ['VI'],
  ゔぇ: ['VE'],
  ゔぉ: ['VO'],
  え: ['E'],
  ぇ: ['XE', 'LE', 'XYE'],
  ゑ: ['WYE'],
  お: ['O'],
  ぉ: ['XO', 'LO'],

  か: ['KA', 'CA'],
  が: ['GA'],
  き: ['KI'],
  ぎ: ['GI'],
  ぎゃ: ['GYA'],
  ぎぃ: ['GYI'],
  ぎゅ: ['GYU'],
  ぎぇ: ['GYE'],
  ぎょ: ['GYO'],
  く: ['KU', 'CU', 'QU'],
  くぁ: ['QA'],
  くぃ: ['QI'],
  くぇ: ['QE'],
  くぉ: ['QO'],
  ぐ: ['GU'],
  ぐぁ: ['GWA'],
  ぐぃ: ['GWI'],
  ぐぅ: ['GWU'],
  ぐぇ: ['GWE'],
  ぐぉ: ['GWO'],
  け: ['KE'],
  げ: ['GE'],
  こ: ['KO', 'CO'],
  ご: ['GO'],

  さ: ['SA'],
  ざ: ['ZA'],
  し: ['SI', 'SHI', 'CI'],
  しゃ: ['SYA', 'SHA'],
  しぃ: ['SYI'],
  しゅ: ['SHU', 'SYU'],
  しぇ: ['SHE', 'SYE'],
  しょ: ['SHO', 'SYO'],
  じ: ['JI', 'ZI'],
  じゃ: ['JA', 'ZYA'],
  じぃ: ['JYI', 'ZYI'],
  じゅ: ['JU', 'JYU', 'ZYU'],
  じぇ: ['JE', 'JYE', 'ZYE'],
  じょ: ['JO', 'JYO', 'ZYO'],
  す: ['SU'],
  ず: ['ZU'],
  せ: ['SE', 'CE'],
  ぜ: ['ZE'],
  そ: ['SO'],
  ぞ: ['ZO'],

  た: ['TA'],
  だ: ['DA'],
  ち: ['TI', 'CHI'],
  ちゃ: ['TYA', 'CHA', 'CYA'],
  ちぃ: ['TYI', 'CYI'],
  ちゅ: ['TYU', 'CHU', 'CYU'],
  ちぇ: ['TYE', 'CHE', 'CYE'],
  ちょ: ['TYO', 'CHO', 'CYO'],
  ぢ: ['DI'],
  ぢゃ: ['DYA'],
  ぢぃ: ['DYI'],
  ぢゅ: ['DYU'],
  ぢぇ: ['DYE'],
  ぢょ: ['DYO'],
  っ: ['XTU', 'LTU', 'XTSU', 'LTSU'],
  つ: ['TU', 'TSU'],
  つぁ: ['TSA'],
  つぃ: ['TSI'],
  つぇ: ['TSE'],
  つぉ: ['TSO'],
  づ: ['DU'],
  て: ['TE'],
  てゃ: ['THA'],
  てぃ: ['THI'],
  てゅ: ['THU'],
  てぇ: ['THE'],
  てょ: ['THO'],
  で: ['DE'],
  でゃ: ['DHA'],
  でぃ: ['DHI'],
  でゅ: ['DHU'],
  でぇ: ['DHE'],
  でょ: ['DHO'],
  と: ['TO'],
  ど: ['DO'],
  どぁ: ['DWA'],
  どぃ: ['DWI'],
  どぅ: ['DWU'],
  どぇ: ['DWE'],
  どぉ: ['DWO'],

  な: ['NA'],
  に: ['NI'],
  にゃ: ['NYA'],
  にぃ: ['NYI'],
  にゅ: ['NYU'],
  にぇ: ['NYE'],
  にょ: ['NYO'],
  ぬ: ['NU'],
  ね: ['NE'],
  の: ['NO'],

  は: ['HA'],
  ば: ['BA'],
  ぱ: ['PA'],
  ひ: ['HI'],
  ひゃ: ['HYA'],
  ひぃ: ['HYI'],
  ひゅ: ['HYU'],
  ひぇ: ['HYE'],
  ひょ: ['HYO'],
  び: ['BI'],
  びゃ: ['BYA'],
  びぃ: ['BYI'],
  びゅ: ['BYU'],
  びぇ: ['BYE'],
  びょ: ['BYO'],
  ぴ: ['PI'],
  ぴゃ: ['PYA'],
  ぴぃ: ['PYI'],
  ぴゅ: ['PYU'],
  ぴぇ: ['PYE'],
  ぴょ: ['PYO'],
  ふ: ['FU', 'HU'],
  ふぁ: ['FA', 'HWA'],
  ふぃ: ['FI', 'HWI'],
  ふぇ: ['FE', 'HWE'],
  ふぉ: ['FO', 'HWO'],
  ぶ: ['BU'],
  ぷ: ['PU'],
  へ: ['HE'],
  べ: ['BE'],
  ぺ: ['PE'],
  ほ: ['HO'],
  ぼ: ['BO'],
  ぽ: ['PO'],

  ま: ['MA'],
  み: ['MI'],
  みゃ: ['MYA'],
  みぃ: ['MYI'],
  みゅ: ['MYU'],
  みぇ: ['MYE'],
  みょ: ['MYO'],
  む: ['MU'],
  め: ['ME'],
  も: ['MO'],

  や: ['YA'],
  ゃ: ['XYA', 'LYA'],
  ゆ: ['YU'],
  ゅ: ['XYU', 'LYU'],
  よ: ['YO'],
  ょ: ['XYO', 'LYO'],

  ら: ['RA'],
  り: ['RI'],
  りゃ: ['RYA'],
  りぃ: ['RYI'],
  りゅ: ['RYU'],
  りぇ: ['RYE'],
  りょ: ['RYO'],
  る: ['RU'],
  れ: ['RE'],
  ろ: ['RO'],

  わ: ['WA'],
  を: ['WO'],
  ん: ['NN'],
  ',': [','],
  '.': ['.'],
  '、': [','],
  '。': ['.'],
  ー: ['-']
}

export default {
  name: 'Race',
  data() {
    return {
      doneMoras: [],
      doneRomajis: '',
      remainMoras: [
        'にゃ',
        'ん',
        'ぱ',
        'す',
        'ー',
        '。',
        'あ',
        'の',
        'しゃ',
        'っ',
        'け',
        '、',
        'お',
        'い',
        'し',
        'そ',
        'う',
        'だ',
        'にゃ',
        'あ',
        '。'
      ],
      inputBuffer: ''
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
      result += current.candidate

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
      const key = keyCodes[event.keyCode]

      if (!key) {
        return
      }
      if (this.remainMoras.length === 0) {
        return
      }

      const candidates = this.getCandidates(0).filter(
        (c) => c.commitablePart.indexOf(this.inputBuffer) === 0
      )

      const nextBuffer = this.inputBuffer + key
      for (let i = 0; i < candidates.length; i++) {
        const candidate = candidates[i]

        if (candidate.candidate.indexOf(nextBuffer) === 0) {
          // commit single mora
          if (nextBuffer === candidate.candidate) {
            this.doneMoras.push(this.remainMoras.shift())
            this.doneRomajis += nextBuffer
            this.inputBuffer = ''
            return
          }
          // commit composite mora
          if (nextBuffer.length > candidate.commitablePart.length) {
            this.doneMoras.push(this.remainMoras.shift())
            this.doneRomajis += candidate.commitablePart
            this.inputBuffer = nextBuffer.substring(
              candidate.commitablePart.length
            )
            return
          }
          // commit buffer
          if (candidate.candidate.indexOf(nextBuffer) === 0) {
            this.inputBuffer = nextBuffer
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
          return moraKeyCandidates[mora].map((c) => {
            return { candidate: c, commitablePart: c, length: 1 }
          })
        }
        const result = []
        const nextMora = this.remainMoras[moraPosition + 1]
        moraKeyCandidates[nextMora].forEach((c) => {
          if (c.indexOf('N') !== 0) {
            result.push({
              candidate: 'N' + c,
              commitablePart: 'N',
              length: 2
            })
          }
        })
        result.push({
          candidate: 'NN',
          commitablePart: 'NN',
          length: 1
        })
        return result
      } else if (mora === 'っ') {
        // 促音
        if (this.remainMoras.length < 2) {
          return moraKeyCandidates[mora].map((c) => {
            return { candidate: c, commitablePart: c, length: 1 }
          })
        }
        const result = []
        const nextMora = this.remainMoras[moraPosition + 1]
        moraKeyCandidates[nextMora].forEach((c) => {
          result.push({
            candidate: c[0] + c,
            commitablePart: c[0],
            length: 2
          })
        })
        moraKeyCandidates[mora].forEach((c) => {
          result.push({ candidate: c, commitablePart: c, length: 1 })
        })
        return result
      } else {
        return moraKeyCandidates[mora].map((c) => {
          return { candidate: c, commitablePart: c, length: 1 }
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
