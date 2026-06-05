import axios from 'axios'
import { createDiscreteApi } from 'naive-ui'

const { notification } = createDiscreteApi(['notification'])

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
})

http.interceptors.response.use(
  (response) => response,
  (error) => {
    const status: number | undefined = error.response?.status
    const data = error.response?.data
    const message = typeof data === 'string' ? data : error.message ?? 'Erro desconhecido'

    // Erros de servidor (5xx) e de rede exibem notificação global
    if (!status || status >= 500) {
      notification.error({
        title: 'Erro inesperado',
        content: status
          ? `O servidor retornou um erro (${status}). Tente novamente.`
          : 'Não foi possível conectar ao servidor. Verifique sua conexão.',
        duration: 5000,
      })
    }

    // Propaga o erro para o handler local do componente
    return Promise.reject(new Error(typeof message === 'string' ? message : JSON.stringify(message)))
  },
)

export default http
