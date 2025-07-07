import axios from './axios'

export const loginApi = (payload) => axios.post('/auth/login', payload).then((res) => res.data)

export const register = (payload) => axios.post('/auth/register', payload).then((res) => res.data)

export const refreshToken = (refreshToken) =>
  axios
    .post(
      '/auth/refresh',
      {},
      {
        headers: {
          Authorization: `Bearer ${refreshToken}`,
        },
      }
    )
    .then((res) => res.data)
