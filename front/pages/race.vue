<template>
  <section class="section">
    <div class="container">
      <div class="card">
        <div class="card-content">
          <span class="done">{{ doneText }}</span>
          <span>{{ remainText }}</span>
        </div>
        <footer class="card-footer">
          <span class="done">{{ doneRomajis }}</span>
          <span>{{ remainRomajis }}</span>
        </footer>
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

  や: ['YA'],
  ゃ: ['XYA', 'LYA'],
  ゆ: ['YU'],
  ゅ: ['XYU', 'LYU'],
  よ: ['YO'],
  ょ: ['XYO', 'LYO'],

  を: ['WO'],
  ん: ['NN'],
  ',': [','],
  '.': ['.'],
  '、': [','],
  '。': ['.']
}

export default {
  name: 'Race',
  data() {
    return {
      doneMoras: [],
      doneRomajis: '',
      remainMoras: [
        'あ',
        'の',
        'しゃ',
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
        '。',
        'け',
        'ん',
        'こ',
        'う'
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
      result += moraKeyCandidates[this.remainMoras[0]].filter(
        (c) => c.indexOf(this.inputBuffer) === 0
      )[0]
      for (let i = 1; i < this.remainMoras.length; i++) {
        result += moraKeyCandidates[this.remainMoras[i]][0]
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

      const candidates = moraKeyCandidates[this.remainMoras[0]].filter(
        (c) => c.indexOf(this.inputBuffer) === 0
      )

      const nextBuffer = this.inputBuffer + key
      for (let i = 0; i < candidates.length; i++) {
        const candidate = candidates[i]

        // commit mora
        if (nextBuffer === candidate) {
          this.doneMoras.push(this.remainMoras.shift())
          this.doneRomajis += nextBuffer
          this.inputBuffer = ''
          return
        }
        // commit buffer
        if (candidate.indexOf(nextBuffer) === 0) {
          this.inputBuffer = nextBuffer
          return
        }
      }

      console.log('MISS')
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
