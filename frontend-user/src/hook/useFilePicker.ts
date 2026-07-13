import { ref, onUnmounted } from 'vue'

interface FilePickerOptions {
  accept?: string
  multiple?: boolean
}

export function useFilePicker(options: FilePickerOptions = {}) {
  const inputRef = ref<HTMLInputElement | null>(null)

  function createInput() {
    const input = document.createElement('input')
    input.type = 'file'
    if (options.accept) input.accept = options.accept
    if (options.multiple) input.multiple = true
    input.style.display = 'none'
    document.body.appendChild(input)
    inputRef.value = input
    return input
  }

  function cleanup(input: HTMLInputElement) {
    input.value = ''
    if (document.body.contains(input)) {
      document.body.removeChild(input)
    }
    inputRef.value = null
  }

  function pickFiles(): Promise<File | FileList> {
    return new Promise((resolve, reject) => {
      const input = createInput()

      function onChange() {
        if (!input.files || input.files.length === 0) {
          cleanup(input)
          return reject(new Error('No files selected'))
        }
        const result = options.multiple ? input.files : input.files[0]
        cleanup(input)
        resolve(result)
      }

      input.addEventListener('change', onChange, { once: true })

      input.click()
    })
  }

  onUnmounted(() => {
    const input = inputRef.value
    if (input) {
      cleanup(input)
    }
  })

  return { pickFiles }
}
